package net.slisenko.jpa.examples.lifecycle.locking;

import junit.framework.Assert;
import net.slisenko.jpa.examples.lifecycle.locking.exception.TransactionFailException;
import net.slisenko.jpa.examples.lifecycle.locking.model.NonVersionedEntity;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.*;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * http://www.xaprb.com/blog/2006/08/05/how-to-give-locking-hints-in-mysql/
 * http://dev.mysql.com/doc/refman/5.0/en/innodb-locking-reads.html
 * http://www.percona.com/blog/2006/08/06/select-lock-in-share-mode-and-for-update/
 *
 * Пессимистическая блокировка:
 * Идея:
 * - Предполагаем пессимистически: скорее всего кто-то будет пытаться изменять данные, над которыми мы работаем
 * - Блокируем данные на уровне БД, другие транзакции джут пока не закончится наша
 * - Иногда не хочется получать OptimisticLockException, но хочется заблокировать данные
 *
 * Реализация:
 * - Поддержка на уровне БД, особые типы запросов SELECT ... FOR UPDATE
 * - Блокировка работает пока держится транзакция (до вызова commit/rollback)
 * - При пессимистической блокировке всегда работает и оптимистическая (если есть поле версии) TODO проверить
 *
 * Use-cases:
 * - Запускаем выполнение отчёта, за это время не даём никому менять наши данные
 * - Ограждаем от проблем критичные операции с данными
 *
 * Преимущества:
 * - Реально защищает данные от других транзакция, в том числе и на удаление
 *
 * Недостатки:
 * - Работает только пока живёт транзакция. Если мы передали detached-данные в слой представления, то они уже не блокируются
 * - Снижает производительность и масштабируемость
 *
 * Аналогия с Java для лучшего понимания:
 * - В Java есть класс ReadWriteLock, представим что объект этого класса есть каждого рядка таблицы
 * - ReadWriteLock содержит 1 лок на запись и бесконечность локов на чтение
 * - Мы не можем взять лок на запись пока не освободяться все локи на чтение
 * - Если мы взяли лок на запись - все ждут пока мы не отпустим
 * - Если мы взяли лок на чтение - другие тоже могут брать локи на чтение
 */
public class TestPessimisticLocking extends AbstractLockingTest {

    @Before
    public void before() {
        setUp(Connection.TRANSACTION_READ_COMMITTED);
        clearDB();
    }

    /**
     * PESSIMISTIC_READ - Блокирует на чтение. Другие транзакции не могут менять данные пока мы с ними работаем.
     * Если во время блокирования другая транзакция меняет данные, мы ждём пока данные не поменяются полностью.
     *      MySQL: используется SELECT ... LOCK IN SHARE MODE
     *      Postgres: используется SELECT ... FOR SHARE
     */
    @Test
    public void testPessimisticReadLocking() {
        /**
         * Блокировка на чтение - множественная. Хорошая была бы аналогия с мячиками в корзинах. В Java есть аналог ReadWriteLock.
         * - tx1 READ, tx2 READ: обе транзакции читают данные и делают блокировку на чтение. Никто из них не может поменять данные.
         * 1. tx1.read(1, READ)
         * 2. tx2.read(1, READ) - прочитает нормально
         * 3. tx1.update(1->2), tx1.commit() - будет заблокирована потому что меняет данные, которые ещё заблокированы tx2
         */
        assertTransactionFail(LockModeType.PESSIMISTIC_READ, LockModeType.PESSIMISTIC_READ, "tx1.commit", LockTimeoutException.class);  // tx1.commit() will be blocked

        // Кто блокировку не спрашивает - тот без спроса читает что хочет
        testConcurrentTransactions(emf, LockModeType.PESSIMISTIC_READ, LockModeType.NONE);
        // Но записать не может
        assertTransactionFail(LockModeType.NONE, LockModeType.PESSIMISTIC_READ, "tx1.commit", LockTimeoutException.class);
    }

    /**
     * PESSIMISTIC_WRITE - Блокирует на запись и чтение. У транзакции появляется эксклюзивное право в данный момент записывать данные в эту ячейку.
     * Другие транзакции могут читать. Но если другие транзакции получают запись с блокировкий PESSIMISTIC_READ, то они будут ждать.
     *      MySQL: используется SELECT ... FOR UPDATE
     *      Postgres: используется SELECT ... FOR UPDATE
     *
     * Странная ситуация, хибернейт тут выбрасывает свой эксепшен, а не JPA. В блокировке PESSIMISTIC_READ наоборот.
     */
    @Test
    public void testPessimisticWriteLocking() {
        /**
         * - tx1 READ, tx2 WRITE: одна транзакция блокирует на чтение, другая потом на запись (заблокируется ли она)?
         * Можно ли блокировать на запись когда кто-то взял блокировку на чтение?
         * 1. tx1.read(1, READ)
         * 2. tx2.read(1, WRITE) - заблокируется, будет ждать пока не вернут блокировки на чтение
         */
        assertTransactionFail(LockModeType.PESSIMISTIC_READ, LockModeType.PESSIMISTIC_WRITE, "tx2.read", org.hibernate.exception.LockTimeoutException.class);  // tx2.read() will be blocked

        /**
         * Блокировка на запись - эксклюзивная
         * - tx1 WRITE, tx2 READ/WRITE: одна транзакция блокирует на запись, больше никто не может взять никакую блокировку
         * 1. tx1.read(1, WRITE)
         * 2. tx2.read(1, READ/WRITE) - заблокируется, будет ждать пока первая транзакция не отпустит блокировку на запись
         */
        assertTransactionFail(LockModeType.PESSIMISTIC_WRITE, LockModeType.PESSIMISTIC_READ, "tx2.read", org.hibernate.exception.LockTimeoutException.class);  // tx2.read() will be blocked
        assertTransactionFail(LockModeType.PESSIMISTIC_WRITE, LockModeType.PESSIMISTIC_WRITE, "tx2.read", org.hibernate.exception.LockTimeoutException.class);  // tx2.read() will be blocked

        // Кто блокировку не спрашивает - тот без спроса читает что хочет
        testConcurrentTransactions(emf, LockModeType.PESSIMISTIC_WRITE, LockModeType.NONE);
        // Но записать не может
        assertTransactionFail(LockModeType.NONE, LockModeType.PESSIMISTIC_WRITE, "tx1.commit", LockTimeoutException.class);
    }

    private void assertTransactionFail(LockModeType tx1LockMode, LockModeType tx2LockMode, String failedTxPointId, Class expectedException) {
        try {
            testConcurrentTransactions(emf, tx1LockMode, tx2LockMode);
            Assert.fail("No transaction fail");
        } catch (TransactionFailException e) {
            Assert.assertTrue(e.getCause().getClass().equals(expectedException));
            Assert.assertEquals(failedTxPointId, e.getTransactionId());
            e.printStackTrace();
        }
    }

    private void testConcurrentTransactions(EntityManagerFactory emf, LockModeType tx1LockMode, LockModeType tx2LockMode) {
        testConcurrentTransactions(emf, tx1LockMode, tx2LockMode, new HashMap<String, Object>());
    }

    private void testConcurrentTransactions(EntityManagerFactory emf, LockModeType tx1LockMode, LockModeType tx2LockMode, Map<String, Object> props) {
        // Init data
        EntityManager emInit = emf.createEntityManager();
        emInit.getTransaction().begin();
        NonVersionedEntity entity = new NonVersionedEntity("entity");
        emInit.persist(entity);
        emInit.getTransaction().commit();
        emInit.close();

        // Prepare ems
        EntityManager em1 = emf.createEntityManager();
        EntityManager em2 = emf.createEntityManager();

        // Start test
        try {
            em1.getTransaction().begin();
            em2.getTransaction().begin();

            // После того как мы получили данные, но ещё их не заблокировали лучше не выполнять никаких операций
            // Т.к. кто-то другой может в это время перезаписать данные
            NonVersionedEntity em1Entity = em1.find(entity.getClass(), entity.getId()); // Обычный запрос без блокировки
            em1.lock(em1Entity, tx1LockMode, props); // То при вызове lock() делается дополнительный запрос уже с блокировкой

            NonVersionedEntity em2Entity = null;
            try {
                // В этом случае будет сразу выполнен запрос с блокировкой
                em2Entity = em2.find(entity.getClass(), entity.getId(), tx2LockMode, props);
            } catch (LockTimeoutException e) {
                em2.getTransaction().rollback();
                throw new TransactionFailException("tx2.read", e.getCause());
            }

            em1Entity.setName("renamed em1");
            em2Entity.setName("renamed em2");

            try {
                em1.getTransaction().commit();
            } catch (RollbackException e) { throw new TransactionFailException("tx1.commit", e.getCause()); }

            try {
                em2.getTransaction().commit();
            } catch (RollbackException e) { throw new TransactionFailException("tx2.commit", e.getCause()); }
        } finally {
            em1.close();
            em2.close();
        }
    }

    /**
     * Optimistic Write + Pessimistic Read
     * Защищает связи между сущностями если связь не реализована на стороне блокируемого объекта.
     */
    @Test
    public void testPessimisticForceIncrementLocking() {
        // TODO
    }

    /**
     * По дефолту связанные с данной сущность другие объекты не блокируются т.к. это может вызвать дедлок
     * javax.persistence.lock.scope = PessimisticLockScope.EXTENDED - включает эту возможность
     * Проверить блокировку с разными типами связей
     *
     * Использовать нужно с большой осторожностью т.к. могут возникать дедлоки.
     */
    @Test
    public void testPessimisticLockWithRelationships() {
        // TODO проверить, распространяется ли блокировка на связанные сущности при eager loading (или при Lazy loading, когда потом мы дёргаем геттеры)
        // TODO проверить работу lock.scope
    }

    /**
     * JPA не обязывает реализовывать эту настройку, но некоторые провайдеры могут:
     * setHint()
     * javax.persistence.lock.timeout = 0 (не ждать), или максимальное количество милисекунд для ожидания
     * Реализация зависит от провайдера.
     */
    @Test(expected = TransactionFailException.class)
    public void testPessimisticLockTimeouts() {
        // TODO не работает?
        Map<String,Object> props = new HashMap<>();
        props.put("javax.persistence.lock.timeout",0); // No timeout, if error - we fall immediately
        testConcurrentTransactions(emf, LockModeType.PESSIMISTIC_READ, LockModeType.PESSIMISTIC_WRITE, props);
    }
}
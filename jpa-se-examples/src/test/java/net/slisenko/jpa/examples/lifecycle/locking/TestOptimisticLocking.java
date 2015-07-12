package net.slisenko.jpa.examples.lifecycle.locking;

import junit.framework.Assert;
import net.slisenko.jpa.examples.lifecycle.locking.exception.TransactionFailException;
import net.slisenko.jpa.examples.lifecycle.locking.model.EntityWithNameAndId;
import net.slisenko.jpa.examples.lifecycle.locking.model.NonVersionedEntity;
import net.slisenko.jpa.examples.lifecycle.locking.model.VersionedEntity;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.*;
import java.sql.Connection;

/**
 * JPA позоляет делать блокировки для отдельных сущностей, получая уровень уровень REPEATABLE_READ.
 * Это полезно если к примеру по умолчанию у нас стоит READ_COMMITTED.
 *
 * Блокировки могут быть:
 * 1. Оптимистические
 * 2. Пессимистические
 * Эти 2 виде блокировок - разные => разные use-cases, разная реализация. Нельзя сказать какая из них лучше.
 *
 * Оптимистическая блокировка:
 * Идея:
 * - Предполагаем оптимистически: скорее всего никто не будет трогать данные пока мы с ними работаем (случай параллельных изменений редкий)
 * - Если кто-то изменил данные пока мы с ними работали - получаем OptimisticLockException.
 * - Какая из транзакций упадёт - зависит от имплмементации. (В Hibernate выигрывает та, что закомитала первая)
 * - На уровне БД данные не блокируются, кто угодно может их читать и изменять.
 *
 * Реализация:
 * - Сущности добавляется версионный столбец, помеченный аннотацией @Version. При каждом обновлении сушности, номер версии увеличивается.
 * - Когда транзакиця комитится, выполняется сверка версии со значением в БД. Если кто-то другой уже успел обновить сущность - то версия в БД будет больше нашей.
 * - В случае если наши данные поменяли, получаем OptimisticLockException и наша транзакция откатывается.
 * - Нет нужды держать открытой транзакцию чтобы защищать данные.
 *
 * Use-cases:
 * - Запрашиваем данные из БД и передаём в слой представления. После того как пользователь отредактировал данные, передаём их обратно в БД.
 * - Редактирование происходит редко, например у нас только 1 администратор, который может менять контент
 *
 * Преимущества:
 * - Если @Verison-поле имеет тип Timestamp, у нас появляется бесплатный способ получить время последнего обновления сущности.
 *   Такой подход не рекомендуется использовать если у нас высоконагруженная система и две транзакции теоретически могут произойти
 *   в одно и то же время (с одинаковым Timestamp).
 * - Работает с detached-данными: мы можем отдать данные в слой представления, там что-то с ними поделать долгое время,
 *   а потом попытаться записать в базу и будет выполнена проверка на версию.
 *
 * Недостатки:
 * - Не работает при Bulk-updates (при выполнении JPA запросов UPDATE ...)
 * - Не позволит защитить данные от удаления другой транзакцией
 */

/**
 * TO THINK: Скорее эта блокировка решает не столько REPEATABLE_READ, а проблему перетирки изменений (Вторую проблему обновлений)!
 *
 * Это не воспроизвелось:
 * TODO если мы делаем lock, то будет выполнена проверка версии сущности мимо кеша! => при неповторяемом чтении транзакция отвалится. Нужно попробовать тест неповторяемого чтения на версионную сушность с использованием LockModeType.
 * вызов lock будет выполнять проверку версии мимо кеша.
 */
public class TestOptimisticLocking extends AbstractLockingTest {

    @Before
    public void before() {
        setUp(Connection.TRANSACTION_READ_COMMITTED);
        clearDB();
    }

    /**
     * Нет блокировок. Обе транзакции закомитаются, не произойдёт OptimisticLockException.
     * Транзакция, которая последней закомитает перетрёт изменения прошлой транзакции.
     */
    @Test
    public void testNoOptimisticBlock() {
        // Нет исключения, если используем неверсионную сущность, мы не узнаем о проблеме
        testConcurrentTransactions(emf, LockModeType.NONE, true, true, new NonVersionedEntity("non versioned"));

        // Если для неверсионной сущности пытаемся сделать оптимистическую блокировку, то полечаем NullPointer в недрах Hibernate
//        testConcurrentTransactionsNonVersionedEntity(emf, LockModeType.OPTIMISTIC, true, true);
    }

    /**
     * LockModeType.OPTIMISTIC (=LockModeType.READ in JPA 1.0)
     * При комите происходит проверка не поменялись ли данные + инкременитися номер версии.
     * Если две транзакции одновременно меняли какие-либо записи в таблицах, то выигрывает та, которая закомитала первая.
     * Вторая транзакция упадёт с ошибкой.
     *
     * Блокировка работает только для сущностей, которые мы изменяем.
     *
     * TODO одно ли тоже если мы используем OPTIMISTIC или просто работаем с версионной сущностью?
     *
     * TODO Почему-то получаем NullPointer если используем сущность без колонки @Version - видиио баг Hibernate
     * TODO .setLockMode(LockModeType.OPTIMISTIC) - почему-то Hibernate кидает NullPointer если у сущности нет поля с @Version. Проверить, обязательно ли должны быть версионные сущности для оптимистических блокировок.
     */
    @Test
    public void testOptimisticReadLocking() {
        checkTransactionFailed(true, true, "tx2");  // tx1 commits, tx2 fails
        checkTransactionFailed(true, false, "tx1"); // tx2 commits, tx1 fails
        checkTransactionFailed(false, true, "tx2"); // tx1 commits, tx2 fails
        checkTransactionFailed(false, false, "tx1"); // tx2 commits, tx1 fails
    }

    private void checkTransactionFailed(boolean em1ReadsFirst, boolean em1CommitsFirst, String failedTxId) {
        try {
            testConcurrentTransactions(emf, LockModeType.NONE, em1ReadsFirst, em1CommitsFirst, new VersionedEntity("versioned"));
            Assert.fail("No optimistic lock exception");
        } catch (TransactionFailException e) {
            Assert.assertTrue(e.getCause() instanceof OptimisticLockException);
            Assert.assertEquals(failedTxId, e.getTransactionId());
            e.printStackTrace();
        }
    }

    /**
     * Одновременно модифицирвем данные двумя разными транзакциями.
     */
    private void testConcurrentTransactions(EntityManagerFactory emf, LockModeType lockMode, boolean em1ReadsFirst, boolean em1CommitsFirst, EntityWithNameAndId entity) {
        // Init data
        EntityManager emInit = emf.createEntityManager();
        emInit.getTransaction().begin();
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

            if (em1ReadsFirst) {
                em1.find(entity.getClass(), entity.getId(), lockMode).setName("em1");
                em2.find(entity.getClass(), entity.getId()).setName("em2");
            } else {
                em2.find(entity.getClass(), entity.getId()).setName("em2");
                em1.find(entity.getClass(), entity.getId(), lockMode).setName("em1");
            }

            if (em1CommitsFirst) {
                commitTransactionAndHandleError(em1, "tx1");
                commitTransactionAndHandleError(em2, "tx2");
            } else {
                commitTransactionAndHandleError(em2, "tx2");
                commitTransactionAndHandleError(em1, "tx1");
            }
        } finally {
            em1.close();
            em2.close();
        }
    }

    private void commitTransactionAndHandleError(EntityManager em, String transactionId) {
        try {
            em.getTransaction().commit();
        } catch (RollbackException e) {
            throw new TransactionFailException(transactionId, e.getCause());
        }
    }

    /**
     * LockModeType.OPTIMISTIC_FORCE_INCREMENT (=LockModeType.WRITE in JPA 1.0) - инкрементит версию сразу при первом чтении.
     * Эта блокировка используется если мы изменяем связи, не находящиеся на стороне данной сущности.
     * Либо мы хотим показать что мы меняем данные других таблиц, которые относятся к данной сущности (Мы косвенно меняем эту сущность).
     * Блокировка работает даже если мы сущность не меняем. Выполняя блокировку мы как-бы показываем что сущность "Dirty".
     *
     * Сценарий проверки:
     * 1. Одна транзакция запрашивает сущность и её связи, тем самым "составляя отчёт". Сама сущность не меняется.
     * 2. Другая транзакция меняет связи
     *
     * OPTIMISTIC - исключение не произойдёт, мы не узнаем о проблеме, которую сделала другая транзакция
     * OPTIMISTIC_FORCE_INCREMENT - произойдёт исключение, мы узнаем о проблеме. Только одна из двух транзакций завершится успешно.
     */
    @Test
    public void testOptimisticForceIncrementReadLocking() {
        // We do not get error and do not know about problem
        testConcurrentTransactionsRelationships(emf, LockModeType.OPTIMISTIC);

        // We get error and know about problem
        try {
            testConcurrentTransactionsRelationships(emf, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            // TODO почему-то мы не ловим эксепшен если стаит уровень изоляции ниже REPEATABLE_READ в setUp()
            // TODO нужно разобраться
            Assert.fail("No optimistic lock exception!");
        } catch (RollbackException e) {
            Assert.assertTrue(e.getCause() instanceof OptimisticLockException);
            e.printStackTrace();
        }
    }

    /**
     * Одна транзакция делает отчёт: находит все VersionEntity, и находит все их NonVersionedEntity
     * Другая транзакция делает изменения: перебрасывает связь у NonVersionedEntity с одной VersionEntity на другую
     *
     * С помощью оптимистической инкрементной блокировки мы можем понять, изменилась ли информация пока мы делали отчёт (валидный ли отчёт).
     * Связь не находится на стороне VersionedEntity, поэтому если использовать обычную оптимистическую блокировку,
     * то мы не обнаружим невалидность отчёта.
     */
    public void testConcurrentTransactionsRelationships(EntityManagerFactory emf, LockModeType lockMode) {
        EntityManager emInit = emf.createEntityManager();
        // Init
        emInit.getTransaction().begin();
        VersionedEntity ve = new VersionedEntity("ve");
        emInit.persist(ve);

        NonVersionedEntity nve1 = new NonVersionedEntity("nve1");
        nve1.setVersionedEntity(ve);
        emInit.persist(nve1);

        VersionedEntity ve2 = new VersionedEntity("ve2");
        emInit.persist(ve2);
        emInit.getTransaction().commit();
        emInit.close();

        EntityManager em1 = emf.createEntityManager();
        EntityManager em2 = emf.createEntityManager();

        // Em1 считает количество NonVersionedEntities для каждой VersionEntities
        em1.getTransaction().begin();
        VersionedEntity em1Ve = em1.find(VersionedEntity.class, ve.getId(), lockMode);
        Assert.assertEquals(1, em1Ve.getNonVersionedEntities().size());

        // Em2 перекидывает связь на другую VersionedEntity
        em2.getTransaction().begin();
        VersionedEntity em2Ve2 = em2.find(VersionedEntity.class, ve2.getId(), lockMode);
        NonVersionedEntity em2nve = em2.find(NonVersionedEntity.class, nve1.getId());
        em2nve.setVersionedEntity(em2Ve2);
        em2.getTransaction().commit();

        VersionedEntity em1Ve2 = em1.find(VersionedEntity.class, ve2.getId(), lockMode);
        // Другая транзакция поменяла связь (ve1 -> ve2), но мы увидели как-будто связи и у ve1, и у ve2
        // If no LOCK specified we will not get exception
        em1Ve2.getNonVersionedEntities().size();
//        Assert.assertEquals(0, em1Ve2.getNonVersionedEntities().size());

        // TODO должен ди идти мимо кеша запрос с локом? В этом случае не работает.
        // Assert.assertEquals(1, em1.find(VersionedEntity.class, ve.getId()).getNonVersionedEntities().size());
        // Assert.assertEquals(1, em1.find(VersionedEntity.class, ve.getId(), lockMode).getNonVersionedEntities().size());

        em1.getTransaction().commit();

        em1.close();
        em2.close();
    }

    /**
     * EntityManager.lock() - добавляет объект в список для блокировки
     * TODO в тесте проверить разницу между блокировкой при запросе и блокировкой отдельно методом lock
     */
    @Test
    public void testEntityManagerLockMethod() {

    }

    /**
     * Оптимистическая блокировка не работает когда выполняется Bulk-update запрос.
     */
    @Test
    public void testOptimisticLockingBulkUpdate() {
        // Создаём версионную запись
        EntityManager em = emf.createEntityManager();
        VersionedEntity ve = new VersionedEntity("ve");
        em.getTransaction().begin();
        em.persist(ve);
        em.getTransaction().commit();

        // Выполняем BULK-update запрос
        em.getTransaction().begin();
        em.createQuery("UPDATE VersionedEntity ve SET ve.name='changed'").executeUpdate();
        em.getTransaction().commit();
        em.clear();

        // Read version value
        Assert.assertEquals(0, em.find(VersionedEntity.class, ve.getId()).getVersion());
        Assert.assertEquals("changed", em.find(VersionedEntity.class, ve.getId()).getName());
    }
}
package net.slisenko.jpa.examples.lifecycle.locking;

import net.slisenko.jpa.examples.lifecycle.locking.model.NonVersionedEntity;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import javax.persistence.*;
import java.sql.Connection;
import java.util.List;

/**
 * https://www.youtube.com/watch?v=4PKZRQAtf38
 * http://dr-magic.blogspot.com/2010/01/5-hibernate-1.html
 * http://dr-magic.blogspot.com/2010/01/hibernate-2.html
 * http://dr-magic.blogspot.com/2010/01/3.html
 * http://www.myshared.ru/slide/649123/
 * http://from-prog.blogspot.com/2011/03/optimistic-locking-jpa.html
 *
 * Может сделать презентацию по этой теме с примерами? Можно мини-тренинг.
 * TODO нарисовать диаграмму где показаны все виды блокировок и аномалии, а так же бизнес-кейсы
 *
 * Anomalies:
 * 0. Lost changes - no locking, no isolation between transactions. Each transaction cah read and modify another transactions data.
 * Protect by: ISOLATION_READ_UNCOMMITTED - no possible to modify, but possible to read dirty data
 * грязные данные можно и модифицировать, и читать
 *      tx1.update(1)
 *      tx2.update(2)
 *      tx1.rollback() 1->0
 *      we lost changes of tx2, value in database will be 0
 * 1. Dirty read - one transaction reads data which was not committed, transaction intermediate data is visible. Transactions can not modify data of each other.
 * Protect by: ISOLATION_READ_COMMITTED (default) - no access to dirty intermediate data
 * грязные данные можно только читать
 *      rx1.read() returns 1
 *      tx1.update(2)
 *      tx2.read() returns 2
 *      tx1.rollback() changes 2 -> 1 // tx2 has wrong dirty data 2
 * 2. Not repeated read
 * Protect by: ISOLATION_REPEATABLE_READ - block table rows (SELECT FOR UPDATE) - block for read and write
 *      tx1.read() returns 1
 *      tx2.update(2) tx2.commit() was 1 -> became 2
 *      tx1.read() returns 2
 * + Вторая проблема обновлений (ещё один тест на неповторяемое чтение) – частный случай неповторяемого чтения.
 * Представьте себе, что есть две параллельные транзакции, обе читают строки, одна записывает туда и фиксируется, и далее
 * вторая пишет туда же и фиксируется. Изменения, сделанные первой транзакцией, будут потеряны.
 * 3. Phantom insert/delete
 * Protect by: ISOLATION_SERIALIZABLE - block all table
 *      tx1.read() reads 1
 *      tx2.insert(2) tx2.commit() inserts 2
 *      tx1.read() reads 1,2
 *
 * isolation levels can be specified for:
 * - all transactions (hibernate.connection.isolation={1,2,4,8})
 * - specific transaction (TODO how to specify it in config?)
 *
 * in JPA we can lock specific objects (EntityManager.lock())
 */
public class TestJPAConcurrentAnomalies extends AbstractLockingTest {

    /**
     * TRANSACTION_READ_UNCOMMITTED
     *      not allows transactions modify dirty data
     *      allows read dirty data
     */
    @Test
    public void testReadUncommitted() {
        setUp(Connection.TRANSACTION_READ_UNCOMMITTED);
        Assert.assertFalse(canDropDirtyChangesOfAnotherTransaction(emf));
        Assert.assertTrue(canReadDirtyData(emf));
        // Behaves similar not depends on isolation level: when we execute query second time, EntityManager returns cached values from context
        Assert.assertFalse(canReadNotRepeatableUpdate(emf));
        Assert.assertTrue(canReadNotRepeatableInsertDelete(emf));

        canRewriteAnotherCommittedTransactionChanges(emf);
    }

    /**
     * TRANSACTION_READ_COMMITTED
     *      not allows dirty data read
     *      allows not repeatable read
     */
    @Test
    public void testReadCommitted() {
        setUp(Connection.TRANSACTION_READ_COMMITTED);
        Assert.assertFalse(canDropDirtyChangesOfAnotherTransaction(emf));
        Assert.assertFalse(canReadDirtyData(emf));
        // Behaves similar not depends on isolation level: when we execute query second time, EntityManager returns cached values from context
        Assert.assertFalse(canReadNotRepeatableUpdate(emf));
        Assert.assertTrue(canReadNotRepeatableInsertDelete(emf));

        canRewriteAnotherCommittedTransactionChanges(emf);
    }

    /**
     * TRANSACTION_REPEATABLE_READ
     *      not allows not repeatable reads
     *      not allows dirty data read
     */
    @Test
    public void testRepeatableRead() {
        setUp(Connection.TRANSACTION_REPEATABLE_READ);
        Assert.assertFalse(canDropDirtyChangesOfAnotherTransaction(emf));
        Assert.assertFalse(canReadDirtyData(emf));
        // Behaves similar not depends on isolation level: when we execute query second time, EntityManager returns cached values from context
        Assert.assertFalse(canReadNotRepeatableUpdate(emf));
        Assert.assertFalse(canReadNotRepeatableInsertDelete(emf));

        canRewriteAnotherCommittedTransactionChanges(emf);
    }

    /**
     * TRANSACTION_SERIALIZABLE
     *      strict and full transaction isolation, like they work successively
     */
    @Ignore
    @Test
    public void testZSerializable() {
        setUp(Connection.TRANSACTION_SERIALIZABLE);

        try {
            canDropDirtyChangesOfAnotherTransaction(emf);
            Assert.fail("No LockTimeoutException");
        } catch (LockTimeoutException e) {
            System.out.println("canDropDirtyChangesOfAnotherTransaction: In serializable isolation level we get: " + e);
        }

        try {
            canReadDirtyData(emf);
            Assert.fail("No LockTimeoutException");
        } catch (LockTimeoutException e) {
            System.out.println("canReadDirtyData: In serializable isolation level we get: " + e);
        }

        try {
            canReadNotRepeatableUpdate(emf);
            Assert.fail("No LockTimeoutException");
        } catch (LockTimeoutException e) {
            System.out.println("canReadNotRepeatableUpdate: In serializable isolation level we get: " + e);
        }

        try {
            canReadNotRepeatableInsertDelete(emf);
            Assert.fail("No LockTimeoutException");
        } catch (LockTimeoutException e) {
            System.out.println("canReadNotRepeatableInsert: In serializable isolation level we get: " + e);
        }
    }

    public boolean canDropDirtyChangesOfAnotherTransaction(EntityManagerFactory emf) {
        clearDB();

        NonVersionedEntity entity = new NonVersionedEntity("initial name");

        // Insert initial data
        EntityManager em0 = emf.createEntityManager();
        try {
            em0.getTransaction().begin();
            em0.persist(entity);
            em0.getTransaction().commit();
        } finally {
            em0.close();
        }

        EntityManager em1 = emf.createEntityManager();
        EntityManager em2 = emf.createEntityManager();

        try {
            System.out.println("Transaction 1 changes data");
            // Start tx1, update data null->"em1 data"
            em1.getTransaction().begin();
            NonVersionedEntity em1Entity = em1.find(NonVersionedEntity.class, entity.getId());
            em1Entity.setName("em1 data");
            em1.flush();

            // Start tx2, update same data null->"em2 data"
            System.out.println("Transaction 2 changes same data");

            em2.getTransaction().begin();
            NonVersionedEntity em2Entity = em1.find(NonVersionedEntity.class, entity.getId());
            em2Entity.setName("em2 data");
            em2.flush();
            em2Entity = em2.find(NonVersionedEntity.class, em2Entity.getId());

            System.out.println("Transaction 1 rollback");
            // tx1.rollback(), we loose also changes, made by tx2
            em1.getTransaction().rollback();

            // tx2 reads data again and finds data loss
            em2Entity = em2.find(NonVersionedEntity.class, em2Entity.getId());
            System.out.println("Transaction 2 queries it's changes, but changes are lost, value=" + em2Entity.getName());

            return em2Entity.getName() == "initial name";
        } finally {
            em2.getTransaction().commit();
            em1.close();
            em2.close();
        }
    }

    /**
     * Тестирует грязное чтение - одна транзакция может получать доступ к промежуточным (не закомитанным) данным другой транзакции
     *
     * У PostgreSQL никогда не воспроизводится чтение грязных данных (видимо по умолчанию это выключено)
     */
    public boolean canReadDirtyData(EntityManagerFactory emf) {
        clearDB();

        EntityManager em1 = emf.createEntityManager();
        EntityManager em2 = emf.createEntityManager();

        try {
            // Start tx1
            em1.getTransaction().begin();
            // Add entity in tx1, not commit now!
            NonVersionedEntity entity = new NonVersionedEntity("tx1 temp entity");
            em1.persist(entity);

            // Start tx2
            em2.getTransaction().begin();
            // Read intermediate tx1 data in tx2
            NonVersionedEntity entityInTx2 = em2.find(NonVersionedEntity.class, entity.getId());
            System.out.println("Dirty data: " + entityInTx2);
            return entityInTx2 != null;
        } finally {
            em1.getTransaction().commit();
            em2.getTransaction().commit();
        }
    }

    /**
     * Решается версионированием, попробовать этот тест с двумя типами классов
     * В PostgreSQL на уровне REPEATABLE_READ выдаёт исключение, на уровнях ниже - не выдаёт
     * TODO что делать с этим тестом?
     *
     * @param emf
     */
    public void canRewriteAnotherCommittedTransactionChanges(EntityManagerFactory emf) {
        EntityManager emInit = emf.createEntityManager();
        EntityManager em1 = emf.createEntityManager();
        EntityManager em2 = emf.createEntityManager();

        try {
            emInit.getTransaction().begin();
            NonVersionedEntity entity = new NonVersionedEntity("entity");
            emInit.persist(entity);
            emInit.getTransaction().commit();
            emInit.clear();

            em1.getTransaction().begin();

            NonVersionedEntity em1Entity = em1.find(NonVersionedEntity.class, entity.getId());

            em2.getTransaction().begin();
            em2.find(NonVersionedEntity.class, entity.getId()).setName("em2Name");
            em2.getTransaction().commit();

            em1Entity.setName("em1Renamed");
            em1.getTransaction().commit();

            // What name is in database?
            entity = emInit.find(NonVersionedEntity.class, entity.getId());
            System.out.println("ENTITY IN DATABASE: " + entity.getName());
        } finally {
            emInit.close();
            em1.close();
            em2.close();
        }
    }

    /**
     * POSTGRES - если ниже уровня REPEATEBLE READ - то неповторяемое чтение воспроизводится
     * MySQL - неповторяемое чтение никогда не воспроизводится потому что EM достаёт данные из контекста
     * @param emf
     * @return
     */
    public boolean canReadNotRepeatableUpdate(EntityManagerFactory emf) {
        return canReadNotRepeatable(emf, true, false);
    }

    public boolean canReadNotRepeatableInsertDelete(EntityManagerFactory emf) {
        return canReadNotRepeatable(emf, false, true);
    }

    public boolean canReadNotRepeatable(EntityManagerFactory emf, boolean isDoUpdate, boolean isDoInsertDelete) {
        clearDB();

        EntityManager emInit = emf.createEntityManager();

        // Init 2 entities in DB
        emInit.getTransaction().begin();
        NonVersionedEntity entity1 = new NonVersionedEntity("entity");
        emInit.persist(entity1);
        NonVersionedEntity entity2 = new NonVersionedEntity("entity");
        emInit.persist(entity2);
        emInit.getTransaction().commit();
        emInit.close();

        EntityManager em1 = emf.createEntityManager();
        EntityManager em2 = emf.createEntityManager();

        try {
            em1.getTransaction().begin();
            // Read data in tx1
            List<NonVersionedEntity> em1Entities1 = em1.createQuery("FROM NonVersionedEntity", NonVersionedEntity.class)
                    .getResultList();
            // Start another transaction and change data
            em2.getTransaction().begin();
            if (isDoUpdate) {
                // Update one entity
                em2.find(NonVersionedEntity.class, entity1.getId()).setName("renamed");
            }
            if (isDoInsertDelete) {
                // Remove another entity
                em2.remove(em2.find(NonVersionedEntity.class, entity2.getId()));
                // Insert new entity
                em2.persist(new NonVersionedEntity("new entity"));
            }
            em2.getTransaction().commit();

            // Read data in tx1s
            List<NonVersionedEntity> em1Entities2 = em1.createQuery("FROM NonVersionedEntity nve WHERE nve.name != 'wrong name'", NonVersionedEntity.class)
                    .getResultList();
            em1.getTransaction().commit();

            // Compare entities of first and second queries
            return !em1Entities1.equals(em1Entities2);
        } finally {
            em1.close();
            em2.close();
        }
    }

//    /**
//     * We do several transactions, but commit all of then only when EntityManager.flush()
//     * For this we set EntityManager.setFlushMode(MANUAL)
//     * TODO doesn't work for me
//     */
//    @Test
//    public void testBusinessTransaction() {
//        Session session = em.unwrap(Session.class);
////        session.setFlushMode(FlushMode.MANUAL);
//        System.out.println("Underlying Hibernate session flushmode " + session.getFlushMode());
////        System.out.println("EntityManager flushmode " + em.getFlushMode());
//
//        session.getTransaction().begin();
//        session.save(new NonVersionedEntity("hibernate manual flush 1"));
//        session.save(new NonVersionedEntity("hibernate manual flush 2"));
//        session.save(new NonVersionedEntity("hibernate manual flush 3"));
//        session.save(new NonVersionedEntity("hibernate manual flush 4"));
//        session.getTransaction().commit();
//
//        session.getTransaction().begin();
//        session.save(new NonVersionedEntity("hibernate manual flush 2.1"));
//        session.save(new NonVersionedEntity("hibernate manual flush 2.2"));
//        session.save(new NonVersionedEntity("hibernate manual flush 2.3"));
//        session.save(new NonVersionedEntity("hibernate manual flush 2.4"));
//        session.getTransaction().commit();
//
//        System.out.println("===== Flush goes after this line =====");
//        session.close(); // Disable any changes to database
//
////        em.getTransaction().begin();
////        em.persist(new NonVersionedEntity("flush on commit"));
////        em.persist(new NonVersionedEntity("flush on commit"));
////
////        System.out.println("===== No flush here =====");
////
////        em.persist(new NonVersionedEntity("flush on commit"));
////        em.persist(new NonVersionedEntity("flush on commit"));
////
////        System.out.println("===== Flushing should be after this line =====");
////        em.getTransaction().commit();
//    }
}
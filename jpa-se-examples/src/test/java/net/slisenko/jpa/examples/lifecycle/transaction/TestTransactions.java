package net.slisenko.jpa.examples.lifecycle.transaction;

import net.slisenko.AbstractJpaTest;
import net.slisenko.jpa.examples.JPAUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import java.util.List;

public class TestTransactions extends AbstractJpaTest {

    EntityManager em2;

    @Before
    public void setUpSecondEM() {
        em2 = emf.createEntityManager();
    }

    // TODO try clean() inside transaction, see that not all entities can be rolled-back if we do this
    /**
     * the use of transaction-scoped entity managers outside of a transaction. Any query
     executed in this situation returns detached entity instances instead of managed entity instances. To make changes
     on these detached entities, they must first be merged into a persistence context before they can be synchronized with
     the database.
     */

    /**
     * If we use merge() method parameter object will not get ID specified and will be detached.
     *
     * After em.clear() all objects become detached.
     * After transaction rollback all entities become detached.
     *
     * If we do persist/merge in transaction -> Entities will have ID parameter specified.
     *      If commit entities with this IDs will appear in Database.
     *      If rollback: Entities with this IDs will not appear in database. But sequence will be moved up and this IDs will not be used again.
     *
     * If we persist/merge not in transaction -> Entities will not get IDs
     */
    @Test
    public void test() {
        // Persist/merge not in transaction
        // Entity doesn't get ID until transaction commits
        SimpleObject so = new SimpleObject("object1");
        em.persist(so);

        SimpleObject so2 = new SimpleObject("object2");
        SimpleObject so2Merged = em.merge(so2);

        System.out.println("<Not in transaction -> entities are managed, IDs are empty>");
        JPAUtil.printContext(em);

        em.getTransaction().begin();
        em.getTransaction().rollback();

        System.out.println("<After transaction rollback -> empty context>");
        JPAUtil.printContext(em);

        // Merge entities back
        so.setName("changed name");
        so = em.merge(so);
        so2Merged = em.merge(so2);

        System.out.println("<Merge entities back -> IDs are empty>");
        JPAUtil.printContext(em);

        em.getTransaction().begin();

        System.out.println("<Begin transaction, we already have merged entities -> IDs are empty>");
        JPAUtil.printContext(em);

        em.getTransaction().rollback();

        em.getTransaction().begin();
        em.merge(so);
        em.merge(so2);
        System.out.println("<Merge entities inside transaction -> IDs are assigned!>");
        JPAUtil.printContext(em);
        em.getTransaction().commit();

        System.out.println("<Context after tx.commit(), entities are in context>");
        JPAUtil.printContext(em);

        em.clear();

        System.out.println("<Context after em.clear() -> empty>");
        JPAUtil.printContext(em);
    }

    /**
     * Create two parallel transactions and see which IDs are assigned
     * Each transaction reserves IDs
     */
    @Test
    public void testParallelTransactions() {
        SimpleObject so = new SimpleObject("so");
        SimpleObject so2 = new SimpleObject("so2");

        em.getTransaction().begin();
        em.merge(so);
        em.merge(so2);

        System.out.println("<EM in transaction>");
        JPAUtil.printContext(em);

        em2.getTransaction().begin();
        em2.merge(so);
        em2.merge(so2);

        System.out.println("<EM2 in transaction>");
        JPAUtil.printContext(em2);

        em.getTransaction().rollback();
        em2.getTransaction().commit();

        em.clear();
        List<SimpleObject> simpleObjects = em.createQuery("from SimpleObject", SimpleObject.class).getResultList();
        for (SimpleObject object : simpleObjects) {
            System.out.println(object);
        }

        System.out.println("<Entities after query clean context>");
        JPAUtil.printContext(em);
    }

    /**
     * One EM deletes entity, another EM edits
     */
    @Test
    public void testTransactionConflict() {
        SimpleObject so = new SimpleObject("so");

        // Add record
        em.getTransaction().begin();
        em.persist(so);
        em.getTransaction().commit();
        em.clear();

        em.getTransaction().begin();
        em2.getTransaction().begin();

        so = em.merge(so);
        so.setName("so renamed");

        SimpleObject soQuried = em2.find(SimpleObject.class, so.getId());
        em2.remove(soQuried);

        em2.getTransaction().commit();
        em.getTransaction().commit(); // This transaction can not update entity because it was deleted
    }

    @After
    public void tearDownSecondEM() {
        em2.close();
    }
}
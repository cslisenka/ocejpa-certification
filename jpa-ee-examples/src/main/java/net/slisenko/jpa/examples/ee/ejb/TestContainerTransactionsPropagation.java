package net.slisenko.jpa.examples.ee.ejb;

import net.slisenko.jpa.examples.ee.model.EESimpleEntity;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * J2EE container manages transactions automatically (begin, commit, rollback).
 * We can tune this behaviour using @Transactional annotation.
 * There are 6 types: MANDATORY, REQUIRED, REQUIRES_NEW, SUPPORTS, NOT_SUPPORTED, NEVER.
 *
 * If no attribute was specified on the class, so the default behavior of REQUIRED will apply to all the methods of the class.
 *
 * TransactionAttributeTypes are only taken into consideration when crossing Bean boundaries.
 * When calling methods within the same bean TransactionAttributeTypes have no effect, no matter what Types are put on the methods.
 *
 * I think the thing is each bean is wrapped in a proxy that controls the transactional behaviour. When you call from one bean to another, you're
 * going via that bean's proxy and the transaction behaviour can be changed by the proxy. But when a bean calls a method on itself with a different
 * transaction attribute, the call doesn't go via the proxy, so the behaviour doesn't change.
 */
@TransactionManagement(TransactionManagementType.CONTAINER)
@Path("/tx")
@Stateless
public class TestContainerTransactionsPropagation {

    @PersistenceContext
    private EntityManager em;

    @EJB
    private AnotherBean anotherBean;

    @GET
    @Path("/clear")
    @Produces(MediaType.APPLICATION_JSON)
    public String clean() {
        int recordsDeleted = em.createQuery("DELETE FROM EESimpleEntity").executeUpdate();
        return String.format("%d records deleted", recordsDeleted);
    }

    @GET
    @Path("/view")
    @Produces(MediaType.APPLICATION_JSON)
    public List<EESimpleEntity> getAll() {
        return em.createQuery("SELECT e FROM EESimpleEntity e").getResultList();
    }

    /**
     * REQUIRES_NEW
     * no tx: begin new
     *    tx: begin new
     *
     * tx1.begin
     * tx1.persist(1)
     *      tx2.begin
     *      tx2.persist(2)
     *      tx2.commit
     * tx1.rollback
     *
     * 2 - saved to database
     * 1 - not saved because of rollback
     */
    @GET
    @Path("/requiresNew")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(Transactional.TxType.REQUIRED)
    public List<EESimpleEntity> testMandatoryNoTransaction() {
        EESimpleEntity entity = new EESimpleEntity("tx=required, should not be saved because of rollback in future");
        em.persist(entity);
        // Method works in another transaction which success
        anotherBean.requiresNewTx(entity.getId());
        // Current transaction rollbacks
        throw new RuntimeException("force rollback main transaction");
    }

    /**
     * NOT SUPPORTED
     * no tx: -
     *    tx: suspend
     *
     * We can not do updates/inserts without transaction.
     * This method throws exception.
     */
    @GET
    @Path("/notSupported")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public EESimpleEntity testNotSupported() {
        EESimpleEntity entity = new EESimpleEntity("persists in not supported");
        em.persist(entity);
        return entity;
    }

    /**
     * NEVER
     * no tx: -
     *    tx: exception
     *
     * We get exception because we open transaction in this method and anotherBean.neverTx() has "never" transaction attribute
     * and throws exception because we already have transaction opened.
     */
    @GET
    @Path("/never")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(Transactional.TxType.REQUIRED)
    public EESimpleEntity testNever() {
        EESimpleEntity entity = new EESimpleEntity("entity persisted in required, but next method throws exception and our transaction was not succeed");
        em.persist(entity);
        anotherBean.neverTx();
        return entity;
    }

    /**
     * MANDATORY
     * no tx: exception
     *    tx: join
     */
    @GET
    @Path("/mandatoryInsideTx")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(Transactional.TxType.REQUIRED)
    public EESimpleEntity testMandatoryInsideTx() {
        EESimpleEntity entity = new EESimpleEntity("entity persisted in required");
        em.persist(entity);
        return anotherBean.mandatoryTx(entity.getId());
    }

    /**
     * Throws exception because we do not have parent transaction.
     */
    @GET
    @Path("/mandatoryNoTx")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public EESimpleEntity testMandatoryNoTx() {
        EESimpleEntity entity = new EESimpleEntity("entity persisted in required, but next method throws exception and our transaction was not succeed");
        em.persist(entity);
        anotherBean.mandatoryTx(entity.getId());
        return entity;
    }

    /**
     * SUPPORTS
     * no tx: -
     *    tx: join
     */
    @GET
    @Path("/supports")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(Transactional.TxType.REQUIRED)
    public EESimpleEntity testSupports() {
        EESimpleEntity entity = new EESimpleEntity("entity persisted in required");
        em.persist(entity);
        anotherBean.supportsTx(entity.getId());
        return entity;
    }
}
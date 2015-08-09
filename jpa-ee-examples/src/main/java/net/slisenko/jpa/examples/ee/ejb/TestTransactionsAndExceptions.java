package net.slisenko.jpa.examples.ee.ejb;

import net.slisenko.jpa.examples.ee.exception.MyApplicationException;
import net.slisenko.jpa.examples.ee.exception.MyApplicationExceptionWithRollback;
import net.slisenko.jpa.examples.ee.model.EESimpleEntity;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@TransactionManagement(TransactionManagementType.CONTAINER)
@Path("/tx")
@Stateless
public class TestTransactionsAndExceptions {

    @PersistenceContext
    private EntityManager em;

    @EJB
    private AnotherBean anotherBean;

    @EJB
    private SessionbeanWithErrors sessionBean;

    /**
     * All runtime exceptions thrown by ejb beans transformed to EJBException.
     * In this case it is EJBTransactionRolledbackException.
     */
    @GET
    @Path("/testExceptionType")
    @Produces(MediaType.APPLICATION_JSON)
    public String clean() {
        try {
            anotherBean.throwSystemException();
        } catch (EJBTransactionRolledbackException e) {
            return "EJBTransactionRolledbackException";
        } catch (EJBException e) {
            return "EJBException";
        }

        return "";
    }

    /**
     * Transaction is not commited because we got system exception
     * All runtime exceptions are automatically wrapped to EJBException.
     */
    @GET
    @Path("/txWithSystemException")
    @Produces(MediaType.APPLICATION_JSON)
    public EESimpleEntity testSystemExceptionBreaksTransaction() {
        EESimpleEntity entity = new EESimpleEntity("We got system exception");
        em.persist(entity);
        try {
            // Because method throws exception, transaction is marked for rollback.
            anotherBean.throwSystemException();
        } catch (EJBException e) {
        }
        return entity;
    }

    /**
     * Transaction is commited because we got application exception.
     * Application exceptions are not wrapped to EJBException.
     */
    @GET
    @Path("/txWithApplicationException")
    @Produces(MediaType.APPLICATION_JSON)
    public EESimpleEntity testApplicationExceptionNotBreaksTransaction() {
        EESimpleEntity entity = new EESimpleEntity("We got application exception");
        em.persist(entity);
        try {
            // Because method throws exception, transaction is marked for rollback.
            anotherBean.throwApplicationException();
        } catch (MyApplicationException e) {
        }
        return entity;
    }

    /**
     * Transaction is not commited because we got application exception with rollback=true.
     * Application exceptions are not wrapped to EJBException.
     */
    @GET
    @Path("/txWithApplicationExceptionWithRollback")
    @Produces(MediaType.APPLICATION_JSON)
    public EESimpleEntity testApplicationExceptionWithRollbackBreaksTransaction() {
        EESimpleEntity entity = new EESimpleEntity("We got application exception with rollback=true");
        em.persist(entity);
        try {
            // Because method throws exception, transaction is marked for rollback.
            anotherBean.throwApplicationExceptionWithRollback();
        } catch (MyApplicationExceptionWithRollback e) {
        }
        return entity;
    }

    public void callSessionBeanWithError() {
        sessionBean.throwException();
    }

    public List<Integer> callGetNumbers() {
        return sessionBean.getNumbers();
    }

    public int callAdd() {
        return sessionBean.addNumber();
    }
}
package net.slisenko.jpa.examples.ee.ejb;

import net.slisenko.jpa.examples.ee.exception.MyApplicationException;
import net.slisenko.jpa.examples.ee.exception.MyApplicationExceptionWithRollback;
import net.slisenko.jpa.examples.ee.model.EESimpleEntity;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Stateless
public class AnotherBean {

    @PersistenceContext
    private EntityManager em;

     @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void requiresNewTx(int parentTxEntityId) {
        // Check that we do not find entity from parent transaction
        EESimpleEntity parentTxEntity = em.find(EESimpleEntity.class, parentTxEntityId);
        System.out.println("Entity from parent transaction=" + parentTxEntity);
        if (parentTxEntity != null) {
            // We do not get this exception because this transaction can not see parent transaction entity
            throw new RuntimeException("not null!");
        }

        EESimpleEntity entity = new EESimpleEntity("tx=requires_new should be saved, I can not see parent tx entity=" + parentTxEntity);
        em.persist(entity);
    }

    @Transactional(Transactional.TxType.NEVER)
    public EESimpleEntity neverTx() {
        EESimpleEntity entity = new EESimpleEntity("persists in not supported");
        em.persist(entity);
        return entity;
    }

    @Transactional(Transactional.TxType.MANDATORY)
    public EESimpleEntity mandatoryTx(int parentEntityId) {
        EESimpleEntity parentEntity = em.find(EESimpleEntity.class, parentEntityId);
        EESimpleEntity entity = new EESimpleEntity("entity in mandatory tx, we can see parent entity " + parentEntity);
        em.persist(entity);
        return entity;
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public EESimpleEntity supportsTx(int parentEntityId) {
        EESimpleEntity parentEntity = em.find(EESimpleEntity.class, parentEntityId);
        EESimpleEntity entity = new EESimpleEntity("entity in supports tx, we can see parent entity " + parentEntity);
        em.persist(entity);
        return entity;
    }

    public void throwSystemException() {
        throw new RuntimeException("Some system exception thrown by EJB bean");
    }

    public void throwApplicationException() {
        throw new MyApplicationException();
    }

    public void throwApplicationExceptionWithRollback() {
        throw new MyApplicationExceptionWithRollback();
    }
}
package net.slisenko.jpa.examples.ee.ejb;

import net.slisenko.jpa.examples.ee.model.EESimpleEntity;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Stateless
public class TransactionExamplesBean {

    @PersistenceContext
    private EntityManager em;

    /**
     * YES: -
     * NOT: exception
     */
    @Transactional(Transactional.TxType.MANDATORY)
    public void txMandatory() {
        // Do write operations
        em.persist(new EESimpleEntity("mandatory"));
    }

    /**
     * YES: -
     * NOT: tx.start
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public void txRequired() {
        em.persist(new EESimpleEntity("mandatory"));
    }

    /**
     * YES: tx.start
     * NOT: tx.start
     */
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void txRequiresNew() {
        em.persist(new EESimpleEntity("requires new"));
    }

    /**
     * YES: -
     * NOT: -
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public void txSupports() {
        em.persist(new EESimpleEntity("supports"));
    }

    /**
     * YES: suspend
     * NOT: -
     */
    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public void testNotSupported() {
        em.createQuery("SELECT ee FROM EESimpleEntity ee").getResultList();
    }

    /**
     * YES: exception
     * NOT: -
     */
    @Transactional(Transactional.TxType.NEVER)
    public void txNewer() {
        em.createQuery("SELECT ee FROM EESimpleEntity ee").getResultList();
    }
}
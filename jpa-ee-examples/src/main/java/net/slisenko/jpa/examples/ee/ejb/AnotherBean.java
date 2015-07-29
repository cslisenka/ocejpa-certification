package net.slisenko.jpa.examples.ee.ejb;

import net.slisenko.jpa.examples.ee.model.Member;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Stateless
public class AnotherBean {

    @PersistenceContext
    private EntityManager em;

    public List<Member> getMembers() {
        List<Member> members = em.createQuery("SELECT m FROM Member m").getResultList();
        return members;
    }
}
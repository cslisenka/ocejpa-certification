package net.slisenko.web;

import net.slisenko.sharedlib.MyEntityClass;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class PU2Ejb {

    @PersistenceContext
    private EntityManager em;

    public MyEntityClass save() {
        MyEntityClass entity = new MyEntityClass();
        entity.setName("saved in persistence unit web2");
        em.persist(entity);
        return entity;
    }
}

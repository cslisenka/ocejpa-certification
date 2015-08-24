package net.slisenko.web;

import net.slisenko.sharedlib.MyEntityClass;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class PUEjb {

    /**
     * Persistence units in lib/***.jar file has same name as in war-file
     * By default, war-file overrides persistence unit
     * But we can access persistence-unit from jar-file using following name
     */
    @PersistenceContext(unitName = "../lib/wildfly-ejb-in-ear-shared-library.jar#pu")
    private EntityManager em;

    public MyEntityClass save() {
        MyEntityClass entity = new MyEntityClass();
        entity.setName("saved in persistence unit web!!!!!");
        em.persist(entity);
        return entity;
    }
}

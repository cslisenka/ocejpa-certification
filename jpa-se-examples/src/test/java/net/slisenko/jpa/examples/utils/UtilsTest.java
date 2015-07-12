package net.slisenko.jpa.examples.utils;

import junit.framework.Assert;
import net.slisenko.AbstractJpaTest;
import org.junit.Test;

import javax.persistence.Persistence;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.PersistenceUtil;

public class UtilsTest extends AbstractJpaTest {

    @Test
    public void testPersistenceUtil() {
        em.getTransaction().begin();
        UtilTestEntityA a = new UtilTestEntityA("aaa");
        UtilTestEntityB b = new UtilTestEntityB("bbb");
        a.setEntityB(b);
        em.persist(a);

        em.getTransaction().commit();
        em.clear();

        PersistenceUtil util = Persistence.getPersistenceUtil();
        // Test is entity loaded
        a = em.getReference(UtilTestEntityA.class, a.getId());
        System.out.println("Entity a is not loaded: " + util.isLoaded(a));
        Assert.assertFalse(util.isLoaded(a));

        a = em.find(UtilTestEntityA.class, a.getId());
        System.out.println("Entity a is loaded: " + util.isLoaded(a));
        Assert.assertTrue(util.isLoaded(a));

        System.out.println("Entity attribute 'name' is loaded: " + util.isLoaded(a, "name"));
        Assert.assertTrue(util.isLoaded(a));
    }

    @Test
    public void testPersistenceUnitUtil() {
        em.getTransaction().begin();
        UtilTestEntityA a = new UtilTestEntityA("aaa");
        em.persist(a);
        em.getTransaction().commit();

        PersistenceUnitUtil util = emf.getPersistenceUnitUtil();
        // This method can also return complex identifiers
        System.out.println("entity a identifier = " + util.getIdentifier(a));
    }
}
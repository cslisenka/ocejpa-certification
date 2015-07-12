package net.slisenko.jpa.examples.secondaryTable;

import net.slisenko.AbstractJpaTest;
import org.junit.Test;

import java.util.List;

/**
 * One entity stored in two separate database tables
 * We can use this feature for:
 * 1. For performance reasons
 * 2. When we have DB structure and Object model difference
 */
public class TestSecondaryTables extends AbstractJpaTest {

    @Test
    public void test() {
        em.getTransaction().begin();
        em.persist(new OneEntityTwoTables("a", "b"));
        em.persist(new OneEntityTwoTables("c", "d"));
        em.getTransaction().commit();
        em.clear();

        List<OneEntityTwoTables> entities = em.createQuery("FROM OneEntityTwoTables").getResultList();
        for (OneEntityTwoTables one : entities) {
            System.out.println(one);
        }
    }
}
package net.slisenko.jpa.examples.inheritance.tablePerClass;

import net.slisenko.AbstractJpaTest;
import org.junit.Test;

import java.util.List;

/**
 * Each class has separate table which all common values copied
 * We need common ID (sequence/table) for all subclasses
 * Queries for base class are not effective because they should use UNION
 * Good performance if query one entity type
 * We can apply not null constraints
 */
public class TestTablePerClass extends AbstractJpaTest {

    @Test
    public void testHieratchy() {
        em.getTransaction().begin();
        TChildA childA1 = new TChildA();
        childA1.setChildAProp("1");
        childA1.setBase("base param value");
        em.persist(childA1);

        TChildA childA2 = new TChildA();
        childA2.setChildAProp("1");
        childA2.setBase("base param value");
        em.persist(childA2);

        TChildB childB1 = new TChildB();
        childB1.setBase("base");
        childB1.setChildAProp(11);
        em.persist(childB1);

        em.getTransaction().commit();
        em.clear();

        System.out.println("Query by base type");
        List<TCBase> bases = em.createQuery("FROM TCBase b").getResultList();
        System.out.println(bases);

        System.out.println("Query by sub type");
        List<TChildA> subs = em.createQuery("FROM TChildA b").getResultList();
        System.out.println(subs);
    }
}
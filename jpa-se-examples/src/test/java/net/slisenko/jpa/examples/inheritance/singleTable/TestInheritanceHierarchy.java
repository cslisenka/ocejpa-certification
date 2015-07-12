package net.slisenko.jpa.examples.inheritance.singleTable;

import net.slisenko.AbstractJpaTest;
import org.junit.Test;

import java.util.List;

/**
 * One table created for all hierarchy.
 *
 * 1. We can not use NOT NULL constraints in child classes
 * 2. Better performance for queries (we do not need joins)
 * 3. We can query by base class
 * 4. JPA creates additional "discriminator column" where stores concrete class type for each row (column can be String, Char, Integer type)
 */
public class TestInheritanceHierarchy extends AbstractJpaTest {

    @Test
    public void testHieratchy() {
        em.getTransaction().begin();
        SingleTableSub1 sub11 = new SingleTableSub1();
        sub11.setName("sub11");
        sub11.setSub1Field("subfield 11");
        em.persist(sub11);

        SingleTableSub1 sub12 = new SingleTableSub1();
        sub12.setName("sub12");
        sub12.setSub1Field("subfield 12");
        em.persist(sub12);

        SingleTableSub2 sub2 = new SingleTableSub2();
        sub2.setName("sub2");
        sub2.setSub2(11);
        em.persist(sub2);

        em.getTransaction().commit();
        em.clear();

        // Query by base type
        System.out.println("Query by base type");
        List<SingleTableBase> bases = em.createQuery("FROM SingleTableBase b").getResultList();
        System.out.println(bases);

        // Query by subtype
        System.out.println("Query by sub type");
        List<SingleTableSub1> subs = em.createQuery("FROM SingleTableSub1 b").getResultList();
        System.out.println(subs);
    }
}
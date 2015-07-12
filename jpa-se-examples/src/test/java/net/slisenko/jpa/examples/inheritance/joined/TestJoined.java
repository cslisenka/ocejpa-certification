package net.slisenko.jpa.examples.inheritance.joined;

import net.slisenko.AbstractJpaTest;
import org.junit.Test;

import java.util.List;

/**
 * One table per each java class.
 * We can apply not null constraints
 * Queries always require joins! (Not good from performance side).
 * Better than "table per class" when we query by base entity (unions are not used).
 * Bad for hierarchies with many layers.
 * Discriminator column used to store entity type of each row.
 *
 * In current case 3 tables created: TPEBase, TPEChildA, TPEChildB
 * Entity TPEChildA stored in TPEBase + TPEChildA. When we request for TPEChildA, hibernate executes join.
 */
public class TestJoined extends AbstractJpaTest {

    @Test
    public void testHieratchy() {
        em.getTransaction().begin();
        TPEChildA childA1 = new TPEChildA();
        childA1.setChildAParam("a param");
        childA1.setBase("base param value");
        em.persist(childA1);

        TPEChildA childA2 = new TPEChildA();
        childA2.setChildAParam("a param 2");
        childA2.setBase("base param value 2");
        em.persist(childA2);

        TPEChildB childB1 = new TPEChildB();
        childB1.setBase("base");
        childB1.setChildAParam(1);
        em.persist(childB1);

        em.getTransaction().commit();
        em.clear();

        System.out.println("Query by base type");
        List<TPEBase> bases = em.createQuery("FROM TPEBase b").getResultList();
        System.out.println(bases);

        System.out.println("Query by sub type");
        List<TPEBase> subs = em.createQuery("FROM TPEChildA b").getResultList();
        System.out.println(subs);
    }
}
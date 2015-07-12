package net.slisenko.jpa.examples.relationship.oneToOne;

import net.slisenko.AbstractJpaTest;
import org.junit.Test;

public class OneToOneTest extends AbstractJpaTest {

    /**
     * One to one relationship is eager by default
     */
    @Test
    public void test() {
        em.getTransaction().begin();

        Item4 child = new Item4("main item");
        em.persist(child);

        Item3 main = new Item3("child item");
        main.setOneToOne(child);
        em.persist(main);

        em.getTransaction().commit();
        em.clear();

        Item3 founded = em.find(Item3.class, main.getId());
        System.out.format("main item -> id: %d, name: %s\n", founded.getId(), founded.getName());
        System.out.format("child item -> id: %s, name: %s\n", founded.getOneToOne().getId(), founded.getOneToOne().getName());
    }
}
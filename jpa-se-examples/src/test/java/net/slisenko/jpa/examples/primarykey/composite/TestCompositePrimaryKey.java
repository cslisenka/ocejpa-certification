package net.slisenko.jpa.examples.primarykey.composite;

import net.slisenko.AbstractJpaTest;
import org.junit.Test;

/**
 * To make composite primary key we need to have separate class for it
 */
public class TestCompositePrimaryKey extends AbstractJpaTest {

    @Test
    public void test() {
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Building").executeUpdate();
        em.persist(new Building("Minsk", "Independence av.", 10, 250000));
        em.persist(new Building("Vitebsk", "Independence av.", 10, 70000));
        em.persist(new Building("Vitebsk", "Victory park", 1, 50000));
        em.getTransaction().commit();

        em.clear();
        Building founded = em.find(Building.class, new BuildingId("Minsk", "Independence av.", 10));
        System.out.println("Founded building: " + founded);
    }

    @Test
    public void testEntityWithEmbeddedIdClass() {
        em.getTransaction().begin();
        em.createQuery("DELETE FROM BuildingEmbeddedId").executeUpdate();
        em.persist(new BuildingEmbeddedId(new BuildingId("Minsk", "Independent av.", 15), 150000));
        em.getTransaction().commit();
        em.clear();

        BuildingEmbeddedId buildingEmbeddedId = em.find(BuildingEmbeddedId.class, new BuildingId("Minsk", "Independent av.", 15));
        System.out.println(buildingEmbeddedId);
    }
}
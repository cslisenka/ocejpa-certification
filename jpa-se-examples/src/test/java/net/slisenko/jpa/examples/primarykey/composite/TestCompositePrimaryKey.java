package net.slisenko.jpa.examples.primarykey.composite;

import net.slisenko.AbstractJpaTest;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.persistence.EntityManager;

/**
 * To make composite primary key we need to have separate class for it.
 * When we want to find entity, we put primary key object in find() method
 *
 * Two tables are completely equal in database.
 */
public class TestCompositePrimaryKey extends AbstractJpaTest {

    @BeforeClass
    public static void cleanDB() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Building").executeUpdate();
        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void test() {
        em.getTransaction().begin();
        em.persist(new Building("Minsk", "Independence av.", 10, 250000));
        em.persist(new Building("Vitebsk", "Independence av.", 10, 70000));
        em.persist(new Building("Vitebsk", "Victory park", 1, 50000));
        em.getTransaction().commit();
        em.clear();

        Building founded = em.find(Building.class, new BuildingId("Minsk", "Independence av.", 10));
        p("Founded building: " + founded);
    }

    @Test
    public void testEntityWithEmbeddedIdClass() {
        em.getTransaction().begin();
        em.persist(new BuildingEmbeddedId(new BuildingId("Minsk", "Independent av.", 15), 150000));
        em.getTransaction().commit();
        em.clear();

        BuildingEmbeddedId buildingEmbeddedId = em.find(BuildingEmbeddedId.class,
                new BuildingId("Minsk", "Independent av.", 15));
        p("Founded building: " + buildingEmbeddedId);
    }
}
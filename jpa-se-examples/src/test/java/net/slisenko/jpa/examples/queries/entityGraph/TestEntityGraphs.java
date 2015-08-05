package net.slisenko.jpa.examples.queries.entityGraph;

import junit.framework.Assert;
import net.slisenko.AbstractJpaTest;
import net.slisenko.jpa.examples.queries.House;
import net.slisenko.jpa.examples.queries.Street;
import org.hibernate.LazyInitializationException;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.persistence.*;

/**
 * Entity graph is a fetch plan for query. We can define LAZY relationships, but in some cases we need to eagerly load them.
 * For example when we make s query and them pass entities to web tier and they are detached.
 */
public class TestEntityGraphs extends AbstractJpaTest {

    private static House h;
    private static Street s;

    @BeforeClass
    public static void prepareData() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        s = new Street("lazy street in entity graph");
        em.persist(s);
        h = new House("house in entity graph");
        h.setStreet(s);
        em.persist(h);
        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void testEntityGraphAnnotations() {
        // Get house without entity graph
        EntityManager em2 = emf.createEntityManager();
        House house = em2.createQuery("SELECT h FROM House h WHERE h.id=:id", House.class)
                .setParameter("id", h.getId()).getSingleResult();
        em2.close();

        System.out.println(house);
        try {
            System.out.println(house.getStreet());
            Assert.fail("No Lazy initialization exception");
        } catch (LazyInitializationException e) {
        }

        em2 = emf.createEntityManager();
        house = em2.createQuery("SELECT h FROM House h WHERE h.id=:id", House.class).setParameter("id", h.getId())
            .setHint("javax.persistence.fetchgraph", em2.getEntityGraph("HouseWithStreet")).getSingleResult();
        em2.close();

        System.out.println(house);
        System.out.println(house.getStreet());
    }

    @Test
    public void testEntityGraphAPI() {
        EntityGraph<House> graphHouse = em.createEntityGraph(House.class);
        graphHouse.addAttributeNodes("price", "name", "floors");
        Subgraph<Street> graphStreet = graphHouse.addSubgraph("street");
        graphStreet.addAttributeNodes("name");

        EntityManager em2 = emf.createEntityManager();
        em2 = emf.createEntityManager();
        House house = em2.createQuery("SELECT h FROM House h WHERE h.id=:id", House.class).setParameter("id", h.getId())
                .setHint("javax.persistence.fetchgraph", graphHouse).getSingleResult();
        em2.close();
        System.out.println(house);
        System.out.println(house.getStreet());
    }
}
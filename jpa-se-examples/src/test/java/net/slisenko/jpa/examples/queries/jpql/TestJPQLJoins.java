package net.slisenko.jpa.examples.queries.jpql;

import net.slisenko.jpa.examples.queries.House;
import net.slisenko.jpa.examples.queries.Street;
import org.junit.Test;

import java.util.List;

public class TestJPQLJoins extends BaseJPQLTest {

    @Test
    public void testJoin() {
        System.out.println("Test joins");
        em.getTransaction().begin();
        List<House> houses = em.createQuery("SELECT DISTINCT h FROM House h JOIN h.street s JOIN s.city c WHERE c.name = :name")
                .setParameter("name", "city1").getResultList();

        System.out.println(houses);
        em.getTransaction().commit();
    }

    /**
     * Get all city service numbers
     */
    @Test
    public void testMapJoin() {
        List<Object[]> results = em.createQuery("SELECT DISTINCT c.name, KEY(services), VALUE(services) FROM City c JOIN c.cityServicesNumbers services WHERE KEY(services) in ('101', '102')")
                .getResultList();
        for (Object[] row : results) {
            System.out.format("%s, %s, %s \n", row[0], row[1], row[2]);
        }
    }

    /**
     * By default JPA uses inner joins. If we query for House join Street, we will get only houses which have relationship to streets.
     *
     * We can use LEFT OUTER JOIN to have all houses: which have streets or not.
     */
    @Test
    public void testOuterJoins() {
        // Use default inner join
        System.out.println("Default inner join");
        List<Object[]> results = em.createQuery("SELECT h.name, s.name FROM House h JOIN h.street s").getResultList();
        for (Object[] row : results) {
            System.out.format("%s, %s \n", row[0], row[1]);
        }

        System.out.println("Left outer join");
        // Use left outer join
        results = em.createQuery("SELECT h.name, s.name FROM House h LEFT JOIN h.street s").getResultList();
        for (Object[] row : results) {
            System.out.format("%s, %s \n", row[0], row[1]);
        }
    }

    /**
     * Fetch join allows to extract all entities from database which are lazy loaded by default.
     * It helps when we get entities, close entity manager, and then move them to web application layer.
     */
    @Test
    public void testFetchJoin() {
        // Regular join without fetch
        System.out.println("Select streets using regular join");
        List<Street> streets =  em.createQuery("SELECT s FROM Street s JOIN s.city c").getResultList();
        // When we want to get relationship in join, Hibernate will do another query to database (because relationship is lazy)
        System.out.println(streets.get(0).getCity());

        // Join with fetch
        System.out.println("Select streets using fetch join with house");
        streets =  em.createQuery("SELECT s FROM Street s JOIN FETCH s.city c").getResultList();
        // No another query required because we used fetch join
        System.out.println(streets.get(0).getCity());
    }
}

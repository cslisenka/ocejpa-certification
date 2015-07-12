package net.slisenko.jpa.examples.queries.jpql;

import net.slisenko.jpa.examples.queries.House;
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

    @Test
    public void testMapJoin() {
        // TODO test map joins "SELECT e.name, KEY(p), VALUE(p) FROM Employee e JOIN e.phones p" where e.phones is map
    }

    @Test
    public void testOuterJoins() {
        // TODO outer joins
    }

    @Test
    public void testFetchJoin() {
        // TODO fetch join
    }
}

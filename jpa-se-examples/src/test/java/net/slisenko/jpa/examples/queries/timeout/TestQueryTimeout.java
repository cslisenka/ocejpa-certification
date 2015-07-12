package net.slisenko.jpa.examples.queries.timeout;

import net.slisenko.AbstractJpaTest;
import net.slisenko.jpa.examples.queries.City;
import org.junit.Test;

import java.util.Date;
import java.util.List;

/**
 * Query timeout is not a portable behaviour. It can be not supported by some databases.
 *
 * It doesn't work with MySQL and Hibernate
 */
public class TestQueryTimeout extends AbstractJpaTest {

    @Test
    public void testTimeout() {
        // Insert a lot of data
        em.getTransaction().begin();
        for (int i = 0; i < 1000; i++) {
            em.persist(new City("city " + i));
        }
        em.getTransaction().commit();
        em.clear();

        long timeBefore = new Date().getTime();
        List<City> results = em.createQuery("FROM City")
                .setHint("javax.persistence.query.timeout", 100).getResultList();
        // Query hints = extensions to queries (can be vendor-specific)
        for (int i = 0; i < 10; i++) {
            System.out.println(results.get(i));
        }
        System.out.println("Query time = " + (new Date().getTime() - timeBefore));
    }
}
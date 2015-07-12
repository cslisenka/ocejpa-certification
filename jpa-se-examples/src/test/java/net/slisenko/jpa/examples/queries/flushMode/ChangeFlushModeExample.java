package net.slisenko.jpa.examples.queries.flushMode;

import net.slisenko.AbstractJpaTest;
import net.slisenko.jpa.examples.queries.City;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.FlushModeType;
import java.util.List;

/**
 * If you change data in transaction and then query this data, persistence provider can return different results
 * depending on FLUSH MODE:
 * AUTO - all changes will be visible in query results - better data integrity. DEFAULT VALUE.
 * COMMIT - changes will not be visible until tx.commit() - better performance
 */
public class ChangeFlushModeExample extends AbstractJpaTest {

    @Before
    public void before() {
        em.getTransaction().begin();
        em.createQuery("DELETE FROM House h").executeUpdate();
        em.createQuery("DELETE FROM Street s").executeUpdate();
        em.createQuery("DELETE FROM City s").executeUpdate();
        em.getTransaction().commit();
    }

    @Test
    public void testCommitFlushMode() {
        em.getTransaction().begin();
        em.persist(new City("Minsk"));
        em.persist(new City("Vitebsk"));
        em.getTransaction().commit();

        makeChangesAndQueryThemAgain(FlushModeType.AUTO);
        makeChangesAndQueryThemAgain(FlushModeType.COMMIT);
    }

    void makeChangesAndQueryThemAgain(FlushModeType flushMode) {
        System.out.format("===== Use %s mode =====\n", flushMode.name());
        em.setFlushMode(flushMode);
        em.getTransaction().begin();
        // Query this data
        List<City> results = em.createQuery("FROM City").getResultList();
        System.out.println("Cities before modification");
        System.out.println(results);

        for (City city : results) {
            city.setName(city.getName() + "renamed");}

        System.out.println("Cities after modification");
        results = em.createQuery("FROM City").getResultList();
        System.out.println(results);

        em.getTransaction().rollback();
    }
}
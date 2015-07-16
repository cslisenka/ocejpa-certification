package net.slisenko;

import org.hibernate.jpa.internal.EntityManagerFactoryImpl;
import org.junit.After;
import org.junit.Before;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class AbstractJpaTest {

    public static final String CACHE = "testSecondLevelCache";
    public static final String POSTRGES = "testJpaPostgres";
    public static final String MYSQL = "testJpaMySQL";

    /**
     * Should be one for each database.
     * Heavy object, contains connections, objects cache, configuration parameters.
     * 2-nd level cache.
     */
    protected EntityManagerFactory emf;

    /**
     * Lightweight object which created one time per database interaction session.
     * We can create many instances of EntityManager.
     * We should close EntityManager after finish some actions with database.
     * Has 1 entity transaction.
     * Has small objects cache. (1-st level cache)
     */
    protected EntityManager em;

    @Before
    public void setUp() {
        // Create entity manager factory from persistence unit
        emf = Persistence.createEntityManagerFactory(CACHE);
        // Create entity manager
        em = emf.createEntityManager();
    }

    @After
    public void tearDown() {
        // Print hibernate statistics
        EntityManagerFactoryImpl empImpl = (EntityManagerFactoryImpl) emf;
        System.out.println(empImpl.getSessionFactory().getStatistics());

        if (em.isOpen()) {
            em.close();
        }

        if (emf.isOpen()) {
            emf.close();
        }
    }
}
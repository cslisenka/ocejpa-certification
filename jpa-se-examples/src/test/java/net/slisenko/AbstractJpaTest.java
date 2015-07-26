package net.slisenko;

import org.hibernate.jpa.internal.EntityManagerFactoryImpl;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

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
    protected static EntityManagerFactory emf;

    /**
     * Lightweight object which created one time per database interaction session.
     * We can create many instances of EntityManager.
     * We should close EntityManager after finish some actions with database.
     * Has 1 entity transaction.
     * Has small objects cache. (1-st level cache)
     */
    protected EntityManager em;

    @BeforeClass
    public static void prepareEntityManagerFactory() {
        // Create entity manager factory from persistence unit
        emf = Persistence.createEntityManagerFactory(CACHE);
    }

    @Before
    public void prepareEntityManager() {
        // Create entity manager
        em = emf.createEntityManager();
    }

    @After
    public void printStatisticAndCloseEntityManager() {
        // Print hibernate statistics
        System.out.println("========== Hibernate statistics ==========");
        EntityManagerFactoryImpl empImpl = (EntityManagerFactoryImpl) emf;
        System.out.println(empImpl.getSessionFactory().getStatistics().toString().replace(",", ",\n"));
        System.out.println("========== ==========");

        if (em.isOpen()) {
            em.close();
        }
    }

    @AfterClass
    public static void closeEntityManagerFactory() {
        if (emf.isOpen()) {
            emf.close();
        }
    }
}
package net.slisenko.jpa.examples.lifecycle.locking;

import org.junit.After;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class AbstractLockingTest {

    public static final String MYSQL = "testJpaMySQL";
    public static final String POSTGRES = "testJpaPostgres";

    protected EntityManagerFactory emf;

    public void setUp(Map<String, String> settings) {
        emf = Persistence.createEntityManagerFactory(MYSQL, settings);
    }

    public void setUp(int txIsolationLevel) {
        Map<String, String> settings = new HashMap<>();
        settings.put("hibernate.connection.isolation", txIsolationLevel + "");
        setUp(settings);
    }

    public void clearDB() {
        // Delete old data
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM NonVersionedEntity").executeUpdate();
            em.createQuery("DELETE FROM VersionedEntity").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @After
    public void tearDown() {
        emf.close();
    }
}

package net.slisenko.jpa.examples.caching;

import junit.framework.Assert;
import net.slisenko.AbstractJpaTest;
import net.slisenko.Identity;
import org.junit.After;
import org.junit.Before;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

public class BaseCacheTest extends AbstractJpaTest {

    protected static List<CachedEntity> entities = new ArrayList<>();

    @Before
    public void initData() {
        EntityManager em = emf.createEntityManager();
        cleanAll(em);
        insertCachedEntities(em);
        em.close();
    }

    @After
    public void evictCache() {
        emf.getCache().evictAll();
    }

    public static <T extends Identity> void assertCached(List<T> entities) {
        for (T oneEntity : entities) {
            assertCached(oneEntity);
        }
    }

    public static <T extends Identity> void assertCached(T entity) {
        Assert.assertTrue(emf.getCache().contains(entity.getClass(), entity.getId()));
    }

    public static <T> void assertNotCached(T entity) {
        Assert.assertFalse(emf.getCache().contains(entity.getClass(), entity));
    }

    static void p(Object text) {
        System.out.println(text);
    }

    public static void cleanAll(EntityManager em) {
        em.getTransaction().begin();
        em.createQuery("DELETE FROM CachedEntity c").executeUpdate();
        em.createQuery("DELETE FROM CachedEntityRelationship c").executeUpdate();
        em.getTransaction().commit();
        em.clear();
    }

    public static void insertCachedEntities(EntityManager em) {
        entities.clear();
        em.getTransaction().begin();
        // Create 20 CachedEntity objects
        for (int i = 0; i < 20; i++) {
            CachedEntity cachedEntity = new CachedEntity(String.format("cached entity %d", i));
            entities.add(cachedEntity);
            em.persist(cachedEntity);
        }
        em.getTransaction().commit();
        em.clear();
    }
}

package net.slisenko.jpa.examples.caching;

import junit.framework.Assert;
import net.slisenko.AbstractJpaTest;
import net.slisenko.Identity;
import net.slisenko.jpa.examples.caching.model.CachedEntity;
import org.junit.After;
import org.junit.Before;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BaseCacheTest extends AbstractJpaTest {

    protected static List<CachedEntity> entities = new ArrayList<>();

//    @Before
//    public void initData() {
//        EntityManager em = emf.createEntityManager();
//        cleanAll(em);
//        insertCachedEntities(em);
//        em.close();
//    }

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

    public static void cleanAll(EntityManager em) {
        em.getTransaction().begin();
        List<CachedEntity> ces = em.createQuery("SELECT ce FROM CachedEntity ce", CachedEntity.class).getResultList();
        for (CachedEntity c : ces) {
            em.remove(c);
        }

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

    public <T extends Identity> T persistAndLoadToCache(T entity) {
        p("========== Persist entity ==========");
        em.getTransaction().begin();
        em.persist(entity);
        em.getTransaction().commit();
        em.clear();
        assertNotCached(entity);

        p("========== Request entity and populate cache ==========");
        Identity founded = em.find(entity.getClass(), entity.getId());
        assertCached(founded);
        em.clear();
        return (T) founded;
    }

    public void updateEntity(Identity entity) {
        p("========== Update entity ==========");
        em.getTransaction().begin();
        Identity founded = em.find(entity.getClass(), entity.getId());
        founded.setName(new Date().getTime() + "");
        em.getTransaction().commit();
        em.clear();
    }

    public void queryEntityManyTimes(Identity entity, boolean isCreateEntityManagerForEachRequest) {
        EntityManager myEm = em;
        for (int i = 0; i < 10; i++) {
            p("========== Query entity " + (isCreateEntityManagerForEachRequest ? "with new entity manager" : "") + "==========");
            if (isCreateEntityManagerForEachRequest) {
                myEm = emf.createEntityManager();
            }

            myEm.find(entity.getClass(), entity.getId());

            if (isCreateEntityManagerForEachRequest) {
                myEm.close();
            } else {
                myEm.clear();
            }
        }
    }
}

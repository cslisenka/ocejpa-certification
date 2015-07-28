package net.slisenko.jpa.examples.caching;

import junit.framework.Assert;
import net.slisenko.AbstractJpaTest;
import net.slisenko.Identity;
import org.junit.After;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.List;

public class BaseCacheTest extends AbstractJpaTest {

    @After
    public void evictCache() {
        emf.getCache().evictAll();
    }

    public static <T extends Identity> void assertNotCached(List<T> entities) {
        for (T oneEntity : entities) {
            assertNotCached(oneEntity);
        }
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

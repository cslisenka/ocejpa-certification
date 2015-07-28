package net.slisenko.jpa.examples.caching;

import net.slisenko.jpa.examples.caching.model.CachedEntity;
import org.hibernate.annotations.Cache;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Query cache problems:
 *      If query for entities - only their IDs are stored. Then hibernate goes to entity cache and finds objects by ids.
 *      If query for simple types - values are stored
 *
 * Query cache stores [parameters,query]->values
 *      Query cache makes sense only when we execute same query many times
 */
public class TestQueryCache extends BaseCacheTest {

    static List<CachedEntity> entities = new ArrayList<>();

    @BeforeClass
    public static void createEntitiesAndPopulate() {
        EntityManager em = emf.createEntityManager();
        p("========== Clean database ==========");
        em.getTransaction().begin();
        List<CachedEntity> all = em.createQuery("SELECT e FROM CachedEntity e").getResultList();
        for (CachedEntity e : all) {
            em.remove(e);
        }
        em.getTransaction().commit();


        p("========== Persist entities ==========");
        em.getTransaction().begin();
        for (int i = 0; i < 10; i++) {
            CachedEntity entity = new CachedEntity("cached entity " + i);
            em.persist(entity);
            entities.add(entity);
        }
        em.getTransaction().commit();
        em.clear();

        p("========== Populate cache ==========");
        for (CachedEntity entity : entities) {
            em.find(entity.getClass(), entity.getId());
        }

        em.close();
    }

    @Test
    public void testQueryCache() {
        p("========== Get entity using query - SQL works (only single entities are cached) ==========");
        List<CachedEntity> results = em.createQuery("SELECT c FROM CachedEntity c WHERE c.name LIKE 'cached entity%'", CachedEntity.class).getResultList();
        em.clear();
        assertNotCached(results);

        p("========== Get entity using query with cache hint (query results will be cached) ==========");
        results = em.createQuery("SELECT c FROM CachedEntity c WHERE c.name LIKE 'cached entity%'", CachedEntity.class)
                .setHint("org.hibernate.cacheable", true).getResultList();
        em.clear();
        assertCached(results);
        // TODO doesn't work for me
//        q.setHint("javax.persistence.cache.retrieveMode", CacheRetrieveMode.USE);

        for (int i = 0; i < 3; i++) {
            p("========== Get data from query cache, no SQL should happen ==========");
            em.createQuery("SELECT c FROM CachedEntity c WHERE c.name LIKE 'cached entity%'", CachedEntity.class)
                    .setHint("org.hibernate.cacheable", true).getResultList();
            em.clear();
        }
    }

    /**
     * This test shows query cache problem: if entities are expired in entity cache, but query is not - Hibernate
     * sends lots of SQL queries to retrive each entity separately!
     */
    @Test
    public void testQueryCacheWhenEntityCacheIsEmpty() {
        p("========== Get entity using query with cache hint (query results will be cached) ==========");
        em.createQuery("SELECT c FROM CachedEntity c WHERE c.name LIKE 'cached entity%'", CachedEntity.class)
            .setHint("org.hibernate.cacheable", true).getResultList();
        em.clear();

        p("========== Evict all cached entities ==========");
        emf.getCache().evictAll();
        assertNotCached(entities);

        // We use query cache, but all entities are evicted, and Hibernate sends 20 SQL queryes for each entity!
        p("========== Get data from query cache, LOTS OF SQL FOR ENTITIES HAPPENS! ==========");
        for (int i = 0; i < 3; i++) {
            em.createQuery("SELECT c FROM CachedEntity c WHERE c.name LIKE 'cached entity%'", CachedEntity.class)
                .setHint("org.hibernate.cacheable", true).getResultList();
            em.clear();
        }
        p("====================");

        // We do not use query cache, but there is only 1 query instead of 20!
        p("========== Query entities, not use query cache: we have single SQL query for all entities ==========");
        em.createQuery("SELECT c FROM CachedEntity c WHERE c.name LIKE 'cached entity%'", CachedEntity.class)
            .getResultList();
        em.clear();
    }

    /**
     * Query Cache can invalidate its entries whenever the associated table space changes.
     * Every time we persist/remove/update an Entity, all Query Cache entries using that particular table will get invalidated.
     */
    @Test
    public void testQueryCacheInvalidation() {
        p("========== Get entity using query with cache hint (query results will be cached) ==========");
        em.createQuery("SELECT c FROM CachedEntity c WHERE c.name LIKE 'cached entity%'", CachedEntity.class)
                .setHint("org.hibernate.cacheable", true).getResultList();
        em.clear();

        for (int i = 0; i < 3; i++) {
            p("========== Get data from query cache ==========");
            em.createQuery("SELECT c FROM CachedEntity c WHERE c.name LIKE 'cached entity%'", CachedEntity.class)
                    .setHint("org.hibernate.cacheable", true).getResultList();
            em.clear();
        }
        p("====================");

        // Update one of entity
        p("========== Update one entity ==========");
        em.getTransaction().begin();
        CachedEntity entity = em.find(CachedEntity.class, entities.get(0).getId());
        entity.setName("changed");
        em.getTransaction().commit();
        em.clear();

        p("========== Get entity using query with cache hint after we changed entity, query cache was invalidated! ==========");
        em.createQuery("SELECT c FROM CachedEntity c WHERE c.name LIKE 'cached entity%'", CachedEntity.class)
                .setHint("org.hibernate.cacheable", true).getResultList();
        em.clear();
    }
}
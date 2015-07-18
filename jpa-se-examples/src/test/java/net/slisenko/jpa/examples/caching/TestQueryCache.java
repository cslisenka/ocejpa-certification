package net.slisenko.jpa.examples.caching;

import net.slisenko.jpa.examples.caching.model.CachedEntity;
import org.junit.Test;

import java.util.List;

/**
 * Query cache problems:
 *      If query for entities - only their IDs are stored.
 * Кеш запросов возврашает только id-шники, если в кеше объектов этих сущностей нет, то в базу идёт куча запросов на ID (TODO проверить)
 * Кеш запросов сохраняет возвращаемые значения по запросу+параметрам, его имеет смысл включать только если у нас выполняется один и тот же запрос постоянно. Если постоянно выполняются
 * разные запросы, то кеш никакого эффекта не даст.
 * TODO вытянуть одну и ту же сущность по имени и по другому параметру. Убедиться что мы промазываем мимо кеша.
 */
public class TestQueryCache extends BaseCacheTest {

    List<CachedEntity> result = null;

    @Test
    public void testQueryCache() {
        p("========== Populate entities cache ==========");
        for (CachedEntity entity : entities) {
            CachedEntity e = em.find(CachedEntity.class, entity.getId());
            assertCached(e);
        }

        p("========== Get entity using query - SQL works (only single entities are cached) ==========");
        em.createQuery("SELECT c FROM CachedEntity c WHERE c.name LIKE 'cached entity%'", CachedEntity.class)
            .getResultList();
        em.clear();

        p("========== Get entity using query with cache hint (query results will be cached) ==========");
        em.createQuery("SELECT c FROM CachedEntity c WHERE c.name LIKE 'cached entity%'", CachedEntity.class)
                .setHint("org.hibernate.cacheable", true).getResultList();
        em.clear();
        // TODO doesn't work for me
//        q.setHint("javax.persistence.cache.retrieveMode", CacheRetrieveMode.USE);

        p("========== Get data from query cache, no SQL should happen ==========");
        for (int i = 0; i < 3; i++) {
            em.createQuery("SELECT c FROM CachedEntity c WHERE c.name LIKE 'cached entity%'", CachedEntity.class)
                    .setHint("org.hibernate.cacheable", true).getResultList();
            em.clear();
        }
        p("====================");
    }

    /**
     * This test shows query cache problem: if entities are expired in entity cache, but query is not - Hibernate
     * sends lots of SQL queries to retrive each entity separately!
     */
    @Test
    public void testQueryCacheWhenEntityCacheIsEmpty() {
        p("========== Populate entities cache ==========");
        for (CachedEntity entity : entities) {
            CachedEntity e = em.find(CachedEntity.class, entity.getId());
            assertCached(e);
        }

        p("========== Get entity using query with cache hint (query results will be cached) ==========");
        em.createQuery("SELECT c FROM CachedEntity c WHERE c.name LIKE 'cached entity%'", CachedEntity.class)
            .setHint("org.hibernate.cacheable", true).getResultList();
        em.clear();
        // TODO doesn't work for me
//        q.setHint("javax.persistence.cache.retrieveMode", CacheRetrieveMode.USE);

        p("========== Evict all cached entities ==========");
        emf.getCache().evict(CachedEntity.class);

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
        p("====================");
    }
}
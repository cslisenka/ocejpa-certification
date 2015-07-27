package net.slisenko.jpa.examples.caching;

import net.slisenko.jpa.examples.caching.model.CachedEntity;
import net.slisenko.jpa.examples.caching.model.CachedEntityRelationship;
import org.junit.Test;

/**
 * JPA cache structure:
 *      1-st lvl: EntityManager
 *      2-nd level: EntityManagerFactory
 *          - external cache provider (EHCache, ...), can be local or clustered
 *          - shared between entity managers
 *      JDBC caching (transparently for programmer)
 *
 * Types of caches:
 *      Entity cache - caches hydrated entities (only attributes), constructs entity objects for each request
 *          [id -> entity]
 *          relationships are not stored by default, we need to specify their caching explicitly, if so, cache only stores relationships ids
 *          [id -> entity,relationships{1, 2, 3}]
 *      Query cache - caches query results
 *          [query,parameters -> query results]
 *          only stores IDs of entities, they are resolved using Entity cache, primitive types stored as they are
 *          query cache is effective only when we execute one query many times with same parameters (because key is query+parameters)
 *
 * Different kinds of data need different cache policies (or not cache at all). That's why 2-nd level cache
 * configuration can be specified for each entity separately.
 *
 * Cache use cases:
 *      + Read-only data, immutable data (not updated), reference books
 *      - Data which often updated, data which needed to be always actual
 *
 * Caching problems:
 *      - If another application changes data in database, we can not use 2-nd level cache because we will get stale data in cache.
 *          (we need to set up expiration policies for data if this is allowed by requirements)
*       - Entity cache expired, but query cache isn't. When we execute queries, hibernate sends series of queries to get
 *          single entity to database.
 *          (expiration time for query cache should be less than entity cache)
 *      - В случае иерархий применяются настройки стратегии только указанной на базовой сушности
 *      - Неверно настроенная стратегия кеша может ослабить уровень изоляции в БД (NONSTRICT READ WRITE сделает не выше read commited (ато иногда и dirty read), READ_WRITE - сделает не выше repeatable read)
 *
 * Some cache implementations
 *      - EhCache - single JVM, caching on disk or in memory
 *      - JBossCache - supports TRANSACTION strategy, full-power enterprise cache
 *
 * If we cache on disk, entities must be Serializable.
 *
 * Caching problems:
 *      http://planet.jboss.org/post/collection_caching_in_the_hibernate_second_level_cache
 *      http://www.javalobby.org/java/forums/t48846.html
 *      http://docs.jboss.org/hibernate/orm/3.5/reference/en/html/performance.html
 */
public class TestCache extends BaseCacheTest {

    @Test
    public void testCacheSimpleEntity() {
        CachedEntity entity = persistAndLoadToCache(new CachedEntity());

        // After we populated cache, hibernate always goes to cache.
        // Doesn't matter new session or old.
        queryEntityManyTimes(entity, false);
        queryEntityManyTimes(entity, true);
    }

    /**
     * One to one relationship
     *      Foreign keys are in entity objects directly, that's enough to set @Cache on both entities, not on relationship
     *      Eager loading - cache works
     *      Lazy loading - caching works only after we force query entity
     */
    @Test
    public void testLazyEagerOneToOneRelationships() {
        // Prepare cached entity with relationship
        em.getTransaction().begin();
        CachedEntity entity = new CachedEntity("entity");
        entity.setRelationshipEager(new CachedEntityRelationship("eager", entity));
        entity.setRelationshipLazy(new CachedEntityRelationship("lazy", entity));
        em.persist(entity);
        em.getTransaction().commit();
        em.clear();

        p("========== Get entity with relationship first time ==========");
        entity = em.find(CachedEntity.class, entity.getId());
        p(entity);
        p("========== Get relationship ==========");
        // If relationship is lazy, we need to force it querying. After that relationship will be in cache.
        p(entity.getRelationshipLazy());
        em.clear();

        p("========== Get entity with relationship second time (no SQL works) ==========");
        entity = em.find(CachedEntity.class, entity.getId());
        p(entity);
        p(entity.getRelationshipLazy());
        p(entity.getRelationshipEager());

        assertCached(entity);
        assertCached(entity.getRelationshipEager());
        // TODO got exception "Unknown entity: net.slisenko.jpa.examples.caching.model.CachedEntityRelationship_$$_jvstb_10" WTF?
//        assertCached(entity.getRelationshipLazy());
    }

    /**
     * Если используется join-таблица, то кеширование не заработало. TODO понять почему
     * Если связь на стороне другой сущности, то на коллекцию нужно ставить @Cached. Тогда кеш сохраняет список ID-шников.
     */
    @Test
    public void testCollectionRelationship() {
        em.getTransaction().begin();
        CachedEntity c = new CachedEntity("entity");
        em.persist(c);
        em.persist(new CachedEntityRelationship("rel1", c));
        em.persist(new CachedEntityRelationship("rel2", c));
        em.persist(new CachedEntityRelationship("rel3", c));
        em.getTransaction().commit();
        em.clear();

        p("========== Get entity first time ==========");
        c = em.find(CachedEntity.class, c.getId());
        p("========== Get relationship list first time ==========");
        p(c.getListBack());
        assertCached(c);
        assertCached(c.getListBack());
        em.clear();

        p("========== Get entity second time (cache usage, no SQL executed) ==========");
        CachedEntity e = em.find(CachedEntity.class, c.getId());
        p(e.getListBack());
    }
}
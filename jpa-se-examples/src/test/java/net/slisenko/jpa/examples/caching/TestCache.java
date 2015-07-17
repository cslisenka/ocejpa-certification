package net.slisenko.jpa.examples.caching;

import net.slisenko.AbstractJpaTest;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.persistence.CacheRetrieveMode;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA cache structure:
 *      1-st lvl: EntityManager
 *      2-nd level: EntityManagerFactory
 *          - external cache provider (EHCache, ...), can be local or clustered
 *          - shared between entity managers
 *      JDBC caching (transparently for programmer)
 *
 * Types of caches:
 *      Entity cache - caches entity attributes (not entity objects!), constructs entity objects for each request
 *          [id -> entity]
 *          relationships are not stored by default, we need to specify their caching explicitly, if so, cache only
 *          stores relationships ids
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
 *
 * Параметры кеша 2:
 * 1. Стратегия параллелизма - как изолированы транзакции
 *      - транзакционная - полная изоляция транзакций (использовать лучше для в основном считывающихся данных, но который должны быть up to date)
 *      - чтение-запись - использует механизм временных меток, работает как предыдущея, не применяется в кластерных средах
 *      - нестрогое чтение-запись - нет гарантии согласованности кеша и БД, нужно настраивать таймаут нахождения записи в кеше, иначе будут считываться устаревшие данные, применять для редкоменяющихся данных
 *      - только для чтения - данные никогда не меняются
 *
 * 2. Поставщик кеша - какую внешнюю имплементацию взять
 *      - EhCache - одна JVM, кеширование а памяти или на диске
 *      - OpenSymphony OS Cache - одна JVM, а памяти или на диске, больше политик истечения + кеш запросов
 *      - SwarmCache - кластерный кеш
 *      - JBossCache - транзакционно-репликационный кластерный кеш
 *
 * Проблемы кеширования:
 * http://planet.jboss.org/post/collection_caching_in_the_hibernate_second_level_cache
 * http://www.javalobby.org/java/forums/t48846.html
 * http://docs.jboss.org/hibernate/orm/3.5/reference/en/html/performance.html
 *
 * Кеш запросов возврашает только id-шники, если в кеше объектов этих сущностей нет, то в базу идёт куча запросов на ID (TODO проверить)
 * Кеш запросов сохраняет возвращаемые значения по запросу+параметрам, его имеет смысл включать только если у нас выполняется один и тот же запрос постоянно. Если постоянно выполняются
 * разные запросы, то кеш никакого эффекта не даст.
 * TODO вытянуть одну и ту же сущность по имени и по другому параметру. Убедиться что мы промазываем мимо кеша.
 * TODO провешировать сущность, потом её модифицировать, и опять считать. Посмотреть что будет.
 */
public class TestCache extends AbstractJpaTest {

    protected static List<CachedEntity> entities = new ArrayList<>();

    @BeforeClass
    public static void initData() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.createQuery("DELETE FROM CachedEntity c").executeUpdate();
        em.getTransaction().commit();

        em.getTransaction().begin();
        // Create 20 CachedEntity objects
        for (int i = 0; i < 20; i++) {
            CachedEntity cachedEntity = new CachedEntity(String.format("cached entity %d", i));
            entities.add(cachedEntity);
            em.persist(cachedEntity);
        }
        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void testCacheSimpleEntity() {
        // Start query entities
        System.out.println("========== Get entity first time, SQL works and populates cache ==========");
        em.find(CachedEntity.class, entities.get(0).getId());

        System.out.println("========== Get entity second time, no SQL query should happen ==========");
        for (int i = 0; i < 10; i++) {
            em.find(CachedEntity.class, entities.get(0).getId());
            em.clear();
        }

        // Try get entity using second entity manager without SQL to database
        System.out.println("========== Get entity using another EntityManager - no SQL query happen, data retrieved from cache ==========");
        EntityManager em2 = emf.createEntityManager();
        em2.find(CachedEntity.class, entities.get(0).getId());
        em2.close();
        System.out.println("====================");
    }

    /**
     * Связь к одному
     *      Если relationship=eager, то обе сущности должны быть @Cache, иначе никакого кеша не будет (связь @Cache можно не помечать)
     *      Если relationship=lazy, то кешируется только основная сущность, если мы обращаемся по связи - то идёт запрос в базу.
     *      TODO кешировать LAZY не получилось, нужно почитать разобраться
     */
    @Test
    public void testSingleRelationship() {
        // Prepare cached entity with relationship
        em.getTransaction().begin();
        CachedEntity cachedEntity = new CachedEntity("entity");
        CachedEntityRelationship relationship = new CachedEntityRelationship("relationship");
//        cachedEntity.setRelationshipEager(relationship);
        cachedEntity.setRelationshipLazy(relationship);
        em.persist(cachedEntity);
        em.getTransaction().commit();
        em.clear();

        System.out.println("========== Get entity with relationship first time ==========");
        em.find(CachedEntity.class, cachedEntity.getId());
        // TODO перейти по LAZY-связи чтобы она прокешировалась! потом проверить что хибернейт достаёт её из кеша
        em.clear();

        System.out.println("========== Get entity with relationship second time (no SQL works) ==========");
        CachedEntity entity = em.find(CachedEntity.class, cachedEntity.getId());
        System.out.println(entity);
        System.out.println("========== Get relationship ==========");
//        System.out.println(entity.getRelationshipLazy());
//        System.out.println(entity.getRelationshipEager());
        System.out.println("========== ==========");
    }

    /**
     * Тут что lazy что eager - кеширует только саму сущность. За связями идёт в базу. TODO разобраться почему
     */
    @Test
    public void testCollectionRelationship() {
        em.getTransaction().begin();
        CachedEntity c = new CachedEntity("entity");
        c.getList().add(new CachedEntityRelationship("rel1"));
        c.getList().add(new CachedEntityRelationship("rel2"));
        c.getList().add(new CachedEntityRelationship("rel3"));
        em.persist(c);
        em.getTransaction().commit();
        em.clear();

        System.out.println("========== Get entity first time ==========");
        em.find(CachedEntity.class, c.getId());
        em.clear();

        System.out.println("========== Get entity second time (cache usage, no SQL executed) ==========");
        CachedEntity e = em.find(CachedEntity.class, c.getId());
        System.out.println("========== Get entity relationships ==========");
        System.out.println(e.getList());
    }

    @Test
    public void testQueryCache() {
        System.out.println("========== Populate entities cache ==========");
        for (int i = 0; i < 10; i++) {
            em.find(CachedEntity.class, entities.get(0).getId());
        }

        System.out.println("========== Get entity using find() - no SQL happens ==========");
        em.find(CachedEntity.class, entities.get(0).getId());

        System.out.println("========== Get entity using query - SQL works (only single entities are cached) ==========");
        em.createQuery("SELECT c FROM CachedEntity c WHERE c.id=:id", CachedEntity.class)
                .setParameter("id", entities.get(0).getId())
                .getSingleResult();
        em.clear();

        System.out.println("========== Get entity using query with cache hint (query results will be cached) ==========");
        em.createQuery("SELECT c FROM CachedEntity c WHERE c.id=:id", CachedEntity.class)
                .setParameter("id", entities.get(0).getId())
                .setHint("org.hibernate.cacheable", true)
                .getSingleResult();
        em.clear();
        // TODO doesn't work for me
//        q.setHint("javax.persistence.cache.retrieveMode", CacheRetrieveMode.USE);

        System.out.println("========== Get data from query cache, no SQL should happen ==========");
        for (int i = 0; i < 3; i++) {
            em.createQuery("SELECT c FROM CachedEntity c WHERE c.id=:id", CachedEntity.class)
                    .setParameter("id", entities.get(0).getId())
                    .setHint("org.hibernate.cacheable", true).getSingleResult();
            em.clear();
        }
        System.out.println("====================");
    }

    @After
    public void evictCache() {
        emf.getCache().evictAll();
    }
}
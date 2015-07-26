package net.slisenko.jpa.examples.caching;

import net.slisenko.jpa.examples.caching.model.CachedEntity;
import net.slisenko.jpa.examples.caching.model.CachedEntityRelationship;
import org.junit.Test;

import javax.persistence.EntityManager;

/**
 * JPA cache structure:
 *      1-st lvl: EntityManager
 *      2-nd level: EntityManagerFactory
 *          - external cache provider (EHCache, ...), can be local or clustered
 *          - shared between entity managers
 *      JDBC caching (transparently for programmer)
 *
 * Types of caches:
 *      Entity cache - caches entity attributes (not entity objects!, this called hydrated), constructs entity objects for each request
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
 *      - Неверно настроенная стратегия кеша может ослабить уровень изоляции в БД (NONSTRICT READ WRITE сделает не выше read commited (ато иногда и dirty read), READ_WRITE - сделает не выше repeatable read)
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
 *
 * TODO обязательно ли помечать сущности как serializable чтобы они могли храниться в кеше при сбросе на диск? - проверить
 *
 * Ehcache
 *      - Использует java heap space или local disk storage
 *
 * @Cacheable аннотация, которая используется в JPA не позволяет задать стратегию кеширования для конкретной сущности
 */
public class TestEntityCache extends BaseCacheTest {

    @Test
    public void testCacheSimpleEntity() {
        // Start query entities
        p("========== Get entity first time, SQL works and populates cache ==========");
        em.getTransaction().begin();
        CachedEntity entity = em.find(CachedEntity.class, entities.get(0).getId());
        assertCached(entity);
        em.getTransaction().commit();
        em.clear();

        // Почему-то если создавать новый EntityManager, то мы идём в кеш, а если пользоваться старым - то берём из базы
        // Это если READ_WRITE
        // NONSTRICT_READ_WRITE берёт свободней из кеша
        // READ_WRITE уровень изоляции позволяет читать из кеша только если сессия стартовала позже, чем была создана запись в кеше

        // TODO может быть это связано с хибернейтовской сессией?
        p("========== Get entity second time, no SQL query should happen ==========");
//        em = emf.createEntityManager();
        em.getTransaction().begin();
        for (int i = 0; i < 10; i++) {
            entity = em.find(CachedEntity.class, entities.get(0).getId());
            assertCached(entity);
            em.clear();
        }
        em.getTransaction().commit();

        // Try get entity using second entity manager without SQL to database
        p("========== Get entity using another EntityManager - no SQL query happen, data retrieved from cache ==========");
        EntityManager em2 = emf.createEntityManager();
        entity = em2.find(CachedEntity.class, entities.get(0).getId());
        assertCached(entity);
        em2.close();
        p("====================");
    }

    /**
     * Связь к одному
     *      Т.к. связь находится в одном и том же объекте, то достаточно сделать обе сущности @Cache
     *      В случае eager - всё работает сразу
     *      В случае lazy - кеширование для связи будет работать только если она была принудительно проинициилизирована.
     */
    @Test
    public void testSingleRelationship() {
        // Prepare cached entity with relationship
        em.getTransaction().begin();
        CachedEntity entity = new CachedEntity("entity");
        entity.setRelationshipEager(new CachedEntityRelationship("eager"));
        entity.setRelationshipLazy(new CachedEntityRelationship("lazy"));
        em.persist(entity);
        em.getTransaction().commit();
        em.clear();

        p("========== Get entity with relationship first time ==========");
        entity = em.find(CachedEntity.class, entity.getId());
        p(entity);
        p("========== Get relationship ==========");
        // If relationship is lazy, we need to force it querying. After that relationship will be in cache.
        p(entity.getRelationshipLazy());
        p(entity.getRelationshipEager());
        em.clear();

        p("========== Get entity with relationship second time (no SQL works) ==========");
        entity = em.find(CachedEntity.class, entity.getId());
        p(entity);
        p("========== Get relationship ==========");
        p(entity.getRelationshipLazy());
        p(entity.getRelationshipEager());

        assertCached(entity);
        assertCached(entity.getRelationshipEager());
        // Actually, there is no additional SQL for this entity, but cache.contains returns false
//        assertCached(entity.getRelationshipLazy());
        p("========== ==========");
    }

    /**
     * Если используется join-таблица, то кеширование не заработало. TODO понять почему
     *
     * Если связь на стороне другой сущности, то на коллекцию нужно ставить @Cached. Тогда кеш сохраняет список ID-шников.
     */
    @Test
    public void testCollectionRelationship() {
        em.getTransaction().begin();
        CachedEntity c = new CachedEntity("entity");
        em.persist(c);
        CachedEntityRelationship rel1 = new CachedEntityRelationship("rel1");
        rel1.setEntity(c);
        em.persist(rel1);
        CachedEntityRelationship rel2 = new CachedEntityRelationship("rel2");
        rel2.setEntity(c);
        em.persist(rel2);
        CachedEntityRelationship rel3 = new CachedEntityRelationship("rel3");
        rel3.setEntity(c);
        em.persist(rel3);
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
        p("========== Get entity relationships (cache usage, no SQL executed) ==========");
        p(e.getListBack());
    }
}
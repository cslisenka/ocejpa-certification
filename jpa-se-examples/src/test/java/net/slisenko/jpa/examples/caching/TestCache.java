package net.slisenko.jpa.examples.caching;

import net.slisenko.AbstractJpaTest;
import org.junit.Test;

import javax.persistence.Cache;
import javax.persistence.CacheRetrieveMode;
import javax.persistence.CacheStoreMode;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Cache structure:
 * Cache on application level
 * 1-st lvl cache => EntityManager context
 * 2-nd lvl cache => EntityManagerFactory cache (shared for all EntityManagers)
 * Cache on JDBC level
 *
 * There is also a query cache
 *
 * http://blog.lauramamina.com/article?title=hibernate%20caching%20mechanisms
 * http://www.objectdb.com/java/jpa/persistence/cache#Setting_the_Shared_Cache_
 * https://www.linkedin.com/pulse/20140802232705-39109193-jpa-2-1-second-level-cache
 *
 * Различные виды данных требуют различной политики кэширования: соотношение чтение к записи изменяется, размер БД изменяется, и  некоторые таблицы используются совместно с другими приложениями. Так что, кэш второго уровня настраиваемся под детализацию каждого индивидуального класса или коллекцию ролей. Это позволяет, например, разрешить кэш второго уровня для справочных данных и запретить его для классов, представляющих финансовые записи.
 *
 * кэш-память, как правило полезно только для классов, которые по большинству считываются. Если у вас есть данные, которые обновляются чаще, чем читаются, не разрешайте кэш второго уровня, даже если все остальные условия для кэширования верны! Кроме того, кэш второго уровня может быть опасен в системах, которые разделяют данные с другими приложениями, которые могут эти данные изменить.
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
 *
 *  http://dr-magic.blogspot.com/2010/01/hibernate-5.html
 */
public class TestCache extends AbstractJpaTest {

    // TODO try caching in combination with javax.persistence.cache.retrieveMode
    // TODO try use different entity managers and see if they use shared cache

    // TODO create unit-test for 1-st level cache (EntityManager persistence context)

    // TODO to demonstrate cache we need to call this test 2 times
    @Test
    public void test() {
        em.getTransaction().begin();
        em.persist(new CachedEntity());
        em.persist(new CachedEntity());
        em.persist(new NotCachedEntity());
        em.persist(new NotCachedEntity());
        em.getTransaction().commit();

        Cache cache = emf.getCache();

        // For me nothing was added to shared cache :(
        List<CachedEntity> cachedEntities = em.createQuery("FROM CachedEntity").getResultList();
        for (CachedEntity cached : cachedEntities) {
            System.out.println("cached entity in cache = " + cache.contains(CachedEntity.class, cached.getId()));
        }

        List<NotCachedEntity> notCachedEntities = em.createQuery("FROM NotCachedEntity").getResultList();
        for (NotCachedEntity notCached : notCachedEntities) {
            System.out.println("not cached entity in cache = " + cache.contains(NotCachedEntity.class, notCached.getId()));
        }

        cache.evictAll();

        cachedEntities = em.createQuery("FROM CachedEntity").getResultList();
        for (CachedEntity cached : cachedEntities) {
            System.out.println("cached entity in cache = " + cache.contains(CachedEntity.class, cached.getId()));
        }

        notCachedEntities = em.createQuery("FROM NotCachedEntity").getResultList();
        for (NotCachedEntity notCached : notCachedEntities) {
            System.out.println("not cached entity in cache = " + cache.contains(NotCachedEntity.class, notCached.getId()));
        }
    }

    /**
     * For each separate query we can owerride caching behaviour (for example - not use cache for some specific query)
     * This settings are only for shared cache. If we have entity in 1-st level cache, this will not make any effect.
     *
     * TODO how to do this:
     * REFRESH option must be turned on if we read database from something else
     */
    @Test
    public void testDynamicCacheManagement() {
        // TODO investigate how to check this options
        TypedQuery<CachedEntity> q = em.createQuery("FROM CachedEntity", CachedEntity.class);
        // This query does not use cache when gets data, but updates cache for another queries
        q.setHint("javax.persistence.cache.retrieveMode", CacheRetrieveMode.BYPASS); // Not use cache when get data from database
        q.setHint("javax.persistence.cache.storeMode", CacheStoreMode.REFRESH); // Update cache after we got data
        List<CachedEntity> cachedEntities = q.getResultList();
        System.out.println(cachedEntities);
    }
}
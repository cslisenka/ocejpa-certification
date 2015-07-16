package net.slisenko.jpa.examples.caching;

import net.slisenko.AbstractJpaTest;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

/**
 * Cache structure:
 * Cache on application level
 * 1-st lvl cache => EntityManager context
 * 2-nd lvl cache => EntityManagerFactory cache (shared for all EntityManagers). Can be local and cluster.
 * Cache on JDBC level
 *
 * Types of caches:
 * 1. Entity cache (works with find())
 * 2. Query cache - caches query results with query parameters*
 *
 * Различные виды данных требуют различной политики кэширования: соотношение чтение к записи изменяется, размер БД изменяется, и  некоторые таблицы используются
 * совместно с другими приложениями. Так что, кэш второго уровня настраиваемся под детализацию каждого индивидуального класса или коллекцию ролей. Это позволяет,
 * например, разрешить кэш второго уровня для справочных данных и запретить его для классов, представляющих финансовые записи.
 *
 * кэш-память, как правило полезно только для классов, которые по большинству считываются. Если у вас есть данные, которые обновляются чаще, чем читаются,
 * не разрешайте кэш второго уровня, даже если все остальные условия для кэширования верны! Кроме того, кэш второго уровня может быть опасен в системах,
 * которые разделяют данные с другими приложениями, которые могут эти данные изменить.
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
 */
public class TestCache extends AbstractJpaTest {

    protected List<CachedEntity> entities = new ArrayList<>();

    @Before
    public void initData() {
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

    @Test
    public void testCacheSimpleEntity() {
        // Start query entities
        System.out.println("========== SQL query which gets item and stores in cache ==========");
        CachedEntity ce1 = em.find(CachedEntity.class, entities.get(0).getId());
        System.out.println("====================");

        System.out.println("========== No sql query should happen ==========");
        for (int i = 0; i < 10; i++) {
            ce1 = em.find(CachedEntity.class, entities.get(0).getId());
            em.clear();
        }
        System.out.println("====================");

        // Try get entity using second entity manager without SQL to database
        System.out.println("========== Try get entity using another EntityManager - no SQL query happen ==========");
        EntityManager em2 = emf.createEntityManager();
        ce1 = em2.find(CachedEntity.class, entities.get(0).getId());
        System.out.println("====================");
        em2.close();
    }

    @Test
    public void testQueryCache() {
        System.out.println("========== Populate entities cache ==========");
        for (int i = 0; i < 10; i++) {
            CachedEntity e1 = em.find(CachedEntity.class, entities.get(0).getId());
        }
        System.out.println("====================");

        System.out.println("========== Get entity using find() - no SQL happens ==========");
        em.find(CachedEntity.class, entities.get(0).getId());
        System.out.println("====================");

        System.out.println("========== Get entity using query ==========");
        for (int i = 0; i < 3; i++) {
            CachedEntity ce = em.createQuery("SELECT c FROM CachedEntity c WHERE c.id=:id", CachedEntity.class).setParameter("id", entities.get(0).getId()).getSingleResult();
        }
        System.out.println("====================");

        System.out.println("========== Get entity using query with cache hint ==========");
        for (int i = 0; i < 3; i++) {
            TypedQuery<CachedEntity> q = em.createQuery("SELECT c FROM CachedEntity c WHERE c.id=:id", CachedEntity.class).setParameter("id", entities.get(0).getId());
            q.setHint("org.hibernate.cacheable", true);
            CachedEntity ce = q.getSingleResult();
        }
        System.out.println("====================");
    }
}
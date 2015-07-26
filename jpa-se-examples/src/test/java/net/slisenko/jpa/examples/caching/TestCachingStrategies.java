package net.slisenko.jpa.examples.caching;

import net.slisenko.Identity;
import net.slisenko.jpa.examples.caching.model.NonStrictReadWriteEntity;
import net.slisenko.jpa.examples.caching.model.NotCachedEntity;
import net.slisenko.jpa.examples.caching.model.ReadOnlyEntity;
import net.slisenko.jpa.examples.caching.model.ReadWriteEntity;
import org.junit.Assert;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;

/**
 * Если не делается записи, то в принципе все стратегии работают одинаково быстро.
 * Если запись происходит, то READ_WRITE опирается в проблемах на базу, NON_STRICT_READ_WRITE - на себя
 * По сути если записи нет, то read_write делает для каждой операции поиска в кеше ещё и сравнение timestamps, а nonstrict - не делает
 */
public class TestCachingStrategies extends BaseCacheTest {

     /**
      * Hibernate throws exception if we want to change @Cache(READ ONLY) entity
      *
      * Caches data that is never updated.
      *
      * Good solution for immutable data (for example: messages in chat, bids in auction).
      * No consistency problems.
      *
      * http://vladmihalcea.com/2015/04/27/how-does-hibernate-read_only-cacheconcurrencystrategy-work/
      */
     @Test(expected = RollbackException.class)
     public void testReadOnly() {
          testCache(new ReadOnlyEntity());
     }

     /**
      * Entity is cached, but when we retrieve entity, Hibernate generates SQL
      * TODO what is impact from cache?
      *
      * Caches data that is sometimes updated while maintaining the semantics of “read committed” isolation level.
      * If the database is set to “repeatable read,” this concurrency strategy almost maintains the semantics.
      * Repeatable read isolation is compromised in the case of concurrent writes.
      *
      * http://codespot.net/2014/02/03/hibernate-caching-strategies/
      *
      * Non strict read write
      * TODO Pending (i.e. uncommitted) changes can become visible through the cache to other transactions, breaking the I guarantee from ACID.!
      * This strategy will invalidate cached entries on updates and only populate the cache with data loaded from the database.
      * Note that these invalidations happen after the transaction has successfully committed. As a result there is a race where “old” values can be seen in the cache,
      * while updated in the database.
      *
      *  There’s no locking ever.

      • So, when the object is actually being updated in the database, at the point of committing(till database completes the commit),
      the cache has the old object, database has the new object.

      • Now, if any other session looks for the object , it will look in the cache and find the old object.(DIRTY READ)

      • However, as soon as the commit is complete, the object will be evicted from the cache, so that the next session which looks for the object, will have to look in the database.

      • If you execute the same code (Demo1) with the diagnostic: System.out.println(“Cache Contains?”+sf.getCache().containsEntity(Organization.class,421l));

      Before and update the tx.commit() you will find that before the commit, the cache contained the entry, after the commit, it’s gone. Hence forcing session2 to look in the database and reload the data in the cache.

      So, nonstrict read/write is appropriate if you don’t require absolute protection from dirty reads, or if the odds of concurrent access are so slim that you’re willing to accept an occasional dirty read. Obviously the window of Dirty Read is during the time when the database is actually updated, and the object has not YET been evicted from the cache.
      *
      *
      *
      * Strict read/write
      * Every other transaction accessing a locked entry will consider it a cache miss and hit the database instead.
      * • As soon as somebody tries to update/delete an item, the item is soft-locked in the cache, so that if any other session tries to look for it, it has to go to the database.

      • Now, once the update is over and the data has been committed, the cache is refreshed with the fresh data and the lock is released, so that other transactions can now look in the CACHE and don’t have to go to the database.

      • So, there is no chance of Dirty Read, and any session will almost ALWAYS read READ COMMITTED data from the database/Cache.
      *
      * Нет inconsistency window:
      * На сущность в кеше ставится lock.
      * Происходит комит транзакции в БД.
      * Другие транзакции вынуждены читать из базы когда установлен лок.
      * После комита данные обновляются в кеше и лок снимается.
      * Другие транзакции читают обновлённую сущность из кеша.
      *
      * На лок ставится таймаут. Если транзакция rollback, то лок ещё какое-то время висит.
      * Таким образом мы имеем гарантию что всегда получим последнюю версию сущности.
      *
      * Мы можем брать из кеша только записи, timestamp у которых меньше чем у нашей сессии. Если больше - идём в базу
      * that i.e. cache entry reflects the database state prior to transaction start.
      * Таким образом сущности, вытягиваемые из кеша отображают состояние базы до начала сессии.
      * Если что-то было обновлено - то мы читаем из базы полагаясь на её уровень изоляции. В случае конфликта - даём БД право решать.
      *
      * https://anirbanchowdhury.wordpress.com/2012/07/23/hibernate-second-level-cache-ehcache/
      * TODO попробовать оптимистическую и писсимистическую блокировку + кеш второго уровня
      * https://anirbanchowdhury.wordpress.com/2012/07/23/hibernate-second-level-cache-ehcache/
      *http://vladmihalcea.com/2015/05/18/how-does-hibernate-nonstrict_read_write-cacheconcurrencystrategy-work/
      * http://vladmihalcea.com/2015/05/25/how-does-hibernate-read_write-cacheconcurrencystrategy-work/
      * http://vladmihalcea.com/2015/04/27/how-does-hibernate-read_only-cacheconcurrencystrategy-work/
      * http://vladmihalcea.com/2015/04/16/things-to-consider-before-jumping-to-enterprise-caching/
      * http://vladmihalcea.com/2014/03/03/caching-best-practices/
      * http://vladmihalcea.com/2015/04/20/a-beginners-guide-to-cache-synchronization-strategies/
      */
     @Test
     public void testReadWrite() {
          /**
           * обычный read-write работает как-будто вообще никакого кеша не включено
           * Только ассерт на нахождение записи в кеше возвращает true
            */
//          testCache(new NotCachedEntity());
          testCache(new ReadWriteEntity());


         // Что будет если записывать в двух параллельных транзакциях?
         // Одна транзакция делает запись, посавила лок
         // В этот момент вторая транзакция хочет записать, может ли она это сделать если в кеше стоит лок?
//          testConcurrentCacheUpdates(new ReadWriteEntity());
//          testConcurrentCacheUpdates(new NonStrictReadWriteEntity());
//          testConcurrentCacheUpdates(new NotCachedEntity());

     }

     /**
      * Cache is invalidated after we update entity, but works if we retrieve entity several times
      *
      * Caches data that is sometimes updated without ever locking the cache. If concurrent access to an item is possible,
      * this concurrency strategy makes no guarantee that the item returned from the cache is the latest version available
      * in the database. Configure your cache timeout accordingly.
      *
      * Перед тем как закомитить транзакцию, объект чистится из кеша.
      * Транзакция комитится.
      * Чистка кеша выполняется ещё раз.
      *
      * Маленькое inconsistency window:
      * Кто-то может запросить данные после первой очистки, но до комита и тем самым заполнить кеш старой версией.
      * Комит проходит, но другие транзакции получают всё ещё старую версию.
      * Только после второй очистки все будут получать новую версию.
      *
      * Нет гарантии что мы получим последнюю версию сущности.
      *
      * the entity is sometimes updated (so read-only does not apply), but that it's extremely unlikely that two concurrent transactions update the same item.
      * For example, if you have thousands of users accessing the data, an one batch regularly update it, this option is the right one to choose: only one transaction updates the items at a time.
      *
      * TODO test concurrent strategy
      *
      * http://vladmihalcea.com/2015/05/18/how-does-hibernate-nonstrict_read_write-cacheconcurrencystrategy-work/
      */
     @Test
     public void testNonStrictReadWrite() {
          /**
           * По моим экспериментам, nonstrict работает как положено - кеш используется, после апдейта запись из кеша выкидывается
           */
          testCache(new NonStrictReadWriteEntity());
//          p("========== Populate initial entity and cache ==========");
//          // Test concurrency
//          NonStrictReadWriteEntity re = new NonStrictReadWriteEntity();
//          re.setName("concurrent");
//          em.getTransaction().begin();
//          em.persist(re);
//          em.getTransaction().commit();
//          em.clear();
//          // Populate cache
//          re = em.find(NonStrictReadWriteEntity.class, re.getId());
//          em.clear();
//          assertCached(re);
//
//          p("========== Get entity, no SQL runs ==========");
//          // Get entity using em1
//          em.getTransaction().begin();
//          re = em.find(NonStrictReadWriteEntity.class, re.getId());
//
//          p("========== Change entity using another transaction ==========");
//          EntityManager em2 = emf.createEntityManager();
//          em2.getTransaction().begin();
//          NonStrictReadWriteEntity re2 = em2.find(NonStrictReadWriteEntity.class, re.getId());
//          assertCached(re2);
//          re2.setName("changed concurrently");
//          em2.getTransaction().commit();
//          em2.close();
//          assertNotCached(re2); // Non strict read-write выкидывает entity из кеша
//
//          p("========== Read entity one more time in 1-st transaction, get stale data ==========");
//          em.clear();
//          // Get entity one more time - тут по идее мы должны уже новую сущность загрузить
//          re = em.find(NonStrictReadWriteEntity.class, re.getId());
//          assertCached(re);
//          // In same transaction we do not see changes, repeatable read works
//          // TODO и почему интересно он работает? я его не просил
//          Assert.assertEquals("concurrent", re.getName());
//          em.getTransaction().commit();
//
//          em.clear();
//          emf.getCache().evictAll();
//
//          // Try get entity in another transaction
//          p("========== Read stale data once again from cache ==========");
//          em.getTransaction().begin();
//          re = em.find(NonStrictReadWriteEntity.class, re.getId());
//          Assert.assertEquals("concurrent", re.getName());
//          em.getTransaction().commit();
     }

     public void testConcurrentCacheUpdates(Identity re) {
          p("========== Populate initial entity and cache ==========");
          re.setName("concurrent");
          em.getTransaction().begin();
          em.persist(re);
          em.getTransaction().commit();
          em.clear();

          // Populate cache
          re = em.find(re.getClass(), re.getId());
          em.clear();
          assertCached(re);

//          p("========== Get entity, no SQL runs ==========");
          // Get entity using em1
//          em.getTransaction().begin();
//          re = em.find(re.getClass(), re.getId());

          p("========== Get entity for change, no SELECT SQL generated ==========");
          em.getTransaction().begin();
          Identity re2 = em.find(re.getClass(), re.getId());
          assertCached(re2);
          re2.setName("changed concurrently");
          em.getTransaction().commit();
          assertNotCached(re2); // Non strict read-write выкидывает entity из кеша
//
//          p("========== Read entity one more time in 1-st transaction ==========");
//          // Get entity one more time - тут по идее мы должны уже новую сущность загрузить
//          // Почему тут возвращается старая версия?
//          re = em.find(re.getClass(), re.getId());
////          assertCached(re);
//          // In same transaction we do not see changes, repeatable read works
//          // TODO и почему интересно он работает? я его не просил
//          Assert.assertEquals("concurrent", re.getName());
//          em.getTransaction().commit();
//
//          em.clear();
//          // Почему без евикта мы продолжаем получать старые версии?
////          emf.getCache().evictAll();
//
//          // Try get entity in another transaction
//          p("========== Read stale data once again from cache ==========");
//          em.getTransaction().begin();
//          re = em.find(re.getClass(), re.getId());
//          // При non strict read/write мы всё ещё видим старую версию. Почему?
//          Assert.assertEquals("changed concurrently", re.getName());
//          em.getTransaction().commit();
     }

     public void testCache(Identity re) {
         // TODO test remove entity
          p("========== Persist initial entity ==========");
          em.getTransaction().begin();
          // Save read only entity
          re.setName("read write initial");
          em.persist(re);
          em.getTransaction().commit();
          em.clear();

          for (int i = 0; i < 3; i++) {
               p("========== Get entity, use SQL for populating cache " + i + "==========");
               em.getTransaction().begin();
               // Read entity, populate cache
               re = em.find(re.getClass(), re.getId());
               em.getTransaction().commit();
               em.clear();
               assertCached(re);
          }

          p("========== Get entity second time, no SQL used, change name, after UPDATE query, entity is evicted  ==========");
          em.getTransaction().begin();
          re = em.find(re.getClass(), re.getId());
          re.setName("changed");
          em.getTransaction().commit();
          em.clear();
          assertNotCached(re);

          for (int i = 0; i < 3; i++) {
               p("========== Get entity once again, SQL query works because entity was evicted " + i + "  ==========");
               em.getTransaction().begin();
               re = em.find(re.getClass(), re.getId());
               Assert.assertEquals(re.getName(), "changed");
               em.getTransaction().commit();
               em.clear();
               assertCached(re);
          }
     }

     /**
      * Обновления в БД и кеш происходят в рамках одной распределённой XA-транзакции = синхронно.
      */
     @Test
     public void testTransactionalCache() {

     }
}
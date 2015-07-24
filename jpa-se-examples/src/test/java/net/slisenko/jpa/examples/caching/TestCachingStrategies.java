package net.slisenko.jpa.examples.caching;

import net.slisenko.Identity;
import net.slisenko.jpa.examples.caching.model.NonStrictReadWriteEntity;
import net.slisenko.jpa.examples.caching.model.ReadOnlyEntity;
import net.slisenko.jpa.examples.caching.model.ReadWriteEntity;
import org.junit.Assert;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;

public class TestCachingStrategies extends BaseCacheTest {

     /**
      * Hibernate throws exception if we want to change @Cache(READ ONLY) entity
      *
      * Caches data that is never updated.
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
      *
      *
      *
      * TODO попробовать оптимистическую и писсимистическую блокировку + кеш второго уровня
      * https://anirbanchowdhury.wordpress.com/2012/07/23/hibernate-second-level-cache-ehcache/
      *http://vladmihalcea.com/2015/05/18/how-does-hibernate-nonstrict_read_write-cacheconcurrencystrategy-work/
      * http://vladmihalcea.com/2015/05/25/how-does-hibernate-read_write-cacheconcurrencystrategy-work/
      */
     @Test
     public void testReadWrite() {
//          testCache(new ReadWriteEntity());

          p("========== Populate initial entity and cache ==========");
          // Test concurrency
          NonStrictReadWriteEntity re = new NonStrictReadWriteEntity();
          re.setName("concurrent");
          em.getTransaction().begin();
          em.persist(re);
          em.getTransaction().commit();
          em.clear();
          // Populate cache
          re = em.find(NonStrictReadWriteEntity.class, re.getId());
          em.clear();
//          assertCached(re);

          p("========== Get entity, no SQL runs ==========");
          // Get entity using em1
          em.getTransaction().begin();
          re = em.find(NonStrictReadWriteEntity.class, re.getId());

               p("========== Change entity using another transaction ==========");
               EntityManager em2 = emf.createEntityManager();
               em2.getTransaction().begin();
               NonStrictReadWriteEntity re2 = em2.find(NonStrictReadWriteEntity.class, re.getId());
     //          assertCached(re2);
               re2.setName("changed concurrently");
               em2.getTransaction().commit();
               em2.close();
     //          assertNotCached(re2);

          p("========== Read entity one more time in 1-st transaction, get stale data ==========");
          em.clear();
          // Get entity one more time
          re = em.find(NonStrictReadWriteEntity.class, re.getId());
//          assertNotCached(re);
          // In same transaction we do not see changes, repeatable read works
          // TODO и почему интересно он работает? я его не просил
          Assert.assertEquals("concurrent", re.getName());
          em.getTransaction().commit();

          em.clear();
//          emf.getCache().evictAll();

          // Если у нас включён кеш, мы продолжаем читать устаревшие данные, нужно грамотно настроить экспирейшен
          // Try get entity in another transaction
          p("========== Read stale data once again from cache ==========");
          em.getTransaction().begin();
          re = em.find(NonStrictReadWriteEntity.class, re.getId());
          Assert.assertEquals("changed concurrently", re.getName());
          em.getTransaction().commit();
     }

     /**
      * Cache is invalidated after we update entity, but works if we retrieve entity several times
      *
      * Caches data that is sometimes updated without ever locking the cache. If concurrent access to an item is possible,
      * this concurrency strategy makes no guarantee that the item returned from the cache is the latest version available
      * in the database. Configure your cache timeout accordingly.
      *
      * TODO test concurrent strategy
      */
     @Test
     public void testNonstrictReadWrite() {
          testCache(new NonStrictReadWriteEntity());
     }

     public void testCache(Identity re) {
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
}
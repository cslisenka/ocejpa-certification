package net.slisenko.jpa.examples.caching;

import net.slisenko.Identity;
import net.slisenko.jpa.examples.caching.model.NonStrictReadWriteEntity;
import net.slisenko.jpa.examples.caching.model.ReadOnlyEntity;
import net.slisenko.jpa.examples.caching.model.ReadWriteEntity;
import org.junit.Assert;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.util.Date;

/**
 * There are 4 cache strategies:
 *        READ_ONLY - allows only reads/inserts, no consistency problems
 *        NONSTRICT_READ_WRITE - allows reads/updates, not good isolation guarantee
 *        READ_WRITE - allows reads/updates, isolation guarantee
 *        TRANSACTIONAL - allows reads/updates, isolation guarantee, works only in J2EE environment
 *
 * If we do not make updates, all caching strategies almost similar in performance.
 * If updates happen, NONSTRICT READ_WRITE relies on cache, READ_WRITE - on database.
 *
 * http://vladmihalcea.com/2015/04/16/things-to-consider-before-jumping-to-enterprise-caching/
 * http://vladmihalcea.com/2014/03/03/caching-best-practices/
 * http://vladmihalcea.com/2015/04/20/a-beginners-guide-to-cache-synchronization-strategies/
 */
public class TestCachingStrategies extends BaseCacheTest {

     /**
      * Caches data that is never updated. No consistency problems.
      * Hibernate throws exception if we want to change @Cache(READ ONLY) entity
      *
      * Use cases:
      *        Immutable data (for example: messages in chat, bids in auction).
      *
      * http://vladmihalcea.com/2015/04/27/how-does-hibernate-read_only-cacheconcurrencystrategy-work/
      */
     @Test
     public void testReadOnly() {
          ReadOnlyEntity entity = persistAndLoadToCache(new ReadOnlyEntity());

          // After we populated cache, hibernate always goes to cache.
          // Doesn't matter new session or old.
          queryEntityManyTimes(entity, false);
          queryEntityManyTimes(entity, true);

          // Read only cache doesn't allow to update/delete entities
          try {
               updateEntity(entity);
               Assert.fail("No exception");
          } catch (PersistenceException e) {
          }
     }

     /**
      * Allows update/delete entities. Before and after transaction commited, entity removed from cache.
      * If entity is already in cache, hibernate goes to cache. There’s no locking ever.
      * This concurrency strategy makes no guarantee that the item returned from the cache is the latest version available in database.
      * Configure your cache timeout accordingly.
      *
      * Small inconsistency window:
      *        tx1 invalidates cache
      *        tx2.read - populates cache
      *        tx1.commit
      *        tx3.read - gets old entity (which is already in cache)
      *        tx1.invalidate
      *        after that all transactions get recent version
      *
      * So, when the object is actually being updated in the database, at the point of committing(till database completes the commit),
      * the cache has the old object, database has the new object.
      * No read commited guaranty:
      *        tx1.read (get v1)
      *        tx2.update(v2), tx2.evict
      *        tx1.read (get v2)
      * Use cases:
      *        So, nonstrict read/write is appropriate if you don’t require absolute protection from dirty reads, or if the odds
      *        of concurrent access are so slim that you’re willing to accept an occasional dirty read.
      *
      * http://vladmihalcea.com/2015/05/18/how-does-hibernate-nonstrict_read_write-cacheconcurrencystrategy-work/
      * https://anirbanchowdhury.wordpress.com/2012/07/23/hibernate-second-level-cache-ehcache/
      */
     @Test
     public void testNonStrictReadWrite() {
          /**
           * По моим экспериментам, nonstrict работает как положено - кеш используется, после апдейта запись из кеша выкидывается
           */
          NonStrictReadWriteEntity entity = persistAndLoadToCache(new NonStrictReadWriteEntity());

          // If object is in cache, hibernate goes to cache
          queryEntityManyTimes(entity, false);
          queryEntityManyTimes(entity, true);

          // Entity removed from cache after update
          updateEntity(entity);
          assertNotCached(entity);
     }

     /**
      * Possible to update cached data. When we update/delete - hibernate locks cached entity. All other requests go to database in that time.
      * Each cached entity has own timestamp.
      *        If session timestamp > entity timestamp, hibernate goes to cache (entity updated before session started)
      *        If session timestamp < entity timestamp, hibernate goes to database (entity updated after session started)
      *             Every other transaction accessing a locked entry will consider it a cache miss and hit the database instead.
      *             When session works, something changed. Hibernate relies on database isolation level.
      * Maintains read commited isolation level. No stale data reads.
      * Less cache usage than NONSTRICT_READ_WRITE, more rely on database => more queries to database.
      *
      * Use cases:
      *        Actual data state is guaranteed, dirty reads/read commited violation is not allowed.
      *
      * http://codespot.net/2014/02/03/hibernate-caching-strategies/
      * http://vladmihalcea.com/2015/05/25/how-does-hibernate-read_write-cacheconcurrencystrategy-work/
      */
     @Test
     public void testReadWrite() {
          ReadWriteEntity entity = persistAndLoadToCache(new ReadWriteEntity());

          // If object updated after session started, hibernate goes to database always
          // If something changed, we rely on database isolation level
          queryEntityManyTimes(entity, false);

          // If object updated before session started, hibernate goes to cache
          queryEntityManyTimes(entity, true);

          // Entity updated in cache after update
          updateEntity(entity);
          assertCached(entity);
     }

     /**
      * Cache and database are synchronously updated using distributed XA-transaction.
      * Works only in J2EE environment.
      * Guarantees read-commited isolation level.
      */
     @Test
     public void testTransactionalCache() {
          // TODO test in J2EE environment
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
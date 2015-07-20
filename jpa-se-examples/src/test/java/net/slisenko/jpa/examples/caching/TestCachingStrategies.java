package net.slisenko.jpa.examples.caching;

import net.slisenko.jpa.examples.caching.model.CachedEntity;
import net.slisenko.jpa.examples.caching.model.NonStrictReadWriteEntity;
import net.slisenko.jpa.examples.caching.model.ReadOnlyEntity;
import org.junit.Assert;
import org.junit.Test;

import javax.persistence.RollbackException;

///**
// * TODO check all this behaviour
// * * TODO провешировать сущность, потом её модифицировать, и опять считать. Посмотреть что будет.
// * read-only - Caches data that is never updated.
//
// nonstrict-read-write - Caches data that is sometimes updated without ever
// locking the cache. If concurrent access to an item is possible, this concurrency
// strategy makes no guarantee that the item returned from the cache is the latest
// version available in the database. Configure your cache timeout accordingly.
//
// read-write - Caches data that is sometimes updated while maintaining the
// semantics of “read commied” isolation level. If the database is set to “repeatable
// read,” this concurrency strategy almost maintains the semantics. Repeatable read
// isolation is compromised in the case of concurrent writes.
// */
public class TestCachingStrategies extends BaseCacheTest {

     @Test(expected = RollbackException.class)
     public void testReadOnly() {
          p("Persist initial entity");
          em.getTransaction().begin();
          // Save read only entity
          ReadOnlyEntity re = new ReadOnlyEntity();
          re.setName("read only initial");
          em.persist(re);
          em.getTransaction().commit();
          em.clear();

          p("Change entity name");
          em.getTransaction().begin();
          // Read entity, populate cache
          re = em.find(ReadOnlyEntity.class, re.getId());
          assertCached(re);
          // Change entity
          re.setName("changed");
          em.getTransaction().commit();
          em.clear();

          p("Get entity second time");
          re = em.find(ReadOnlyEntity.class, re.getId());
          Assert.assertEquals(re.getName(), "changed");
     }

     @Test
     public void testReadWrite() {
          p("========== Persist initial entity ==========");
          em.getTransaction().begin();
          // Save read only entity
          NonStrictReadWriteEntity re = new NonStrictReadWriteEntity();
          re.setName("read write initial");
          em.persist(re);
          em.getTransaction().commit();
          em.clear();

          p("========== Get entity, use SQL for populating cache ==========");
          em.getTransaction().begin();
          // Read entity, populate cache
          re = em.find(NonStrictReadWriteEntity.class, re.getId());
          assertCached(re);
          // Change entity
          em.getTransaction().commit();
          em.clear();

          p("========== Get entity second time, no SQL used, change name ==========");
          em.getTransaction().begin();
          re = em.find(NonStrictReadWriteEntity.class, re.getId());
          re.setName("changed");
          em.getTransaction().commit();
          em.clear();

     }

     @Test
     public void testNonstrictReadWrite() {

     }
}
package net.slisenko.jpa.examples.caching;

import org.junit.Test;

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

     @Test
     public void testReadOnly() {
          em.getTransaction().begin();


          em.getTransaction().commit();
     }

     @Test
     public void testReadWrite() {

     }

     @Test
     public void testNonstrictReadWrite() {

     }
}
package net.slisenko.jpa.examples.queries.bulk;

import net.slisenko.AbstractJpaTest;
import net.slisenko.jpa.examples.JPAUtil;
import net.slisenko.jpa.examples.queries.City;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Bulk operations are dangerous because:
 * 1. They do not update objects in persistence context
 * 2. They do not increment version number when we use optimistic locking
 * 3. Cascading operations do not work with bulk queries
 * 4. Bulk queries do not care about relationships and we can get FOREIGN KEY CONSTRAINT FAIL
 * 5. FOREIGN KEY fails also when entity has persistent collection
 */
public class TestBulkQueriesExample extends AbstractJpaTest {

    @Test
    public void testBulkUpdateDelete() {
        em.getTransaction().begin();
        for (int i = 0; i < 10; i++) {
            em.persist(new City("city " + i));
        }
        em.getTransaction().commit();

        // Bulk update
        em.getTransaction().begin();
        em.createQuery("UPDATE City c SET c.name = 'renamed city'").executeUpdate();
        JPAUtil.printContext(em);
        em.getTransaction().commit();

        // Bulk delete
        em.getTransaction().begin();
        em.createQuery("DELETE FROM City c").executeUpdate();
        JPAUtil.printContext(em);
        em.getTransaction().commit();
    }
}

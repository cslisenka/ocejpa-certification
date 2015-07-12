package net.slisenko.jpa.examples.schemaGenerator;

import net.slisenko.AbstractJpaTest;
import org.junit.Assert;
import org.junit.Test;

import javax.persistence.PersistenceException;

public class TestIndexing extends AbstractJpaTest {

    @Test
    public void testIndexing() {
        em.getTransaction().begin();

        IndexedEntity entity = new IndexedEntity();
        entity.setName("name");
        entity.setAge(15);
        entity.setEmail("email");
        em.persist(entity);

        em.getTransaction().commit();

        // Try to violate unique constraint
        try {
            em.getTransaction().begin();

            IndexedEntity entity2 = new IndexedEntity();
            entity2.setName("name2");
            entity2.setAge(17);
            entity2.setEmail("email");
            em.persist(entity2);

            em.getTransaction().commit();
            Assert.fail("No unique constraint exception");
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
    }
}
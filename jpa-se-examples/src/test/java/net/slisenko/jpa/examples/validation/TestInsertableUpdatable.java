package net.slisenko.jpa.examples.validation;

import net.slisenko.AbstractJpaTest;
import org.junit.Test;

import javax.persistence.PersistenceException;

/**
 *  nullable=false - database constraint
 *  insertable=false - jpa does not save field value when execute INSERT statement
 *  updateble=false - jpa does not save field value when execute UPDATE statement
 *
 *  JPA does not throw exception
 */
public class TestInsertableUpdatable extends AbstractJpaTest {

    @Test
    public void testCorrectValues() {
        em.getTransaction().begin();
        NotOptionalRelationship notOptionalRelationship = new NotOptionalRelationship("first");
        em.persist(notOptionalRelationship);

        EntityWithNotOptionalMappings readOnly = new EntityWithNotOptionalMappings();
        readOnly.setNotNullable("not nullable");
        readOnly.setNotUpdatable("not updatable");
        readOnly.setNotInsertable("not insertable");
        readOnly.setNotOptional(notOptionalRelationship);
        em.persist(readOnly);
        em.getTransaction().commit();
        em.clear();

        System.out.println(em.find(EntityWithNotOptionalMappings.class, readOnly.getId()));

        // Try to update - no exceptions, just impossible to update
        em.getTransaction().begin();
        NotOptionalRelationship second = new NotOptionalRelationship("second");
        em.persist(second);

        readOnly = em.merge(readOnly);
        readOnly.setNotNullable("changed");
        readOnly.setNotInsertable("update not insertable");
        readOnly.setNotUpdatable("update not updatable");
        readOnly.setNotOptional(second);
        em.getTransaction().commit();
        em.clear();

        System.out.println(em.find(EntityWithNotOptionalMappings.class, readOnly.getId()));

        // Try to clear not optional relationship
        em.getTransaction().begin();
        em.find(EntityWithNotOptionalMappings.class, readOnly.getId()).setNotOptional(null);
        em.getTransaction().commit();
        em.clear();

        // JPA does not clear not optional entity
        System.out.println(em.find(EntityWithNotOptionalMappings.class, readOnly.getId()));


        // Create new entity with null not optional relationship!
        try {
            em.getTransaction().begin();
            EntityWithNotOptionalMappings readOnly2 = new EntityWithNotOptionalMappings();
            readOnly2.setNotNullable("not nullable");
            readOnly2.setNotUpdatable("not updatable");
            readOnly2.setNotInsertable("not insertable");
            em.persist(readOnly2);
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            System.out.println("Can not save entity where not optional relationship is null");
            e.printStackTrace();
        }
    }
}
package net.slisenko.jpa.examples.cascading;

import net.slisenko.AbstractJpaTest;
import org.junit.Test;

import java.util.List;

public class CascadingExampleTest extends AbstractJpaTest {

    @Test
    public void testCascadeAll() {
        em.getTransaction().begin();
        ParentEntity parentEntity = new ParentEntity();
        ChildCascadeEntity cascadeAllEntity = new ChildCascadeEntity("cascade all");
        parentEntity.setCascadeAllEntity(cascadeAllEntity);

        ChildCascadeEntity cascadeMergeEntity = new ChildCascadeEntity("cascade merge");
        parentEntity.setCascadeMergeEntity(cascadeMergeEntity);

        ChildCascadeEntity cascadeDeleteEntity = new ChildCascadeEntity("cascade delete");
        parentEntity.setCascadeDeleteEntity(cascadeDeleteEntity);

        // We have only DELETE cascade here, we need to persist this entity manually
        em.persist(cascadeDeleteEntity);
        parentEntity = em.merge(parentEntity); // Call MERGE, because we defined cascade MERGE

        em.getTransaction().commit();
        em.clear();

        // Check that all entities stored
        ParentEntity pe = em.find(ParentEntity.class, parentEntity.getId());
        System.out.println(pe);

        // Delete parent entity - cascade ALL and cascade DELETE should be removed
        em.getTransaction().begin();
        em.remove(pe);
        em.getTransaction().commit();
        em.clear();

        // Here we see entities only with CASCADE MERGE
        List<ChildCascadeEntity> childsEntities = em.createQuery("FROM ChildCascadeEntity").getResultList();
        System.out.println("Entities with cascade=MERGE are not deleted, other entities are deleted");
        System.out.println(childsEntities);
    }
}
package net.slisenko.jpa.examples.relationship.orphanRemoval;

import net.slisenko.AbstractJpaTest;
import org.junit.Test;

import java.util.List;

/**
 * Managing parent-child relationships
 * Orphpan removal can be defined on @OneToMany and @mManyToOne relationships
 * Child relationship will be removed when relationship is broken:
 * 1. set null to relationship
 * 2. remove element from collection
 *
 * Similar to CASCADE REMOVE
 */
public class TestOrphanRemoval extends AbstractJpaTest {

    @Test
    public void test() {
        em.getTransaction().begin();
        OrphanParentEntity parent = new OrphanParentEntity();

        OrphanEntity single = new OrphanEntity("single");
        em.persist(single);

        OrphanEntity noOrphan = new OrphanEntity("no orphan");
        em.persist(noOrphan);

        OrphanEntity list1 = new OrphanEntity("list 1");
        em.persist(list1);

        OrphanEntity list2 = new OrphanEntity("list 2");
        em.persist(list2);

        OrphanEntity list3 = new OrphanEntity("list 3");
        em.persist(list3);

        parent.setSingleOrphan(single);
        parent.setNotOrphan(noOrphan);
        parent.getMultipleOrphans().add(list1);
        parent.getMultipleOrphans().add(list2);
        parent.getMultipleOrphans().add(list3);

        em.persist(parent);
        em.getTransaction().commit();
        em.clear();

        em.getTransaction().begin();
        parent = em.find(OrphanParentEntity.class, parent.getId());
        System.out.println(parent);
        em.remove(parent);
        em.getTransaction().commit();
        em.clear();

        // Find all exist orphans - only "no orphan" should exist
        List<OrphanEntity> orphans = em.createQuery("FROM OrphanEntity").getResultList();
        System.out.println(orphans);
    }

    // TODO test remove orphan from list (destroy relationships) - orphan should be removed
}
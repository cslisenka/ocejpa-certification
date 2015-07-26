package net.slisenko.jpa.examples.relationship.orphanRemoval;

import junit.framework.Assert;
import net.slisenko.AbstractJpaTest;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Managing parent-child relationships
 * Orphan removal can be defined on @OneToMany and @mManyToOne relationships
 *
 * Child relationship will be removed when relationship is broken:
 * 1. set null to relationship
 * 2. remove element from collection
 *
 * Orphan removal doesn't work when we use JPQL DELETE queries
 */
public class TestOrphanRemoval extends AbstractJpaTest {

    private OrphanParentEntity parent;
    private OrphanEntity single;
    private OrphanEntity noOrphan;
    private OrphanEntity list1, list2, list3;

    @Before
    public void initData() {
        em.getTransaction().begin();
        em.createQuery("DELETE FROM OrphanParentEntity").executeUpdate();
        em.createQuery("DELETE FROM OrphanEntity").executeUpdate();
        em.getTransaction().commit();

        em.getTransaction().begin();

        parent = new OrphanParentEntity();
        single = new OrphanEntity("single");
        em.persist(single);

        noOrphan = new OrphanEntity("no orphan");
        em.persist(noOrphan);

        list1 = new OrphanEntity("list 1");
        em.persist(list1);

        list2 = new OrphanEntity("list 2");
        em.persist(list2);

        list3 = new OrphanEntity("list 3");
        em.persist(list3);

        parent.setSingleOrphan(single);
        parent.setNotOrphan(noOrphan);
        parent.getMultipleOrphans().add(list1);
        parent.getMultipleOrphans().add(list2);
        parent.getMultipleOrphans().add(list3);

        em.persist(parent);
        em.getTransaction().commit();
        em.clear();
    }

    @Test
    public void testRemoteOrphansWhenRemoveParent() {
        em.getTransaction().begin();
        parent = em.find(OrphanParentEntity.class, parent.getId());
        System.out.println(parent);
        em.remove(parent);
        em.getTransaction().commit();
        em.clear();

        // Find all exist orphans
        List<OrphanEntity> orphans = em.createQuery("FROM OrphanEntity").getResultList();
        p(orphans);
        // Only "no orphan" should exist
        Assert.assertEquals(1, orphans.size());
        Assert.assertEquals(noOrphan.getId(), orphans.get(0).getId());
    }

    @Test
    public void testOrphanRemovedWhenDropFromList() {
        em.getTransaction().begin();
        List<OrphanEntity> orphans = em.createQuery("FROM OrphanEntity").getResultList();
        Assert.assertEquals(5, orphans.size());

        parent = em.find(OrphanParentEntity.class, parent.getId());
        parent.getMultipleOrphans().remove(parent.getMultipleOrphans().get(0));
        em.getTransaction().commit();

        // Check that one orphan was removed
        orphans = em.createQuery("FROM OrphanEntity").getResultList();
        p(orphans);
        // Two related orphans and one not related exist
        Assert.assertEquals(4, orphans.size());
    }

    @Test
    public void testOrphanNotRemovedWhenJPQLDeleteQueries() {
        em.getTransaction().begin();
        List<OrphanEntity> orphans = em.createQuery("FROM OrphanEntity").getResultList();
        Assert.assertEquals(5, orphans.size());
        em.createQuery("DELETE FROM OrphanParentEntity").executeUpdate();
        em.getTransaction().commit();
        em.clear();

        orphans = em.createQuery("FROM OrphanEntity").getResultList();
        Assert.assertEquals(5, orphans.size());
    }
}
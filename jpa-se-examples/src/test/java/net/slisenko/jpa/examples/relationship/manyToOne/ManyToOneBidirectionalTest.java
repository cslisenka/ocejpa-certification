package net.slisenko.jpa.examples.relationship.manyToOne;

import junit.framework.Assert;
import net.slisenko.AbstractJpaTest;
import org.junit.Test;

import java.util.List;

public class ManyToOneBidirectionalTest extends AbstractJpaTest {

    @Test
    public void test() {
        em.getTransaction().begin();

        ItemGroup2 group = new ItemGroup2("group1");
        em.persist(group);

        ItemGroup2 group2 = new ItemGroup2("group2");
        em.persist(group2);

        Item2 item1 = new Item2("item1");
        item1.setGroup(group);
        em.persist(item1);

        Item2 item2 = new Item2("item2");
        item2.setGroup(group2);
        em.persist(item2);

        Item2 item3 = new Item2("item3");
        item3.setGroup(group2);
        em.persist(item3);

        em.getTransaction().commit();
        em.clear();

        List<ItemGroup2> groups = em.createQuery("from ItemGroup2", ItemGroup2.class).getResultList();
        for (ItemGroup2 oneGroup : groups) {
            System.out.format("Group %s\n", oneGroup.getName());
            for (Item2 item : oneGroup.getItems()) {
                System.out.format("Item %s\n", item.getName());
            }
        }
    }

    /**
     * If link is mappedby, we need to set group from Item side
     */
    @Test
    public void testAddToGroup() {
        em.getTransaction().begin();

        Item2 item1 = new Item2("item1");
        em.persist(item1);

        ItemGroup2 group = new ItemGroup2("group1");
        group.getItems().add(item1);
        em.persist(group);

        em.getTransaction().commit();
        em.clear();

        Assert.assertTrue(em.find(ItemGroup2.class, group.getId()).getItems().isEmpty());
        Assert.assertNull(em.find(Item2.class, item1.getId()).getGroup());
    }
}
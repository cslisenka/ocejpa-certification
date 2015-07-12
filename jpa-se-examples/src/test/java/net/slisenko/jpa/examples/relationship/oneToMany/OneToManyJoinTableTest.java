package net.slisenko.jpa.examples.relationship.oneToMany;

import net.slisenko.AbstractJpaTest;
import org.junit.Test;

import java.util.List;

public class OneToManyJoinTableTest extends AbstractJpaTest {

    @Test
    public void test() {
        em.getTransaction().begin();

        Item item1 = new Item("item1");
        em.persist(item1);

        Item item2 = new Item("item2");
        em.persist(item2);

        Item item3 = new Item("item3");
        em.persist(item3);

        ItemGroup group = new ItemGroup("group1");
        group.getItems().add(item1);
        em.persist(group);

        ItemGroup group2 = new ItemGroup("group2");
        // We can not add item1 because this is @OneToMany => to do this we should do @ManyToMany
        group2.getItems().add(item2);
        group2.getItems().add(item3);
        em.persist(group2);

        em.getTransaction().commit();
        em.close();

        em = emf.createEntityManager();
        List<ItemGroup> groups = em.createQuery("from ItemGroup", ItemGroup.class).getResultList();
        for (ItemGroup oneGroup : groups) {
            System.out.format("Group %s\n", oneGroup.getName());
            for (Item item : oneGroup.getItems()) {
                System.out.format("Item %s\n", item.getName());
            }
        }
    }
}

package net.slisenko.jpa.examples.relationship.collections.list;

import net.slisenko.AbstractJpaTest;
import org.junit.Test;

public class CollectionsExampleTest extends AbstractJpaTest{

    /**
     * Hibernate creates 3 tables.
     */
    @Test
    public void test() {
        em.getTransaction().begin();
        Task myTask = new Task("task1");
        myTask.getComments().add("comment 1");
        myTask.getComments().add("comment 2");
        myTask.getComments().add("comment 3");
        myTask.getAttachments().add(new Attachment("screenshot.jpg", 1400));
        myTask.getAttachments().add(new Attachment("screenshot2.jpg", 1800));
        em.persist(myTask);

        em.getTransaction().commit();
        em.clear();

        Task foundedTask = em.find(Task.class, myTask.getId());
//        System.out.println(foundedTask);
        System.out.format("Task => id: %s, name: %s\n", foundedTask.getId(), foundedTask.getName());
    }
}

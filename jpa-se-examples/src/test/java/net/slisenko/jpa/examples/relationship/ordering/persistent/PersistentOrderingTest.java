package net.slisenko.jpa.examples.relationship.ordering.persistent;

import net.slisenko.AbstractJpaTest;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class PersistentOrderingTest extends AbstractJpaTest {

    @Test
    public void test() {
        em.getTransaction().begin();

        PrintQueue queue = new PrintQueue();
        em.persist(queue);

        PrintJob job1 = new PrintJob("job1");
        em.persist(job1);
        PrintJob job2 = new PrintJob("job2");
        em.persist(job2);
        PrintJob job3 = new PrintJob("job3");
        em.persist(job3);
        PrintJob job4 = new PrintJob("job4");
        em.persist(job4);

        queue.getJobs().add(job1);
        queue.getJobs().add(job2);
        queue.getJobs().add(job3);
        queue.getJobs().add(job4);

        em.getTransaction().commit();
        em.clear();

        queue = em.find(PrintQueue.class, queue.getId());
        for (PrintJob job : queue.getJobs()) {
            System.out.println(job);
        }

        // Change order
        em.getTransaction().begin();

        List<PrintJob> newJobList = new ArrayList<>();
        newJobList.add(queue.getJobs().get(1));
        newJobList.add(queue.getJobs().get(0));
        newJobList.add(queue.getJobs().get(3));
        newJobList.add(queue.getJobs().get(2));
        queue.setJobs(newJobList);

        em.getTransaction().commit();
        em.clear();

        queue = em.find(PrintQueue.class, queue.getId());
        for (PrintJob job : queue.getJobs()) {
            System.out.println(job);
        }
    }
}

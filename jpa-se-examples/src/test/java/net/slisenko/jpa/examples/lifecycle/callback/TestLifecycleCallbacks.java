package net.slisenko.jpa.examples.lifecycle.callback;

import net.slisenko.AbstractJpaTest;
import org.junit.Test;

public class TestLifecycleCallbacks extends AbstractJpaTest {

    @Test
    public void test() {
        em.getTransaction().begin();
        EntityEventListener entityEventListener = new EntityEventListener();
        em.persist(entityEventListener);
        entityEventListener.setName("name");
        em.getTransaction().commit();
        em.clear();

        em.getTransaction().begin();
        EntityEventListener eventListener = em.find(EntityEventListener.class, entityEventListener.getId());
        em.remove(eventListener);
        em.getTransaction().commit();
    }
}
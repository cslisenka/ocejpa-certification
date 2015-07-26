package net.slisenko.jpa.examples.primarykey.shared;

import net.slisenko.AbstractJpaTest;
import org.junit.Test;

import java.util.List;

/**
 * When primary key includes foreign key to another entity
 * One entity is dependent (can not exist without another)
 */
public class SharedPrimaryKeyExampleTest extends AbstractJpaTest {

    @Test
    public void testSharedPrimaryKey() {
        em.getTransaction().begin();
        MainEntity me = new MainEntity();
        me.setName("me name");
        em.persist(me);
        em.getTransaction().commit();

        em.getTransaction().begin();
        DependentEntity de = new DependentEntity();
        de.setOwnName("my name");
        de.setMain(me);
        em.persist(de);
        em.getTransaction().commit();

        em.clear();

        List<DependentEntity> des = em.createQuery("SELECT de FROM DependentEntity de").getResultList();
        for (DependentEntity oneDe : des) {
            System.out.println(oneDe);
        }
    }
}
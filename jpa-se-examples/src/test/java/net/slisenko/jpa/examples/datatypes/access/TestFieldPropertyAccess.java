package net.slisenko.jpa.examples.datatypes.access;

import net.slisenko.AbstractJpaTest;
import org.junit.Test;

public class TestFieldPropertyAccess extends AbstractJpaTest {

    @Test
    public void test() {
        // TODO investigate why both field and property columns created
        em.getTransaction().begin();
        DifferentAccess da = new DifferentAccess();
        da.setProperty("property");
        da.setField("field");
        em.persist(da);
        em.getTransaction().commit();
        em.clear();

        DifferentAccess daFromDB = em.find(DifferentAccess.class, da.getId());
        System.out.println(daFromDB);
    }
}

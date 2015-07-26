package net.slisenko.jpa.examples.datatypes.readonly;

import junit.framework.Assert;
import net.slisenko.AbstractJpaTest;
import org.junit.Test;

import javax.persistence.PersistenceException;

public class ReadOnlyMappingsTest extends AbstractJpaTest {

    @Test
    public void testNotUpdatable() {
        em.getTransaction().begin();
        NotOptionalEntity nor = new NotOptionalEntity();
        em.persist(nor);
        em.getTransaction().commit();

        em.getTransaction().begin();
        ReadOnlyMappingsEntity rom = new ReadOnlyMappingsEntity();
        rom.setEntity(nor);
        rom.setNotUpdatable("insert");
        rom.setNotInsertable("not insert");
        em.persist(rom);
        em.getTransaction().commit();
        em.clear();

        p("Read entity and try to change not updatable value");
        em.getTransaction().begin();
        rom = em.find(ReadOnlyMappingsEntity.class, rom.getId());
        Assert.assertEquals("insert", rom.getNotUpdatable());
        Assert.assertEquals(null, rom.getNotInsertable());

        rom.setNotUpdatable("update");
        rom.setNotInsertable("not insert");
        em.getTransaction().commit();
        em.clear();

        p("Read entity and check not updatable value not changed");
        rom = em.find(ReadOnlyMappingsEntity.class, rom.getId());
        Assert.assertEquals("insert", rom.getNotUpdatable());
        Assert.assertEquals("not insert", rom.getNotInsertable());
    }

    @Test(expected = PersistenceException.class)
    public void testEmptyNotOptionalRelationship() {
        em.getTransaction().begin();
        em.persist(new ReadOnlyMappingsEntity());
        em.getTransaction().commit();
    }
}
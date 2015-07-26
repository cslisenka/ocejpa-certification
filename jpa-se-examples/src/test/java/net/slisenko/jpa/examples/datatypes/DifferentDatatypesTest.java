package net.slisenko.jpa.examples.datatypes;

import net.slisenko.AbstractJpaTest;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

/**
 * Test shows how JPA stores different data types
 */
public class DifferentDatatypesTest extends AbstractJpaTest {

    @Test
    public void test() {
        em.getTransaction().begin();

        EntityWithTypes entity = new EntityWithTypes();
        entity.setTime(new Date());
        entity.setBinaryData("my byte array".getBytes());
        entity.setDate(new Date());
        entity.setLongString("long string");
        entity.setNumberedEnum(MyEnumType.CITY);
        entity.setStringEnum(MyEnumType.COUNTRY);
        entity.setTimestamp(Calendar.getInstance());
        entity.setTransientString("transient string");
        entity.setTransientString2("transient string 2");
        entity.setEmbedded(new MyEmbeddedObject("Belarus", "Minsk"));

        em.persist(entity);
        em.getTransaction().commit();
        em.clear();

        EntityWithTypes result = em.find(EntityWithTypes.class, entity.getId());
        System.out.println(result);
    }
}
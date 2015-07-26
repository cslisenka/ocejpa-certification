package net.slisenko.jpa.examples.datatypes.converters;

import net.slisenko.AbstractJpaTest;
import org.junit.Test;

public class ConverterTest extends AbstractJpaTest {

    @Test
    public void testLoadStore() {
        em.getTransaction().begin();
        EntityWithConvertedField entity = new EntityWithConvertedField();
        entity.setConverterValue(true);
        entity.getConvertedValueCollection().add(true);
        entity.getConvertedValueCollection().add(false);
        em.persist(entity);

        EntityWithConvertedField entity2 = new EntityWithConvertedField();
        entity2.setConverterValue(false);
        em.persist(entity2);
        em.getTransaction().commit();
        em.clear();

        // Load values
        System.out.println(em.find(EntityWithConvertedField.class, entity.getId()));
        System.out.println(em.find(EntityWithConvertedField.class, entity2.getId()));
    }
}
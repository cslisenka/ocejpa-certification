package net.slisenko.jpa.examples.criteriaApi.metamodel;

import net.slisenko.AbstractJpaTest;
import net.slisenko.jpa.examples.datatypes.EntityWithTypes;
import net.slisenko.jpa.examples.relationship.manyToMany.Employee2;
import org.junit.Test;

import javax.persistence.metamodel.*;
import java.util.Set;

public class TestPrintMetamodel extends AbstractJpaTest {

    @Test
    public void testPrintMetamodel() {
        Metamodel model = em.getMetamodel();
        System.out.println("========== Entities ==========");
        Set<EntityType<?>> entities = model.getEntities();
        for (EntityType<?> entity : entities) {
            System.out.format("Entity: %s\n", entity.getName());
        }

        System.out.println("========== Embeddables ==========");
        Set<EmbeddableType<?>> embeddables = model.getEmbeddables();
        for (EmbeddableType<?> embeddable : embeddables) {
            System.out.format("Embeddable: %s\n", embeddable.getJavaType().getName());
        }

        System.out.println("========== ManagedTypes ==========");
        Set<ManagedType<?>> managedTypes = model.getManagedTypes();
        for (ManagedType<?> managedType : managedTypes) {
            System.out.format("ManagedType: %s\n", managedType.getJavaType().getName());
        }
    }

    @Test
    public void testPrintEntityAttributes() {
        Metamodel model = em.getMetamodel();
        EntityType<EntityWithTypes> entity = model.entity(EntityWithTypes.class);
        listAttributes(entity);

        EntityType<Employee2> entity2 = model.entity(Employee2.class);
        listAttributes(entity2);
    }

    public <T> void listAttributes(EntityType<T> type) {
        System.out.format("========== Entity: %s ==========\n", type.getName());
        for (Attribute<? super T, ?> attr : type.getAttributes()) {
            System.out.format("%s %s %s\n", attr.getName(), attr.getJavaType().getName(), attr.getPersistentAttributeType());
        }
    }

    // TODO sample criteria query using strong metamodel
}

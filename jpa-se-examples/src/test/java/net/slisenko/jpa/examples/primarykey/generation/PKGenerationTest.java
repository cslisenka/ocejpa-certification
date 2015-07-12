package net.slisenko.jpa.examples.primarykey.generation;

import net.slisenko.AbstractJpaTest;
import org.junit.Ignore;
import org.junit.Test;

import javax.persistence.PersistenceException;
import java.util.List;

public class PKGenerationTest extends AbstractJpaTest {

    /**
     * Manyally assigned IDs
     */
    @Test
    public void testNoIdGeneration() {
        em.getTransaction().begin();
        // Delete to avoid duplicated primery keys
        em.createQuery("delete from NoGenerationPKEntity").executeUpdate();
        em.persist(new NoGenerationPKEntity(1l, "no generation 1"));
        em.persist(new NoGenerationPKEntity(5l, "no generation 2"));
        em.persist(new NoGenerationPKEntity(10l, "no generation 3"));
        em.getTransaction().commit();
        em.close();

        em = emf.createEntityManager();
        List<NoGenerationPKEntity> list = em.createQuery("from NoGenerationPKEntity e").getResultList();
        for (NoGenerationPKEntity ent : list) {
            System.out.format("id: %d, name: %s\n", ent.getId(), ent.getName());
        }
    }

    /**
     * For MySQL sequence doesn't work:
     * "Could not instantiate id generator [entity-name=net.slisenko.jpa.examples.primarykey.generation.SequencePKEntity"
     * "org.hibernate.dialect.MySQLDialect does not support sequences"
     */
    // TODO use postgre SQL for real sequences
    @Ignore
    @Test
    public void testSequencePKGeneration() {
        em.getTransaction().begin();
        em.persist(new SequencePKEntity("sequence1"));
        em.persist(new SequencePKEntity("sequence2"));
        em.persist(new SequencePKEntity("sequence3"));
        em.getTransaction().commit();
        em.close();

        em = emf.createEntityManager();
        List<SequencePKEntity> list = em.createQuery("from SequencePKEntity e").getResultList();
        for (SequencePKEntity ent : list) {
            System.out.format("id: %d, name: %s\n", ent.getId(), ent.getName());
        }
    }

    @Test(expected = PersistenceException.class)
    public void testSequencePKManual() {
        em.getTransaction().begin();
        // Check manually assigned id
        SequencePKEntity manuallyId = new SequencePKEntity("manual id");
        manuallyId.setId(100l);
        em.persist(manuallyId);
        em.getTransaction().commit();
    }

    // TODO try set ID manually and see what happens

    /**
     * Hibernate gets last sequence value from hibernate_sequences table.
     *
     * Actually Entity id = last_sequence_value * 32768. (May be this is a hibernate optimization and can be changed in settings)
     */
    @Test
    public void testTablePKGeneration() {
        em.getTransaction().begin();
        em.persist(new TablePKEntity("table1"));
        em.persist(new TablePKEntity("table2"));
        TablePKEntity last = new TablePKEntity("table3");
        em.persist(last);
        em.getTransaction().commit();

        em.close();
        em = emf.createEntityManager();
        List<TablePKEntity> list = em.createQuery("from TablePKEntity").getResultList();
        for (TablePKEntity ent : list) {
            System.out.format("id: %d, name: %s\n", ent.getId(), ent.getName());
        }
    }

    @Test(expected = PersistenceException.class)
    public void testTablePKManual() {
        em.getTransaction().begin();
        // Check manually assigned id
        TablePKEntity manuallyId = new TablePKEntity("manual id");
        manuallyId.setId(100l);
        em.persist(manuallyId);
        em.getTransaction().commit();
    }

    @Test
    public void testIdentityPKGeneration() {
        em.getTransaction().begin();
        em.persist(new IdentityPKEntity("identity1"));
        em.persist(new IdentityPKEntity("identity2"));
        IdentityPKEntity last = new IdentityPKEntity("identity3");
        em.persist(last);
        em.getTransaction().commit();
        em.close();

        em = emf.createEntityManager();
        List<IdentityPKEntity> list = em.createQuery("from IdentityPKEntity").getResultList();
        for (IdentityPKEntity ent : list) {
            System.out.format("id: %d, name: %s\n", ent.getId(), ent.getName());
        }
    }

    @Test(expected = PersistenceException.class)
    public void testIdentityPKManual() {
        em.getTransaction().begin();
        // Check manually assigned id
        IdentityPKEntity manuallyId = new IdentityPKEntity("manual id");
        manuallyId.setId(100l);
        em.persist(manuallyId);
        em.getTransaction().commit();
    }

    /**
     * In MySQL auth is auto_increment
     */
    @Test
    public void testAutoPKGeneration() {
        em.getTransaction().begin();
        em.persist(new AutoPKEntity("auto1"));
        em.persist(new AutoPKEntity("auto2"));
        em.persist(new AutoPKEntity("auto3"));
        em.getTransaction().commit();
        em.close();

        em = emf.createEntityManager();
        List<AutoPKEntity> list = em.createQuery("from AutoPKEntity").getResultList();
        for (AutoPKEntity ent : list) {
            System.out.format("id: %d, name: %s\n", ent.getId(), ent.getName());
        }
    }

    @Test(expected = PersistenceException.class)
    public void testAutoPKManual() {
        em.getTransaction().begin();
        // Check manually assigned id
        AutoPKEntity manuallyId = new AutoPKEntity("manual id");
        manuallyId.setId(100l);
        em.persist(manuallyId);
        em.getTransaction().commit();
    }
}
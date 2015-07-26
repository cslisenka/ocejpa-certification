package net.slisenko.jpa.examples.queries.nativesql;

import net.slisenko.AbstractJpaTest;
import net.slisenko.jpa.examples.JPAUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Sometimes we need support of native DB features (JPQL is too common and abstract):
 * 1. Stored procedures
 * 2. Views
 * We can use native SQL for performance reasons.
 *
 * Native SQL over JDBC:
 * 1. Results can be wrapped into objects
 * 2. We use same interfaces (Query, EntityManager), not PreparedStatement, ...
 * 3. Provider can use common cache with regular JPA entities
 *
 * We can construct new entity instances from tables which do not have mappings
 */

public class NativeSQLExample extends AbstractJpaTest {

    @Before
    public void init() {
        em.getTransaction().begin();
        em.persist(new NativeEmployee("native employee 1"));
        em.persist(new NativeEmployee("native employee 2"));
        em.persist(new NativeEmployee("native employee 3"));
        em.getTransaction().commit();
        em.clear();
    }

    /**
     * 1. No mapping provided
     * 2. Entities do not become managed in Persistence Context
     */
    @Test
    public void testNativeSQLWithoutMapping() {
        List nativeQueryResults = em.createNativeQuery("SELECT * FROM native_employee_table").getResultList();
        p(nativeQueryResults);
        JPAUtil.printContext(em);
    }

    /**
     * 1. Mapping to entity works
     * 2. Returned entities become managed with persistence context
     */
    @Test
    public void testNativeQueryWithMapping() {
        List<NativeEmployee> nativeEmployees = em.createNativeQuery("SELECT * FROM native_employee_table", NativeEmployee.class).getResultList();
        p(nativeEmployees);
        JPAUtil.printContext(em);

        nativeEmployees = em.createNamedQuery("getAllNativeEmployees", NativeEmployee.class).getResultList();
        p(nativeEmployees);
        JPAUtil.printContext(em);
    }

    /**
     * Any native SQL query can be mapped to several JPA entities
     */
    @Test
    public void testSqlResultSetMapping() {
        em.getTransaction().begin();
        EntityRSMapping rsMapping = new EntityRSMapping("rs mapping");
        EntityRSRelationship rsRelationship = new EntityRSRelationship("rs relationship");
        rsMapping.setRelationship(rsRelationship);
        em.persist(rsMapping);
        em.getTransaction().commit();
        em.clear();

        // Get by native sql
        // Test mapping 1
        List results = em.createNativeQuery("SELECT * FROM entity_rs_main", "oneEntity").getResultList();
        System.out.println(results);

        List results2 = em.createNativeQuery("SELECT main.id, main.entityParam, rel.relParam FROM entity_rs_main AS main, entity_rs_rel AS rel WHERE main.relationship_id = rel.id", "bothEntities").getResultList();
        System.out.println(results2);
    }
}

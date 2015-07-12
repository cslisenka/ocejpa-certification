package net.slisenko.jpa.examples.criteriaApi;

import junit.framework.Assert;
import net.slisenko.AbstractJpaTest;
import net.slisenko.jpa.examples.criteriaApi.model.CrAddress;
import net.slisenko.jpa.examples.criteriaApi.model.CrEmployee;
import net.slisenko.jpa.examples.criteriaApi.model.CrProject;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.List;

public class TestCriteriaAPI extends AbstractJpaTest {

    @Before
    public void initData() {
        // Clean existed data
        em.getTransaction().begin();
        em.createQuery("DELETE FROM CrEmployee").executeUpdate();
        em.createQuery("DELETE FROM CrAddress").executeUpdate();
        em.createQuery("DELETE FROM CrProject").executeUpdate();
        em.createQuery("DELETE FROM CrPhone").executeUpdate();
        em.getTransaction().commit();

        em.getTransaction().begin();
        CrEmployee employee = new CrEmployee("Anna", 1000);
        CrEmployee employee2 = new CrEmployee("Elena", 3000);
        CrAddress address = new CrAddress("Minsk", "Nemiga", "1");
        employee.getProjects().add(new CrProject("project1"));
        employee2.getProjects().add(new CrProject("project2"));
        employee.setAddress(address);
        employee2.setAddress(address);

        em.persist(employee);
        em.persist(employee2);
        em.getTransaction().commit();
    }

    @Test
    public void testSimpleQuery() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CrEmployee> cq = cb.createQuery(CrEmployee.class); // Result of query
        Root<CrEmployee> empRoot = cq.from(CrEmployee.class); // Query root, can be many roots
        // Build query
        cq.select(empRoot)
          .where(cb.equal(empRoot.get("name"), "Anna"));

        // Create JPA query from criteria Query
        TypedQuery<CrEmployee> jpaQuery = em.createQuery(cq);
        List<CrEmployee> results = jpaQuery.getResultList();
        System.out.println(results);
        Assert.assertEquals(1, results.size());
    }

    @Test
    public void testManyRoots() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CrAddress> cq = cb.createQuery(CrAddress.class); // Query returns CrAddress objects

        // Define query roots
        Root<CrAddress> rootAddress = cq.from(CrAddress.class);
        Root<CrEmployee> rootEmployee = cq.from(CrEmployee.class);

        // Build query
        // If not distinct, we will get cartesian product
        cq.select(rootAddress).distinct(true).where(cb.equal(rootAddress, rootEmployee.get("address")));

        TypedQuery<CrAddress> jpaQuery = em.createQuery(cq);
        List<CrAddress> results = jpaQuery.getResultList();
        System.out.println(results);
        Assert.assertEquals(1, results.size());
    }

    @Test
    public void testPathExpressions() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CrEmployee> cq = cb.createQuery(CrEmployee.class);
        Root<CrEmployee> rootEmployee = cq.from(CrEmployee.class);
        cq.select(rootEmployee).where(cb.equal(rootEmployee.get("address").get("city"), "Minsk"));

        TypedQuery<CrEmployee> jpaQuery = em.createQuery(cq);
        List<CrEmployee> results = jpaQuery.getResultList();
        System.out.println(results);
        Assert.assertEquals(2, results.size());
    }

    @Test
    public void selectPrimitiveType() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> cq = cb.createQuery(String.class);
        Root<CrEmployee> rootEmployee = cq.from(CrEmployee.class);
        cq.select(rootEmployee.<String> get("name"));

        TypedQuery<String> jpaQuery = em.createQuery(cq);
        List<String> results = jpaQuery.getResultList();
        System.out.println(results);
        Assert.assertEquals(2, results.size());
    }

    @Test
    public void testReturnMultipleTypesUsingTyple() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> cq = cb.createTupleQuery();
        Root<CrEmployee> rootEmployee = cq.from(CrEmployee.class);
        cq.select(cb.tuple(rootEmployee.get("name").alias("nm"), rootEmployee.get("salary")));

        TypedQuery<Tuple> jpaQuery = em.createQuery(cq);
        List<Tuple> results = jpaQuery.getResultList();
        for (Tuple row : results) {
            // We can get alues by aliases
            System.out.println(row.get(0) + "(" + row.get("nm") +  ")" + row.get(1) );
        }
        Assert.assertEquals(2, results.size());
    }

    /**
     * Result contains many values
     */
    @Test
    public void testMultiselect() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
        Root<CrEmployee> rootEmployee = cq.from(CrEmployee.class);
        cq.multiselect(rootEmployee.get("name"), rootEmployee.get("salary"));

        List<Object[]> results = em.createQuery(cq).getResultList();
        for (Object[] res : results) {
            System.out.println(res[0] + " " + res[1]);
        }
    }

    /**
     * Get addresses of all employees which work
     */
    @Test
    public void testJoins() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CrAddress> cq = cb.createQuery(CrAddress.class);
        Root<CrEmployee> rootEmployee = cq.from(CrEmployee.class);
        Join<CrEmployee, CrProject> employeeToProjects = rootEmployee.join("projects");
        Join<CrEmployee, CrAddress> employeeToAddress = rootEmployee.join("address");
        cq.select(employeeToAddress);
        cq.where(cb.equal(employeeToProjects.get("name"), "project1"));

        List<CrAddress> addresses = em.createQuery(cq).getResultList();
        System.out.println(addresses);
    }

    // TODO test join ON

    // TODO test map join

    // TODO test predicates and parameters

    // TODO subqueries

    // TODO hierarchies and downcasting

    // TODO order by

    /**
     * Average salary by city
     */
    @Test
    public void testGroupByHaving() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
        Root<CrEmployee> employeeRoot = cq.from(CrEmployee.class);
        Join<CrEmployee, CrAddress> employeeToAddress = employeeRoot.join("address");
        cq.multiselect(cb.avg(employeeRoot.<Integer>get("salary")), cb.count(employeeRoot), employeeToAddress.get("city"))
            .groupBy(employeeToAddress.get("city"))
            .having(cb.ge(cb.avg(employeeRoot.<Integer>get("salary")), 100));

        List<Object[]> result = em.createQuery(cq).getResultList();
        for (Object[] row : result) {
            System.out.format("%s, %s, %s", row[0], row[1], row[2]);
        }
    }

    // TODO bulk update/delete
}
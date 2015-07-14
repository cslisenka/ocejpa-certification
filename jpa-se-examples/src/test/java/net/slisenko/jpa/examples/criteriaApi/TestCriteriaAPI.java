package net.slisenko.jpa.examples.criteriaApi;

import junit.framework.Assert;
import net.slisenko.jpa.examples.criteriaApi.model.CrAddress;
import net.slisenko.jpa.examples.criteriaApi.model.CrEmployee;
import net.slisenko.jpa.examples.criteriaApi.model.CrEmployeeAverage;
import org.junit.Test;

import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.List;

public class TestCriteriaAPI extends BaseCriteriaAPITest {

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
            // We can get values by aliases
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
     * Return multiple values constructing object
     */
    @Test
    public void testConstructAveragesObjectInQuery() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CrEmployeeAverage> c = cb.createQuery(CrEmployeeAverage.class);
        Root<CrEmployee> employeeRoot = c.from(CrEmployee.class);
        c.select(cb.construct(CrEmployeeAverage.class, employeeRoot.get("salary"), employeeRoot.get("age")));

        List<CrEmployeeAverage> employeeData = em.createQuery(c).getResultList();
        System.out.println(employeeData);
    }

    @Test
    public void testPredicatesAndParameters() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CrEmployee> cq = cb.createQuery(CrEmployee.class);
        Root<CrEmployee> empRoot = cq.from(CrEmployee.class);
        Predicate criteria = cb.conjunction();
        criteria = cb.and(criteria, cb.equal(empRoot.get("salary"), 1000));

        ParameterExpression<Integer> p = cb.parameter(Integer.class, "age");

        criteria = cb.and(criteria, cb.equal(empRoot.get("age"), p));
        cq.where(criteria);

        List<CrEmployee> employees = em.createQuery(cq).setParameter("age", 25).getResultList();
        System.out.println(employees);
    }

    @Test
    public void testSubqueries() {
        // Get all employees where age > average
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CrEmployee> cq = cb.createQuery(CrEmployee.class);
        Root<CrEmployee> empRoot = cq.from(CrEmployee.class);
        cq.select(empRoot);

        Subquery<Double> sq = cq.subquery(Double.class);
        Root<CrEmployee> sqEmpRoot = sq.from(CrEmployee.class);
        sq.select(cb.avg(sqEmpRoot.<Double>get("age")));

        cq.where(cb.ge(empRoot.<Integer>get("age"), sq));

        List<CrEmployee> olderThanAverage = em.createQuery(cq).getResultList();
        System.out.println(olderThanAverage);
    }

    @Test
    public void testInExpression() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CrEmployee> cq = cb.createQuery(CrEmployee.class);
        Root<CrEmployee> empRoot = cq.from(CrEmployee.class);
        cq.select(empRoot).where(cb.in(empRoot.get("age")).value(25).value(30));

        List<CrEmployee> employees = em.createQuery(cq).getResultList();
        System.out.println(employees);
    }

    // TODO hierarchies and downcasting

    @Test
    public void testOrderBy() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CrEmployee> cq = cb.createQuery(CrEmployee.class);
        Root<CrEmployee> empRoot = cq.from(CrEmployee.class);
        cq.select(empRoot).orderBy(cb.desc(empRoot.get("age")));

        List<CrEmployee> employees = em.createQuery(cq).getResultList();
        System.out.println(employees);
    }
}
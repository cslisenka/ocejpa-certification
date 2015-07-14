package net.slisenko.jpa.examples.criteriaApi;

import net.slisenko.jpa.examples.criteriaApi.model.CrAddress;
import net.slisenko.jpa.examples.criteriaApi.model.CrEmployee;
import net.slisenko.jpa.examples.criteriaApi.model.CrEmployeeAverage;
import org.junit.Test;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import java.util.List;

public class TestCriteriaApiGroupByAggregations extends BaseCriteriaAPITest {

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
}

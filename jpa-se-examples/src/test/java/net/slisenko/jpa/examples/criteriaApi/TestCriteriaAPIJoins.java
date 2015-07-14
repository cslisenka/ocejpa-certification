package net.slisenko.jpa.examples.criteriaApi;

import net.slisenko.jpa.examples.criteriaApi.model.CrAddress;
import net.slisenko.jpa.examples.criteriaApi.model.CrEmployee;
import net.slisenko.jpa.examples.criteriaApi.model.CrProject;
import org.junit.Test;

import javax.persistence.criteria.*;
import java.util.List;

public class TestCriteriaAPIJoins extends BaseCriteriaAPITest {

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

    public void testJoinOn() {
        // TODO test join ON
    }

    @Test
    public void testMapJoin() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object> cq = cb.createQuery();
        Root<CrProject> projectRoot = cq.from(CrProject.class);
        MapJoin<CrProject, String, String> join = projectRoot.joinMap("tasks");
        cq.multiselect(projectRoot.get("name"), join.key(), join.value());

//        List<Object[]> results = em.createQuery("SELECT DISTINCT p.name, KEY(p.tasks), VALUE(p.tasks) FROM CrProject p")
//                .getResultList();
//        for (Object[] row : results) {
//            System.out.format("%s, %s, %s \n", row[0], row[1], row[2]);
//        }
        // TODO clarify why happens "ERROR:  node did not reference a map"

        List<Object> rows = em.createQuery(cq).getResultList();
//        for (Object[] row : rows) {
//            System.out.format("%s, %s, %s", row[0], row[1], row[2]);
////            System.out.format("%s", row[0]);
//        }
    }

    @Test
    public void testFetchJoins() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CrEmployee> cq = cb.createQuery(CrEmployee.class);
        Root<CrEmployee> employeeRoot = cq.from(CrEmployee.class);
        employeeRoot.fetch("address");
        cq.select(employeeRoot);

        List<CrEmployee> employees = em.createQuery(cq).getResultList();
        System.out.println(employees);
    }
}
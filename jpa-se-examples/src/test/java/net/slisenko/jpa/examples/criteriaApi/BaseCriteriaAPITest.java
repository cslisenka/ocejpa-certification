package net.slisenko.jpa.examples.criteriaApi;

import net.slisenko.AbstractJpaTest;
import net.slisenko.jpa.examples.criteriaApi.model.CrAddress;
import net.slisenko.jpa.examples.criteriaApi.model.CrEmployee;
import net.slisenko.jpa.examples.criteriaApi.model.CrProject;
import org.junit.Before;

import java.util.List;

public class BaseCriteriaAPITest extends AbstractJpaTest {

    @Before
    public void initData() {
        // Clean existed data
        em.getTransaction().begin();
        em.createQuery("DELETE FROM CrEmployee").executeUpdate();
        em.createQuery("DELETE FROM CrAddress").executeUpdate();
        List<CrProject> projects = em.createQuery("SELECT p FROM CrProject p").getResultList();
        for (CrProject p : projects) {
            em.remove(p);
        }
        em.createQuery("DELETE FROM CrPhone").executeUpdate();
        em.getTransaction().commit();

        em.getTransaction().begin();
        CrEmployee employee = new CrEmployee("Anna", 1000);
        employee.setAge(25);

        CrEmployee employee2 = new CrEmployee("Elena", 3000);
        employee2.setAge(40);

        CrAddress address = new CrAddress("Minsk", "Nemiga", "1");
        CrProject pr1 = new CrProject("project1");
        pr1.getTasks().put("task1", "task 1 description");
        pr1.getTasks().put("task2", "task 2 description");
        CrProject pr2 = new CrProject("project2");
        pr2.getTasks().put("task3", "task 3 description");
        pr2.getTasks().put("task4", "task 4 description");

        employee.getProjects().add(pr1);
        employee2.getProjects().add(pr2);
        employee.setAddress(address);
        employee2.setAddress(address);

        em.persist(employee);
        em.persist(employee2);
        em.getTransaction().commit();
    }
}

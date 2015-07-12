package net.slisenko.jpa.examples.relationship.manyToMany;

import net.slisenko.AbstractJpaTest;
import org.junit.Test;

import java.util.List;

public class TestManyToMany extends AbstractJpaTest {

    @Test
    public void test() {
        em.getTransaction().begin();

        Employee2 emp1 = new Employee2("emp1");
        em.persist(emp1);

        Employee2 emp2 = new Employee2("emp2");
        em.persist(emp2);

        Project2 p1 = new Project2("project1");
        p1.getEmployees().add(emp1);
        p1.getEmployees().add(emp2);
        em.persist(p1);

        Project2 p2 = new Project2("project2");
        p2.getEmployees().add(emp2);
        em.persist(p2);

        em.getTransaction().commit();

        // Test get employees by projects
        em.clear();
        System.out.println("Employees by projects");
        List<Project2> projects = em.createQuery("from Project2", Project2.class).getResultList();
        for (Project2 oneProject : projects) {
            System.out.println(oneProject);
            for (Employee2 emp : oneProject.getEmployees()) {
                System.out.println(emp);
            }
        }

        // Test get projects by employees
        em.clear();
        System.out.println("Projects by employees");
        List<Employee2> employees = em.createQuery("from Employee2", Employee2.class).getResultList();
        for (Employee2 emp : employees) {
            System.out.println(emp);
            for (Project2 oneProject : emp.getProjects()) {
                System.out.println(oneProject);
            }
        }
    }

    /**
     * If we add entities not from owner side, we do not add links!
     */
    @Test
    public void testAddLinkOnNotOwnerSide() {
        em.getTransaction().begin();

        Project2 pr = new Project2("project2");
        em.persist(pr);

        Employee2 emp = new Employee2("emp2");
        emp.getProjects().add(pr);
        em.persist(emp);

        em.clear();
        System.out.println("Employees by projects");
        Project2 project = em.find(Project2.class, pr.getId());
        System.out.println(project);
        for (Employee2 oneEmp : project.getEmployees()) {
            System.out.println(oneEmp);
        }

        em.getTransaction().commit();
    }

//    @Test
//    public void testManyToManyEmbeddableKey() {
//        em.getTransaction().begin();
//
//        Employee emp = new Employee();
//        em.persist(emp);
//
//        AccessCard card = new AccessCard();
//        em.persist(card);
//
//        AccessCard card2 = new AccessCard();
//        em.persist(card2);
//
//        emp.getAccessCards().put(card, new AccessConstraint("room1", true));
//        emp.getAccessCards().put(card2, new AccessConstraint("conference", true));
//
//        em.persist(emp);
//
//        em.getTransaction().commit();
//
//        em.detach(emp);
//
//        Employee me = em.createQuery("from Employee e where e.id = '" + emp.getId() + "'", Employee.class).getSingleResult();
//        for (AccessCard myCard : me.getAccessCards().keySet()) {
//            System.out.format("For room %s access granted %b with access card %s", me.getAccessCards().get(myCard).getRoomId(),
//                me.getAccessCards().get(myCard).isGranted(), myCard);
//        }
//    }
}
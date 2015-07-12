package net.slisenko.jpa.examples.relationship;

import net.slisenko.AbstractJpaTest;

public class RelationshipsTest extends AbstractJpaTest {

//    @Ignore
//    @Test
//    public void testFillData() {
//        em.getTransaction().begin();
//
//        Department hr = new Department("hr");
//        em.persist(hr);
//        Department management = new Department("management");
//        em.persist(management);
//        Employee kostya = new Employee("Kostya", "Slisenko", "boss", EmployeeType.FULL_TIME, Gender.MALE);
//        em.persist(kostya);
//        AccessCard cardForKostya = new AccessCard();
//        em.persist(cardForKostya);
//        Employee vasya = new Employee("Vasya", "Ivanov", "likes sweets", EmployeeType.PART_TIME, Gender.MALE);
//        em.persist(vasya);
//        Employee anna = new Employee("Anna", "Gatzlovskih", "likes ", EmployeeType.PART_TIME, Gender.MALE);
//        em.persist(anna);
//        AccessCard cardForVasya = new AccessCard();
//        em.persist(cardForVasya);
//        Task t1 = new Task("Prepare business plan");
//        em.persist(t1);
//        Task t2 = new Task("Calculate budget");
//        em.persist(t2);
//        Task t3 = new Task("Hire more people");
//        em.persist(t3);
//        Project project1 = new Project("Project 1");
//        em.persist(project1);
//        Project project2 = new Project("Project 2");
//        em.persist(project2);
//        Phone phone1 = new Phone("12321");
//        em.persist(phone1);
//        Phone phone2 = new Phone("334334");
//        em.persist(phone2);
//
//        vasya.setDepartment(hr);
//        anna.setDepartment(hr);
//        kostya.setDepartment(management);
//
//        kostya.getPhones().add(phone1);
//        kostya.getPhones().add(phone2);
//
//        em.persist(vasya);
//        em.persist(anna);
//        em.persist(kostya);
//        em.persist(hr);
//        em.persist(management);
//
//        // TODO this does not work
////        hr.getEmployees().add(vasya);
////        hr.getEmployees().add(anna);
////        management.getEmployees().add(kostya);
//
//        kostya.setAccessCard(cardForKostya);
//        vasya.setAccessCard(cardForVasya);
//
//        t1.setOwner(kostya);
//        t2.setOwner(kostya);
//        t3.setOwner(vasya);
//
//        // Assign projects to people
//        // Project is owner of link, so we need to use relationship from project side
//        project1.getWorkers().add(kostya);
//        project1.getWorkers().add(anna);
//        project1.getWorkers().add(vasya);
//        em.persist(project1);
//
//        project2.getWorkers().add(kostya);
//        project2.getWorkers().add(anna);
//        em.persist(project2);
//
//        em.getTransaction().commit();
//    }
//
//    @Test
//    public void testReadDepartmentsLazyForEmployees() {
//        List<Department> deps = em.createQuery("from Department", Department.class).getResultList();
//        for (Department dep : deps) {
//            System.out.format("Department '%s'\n", dep.getName());
//        }
//    }
//
//    @Test
//    public void testReadDepartmentsAndEmployees() {
//        List<Department> deps = em.createQuery("from Department", Department.class).getResultList();
//        // Read many to one relationship
//        for (Department dep : deps) {
//            System.out.format("Department '%s' employees: %d \n", dep.getName(), dep.getEmployees().size());
//        }
//    }
//
//    @Test
//    public void testReadProjectsAndEagerEmployees() {
//        List<Project> projects = em.createQuery("from Project", Project.class).getResultList();
//        // Read many to one relationship
//        for (Project proj : projects) {
//            System.out.format("Project '%s'\n", proj.getName());
//        }
//    }
//
//    @Test
//    public void testReadEmployees() {
//        List<Employee> employees = em.createQuery("from Employee", Employee.class).getResultList();
//        // Read many to one relationship
//        for (Employee empl : employees) {
//            System.out.format("Employee '%s'\n", empl.getName());
//        }
//
//        // Get lazy loaded AccessCard
//        System.out.format("Employee access card ID %d\n", employees.get(0).getAccessCard().getId());
//    }
}

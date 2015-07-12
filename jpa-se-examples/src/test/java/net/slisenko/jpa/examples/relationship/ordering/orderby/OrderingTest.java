package net.slisenko.jpa.examples.relationship.ordering.orderby;

import net.slisenko.AbstractJpaTest;
import org.junit.Test;

public class OrderingTest extends AbstractJpaTest {

    @Test
    public void test() {
        em.getTransaction().begin();
        School school = new School();
        em.persist(school);

        em.persist(new Student("Kostya", "Slisenko", 7.6, school));
        em.persist(new Student("Ivan", "Petrov", 4.5, school));
        em.persist(new Student("Nikita", "Smirnov", 9.1, school));
        em.persist(new Student("Aliaksei", "Grigorenko", 6.8, school));
        em.persist(new Student("Zanna", "Gatzlovskih", 7.6, school));
        em.persist(new Student("Nancy", "Cooper", 7.6, school));
        em.persist(new Student("Julia", "Smith", 6.1, school));

        em.getTransaction().commit();
        em.clear();

        School founded = em.find(School.class, school.getId());

        System.out.println("Not sorted");
        for (Student stud : founded.getStudents()) {
            System.out.println(stud);
        }

        System.out.println("Sorted by name");
        for (Student stud : founded.getStudentsByName()) {
            System.out.println(stud);
        }

        System.out.println("Sorted by best performance and name");
        for (Student stud : founded.getStudentsByNameAndBestPerformance()) {
            System.out.println(stud);
        }
    }
}
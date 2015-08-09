package net.slisenko.jpa.examples.queries.jpql;

import net.slisenko.AbstractJpaTest;
import net.slisenko.jpa.examples.queries.*;
import org.junit.Before;

import java.util.List;

public class BaseJPQLTest extends AbstractJpaTest {

    protected City city;
    protected House h1;

    @Before
    public void initData() {
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Customer c").executeUpdate();
        em.createQuery("DELETE FROM House c").executeUpdate();
        em.createQuery("DELETE FROM Street c").executeUpdate();
        List<City> cities = em.createQuery("SELECT c FROM City c").getResultList();
        for (City c : cities) {
            em.remove(c);
        }
        em.getTransaction().commit();

        em.getTransaction().begin();

        city = new City("city1");
        city.getCityServicesNumbers().put("101", "fire");
        city.getCityServicesNumbers().put("102", "police");
        city.getCityServicesNumbers().put("103", "ambulance");

        em.persist(city);

        City city2 = new City("city2");
        em.persist(city2);

        Street street = new Street("my street");
        street.setCity(city);
        em.persist(street);

        Street street2 = new Street("my street 2");
        street2.setCity(city2);
        em.persist(street2);

        Street street3 = new Street("my street 3");
        street3.setCity(city2);
        em.persist(street3);

        h1 = new House("h1");
        h1.setStreet(street2);
        h1.setPrice(50000);
        h1.setFloors(5);
        em.persist(h1);

        House h2 = new House("house2");
        h2.setStreet(street);
        h2.setPrice(20000);
        h2.setFloors(2);
        em.persist(h2);

        House h3 = new House("house3");
        h3.setStreet(street2);
        h3.setPrice(80000);
        h3.setFloors(8);
        em.persist(h3);

        House h4 = new House("h4");
        h4.setStreet(street);
        h4.setPrice(40000);
        h4.setFloors(4);
        em.persist(h4);

        // Create house without streets
        House h5 = new House("h5 no street");
        h5.setFloors(40);
        em.persist(h5);

        // Customers
        Customer c1 = new Customer();
        c1.setName("Kostya");
        c1.setBudget(60000);
        em.persist(c1);

        Customer c2 = new Customer();
        c2.setName("John");
        c2.setBudget(45000);
        em.persist(c2);

        Customer c3 = new Customer();
        c3.setName("John 2");
        c3.setBudget(60000);
        em.persist(c3);

        // Different types of customer
        PersonCustomer pk1 = new PersonCustomer();
        pk1.setName("Anna");
        pk1.setSecurityNumber("123456");
        em.persist(pk1);

        OrganizationCustomer ok1 = new OrganizationCustomer();
        ok1.setTaxId(100);
        ok1.setName("Global enterprices");
        em.persist(ok1);

        em.getTransaction().commit();
        em.clear();
    }
}
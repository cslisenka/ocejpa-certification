package net.slisenko.jpa.examples.queries.jpql;

import net.slisenko.AbstractJpaTest;
import net.slisenko.jpa.examples.queries.City;
import net.slisenko.jpa.examples.queries.House;
import net.slisenko.jpa.examples.queries.Street;
import org.junit.Before;

public class BaseJPQLTest extends AbstractJpaTest {

    protected City city;

    @Before
    public void initData() {
        em.getTransaction().begin();

        city = new City("city1");
        em.persist(city);

        City city2 = new City("city2");
        em.persist(city2);

        Street street = new Street("my street");
        street.setCity(city);
        em.persist(street);

        Street street2 = new Street("my street 2");
        street2.setCity(city2);
        em.persist(street2);

        House h1 = new House("h1");
        h1.setStreet(street);
        h1.setFloors(5);
        em.persist(h1);

        House h2 = new House("h1");
        h2.setStreet(street);
        h2.setFloors(2);
        em.persist(h2);

        House h3 = new House("h2");
        h3.setStreet(street2);
        h3.setFloors(8);
        em.persist(h3);

        House h4 = new House("h3");
        h4.setStreet(street);
        h4.setFloors(4);
        em.persist(h4);

        em.getTransaction().commit();
        em.clear();
    }
}
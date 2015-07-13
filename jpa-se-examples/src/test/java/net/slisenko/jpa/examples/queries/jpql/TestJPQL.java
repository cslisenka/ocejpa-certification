package net.slisenko.jpa.examples.queries.jpql;

import net.slisenko.jpa.examples.queries.*;
import org.junit.Test;

import javax.persistence.TypedQuery;
import java.util.List;

public class TestJPQL extends BaseJPQLTest {

    @Test
    public void testQuerySimpleType() {
        em.getTransaction().begin();
        em.persist(new House("house1"));
        em.persist(new House("house2"));
        em.persist(new House("house3"));

        em.getTransaction().commit();
        em.clear();

        // SELECT <object to return> FROM <object expression>
        List<String> results = em.createQuery("SELECT h.name FROM House h", String.class).getResultList();
        for (String name : results) {
            System.out.println(name);
        }
    }

    @Test
    public void testQueryRelationAndFiltering() {
        System.out.println("<streets with houses which have specific name>");
        List<Street> streetsWithH1Houses = em.createQuery("SELECT h.street FROM House h WHERE h.name='h1'", Street.class).getResultList();
        System.out.println(streetsWithH1Houses);

        System.out.println("<middle houses on specific street>");
        List<House> middleHousesOnSpecificStreet = em.createQuery("SELECT h FROM House h WHERE h.street.name = 'my street' AND h.floors IN (5, 4)").getResultList();
        System.out.println(middleHousesOnSpecificStreet);

        System.out.println("<projecting results>");
        List results = em.createQuery("SELECT h.name, h.street.name FROM House h").getResultList();
        for (Object result : results) {
            Object[] resultStrings = (Object[]) result;
            System.out.format("House: %s, Street: %s\n", resultStrings[0], resultStrings[1]);
        }

        // We can not return Collection type
        System.out.println("<automatic join - all floors of houses in specific city>");
        List<Integer> floorsPerCity = em.createQuery("SELECT h.floors FROM House h, Street s WHERE h.street = s AND s.city.id='" + city.getId() + "'", Integer.class).getResultList();
        for (Integer floors : floorsPerCity) {
            System.out.println(floors);
        }

        System.out.println("<manual join - all floors of houses in specific city>");
        floorsPerCity = em.createQuery("SELECT h.floors FROM Street s JOIN s.houses h WHERE s.city.id = '" + city.getId() + "'", Integer.class).getResultList();
        for (Integer floors : floorsPerCity) {
            System.out.println(floors);
        }
    }

    /**
     * JPQL aggregation functions: AVG, COUNT, MIN, MAX, SUM
     */
    @Test
    public void aggregateQuery() {
        // COUNT OF HOUSES, MAX FLOORS, MIN FLOORS, AVG FLOOR, SUM FLOORS
        List result = em.createQuery("SELECT s.name, COUNT(h), MAX(h.floors), MIN(h.floors), AVG(h.floors), SUM(h.floors) " +
            "FROM Street s JOIN s.houses h " +
            "GROUP BY s " +
            "HAVING COUNT(h) > 2").getResultList();

        for (Object resultItem : result) {
            Object[] resultItemParts = (Object[]) resultItem;
            for (Object part : resultItemParts) {
                System.out.print(part + ", ");
            }
            System.out.println();
        }

        // Test use custom return type and not iterate over lists of objects
        List<StreetStatistics> customResultWrappers = em.createQuery("SELECT new net.slisenko.jpa.examples.queries.StreetStatistics(s, COUNT(h), MAX(h.floors), MIN(h.floors), AVG(h.floors), SUM(h.floors)) " +
            "FROM Street s JOIN s.houses h " +
            "GROUP BY s " +
            "HAVING COUNT(h) > 2", StreetStatistics.class).getResultList();

        for (StreetStatistics stat : customResultWrappers) {
            System.out.println(stat);
        }
    }

    @Test
    public void queryParameters() {
        System.out.println("<Houses with 5 floors in city1>");
        List<House> houses = em.createQuery("SELECT h FROM House h WHERE h.floors= ?1 AND h.street.city.id = ?2", House.class)
            .setParameter(1, 5)
            .setParameter(2, city.getId()).getResultList();
        System.out.println(houses);

        houses = em.createQuery("SELECT h FROM House h WHERE h.floors= :floors AND h.street.city.id = :cityId", House.class)
            .setParameter("floors", 5)
            .setParameter("cityId", city.getId()).getResultList();
        System.out.println(houses);

        System.out.println("Entity as query parameter");
        List<Street> streetsInCity = em.createQuery("SELECT s FROM Street s WHERE s.city = :city")
                .setParameter("city", city).getResultList();
        System.out.println(streetsInCity);
    }

    @Test
    public void testDistinct() {
        em.getTransaction().begin();
        System.out.println("Without distinct");
        System.out.println(em.createQuery("SELECT h.street FROM House h").getResultList());

        System.out.println("Distinct");
        System.out.println(em.createQuery("SELECT DISTINCT h.street FROM House h").getResultList());
        em.getTransaction().commit();
    }

    @Test
    public void testQueryEmbeddedObjects() {
        em.getTransaction().begin();
        House h1 = new House();
        OwnerInfo info = new OwnerInfo();
        info.setFirstName("mr. Owner");
        info.setPhone("+172312312");
        h1.setOwner(info);
        em.persist(h1);
        em.getTransaction().commit();
        em.clear();

        // Embedded objects are not managed, changes are not saved to database
        em.getTransaction().begin();
        System.out.println("Query embeddable");
        List<OwnerInfo> owners = em.createQuery("SELECT h.owner FROM House h where h = :house").setParameter("house", h1).getResultList();
        System.out.println(owners);
        owners.get(0).setFirstName("changed");
        em.getTransaction().commit();

        System.out.println("House after changes");
        System.out.println(em.find(House.class, h1.getId()));
    }

    @Test
    public void testSubqueries() {
        // Get all houses where floors number is greater than average
        List<House> housesWithFloorsLessThanAverage = em.createQuery("SELECT h FROM House h WHERE h.floors <= (SELECT AVG(ho.floors) FROM House ho)").getResultList();
        System.out.println(housesWithFloorsLessThanAverage);

        // USING EXISTS
        // TODO clarify how exists works
        List<House> housesOnSpecificStreet = em.createQuery("SELECT h FROM House h WHERE EXISTS (SELECT 1 FROM Street s WHERE h.street = s AND s.name = :name)")
                .setParameter("name", "my street").getResultList();
        System.out.println(housesOnSpecificStreet);
    }

    @Test
    public void testPagination() {
        em.getTransaction().begin();
        for (int i = 0; i < 50; i++) {
            em.persist(new City("city" + i));
        }
        em.getTransaction().commit();
        em.clear();

        List<City> cities0to10 = em.createQuery("FROM City").setFirstResult(0).setMaxResults(10).getResultList();
        System.out.println("<Cities 0 to 10>");
        System.out.println(cities0to10);

        List<City> cities10to20 = em.createQuery("FROM City").setFirstResult(10).setMaxResults(10).getResultList();
        System.out.println("<Cities 10 to 20>");
        System.out.println(cities10to20);

        List<City> cities20to30 = em.createQuery("FROM City").setFirstResult(20).setMaxResults(10).getResultList();
        System.out.println("<Cities 20 to 30>");
        System.out.println(cities20to30);
    }

    @Test
    public void testNamedQueries() {
        em.getTransaction().begin();
        em.persist(new City("Minsk"));
        em.persist(new City("Vitebsk"));
        em.persist(new City("Brest"));
        em.getTransaction().commit();
        em.clear();

        List<City> allCities = em.createNamedQuery("City.findAll", City.class).getResultList();
        System.out.println("<all cities>");
        System.out.println(allCities);

        List<City> citiesByName = em.createNamedQuery("City.findByName", City.class)
                .setParameter("name", "Minsk").getResultList();
        System.out.println("<cities by name>");
        System.out.println(citiesByName);
    }

    @Test
    public void testOrderBy() {
        List<Customer> orderedCustomers = em.createQuery("SELECT c FROM Customer c ORDER BY c.budget DESC").getResultList();
        System.out.println(orderedCustomers);
    }

    /**
     * JPA 2.1 allows to add named queries to persistence context in code
     */
    @Test
    public void testDynamicNamedQueries() {
        TypedQuery<Street> allStreetsNamedQuery = em.createQuery("SELECT s FROM Street s", Street.class);
        emf.addNamedQuery("allStreets", allStreetsNamedQuery);

        List<Street> streets = em.createNamedQuery("allStreets").getResultList();
        System.out.println(streets);
    }
}
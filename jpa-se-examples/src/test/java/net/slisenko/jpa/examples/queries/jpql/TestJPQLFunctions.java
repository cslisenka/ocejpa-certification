package net.slisenko.jpa.examples.queries.jpql;

import net.slisenko.jpa.examples.queries.Customer;
import net.slisenko.jpa.examples.queries.House;
import net.slisenko.jpa.examples.queries.Street;
import org.junit.Test;

import java.util.List;

public class TestJPQLFunctions extends BaseJPQLTest {

    @Test
    public void testBetween() {
        List<House> houses = em.createQuery("SELECT h FROM House h WHERE h.floors BETWEEN 3 AND 6").getResultList();
        System.out.println(houses);
    }

    @Test
    public void testLike() {
        //  means from 0 to any number of any characters
        p("Using %");
        List<House> houses = em.createQuery("SELECT h FROM House h WHERE h.name LIKE 'house%'").getResultList();
        p(houses);

        // _ means any one character
        p("Using _");
        houses = em.createQuery("SELECT h FROM House h WHERE h.name LIKE 'hous__'").getResultList();
        p(houses);
    }

    @Test
    public void testCollectionIsEmpty() {
        System.out.println("Streets without houses");
        List<Street> streets = em.createQuery("SELECT s FROM Street s WHERE s.houses IS EMPTY").getResultList();
        System.out.println(streets);

        System.out.println("Streets with houses");
        streets = em.createQuery("SELECT s FROM Street s WHERE s.houses IS NOT EMPTY").getResultList();
        System.out.println(streets);
    }

    @Test
    public void testMemberOf() {
        System.out.println("Streets with house h1");
        List<Street> streets = em.createQuery("SELECT s FROM Street s WHERE :house MEMBER OF s.houses").setParameter("house", h1).getResultList();
        System.out.println(streets);
    }

    @Test
    public void testAnyAllSome() {
        // ALL
        System.out.println("Houses which all customers have money");
        List<Street> streets = em.createQuery("SELECT h FROM House h WHERE h.price <= ALL (SELECT c.budget FROM Customer c)")
                .getResultList();
        System.out.println(streets);

        // All houses which can at least one customer buy
        // ANY/SOME
        System.out.println("Houses which at least one customer have money");
        streets = em.createQuery("SELECT h FROM House h WHERE h.price <= ANY (SELECT c.budget FROM Customer c)")
                .getResultList();
        System.out.println(streets);
    }

    @Test
    public void testTypeTreatAs() {
        // Select all organization customers using TYPE
        System.out.println("Select customers by specific type");
        List<Customer> organizations = em.createQuery("SELECT c FROM Customer c WHERE TYPE(c) = OrganizationCustomer").getResultList();
        System.out.println(organizations);

        // Select type of customers
        // Hibernate throws null pointer exception
//        System.out.println("Customer types");
//        List customerTypes = em.createQuery("SELECT TYPE(c) FROM Customer c").getResultList();
//        System.out.println(customerTypes);

        // Doesn't work with hibernate
//        System.out.println("Downcasting types");
//        List<Customer> customersWithTaxIdOrSecurityNumber =
//                em.createQuery("SELECT c FROM Customer c WHERE TREAT(c AS PersonCustomer).securityNumber = '12345'").getResultList();
//        System.out.println(customersWithTaxIdOrSecurityNumber);
    }

    @Test
    public void testDistinctInAggregationFunctions() {
        p("Query which returns duplicates");
        System.out.println(em.createQuery("SELECT c.budget FROM Customer c, Customer c2").getResultList());

        p("Count of records which return duplicates");
        System.out.println(em.createQuery("SELECT COUNT(c) FROM Customer c, Customer c2").getSingleResult());

        p("Count of records which return duplicates with distinct");
        System.out.println(em.createQuery("SELECT COUNT(distinct c) FROM Customer c, Customer c2").getSingleResult());
    }
}
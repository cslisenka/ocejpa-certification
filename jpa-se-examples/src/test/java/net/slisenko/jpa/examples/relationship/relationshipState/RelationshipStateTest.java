package net.slisenko.jpa.examples.relationship.relationshipState;

import net.slisenko.AbstractJpaTest;
import org.junit.Test;

import java.util.Calendar;
import java.util.List;

/**
 * Relationships can have states.
 * Airplane has relationship to Airport and relationship has parameters (arrivalDate, departureDate)
 */
public class RelationshipStateTest extends AbstractJpaTest {

    @Test
    public void test() {
        em.getTransaction().begin();

        Airport minsk = new Airport("Minsk");
        em.persist(minsk);

        Airport barcelona = new Airport("Barcelona");
        em.persist(barcelona);

        Airplane boeing747 = new Airplane("Boeing 747");
        em.persist(boeing747);

        Airplane airbusA330 = new Airplane("Airbus A330");
        em.persist(airbusA330);

        // Make relationships
        Calendar calendar = Calendar.getInstance();
        em.persist(new ArrivalDeparture(new ArrivalDeparturePK(boeing747, minsk), calendar.getTime(), calendar.getTime()));
        em.persist(new ArrivalDeparture(new ArrivalDeparturePK(airbusA330, minsk), calendar.getTime(), calendar.getTime()));
        em.persist(new ArrivalDeparture(new ArrivalDeparturePK(boeing747, barcelona), calendar.getTime(), calendar.getTime()));

        em.getTransaction().commit();
        em.clear();

        // Find all airplanes arrivals-departures
        List<ArrivalDeparture> arrivalDepartureList = em.createQuery("SELECT ad FROM ArrivalDeparture ad").getResultList();
        for (ArrivalDeparture arrDep : arrivalDepartureList) {
            System.out.println(arrDep);
        }
    }
}
package net.slisenko.jpa.examples.relationship.relationshipState;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
public class ArrivalDeparturePK implements Serializable {

    @ManyToOne
    private Airplane airplane;

    @ManyToOne
    private Airport airport;

    public ArrivalDeparturePK() {
    }

    public ArrivalDeparturePK(Airplane airplane, Airport airport) {
        this.airplane = airplane;
        this.airport = airport;
    }

    public Airplane getAirplane() {
        return airplane;
    }

    public void setAirplane(Airplane airplane) {
        this.airplane = airplane;
    }

    public Airport getAirport() {
        return airport;
    }

    public void setAirport(Airport airport) {
        this.airport = airport;
    }

    @Override
    public String toString() {
        return "ArrivalDeparturePK{" +
                "airplane=" + airplane +
                ", airport=" + airport +
                '}';
    }
}
package net.slisenko.jpa.examples.relationship.relationshipState;

import net.slisenko.Identity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Airport extends Identity {

    @OneToMany(mappedBy = "pk.airport")
    private List<ArrivalDeparture> arrivalDepartures = new ArrayList<>();

    public Airport() {
    }

    public Airport(String name) {
        this.name = name;
    }

    public List<ArrivalDeparture> getArrivalDepartures() {
        return arrivalDepartures;
    }

    public void setArrivalDepartures(List<ArrivalDeparture> arrivalDepartures) {
        this.arrivalDepartures = arrivalDepartures;
    }

    @Override
    public String toString() {
        return "Airport{" +
                "name='" + name + '\'' +
                "} " + super.toString();
    }
}
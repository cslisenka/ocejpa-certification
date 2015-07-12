package net.slisenko.jpa.examples.relationship.relationshipState;

import net.slisenko.Identity;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Airplane extends Identity {

    @OneToMany(mappedBy = "pk.airplane")
    private List<ArrivalDeparture> arrivalDepartures = new ArrayList<>();

    public Airplane() {
    }

    public Airplane(String name) {
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
        return "Airplane{" +
                "name='" + name + '\'' +
                "} " + super.toString();
    }
}
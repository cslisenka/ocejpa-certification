package net.slisenko.jpa.examples.relationship.relationshipState;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
public class ArrivalDeparture {

    @EmbeddedId
    private ArrivalDeparturePK pk;

    @Temporal(TemporalType.TIMESTAMP)
    private Date arrivalDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date departureDate;

    public ArrivalDeparture() {
    }

    public ArrivalDeparture(ArrivalDeparturePK pk, Date arrivalDate, Date departureDate) {
        this.pk = pk;
        this.arrivalDate = arrivalDate;
        this.departureDate = departureDate;
    }

    public ArrivalDeparturePK getPk() {
        return pk;
    }

    public void setPk(ArrivalDeparturePK pk) {
        this.pk = pk;
    }

    public Date getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(Date arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public Date getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(Date departureDate) {
        this.departureDate = departureDate;
    }

    @Override
    public String toString() {
        return "ArrivalDeparture{" +
                "pk=" + pk +
                ", arrivalDate=" + arrivalDate +
                ", departureDate=" + departureDate +
                '}';
    }
}
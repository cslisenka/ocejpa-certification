package net.slisenko.jpa.examples.queries;

import net.slisenko.Identity;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@Entity
public class House extends Identity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Street street;

    @Embedded
    private OwnerInfo owner;

    private int floors;

    public House() {
    }

    public House(String name) {
        this.name = name;
    }

    public Street getStreet() {
        return street;
    }

    public void setStreet(Street street) {
        this.street = street;
    }

    public int getFloors() {
        return floors;
    }

    public void setFloors(int floors) {
        this.floors = floors;
    }

    public OwnerInfo getOwner() {
        return owner;
    }

    public void setOwner(OwnerInfo owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "House{" +
             //   "street=" + street +
                ", owner=" + owner +
                ", floors=" + floors +
                '}';
    }
}
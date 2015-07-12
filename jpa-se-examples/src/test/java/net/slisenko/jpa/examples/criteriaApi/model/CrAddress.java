package net.slisenko.jpa.examples.criteriaApi.model;

import net.slisenko.Identity;

import javax.persistence.Entity;

@Entity
public class CrAddress extends Identity {

    private String city;
    private String street;
    private String house;

    public CrAddress() {
    }

    public CrAddress(String city, String street, String house) {
        this.city = city;
        this.street = street;
        this.house = house;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    @Override
    public String toString() {
        return "CrAddress{" +
                "city='" + city + '\'' +
                ", street='" + street + '\'' +
                ", house='" + house + '\'' +
                "} " + super.toString();
    }
}

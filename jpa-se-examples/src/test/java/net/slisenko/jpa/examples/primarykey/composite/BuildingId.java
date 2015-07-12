package net.slisenko.jpa.examples.primarykey.composite;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class BuildingId implements Serializable {

    private String city;

    private String street;

    private int house;

    public BuildingId() {
    }

    public BuildingId(String city, String street, int house) {
        this.city = city;
        this.street = street;
        this.house = house;
    }

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public int getHouse() {
        return house;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BuildingId that = (BuildingId) o;

        if (house != that.house) return false;
        if (city != null ? !city.equals(that.city) : that.city != null) return false;
        if (street != null ? !street.equals(that.street) : that.street != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = city != null ? city.hashCode() : 0;
        result = 31 * result + (street != null ? street.hashCode() : 0);
        result = 31 * result + house;
        return result;
    }
}
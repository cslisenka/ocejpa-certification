package net.slisenko.jpa.examples.queries;

import net.slisenko.Identity;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import java.util.HashMap;
import java.util.Map;

@NamedQueries({
    @NamedQuery(name = "City.findAll", query = "SELECT c FROM City c"),
    @NamedQuery(name = "City.findByName", query = "SELECT c FROM City c WHERE c.name=:name")
})
@Entity
public class City extends Identity {

    @ElementCollection
    private Map<String, String> cityServicesNumbers = new HashMap<>();

    public City() {
    }

    public City(String name) {
        this.name = name;
    }

    public Map<String, String> getCityServicesNumbers() {
        return cityServicesNumbers;
    }

    public void setCityServicesNumbers(Map<String, String> cityServicesNumbers) {
        this.cityServicesNumbers = cityServicesNumbers;
    }

    @Override
    public String toString() {
        return "City{" +
                "name='" + name + '\'' +
                '}';
    }
}
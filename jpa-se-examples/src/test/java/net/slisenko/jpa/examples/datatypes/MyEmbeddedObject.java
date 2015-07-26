package net.slisenko.jpa.examples.datatypes;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Embeddable
public class MyEmbeddedObject {

    private String country;
    private String city;

    @OneToOne
    @JoinColumn(name = "link")
    private EntityWithTypes entity;

    public MyEmbeddedObject() {
    }

    public MyEmbeddedObject(String country, String city) {
        this.country = country;
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public EntityWithTypes getEntity() {
        return entity;
    }

    public void setEntity(EntityWithTypes entity) {
        this.entity = entity;
    }
}
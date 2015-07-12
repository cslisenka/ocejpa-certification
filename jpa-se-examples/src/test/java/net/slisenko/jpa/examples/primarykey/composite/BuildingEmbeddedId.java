package net.slisenko.jpa.examples.primarykey.composite;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
public class BuildingEmbeddedId {

    @EmbeddedId
    private BuildingId id;

    private double price;

    public BuildingEmbeddedId() {
    }

    public BuildingEmbeddedId(BuildingId id, double price) {
        this.id = id;
        this.price = price;
    }

    public BuildingId getId() {
        return id;
    }

    public void setId(BuildingId id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
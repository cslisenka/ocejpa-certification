package net.slisenko.jpa.examples.criteriaApi;

import net.slisenko.Identity;

import javax.persistence.Entity;

@Entity
public class Car extends Identity {

    private String model;

    public Car() {
    }

    public Car(String model) {
        this.model = model;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @Override
    public String toString() {
        return "Car{" +
                "model='" + model + '\'' +
                "} " + super.toString();
    }
}
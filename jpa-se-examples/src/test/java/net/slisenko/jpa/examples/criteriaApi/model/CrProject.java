package net.slisenko.jpa.examples.criteriaApi.model;

import net.slisenko.Identity;

import javax.persistence.Entity;

@Entity
public class CrProject extends Identity {

    public CrProject() {
    }

    public CrProject(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "CrProject{" +
                "name='" + name + '\'' +
                "} " + super.toString();
    }
}

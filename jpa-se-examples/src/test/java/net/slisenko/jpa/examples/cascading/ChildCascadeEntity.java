package net.slisenko.jpa.examples.cascading;

import net.slisenko.Identity;

import javax.persistence.Entity;

@Entity
public class ChildCascadeEntity extends Identity {

    public ChildCascadeEntity() {
    }

    public ChildCascadeEntity(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ChildCascadeEntity{" +
                "name='" + name + '\'' +
                "} " + super.toString();
    }
}
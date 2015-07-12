package net.slisenko.jpa.examples.relationship.orphanRemoval;

import net.slisenko.Identity;

import javax.persistence.Entity;

@Entity
public class OrphanEntity extends Identity {

    public OrphanEntity() {
    }

    public OrphanEntity(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "OrphanEntity{" +
                "name='" + name + '\'' +
                "} " + super.toString();
    }
}

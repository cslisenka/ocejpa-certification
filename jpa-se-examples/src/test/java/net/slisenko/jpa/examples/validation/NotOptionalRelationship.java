package net.slisenko.jpa.examples.validation;

import net.slisenko.Identity;

import javax.persistence.Entity;

@Entity
public class NotOptionalRelationship extends Identity {

    public NotOptionalRelationship() {
    }

    public NotOptionalRelationship(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "NotOptionalEntity{" +
                "name='" + name + '\'' +
                "} " + super.toString();
    }
}
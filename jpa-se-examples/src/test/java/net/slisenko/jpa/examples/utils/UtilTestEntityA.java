package net.slisenko.jpa.examples.utils;

import net.slisenko.Identity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@Entity
public class UtilTestEntityA extends Identity {

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private UtilTestEntityB entityB;

    public UtilTestEntityA() {
    }

    public UtilTestEntityA(String name) {
        this.name = name;
    }

    public UtilTestEntityB getEntityB() {
        return entityB;
    }

    public void setEntityB(UtilTestEntityB entityB) {
        this.entityB = entityB;
    }
}
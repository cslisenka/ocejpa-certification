package net.slisenko.jpa.examples.utils;

import net.slisenko.Identity;

import javax.persistence.Entity;

@Entity
public class UtilTestEntityB extends Identity {

    private String nameB;

    public UtilTestEntityB() {
    }

    public UtilTestEntityB(String nameB) {
        this.nameB = nameB;
    }

    public String getNameB() {
        return nameB;
    }

    public void setNameB(String nameB) {
        this.nameB = nameB;
    }
}

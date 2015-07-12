package net.slisenko.jpa.examples.relationship.oneToOne;

import net.slisenko.Identity;

import javax.persistence.Entity;

@Entity
public class Item4 extends Identity {

    public Item4() {
    }

    public Item4(String name) {
        this.name = name;
    }
}
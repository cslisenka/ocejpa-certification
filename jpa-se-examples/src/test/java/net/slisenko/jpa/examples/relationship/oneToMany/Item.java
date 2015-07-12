package net.slisenko.jpa.examples.relationship.oneToMany;

import net.slisenko.Identity;

import javax.persistence.Entity;

@Entity
public class Item extends Identity {

    public Item() {
    }

    public Item(String name) {
        this.name = name;
    }
}

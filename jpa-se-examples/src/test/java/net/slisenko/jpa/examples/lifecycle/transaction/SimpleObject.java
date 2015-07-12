package net.slisenko.jpa.examples.lifecycle.transaction;

import net.slisenko.Identity;

import javax.persistence.Entity;

@Entity
public class SimpleObject extends Identity {

    public SimpleObject() {
    }

    public SimpleObject(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "SimpleObject{" +
            "id='" + id + '\'' +
            "name='" + name + '\'' +
            '}';
    }
}
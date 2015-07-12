package net.slisenko.jpa.examples.relationship.ordering.persistent;

import net.slisenko.Identity;

import javax.persistence.Entity;

@Entity
public class PrintJob extends Identity {

    public PrintJob() {
    }

    public PrintJob(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "PrintJob{" +
                "name='" + name + '\'' +
                "} " + super.toString();
    }
}

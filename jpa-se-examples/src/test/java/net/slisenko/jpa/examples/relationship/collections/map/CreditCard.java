package net.slisenko.jpa.examples.relationship.collections.map;

import net.slisenko.Identity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class CreditCard extends Identity {

    @ManyToOne
    private Person person;

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}

package net.slisenko.jpa.examples.relationship.oneToOne;

import net.slisenko.Identity;

import javax.persistence.*;

@Entity
public class Item3 extends Identity {

    // If lazy - Hibernate makes two separate queries
    // If eager - one query with join
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oto_id")
    // Join table can also be used
    //@JoinTable(name = "join_oto")
    // TODO use compound columns - when we join by 2 columns
    private Item4 oneToOne;

    public Item3() {
    }

    public Item3(String name) {
        this.name = name;
    }

    public Item4 getOneToOne() {
        return oneToOne;
    }

    public void setOneToOne(Item4 oneToOne) {
        this.oneToOne = oneToOne;
    }
}
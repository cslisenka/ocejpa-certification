package net.slisenko.jpa.examples.relationship.manyToOne;

import net.slisenko.Identity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Item2 extends Identity {

    // ManyToOne relationships are lazy by default
    @ManyToOne
    @JoinColumn(name = "GROUP_ID")
    private ItemGroup2 group;

    public Item2() {
    }

    public Item2(String name) {
        this.name = name;
    }

    public ItemGroup2 getGroup() {
        return group;
    }

    public void setGroup(ItemGroup2 group) {
        this.group = group;
    }
}
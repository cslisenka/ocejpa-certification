package net.slisenko.jpa.examples.relationship.manyToOne;

import net.slisenko.Identity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ItemGroup2 extends Identity {

    // OneToMany relationships are lazy by default
    // If eager -> hibernate will evaluate SQL queries first, then print everything
    @OneToMany(mappedBy = "group", fetch = FetchType.EAGER)
    private List<Item2> itemList = new ArrayList<>();

    public ItemGroup2() {
    }

    public ItemGroup2(String name) {
        this.name = name;
    }

    public List<Item2> getItems() {
        return itemList;
    }

    public void setItems(List<Item2> itemList) {
        this.itemList = itemList;
    }
}
package net.slisenko.jpa.examples.relationship.oneToMany;

import net.slisenko.Identity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ItemGroup extends Identity {

    @OneToMany
    // If no join table specified, ItemGroup id will be on Item side
    @JoinTable(name = "CUSTOM_OTM",
        joinColumns = @JoinColumn(name = "itm_gr_id"),
        inverseJoinColumns = @JoinColumn(name = "itm_id")
    )
    private List<Item> itemList = new ArrayList<>();

    public ItemGroup() {
    }

    public ItemGroup(String name) {
        this.name = name;
    }

    public List<Item> getItems() {
        return itemList;
    }

    public void setItems(List<Item> itemList) {
        this.itemList = itemList;
    }
}
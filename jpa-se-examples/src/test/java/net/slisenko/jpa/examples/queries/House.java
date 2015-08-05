package net.slisenko.jpa.examples.queries;

import net.slisenko.Identity;

import javax.persistence.*;

@NamedEntityGraph(name = "HouseWithStreet", attributeNodes = {
        @NamedAttributeNode("price"),
        @NamedAttributeNode("name"),
        @NamedAttributeNode("floors"),
        @NamedAttributeNode(value = "street", subgraph = "streetSubgraph")
}, subgraphs = {
        @NamedSubgraph(name = "streetSubgraph", attributeNodes = {
                @NamedAttributeNode("name")
        })
})
@Entity
public class House extends Identity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Street street;

    @Embedded
    private OwnerInfo owner;

    private int floors;

    private int price;

    public House() {
    }

    public House(String name) {
        this.name = name;
    }

    public Street getStreet() {
        return street;
    }

    public void setStreet(Street street) {
        this.street = street;
    }

    public int getFloors() {
        return floors;
    }

    public void setFloors(int floors) {
        this.floors = floors;
    }

    public OwnerInfo getOwner() {
        return owner;
    }

    public void setOwner(OwnerInfo owner) {
        this.owner = owner;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "House{" +
                "price=" + price +
                "name=" + name +
                ", owner=" + owner +
                ", floors=" + floors +
                '}';
    }
}
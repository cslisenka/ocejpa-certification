package net.slisenko.jpa.examples.inheritance.tablePerClass;

import javax.persistence.Entity;

@Entity
public class TChildA extends TCBase {

    private String childAProp;

    public String getChildAProp() {
        return childAProp;
    }

    public void setChildAProp(String childAProp) {
        this.childAProp = childAProp;
    }

    @Override
    public String toString() {
        return "TChildA{" +
                "childAProp='" + childAProp + '\'' +
                "} " + super.toString();
    }
}
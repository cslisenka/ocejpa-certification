package net.slisenko.jpa.examples.inheritance.tablePerClass;

import javax.persistence.Entity;

@Entity
public class TChildB extends TCBase {

    private int childBProp;

    public int getChildAProp() {
        return childBProp;
    }

    public void setChildAProp(int childBProp) {
        this.childBProp = childBProp;
    }

    @Override
    public String toString() {
        return "TChildB{" +
                "childBProp=" + childBProp +
                "} " + super.toString();
    }
}
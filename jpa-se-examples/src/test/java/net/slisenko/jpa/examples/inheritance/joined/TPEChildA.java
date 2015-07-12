package net.slisenko.jpa.examples.inheritance.joined;

import javax.persistence.Entity;

@Entity
public class TPEChildA extends TPEBase {

    private String childAParam;

    public String getChildAParam() {
        return childAParam;
    }

    public void setChildAParam(String childAParam) {
        this.childAParam = childAParam;
    }
}
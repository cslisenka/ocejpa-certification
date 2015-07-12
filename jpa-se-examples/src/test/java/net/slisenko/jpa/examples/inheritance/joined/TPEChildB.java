package net.slisenko.jpa.examples.inheritance.joined;

import javax.persistence.Entity;

@Entity
public class TPEChildB extends TPEBase {

    private int childBParam;

    public int getChildAParam() {
        return childBParam;
    }

    public void setChildAParam(int childBParam) {
        this.childBParam = childBParam;
    }
}
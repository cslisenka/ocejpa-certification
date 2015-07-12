package net.slisenko.jpa.examples.inheritance.singleTable;

import javax.persistence.Entity;

@Entity
public class SingleTableSub2 extends SingleTableBase {

    private int sub2;

    public int getSub2() {
        return sub2;
    }

    public void setSub2(int sub2) {
        this.sub2 = sub2;
    }

    @Override
    public String toString() {
        return "SingleTableSub2{" +
                "sub2=" + sub2 +
                "} " + super.toString();
    }
}
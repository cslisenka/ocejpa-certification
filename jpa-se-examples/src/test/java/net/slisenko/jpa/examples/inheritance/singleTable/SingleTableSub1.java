package net.slisenko.jpa.examples.inheritance.singleTable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = "42") // If we choose discriminatorType = DiscriminatorType.INTEGER, we can not put not-integer value here
public class SingleTableSub1 extends SingleTableBase {

    private String sub1Field;

    public String getSub1Field() {
        return sub1Field;
    }

    public void setSub1Field(String sub1Field) {
        this.sub1Field = sub1Field;
    }

    @Override
    public String toString() {
        return "SingleTableSub1{" +
                "sub1Field='" + sub1Field + '\'' +
                "} " + super.toString();
    }
}
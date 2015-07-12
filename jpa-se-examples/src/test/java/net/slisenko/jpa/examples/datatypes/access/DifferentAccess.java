package net.slisenko.jpa.examples.datatypes.access;

import net.slisenko.Identity;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;

@Entity
@Access(AccessType.FIELD)
public class DifferentAccess extends Identity {

    private String field;
    private String field2;

    /**
     * In database hibernate will take value from column "field"
     */
    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    /**
     * In database hibernate will take value from column "propety"
     */
    @Access(AccessType.PROPERTY)
    public String getProperty() {
        return field2;
    }

    public void setProperty(String field2) {
        this.field2 = field2;
    }

    @Override
    public String toString() {
        return "DifferentAccess{" +
                "field='" + field + '\'' +
                ", field2='" + field2 + '\'' +
                '}';
    }
}

package net.slisenko.jpa.examples.datatypes.readonly;

import net.slisenko.Identity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class ReadOnlyMappingsEntity extends Identity {

    /**
     * Hibernate does not send changes to database
     */
    @Column(updatable = false)
    private String notUpdatable;

    /**
     * Hibernate ignores this value when we insert first time.
     * When we update existed entity, hibernate saves value.
     */
    @Column(insertable = false)
    private String notInsertable;

    @OneToOne(optional = false)
    private NotOptionalEntity entity;

    public String getNotUpdatable() {
        return notUpdatable;
    }

    public void setNotUpdatable(String notUpdatable) {
        this.notUpdatable = notUpdatable;
    }

    public String getNotInsertable() {
        return notInsertable;
    }

    public void setNotInsertable(String notInsertable) {
        this.notInsertable = notInsertable;
    }

    public NotOptionalEntity getEntity() {
        return entity;
    }

    public void setEntity(NotOptionalEntity entity) {
        this.entity = entity;
    }

    @Override
    public String toString() {
        return "ReadOnlyMappingsEntity{" +
                "notUpdatable='" + notUpdatable + '\'' +
                ", notInsertable='" + notInsertable + '\'' +
                ", entity=" + entity +
                '}';
    }
}
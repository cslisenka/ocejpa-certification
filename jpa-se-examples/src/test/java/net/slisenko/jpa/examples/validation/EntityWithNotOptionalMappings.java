package net.slisenko.jpa.examples.validation;

import net.slisenko.Identity;

import javax.persistence.*;

@Entity
public class EntityWithNotOptionalMappings extends Identity {

    @Column(updatable = false)
    private String notUpdatable;

    @Column(nullable = false)
    private String notNullable;

    @Column(insertable = false)
    private String notInsertable;

    @OneToOne(optional = false)
    @JoinColumn(updatable = false)
    private NotOptionalRelationship notOptional;

    public String getNotUpdatable() {
        return notUpdatable;
    }

    public void setNotUpdatable(String notUpdatable) {
        this.notUpdatable = notUpdatable;
    }

    public String getNotNullable() {
        return notNullable;
    }

    public void setNotNullable(String notNullable) {
        this.notNullable = notNullable;
    }

    public String getNotInsertable() {
        return notInsertable;
    }

    public void setNotInsertable(String notInsertable) {
        this.notInsertable = notInsertable;
    }

    public NotOptionalRelationship getNotOptional() {
        return notOptional;
    }

    public void setNotOptional(NotOptionalRelationship notOptional) {
        this.notOptional = notOptional;
    }

    @Override
    public String toString() {
        return "EntityWithNotOptionalMappings{" +
                "notUpdatable='" + notUpdatable + '\'' +
                ", notNullable='" + notNullable + '\'' +
                ", notInsertable='" + notInsertable + '\'' +
                ", notOptional=" + notOptional +
                "} " + super.toString();
    }
}
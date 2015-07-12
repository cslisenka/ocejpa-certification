package net.slisenko.jpa.examples.lifecycle.locking.model;

import net.slisenko.Identity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class NonVersionedEntity extends Identity implements EntityWithNameAndId {

    @ManyToOne
    private VersionedEntity versionedEntity;

    public NonVersionedEntity() {
    }

    public NonVersionedEntity(String name) {
        this.name = name;
    }

    public VersionedEntity getVersionedEntity() {
        return versionedEntity;
    }

    public void setVersionedEntity(VersionedEntity versionedEntity) {
        this.versionedEntity = versionedEntity;
    }

    @Override
    public String toString() {
        return "NonVersionedEntity{" +
                "name='" + name + '\'' +
                "} " + super.toString();
    }
}
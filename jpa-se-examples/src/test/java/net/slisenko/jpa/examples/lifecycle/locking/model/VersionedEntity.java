package net.slisenko.jpa.examples.lifecycle.locking.model;

import net.slisenko.Identity;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import java.util.ArrayList;
import java.util.List;

@Entity
public class VersionedEntity extends Identity {

    //TODO TRY TIMESTAMP VERSION FIELD
    @Version
    private int version;

    @OneToMany(mappedBy = "versionedEntity")
    private List<NonVersionedEntity> nonVersionedEntities = new ArrayList<>();

    public VersionedEntity() {
    }

    public VersionedEntity(String name) {
        this.name = name;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public List<NonVersionedEntity> getNonVersionedEntities() {
        return nonVersionedEntities;
    }

    public void setNonVersionedEntities(List<NonVersionedEntity> nonVersionedEntities) {
        this.nonVersionedEntities = nonVersionedEntities;
    }

    @Override
    public String toString() {
        return "VersionedEntity{" +
                "version=" + version +
                ", name='" + name + '\'' +
                "} " + super.toString();
    }
}
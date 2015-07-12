package net.slisenko.jpa.examples.cascading;

import net.slisenko.Identity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class ParentEntity extends Identity {

    @ManyToOne(cascade = CascadeType.ALL)
    private ChildCascadeEntity cascadeAllEntity;

    @ManyToOne(cascade = CascadeType.REMOVE)
    private ChildCascadeEntity cascadeDeleteEntity;

    @ManyToOne(cascade = CascadeType.MERGE)
    private ChildCascadeEntity cascadeMergeEntity;

    public ChildCascadeEntity getCascadeAllEntity() {
        return cascadeAllEntity;
    }

    public void setCascadeAllEntity(ChildCascadeEntity cascadeAllEntity) {
        this.cascadeAllEntity = cascadeAllEntity;
    }

    public ChildCascadeEntity getCascadeDeleteEntity() {
        return cascadeDeleteEntity;
    }

    public void setCascadeDeleteEntity(ChildCascadeEntity cascadeDeleteEntity) {
        this.cascadeDeleteEntity = cascadeDeleteEntity;
    }

    public ChildCascadeEntity getCascadeMergeEntity() {
        return cascadeMergeEntity;
    }

    public void setCascadeMergeEntity(ChildCascadeEntity cascadeMergeEntity) {
        this.cascadeMergeEntity = cascadeMergeEntity;
    }

    @Override
    public String toString() {
        return "ParentEntity{" +
                "cascadeAllEntity=" + cascadeAllEntity +
                ", cascadeDeleteEntity=" + cascadeDeleteEntity +
                ", cascadeMergeEntity=" + cascadeMergeEntity +
                "} " + super.toString();
    }
}
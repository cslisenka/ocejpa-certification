package net.slisenko.jpa.examples.relationship.orphanRemoval;

import net.slisenko.Identity;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;

@Entity
public class OrphanParentEntity extends Identity {

    // TODO чем отличается от cascade delete?
    @OneToOne(orphanRemoval = true)
    private OrphanEntity singleOrphan;

    @OneToMany(orphanRemoval = true)
    private List<OrphanEntity> multipleOrphans = new ArrayList<>();

    @OneToOne
    private OrphanEntity notOrphan;

    public OrphanEntity getSingleOrphan() {
        return singleOrphan;
    }

    public void setSingleOrphan(OrphanEntity singleOrphan) {
        this.singleOrphan = singleOrphan;
    }

    public List<OrphanEntity> getMultipleOrphans() {
        return multipleOrphans;
    }

    public void setMultipleOrphans(List<OrphanEntity> multipleOrphans) {
        this.multipleOrphans = multipleOrphans;
    }

    public OrphanEntity getNotOrphan() {
        return notOrphan;
    }

    public void setNotOrphan(OrphanEntity notOrphan) {
        this.notOrphan = notOrphan;
    }

    @Override
    public String toString() {
        return "OrphanParentEntity{" +
                "singleOrphan=" + singleOrphan +
                ", multipleOrphans=" + multipleOrphans +
                ", notOrphan=" + notOrphan +
                "} " + super.toString();
    }
}

package net.slisenko.jpa.examples.lifecycle.locking.model;

import net.slisenko.Identity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class LockingEntityWithElementCollection extends Identity{

    @CollectionTable(name = "locking_collection_table")
    @ElementCollection
    private List<String> names = new ArrayList<>();

    @JoinTable(name = "join_table_for_testing_extended_lock_mode")
    @OneToOne(cascade = CascadeType.ALL)
    private NonVersionedEntity relationship;

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public NonVersionedEntity getRelationship() {
        return relationship;
    }

    public void setRelationship(NonVersionedEntity relationship) {
        this.relationship = relationship;
    }
}
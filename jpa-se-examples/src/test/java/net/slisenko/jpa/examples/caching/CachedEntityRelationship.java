package net.slisenko.jpa.examples.caching;

import net.slisenko.Identity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;

@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Entity
public class CachedEntityRelationship extends Identity {

    public CachedEntityRelationship(String name) {
        this.name = name;
    }

    public CachedEntityRelationship() {
    }
}
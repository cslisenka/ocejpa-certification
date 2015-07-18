package net.slisenko.jpa.examples.caching.model;

import net.slisenko.Identity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Entity
public class CachedEntityRelationship extends Identity {

    @ManyToOne
    private CachedEntity entity;

    public CachedEntityRelationship(String name) {
        this.name = name;
    }

    public CachedEntityRelationship() {
    }

    public CachedEntity getEntity() {
        return entity;
    }

    public void setEntity(CachedEntity entity) {
        this.entity = entity;
    }
}
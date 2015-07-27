package net.slisenko.jpa.examples.caching.model;

import net.slisenko.Identity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
public class CachedEntityRelationship extends Identity {

    @ManyToOne
    private CachedEntity entity;

    public CachedEntityRelationship(String name, CachedEntity entity) {
        this.name = name;
        this.entity = entity;
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
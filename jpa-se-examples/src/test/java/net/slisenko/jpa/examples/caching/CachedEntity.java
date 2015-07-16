package net.slisenko.jpa.examples.caching;

import net.slisenko.Identity;

import javax.persistence.Cacheable;
import javax.persistence.Entity;

@Cacheable
@Entity
public class CachedEntity extends Identity {

    public CachedEntity() {
    }

    public CachedEntity(String name) {
        this.name = name;
    }
}

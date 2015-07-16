package net.slisenko.jpa.examples.caching;

import net.slisenko.Identity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Cacheable;
import javax.persistence.Entity;

// Cacheable didn't work with hibernate
//@Cacheable(value = true) // Or simply @Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Entity
public class CachedEntity extends Identity {

    public CachedEntity() {

    }

    public CachedEntity(String name) {
        this.name = name;
    }
}

package net.slisenko.jpa.examples.caching;

import net.slisenko.Identity;

import javax.persistence.Cacheable;
import javax.persistence.Entity;

@Cacheable(true) // Or simply @Cacheable
@Entity
public class CachedEntity extends Identity {
}

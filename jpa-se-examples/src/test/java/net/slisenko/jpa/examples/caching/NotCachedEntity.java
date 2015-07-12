package net.slisenko.jpa.examples.caching;

import net.slisenko.Identity;

import javax.persistence.Cacheable;
import javax.persistence.Entity;

@Cacheable(false)
@Entity
public class NotCachedEntity extends Identity {
}

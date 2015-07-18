package net.slisenko.jpa.examples.caching.model;

import net.slisenko.Identity;

import javax.persistence.Cacheable;
import javax.persistence.Entity;

@Cacheable(false)
@Entity
public class NotCachedEntity extends Identity {
}

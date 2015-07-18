package net.slisenko.jpa.examples.caching.model;

import net.slisenko.Identity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;

@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
public class NonStrictReadWriteEntity extends Identity {
}

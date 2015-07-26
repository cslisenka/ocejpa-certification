package net.slisenko.jpa.examples.caching.model;

import net.slisenko.Identity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
public class ReadWriteEntity extends Identity{
}

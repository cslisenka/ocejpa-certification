package net.slisenko.jpa.examples.lifecycle.locking.model;

public interface EntityWithNameAndId {

    public Long getId();

    public void setId(Long id);

    public String getName();

    public void setName(String name);
}
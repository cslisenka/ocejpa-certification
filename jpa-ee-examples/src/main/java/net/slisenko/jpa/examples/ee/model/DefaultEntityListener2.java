package net.slisenko.jpa.examples.ee.model;

import javax.persistence.PrePersist;

public class DefaultEntityListener2 {

    @PrePersist
    public void prePersist(Object entity) {
        System.out.println("DefaultEntityListener2 called!!!!!!! " + entity);
    }
}

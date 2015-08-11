package net.slisenko.jpa.examples.ee.model;

import javax.persistence.PrePersist;

public class DefaultEntityListener1 {

    @PrePersist
    public void prePersist(Object entity) {
        System.out.println("DefaultEntityListener1 called!!!!!!! " + entity);
    }
}

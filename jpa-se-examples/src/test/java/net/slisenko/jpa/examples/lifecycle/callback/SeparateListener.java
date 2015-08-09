package net.slisenko.jpa.examples.lifecycle.callback;

import javax.persistence.PrePersist;

public class SeparateListener {

    @PrePersist
    public void listen(EntityEventListener entity) {
        System.out.println("SeparateListener!!!!!! " + entity);
    }
}

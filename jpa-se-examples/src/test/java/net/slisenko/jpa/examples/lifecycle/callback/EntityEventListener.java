package net.slisenko.jpa.examples.lifecycle.callback;

import net.slisenko.Identity;

import javax.persistence.*;

@Entity
@EntityListeners({SeparateListener.class})
public class EntityEventListener extends Identity {

    /**
     * Callback method: public void myCallback() {...}
     * no arguments, return void, not final, not static, no checked exceptions
     * Only 1 each lifecycle annotation per class
     * Not possible to access EntityManager, executing queries
     * Can be multiple annotations per 1 method
     */

    @PostLoad
    public void postLoad() {
        System.out.println("POST LOAD " + this);
    }

    // doesn't guarantee the success, because transaction can be rolled back
    @PostPersist
    public void postPersist() {
        System.out.println("POST PERSIST " + this);
    }

    // doesn't guarantee the success, because transaction can be rolled back
    @PrePersist
    public void prePersist() {
        System.out.println("PRE PERSIST " + this);
    }

    // doesn't guarantee the success, because transaction can be rolled back
    @PreUpdate
    @PostUpdate
    public void prePostUpdate() {
        System.out.println("PRE/POST UPDATE " + this);
    }

    // doesn't guarantee the success, because transaction can be rolled back
    @PreRemove
    public void preRemove() {
        System.out.println("PRE REMOVE " + this);
    }

    // doesn't guarantee the success, because transaction can be rolled back
    @PostRemove
    public void postRemove() {
        System.out.println("POST REMOVE " + this);
    }

    @Override
    public String toString() {
        return "EntityEventListener{" +
                "name='" + name + '\'' +
                "} " + super.toString();
    }
}
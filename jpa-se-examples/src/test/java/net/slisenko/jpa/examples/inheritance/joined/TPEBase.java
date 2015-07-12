package net.slisenko.jpa.examples.inheritance.joined;

import net.slisenko.Identity;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
// Descriminator column has string type by default
@DiscriminatorColumn(name="type")
public class TPEBase extends Identity {

    private String base;

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }
}
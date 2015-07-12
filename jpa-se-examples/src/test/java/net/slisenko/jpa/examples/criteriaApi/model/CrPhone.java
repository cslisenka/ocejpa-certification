package net.slisenko.jpa.examples.criteriaApi.model;

import net.slisenko.Identity;

import javax.persistence.Entity;

@Entity
public class CrPhone extends Identity {

    private String number;

    public CrPhone() {
    }

    public CrPhone(String number) {
        this.number = number;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "CrPhone{" +
                "number='" + number + '\'' +
                "} " + super.toString();
    }
}

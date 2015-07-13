package net.slisenko.jpa.examples.queries;

import net.slisenko.Identity;

import javax.persistence.Entity;

@Entity
public class Customer extends Identity {

    private int budget;

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "name=" + name +
                "budget=" + budget +
                '}';
    }
}

package net.slisenko.jpa.examples.queries;

import javax.persistence.Entity;

@Entity
public class PersonCustomer extends Customer {

    private String securityNumber;

    public String getSecurityNumber() {
        return securityNumber;
    }

    public void setSecurityNumber(String securityNumber) {
        this.securityNumber = securityNumber;
    }

    @Override
    public String toString() {
        return "PersonCustomer{" +
                "name=" + name + '\'' +
                "securityNumber='" + securityNumber + '\'' +
                '}';
    }
}

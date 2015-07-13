package net.slisenko.jpa.examples.queries;

import javax.persistence.Entity;

@Entity
public class OrganizationCustomer extends Customer {

    private int taxId;

    public int getTaxId() {
        return taxId;
    }

    public void setTaxId(int taxId) {
        this.taxId = taxId;
    }

    @Override
    public String toString() {
        return "PersonCustomer{" +
                "name=" + name + '\'' +
                "taxId='" + taxId + '\'' +
                '}';
    }
}
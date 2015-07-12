package net.slisenko.jpa.examples.queries;

import javax.persistence.Embeddable;

@Embeddable
public class OwnerInfo {

    private String firstName;
    private String phone;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "OwnerInfo{" +
                "firstName='" + firstName + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}

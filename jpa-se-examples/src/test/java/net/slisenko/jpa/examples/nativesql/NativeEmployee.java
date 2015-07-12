package net.slisenko.jpa.examples.nativesql;

import net.slisenko.Identity;

import javax.persistence.Entity;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;

@NamedNativeQuery(
    name = "getAllNativeEmployees",
    query = "SELECT * FROM native_employee_table",
    resultClass = NativeEmployee.class
)
@Entity
@Table(name = "native_employee_table")
public class NativeEmployee extends Identity {

    public NativeEmployee() {
    }

    public NativeEmployee(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "NativeEmployee{" +
                "name='" + name + '\'' +
                "} " + super.toString();
    }
}
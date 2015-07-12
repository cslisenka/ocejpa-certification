package net.slisenko.jpa.examples.relationship.manyToMany;

import net.slisenko.Identity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Project2 extends Identity {

    /**
     * By default fetch is lazy.
     * If we specify eager loading hibernate will query all employees before we call getEmployees()
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "CUSTOM_JOIN_TBL_EMPL_PROJ",
        joinColumns = @JoinColumn(name = "emp_id"),
        inverseJoinColumns = @JoinColumn(name = "proj_id")
    )
    private List<Employee2> employees = new ArrayList<>();

    public Project2() {
    }

    public Project2(String name) {
        this.name = name;
    }

    public List<Employee2> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee2> employees) {
        this.employees = employees;
    }

    @Override
    public String toString() {
        return "Project{" +
                "name='" + name + '\'' +
                "} " + super.toString();
    }
}
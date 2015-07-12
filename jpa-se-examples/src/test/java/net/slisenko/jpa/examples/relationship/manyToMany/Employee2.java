package net.slisenko.jpa.examples.relationship.manyToMany;

import net.slisenko.Identity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Employee2 extends Identity {

    @ManyToMany(mappedBy = "employees")
    private List<Project2> projects = new ArrayList<>();

    public Employee2() {
    }

    public Employee2(String name) {
        this.name = name;
    }

    public List<Project2> getProjects() {
        return projects;
    }

    public void setProjects(List<Project2> projects) {
        this.projects = projects;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "name='" + name + '\'' +
                "} " + super.toString();
    }
}
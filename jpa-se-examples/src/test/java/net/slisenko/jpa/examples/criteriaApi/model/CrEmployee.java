package net.slisenko.jpa.examples.criteriaApi.model;

import net.slisenko.Identity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class CrEmployee extends Identity {

    private int salary;

    @ManyToOne(cascade = CascadeType.ALL)
    private CrAddress address;

    @OneToMany(cascade = CascadeType.ALL)
    private List<CrProject> projects = new ArrayList<>();

    public CrEmployee() {
    }

    public CrEmployee(String name, int salary) {
        this.name = name;
        this.salary = salary;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public CrAddress getAddress() {
        return address;
    }

    public void setAddress(CrAddress address) {
        this.address = address;
    }

    public List<CrProject> getProjects() {
        return projects;
    }

    public void setProjects(List<CrProject> projects) {
        this.projects = projects;
    }

    @Override
    public String toString() {
        return "CrEmployee{" +
                "name='" + name + '\'' +
                ", salary=" + salary +
                "} " + super.toString();
    }
}
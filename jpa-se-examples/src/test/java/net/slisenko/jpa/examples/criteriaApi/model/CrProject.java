package net.slisenko.jpa.examples.criteriaApi.model;

import net.slisenko.Identity;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.util.HashMap;
import java.util.Map;

@Entity
public class CrProject extends Identity {

    @ElementCollection
    protected Map<String, String> tasks = new HashMap<>();

    public CrProject() {
    }

    public CrProject(String name) {
        this.name = name;
    }

    public Map<String, String> getTasks() {
        return tasks;
    }

    public void setTasks(Map<String, String> tasks) {
        this.tasks = tasks;
    }

    @Override
    public String toString() {
        return "CrProject{" +
                "name='" + name + '\'' +
                "} " + super.toString();
    }
}

package net.slisenko.jpa.examples.relationship.ordering.persistent;

import net.slisenko.Identity;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import java.util.ArrayList;
import java.util.List;

@Entity
public class PrintQueue extends Identity {

    @OneToMany
    @OrderColumn(name = "print_order")
    private List<PrintJob> jobs = new ArrayList<>();

    public List<PrintJob> getJobs() {
        return jobs;
    }

    public void setJobs(List<PrintJob> jobs) {
        this.jobs = jobs;
    }
}
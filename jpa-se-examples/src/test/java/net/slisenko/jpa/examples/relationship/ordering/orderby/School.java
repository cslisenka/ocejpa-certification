package net.slisenko.jpa.examples.relationship.ordering.orderby;

import net.slisenko.Identity;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.util.ArrayList;
import java.util.List;

@Entity
public class School extends Identity {

    @OneToMany(mappedBy = "school")
    private List<Student> students = new ArrayList<>();

    @OneToMany(mappedBy = "school")
    @OrderBy("name")
    private List<Student> studentsByName = new ArrayList<>();

    @OneToMany(mappedBy = "school")
    @OrderBy("averageScores DESC, name")
    private List<Student> studentsByNameAndBestPerformance = new ArrayList<>();

    public List<Student> getStudents() {
        return students;
    }

    public List<Student> getStudentsByName() {
        return studentsByName;
    }

    public List<Student> getStudentsByNameAndBestPerformance() {
        return studentsByNameAndBestPerformance;
    }
}
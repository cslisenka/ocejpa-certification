package net.slisenko.jpa.examples.queries;

import net.slisenko.Identity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class JPQLPresentation extends Identity {

    @ManyToOne
    private JPQLStudent student;
    private int marksObtained;

    public JPQLPresentation() {
    }

    public JPQLPresentation(String name, int marksObtained) {
        this.marksObtained = marksObtained;
        this.name = name;
    }

    public JPQLStudent getStudent() {
        return student;
    }

    public void setStudent(JPQLStudent student) {
        this.student = student;
    }

    public int getMarksObtained() {
        return marksObtained;
    }

    public void setMarksObtained(int marksObtained) {
        this.marksObtained = marksObtained;
    }
}

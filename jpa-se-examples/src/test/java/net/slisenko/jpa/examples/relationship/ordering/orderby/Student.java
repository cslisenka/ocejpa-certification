package net.slisenko.jpa.examples.relationship.ordering.orderby;

import net.slisenko.Identity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Student extends Identity {

    private String surname;
    private double averageScores;

    @ManyToOne
    private School school;

    public Student() {
    }

    public Student(String name, String surname, double averageScores, School school) {
        this.name = name;
        this.surname = surname;
        this.averageScores = averageScores;
        this.school = school;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public double getAverageScores() {
        return averageScores;
    }

    public void setAverageScores(double averageScores) {
        this.averageScores = averageScores;
    }

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", averageScores=" + averageScores +
                ", school=" + school +
                "} " + super.toString();
    }
}
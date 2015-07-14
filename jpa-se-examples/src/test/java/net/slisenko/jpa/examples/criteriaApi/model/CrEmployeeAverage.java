package net.slisenko.jpa.examples.criteriaApi.model;

public class CrEmployeeAverage {

    private int avSalary;

    private int avAge;

    public CrEmployeeAverage(int avSalary, int avAge) {
        this.avSalary = avSalary;
        this.avAge = avAge;
    }

    public int getAvSalary() {
        return avSalary;
    }

    public void setAvSalary(int avSalary) {
        this.avSalary = avSalary;
    }

    public int getAvAge() {
        return avAge;
    }

    public void setAvAge(int avAge) {
        this.avAge = avAge;
    }

    @Override
    public String toString() {
        return "CrEmployeeAverage{" +
                "avSalary=" + avSalary +
                ", avAge=" + avAge +
                '}';
    }
}

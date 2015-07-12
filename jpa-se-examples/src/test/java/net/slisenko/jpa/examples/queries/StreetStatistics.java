package net.slisenko.jpa.examples.queries;

public class StreetStatistics {

    private Street street;
    private long housesCount;
    private int maxFloors;
    private int minFloors;
    private double avgFloors;
    private long sumFloors;

    public StreetStatistics(Street street, long housesCount, int maxFloors, int minFloors, double avgFloors, long sumFloors) {
        this.street = street;
        this.housesCount = housesCount;
        this.maxFloors = maxFloors;
        this.minFloors = minFloors;
        this.avgFloors = avgFloors;
        this.sumFloors = sumFloors;
    }

    public Street getStreet() {
        return street;
    }

    public void setStreet(Street street) {
        this.street = street;
    }

    public long getHousesCount() {
        return housesCount;
    }

    public void setHousesCount(long housesCount) {
        this.housesCount = housesCount;
    }

    public int getMaxFloors() {
        return maxFloors;
    }

    public void setMaxFloors(int maxFloors) {
        this.maxFloors = maxFloors;
    }

    public int getMinFloors() {
        return minFloors;
    }

    public void setMinFloors(int minFloors) {
        this.minFloors = minFloors;
    }

    public double getAvgFloors() {
        return avgFloors;
    }

    public void setAvgFloors(double avgFloors) {
        this.avgFloors = avgFloors;
    }

    public long getSumFloors() {
        return sumFloors;
    }

    public void setSumFloors(long sumFloors) {
        this.sumFloors = sumFloors;
    }

    @Override
    public String toString() {
        return "StreetStatistics{" +
                "street=" + street +
                ", housesCount=" + housesCount +
                ", maxFloors=" + maxFloors +
                ", minFloors=" + minFloors +
                ", avgFloors=" + avgFloors +
                ", sumFloors=" + sumFloors +
                '}';
    }
}
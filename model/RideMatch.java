package model;

public class RideMatch implements Comparable<RideMatch> {

    private final Driver driver;
    private final Passenger passenger;
    private final double distance;
    private final double score;

    public RideMatch(Driver driver, Passenger passenger, double distance, double score) {
        this.driver = driver;
        this.passenger = passenger;
        this.distance = distance;
        this.score = score;
    }

    public Driver getDriver() {
        return driver;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public double getDistance() {
        return distance;
    }

    public double getScore() {
        return score;
    }

    @Override
    public int compareTo(RideMatch other) {
        return Double.compare(this.score, other.score); // Min-heap behavior
    }
}

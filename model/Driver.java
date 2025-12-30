package model;

import java.time.LocalTime;

public class Driver {

    private final String id;
    private final String name;
    private final String startLocation;
    private final String endLocation;
    private final LocalTime departureTime;
    private int availableSeats;
    private final double pricePerSeat;

    public Driver(String id, String name, String startLocation, String endLocation,
                  LocalTime departureTime, int availableSeats, double pricePerSeat) {
        this.id = id;
        this.name = name;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.departureTime = departureTime;
        this.availableSeats = availableSeats;
        this.pricePerSeat = pricePerSeat;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public double getPricePerSeat() {
        return pricePerSeat;
    }

    public void decrementSeat() {
        if (availableSeats > 0) {
            availableSeats--;
        }
    }
}

package model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Driver {

    private final String id;
    private final String name;
    private final String startLocation;
    private final String endLocation;
    private final LocalTime departureTime;
    private int availableSeats;
    private final double pricePerSeat;
    private final List<String> bookedPassengerIds; // Track passenger IDs booked

    public Driver(String id, String name, String startLocation, String endLocation,
                  LocalTime departureTime, int availableSeats, double pricePerSeat) {
        this.id = id;
        this.name = name;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.departureTime = departureTime;
        this.availableSeats = availableSeats;
        this.pricePerSeat = pricePerSeat;
        this.bookedPassengerIds = new ArrayList<>();
    }

    // -------------------- Getters --------------------
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

    public List<String> getBookedPassengerIds() {
        return bookedPassengerIds;
    }

    // -------------------- Seat Management --------------------
    public boolean hasAvailableSeats() {
        return availableSeats > 0;
    }

    public void decrementSeat(String passengerId) {
        if (availableSeats > 0) {
            availableSeats--;
            bookedPassengerIds.add(passengerId);
        }
    }

    public void incrementSeat(String passengerId) {
        if (bookedPassengerIds.remove(passengerId)) {
            availableSeats++;
        }
    }

    // -------------------- Optional --------------------
    public boolean isPassengerBooked(String passengerId) {
        return bookedPassengerIds.contains(passengerId);
    }
}
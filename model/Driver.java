package model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Driver {

    private String id;
    private String name;
    private String startLocation;
    private String endLocation;
    private LocalTime departureTime;
    private int availableSeats;
    private double pricePerSeat;
    private List<String> bookedPassengerIds; // Track passenger IDs booked

    public Driver(String id, String name) {
        this.id = id;
        this.name = name;
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
        // Initialize if null (happens when deserializing old data)
        if (bookedPassengerIds == null) {
            bookedPassengerIds = new ArrayList<>();
        }
        return bookedPassengerIds;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public void setPricePerSeat(double pricePerSeat) {
        this.pricePerSeat = pricePerSeat;
    }

    public void setBookedPassengerIds(List<String> bookedPassengerIds) {
        this.bookedPassengerIds = bookedPassengerIds != null ? bookedPassengerIds : new ArrayList<>();
    }

    // -------------------- Seat Management --------------------
    public boolean hasAvailableSeats() {
        return availableSeats > 0;
    }

    public void decrementSeat(String passengerId) {
        if (availableSeats <= 0) {
            throw new IllegalStateException("No seats available");
        }
        // Initialize if null
        if (bookedPassengerIds == null) {
            bookedPassengerIds = new ArrayList<>();
        }
        availableSeats--;
        bookedPassengerIds.add(passengerId);
    }

    public void incrementSeat(String passengerId) {
        // Initialize if null
        if (bookedPassengerIds == null) {
            bookedPassengerIds = new ArrayList<>();
        }
        if (bookedPassengerIds.remove(passengerId)) {
            availableSeats++;
        }
    }

    // -------------------- Optional --------------------
    public boolean isPassengerBooked(String passengerId) {
        // Initialize if null
        if (bookedPassengerIds == null) {
            bookedPassengerIds = new ArrayList<>();
        }
        return bookedPassengerIds.contains(passengerId);
    }
}
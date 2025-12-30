package model;

import java.time.LocalTime;

public class Passenger {

    private final String id;
    private final String name;
    private final String pickupLocation;
    private final String dropOffLocation;
    private final LocalTime preferredTime;

    public Passenger(String id, String name, String pickupLocation,
                     String dropOffLocation, LocalTime preferredTime) {
        this.id = id;
        this.name = name;
        this.pickupLocation = pickupLocation;
        this.dropOffLocation = dropOffLocation;
        this.preferredTime = preferredTime;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public String getDropOffLocation() {
        return dropOffLocation;
    }

    public LocalTime getPreferredTime() {
        return preferredTime;
    }
}

package model;

import java.time.LocalTime;

public class Passenger {

    private String id;
    private String name;
    private String pickupLocation;
    private String dropOffLocation;
    private LocalTime preferredTime;

    public Passenger(String id, String name) {
        this.id = id;
        this.name = name;
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

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public void setDropOffLocation(String dropOffLocation) {
        this.dropOffLocation = dropOffLocation;
    }

    public void setPreferredTime(LocalTime preferredTime) {
        this.preferredTime = preferredTime;
    }
}

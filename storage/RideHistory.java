package storage;

import java.util.ArrayList;
import java.util.List;
import model.Driver;
import model.Passenger;
import model.RideMatch;

public class RideHistory {

    private final List<RideMatch> history;

    public RideHistory() {
        this.history = new ArrayList<>();
    }

    public void addRide(RideMatch match) {
        this.history.add(match);
    }

    public void removeRide(RideMatch match) {
        this.history.remove(match);
    }

    public List<RideMatch> getHistory() {
        return this.history;
    }

    public boolean hasActiveRide(Passenger p) {
        return getActiveRideForPassenger(p) != null;
    }

    public RideMatch getActiveRideForPassenger(Passenger p) {
        for (RideMatch r : history) {
            if (r.getPassenger().equals(p)) {
                return r;
            }
        }
        return null;
    }

    public List<RideMatch> getRidesForPassenger(Passenger p) {
        List<RideMatch> rides = new ArrayList<>();
        for (RideMatch r : this.history) {
            if (r.getPassenger().equals(p)) {
                rides.add(r);
            }
        }
        return rides;
    }

    public List<RideMatch> getActiveRidesForDriver(Driver d) {
        List<RideMatch> rides = new ArrayList<>();
        for (RideMatch r : this.history) {
            if (r.getDriver().equals(d)) {
                rides.add(r);
            }
        }
        return rides;
    }

}

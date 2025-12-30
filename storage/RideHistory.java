package storage;

import model.RideMatch;

import java.util.ArrayList;
import java.util.List;

public class RideHistory {

    private final List<RideMatch> history;

    public RideHistory() {
        this.history = new ArrayList<>();
    }

    public void addRide(RideMatch match) {
        history.add(match);
    }

    public List<RideMatch> getHistory() {
        return history;
    }
}

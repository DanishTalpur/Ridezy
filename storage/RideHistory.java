package storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.*;
import java.io.*;
import java.nio.file.*;
import java.lang.reflect.Type;
import java.util.*;

public class RideHistory {

    private final List<RideMatch> history;
    private final Map<String, RideStatus> rideStatusMap;
    private final Map<String, Double> rideDistanceMap;
    private final Map<String, Double> rideScoreMap;
    private final Map<String, String> ridePickupLocationMap;
    private final Map<String, String> rideDropOffLocationMap;
    private static final String RIDES_FILE = "rides.json";
    private final UserStorage userStorage;
    private final Gson gson;

    public RideHistory(UserStorage userStorage) {
        this.history = new ArrayList<>();
        this.rideStatusMap = new HashMap<>();
        this.rideDistanceMap = new HashMap<>();
        this.rideScoreMap = new HashMap<>();
        this.ridePickupLocationMap = new HashMap<>();
        this.rideDropOffLocationMap = new HashMap<>();
        this.userStorage = userStorage;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        loadRides();
    }

    public void addRide(RideMatch match) {
        String key = getRideKey(match.getPassenger().getId(), match.getDriver().getId());
        boolean exists = history.stream()
                .anyMatch(m -> m.getPassenger().getId().equals(match.getPassenger().getId())
                        && m.getDriver().getId().equals(match.getDriver().getId()));

        if (!exists) {
            this.history.add(match);
        }

        this.rideStatusMap.put(key, RideStatus.ACTIVE);
        this.rideDistanceMap.put(key, match.getDistance());
        this.rideScoreMap.put(key, match.getScore());

        String pickup = match.getPassenger().getPickupLocation();
        String dropoff = match.getPassenger().getDropOffLocation();
        this.ridePickupLocationMap.put(key, (pickup != null && !pickup.trim().isEmpty()) ? pickup.trim() : "");
        this.rideDropOffLocationMap.put(key, (dropoff != null && !dropoff.trim().isEmpty()) ? dropoff.trim() : "");
        saveRides();
    }

    public void addRide(RideMatch match, String pickupLocation, String dropOffLocation) {
        String key = getRideKey(match.getPassenger().getId(), match.getDriver().getId());
        boolean exists = history.stream()
                .anyMatch(m -> m.getPassenger().getId().equals(match.getPassenger().getId())
                        && m.getDriver().getId().equals(match.getDriver().getId()));

        if (!exists) {
            this.history.add(match);
        }

        this.rideStatusMap.put(key, RideStatus.ACTIVE);
        this.rideDistanceMap.put(key, match.getDistance());
        this.rideScoreMap.put(key, match.getScore());
        this.ridePickupLocationMap.put(key, (pickupLocation != null && !pickupLocation.trim().isEmpty()) ? pickupLocation.trim() : "");
        this.rideDropOffLocationMap.put(key, (dropOffLocation != null && !dropOffLocation.trim().isEmpty()) ? dropOffLocation.trim() : "");
        saveRides();
    }

    public void markRideCompleted(Passenger passenger) {
        for (RideMatch match : history) {
            if (match.getPassenger().getId().equals(passenger.getId())) {
                String key = getRideKey(passenger.getId(), match.getDriver().getId());
                if (rideStatusMap.getOrDefault(key, RideStatus.ACTIVE) == RideStatus.ACTIVE) {
                    rideStatusMap.put(key, RideStatus.COMPLETED);
                    saveRides();
                    return;
                }
            }
        }
    }

    public void markRideCompletedDriver(Driver driver) {
        boolean updated = false;
        for (RideMatch match : history) {
            if (match.getDriver().getId().equals(driver.getId())) {
                String key = getRideKey(match.getPassenger().getId(), driver.getId());
                if (rideStatusMap.getOrDefault(key, RideStatus.ACTIVE) == RideStatus.ACTIVE) {
                    rideStatusMap.put(key, RideStatus.COMPLETED);
                    updated = true;
                }
            }
        }
        if (updated) {
            saveRides();
        }
    }

    public void removeRide(RideMatch match) {
        String key = getRideKey(match.getPassenger().getId(), match.getDriver().getId());
        this.history.remove(match);
        this.rideStatusMap.remove(key);
        this.rideDistanceMap.remove(key);
        this.rideScoreMap.remove(key);
        this.ridePickupLocationMap.remove(key);
        this.rideDropOffLocationMap.remove(key);
        saveRides();
    }

    public List<RideMatch> getHistory() {
        return this.history;
    }

    public boolean hasActiveRide(Passenger passenger) {
        return history.stream()
                .filter(match -> match.getPassenger().getId().equals(passenger.getId()))
                .anyMatch(match -> {
                    String key = getRideKey(passenger.getId(), match.getDriver().getId());
                    return rideStatusMap.getOrDefault(key, RideStatus.ACTIVE) == RideStatus.ACTIVE;
                });
    }

    public boolean hasActiveRide(Driver driver) {
        return driver.getStartLocation() != null
                && driver.getEndLocation() != null
                && driver.getDepartureTime() != null
                && driver.getAvailableSeats() > 0;
    }


    public RideMatch getActiveRideForPassenger(Passenger p) {
        return history.stream()
                .filter(r -> r.getPassenger().getId().equals(p.getId()))
                .filter(r -> {
                    String key = getRideKey(p.getId(), r.getDriver().getId());
                    return rideStatusMap.getOrDefault(key, RideStatus.ACTIVE) == RideStatus.ACTIVE;
                })
                .findFirst()
                .orElse(null);
    }

    public List<RideMatch> getRidesForPassenger(Passenger p) {
        return history.stream()
                .filter(r -> r.getPassenger().getId().equals(p.getId()))
                .toList();
    }

    public List<RideMatch> getActiveRidesForDriver(Driver d) {
        return history.stream()
                .filter(r -> r.getDriver().getId().equals(d.getId()))
                .filter(r -> {
                    String key = getRideKey(r.getPassenger().getId(), d.getId());
                    return rideStatusMap.getOrDefault(key, RideStatus.ACTIVE) == RideStatus.ACTIVE;
                })
                .toList();
    }

    public List<RideMatch> getRidesForDriver(Driver d) {
        return history.stream()
                .filter(r -> r.getDriver().getId().equals(d.getId()))
                .toList();
    }

    public RideStatus getRideStatus(RideMatch match) {
        String key = getRideKey(match.getPassenger().getId(), match.getDriver().getId());
        return rideStatusMap.getOrDefault(key, RideStatus.ACTIVE);
    }

    public String getPickupLocation(RideMatch match) {
        String key = getRideKey(match.getPassenger().getId(), match.getDriver().getId());
        return ridePickupLocationMap.getOrDefault(key, "");
    }

    public String getDropOffLocation(RideMatch match) {
        String key = getRideKey(match.getPassenger().getId(), match.getDriver().getId());
        return rideDropOffLocationMap.getOrDefault(key, "");
    }

    private String getRideKey(String passengerId, String driverId) {
        return passengerId + "-" + driverId;
    }

    private void loadRides() {
        try {
            Type listType = new TypeToken<List<RideRecord>>(){}.getType();
            List<RideRecord> records =
                    Database.loadFromFile(RIDES_FILE, listType);

            if (records == null) return;

            for (RideRecord record : records) {
                Driver driver = userStorage.getDrivers().get(record.driverId);
                Passenger passenger = userStorage.getPassengers().get(record.passengerId);

                if (driver != null && passenger != null) {
                    String key = getRideKey(record.passengerId, record.driverId);

                    boolean exists = history.stream()
                            .anyMatch(m -> m.getPassenger().getId().equals(record.passengerId)
                                    && m.getDriver().getId().equals(record.driverId));

                    if (!exists) {
                        RideMatch match = new RideMatch(driver, passenger, record.distance, record.score);
                        history.add(match);
                    }

                    rideStatusMap.put(key, record.status);
                    rideDistanceMap.put(key, record.distance);
                    rideScoreMap.put(key, record.score);
                    ridePickupLocationMap.put(key, record.pickupLocation != null ? record.pickupLocation : "");
                    rideDropOffLocationMap.put(key, record.dropOffLocation != null ? record.dropOffLocation : "");
                }
            }
        } catch (Exception e) {
            System.err.println("Could not load rides: " + e.getMessage());
        }
    }

    private void saveRides() {
        try {
            List<RideRecord> records = new ArrayList<>();
            for (RideMatch match : history) {
                String key = getRideKey(match.getPassenger().getId(), match.getDriver().getId());
                RideRecord record = new RideRecord();
                record.passengerId = match.getPassenger().getId();
                record.driverId = match.getDriver().getId();
                record.status = rideStatusMap.getOrDefault(key, RideStatus.ACTIVE);
                record.distance = rideDistanceMap.getOrDefault(key, match.getDistance());
                record.score = rideScoreMap.getOrDefault(key, match.getScore());
                record.pickupLocation = ridePickupLocationMap.getOrDefault(key, "");
                record.dropOffLocation = rideDropOffLocationMap.getOrDefault(key, "");
                records.add(record);
            }
            Database.saveToFile(RIDES_FILE, records);
        } catch (Exception e) {
            System.err.println("Could not save rides: " + e.getMessage());
        }
    }

    private static class RideRecord {
        String passengerId;
        String driverId;
        RideStatus status;
        double distance;
        double score;
        String pickupLocation;
        String dropOffLocation;
    }


}

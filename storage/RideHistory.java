package storage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.Driver;
import model.Passenger;
import model.RideMatch;
import model.RideStatus;

public class RideHistory {

    private final List<RideMatch> history;
    private final Map<String, RideStatus> rideStatusMap; // Key: passengerId-driverId, Value: status
    private final Map<String, Double> rideDistanceMap; // Key: passengerId-driverId, Value: distance
    private final Map<String, Double> rideScoreMap; // Key: passengerId-driverId, Value: score
    private final Map<String, String> ridePickupLocationMap; // Key: passengerId-driverId, Value: pickup location
    private final Map<String, String> rideDropOffLocationMap; // Key: passengerId-driverId, Value: dropoff location
    private static final String RIDES_FILE = "data/rides.json";
    private final UserStorage userStorage;

    public RideHistory(UserStorage userStorage) {
        this.history = new ArrayList<>();
        this.rideStatusMap = new HashMap<>();
        this.rideDistanceMap = new HashMap<>();
        this.rideScoreMap = new HashMap<>();
        this.ridePickupLocationMap = new HashMap<>();
        this.rideDropOffLocationMap = new HashMap<>();
        this.userStorage = userStorage;
        loadRides();
    }

    public void addRide(RideMatch match) {
        String key = getRideKey(match.getPassenger().getId(), match.getDriver().getId());
        // Check if ride already exists - if it does, don't add again
        boolean exists = false;
        for (RideMatch existingMatch : history) {
            if (existingMatch.getPassenger().getId().equals(match.getPassenger().getId()) &&
                existingMatch.getDriver().getId().equals(match.getDriver().getId())) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            this.history.add(match);
        }
        // Always update the maps (in case ride already existed)
        this.rideStatusMap.put(key, RideStatus.ACTIVE);
        this.rideDistanceMap.put(key, match.getDistance());
        this.rideScoreMap.put(key, match.getScore());
        // Save passenger's pickup and dropoff locations for this ride
        String pickup = match.getPassenger().getPickupLocation();
        String dropoff = match.getPassenger().getDropOffLocation();
        this.ridePickupLocationMap.put(key, (pickup != null && !pickup.trim().isEmpty()) ? pickup.trim() : "");
        this.rideDropOffLocationMap.put(key, (dropoff != null && !dropoff.trim().isEmpty()) ? dropoff.trim() : "");
        saveRides();
    }

    public void addRide(RideMatch match, String pickupLocation, String dropOffLocation) {
        String key = getRideKey(match.getPassenger().getId(), match.getDriver().getId());
        // Check if ride already exists - if it does, don't add again
        boolean exists = false;
        for (RideMatch existingMatch : history) {
            if (existingMatch.getPassenger().getId().equals(match.getPassenger().getId()) &&
                existingMatch.getDriver().getId().equals(match.getDriver().getId())) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            this.history.add(match);
        }
        // Always update the maps (in case ride already existed)
        this.rideStatusMap.put(key, RideStatus.ACTIVE);
        this.rideDistanceMap.put(key, match.getDistance());
        this.rideScoreMap.put(key, match.getScore());
        // Save pickup and dropoff locations explicitly
        this.ridePickupLocationMap.put(key, (pickupLocation != null && !pickupLocation.trim().isEmpty()) ? pickupLocation.trim() : "");
        this.rideDropOffLocationMap.put(key, (dropOffLocation != null && !dropOffLocation.trim().isEmpty()) ? dropOffLocation.trim() : "");
        saveRides();
    }

    public void markRideCompleted(Passenger passenger) {
        for (RideMatch match : history) {
            if (match.getPassenger().equals(passenger)) {
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
            if (match.getDriver().equals(driver)) {
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
        for (RideMatch match : history) {
            if (match.getPassenger().equals(passenger)) {
                String key = getRideKey(passenger.getId(), match.getDriver().getId());
                if (rideStatusMap.getOrDefault(key, RideStatus.ACTIVE) == RideStatus.ACTIVE) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasActiveRide(Driver driver) {
        for (RideMatch match : history) {
            if (match.getDriver().equals(driver)) {
                String key = getRideKey(match.getPassenger().getId(), driver.getId());
                if (rideStatusMap.getOrDefault(key, RideStatus.ACTIVE) == RideStatus.ACTIVE) {
                    return true;
                }
            }
        }
        return false;
    }

    public RideMatch getActiveRideForPassenger(Passenger p) {
        for (RideMatch r : history) {
            if (r.getPassenger().equals(p)) {
                String key = getRideKey(p.getId(), r.getDriver().getId());
                if (rideStatusMap.getOrDefault(key, RideStatus.ACTIVE) == RideStatus.ACTIVE) {
                    return r;
                }
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
                String key = getRideKey(r.getPassenger().getId(), d.getId());
                if (rideStatusMap.getOrDefault(key, RideStatus.ACTIVE) == RideStatus.ACTIVE) {
                    rides.add(r);
                }
            }
        }
        return rides;
    }

    public List<RideMatch> getRidesForDriver(Driver d) {
        List<RideMatch> rides = new ArrayList<>();
        for (RideMatch r : this.history) {
            if (r.getDriver().equals(d)) {
                rides.add(r);
            }
        }
        return rides;
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
            if (!Files.exists(Paths.get(RIDES_FILE))) {
                return;
            }
            String content = new String(Files.readAllBytes(Paths.get(RIDES_FILE))).trim();
            if (content.isEmpty() || content.equals("[]")) {
                return;
            }
            List<RideRecord> records = parseRides(content);
            for (RideRecord record : records) {
                Driver driver = userStorage.getDrivers().get(record.driverId);
                Passenger passenger = userStorage.getPassengers().get(record.passengerId);
                if (driver != null && passenger != null) {
                    String key = getRideKey(record.passengerId, record.driverId);
                    // Check if ride already exists in history to avoid duplicates
                    boolean exists = false;
                    for (RideMatch existingMatch : history) {
                        if (existingMatch.getPassenger().getId().equals(record.passengerId) &&
                            existingMatch.getDriver().getId().equals(record.driverId)) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        RideMatch match = new RideMatch(driver, passenger, record.distance, record.score);
                        history.add(match);
                    }
                    // Always update the maps (restore state)
                    rideStatusMap.put(key, record.status);
                    rideDistanceMap.put(key, record.distance);
                    rideScoreMap.put(key, record.score);
                    // Restore pickup and dropoff locations
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
            Files.createDirectories(Paths.get(RIDES_FILE).getParent());
            StringBuilder sb = new StringBuilder();
            sb.append("[\n");
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
            for (int i = 0; i < records.size(); i++) {
                RideRecord r = records.get(i);
                sb.append("  {\n");
                sb.append("    \"passengerId\": \"").append(escapeJson(r.passengerId)).append("\",\n");
                sb.append("    \"driverId\": \"").append(escapeJson(r.driverId)).append("\",\n");
                sb.append("    \"status\": \"").append(r.status.name()).append("\",\n");
                sb.append("    \"distance\": ").append(r.distance).append(",\n");
                sb.append("    \"score\": ").append(r.score).append(",\n");
                sb.append("    \"pickupLocation\": \"").append(escapeJson(r.pickupLocation != null ? r.pickupLocation : "")).append("\",\n");
                sb.append("    \"dropOffLocation\": \"").append(escapeJson(r.dropOffLocation != null ? r.dropOffLocation : "")).append("\"\n");
                sb.append("  }");
                if (i < records.size() - 1) {
                    sb.append(",");
                }
                sb.append("\n");
            }
            sb.append("]");
            Files.write(Paths.get(RIDES_FILE), sb.toString().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<RideRecord> parseRides(String json) {
        List<RideRecord> list = new ArrayList<>();
        // Pattern to match full format with locations: { "passengerId": "...", "driverId": "...", "status": "...", "distance": ..., "score": ..., "pickupLocation": "...", "dropOffLocation": "..." }
        Pattern fullPatternWithLocations = Pattern.compile(
            "\\{\\s*\"passengerId\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"driverId\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*" +
            "\"status\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"distance\"\\s*:\\s*([\\d.]+)\\s*,\\s*\"score\"\\s*:\\s*([\\d.]+)\\s*,\\s*" +
            "\"pickupLocation\"\\s*:\\s*\"([^\"]*)\"\\s*,\\s*\"dropOffLocation\"\\s*:\\s*\"([^\"]*)\"\\s*\\}"
        );
        // Pattern to match full format without locations: { "passengerId": "...", "driverId": "...", "status": "...", "distance": ..., "score": ... }
        Pattern fullPattern = Pattern.compile(
            "\\{\\s*\"passengerId\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"driverId\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*" +
            "\"status\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"distance\"\\s*:\\s*([\\d.]+)\\s*,\\s*\"score\"\\s*:\\s*([\\d.]+)\\s*\\}"
        );
        // Pattern to match simple format: { "passengerId": "...", "driverId": "...", "status": "..." }
        Pattern simplePattern = Pattern.compile(
            "\\{\\s*\"passengerId\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"driverId\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*" +
            "\"status\"\\s*:\\s*\"([^\"]+)\"\\s*\\}"
        );
        
        Matcher fullMatcherWithLocations = fullPatternWithLocations.matcher(json);
        while (fullMatcherWithLocations.find()) {
            RideRecord record = new RideRecord();
            record.passengerId = fullMatcherWithLocations.group(1);
            record.driverId = fullMatcherWithLocations.group(2);
            record.status = RideStatus.valueOf(fullMatcherWithLocations.group(3));
            record.distance = Double.parseDouble(fullMatcherWithLocations.group(4));
            record.score = Double.parseDouble(fullMatcherWithLocations.group(5));
            record.pickupLocation = fullMatcherWithLocations.group(6);
            record.dropOffLocation = fullMatcherWithLocations.group(7);
            list.add(record);
        }
        
        // If no matches with full pattern with locations, try pattern without locations
        if (list.isEmpty()) {
            Matcher fullMatcher = fullPattern.matcher(json);
            while (fullMatcher.find()) {
                RideRecord record = new RideRecord();
                record.passengerId = fullMatcher.group(1);
                record.driverId = fullMatcher.group(2);
                record.status = RideStatus.valueOf(fullMatcher.group(3));
                record.distance = Double.parseDouble(fullMatcher.group(4));
                record.score = Double.parseDouble(fullMatcher.group(5));
                record.pickupLocation = ""; // Default for old format
                record.dropOffLocation = ""; // Default for old format
                list.add(record);
            }
        }
        
        // If still no matches, try simple pattern
        if (list.isEmpty()) {
            Matcher simpleMatcher = simplePattern.matcher(json);
            while (simpleMatcher.find()) {
                RideRecord record = new RideRecord();
                record.passengerId = simpleMatcher.group(1);
                record.driverId = simpleMatcher.group(2);
                record.status = RideStatus.valueOf(simpleMatcher.group(3));
                record.distance = 0.0; // Default values for old format
                record.score = 0.0;
                record.pickupLocation = ""; // Default for old format
                record.dropOffLocation = ""; // Default for old format
                list.add(record);
            }
        }
        return list;
    }

    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }

    // Inner class to represent a ride record for JSON storage
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
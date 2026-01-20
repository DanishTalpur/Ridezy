package storage;

import model.Driver;
import model.Passenger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserStorage {

    private final Map<String, Driver> drivers;
    private static final String DRIVER_FILE = "data/drivers.json";
    private static final String PASSENGER_FILE = "data/passengers.json";
    private final Map<String, Passenger> passengers;

    public UserStorage() {
        this.drivers = new HashMap<>();
        this.passengers = new HashMap<>();
        loadPassengers();
        loadDrivers();
    }

    public void addDriver(Driver driver) {
        drivers.put(driver.getId(), driver);
        saveDriversInternal();
    }

    public void addPassenger(Passenger passenger) {
        passengers.put(passenger.getId(), passenger);
        savePassengersInternal();
    }

    public Map<String, Driver> getDrivers() {
        return drivers;
    }

    public Map<String, Passenger> getPassengers() {
        return passengers;
    }

    public void saveAll() {
        saveDriversInternal();
        savePassengersInternal();
    }

    public void saveDrivers() {
        saveDriversInternal();
    }

    public void savePassengers() {
        savePassengersInternal();
    }

    private void loadPassengers() {
        try {
            if (!Files.exists(Paths.get(PASSENGER_FILE))) {
                return;
            }
            String content = new String(Files.readAllBytes(Paths.get(PASSENGER_FILE))).trim();
            if (content.isEmpty() || content.equals("[]")) {
                return;
            }
            List<Passenger> list = parsePassengers(content);
            for (Passenger p : list) {
                passengers.put(p.getId(), p);
            }
        } catch (Exception e) {
            System.err.println("Could not load passengers: " + e.getMessage());
        }
    }

    private void loadDrivers() {
        try {
            if (!Files.exists(Paths.get(DRIVER_FILE))) {
                return;
            }
            String content = new String(Files.readAllBytes(Paths.get(DRIVER_FILE))).trim();
            if (content.isEmpty() || content.equals("[]")) {
                return;
            }
            List<Driver> list = parseDrivers(content);
            for (Driver d : list) {
                drivers.put(d.getId(), d);
            }
        } catch (Exception e) {
            System.err.println("Could not load drivers: " + e.getMessage());
        }
    }

    private List<Passenger> parsePassengers(String json) {
        List<Passenger> list = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\{\\s*\"id\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"name\"\\s*:\\s*\"([^\"]+)\"\\s*\\}");
        Matcher matcher = pattern.matcher(json);
        while (matcher.find()) {
            String id = matcher.group(1);
            String name = matcher.group(2);
            list.add(new Passenger(id, name));
        }
        return list;
    }

    private List<Driver> parseDrivers(String json) {
        List<Driver> list = new ArrayList<>();
        // Pattern to match driver objects with all fields
        Pattern pattern = Pattern.compile(
            "\\{\\s*\"id\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"name\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*" +
            "\"startLocation\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"endLocation\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*" +
            "\"departureTime\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"availableSeats\"\\s*:\\s*(\\d+)\\s*,\\s*" +
            "\"pricePerSeat\"\\s*:\\s*(\\d+(?:\\.\\d+)?)\\s*,\\s*\"bookedPassengerIds\"\\s*:\\s*\\[([^\\]]*)\\]\\s*\\}"
        );
        Matcher matcher = pattern.matcher(json);
        while (matcher.find()) {
            String id = matcher.group(1);
            String name = matcher.group(2);
            String startLocation = matcher.group(3);
            String endLocation = matcher.group(4);
            String departureTimeStr = matcher.group(5);
            int availableSeats = Integer.parseInt(matcher.group(6));
            double pricePerSeat = Double.parseDouble(matcher.group(7));
            String bookedIdsStr = matcher.group(8);

            Driver driver = new Driver(id, name);
            driver.setStartLocation(startLocation);
            driver.setEndLocation(endLocation);
            if (departureTimeStr != null && !departureTimeStr.isEmpty()) {
                driver.setDepartureTime(LocalTime.parse(departureTimeStr));
            }
            driver.setAvailableSeats(availableSeats);
            driver.setPricePerSeat(pricePerSeat);

            // Parse booked passenger IDs
            if (bookedIdsStr != null && !bookedIdsStr.trim().isEmpty()) {
                Pattern idPattern = Pattern.compile("\"([^\"]+)\"");
                Matcher idMatcher = idPattern.matcher(bookedIdsStr);
                List<String> bookedIds = new ArrayList<>();
                while (idMatcher.find()) {
                    bookedIds.add(idMatcher.group(1));
                }
                driver.setBookedPassengerIds(bookedIds);
            }

            list.add(driver);
        }
        return list;
    }

    private void savePassengersInternal() {
        try {
            Files.createDirectories(Paths.get(PASSENGER_FILE).getParent());
            StringBuilder sb = new StringBuilder();
            sb.append("[\n");
            List<Passenger> list = new ArrayList<>(passengers.values());
            for (int i = 0; i < list.size(); i++) {
                Passenger p = list.get(i);
                sb.append("  { \"id\": \"").append(escapeJson(p.getId()))
                  .append("\", \"name\": \"").append(escapeJson(p.getName())).append("\" }");
                if (i < list.size() - 1) {
                    sb.append(",");
                }
                sb.append("\n");
            }
            sb.append("]");
            Files.write(Paths.get(PASSENGER_FILE), sb.toString().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveDriversInternal() {
        try {
            Files.createDirectories(Paths.get(DRIVER_FILE).getParent());
            StringBuilder sb = new StringBuilder();
            sb.append("[\n");
            List<Driver> list = new ArrayList<>(drivers.values());
            for (int i = 0; i < list.size(); i++) {
                Driver d = list.get(i);
                sb.append("  {\n");
                sb.append("    \"id\": \"").append(escapeJson(d.getId())).append("\",\n");
                sb.append("    \"name\": \"").append(escapeJson(d.getName())).append("\",\n");
                sb.append("    \"startLocation\": \"").append(escapeJson(d.getStartLocation() != null ? d.getStartLocation() : "")).append("\",\n");
                sb.append("    \"endLocation\": \"").append(escapeJson(d.getEndLocation() != null ? d.getEndLocation() : "")).append("\",\n");
                sb.append("    \"departureTime\": \"").append(d.getDepartureTime() != null ? d.getDepartureTime().toString() : "").append("\",\n");
                sb.append("    \"availableSeats\": ").append(d.getAvailableSeats()).append(",\n");
                sb.append("    \"pricePerSeat\": ").append(d.getPricePerSeat()).append(",\n");
                sb.append("    \"bookedPassengerIds\": [");
                if (d.getBookedPassengerIds() != null && !d.getBookedPassengerIds().isEmpty()) {
                    for (int j = 0; j < d.getBookedPassengerIds().size(); j++) {
                        sb.append("\"").append(escapeJson(d.getBookedPassengerIds().get(j))).append("\"");
                        if (j < d.getBookedPassengerIds().size() - 1) {
                            sb.append(", ");
                        }
                    }
                }
                sb.append("]\n");
                sb.append("  }");
                if (i < list.size() - 1) {
                    sb.append(",");
                }
                sb.append("\n");
            }
            sb.append("]");
            Files.write(Paths.get(DRIVER_FILE), sb.toString().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}
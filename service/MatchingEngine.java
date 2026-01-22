package service;

import algorithm.Dijkstra;
import datastructures.Graph;
import model.*;
import storage.RideHistory;
import storage.UserStorage;

import java.time.Duration;
import java.util.Map;
import java.util.PriorityQueue;

public class MatchingEngine {

    private final Graph graph;
    private final UserStorage storage;
    private final RideHistory history;
    private static final String UNIVERSITY = "University";

    public MatchingEngine(Graph graph, UserStorage storage, RideHistory history) {
        this.graph = graph;
        this.storage = storage;
        this.history = history;
    }

    public PriorityQueue<RideMatch> findMatches(Passenger passenger) {
        PriorityQueue<RideMatch> matches = new PriorityQueue<>();

        // Validate passenger locations exist in graph (case-insensitive)
        String passengerPickup = passenger.getPickupLocation();
        String passengerDropoff = passenger.getDropOffLocation();

        System.out.println("DEBUG: Passenger pickup: " + passengerPickup + ", dropoff: " + passengerDropoff);

        if (passengerPickup == null || passengerDropoff == null) {
            System.out.println("DEBUG: Passenger locations are null");
            return matches;
        }

        // Find matching location names (case-insensitive)
        String matchedPickup = findMatchingLocation(graph, passengerPickup);
        String matchedDropoff = findMatchingLocation(graph, passengerDropoff);

        System.out.println("DEBUG: Matched pickup: " + matchedPickup + ", matched dropoff: " + matchedDropoff);

        if (matchedPickup == null || matchedDropoff == null) {
            System.out.println("DEBUG: Locations not found in graph");
            return matches; // Locations not found in graph
        }

        passengerPickup = matchedPickup;
        passengerDropoff = matchedDropoff;

        // Calculate distance for passenger's route
        Map<String, Double> distances = Dijkstra.shortestPaths(graph, passengerPickup);
        double passengerDistance = distances.getOrDefault(passengerDropoff, Double.MAX_VALUE);

        System.out.println("DEBUG: Passenger route distance: " + passengerDistance);

        if (passengerDistance == Double.MAX_VALUE) {
            System.out.println("DEBUG: No valid route for passenger");
            return matches; // No valid route for passenger
        }

        System.out.println("DEBUG: Checking drivers...");
        int driverCount = 0;

        for (Driver driver : storage.getDrivers().values()) {
            driverCount++;
            System.out.println("\nDEBUG: Checking driver #" + driverCount + ": " + driver.getName());

            // Only include drivers who have created an active ride
            if (driver.getStartLocation() == null || driver.getStartLocation().trim().isEmpty() ||
                    driver.getEndLocation() == null || driver.getEndLocation().trim().isEmpty() ||
                    driver.getDepartureTime() == null) {
                System.out.println("DEBUG: Driver has incomplete ride info");
                System.out.println("  - Start: " + driver.getStartLocation());
                System.out.println("  - End: " + driver.getEndLocation());
                System.out.println("  - Time: " + driver.getDepartureTime());
                continue;
            }

            // Check if driver has available seats
            int availableSeats = driver.getAvailableSeats();
            System.out.println("DEBUG: Driver available seats: " + availableSeats);
            if (availableSeats <= 0) {
                System.out.println("DEBUG: Driver has no available seats");
                continue;
            }

            // Validate driver locations exist in graph (case-insensitive)
            String driverStart = driver.getStartLocation();
            String driverEnd = driver.getEndLocation();

            System.out.println("DEBUG: Driver route: " + driverStart + " -> " + driverEnd);

            if (driverStart == null || driverEnd == null) {
                System.out.println("DEBUG: Driver locations are null");
                continue;
            }

            String matchedDriverStart = findMatchingLocation(graph, driverStart);
            String matchedDriverEnd = findMatchingLocation(graph, driverEnd);

            System.out.println("DEBUG: Matched driver route: " + matchedDriverStart + " -> " + matchedDriverEnd);

            if (matchedDriverStart == null || matchedDriverEnd == null) {
                System.out.println("DEBUG: Driver locations not found in graph");
                continue;
            }

            driverStart = matchedDriverStart;
            driverEnd = matchedDriverEnd;

            // Check if driver's route can accommodate passenger's route
            Map<String, Double> driverDistances = Dijkstra.shortestPaths(graph, driverStart);
            double distToPassengerPickup = driverDistances.getOrDefault(passengerPickup, Double.MAX_VALUE);
            double distToPassengerDropoff = driverDistances.getOrDefault(passengerDropoff, Double.MAX_VALUE);

            System.out.println("DEBUG: Distance from driver start to passenger pickup: " + distToPassengerPickup);
            System.out.println("DEBUG: Distance from driver start to passenger dropoff: " + distToPassengerDropoff);

            // Both locations must be reachable from driver start
            if (distToPassengerPickup == Double.MAX_VALUE || distToPassengerDropoff == Double.MAX_VALUE) {
                System.out.println("DEBUG: Passenger locations not reachable from driver start");
                continue;
            }

            // Passenger pickup should come before dropoff along driver's route
            if (distToPassengerPickup >= distToPassengerDropoff) {
                System.out.println("DEBUG: Pickup not before dropoff along driver route (pickup: " + distToPassengerPickup + ", dropoff: " + distToPassengerDropoff + ")");
                continue;
            }

            // Verify passenger dropoff is reachable from passenger pickup
            Map<String, Double> pickupDistances = Dijkstra.shortestPaths(graph, passengerPickup);
            double distPickupToDropoff = pickupDistances.getOrDefault(passengerDropoff, Double.MAX_VALUE);

            System.out.println("DEBUG: Distance from passenger pickup to dropoff: " + distPickupToDropoff);

            if (distPickupToDropoff == Double.MAX_VALUE) {
                System.out.println("DEBUG: No path from pickup to dropoff");
                continue;
            }

            // Skip if passenger already has a completed ride with this driver
            boolean hasCompletedRide = history.getHistory().stream()
                    .filter(match -> match.getPassenger().getId().equals(passenger.getId())
                            && match.getDriver().getId().equals(driver.getId()))
                    .anyMatch(match -> history.getRideStatus(match) == RideStatus.COMPLETED);

            if (hasCompletedRide) {
                System.out.println("DEBUG: Passenger already has completed ride with this driver");
                continue;
            }

            // Calculate time difference
            long timeDiff = Math.abs(
                    Duration.between(driver.getDepartureTime(),
                            passenger.getPreferredTime()).toMinutes()
            );

            // Use passenger's route distance for scoring
            double score = (passengerDistance * 0.5) + (timeDiff * 0.3) + (driver.getPricePerSeat() * 0.2);

            System.out.println("DEBUG: ✓ Match found with driver " + driver.getName() + " | Score: " + score);
            matches.offer(new RideMatch(driver, passenger, passengerDistance, score));
        }

        System.out.println("\nDEBUG: Total drivers checked: " + driverCount);
        System.out.println("DEBUG: Total matches found: " + matches.size());
        return matches;
    }

    // Helper method to find matching location (case-insensitive)
    private String findMatchingLocation(Graph graph, String location) {
        if (location == null) return null;
        String trimmed = location.trim();

        // Exact match
        if (graph.getLocations().contains(trimmed)) {
            return trimmed;
        }

        // Case-insensitive match
        for (String graphLocation : graph.getLocations()) {
            if (graphLocation.equalsIgnoreCase(trimmed)) {
                return graphLocation;
            }
        }

        // Handle common spelling variations
        if (trimmed.equalsIgnoreCase("Defense")) {
            for (String graphLocation : graph.getLocations()) {
                if (graphLocation.equalsIgnoreCase("Defence")) {
                    return graphLocation;
                }
            }
        }

        return null;
    }
}
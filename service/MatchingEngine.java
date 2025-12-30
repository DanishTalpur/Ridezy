package service;

import algorithm.Dijkstra;
import datastructures.Graph;
import model.Driver;
import model.Passenger;
import model.RideMatch;
import storage.UserStorage;

import java.time.Duration;
import java.util.Map;
import java.util.PriorityQueue;

public class MatchingEngine {

    private final Graph graph;
    private final UserStorage storage;
    private static final String UNIVERSITY = "University";

    public MatchingEngine(Graph graph, UserStorage storage) {
        this.graph = graph;
        this.storage = storage;
    }

    public PriorityQueue<RideMatch> findMatches(Passenger passenger) {
        PriorityQueue<RideMatch> matches = new PriorityQueue<>();

        if (!passenger.getPickupLocation().equals(UNIVERSITY)
                && !passenger.getDropOffLocation().equals(UNIVERSITY)) {
            return matches; // Invalid request
        }

        for (Driver driver : storage.getDrivers().values()) {
            if (driver.getAvailableSeats() <= 0) continue;

            String source = passenger.getPickupLocation();
            String destination = passenger.getDropOffLocation();

            Map<String, Double> distances = Dijkstra.shortestPaths(graph, source);
            double distance = distances.getOrDefault(destination, Double.MAX_VALUE);

            long timeDiff = Math.abs(
                    Duration.between(driver.getDepartureTime(),
                            passenger.getPreferredTime()).toMinutes()
            );

            double score = (distance * 0.5) + (timeDiff * 0.3) + (driver.getPricePerSeat() * 0.2);

            matches.offer(new RideMatch(driver, passenger, distance, score));
        }
        return matches;
    }
}

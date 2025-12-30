package ui.console;

import datastructures.Graph;
import model.Driver;
import model.Passenger;
import model.RideMatch;
import service.MatchingEngine;
import storage.RideHistory;
import storage.UserStorage;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

public class ConsoleUI {

    private final Scanner scanner;
    private final UserStorage storage;
    private final RideHistory history;
    private final MatchingEngine matchingEngine;

    public ConsoleUI(Graph graph) {
        this.scanner = new Scanner(System.in);
        this.storage = new UserStorage();
        this.history = new RideHistory();
        this.matchingEngine = new MatchingEngine(graph, storage);
    }

    public void start() {
        System.out.println("===== Welcome to Smart City–University Ride Sharing =====");

        // Phase 1: Registration
        boolean registering = true;
        while (registering) {
            System.out.print("\nRegister as Driver (D) or Passenger (P) or Done (X): ");
            String choice = scanner.nextLine().toUpperCase();
            switch (choice) {
                case "D" -> registerDriver();
                case "P" -> registerPassenger();
                case "X" -> registering = false;
                default -> System.out.println("Invalid choice. Enter D, P, or X.");
            }
        }

        // Phase 2: Ride & History Menu
        boolean running = true;
        while (running) {
            printMenu();
            int option = Integer.parseInt(scanner.nextLine());
            switch (option) {
                case 1 -> requestRide();
                case 2 -> viewRideHistory();
                case 0 -> {
                    System.out.println("Exiting system. Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n===== Menu =====");
        System.out.println("1. Request Ride");
        System.out.println("2. View Ride History");
        System.out.println("0. Exit");
        System.out.print("Choose option: ");
    }

    private void registerDriver() {
        System.out.println("\n--- Driver Registration ---");
        System.out.print("Driver ID: ");
        String id = scanner.nextLine();

        System.out.print("Name: ");
        String name = scanner.nextLine();

        System.out.print("Start Location: ");
        String start = scanner.nextLine();

        System.out.print("End Location: ");
        String end = scanner.nextLine();

        System.out.print("Departure Time (HH:mm): ");
        LocalTime time = LocalTime.parse(scanner.nextLine());

        System.out.print("Available Seats: ");
        int seats = Integer.parseInt(scanner.nextLine());

        System.out.print("Price per Seat: ");
        double price = Double.parseDouble(scanner.nextLine());

        Driver driver = new Driver(id, name, start, end, time, seats, price);
        storage.addDriver(driver);

        System.out.println("Driver registered successfully.");
    }

    private void registerPassenger() {
        System.out.println("\n--- Passenger Registration ---");
        System.out.print("Passenger ID: ");
        String id = scanner.nextLine();

        System.out.print("Name: ");
        String name = scanner.nextLine();

        System.out.print("Pickup Location: ");
        String pickup = scanner.nextLine();

        System.out.print("Drop-off Location: ");
        String drop = scanner.nextLine();

        System.out.print("Preferred Time (HH:mm): ");
        LocalTime time = LocalTime.parse(scanner.nextLine());

        Passenger passenger = new Passenger(id, name, pickup, drop, time);
        storage.addPassenger(passenger);

        System.out.println("Passenger registered successfully.");
    }

    private void requestRide() {
        System.out.print("\nEnter Passenger ID: ");
        String passengerId = scanner.nextLine();
        Passenger passenger = storage.getPassengers().get(passengerId);

        if (passenger == null) {
            System.out.println("Passenger not found.");
            return;
        }

        PriorityQueue<RideMatch> matches = matchingEngine.findMatches(passenger);

        if (matches.isEmpty()) {
            System.out.println("No rides available for your request.");
            return;
        }

        List<RideMatch> allMatches = new ArrayList<>();
        while (!matches.isEmpty()) allMatches.add(matches.poll());

        System.out.println("\n--- Available Rides ---");
        for (int i = 0; i < allMatches.size(); i++) {
            RideMatch m = allMatches.get(i);
            System.out.println((i + 1) + ". Driver: " + m.getDriver().getName() +
                    " | Route: " + m.getDriver().getStartLocation() + " → " + m.getDriver().getEndLocation() +
                    " | Departure: " + m.getDriver().getDepartureTime() +
                    " | Price: " + m.getDriver().getPricePerSeat() +
                    " | Distance: " + m.getDistance() +
                    " | Score: " + m.getScore());
        }

        RideMatch bestMatch = allMatches.get(0);
        System.out.println("\n--- Best Match ---");
        System.out.println("Driver: " + bestMatch.getDriver().getName() +
                " | Route: " + bestMatch.getDriver().getStartLocation() + " → " + bestMatch.getDriver().getEndLocation() +
                " | Departure: " + bestMatch.getDriver().getDepartureTime() +
                " | Price: " + bestMatch.getDriver().getPricePerSeat());

        System.out.print("\nConfirm this ride? (Y/N): ");
        String confirm = scanner.nextLine().toUpperCase();
        RideMatch selectedMatch;

        if (confirm.equals("Y")) {
            selectedMatch = bestMatch;
        } else {
            System.out.print("Enter the number of the ride you want to choose: ");
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice < 1 || choice > allMatches.size()) {
                System.out.println("Invalid choice. Booking best match by default.");
                selectedMatch = bestMatch;
            } else {
                selectedMatch = allMatches.get(choice - 1);
            }
        }

        selectedMatch.getDriver().decrementSeat();
        history.addRide(selectedMatch);
        System.out.println("Ride booked successfully with Driver: " + selectedMatch.getDriver().getName());
    }

    private void viewRideHistory() {
        System.out.println("\n===== Ride History =====");
        if (history.getHistory().isEmpty()) {
            System.out.println("No rides booked yet.");
            return;
        }
        for (RideMatch match : history.getHistory()) {
            System.out.println(match.getPassenger().getName() + " → " +
                    match.getDriver().getName() +
                    " | Route: " + match.getDriver().getStartLocation() + " → " + match.getDriver().getEndLocation() +
                    " | Departure: " + match.getDriver().getDepartureTime() +
                    " | Price: " + match.getDriver().getPricePerSeat());
        }
    }
}

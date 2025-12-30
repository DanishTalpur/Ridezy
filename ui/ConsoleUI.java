package ui;

import datastructures.Graph;
import java.time.LocalTime;
import java.util.*;
import model.Driver;
import model.Passenger;
import model.RideMatch;
import service.MatchingEngine;
import storage.RideHistory;
import storage.UserStorage;

public class ConsoleUI {

    private final Scanner scanner;
    private final UserStorage storage;
    private final RideHistory history;
    private final MatchingEngine matchingEngine;

    private Passenger currentPassenger = null;
    private Driver currentDriver = null;

    public ConsoleUI(Graph graph) {
        this.scanner = new Scanner(System.in);
        this.storage = new UserStorage();
        this.history = new RideHistory();
        this.matchingEngine = new MatchingEngine(graph, storage);
    }

    public void start() {
        System.out.println("===== Welcome to Smart City–University Ride Sharing =====");

        boolean running = true;
        while (running) {
            if (currentPassenger == null && currentDriver == null) {
                loginOrRegisterMenu();
            } else if (currentPassenger != null) {
                passengerMenu();
            } else {
                driverMenu();
            }
        }
    }

    private void loginOrRegisterMenu() {
        System.out.println("\n1. Register / Login as Passenger");
        System.out.println("2. Register / Login as Driver");
        System.out.println("0. Exit");
        System.out.print("Choose option: ");
        String choice = scanner.nextLine();

        switch (choice) {
            case "1" -> registerOrLoginPassenger();
            case "2" -> registerOrLoginDriver();
            case "0" -> {
                System.out.println("Goodbye!");
                System.exit(0);
            }
            default -> System.out.println("Invalid choice.");
        }
    }

    private void registerOrLoginPassenger() {
        System.out.print("Enter Passenger ID: ");
        String id = scanner.nextLine();

        Passenger passenger = storage.getPassengers().get(id);
        if (passenger != null) {
            currentPassenger = passenger;
            System.out.println("Welcome back, " + passenger.getName());
            return;
        }

        // Registration
        System.out.print("Name: ");
        String name = scanner.nextLine();

        System.out.print("Pickup Location: ");
        String pickup = scanner.nextLine();

        System.out.print("Drop-off Location: ");
        String drop = scanner.nextLine();

        System.out.print("Preferred Time (HH:mm): ");
        LocalTime time = LocalTime.parse(scanner.nextLine());

        passenger = new Passenger(id, name, pickup, drop, time);
        storage.addPassenger(passenger);
        currentPassenger = passenger;
        System.out.println("Passenger registered and logged in as " + name);
    }

    private void registerOrLoginDriver() {
        System.out.print("Enter Driver ID: ");
        String id = scanner.nextLine();

        Driver driver = storage.getDrivers().get(id);
        if (driver != null) {
            currentDriver = driver;
            System.out.println("Welcome back, " + driver.getName());
            return;
        }

        // Registration
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

        driver = new Driver(id, name, start, end, time, seats, price);
        storage.addDriver(driver);
        currentDriver = driver;
        System.out.println("Driver registered and logged in as " + name);
    }

    // ---------------------- Passenger Menu ----------------------
    private void passengerMenu() {
        System.out.println("\n===== Passenger Menu (" + currentPassenger.getName() + ") =====");
        System.out.println("1. Request Ride");
        System.out.println("2. View Ride History");
        System.out.println("3. Cancel Active Ride");
        System.out.println("4. Logout");
        System.out.print("Choose option: ");
        String choice = scanner.nextLine();

        switch (choice) {
            case "1" -> requestRide();
            case "2" -> viewRideHistory();
            case "3" -> cancelRide();
            case "4" -> logoutPassenger();
            default -> System.out.println("Invalid option.");
        }
    }

    private void logoutPassenger() {
        System.out.println("Logging out " + currentPassenger.getName());
        currentPassenger = null;
    }

    private void cancelRide() {
        RideMatch activeRide = history.getActiveRideForPassenger(currentPassenger);
        if (activeRide == null) {
            System.out.println("No active ride to cancel.");
            return;
        }
        activeRide.getDriver().incrementSeat(currentPassenger.getId());
        history.removeRide(activeRide);
        System.out.println("Ride canceled successfully.");
    }

    private void requestRide() {
        if (history.hasActiveRide(currentPassenger)) {
            System.out.println("You already have an active ride. Cancel it first to book a new one.");
            return;
        }

        PriorityQueue<RideMatch> matches = matchingEngine.findMatches(currentPassenger);
        if (matches.isEmpty()) {
            System.out.println("No rides available.");
            return;
        }

        List<RideMatch> allMatches = new ArrayList<>();
        while (!matches.isEmpty()) {
            allMatches.add(matches.poll());
        }

        System.out.println("\n--- Available Rides ---");
        for (int i = 0; i < allMatches.size(); i++) {
            RideMatch m = allMatches.get(i);
            System.out.println((i + 1) + ". Driver: " + m.getDriver().getName()
                    + " | Route: " + m.getDriver().getStartLocation() + " → " + m.getDriver().getEndLocation()
                    + " | Departure: " + m.getDriver().getDepartureTime()
                    + " | Price: " + m.getDriver().getPricePerSeat()
                    + " | Distance: " + m.getDistance()
                    + " | Score: " + m.getScore()
                    + " | Available Seats: " + m.getDriver().getAvailableSeats());
        }

        RideMatch bestMatch = allMatches.get(0);
        System.out.println("\n--- Best Match ---");
        System.out.println("Driver: " + bestMatch.getDriver().getName()
                + " | Route: " + bestMatch.getDriver().getStartLocation() + " → " + bestMatch.getDriver().getEndLocation()
                + " | Departure: " + bestMatch.getDriver().getDepartureTime()
                + " | Price: " + bestMatch.getDriver().getPricePerSeat()
                + " | Available Seats: " + bestMatch.getDriver().getAvailableSeats());

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

        // Use Driver's new method to decrement seat and track passenger
        if (!selectedMatch.getDriver().hasAvailableSeats()) {
            System.out.println("Driver has no available seats.");
            return;
        }

        selectedMatch.getDriver().decrementSeat(currentPassenger.getId());
        history.addRide(selectedMatch);
        System.out.println("Ride booked successfully with Driver: " + selectedMatch.getDriver().getName());
    }

    private void viewRideHistory() {
        System.out.println("\n===== Ride History =====");
        List<RideMatch> rides = history.getRidesForPassenger(currentPassenger);
        if (rides.isEmpty()) {
            System.out.println("No rides booked yet.");
            return;
        }
        for (RideMatch match : rides) {
            System.out.println(match.getPassenger().getName() + " → "
                    + match.getDriver().getName()
                    + " | Route: " + match.getDriver().getStartLocation() + " → " + match.getDriver().getEndLocation()
                    + " | Departure: " + match.getDriver().getDepartureTime()
                    + " | Price: " + match.getDriver().getPricePerSeat());
        }
    }

    // ---------------------- Driver Menu ----------------------
    private void driverMenu() {
        System.out.println("\n===== Driver Menu (" + currentDriver.getName() + ") =====");
        System.out.println("1. View / Cancel Passengers");
        System.out.println("2. Logout");
        System.out.print("Choose option: ");
        String choice = scanner.nextLine();

        switch (choice) {
            case "1" -> managePassengers();
            case "2" -> logoutDriver();
            default -> System.out.println("Invalid option.");
        }
    }

    private void logoutDriver() {
        System.out.println("Logging out " + currentDriver.getName());
        currentDriver = null;
    }

    private void managePassengers() {
        List<RideMatch> matches = history.getActiveRidesForDriver(currentDriver);
        if (matches.isEmpty()) {
            System.out.println("No passengers booked yet.");
            return;
        }

        for (int i = 0; i < matches.size(); i++) {
            RideMatch m = matches.get(i);
            System.out.println((i + 1) + ". Passenger: " + m.getPassenger().getName()
                    + " | Pickup: " + m.getPassenger().getPickupLocation()
                    + " | Drop: " + m.getPassenger().getDropOffLocation());
        }

        System.out.print("Enter passenger number to cancel or 0 to return: ");
        int choice = Integer.parseInt(scanner.nextLine());
        if (choice == 0) return;

        if (choice < 1 || choice > matches.size()) {
            System.out.println("Invalid choice.");
            return;
        }

        RideMatch toCancel = matches.get(choice - 1);
        toCancel.getDriver().incrementSeat(toCancel.getPassenger().getId());
        history.removeRide(toCancel);
        System.out.println("Passenger " + toCancel.getPassenger().getName() + " removed from your ride.");
    }
}

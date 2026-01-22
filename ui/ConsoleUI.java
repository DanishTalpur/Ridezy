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
        this.history = new RideHistory(storage);
        this.matchingEngine = new MatchingEngine(graph, storage, history);
    }

    public void start() {
        System.out.println("=======================================");
        System.out.println("||      \uD83C\uDF08 Welcome to Ridezy \uD83C\uDF08      ||");
        System.out.println("||       Let's ride together \uD83D\uDE0E      ||");
        System.out.println("=======================================");
        System.out.println();

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
                System.out.println("Goodbye! Enjoy the Ride \uD83E\uDD7A \uD83D\uDC49\uD83D\uDC48");
                System.exit(0);
            }
            default -> System.out.println("Invalid choice.\uD83D\uDE28");
        }
    }

    private void registerOrLoginPassenger() {
        System.out.print("Enter Passenger ID: ");
        String id = scanner.nextLine();

        Passenger passenger = storage.getPassengers().get(id);
        if (passenger != null) {
            currentPassenger = passenger;
            System.out.println("Welcome back, " + passenger.getName() +" \uD83D\uDE0E");
            return;
        }

        System.out.print("Name: ");
        String name = scanner.nextLine();

        passenger = new Passenger(id, name);
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



        driver = new Driver(id, name);
        storage.addDriver(driver);
        currentDriver = driver;
        System.out.println("Driver registered and logged in as " + name);
    }

    // ---------------------- Passenger Menu ----------------------
    private void passengerMenu() {
        System.out.println("\n===== Passenger Menu (" + currentPassenger.getName() + ") \uD83E\uDD20 =====");
        System.out.println("1. Request Ride");
        System.out.println("2. View Ride History");
        System.out.println("3. Cancel Active Ride");
        System.out.println("4. Mark Ride as Done");
        System.out.println("5. Logout");
        System.out.print("Choose option: ");
        String choice = scanner.nextLine();

        switch (choice) {
            case "1" -> requestRide();
            case "2" -> viewRideHistory();
            case "3" -> cancelRide();
            case "4" -> markRideDone();
            case "5" -> logoutPassenger();
            default -> System.out.println("Invalid option.\uD83D\uDE28");
        }
    }

    private void markRideDone() {
        if (!history.hasActiveRide(currentPassenger)) {
            System.out.println("No active ride.");
            return;
        }

        // Mark as completed in history
        history.markRideCompleted(currentPassenger);

        System.out.println("Ride marked as completed");
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
        storage.saveDrivers();
        history.removeRide(activeRide);
        System.out.println("Ride canceled successfully.");
    }

    private void requestRide() {
        if (history.hasActiveRide(currentPassenger)) {
            System.out.println("You already have an active ride. Cancel it first to book a new one.");
            return;
        }

        System.out.print("Pickup Location: ");
        String pickup = scanner.nextLine();
        currentPassenger.setPickupLocation(pickup);

        System.out.print("Drop-off Location: ");
        String drop = scanner.nextLine();
        currentPassenger.setDropOffLocation(drop);

        System.out.print("Preferred Time (HH:mm): ");
        LocalTime time = LocalTime.parse(scanner.nextLine());
        currentPassenger.setPreferredTime(time);

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
                    + " | Distance: " + String.format("%.2f", m.getDistance()) // 2 decimal places
                    + " | Score: " + String.format("%.2f", m.getScore()) // 2 decimal places
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
                System.out.println("None chosen.");
                return;
            } else {
                selectedMatch = allMatches.get(choice - 1);
            }
        }

        if (!selectedMatch.getDriver().hasAvailableSeats()) {
            System.out.println("Driver has no available seats.");
            return;
        }

        selectedMatch.getDriver().decrementSeat(currentPassenger.getId());
        storage.saveDrivers();
        history.addRide(selectedMatch, currentPassenger.getPickupLocation(), currentPassenger.getDropOffLocation());
        System.out.println("Ride booked successfully with Driver: " + selectedMatch.getDriver().getName());
    }

    private void viewRideHistory() {
        System.out.println("\n===== My Ride History =====");
        List<RideMatch> rides = history.getRidesForPassenger(currentPassenger);
        if (rides.isEmpty()) {
            System.out.println("No rides booked yet.");
            return;
        }
        
        int activeCount = 0;
        int completedCount = 0;
        
        System.out.println("┌─────────────────────────────────────────────────────────────────────────────┐");
        System.out.println("│ Passenger: " + currentPassenger.getName() + " │");
        System.out.println("├─────────────────────────────────────────────────────────────────────────────┤");
        
        for (RideMatch match : rides) {
            String status = history.getRideStatus(match) == model.RideStatus.ACTIVE ? "🟢 ACTIVE" : "✅ COMPLETED";
            if (history.getRideStatus(match) == model.RideStatus.ACTIVE) {
                activeCount++;
            } else {
                completedCount++;
            }
            // Get pickup and dropoff locations from stored ride data
            String pickup = history.getPickupLocation(match);
            String dropoff = history.getDropOffLocation(match);
            // Fallback to passenger object if stored data is empty (for backward compatibility)
            if (pickup == null || pickup.trim().isEmpty()) {
                String passengerPickup = match.getPassenger().getPickupLocation();
                pickup = (passengerPickup != null && !passengerPickup.trim().isEmpty()) ? passengerPickup : "N/A";
            }
            if (dropoff == null || dropoff.trim().isEmpty()) {
                String passengerDropoff = match.getPassenger().getDropOffLocation();
                dropoff = (passengerDropoff != null && !passengerDropoff.trim().isEmpty()) ? passengerDropoff : "N/A";
            }
            String route = pickup + " → " + dropoff;
            System.out.println("│ Driver: " + padRight(match.getDriver().getName(), 15)
                    + " | Route: " + padRight(route, 20)
                    + " | Departure: " + padRight(match.getDriver().getDepartureTime() != null ? match.getDriver().getDepartureTime().toString() : "N/A", 8)
                    + " | Price: Rs. " + match.getDriver().getPricePerSeat()
                    + " | " + status + " │");
        }
        
        System.out.println("├─────────────────────────────────────────────────────────────────────────────┤");
        System.out.println("│ Summary: " + activeCount + " Active | " + completedCount + " Completed | Total: " + rides.size() + " │");
        System.out.println("└─────────────────────────────────────────────────────────────────────────────┘");
    }

    private void viewRideHistoryDriver() {
        System.out.println("\n===== My Ride History =====");
        List<RideMatch> rides = history.getRidesForDriver(currentDriver);
        if (rides.isEmpty()) {
            System.out.println("No rides created yet.");
            return;
        }
        
        int activeCount = 0;
        int completedCount = 0;
        
        System.out.println("┌─────────────────────────────────────────────────────────────────────────────┐");
        System.out.println("│ Route: " + (currentDriver.getStartLocation() != null ? currentDriver.getStartLocation() : "N/A") 
                         + " → " + (currentDriver.getEndLocation() != null ? currentDriver.getEndLocation() : "N/A") + " │");
        System.out.println("├─────────────────────────────────────────────────────────────────────────────┤");
        
        for (RideMatch match : rides) {
            String status = history.getRideStatus(match) == model.RideStatus.ACTIVE ? "🟢 ACTIVE" : "✅ COMPLETED";
            if (history.getRideStatus(match) == model.RideStatus.ACTIVE) {
                activeCount++;
            } else {
                completedCount++;
            }
            System.out.println("│ Passenger: " + padRight(match.getPassenger().getName(), 15)
                    + " | Pickup: " + padRight(match.getPassenger().getPickupLocation() != null ? match.getPassenger().getPickupLocation() : "N/A", 12)
                    + " | Drop: " + padRight(match.getPassenger().getDropOffLocation() != null ? match.getPassenger().getDropOffLocation() : "N/A", 12)
                    + " | " + status + " │");
        }
        
        System.out.println("├─────────────────────────────────────────────────────────────────────────────┤");
        System.out.println("│ Summary: " + activeCount + " Active | " + completedCount + " Completed | Total: " + rides.size() + " │");
        System.out.println("└─────────────────────────────────────────────────────────────────────────────┘");
    }

    private String padRight(String s, int n) {
        if (s == null) s = "";
        return String.format("%-" + n + "s", s.length() > n ? s.substring(0, n-3) + "..." : s);
    }

    // ---------------------- Driver Menu ----------------------
    private void driverMenu() {
        System.out.println("\n===== Driver Menu (" + currentDriver.getName() + ")\uD83E\uDD20 =====");
        System.out.println("1. Create Ride");
        System.out.println("2. View / Cancel Passengers");
        System.out.println("3. Mark Ride as Done");
        System.out.println("4. View Ride History");
        System.out.println("5. Logout");
        System.out.print("Choose option: ");
        String choice = scanner.nextLine();

        switch (choice) {
            case "1" -> driverride();
            case "2" -> managePassengers();
            case "3" -> markRideDoneDriver();
            case "4" -> viewRideHistoryDriver();
            case "5" -> logoutDriver();
            default -> System.out.println("Invalid option.");
        }
    }

    private void driverride() {
        if (history.hasActiveRide(currentDriver)) {
            System.out.println("You already have an active ride. Cancel it first to make new one.");
            return;
        }
        System.out.print("Start Location: ");
        String start = scanner.nextLine();
        currentDriver.setStartLocation(start);

        System.out.print("End Location: ");
        String end = scanner.nextLine();
        currentDriver.setEndLocation(end);

        System.out.print("Departure Time (HH:mm): ");
        LocalTime time = LocalTime.parse(scanner.nextLine());
        currentDriver.setDepartureTime(time);

        System.out.print("Available Seats: ");
        int seats = Integer.parseInt(scanner.nextLine());
        currentDriver.setAvailableSeats(seats);

        System.out.print("Price per Seat: ");
        double price = Double.parseDouble(scanner.nextLine());
        currentDriver.setPricePerSeat(price);

        storage.saveDrivers();
        System.out.println("Ride details saved successfully.");
    }

    private void markRideDoneDriver() {
        if (!history.hasActiveRide(currentDriver)) {
            System.out.println("No active ride to mark as done.");
            return;
        }
        history.markRideCompletedDriver(currentDriver);
        System.out.println("Ride marked as completed ✅");
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
            System.out.println("Invalid choice. \uD83D\uDE28");
            return;
        }

        RideMatch toCancel = matches.get(choice - 1);
        toCancel.getDriver().incrementSeat(toCancel.getPassenger().getId());
        storage.saveDrivers();
        history.removeRide(toCancel);
        System.out.println("Passenger " + toCancel.getPassenger().getName() + " removed from your ride. ");
    }
}

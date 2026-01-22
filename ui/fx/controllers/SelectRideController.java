package ui.fx.controllers;

import datastructures.Graph;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.Driver;
import model.Passenger;
import model.RideMatch;
import storage.RideHistory;
import storage.UserStorage;

import java.util.List;

public class SelectRideController {

    public static void showRideSelection(Stage stage, Graph cityGraph, Passenger passenger, List<RideMatch> matches) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: white;");

        Label titleLabel = new Label("Available Rides");
        titleLabel.setFont(new Font("Arial", 20));
        titleLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");

        Label infoLabel = new Label("Found " + matches.size() + " matching ride(s)");
        infoLabel.setFont(new Font("Arial", 12));
        infoLabel.setStyle("-fx-text-fill: #666;");

        // Best match highlight
        if (!matches.isEmpty()) {
            Label bestMatchLabel = new Label("⭐ Best Match - Lowest Score");
            bestMatchLabel.setFont(new Font("Arial", 11));
            bestMatchLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
            root.getChildren().add(bestMatchLabel);
        }

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPrefWidth(335);
        scrollPane.setPrefHeight(420);
        scrollPane.setStyle("-fx-background: white; -fx-border-color: transparent;");

        VBox ridesContainer = new VBox(10);
        ridesContainer.setPadding(new Insets(10));

        for (int i = 0; i < matches.size(); i++) {
            RideMatch match = matches.get(i);
            boolean isBestMatch = (i == 0);
            VBox rideCard = createRideCard(match, i + 1, isBestMatch);
            int finalI = i;

            Button selectBtn = new Button(isBestMatch ? "✓ Select Best Match" : "Select This Ride");
            selectBtn.setPrefWidth(295);
            selectBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 13; -fx-font-weight: bold; -fx-background-radius: 15;");
            selectBtn.setOnAction(e -> {
                RideMatch selected = matches.get(finalI);
                confirmRide(stage, cityGraph, passenger, selected);
            });

            rideCard.getChildren().add(selectBtn);
            ridesContainer.getChildren().add(rideCard);
        }

        scrollPane.setContent(ridesContainer);

        Button backBtn = new Button("Back");
        backBtn.setPrefWidth(335);
        backBtn.setPrefHeight(40);
        backBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 20;");
        backBtn.setOnAction(e -> RequestRideController.showRequestRide(stage, cityGraph, passenger));

        root.getChildren().addAll(titleLabel, infoLabel, scrollPane, backBtn);

        Scene scene = new Scene(root, 375, 667);
        stage.setScene(scene);
    }

    private static VBox createRideCard(RideMatch match, int index, boolean isBestMatch) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(15));

        String borderColor = isBestMatch ? "#f39c12" : "#27ae60";
        String bgColor = isBestMatch ? "#fff9e6" : "white";

        card.setStyle("-fx-background-color: " + bgColor + "; -fx-border-color: " + borderColor +
                "; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label numberLabel = new Label((isBestMatch ? "⭐ " : "") + "Ride #" + index);
        numberLabel.setFont(new Font("Arial", 14));
        numberLabel.setStyle("-fx-text-fill: " + borderColor + "; -fx-font-weight: bold;");

        Label driverLabel = new Label("Driver: " + match.getDriver().getName());
        driverLabel.setFont(new Font("Arial", 13));
        driverLabel.setStyle("-fx-text-fill: #333;");

        String start = match.getDriver().getStartLocation() != null ? match.getDriver().getStartLocation() : "N/A";
        String end = match.getDriver().getEndLocation() != null ? match.getDriver().getEndLocation() : "N/A";
        Label routeLabel = new Label("Route: " + start + " → " + end);
        routeLabel.setFont(new Font("Arial", 12));
        routeLabel.setStyle("-fx-text-fill: #555;");
        routeLabel.setWrapText(true);

        Label timeLabel = new Label("Departure: " +
                (match.getDriver().getDepartureTime() != null ? match.getDriver().getDepartureTime().toString() : "N/A"));
        timeLabel.setFont(new Font("Arial", 12));
        timeLabel.setStyle("-fx-text-fill: #555;");

        Label priceLabel = new Label("Price: Rs. " + String.format("%.0f", match.getDriver().getPricePerSeat()));
        priceLabel.setFont(new Font("Arial", 12));
        priceLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");

        Label distanceLabel = new Label("Distance: " + String.format("%.2f km", match.getDistance()));
        distanceLabel.setFont(new Font("Arial", 11));
        distanceLabel.setStyle("-fx-text-fill: #777;");

        Label scoreLabel = new Label("Score: " + String.format("%.2f", match.getScore()) +
                (isBestMatch ? " (Best!)" : ""));
        scoreLabel.setFont(new Font("Arial", 11));
        scoreLabel.setStyle("-fx-text-fill: #777; -fx-font-style: italic;");

        Label seatsLabel = new Label("Available Seats: " + match.getDriver().getAvailableSeats());
        seatsLabel.setFont(new Font("Arial", 11));
        seatsLabel.setStyle("-fx-text-fill: #777;");

        card.getChildren().addAll(numberLabel, driverLabel, routeLabel, timeLabel, priceLabel,
                distanceLabel, scoreLabel, seatsLabel);

        return card;
    }

    private static void confirmRide(Stage stage, Graph cityGraph, Passenger passenger, RideMatch selectedMatch) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Ride");
        confirmAlert.setHeaderText("Confirm this ride?");
        confirmAlert.setContentText("Driver: " + selectedMatch.getDriver().getName() +
                "\nRoute: " + selectedMatch.getDriver().getStartLocation() + " → " + selectedMatch.getDriver().getEndLocation() +
                "\nPrice: Rs. " + selectedMatch.getDriver().getPricePerSeat());

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                UserStorage storage = new UserStorage();
                // Get passenger from storage to ensure we have the same instance
                Passenger storedPassenger = storage.getPassengers().get(passenger.getId());
                if (storedPassenger == null) {
                    storedPassenger = passenger;
                }
                // Update stored passenger with pickup/dropoff locations
                storedPassenger.setPickupLocation(passenger.getPickupLocation());
                storedPassenger.setDropOffLocation(passenger.getDropOffLocation());
                storedPassenger.setPreferredTime(passenger.getPreferredTime());
                
                RideHistory history = new RideHistory(storage);

                if (history.hasActiveRide(storedPassenger)) {
                    showBookingResult(stage, cityGraph, storedPassenger, false, "You already have an active ride!");
                    return;
                }

                // Get driver from storage
                Driver storedDriver = storage.getDrivers().get(selectedMatch.getDriver().getId());
                if (storedDriver == null) {
                    storedDriver = selectedMatch.getDriver();
                }

                if (!storedDriver.hasAvailableSeats()) {
                    showBookingResult(stage, cityGraph, storedPassenger, false, "Driver has no available seats.");
                    return;
                }

                storedDriver.decrementSeat(storedPassenger.getId());
                storage.saveDrivers();
                // Save passenger data to ensure persistence
                storage.savePassengers();
                
                // Recreate rideMatch with stored driver and passenger
                RideMatch storedRideMatch = new RideMatch(storedDriver, storedPassenger, selectedMatch.getDistance(), selectedMatch.getScore());
                history.addRide(storedRideMatch, storedPassenger.getPickupLocation(), storedPassenger.getDropOffLocation());

                // Show success screen then go to current ride
                showBookingSuccess(stage, cityGraph, storedPassenger, storedRideMatch);
            }
        });
    }

    private static void showBookingSuccess(Stage stage, Graph cityGraph, Passenger passenger, RideMatch match) {
        VBox root = new VBox(30);
        root.setPadding(new Insets(50, 20, 50, 20));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: white;");

        Label iconLabel = new Label("✅");
        iconLabel.setFont(new Font("Arial", 80));

        Label titleLabel = new Label("Ride Booked!");
        titleLabel.setFont(new Font("Arial", 26));
        titleLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");

        Label messageLabel = new Label("Your ride with " + match.getDriver().getName() + " has been confirmed.");
        messageLabel.setFont(new Font("Arial", 14));
        messageLabel.setStyle("-fx-text-fill: #666;");
        messageLabel.setWrapText(true);
        messageLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        messageLabel.setMaxWidth(280);

        Button viewRideBtn = new Button("View Current Ride");
        viewRideBtn.setPrefWidth(280);
        viewRideBtn.setPrefHeight(50);
        viewRideBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold; -fx-background-radius: 25;");
        viewRideBtn.setOnAction(e -> CurrentRideController.showCurrentRide(stage, cityGraph, passenger, match));

        root.getChildren().addAll(iconLabel, titleLabel, messageLabel, viewRideBtn);

        Scene scene = new Scene(root, 375, 667);
        stage.setScene(scene);
    }

    private static void showBookingResult(Stage stage, Graph cityGraph, Passenger passenger, boolean success, String message) {
        VBox root = new VBox(30);
        root.setPadding(new Insets(50, 20, 50, 20));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: white;");

        Label iconLabel = new Label(success ? "✅" : "❌");
        iconLabel.setFont(new Font("Arial", 80));

        Label titleLabel = new Label(success ? "Success!" : "Error");
        titleLabel.setFont(new Font("Arial", 26));
        titleLabel.setStyle("-fx-text-fill: " + (success ? "#27ae60" : "#e74c3c") + "; -fx-font-weight: bold;");

        Label messageLabel = new Label(message);
        messageLabel.setFont(new Font("Arial", 14));
        messageLabel.setStyle("-fx-text-fill: #666;");
        messageLabel.setWrapText(true);
        messageLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        messageLabel.setMaxWidth(280);

        Button backBtn = new Button("Back to Dashboard");
        backBtn.setPrefWidth(280);
        backBtn.setPrefHeight(50);
        backBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold; -fx-background-radius: 25;");
        backBtn.setOnAction(e -> PassengerDashboardController.showDashboard(stage, cityGraph, passenger));

        root.getChildren().addAll(iconLabel, titleLabel, messageLabel, backBtn);

        Scene scene = new Scene(root, 375, 667);
        stage.setScene(scene);
    }
}
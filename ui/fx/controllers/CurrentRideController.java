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

public class CurrentRideController {

    public static void showCurrentRide(Stage stage, Graph cityGraph, Passenger passenger, RideMatch rideMatch) {
        // Reload passenger and driver from storage to ensure we have latest data
        UserStorage storage = new UserStorage();
        final Passenger storedPassenger = storage.getPassengers().get(passenger.getId()) != null 
                ? storage.getPassengers().get(passenger.getId()) 
                : passenger;
        final Driver storedDriver = storage.getDrivers().get(rideMatch.getDriver().getId()) != null
                ? storage.getDrivers().get(rideMatch.getDriver().getId())
                : rideMatch.getDriver();
        // Recreate rideMatch with stored driver and passenger
        final RideMatch storedRideMatch = new RideMatch(storedDriver, storedPassenger, rideMatch.getDistance(), rideMatch.getScore());
        
        VBox root = new VBox(25);
        root.setPadding(new Insets(30, 20, 30, 20));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: white;");

        Label titleLabel = new Label("Current Ride");
        titleLabel.setFont(new Font("Arial", 24));
        titleLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");

        VBox detailsContainer = new VBox(15);
        detailsContainer.setAlignment(Pos.CENTER_LEFT);
        detailsContainer.setPadding(new Insets(20));
        detailsContainer.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 15; -fx-border-color: #27ae60; -fx-border-width: 2; -fx-border-radius: 15;");

        Label driverLabel = new Label("Driver: " + storedRideMatch.getDriver().getName());
        driverLabel.setFont(new Font("Arial", 16));
        driverLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");

        RideHistory history = new RideHistory(storage);
        String pickupTemp = history.getPickupLocation(storedRideMatch);
        String dropoffTemp = history.getDropOffLocation(storedRideMatch);
        final String pickup = (pickupTemp == null || pickupTemp.isEmpty()) ? "N/A" : pickupTemp;
        final String dropoff = (dropoffTemp == null || dropoffTemp.isEmpty()) ? "N/A" : dropoffTemp;

        Label routeLabel = new Label("Route: " + pickup + " → " + dropoff);
        routeLabel.setFont(new Font("Arial", 14));
        routeLabel.setStyle("-fx-text-fill: #333;");
        routeLabel.setWrapText(true);

        Label timeLabel = new Label("Departure: " +
                (storedRideMatch.getDriver().getDepartureTime() != null ? storedRideMatch.getDriver().getDepartureTime().toString() : "N/A"));
        timeLabel.setFont(new Font("Arial", 14));
        timeLabel.setStyle("-fx-text-fill: #333;");

        Label priceLabel = new Label("Price: Rs. " + storedRideMatch.getDriver().getPricePerSeat());
        priceLabel.setFont(new Font("Arial", 14));
        priceLabel.setStyle("-fx-text-fill: #333;");

        detailsContainer.getChildren().addAll(driverLabel, routeLabel, timeLabel, priceLabel);

        Button completeBtn = new Button("Mark as Complete");
        completeBtn.setPrefWidth(280);
        completeBtn.setPrefHeight(50);
        completeBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold; -fx-background-radius: 25;");
        completeBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Complete Ride");
            confirm.setHeaderText("Mark this ride as completed?");
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    history.markRideCompleted(storedPassenger);
                    showAlert("Success", "Ride marked as completed!");
                    PassengerDashboardController.showDashboard(stage, cityGraph, storedPassenger);
                }
            });
        });

        Button cancelBtn = new Button("Cancel Ride");
        cancelBtn.setPrefWidth(280);
        cancelBtn.setPrefHeight(50);
        cancelBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold; -fx-background-radius: 25;");
        cancelBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Cancel Ride");
            confirm.setHeaderText("Are you sure you want to cancel this ride?");
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    storedRideMatch.getDriver().incrementSeat(storedPassenger.getId());
                    storage.saveDrivers();
                    history.removeRide(storedRideMatch);
                    showAlert("Success", "Ride cancelled successfully!");
                    PassengerDashboardController.showDashboard(stage, cityGraph, storedPassenger);
                }
            });
        });

        Button backBtn = new Button("Back to Dashboard");
        backBtn.setPrefWidth(280);
        backBtn.setPrefHeight(40);
        backBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 20;");
        backBtn.setOnAction(e -> PassengerDashboardController.showDashboard(stage, cityGraph, storedPassenger));

        root.getChildren().addAll(titleLabel, detailsContainer, completeBtn, cancelBtn, backBtn);

        Scene scene = new Scene(root, 375, 667);
        stage.setScene(scene);
    }

    public static void showCurrentRideDriver(Stage stage, Graph cityGraph, Driver driver, List<RideMatch> activeRides) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30, 20, 30, 20));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: white;");

        Label titleLabel = new Label("Current Ride Details");
        titleLabel.setFont(new Font("Arial", 24));
        titleLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");

        // Driver's route info
        VBox routeContainer = new VBox(10);
        routeContainer.setAlignment(Pos.CENTER_LEFT);
        routeContainer.setPadding(new Insets(15));
        routeContainer.setStyle("-fx-background-color: #e8f5e9; -fx-background-radius: 10; -fx-border-color: #27ae60; -fx-border-width: 2; -fx-border-radius: 10;");

        Label routeLabel = new Label("Your Route: " +
                (driver.getStartLocation() != null ? driver.getStartLocation() : "N/A") + " → " +
                (driver.getEndLocation() != null ? driver.getEndLocation() : "N/A"));
        routeLabel.setFont(new Font("Arial", 14));
        routeLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        routeLabel.setWrapText(true);

        Label timeLabel = new Label("Departure: " +
                (driver.getDepartureTime() != null ? driver.getDepartureTime().toString() : "N/A"));
        timeLabel.setFont(new Font("Arial", 13));

        Label seatsLabel = new Label("Available Seats: " + driver.getAvailableSeats());
        seatsLabel.setFont(new Font("Arial", 13));

        routeContainer.getChildren().addAll(routeLabel, timeLabel, seatsLabel);

        // Passengers List
        Label passengersTitle = new Label("Passengers (" + activeRides.size() + ")");
        passengersTitle.setFont(new Font("Arial", 16));
        passengersTitle.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPrefWidth(335);
        scrollPane.setPrefHeight(280);
        scrollPane.setStyle("-fx-background: white; -fx-border-color: transparent;");

        VBox passengersList = new VBox(10);
        passengersList.setPadding(new Insets(5));

        UserStorage storage = new UserStorage();
        RideHistory history = new RideHistory(storage);

        for (RideMatch match : activeRides) {
            VBox passengerCard = new VBox(8);
            passengerCard.setPadding(new Insets(12));
            passengerCard.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 10; -fx-border-color: #27ae60; -fx-border-width: 1; -fx-border-radius: 10;");

            Label nameLabel = new Label("Passenger: " + match.getPassenger().getName());
            nameLabel.setFont(new Font("Arial", 14));
            nameLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");

            String pickupTemp = history.getPickupLocation(match);
            String dropoffTemp = history.getDropOffLocation(match);
            final String pickup = (pickupTemp == null || pickupTemp.isEmpty()) ? "N/A" : pickupTemp;
            final String dropoff = (dropoffTemp == null || dropoffTemp.isEmpty()) ? "N/A" : dropoffTemp;

            Label passengerRouteLabel = new Label("Pickup: " + pickup + "\nDrop-off: " + dropoff);
            passengerRouteLabel.setFont(new Font("Arial", 12));
            passengerRouteLabel.setWrapText(true);

            Button cancelPassengerBtn = new Button("Cancel Passenger");
            cancelPassengerBtn.setPrefWidth(290);
            cancelPassengerBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 12; -fx-background-radius: 15;");
            cancelPassengerBtn.setOnAction(e -> {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Cancel Passenger");
                confirm.setHeaderText("Remove " + match.getPassenger().getName() + " from this ride?");
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        match.getDriver().incrementSeat(match.getPassenger().getId());
                        storage.saveDrivers();
                        history.removeRide(match);
                        showAlert("Success", "Passenger removed successfully!");

                        // Refresh the screen
                        List<RideMatch> updatedRides = history.getActiveRidesForDriver(driver);
                        if (updatedRides.isEmpty()) {
                            DriverDashboardController.showDashboard(stage, cityGraph, driver);
                        } else {
                            showCurrentRideDriver(stage, cityGraph, driver, updatedRides);
                        }
                    }
                });
            });

            passengerCard.getChildren().addAll(nameLabel, passengerRouteLabel, cancelPassengerBtn);
            passengersList.getChildren().add(passengerCard);
        }

        scrollPane.setContent(passengersList);

        Button completeBtn = new Button("Mark Ride as Complete");
        completeBtn.setPrefWidth(280);
        completeBtn.setPrefHeight(50);
        completeBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold; -fx-background-radius: 25;");
        completeBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Complete Ride");
            confirm.setHeaderText("Mark this ride as completed for all passengers?");
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    history.markRideCompletedDriver(driver);
                    showAlert("Success", "Ride marked as completed!");
                    DriverDashboardController.showDashboard(stage, cityGraph, driver);
                }
            });
        });

        Button backBtn = new Button("Back to Dashboard");
        backBtn.setPrefWidth(280);
        backBtn.setPrefHeight(40);
        backBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 20;");
        backBtn.setOnAction(e -> DriverDashboardController.showDashboard(stage, cityGraph, driver));

        root.getChildren().addAll(titleLabel, routeContainer, passengersTitle, scrollPane, completeBtn, backBtn);

        Scene scene = new Scene(root, 375, 667);
        stage.setScene(scene);
    }

    private static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
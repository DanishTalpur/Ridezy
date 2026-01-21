package ui.fx.controllers;

import datastructures.Graph;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.Driver;
import model.Passenger;
import model.RideMatch;
import storage.RideHistory;
import storage.UserStorage;

public class CurrentRideController {
    
    public static void showCurrentRide(Stage stage, Graph cityGraph, Passenger passenger, RideMatch rideMatch) {
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
        detailsContainer.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 15;");
        
        Label driverLabel = new Label("Driver: " + rideMatch.getDriver().getName());
        driverLabel.setFont(new Font("Arial", 16));
        driverLabel.setStyle("-fx-text-fill: #27ae60;");
        
        Label routeLabel = new Label("Route: " + passenger.getPickupLocation() + " → " + passenger.getDropOffLocation());
        routeLabel.setFont(new Font("Arial", 14));
        routeLabel.setStyle("-fx-text-fill: #333;");
        
        Label timeLabel = new Label("Departure Time: " + 
                (rideMatch.getDriver().getDepartureTime() != null ? rideMatch.getDriver().getDepartureTime().toString() : "N/A"));
        timeLabel.setFont(new Font("Arial", 14));
        timeLabel.setStyle("-fx-text-fill: #333;");
        
        Label priceLabel = new Label("Price: Rs." + rideMatch.getDriver().getPricePerSeat());
        priceLabel.setFont(new Font("Arial", 14));
        priceLabel.setStyle("-fx-text-fill: #333;");
        
        detailsContainer.getChildren().addAll(driverLabel, routeLabel, timeLabel, priceLabel);
        
        Button cancelBtn = new Button("Cancel Ride");
        cancelBtn.setPrefWidth(280);
        cancelBtn.setPrefHeight(50);
        cancelBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold; -fx-background-radius: 25;");
        cancelBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Cancel Ride");
            confirm.setHeaderText("Are you sure you want to cancel this ride?");
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    UserStorage storage = new UserStorage();
                    RideHistory history = new RideHistory(storage);
                    rideMatch.getDriver().incrementSeat(passenger.getId());
                    storage.saveDrivers();
                    history.removeRide(rideMatch);
                    showAlert("Success", "Ride cancelled successfully!");
                    PassengerDashboardController.showDashboard(stage, cityGraph, passenger);
                }
            });
        });
        
        Button completeBtn = new Button("Mark as Complete");
        completeBtn.setPrefWidth(280);
        completeBtn.setPrefHeight(50);
        completeBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold; -fx-background-radius: 25;");
        completeBtn.setOnAction(e -> {
            UserStorage storage = new UserStorage();
            RideHistory history = new RideHistory(storage);
            history.markRideCompleted(passenger);
            showAlert("Success", "Ride marked as completed!");
            PassengerDashboardController.showDashboard(stage, cityGraph, passenger);
        });
        
        Button backBtn = new Button("Back");
        backBtn.setPrefWidth(280);
        backBtn.setPrefHeight(40);
        backBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 20;");
        backBtn.setOnAction(e -> PassengerDashboardController.showDashboard(stage, cityGraph, passenger));
        
        root.getChildren().addAll(titleLabel, detailsContainer, cancelBtn, completeBtn, backBtn);
        
        Scene scene = new Scene(root, 375, 667);
        stage.setScene(scene);
    }
    
    public static void showCurrentRideDriver(Stage stage, Graph cityGraph, Driver driver, RideMatch rideMatch) {
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
        detailsContainer.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 15;");
        
        Label passengerLabel = new Label("Passenger: " + rideMatch.getPassenger().getName());
        passengerLabel.setFont(new Font("Arial", 16));
        passengerLabel.setStyle("-fx-text-fill: #27ae60;");
        
        Label routeLabel = new Label("Route: " + rideMatch.getPassenger().getPickupLocation() + " → " + rideMatch.getPassenger().getDropOffLocation());
        routeLabel.setFont(new Font("Arial", 14));
        routeLabel.setStyle("-fx-text-fill: #333;");
        
        Label timeLabel = new Label("Departure Time: " + 
                (driver.getDepartureTime() != null ? driver.getDepartureTime().toString() : "N/A"));
        timeLabel.setFont(new Font("Arial", 14));
        timeLabel.setStyle("-fx-text-fill: #333;");
        
        detailsContainer.getChildren().addAll(passengerLabel, routeLabel, timeLabel);
        
        Button completeBtn = new Button("Mark as Complete");
        completeBtn.setPrefWidth(280);
        completeBtn.setPrefHeight(50);
        completeBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold; -fx-background-radius: 25;");
        completeBtn.setOnAction(e -> {
            UserStorage storage = new UserStorage();
            RideHistory history = new RideHistory(storage);
            history.markRideCompletedDriver(driver);
            showAlert("Success", "Ride marked as completed!");
            DriverDashboardController.showDashboard(stage, cityGraph, driver);
        });
        
        Button backBtn = new Button("Back");
        backBtn.setPrefWidth(280);
        backBtn.setPrefHeight(40);
        backBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 20;");
        backBtn.setOnAction(e -> DriverDashboardController.showDashboard(stage, cityGraph, driver));
        
        root.getChildren().addAll(titleLabel, detailsContainer, completeBtn, backBtn);
        
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


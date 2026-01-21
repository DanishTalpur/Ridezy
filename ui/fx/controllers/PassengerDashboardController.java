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
import model.Passenger;
import model.RideMatch;
import storage.RideHistory;
import storage.UserStorage;

public class PassengerDashboardController {
    
    public static void showDashboard(Stage stage, Graph cityGraph, Passenger passenger) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30, 20, 30, 20));
        root.setStyle("-fx-background-color: white;");
        
        // Welcome Label
        Label welcomeLabel = new Label("Welcome, " + passenger.getName() + "!");
        welcomeLabel.setFont(new Font("Arial", 22));
        welcomeLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        
        // Check for active ride
        UserStorage storage = new UserStorage();
        RideHistory history = new RideHistory(storage);
        RideMatch activeRide = history.getActiveRideForPassenger(passenger);
        
        // Menu Options
        Button requestRideBtn = new Button("Request Ride");
        requestRideBtn.setPrefWidth(280);
        requestRideBtn.setPrefHeight(50);
        requestRideBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold; -fx-background-radius: 25;");
        requestRideBtn.setOnAction(e -> {
            if (activeRide != null) {
                showAlert("Already Active", "You already have an active ride. Please complete or cancel it first.");
            } else {
                RequestRideController.showRequestRide(stage, cityGraph, passenger);
            }
        });
        
        Button viewHistoryBtn = new Button("View Ride History");
        viewHistoryBtn.setPrefWidth(280);
        viewHistoryBtn.setPrefHeight(50);
        viewHistoryBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold; -fx-background-radius: 25;");
        viewHistoryBtn.setOnAction(e -> {
            RideHistoryController.showPassengerHistory(stage, cityGraph, passenger);
        });
        
        Button currentRideBtn = null;
        if (activeRide != null) {
            currentRideBtn = new Button("View Current Ride");
            currentRideBtn.setPrefWidth(280);
            currentRideBtn.setPrefHeight(50);
            currentRideBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold; -fx-background-radius: 25;");
            currentRideBtn.setOnAction(e -> {
                CurrentRideController.showCurrentRide(stage, cityGraph, passenger, activeRide);
            });
        }
        
        Button cancelRideBtn = new Button("Cancel Active Ride");
        cancelRideBtn.setPrefWidth(280);
        cancelRideBtn.setPrefHeight(50);
        cancelRideBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold; -fx-background-radius: 25;");
        cancelRideBtn.setOnAction(e -> {
            if (activeRide == null) {
                showAlert("No Active Ride", "You don't have any active ride to cancel.");
            } else {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Cancel Ride");
                confirm.setHeaderText("Are you sure you want to cancel this ride?");
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        activeRide.getDriver().incrementSeat(passenger.getId());
                        storage.saveDrivers();
                        history.removeRide(activeRide);
                        showAlert("Success", "Ride cancelled successfully!");
                        showDashboard(stage, cityGraph, passenger);
                    }
                });
            }
        });
        
        Button logoutBtn = new Button("Logout");
        logoutBtn.setPrefWidth(280);
        logoutBtn.setPrefHeight(45);
        logoutBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 14; -fx-background-radius: 20;");
        logoutBtn.setOnAction(e -> LoginController.showLoginScreen(stage, cityGraph));
        
        root.getChildren().add(welcomeLabel);
        root.getChildren().add(requestRideBtn);
        if (activeRide != null && currentRideBtn != null) {
            root.getChildren().add(currentRideBtn);
        }
        root.getChildren().addAll(viewHistoryBtn, cancelRideBtn, logoutBtn);
        
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

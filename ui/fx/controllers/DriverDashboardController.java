package ui.fx.controllers;

import datastructures.Graph;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.Driver;
import model.RideMatch;
import storage.RideHistory;
import storage.UserStorage;

import java.util.List;

public class DriverDashboardController {
    
    public static void showDashboard(Stage stage, Graph cityGraph, Driver driver) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30, 20, 30, 20));
        root.setStyle("-fx-background-color: white;");
        
        // Welcome Label
        Label welcomeLabel = new Label("Welcome, " + driver.getName() + "!");
        welcomeLabel.setFont(new Font("Arial", 22));
        welcomeLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        
        UserStorage storage = new UserStorage();
        RideHistory history = new RideHistory(storage);
        List<RideMatch> activeRides = history.getActiveRidesForDriver(driver);
        
        // Menu Options
        Button createRideBtn = new Button("Create Ride");
        createRideBtn.setPrefWidth(280);
        createRideBtn.setPrefHeight(50);
        createRideBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold; -fx-background-radius: 25;");
        createRideBtn.setOnAction(e -> {
            showAlert("Coming Soon", "Create Ride feature will be available soon!");
            // TODO: Implement create ride
        });
        
        Button viewPassengersBtn = new Button("View / Cancel Passengers");
        viewPassengersBtn.setPrefWidth(280);
        viewPassengersBtn.setPrefHeight(50);
        viewPassengersBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold; -fx-background-radius: 25;");
        viewPassengersBtn.setOnAction(e -> {
            if (activeRides.isEmpty()) {
                showAlert("No Passengers", "No passengers booked yet.");
            } else {
                showPassengersList(stage, cityGraph, driver, activeRides, history);
            }
        });
        
        if (!activeRides.isEmpty()) {
            Button currentRideBtn = new Button("View Current Ride");
            currentRideBtn.setPrefWidth(280);
            currentRideBtn.setPrefHeight(50);
            currentRideBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold; -fx-background-radius: 25;");
            currentRideBtn.setOnAction(e -> {
                CurrentRideController.showCurrentRideDriver(stage, cityGraph, driver, activeRides.get(0));
            });
            root.getChildren().add(currentRideBtn);
        }
        
        Button viewHistoryBtn = new Button("View Ride History");
        viewHistoryBtn.setPrefWidth(280);
        viewHistoryBtn.setPrefHeight(50);
        viewHistoryBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold; -fx-background-radius: 25;");
        viewHistoryBtn.setOnAction(e -> {
            RideHistoryController.showDriverHistory(stage, cityGraph, driver);
        });
        
        Button logoutBtn = new Button("Logout");
        logoutBtn.setPrefWidth(280);
        logoutBtn.setPrefHeight(45);
        logoutBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 14; -fx-background-radius: 20;");
        logoutBtn.setOnAction(e -> LoginController.showLoginScreen(stage, cityGraph));
        
        root.getChildren().addAll(welcomeLabel, createRideBtn, viewPassengersBtn, viewHistoryBtn, logoutBtn);
        
        Scene scene = new Scene(root, 375, 667);
        stage.setScene(scene);
    }
    
    private static void showPassengersList(Stage stage, Graph cityGraph, Driver driver, List<RideMatch> activeRides, RideHistory history) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: white;");
        
        Label titleLabel = new Label("Passengers");
        titleLabel.setFont(new Font("Arial", 20));
        titleLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        
        VBox passengersList = new VBox(10);
        
        for (RideMatch match : activeRides) {
            VBox passengerCard = new VBox(5);
            passengerCard.setPadding(new Insets(10));
            passengerCard.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 10;");
            
            Label nameLabel = new Label("Passenger: " + match.getPassenger().getName());
            nameLabel.setFont(new Font("Arial", 14));
            nameLabel.setStyle("-fx-text-fill: #27ae60;");
            
            Label routeLabel = new Label("Route: " + history.getPickupLocation(match) + " → " + history.getDropOffLocation(match));
            routeLabel.setFont(new Font("Arial", 12));
            
            Button cancelBtn = new Button("Cancel");
            cancelBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 12; -fx-background-radius: 15;");
            cancelBtn.setOnAction(e -> {
                match.getDriver().incrementSeat(match.getPassenger().getId());
                UserStorage storage = new UserStorage();
                storage.saveDrivers();
                history.removeRide(match);
                showAlert("Success", "Passenger removed successfully!");
                showDashboard(stage, cityGraph, driver);
            });
            
            passengerCard.getChildren().addAll(nameLabel, routeLabel, cancelBtn);
            passengersList.getChildren().add(passengerCard);
        }
        
        ScrollPane scrollPane = new ScrollPane(passengersList);
        scrollPane.setPrefWidth(335);
        scrollPane.setPrefHeight(400);
        
        Button backBtn = new Button("Back");
        backBtn.setPrefWidth(335);
        backBtn.setPrefHeight(40);
        backBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 20;");
        backBtn.setOnAction(e -> showDashboard(stage, cityGraph, driver));
        
        root.getChildren().addAll(titleLabel, scrollPane, backBtn);
        
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

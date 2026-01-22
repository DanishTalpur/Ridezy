package ui.fx.controllers;

import datastructures.Graph;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
        VBox root = new VBox(25);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40, 20, 30, 20));
        root.setStyle("-fx-background-color: white;");

        // Greeting with emoji
        Label greetingLabel = new Label("🚗");
        greetingLabel.setFont(new Font("Arial", 50));

        UserStorage storage = new UserStorage();
        // Get driver from storage to ensure we have the same instance
        Driver driverTemp = storage.getDrivers().get(driver.getId());
        final Driver storedDriver = driverTemp != null ? driverTemp : driver;
        RideHistory history = new RideHistory(storage);
        List<RideMatch> activeRides = history.getActiveRidesForDriver(storedDriver);

        // Welcome Label
        Label welcomeLabel = new Label("Welcome, " + storedDriver.getName() + "!");
        welcomeLabel.setFont(new Font("Arial", 22));
        welcomeLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");

        // Status indicator
        VBox statusBox = new VBox(5);
        statusBox.setAlignment(Pos.CENTER);
        statusBox.setPadding(new Insets(15));
        statusBox.setStyle("-fx-background-color: " + (!activeRides.isEmpty() ? "#e8f5e9" : "#f8f9fa") +
                "; -fx-background-radius: 10; -fx-border-color: " +
                (!activeRides.isEmpty() ? "#27ae60" : "#ccc") + "; -fx-border-width: 1; -fx-border-radius: 10;");

        Label statusLabel = new Label(!activeRides.isEmpty() ? "🟢 Active Ride" : "⚪ No Active Ride");
        statusLabel.setFont(new Font("Arial", 13));
        statusLabel.setStyle("-fx-text-fill: " + (!activeRides.isEmpty() ? "#27ae60" : "#666") + "; -fx-font-weight: bold;");

        if (!activeRides.isEmpty()) {
            Label passengersLabel = new Label(activeRides.size() + " passenger(s) booked");
            passengersLabel.setFont(new Font("Arial", 11));
            passengersLabel.setStyle("-fx-text-fill: #666;");
            statusBox.getChildren().addAll(statusLabel, passengersLabel);
        } else {
            statusBox.getChildren().add(statusLabel);
        }

        // Create Ride Button
        Button createRideBtn = new Button("➕ Create Ride");
        createRideBtn.setPrefWidth(280);
        createRideBtn.setPrefHeight(55);
        createRideBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold; -fx-background-radius: 25;");
        createRideBtn.setOnAction(e -> CreateRideController.showCreateRide(stage, cityGraph, storedDriver));

        // View Current Ride Button
        Button currentRideBtn = new Button("📍 View Current Ride");
        currentRideBtn.setPrefWidth(280);
        currentRideBtn.setPrefHeight(55);
        currentRideBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold; -fx-background-radius: 25;");
        currentRideBtn.setOnAction(e -> {
            // Reload active rides to ensure we have latest data
            UserStorage storage2 = new UserStorage();
            RideHistory history2 = new RideHistory(storage2);
            Driver driverTemp2 = storage2.getDrivers().get(driver.getId());
            final Driver storedDriver2 = driverTemp2 != null ? driverTemp2 : driver;
            List<RideMatch> currentActiveRides = history2.getActiveRidesForDriver(storedDriver2);
            if (!currentActiveRides.isEmpty()) {
                CurrentRideController.showCurrentRideDriver(stage, cityGraph, storedDriver2, currentActiveRides);
            } else {
                showNoActivePassengersMessage(stage, cityGraph, storedDriver2);
            }
        });

        // View Ride History Button
        Button viewHistoryBtn = new Button("📜 View Ride History");
        viewHistoryBtn.setPrefWidth(280);
        viewHistoryBtn.setPrefHeight(55);
        viewHistoryBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold; -fx-background-radius: 25;");
        viewHistoryBtn.setOnAction(e -> RideHistoryController.showDriverHistory(stage, cityGraph, storedDriver));

        // Logout Button
        Button logoutBtn = new Button("🚪 Logout");
        logoutBtn.setPrefWidth(280);
        logoutBtn.setPrefHeight(45);
        logoutBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 14; -fx-background-radius: 20;");
        logoutBtn.setOnAction(e -> LoginController.showLoginScreen(stage, cityGraph));

        root.getChildren().addAll(greetingLabel, welcomeLabel, statusBox, createRideBtn, currentRideBtn, viewHistoryBtn, logoutBtn);

        Scene scene = new Scene(root, 375, 667);
        stage.setScene(scene);
    }

    private static void showNoActivePassengersMessage(Stage stage, Graph cityGraph, Driver driver) {
        VBox root = new VBox(30);
        root.setPadding(new Insets(50, 20, 50, 20));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: white;");

        Label iconLabel = new Label("ℹ️");
        iconLabel.setFont(new Font("Arial", 60));

        Label titleLabel = new Label("No Active Passengers");
        titleLabel.setFont(new Font("Arial", 24));
        titleLabel.setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold;");

        Label messageLabel = new Label("You don't have any active passengers at the moment.\nCreate a ride to get started!");
        messageLabel.setFont(new Font("Arial", 14));
        messageLabel.setStyle("-fx-text-fill: #666;");
        messageLabel.setWrapText(true);
        messageLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        messageLabel.setMaxWidth(280);

        Button createBtn = new Button("Create a Ride");
        createBtn.setPrefWidth(280);
        createBtn.setPrefHeight(50);
        createBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold; -fx-background-radius: 25;");
        createBtn.setOnAction(e -> CreateRideController.showCreateRide(stage, cityGraph, driver));

        Button backBtn = new Button("Back to Dashboard");
        backBtn.setPrefWidth(280);
        backBtn.setPrefHeight(45);
        backBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 14; -fx-background-radius: 20;");
        backBtn.setOnAction(e -> {
            // Reload driver from storage
            UserStorage storage3 = new UserStorage();
            Driver driverTemp3 = storage3.getDrivers().get(driver.getId());
            final Driver storedDriver3 = driverTemp3 != null ? driverTemp3 : driver;
            showDashboard(stage, cityGraph, storedDriver3);
        });

        root.getChildren().addAll(iconLabel, titleLabel, messageLabel, createBtn, backBtn);

        Scene scene = new Scene(root, 375, 667);
        stage.setScene(scene);
    }
}
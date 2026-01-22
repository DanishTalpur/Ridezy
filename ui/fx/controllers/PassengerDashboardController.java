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
import model.Passenger;
import model.RideMatch;
import storage.RideHistory;
import storage.UserStorage;

public class PassengerDashboardController {

    public static void showDashboard(Stage stage, Graph cityGraph, Passenger passenger) {
        VBox root = new VBox(25);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40, 20, 30, 20));
        root.setStyle("-fx-background-color: white;");

        // Greeting with emoji
        Label greetingLabel = new Label("👋");
        greetingLabel.setFont(new Font("Arial", 50));

        // Welcome Label
        Label welcomeLabel = new Label("Welcome, " + passenger.getName() + "!");
        welcomeLabel.setFont(new Font("Arial", 22));
        welcomeLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");

        // Check for active ride - ensure we use passenger from storage
        UserStorage storage = new UserStorage();
        // Get passenger from storage to ensure we have the same instance
        Passenger storedPassenger = storage.getPassengers().get(passenger.getId());
        if (storedPassenger == null) {
            storedPassenger = passenger;
        }
        RideHistory history = new RideHistory(storage);
        RideMatch activeRide = history.getActiveRideForPassenger(storedPassenger);

        // Status indicator
        VBox statusBox = new VBox(5);
        statusBox.setAlignment(Pos.CENTER);
        statusBox.setPadding(new Insets(15));
        statusBox.setStyle("-fx-background-color: " + (activeRide != null ? "#e8f5e9" : "#f8f9fa") +
                "; -fx-background-radius: 10; -fx-border-color: " +
                (activeRide != null ? "#27ae60" : "#ccc") + "; -fx-border-width: 1; -fx-border-radius: 10;");

        Label statusLabel = new Label(activeRide != null ? "🟢 Active Ride" : "⚪ No Active Ride");
        statusLabel.setFont(new Font("Arial", 13));
        statusLabel.setStyle("-fx-text-fill: " + (activeRide != null ? "#27ae60" : "#666") + "; -fx-font-weight: bold;");

        if (activeRide != null) {
            Label driverLabel = new Label("Driver: " + activeRide.getDriver().getName());
            driverLabel.setFont(new Font("Arial", 11));
            driverLabel.setStyle("-fx-text-fill: #666;");
            statusBox.getChildren().addAll(statusLabel, driverLabel);
        } else {
            statusBox.getChildren().add(statusLabel);
        }

        // Request Ride Button
        Button requestRideBtn = new Button(activeRide != null ? "⚠️ Request New Ride" : "🚗 Request Ride");
        requestRideBtn.setPrefWidth(280);
        requestRideBtn.setPrefHeight(55);
        requestRideBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold; -fx-background-radius: 25;");
        requestRideBtn.setOnAction(e -> {
            if (activeRide != null) {
                showAlreadyActiveMessage(stage, cityGraph, passenger);
            } else {
                RequestRideController.showRequestRide(stage, cityGraph, passenger);
            }
        });

        // View Current Ride Button
        Button currentRideBtn = new Button("📍 View Current Ride");
        currentRideBtn.setPrefWidth(280);
        currentRideBtn.setPrefHeight(55);
        currentRideBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold; -fx-background-radius: 25;");
        currentRideBtn.setOnAction(e -> {
            // Reload active ride to ensure we have latest data
            UserStorage storage2 = new UserStorage();
            RideHistory history2 = new RideHistory(storage2);
            Passenger storedPassenger2 = storage2.getPassengers().get(passenger.getId());
            if (storedPassenger2 == null) {
                storedPassenger2 = passenger;
            }
            RideMatch currentActiveRide = history2.getActiveRideForPassenger(storedPassenger2);
            if (currentActiveRide != null) {
                CurrentRideController.showCurrentRide(stage, cityGraph, storedPassenger2, currentActiveRide);
            } else {
                showNoActiveRideMessage(stage, cityGraph, storedPassenger2);
            }
        });

        // View Ride History Button
        Button viewHistoryBtn = new Button("📜 View Ride History");
        viewHistoryBtn.setPrefWidth(280);
        viewHistoryBtn.setPrefHeight(55);
        viewHistoryBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold; -fx-background-radius: 25;");
        viewHistoryBtn.setOnAction(e -> RideHistoryController.showPassengerHistory(stage, cityGraph, passenger));

        // Logout Button
        Button logoutBtn = new Button("🚪 Logout");
        logoutBtn.setPrefWidth(280);
        logoutBtn.setPrefHeight(45);
        logoutBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 14; -fx-background-radius: 20;");
        logoutBtn.setOnAction(e -> LoginController.showLoginScreen(stage, cityGraph));

        // Add all components
        root.getChildren().addAll(greetingLabel, welcomeLabel, statusBox, requestRideBtn, currentRideBtn, viewHistoryBtn, logoutBtn);

        Scene scene = new Scene(root, 375, 667);
        stage.setScene(scene);
    }

    private static void showAlreadyActiveMessage(Stage stage, Graph cityGraph, Passenger passenger) {
        VBox root = new VBox(30);
        root.setPadding(new Insets(50, 20, 50, 20));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: white;");

        Label iconLabel = new Label("⚠️");
        iconLabel.setFont(new Font("Arial", 60));

        Label titleLabel = new Label("Already Active");
        titleLabel.setFont(new Font("Arial", 24));
        titleLabel.setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");

        Label messageLabel = new Label("You already have an active ride.\nPlease complete or cancel it first.");
        messageLabel.setFont(new Font("Arial", 14));
        messageLabel.setStyle("-fx-text-fill: #666;");
        messageLabel.setWrapText(true);
        messageLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        messageLabel.setMaxWidth(280);

        Button backBtn = new Button("Back to Dashboard");
        backBtn.setPrefWidth(280);
        backBtn.setPrefHeight(50);
        backBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold; -fx-background-radius: 25;");
        backBtn.setOnAction(e -> {
            // Reload passenger from storage
            UserStorage storage3 = new UserStorage();
            Passenger storedPassenger3 = storage3.getPassengers().get(passenger.getId());
            if (storedPassenger3 == null) {
                storedPassenger3 = passenger;
            }
            showDashboard(stage, cityGraph, storedPassenger3);
        });

        root.getChildren().addAll(iconLabel, titleLabel, messageLabel, backBtn);

        Scene scene = new Scene(root, 375, 667);
        stage.setScene(scene);
    }

    private static void showNoActiveRideMessage(Stage stage, Graph cityGraph, Passenger passenger) {
        VBox root = new VBox(30);
        root.setPadding(new Insets(50, 20, 50, 20));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: white;");

        Label iconLabel = new Label("ℹ️");
        iconLabel.setFont(new Font("Arial", 60));

        Label titleLabel = new Label("No Active Ride");
        titleLabel.setFont(new Font("Arial", 24));
        titleLabel.setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold;");

        Label messageLabel = new Label("You don't have any active ride at the moment.\nWould you like to request one?");
        messageLabel.setFont(new Font("Arial", 14));
        messageLabel.setStyle("-fx-text-fill: #666;");
        messageLabel.setWrapText(true);
        messageLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        messageLabel.setMaxWidth(280);

        Button requestBtn = new Button("Request a Ride");
        requestBtn.setPrefWidth(280);
        requestBtn.setPrefHeight(50);
        requestBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold; -fx-background-radius: 25;");
        requestBtn.setOnAction(e -> RequestRideController.showRequestRide(stage, cityGraph, passenger));

        Button backBtn = new Button("Back to Dashboard");
        backBtn.setPrefWidth(280);
        backBtn.setPrefHeight(45);
        backBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 14; -fx-background-radius: 20;");
        backBtn.setOnAction(e -> showDashboard(stage, cityGraph, passenger));

        root.getChildren().addAll(iconLabel, titleLabel, messageLabel, requestBtn, backBtn);

        Scene scene = new Scene(root, 375, 667);
        stage.setScene(scene);
    }
}
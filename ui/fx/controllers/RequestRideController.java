package ui.fx.controllers;

import datastructures.Graph;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.Passenger;
import model.RideMatch;
import service.MatchingEngine;
import storage.RideHistory;
import storage.UserStorage;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class RequestRideController {

    public static void showRequestRide(Stage stage, Graph cityGraph, Passenger passenger) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30, 20, 30, 20));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: white;");

        Label titleLabel = new Label("Request a Ride");
        titleLabel.setFont(new Font("Arial", 24));
        titleLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");

        Label subtitleLabel = new Label("Fill in your trip details");
        subtitleLabel.setFont(new Font("Arial", 12));
        subtitleLabel.setStyle("-fx-text-fill: #666;");

        VBox formContainer = new VBox(15);
        formContainer.setAlignment(Pos.CENTER);
        formContainer.setPadding(new Insets(20));
        formContainer.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 15; -fx-border-color: #27ae60; -fx-border-width: 2; -fx-border-radius: 15;");

        TextField pickupField = new TextField();
        pickupField.setPromptText("Pickup Location (e.g., University)");
        pickupField.setPrefWidth(280);
        pickupField.setPrefHeight(45);
        pickupField.setStyle("-fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #27ae60; -fx-border-width: 2; -fx-font-size: 13;");

        TextField dropoffField = new TextField();
        dropoffField.setPromptText("Drop-off Location (e.g., Defense)");
        dropoffField.setPrefWidth(280);
        dropoffField.setPrefHeight(45);
        dropoffField.setStyle("-fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #27ae60; -fx-border-width: 2; -fx-font-size: 13;");

        TextField timeField = new TextField();
        timeField.setPromptText("Preferred Time (HH:mm)");
        timeField.setPrefWidth(280);
        timeField.setPrefHeight(45);
        timeField.setStyle("-fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #27ae60; -fx-border-width: 2; -fx-font-size: 13;");

        // Error label for inline feedback
        Label errorLabel = new Label();
        errorLabel.setFont(new Font("Arial", 11));
        errorLabel.setStyle("-fx-text-fill: #e74c3c;");
        errorLabel.setWrapText(true);
        errorLabel.setMaxWidth(280);
        errorLabel.setVisible(false);

        formContainer.getChildren().addAll(pickupField, dropoffField, timeField, errorLabel);

        Button requestBtn = new Button("Find Rides");
        requestBtn.setPrefWidth(280);
        requestBtn.setPrefHeight(50);
        requestBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold; -fx-background-radius: 25;");
        requestBtn.setOnAction(e -> {
            String pickup = pickupField.getText().trim();
            String dropoff = dropoffField.getText().trim();
            String timeStr = timeField.getText().trim();

            // Inline validation
            if (pickup.isEmpty() || dropoff.isEmpty() || timeStr.isEmpty()) {
                errorLabel.setText("⚠️ Please fill all fields!");
                errorLabel.setVisible(true);
                return;
            }

            try {
                LocalTime time = LocalTime.parse(timeStr);
                passenger.setPickupLocation(pickup);
                passenger.setDropOffLocation(dropoff);
                passenger.setPreferredTime(time);

                UserStorage userStorage = new UserStorage();
                // Save passenger data to ensure persistence
                userStorage.savePassengers();
                RideHistory rideHistory = new RideHistory(userStorage);
                MatchingEngine matchingEngine = new MatchingEngine(cityGraph, userStorage, rideHistory);

                PriorityQueue<RideMatch> matches = matchingEngine.findMatches(passenger);

                if (matches.isEmpty()) {
                    errorLabel.setText("❌ No rides available for your route. Try different locations or time.");
                    errorLabel.setVisible(true);
                    return;
                }

                List<RideMatch> allMatches = new ArrayList<>();
                while (!matches.isEmpty()) {
                    allMatches.add(matches.poll());
                }

                // Show ride selection window
                SelectRideController.showRideSelection(stage, cityGraph, passenger, allMatches);

            } catch (Exception ex) {
                errorLabel.setText("⚠️ Invalid time format. Use HH:mm (e.g., 14:30)");
                errorLabel.setVisible(true);
            }
        });

        Button backBtn = new Button("Back");
        backBtn.setPrefWidth(280);
        backBtn.setPrefHeight(45);
        backBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 20; -fx-font-size: 14;");
        backBtn.setOnAction(e -> PassengerDashboardController.showDashboard(stage, cityGraph, passenger));

        root.getChildren().addAll(titleLabel, subtitleLabel, formContainer, requestBtn, backBtn);

        Scene scene = new Scene(root, 375, 667);
        stage.setScene(scene);
    }
}
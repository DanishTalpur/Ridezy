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
import storage.RideHistory;
import storage.UserStorage;

import java.time.LocalTime;

public class CreateRideController {

    public static void showCreateRide(Stage stage, Graph cityGraph, Driver driver) {


        // Check if driver already has an active ride
        UserStorage storage = new UserStorage();
        RideHistory history = new RideHistory(storage);

        RideHistory rideHistory = new RideHistory(storage);

        if (history.hasActiveRide(driver)) {
            showAlreadyActiveScreen(stage, cityGraph, driver);
            return;
        }

        VBox root = new VBox(20);
        root.setPadding(new Insets(30, 20, 30, 20));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: white;");

        Label titleLabel = new Label("Create a Ride");
        titleLabel.setFont(new Font("Arial", 24));
        titleLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");

        Label subtitleLabel = new Label("Fill in the ride details");
        subtitleLabel.setFont(new Font("Arial", 12));
        subtitleLabel.setStyle("-fx-text-fill: #666;");

        VBox formContainer = new VBox(15);
        formContainer.setAlignment(Pos.CENTER);
        formContainer.setPadding(new Insets(20));
        formContainer.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 15; -fx-border-color: #27ae60; -fx-border-width: 2; -fx-border-radius: 15;");

        TextField startField = new TextField();
        startField.setPromptText("Start Location (e.g., University)");
        startField.setPrefWidth(280);
        startField.setPrefHeight(45);
        startField.setStyle("-fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #27ae60; -fx-border-width: 2; -fx-font-size: 13;");

        TextField endField = new TextField();
        endField.setPromptText("End Location (e.g., Defense)");
        endField.setPrefWidth(280);
        endField.setPrefHeight(45);
        endField.setStyle("-fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #27ae60; -fx-border-width: 2; -fx-font-size: 13;");

        TextField timeField = new TextField();
        timeField.setPromptText("Departure Time (HH:mm)");
        timeField.setPrefWidth(280);
        timeField.setPrefHeight(45);
        timeField.setStyle("-fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #27ae60; -fx-border-width: 2; -fx-font-size: 13;");

        TextField seatsField = new TextField();
        seatsField.setPromptText("Available Seats");
        seatsField.setPrefWidth(280);
        seatsField.setPrefHeight(45);
        seatsField.setStyle("-fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #27ae60; -fx-border-width: 2; -fx-font-size: 13;");

        TextField priceField = new TextField();
        priceField.setPromptText("Price per Seat (Rs.)");
        priceField.setPrefWidth(280);
        priceField.setPrefHeight(45);
        priceField.setStyle("-fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #27ae60; -fx-border-width: 2; -fx-font-size: 13;");

        // Error label for inline feedback
        Label errorLabel = new Label();
        errorLabel.setFont(new Font("Arial", 11));
        errorLabel.setStyle("-fx-text-fill: #e74c3c;");
        errorLabel.setWrapText(true);
        errorLabel.setMaxWidth(280);
        errorLabel.setVisible(false);

        // Success label
        Label successLabel = new Label();
        successLabel.setFont(new Font("Arial", 11));
        successLabel.setStyle("-fx-text-fill: #27ae60;");
        successLabel.setWrapText(true);
        successLabel.setMaxWidth(280);
        successLabel.setVisible(false);

        formContainer.getChildren().addAll(startField, endField, timeField, seatsField, priceField, errorLabel, successLabel);

        Button createBtn = new Button("Create Ride");
        createBtn.setPrefWidth(280);
        createBtn.setPrefHeight(50);
        createBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold; -fx-background-radius: 25;");
        createBtn.setOnAction(e -> {
            String start = startField.getText().trim();
            String end = endField.getText().trim();
            String timeStr = timeField.getText().trim();
            String seatsStr = seatsField.getText().trim();
            String priceStr = priceField.getText().trim();

            errorLabel.setVisible(false);
            successLabel.setVisible(false);

            if (start.isEmpty() || end.isEmpty() || timeStr.isEmpty() || seatsStr.isEmpty() || priceStr.isEmpty()) {
                errorLabel.setText("⚠️ Please fill all fields!");
                errorLabel.setVisible(true);
                return;
            }

            try {
                LocalTime time = LocalTime.parse(timeStr);
                int seats = Integer.parseInt(seatsStr);
                double price = Double.parseDouble(priceStr);

                if (seats <= 0) {
                    errorLabel.setText("⚠️ Seats must be greater than 0!");
                    errorLabel.setVisible(true);
                    return;
                }

                if (price <= 0) {
                    errorLabel.setText("⚠️ Price must be greater than 0!");
                    errorLabel.setVisible(true);
                    return;
                }

                driver.setStartLocation(start);
                driver.setEndLocation(end);
                driver.setDepartureTime(time);
                driver.setAvailableSeats(seats);
                driver.setPricePerSeat(price);

                storage.saveDrivers();

                successLabel.setText("✅ Ride created successfully!");
                successLabel.setVisible(true);

                // Auto-redirect after 1 second
                javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1));
                pause.setOnFinished(ev -> DriverDashboardController.showDashboard(stage, cityGraph, driver));
                pause.play();

            } catch (NumberFormatException ex) {
                errorLabel.setText("⚠️ Invalid number format for seats or price!");
                errorLabel.setVisible(true);
            } catch (Exception ex) {
                errorLabel.setText("⚠️ Invalid time format! Use HH:mm (e.g., 14:30)");
                errorLabel.setVisible(true);
            }
        });

        Button backBtn = new Button("Back");
        backBtn.setPrefWidth(280);
        backBtn.setPrefHeight(45);
        backBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 20; -fx-font-size: 14;");
        backBtn.setOnAction(e -> DriverDashboardController.showDashboard(stage, cityGraph, driver));

        root.getChildren().addAll(titleLabel, subtitleLabel, formContainer, createBtn, backBtn);

        Scene scene = new Scene(root, 375, 667);
        stage.setScene(scene);
    }

    private static void showAlreadyActiveScreen(Stage stage, Graph cityGraph, Driver driver) {
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
        backBtn.setOnAction(e -> DriverDashboardController.showDashboard(stage, cityGraph, driver));

        root.getChildren().addAll(iconLabel, titleLabel, messageLabel, backBtn);

        Scene scene = new Scene(root, 375, 667);
        stage.setScene(scene);
    }



}
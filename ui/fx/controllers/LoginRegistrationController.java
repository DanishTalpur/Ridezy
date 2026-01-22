package ui.fx.controllers;

import datastructures.Graph;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.Driver;
import model.Passenger;
import storage.UserStorage;

public class LoginRegistrationController {

    private static UserStorage storage = new UserStorage();

    public static void showLoginRegistration(Stage stage, Graph cityGraph, String userType) {
        VBox root = new VBox(25);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40, 30, 40, 30));
        root.setStyle("-fx-background-color: white;");

        String typeLabel = userType.equals("passenger") ? "Passenger" : "Driver";

        // Title
        Label titleLabel = new Label(typeLabel + " Login");
        titleLabel.setFont(new Font("Arial", 24));
        titleLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");

        Label subtitleLabel = new Label("Enter your ID or create new account");
        subtitleLabel.setFont(new Font("Arial", 12));
        subtitleLabel.setStyle("-fx-text-fill: #666;");

        // ID Field
        Label idLabel = new Label("Enter " + typeLabel + " ID:");
        idLabel.setFont(new Font("Arial", 14));
        idLabel.setStyle("-fx-text-fill: #27ae60;");

        TextField idField = new TextField();
        idField.setPrefWidth(280);
        idField.setPrefHeight(45);
        idField.setPromptText("Your ID (e.g., P001 or D001)");
        idField.setStyle("-fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #27ae60; -fx-border-width: 2; -fx-font-size: 14;");

        // Name Field (for new users)
        Label nameLabel = new Label("Name (for new users only):");
        nameLabel.setFont(new Font("Arial", 14));
        nameLabel.setStyle("-fx-text-fill: #27ae60;");

        TextField nameField = new TextField();
        nameField.setPrefWidth(280);
        nameField.setPrefHeight(45);
        nameField.setPromptText("Your full name");
        nameField.setStyle("-fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #27ae60; -fx-border-width: 2; -fx-font-size: 14;");

        // Login/Register Button
        Button loginBtn = new Button("Login / Register");
        loginBtn.setPrefWidth(280);
        loginBtn.setPrefHeight(50);
        loginBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold; -fx-background-radius: 25;");

        loginBtn.setOnAction(e -> {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();

            if (id.isEmpty()) {
                showAlert("Error", "Please enter your ID!");
                return;
            }

            if (userType.equals("passenger")) {
                Passenger passenger = storage.getPassengers().get(id);
                if (passenger == null) {
                    // New user - need name
                    if (name.isEmpty()) {
                        showAlert("Error", "Please enter your name for registration!");
                        return;
                    }
                    passenger = new Passenger(id, name);
                    storage.addPassenger(passenger);
                }
                PassengerDashboardController.showDashboard(stage, cityGraph, passenger);
            } else {
                Driver driver = storage.getDrivers().get(id);
                if (driver == null) {
                    // New user - need name
                    if (name.isEmpty()) {
                        showAlert("Error", "Please enter your name for registration!");
                        return;
                    }
                    driver = new Driver(id, name);
                    storage.addDriver(driver);
                }
                DriverDashboardController.showDashboard(stage, cityGraph, driver);
            }
        });

        // Back Button
        Button backBtn = new Button("Back");
        backBtn.setPrefWidth(280);
        backBtn.setPrefHeight(45);
        backBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 14; -fx-background-radius: 20;");
        backBtn.setOnAction(e -> LoginController.showLoginScreen(stage, cityGraph));

        root.getChildren().addAll(titleLabel, subtitleLabel, idLabel, idField, nameLabel, nameField, loginBtn, backBtn);

        Scene scene = new Scene(root, 375, 667);
        stage.setScene(scene);
    }

    private static void showAlert(String title, String message) {
        Alert alert = new Alert(title.equals("Error") ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
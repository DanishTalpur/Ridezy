package ui.fx.controllers;

import datastructures.Graph;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class LoginController {
    
    public static void showLoginScreen(Stage stage, Graph cityGraph) {
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40, 20, 40, 20));
        root.setStyle("-fx-background-color: white;");
        
        // Title
        Label titleLabel = new Label("Welcome to Ridezy");
        titleLabel.setFont(new Font("Arial", 28));
        titleLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        
        Label subtitleLabel = new Label("Let's ride together 🚗");
        subtitleLabel.setFont(new Font("Arial", 16));
        subtitleLabel.setStyle("-fx-text-fill: #27ae60;");
        
        // Passenger Button
        Button passengerBtn = new Button("Login as Passenger");
        passengerBtn.setPrefWidth(280);
        passengerBtn.setPrefHeight(50);
        passengerBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold; -fx-background-radius: 25;");
        passengerBtn.setOnAction(e -> LoginRegistrationController.showLoginRegistration(stage, cityGraph, "passenger"));
        
        // Driver Button
        Button driverBtn = new Button("Login as Driver");
        driverBtn.setPrefWidth(280);
        driverBtn.setPrefHeight(50);
        driverBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold; -fx-background-radius: 25;");
        driverBtn.setOnAction(e -> LoginRegistrationController.showLoginRegistration(stage, cityGraph, "driver"));
        
        root.getChildren().addAll(titleLabel, subtitleLabel, passengerBtn, driverBtn);
        
        Scene scene = new Scene(root, 375, 667);
        stage.setScene(scene);
    }
}


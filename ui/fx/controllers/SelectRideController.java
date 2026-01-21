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
import storage.RideHistory;
import storage.UserStorage;

import java.util.List;

public class SelectRideController {
    
    public static void showRideSelection(Stage stage, Graph cityGraph, Passenger passenger, List<RideMatch> matches) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: white;");
        
        Label titleLabel = new Label("Available Rides");
        titleLabel.setFont(new Font("Arial", 20));
        titleLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPrefWidth(335);
        scrollPane.setPrefHeight(400);
        scrollPane.setStyle("-fx-background: white;");
        
        VBox ridesContainer = new VBox(10);
        ridesContainer.setPadding(new Insets(10));
        
        for (int i = 0; i < matches.size(); i++) {
            RideMatch match = matches.get(i);
            Button rideBtn = createRideButton(match, i + 1);
            int finalI = i;
            rideBtn.setOnAction(e -> {
                RideMatch selected = matches.get(finalI);
                confirmRide(stage, cityGraph, passenger, selected);
            });
            ridesContainer.getChildren().add(rideBtn);
        }
        
        scrollPane.setContent(ridesContainer);
        
        Button backBtn = new Button("Back");
        backBtn.setPrefWidth(335);
        backBtn.setPrefHeight(40);
        backBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 20;");
        backBtn.setOnAction(e -> RequestRideController.showRequestRide(stage, cityGraph, passenger));
        
        root.getChildren().addAll(titleLabel, scrollPane, backBtn);
        
        Scene scene = new Scene(root, 375, 667);
        stage.setScene(scene);
    }
    
    private static Button createRideButton(RideMatch match, int index) {
        String text = String.format("%d. Driver: %s\n   Route: %s → %s\n   Time: %s | Price: Rs.%.0f | Distance: %.1f km",
                index,
                match.getDriver().getName(),
                match.getDriver().getStartLocation() != null ? match.getDriver().getStartLocation() : "N/A",
                match.getDriver().getEndLocation() != null ? match.getDriver().getEndLocation() : "N/A",
                match.getDriver().getDepartureTime() != null ? match.getDriver().getDepartureTime().toString() : "N/A",
                match.getDriver().getPricePerSeat(),
                match.getDistance());
        
        Button btn = new Button(text);
        btn.setPrefWidth(315);
        btn.setPrefHeight(100);
        btn.setStyle("-fx-background-color: white; -fx-text-fill: #27ae60; -fx-border-color: #27ae60; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10; -fx-font-size: 12; -fx-alignment: top-left;");
        btn.setWrapText(true);
        return btn;
    }
    
    private static void confirmRide(Stage stage, Graph cityGraph, Passenger passenger, RideMatch selectedMatch) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Ride");
        confirmAlert.setHeaderText("Confirm this ride?");
        confirmAlert.setContentText("Driver: " + selectedMatch.getDriver().getName() + 
                "\nRoute: " + selectedMatch.getDriver().getStartLocation() + " → " + selectedMatch.getDriver().getEndLocation() +
                "\nPrice: Rs." + selectedMatch.getDriver().getPricePerSeat());
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                UserStorage storage = new UserStorage();
                RideHistory history = new RideHistory(storage);
                
                if (history.hasActiveRide(passenger)) {
                    showAlert("Error", "You already have an active ride!");
                    return;
                }
                
                if (!selectedMatch.getDriver().hasAvailableSeats()) {
                    showAlert("Error", "Driver has no available seats.");
                    return;
                }
                
                selectedMatch.getDriver().decrementSeat(passenger.getId());
                storage.saveDrivers();
                history.addRide(selectedMatch, passenger.getPickupLocation(), passenger.getDropOffLocation());
                
                showAlert("Success", "Ride booked successfully!");
                CurrentRideController.showCurrentRide(stage, cityGraph, passenger, selectedMatch);
            }
        });
    }
    
    private static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


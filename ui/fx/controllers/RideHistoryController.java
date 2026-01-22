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
import model.Passenger;
import model.RideMatch;
import model.RideStatus;
import storage.RideHistory;
import storage.UserStorage;

import java.util.List;

public class RideHistoryController {

    public static void showPassengerHistory(Stage stage, Graph cityGraph, Passenger passenger) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: white;");

        Label titleLabel = new Label("My Ride History");
        titleLabel.setFont(new Font("Arial", 22));
        titleLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");

        UserStorage storage = new UserStorage();
        RideHistory history = new RideHistory(storage);
        List<RideMatch> rides = history.getRidesForPassenger(passenger);

        if (rides.isEmpty()) {
            Label noRidesLabel = new Label("No rides booked yet.");
            noRidesLabel.setFont(new Font("Arial", 14));
            noRidesLabel.setStyle("-fx-text-fill: #666;");
            root.getChildren().addAll(titleLabel, noRidesLabel);
        } else {
            TableView<RideMatch> table = createHistoryTable(history, rides, false);
            table.setPrefWidth(335);
            table.setPrefHeight(450);

            int activeCount = 0;
            int completedCount = 0;
            for (RideMatch match : rides) {
                if (history.getRideStatus(match) == RideStatus.ACTIVE) {
                    activeCount++;
                } else {
                    completedCount++;
                }
            }

            Label summaryLabel = new Label("Summary: " + activeCount + " Active | " + completedCount + " Completed | Total: " + rides.size());
            summaryLabel.setFont(new Font("Arial", 12));
            summaryLabel.setStyle("-fx-text-fill: #27ae60;");

            root.getChildren().addAll(titleLabel, table, summaryLabel);
        }

        Button backBtn = new Button("Back");
        backBtn.setPrefWidth(335);
        backBtn.setPrefHeight(40);
        backBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 20;");
        backBtn.setOnAction(e -> PassengerDashboardController.showDashboard(stage, cityGraph, passenger));

        root.getChildren().add(backBtn);

        Scene scene = new Scene(root, 375, 667);
        stage.setScene(scene);
    }

    public static void showDriverHistory(Stage stage, Graph cityGraph, Driver driver) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: white;");

        Label titleLabel = new Label("My Ride History");
        titleLabel.setFont(new Font("Arial", 22));
        titleLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");

        UserStorage storage = new UserStorage();
        RideHistory history = new RideHistory(storage);
        List<RideMatch> rides = history.getRidesForDriver(driver);

        if (rides.isEmpty()) {
            Label noRidesLabel = new Label("No rides created yet.");
            noRidesLabel.setFont(new Font("Arial", 14));
            noRidesLabel.setStyle("-fx-text-fill: #666;");
            root.getChildren().addAll(titleLabel, noRidesLabel);
        } else {
            TableView<RideMatch> table = createHistoryTable(history, rides, true);
            table.setPrefWidth(335);
            table.setPrefHeight(450);

            int activeCount = 0;
            int completedCount = 0;
            for (RideMatch match : rides) {
                if (history.getRideStatus(match) == RideStatus.ACTIVE) {
                    activeCount++;
                } else {
                    completedCount++;
                }
            }

            Label summaryLabel = new Label("Summary: " + activeCount + " Active | " + completedCount + " Completed | Total: " + rides.size());
            summaryLabel.setFont(new Font("Arial", 12));
            summaryLabel.setStyle("-fx-text-fill: #27ae60;");

            root.getChildren().addAll(titleLabel, table, summaryLabel);
        }

        Button backBtn = new Button("Back");
        backBtn.setPrefWidth(335);
        backBtn.setPrefHeight(40);
        backBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 20;");
        backBtn.setOnAction(e -> DriverDashboardController.showDashboard(stage, cityGraph, driver));

        root.getChildren().add(backBtn);

        Scene scene = new Scene(root, 375, 667);
        stage.setScene(scene);
    }

    private static TableView<RideMatch> createHistoryTable(RideHistory history, List<RideMatch> rides, boolean isDriver) {
        TableView<RideMatch> table = new TableView<>();

        TableColumn<RideMatch, String> nameCol = new TableColumn<>(isDriver ? "Passenger" : "Driver");
        nameCol.setCellValueFactory(data -> {
            if (isDriver) {
                return new javafx.beans.property.SimpleStringProperty(data.getValue().getPassenger().getName());
            } else {
                return new javafx.beans.property.SimpleStringProperty(data.getValue().getDriver().getName());
            }
        });
        nameCol.setPrefWidth(100);

        TableColumn<RideMatch, String> routeCol = new TableColumn<>("Route");
        routeCol.setCellValueFactory(data -> {
            String pickup = history.getPickupLocation(data.getValue());
            String dropoff = history.getDropOffLocation(data.getValue());
            if (pickup == null || pickup.isEmpty()) pickup = "N/A";
            if (dropoff == null || dropoff.isEmpty()) dropoff = "N/A";
            return new javafx.beans.property.SimpleStringProperty(pickup + " → " + dropoff);
        });
        routeCol.setPrefWidth(150);

        TableColumn<RideMatch, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> {
            String status = history.getRideStatus(data.getValue()) == RideStatus.ACTIVE ? "ACTIVE" : "COMPLETED";
            return new javafx.beans.property.SimpleStringProperty(status);
        });
        statusCol.setPrefWidth(85);

        table.getColumns().addAll(nameCol, routeCol, statusCol);
        table.getItems().addAll(rides);
        table.setStyle("-fx-border-color: #27ae60;");

        return table;
    }
}
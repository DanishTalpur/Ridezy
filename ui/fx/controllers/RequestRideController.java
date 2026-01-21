package ui.fx.controllers;

import datastructures.Graph;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import model.Passenger;
import model.RideMatch;
import service.MatchingEngine;
import storage.RideHistory;
import storage.UserStorage;
import util.CityGraphBuilder;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class RequestRideController {
    
    private static final String MAPBOX_ACCESS_TOKEN = "pk.eyJ1IjoiZGFuaXNodGFscHVyMTEiLCJhIjoiY21rbjdqcHAyMGpqNTNkczVnejlrZDh2cyJ9.Hk1DznDD_-tp6ZopOy5gig";
    
    public static void showRequestRide(Stage stage, Graph cityGraph, Passenger passenger) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: white;");
        
        // Top section with map view (embedded directly)
        WebView webView = new WebView();
        webView.setPrefWidth(375);
        webView.setPrefHeight(280);
        WebEngine webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);
        webEngine.loadContent(generateMapHTML(), "text/html");
        
        // Bottom section with form
        VBox formContainer = new VBox(12);
        formContainer.setPadding(new Insets(15));
        formContainer.setStyle("-fx-background-color: white;");
        
        Label titleLabel = new Label("Request a Ride");
        titleLabel.setFont(new Font("Arial", 18));
        titleLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        
        TextField pickupField = new TextField();
        pickupField.setPromptText("Pickup Location (e.g., University)");
        pickupField.setPrefWidth(340);
        pickupField.setPrefHeight(40);
        pickupField.setStyle("-fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #27ae60; -fx-font-size: 13;");
        
        TextField dropoffField = new TextField();
        dropoffField.setPromptText("Drop-off Location (e.g., Defense)");
        dropoffField.setPrefWidth(340);
        dropoffField.setPrefHeight(40);
        dropoffField.setStyle("-fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #27ae60; -fx-font-size: 13;");
        
        TextField timeField = new TextField();
        timeField.setPromptText("Preferred Time (HH:mm)");
        timeField.setPrefWidth(340);
        timeField.setPrefHeight(40);
        timeField.setStyle("-fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #27ae60; -fx-font-size: 13;");
        
        Button requestBtn = new Button("Find Rides");
        requestBtn.setPrefWidth(340);
        requestBtn.setPrefHeight(45);
        requestBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 15; -fx-font-weight: bold; -fx-background-radius: 25;");
        requestBtn.setOnAction(e -> {
            String pickup = pickupField.getText().trim();
            String dropoff = dropoffField.getText().trim();
            String timeStr = timeField.getText().trim();
            
            if (pickup.isEmpty() || dropoff.isEmpty() || timeStr.isEmpty()) {
                showAlert("Error", "Please fill all fields!");
                return;
            }
            
            try {
                LocalTime time = LocalTime.parse(timeStr);
                passenger.setPickupLocation(pickup);
                passenger.setDropOffLocation(dropoff);
                passenger.setPreferredTime(time);
                
                MatchingEngine matchingEngine = new MatchingEngine(cityGraph, new UserStorage());
                PriorityQueue<RideMatch> matches = matchingEngine.findMatches(passenger);
                
                if (matches.isEmpty()) {
                    showAlert("No Rides", "No rides available for your route. Please try again later.");
                    return;
                }
                
                List<RideMatch> allMatches = new ArrayList<>();
                while (!matches.isEmpty()) {
                    allMatches.add(matches.poll());
                }
                
                // Show ride selection window
                SelectRideController.showRideSelection(stage, cityGraph, passenger, allMatches);
                
            } catch (Exception ex) {
                showAlert("Error", "Invalid time format. Please use HH:mm format (e.g., 12:00)");
            }
        });
        
        Button backBtn = new Button("Back");
        backBtn.setPrefWidth(340);
        backBtn.setPrefHeight(40);
        backBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 20;");
        backBtn.setOnAction(e -> PassengerDashboardController.showDashboard(stage, cityGraph, passenger));
        
        formContainer.getChildren().addAll(titleLabel, pickupField, dropoffField, timeField, requestBtn, backBtn);
        formContainer.setAlignment(Pos.CENTER);
        
        root.setTop(webView);
        root.setCenter(formContainer);
        
        Scene scene = new Scene(root, 375, 667);
        stage.setScene(scene);
    }
    
    private static String generateMapHTML() {
        return "<!DOCTYPE html>\n" +
                "<html><head><meta charset='utf-8' />" +
                "<script src='https://api.mapbox.com/mapbox-gl-js/v2.15.0/mapbox-gl.js'></script>" +
                "<link href='https://api.mapbox.com/mapbox-gl-js/v2.15.0/mapbox-gl.css' rel='stylesheet' />" +
                "<style>body{margin:0;padding:0;}#map{width:100%;height:280px;}</style></head>" +
                "<body><div id='map'></div>" +
                "<script>mapboxgl.accessToken='" + MAPBOX_ACCESS_TOKEN + "';" +
                "var map=new mapboxgl.Map({container:'map',style:'mapbox://styles/mapbox/streets-v12'," +
                "center:[67.0011,24.8607],zoom:11});" +
                "map.addControl(new mapboxgl.NavigationControl());" +
                "new mapboxgl.Marker({color:'#27ae60'}).setLngLat([67.0011,24.8607]).addTo(map);" +
                "</script></body></html>";
    }
    
    private static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

package ui.fx.controllers;

import datastructures.Graph;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SplashScreenController {

    public static void showSplashScreen(Stage stage, Graph cityGraph) {
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: white;");

        try {
            // Load logo from img folder
            Image logo = new Image("file:img/logo.png");
            ImageView logoView = new ImageView(logo);

            // Set logo size (adjust as needed)
            logoView.setFitWidth(300);
            logoView.setPreserveRatio(true);

            root.getChildren().add(logoView);

            // Create fade-in animation
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), logoView);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();

            // Wait 2 seconds then transition to login screen
            PauseTransition delay = new PauseTransition(Duration.seconds(2));
            delay.setOnFinished(e -> LoginController.showLoginScreen(stage, cityGraph));
            delay.play();

        } catch (Exception e) {
            // If logo not found, show text splash
            javafx.scene.control.Label titleLabel = new javafx.scene.control.Label("🌈 RIDEZY 🌈");
            titleLabel.setStyle("-fx-font-size: 36; -fx-text-fill: #27ae60; -fx-font-weight: bold;");

            javafx.scene.control.Label subtitleLabel = new javafx.scene.control.Label("Let's ride together 😎");
            subtitleLabel.setStyle("-fx-font-size: 18; -fx-text-fill: #27ae60;");

            root.getChildren().addAll(titleLabel, subtitleLabel);

            PauseTransition delay = new PauseTransition(Duration.seconds(2));
            delay.setOnFinished(ev -> LoginController.showLoginScreen(stage, cityGraph));
            delay.play();
        }

        Scene scene = new Scene(root, 375, 667);
        stage.setScene(scene);
    }
}
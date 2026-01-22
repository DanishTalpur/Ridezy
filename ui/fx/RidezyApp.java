package ui.fx;

import datastructures.Graph;
import javafx.application.Application;
import javafx.stage.Stage;
import ui.fx.controllers.SplashScreenController;
import util.CityGraphBuilder;

public class RidezyApp extends Application {

    private static Graph cityGraph;
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        cityGraph = CityGraphBuilder.buildKarachiGraph();

        // Set mobile screen size (typical mobile dimensions)
        primaryStage.setWidth(375);  // iPhone standard width
        primaryStage.setHeight(667); // iPhone standard height
        primaryStage.setTitle("Ridezy - Carpooling App");
        primaryStage.setResizable(false); // Lock the window size for consistent mobile experience

        // Show splash screen first
        SplashScreenController.showSplashScreen(primaryStage, cityGraph);

        primaryStage.show();
    }

    public static Graph getCityGraph() {
        return cityGraph;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
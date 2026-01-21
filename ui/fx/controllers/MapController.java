package ui.fx.controllers;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class MapController {
    
    private static final String MAPBOX_ACCESS_TOKEN = "pk.eyJ1IjoiZGFuaXNodGFscHVyMTEiLCJhIjoiY21rbjdqcHAyMGpqNTNkczVnejlrZDh2cyJ9.Hk1DznDD_-tp6ZopOy5gig"; // Replace with your Mapbox token
    private static final double DEFAULT_LAT = 24.8607; // Karachi coordinates
    private static final double DEFAULT_LNG = 67.0011;
    
    public static void showMap(Stage stage, double lat, double lng) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(0));
        root.setStyle("-fx-background-color: white;");
        
        WebView webView = new WebView();
        webView.setPrefWidth(375);
        webView.setPrefHeight(667);
        
        WebEngine webEngine = webView.getEngine();
        
        // Enable JavaScript
        webEngine.setJavaScriptEnabled(true);
        
        // Load Mapbox GL JS HTML
        String htmlContent = generateMapHTML(lat, lng);
        webEngine.loadContent(htmlContent, "text/html");
        
        root.setCenter(webView);
        
        Scene scene = new Scene(root, 375, 667);
        stage.setScene(scene);
    }
    
    private static String generateMapHTML(double lat, double lng) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset='utf-8' />\n" +
                "    <meta name='viewport' content='width=device-width, initial-scale=1.0' />\n" +
                "    <title>Ridezy Map</title>\n" +
                "    <script src='https://api.mapbox.com/mapbox-gl-js/v2.15.0/mapbox-gl.js'></script>\n" +
                "    <link href='https://api.mapbox.com/mapbox-gl-js/v2.15.0/mapbox-gl.css' rel='stylesheet' />\n" +
                "    <style>\n" +
                "        body { margin: 0; padding: 0; width: 100%; height: 100%; overflow: hidden; }\n" +
                "        html { width: 100%; height: 100%; overflow: hidden; }\n" +
                "        #map { width: 100%; height: 100%; position: absolute; top: 0; left: 0; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div id='map'></div>\n" +
                "    <script>\n" +
                "        if (typeof mapboxgl !== 'undefined') {\n" +
                "            mapboxgl.accessToken = '" + MAPBOX_ACCESS_TOKEN + "';\n" +
                "            var map = new mapboxgl.Map({\n" +
                "                container: 'map',\n" +
                "                style: 'mapbox://styles/mapbox/streets-v12',\n" +
                "                center: [" + lng + ", " + lat + "],\n" +
                "                zoom: 12\n" +
                "            });\n" +
                "            \n" +
                "            map.on('load', function() {\n" +
                "                // Add navigation controls\n" +
                "                map.addControl(new mapboxgl.NavigationControl());\n" +
                "                \n" +
                "                // Add marker at center\n" +
                "                new mapboxgl.Marker({ color: '#27ae60' })\n" +
                "                    .setLngLat([" + lng + ", " + lat + "])\n" +
                "                    .addTo(map);\n" +
                "            });\n" +
                "        } else {\n" +
                "            document.getElementById('map').innerHTML = '<p style=\"padding: 20px;\">Loading map... Please check your internet connection.</p>';\n" +
                "        }\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
    }
    
    public static void showMap(Stage stage) {
        showMap(stage, DEFAULT_LAT, DEFAULT_LNG);
    }
}


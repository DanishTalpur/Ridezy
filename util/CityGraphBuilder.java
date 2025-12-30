package util;

import datastructures.Graph;

public class CityGraphBuilder {

    public static Graph buildKarachiGraph() {

        Graph graph = new Graph();

        // Locations
        graph.addLocation("University");
        graph.addLocation("Gulshan");
        graph.addLocation("Johar");
        graph.addLocation("Saddar");
        graph.addLocation("Defence");
        graph.addLocation("NorthNazimabad");

        // Routes (distances in km - approximate)
        graph.addEdge("University", "Gulshan", 6);
        graph.addEdge("University", "Johar", 8);
        graph.addEdge("University", "Saddar", 12);

        graph.addEdge("Gulshan", "Johar", 4);
        graph.addEdge("Gulshan", "NorthNazimabad", 7);

        graph.addEdge("Johar", "Defence", 15);
        graph.addEdge("Saddar", "Defence", 5);

        graph.addEdge("NorthNazimabad", "Saddar", 10);

        return graph;
    }
}

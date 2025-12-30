package datastructures;

import java.util.*;

public class Graph {

    private final Map<String, List<Edge>> adjacencyList;

    public Graph() {
        this.adjacencyList = new HashMap<>();
    }

    public void addLocation(String location) {
        adjacencyList.putIfAbsent(location, new ArrayList<>());
    }

    public void addEdge(String source, String destination, double distance) {
        adjacencyList.get(source).add(new Edge(destination, distance));
        adjacencyList.get(destination).add(new Edge(source, distance)); // Undirected
    }

    public List<Edge> getNeighbors(String location) {
        return adjacencyList.getOrDefault(location, new ArrayList<>());
    }

    public Set<String> getLocations() {
        return adjacencyList.keySet();
    }
}

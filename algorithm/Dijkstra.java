package algorithm;

import datastructures.Edge;
import datastructures.Graph;

import java.util.*;

public class Dijkstra {

    public static Map<String, Double> shortestPaths(Graph graph, String source) {
        Map<String, Double> distances = new HashMap<>();
        PriorityQueue<Map.Entry<String, Double>> pq =
                new PriorityQueue<>(Map.Entry.comparingByValue());

        for (String location : graph.getLocations()) {
            distances.put(location, Double.MAX_VALUE);
        }

        distances.put(source, 0.0);
        pq.offer(new AbstractMap.SimpleEntry<>(source, 0.0));

        while (!pq.isEmpty()) {
            String current = pq.poll().getKey();

            for (Edge edge : graph.getNeighbors(current)) {
                double newDist = distances.get(current) + edge.getWeight();
                if (newDist < distances.get(edge.getDestination())) {
                    distances.put(edge.getDestination(), newDist);
                    pq.offer(new AbstractMap.SimpleEntry<>(edge.getDestination(), newDist));
                }
            }
        }
        return distances;
    }
}

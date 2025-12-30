package datastructures;

public class Edge {

    private final String destination;
    private final double weight;

    public Edge(String destination, double weight) {
        this.destination = destination;
        this.weight = weight;
    }

    public String getDestination() {
        return destination;
    }

    public double getWeight() {
        return weight;
    }
}

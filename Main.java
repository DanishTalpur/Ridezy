import datastructures.Graph;
import ui.ConsoleUI;
import util.CityGraphBuilder;

public class Main {
    public static void main(String[] args) {
        Graph cityGraph = CityGraphBuilder.buildKarachiGraph();
        ConsoleUI ui = new ConsoleUI(cityGraph);
        ui.start();
    }
}


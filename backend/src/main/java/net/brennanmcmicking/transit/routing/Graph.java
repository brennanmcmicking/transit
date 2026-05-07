package net.brennanmcmicking.transit.routing;

import lombok.Builder;
import lombok.Getter;
import net.brennanmcmicking.transit.model.Stop;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Graph {

    // Nodes are stops, keyed by stopId here
    private Map<String, Node> nodes;

    public Graph(List<Node> nodes) {
        Map<String, Node> localMap = new ConcurrentHashMap<>();
        nodes.forEach(node -> localMap.put(node.stop.getId(), node));
        this.nodes = localMap;
    }

    public List<Node> getRoute(Node start, Node end) {
        return List.of();
    }


    @Getter
    @Builder
    public static class Node {
        Stop stop;
        List<Edge> edges;

    }

    @Getter
    @Builder
    public static class Edge {
        Node destination;
        Double weight;
    }
}

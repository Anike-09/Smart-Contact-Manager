package com.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Node {
    String name;
    List<Node> children;

    public Node(String name) {
        this.name = name;
        this.children = new ArrayList<>();
    }

    public void addChild(Node child) {
        children.add(child);
    }
}

class DAG {
    private Map<String, Node> nodes;
    private Map<String, String> flowerToBouquet;

    public DAG() {
        nodes = new HashMap<>();
        flowerToBouquet = new HashMap<>();
    }

    public void addNode(String name) {
        if (!nodes.containsKey(name)) {
            nodes.put(name, new Node(name));
        }
    }

    public void addEdge(String parent, String child) {
        if (nodes.containsKey(parent) && nodes.containsKey(child)) {
            Node parentNode = nodes.get(parent);
            Node childNode = nodes.get(child);
            parentNode.addChild(childNode);
        }
    }

    public void addFlowerToBouquet(String flower, String bouquet) {
        flowerToBouquet.put(flower, bouquet);
    }

    public String lookupBouquet(String flower) {
        return flowerToBouquet.getOrDefault(flower, "None");
    }
}

public class Main {
    public static void main(String[] args) {
        // Create a DAG
        DAG bouquetDAG = new DAG();

        // Add nodes
        bouquetDAG.addNode("bouquet1");
        bouquetDAG.addNode("bouquet2");
        bouquetDAG.addNode("bouquet3");
        bouquetDAG.addNode("bouquet4");
        bouquetDAG.addNode("bouquet5");

        // Add edges
        bouquetDAG.addEdge("bouquet1", "bouquet2");
        bouquetDAG.addEdge("bouquet1", "bouquet3");
        bouquetDAG.addEdge("bouquet2", "bouquet4");
        bouquetDAG.addEdge("bouquet3", "bouquet5");

        // Add flower to bouquet mappings
        bouquetDAG.addFlowerToBouquet("Red Rose", "bouquet1");

        // Lookup flower in the bouquet
        String flower = "Red Rose";
        String flower1 = "Pink Lily";
        String bouquet = bouquetDAG.lookupBouquet(flower);
        String bouquet1 = bouquetDAG.lookupBouquet(flower1);
        System.out.println("Flower: " + flower);
        System.out.println("Output: " + bouquet);
        System.out.println("Flower: " + flower1);
        System.out.println("Output: " + bouquet1);
    }
}

package de.xibix.model;

import java.util.ArrayList;
import java.util.List;

public class Element {

    private final List<Node> nodeList = new ArrayList<>();
    private final List<Element> neighbors = new ArrayList<>();
    private int id;
    private double value;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Node> getNodeList() {
        return nodeList;
    }

    public void addNode(Node n) {
        nodeList.add(n);
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public List<Element> getNeighbors() {
        return neighbors;
    }

    public void addNeighbor(Element element) {
        if (!neighbors.contains(element)) {
            neighbors.add(element);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Element element = (Element) o;

        return id == element.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}

package de.xibix.model;

import java.util.*;

public class Mesh {
    private final HashMap<Integer, Node> nodeMap = new HashMap<>();
    private final HashMap<Integer, Element> elementMap = new HashMap<>();

    public HashMap<Integer, Node> getNodeMap() {
        return nodeMap;
    }

    public HashMap<Integer, Element> getElementMap() {
        return elementMap;
    }

    public void addNode(Node n) {
        nodeMap.put(n.getId(), n);
    }

    public Element getElement(int id) {
        return elementMap.get(id);
    }

    public void addElement(Element element) {
        int id = element.getId();
        elementMap.put(id, element);
        List<Node> nodeList = element.getNodeList();
        for (Node node : nodeList) {
            node.addElementId(id);
            Set<Integer> elementIdList = node.getElementIdList();
            for (Integer elId : elementIdList) {
                if (elId != id) {
                    Element el2 = elementMap.get(elId);
                    el2.addNeighbor(element);
                    element.addNeighbor(el2);
                }
            }
        }
    }

    public Node getNode(int nodeId) {
        return nodeMap.get(nodeId);
    }

    /**
     * @return list of view spots, ordered by height (value)
     */
    public List<Element> findSpots() {
        Set<Map.Entry<Integer, Element>> elements = elementMap.entrySet();
        List<Element> allElements = new ArrayList<>();
        for (Map.Entry<Integer, Element> entry : elements) {
            allElements.add(entry.getValue());
        }

        List<Element> viewSpots = new ArrayList<>();
        for (Element e : allElements) {
            List<Element> neighbors = e.getNeighbors();
            double h = e.getValue();
            boolean isHighest = true;
            for (Element neighbor : neighbors) {
                if (neighbor.getValue() > h) {
                    isHighest = false;
                    break;
                }
            }
            if (isHighest) {
                viewSpots.add(e);
            }
        }

        viewSpots.sort((o1, o2) -> Double.compare(o2.getValue(), o1.getValue()));
        return viewSpots;
    }


}

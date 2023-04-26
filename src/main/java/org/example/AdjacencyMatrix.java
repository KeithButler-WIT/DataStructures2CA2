package org.example;

import java.lang.reflect.Array;

public class AdjacencyMatrix {
    public int[][] amat;
    public GraphNode<?>[] nodes;
    public int nodeCount=0;

    public AdjacencyMatrix(int size){
        amat = new int [size][size]; //All false values by default
        nodes = (GraphNode<?>[]) Array.newInstance(GraphNode.class, size);
    }

    public GraphNode<?> getNodeFromStationId (short stationId) {
        for (GraphNode<?> node : nodes) {
            if (node.getStationId() == stationId) return node;
        }
        return null;
    }

    public GraphNode<?> getNodeFromStationName(String stationName) {
        for (GraphNode<?> node : nodes) {
            if (node.getStationName().equals(stationName)) return node;
        }
        return null;
    }

    public int getNodeValue (int station) {
       return amat[station][station];
    }

}
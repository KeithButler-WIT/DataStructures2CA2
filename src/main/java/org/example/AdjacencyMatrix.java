package org.example;

import java.lang.reflect.Array;

public class AdjacencyMatrix {
    public boolean[][] amat;
    public GraphNode<?>[] nodes;
    public int nodeCount=0;

    // TODO: change this to store the weight instead of boolean
    // get weight from distance
    public AdjacencyMatrix(int size){
        amat = new boolean [size][size]; //All false values by default
        nodes = (GraphNode<?>[]) Array.newInstance(GraphNode.class, size);
    }

    public GraphNode<?> getNodeFromStationId (short stationId) {
        for (GraphNode<?> node : nodes) {
            if (node.getStationId() == stationId) return node;
        }
        return null;
    }

}
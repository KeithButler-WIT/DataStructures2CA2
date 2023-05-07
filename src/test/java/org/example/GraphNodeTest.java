package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GraphNodeTest {
    AdjacencyMatrix am;
    GraphNode<?> node1, node2;

    @BeforeEach
    void setUp() {
        am = new AdjacencyMatrix(2);
        node1 = new GraphNode<>((short) 101,50010,50010,"Station 1", am);
        node2 = new GraphNode<>((short) 102,50020,50020,"Station 2", am);
    }

    @AfterEach
    void tearDown() {
        am=null;
        node1=node2=null;
    }

    @Test
    void connectToNodeDirected() {
        assertEquals(0, am.amat[node1.nodeId][node2.nodeId]);
        assertEquals(0, am.amat[node2.nodeId][node1.nodeId]);
        node1.connectToNodeDirected(node2, 10);
        assertEquals(10, am.amat[node1.nodeId][node2.nodeId]);
        assertEquals(0, am.amat[node2.nodeId][node1.nodeId]);
    }

    @Test
    void connectToNodeUndirected() {
        assertEquals(0, am.amat[node1.nodeId][node2.nodeId]);
        assertEquals(0, am.amat[node2.nodeId][node1.nodeId]);
        node1.connectToNodeUndirected(node2, 10);
        assertEquals(10, am.amat[node1.nodeId][node2.nodeId]);
        assertEquals(10, am.amat[node2.nodeId][node1.nodeId]);
    }

    @Test
    void setNodeValue() {
        assertEquals(0, am.amat[node1.nodeId][node1.nodeId]);
        node1.setNodeValue(100);
        assertEquals(100, am.amat[node1.nodeId][node1.nodeId]);
    }

    @Test
    void getNodeValue() {
        assertEquals(0, node1.getNodeValue());
        node1.setNodeValue(100);
        assertEquals(100, node1.getNodeValue());
    }
}
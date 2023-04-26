package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdjacencyMatrixTest {
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
    void getNodeFromStationId() {
        assertEquals(node1, am.getNodeFromStationName("Station 1"));
        assertEquals(node2, am.getNodeFromStationName("Station 2"));
    }

    @Test
    void getNodeFromStationName() {
        assertEquals(node1, am.getNodeFromStationId((short) 101));
        assertEquals(node2, am.getNodeFromStationId((short) 102));
    }

    @Test
    void getNodeValue() {
    }
}
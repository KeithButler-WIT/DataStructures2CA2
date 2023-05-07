package org.example;

public class GraphNode<T> {
    public int nodeValue=Integer.MAX_VALUE;
    public AdjacencyMatrix mat;
    public int nodeId;
    private short stationId;
    private double latitude;
    private double longitude;
    private String stationName;
//    private short zone;

    public GraphNode(short stationId, double latitude, double longitude, String stationName, AdjacencyMatrix mat){
        this.stationId=stationId;
        this.latitude=latitude;
        this.longitude=longitude;
        this.stationName=stationName;
        this.mat=mat;
        mat.nodes[nodeId=mat.nodeCount++]=this; //Add this node to adjacency matrix and record id
    }

    public void connectToNodeDirected(GraphNode<?> destNode, int cost) {
        mat.amat[nodeId][destNode.nodeId]=cost;
    }

    public void connectToNodeUndirected(GraphNode<?> destNode, int cost) {
        mat.amat[nodeId][destNode.nodeId]=mat.amat[destNode.nodeId][nodeId]=cost;
//        mat.amat[destNode.nodeId][nodeId]=mat.amat[nodeId][destNode.nodeId]=cost;
    }

    public void setNodeValue(int value) {
        mat.amat[nodeId][nodeId]=value;
    }

    public int getNodeValue() {
        return mat.amat[nodeId][nodeId];
    }

    public void setMat(AdjacencyMatrix mat) {
        this.mat = mat;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public double getLatitude() {
        return latitude;
    }

    public short getStationId() {
        return stationId;
    }

    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public void setStationId(short stationId) {
        this.stationId = stationId;
    }

    @Override
    public String toString() {
        return "Station [stationId=" + stationId + ", stationName=" + getStationName()
                + ", latitude=" + getLatitude() + ", longitude=" + getLongitude() + "]";
    }

}
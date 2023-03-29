package org.example;

public class GraphNode<T> {
    public T data;
    public AdjacencyMatrix mat;
    public int nodeId;
    private short stationId;
    private double latitude;
    private double longitude;
    private String stationName;
    public int nodeValue = Integer.MAX_VALUE;
    public GraphNode(String stationName,AdjacencyMatrix mat){
        this.stationName=stationName;
        this.mat=mat;
        mat.nodes[nodeId=mat.nodeCount++]=this; //Add this node to adjacency matrix and record id
    }

    public GraphNode(short stationId, double latitude, double longitude, String stationName, AdjacencyMatrix mat){
        this.stationId=stationId;
        this.latitude=latitude;
        this.longitude=longitude;
        this.stationName=stationName;
        this.mat=mat;
        mat.nodes[nodeId=mat.nodeCount++]=this; //Add this node to adjacency matrix and record id
    }

    public void connectToNodeDirected(GraphNode<T> destNode) {
        mat.amat[nodeId][destNode.nodeId]=true;
    }

    public void connectToNodeUndirected(GraphNode<?> destNode) {
        mat.amat[nodeId][destNode.nodeId]=mat.amat[destNode.nodeId][nodeId]=true;
    }

    public void setData(T data) {
        this.data = data;
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
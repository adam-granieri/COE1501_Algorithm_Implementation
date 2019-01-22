//Adam Granieri
//COE 1501 Algorithm Implementation
//Fall 2018

import java.util.LinkedList;

public class Vertex {
    //Data needed for each vertex as adjacency List
    private LinkedList<Edge> connectedEdges;
    private int ID;

    //Constructor
    public Vertex(int id) {
        connectedEdges = new LinkedList<Edge>();
        ID = id;
    }

    //Functions
    public void addEdge(Edge e) {
        connectedEdges.add(e);
    }

    //Setters
    public void setEdges(LinkedList<Edge> connections) {
        connectedEdges = connections;
    }

    public void setID(int id) {
        ID = id;
    }

    //Getters
    public LinkedList<Edge> getConnectedEdges() {
        return connectedEdges;
    }

    public int getID() {
        return ID;
    }
}
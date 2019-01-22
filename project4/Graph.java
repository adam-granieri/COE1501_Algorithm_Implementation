//Adam Granieri
//COE 1501 Algorithm Implementation
//Fall 2018

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

public class Graph {
    //Data for graph structure in adjacency List
    public int vertexCount;
    private Vertex[] verticies;
    private ArrayList<Edge> edges;
    private FlowNetwork dataFlowNetwork;
    private String fileName;
    private EdgeWeightedDigraph weightedGraph = null;

    //Constructor
    public Graph(String file) {
        fileName = file;
        file2graph(file);
    }

    //Functions for required Calculations
    public int lowestLatencyPath(int firstID, int secondID) {
        Vertex start = verticies[firstID], end = verticies[secondID];
        //Create a Path object to call
        Path solution = shortestPathFrom2Points(start, end, Integer.toString(start.getID()), 0.0, -1);
        if (solution == null) {
            return 0;
        }

        //Do a Little String manipulation to make look clearer
        String output = "";
        for (int i = 0; i < solution.getPath().length(); i++) {
            if (i == solution.getPath().length()-1) {
                output += solution.getPath().charAt(i);
            } else {
                output += solution.getPath().charAt(i) + " --> ";
            }
        }

        //Display path and path data
        System.out.println("\nPATH: " + output);
        System.out.println("PATH LATENCY: " + solution.getLength() + " seconds");
        return solution.getBandwidth();
    }

    public boolean cooperConnected() {
        boolean isCopperOnlyConnected = true, isCopper;
        LinkedList<Edge> vEdges;

        //iterating through all vertecies
        for (int i = 0; i < vertexCount; i++) {
            vEdges = verticies[i].getConnectedEdges();
            isCopper = false;
            //Iterate through edges
            for (Edge curr : vEdges) {
                //Check if wire is copper
                if (curr.getCableType().equals("copper")) {
                    isCopper = true;
                    break;
                }
            }

            if (!isCopper) {
                isCopperOnlyConnected = false;
                break;
            }
        }
        
        return isCopperOnlyConnected;
    }

    public int maxDataPath(int sourceID, int sinkID) {
        makeFlowNetwork();
        FordFulkerson ff = new FordFulkerson(dataFlowNetwork, sourceID, sinkID);
        return (int) ff.value();
    }

    public void connectedVertexFailure() {
        // Make edge-weighted graph from text
        if (weightedGraph == null) {
            makeEdgeWeightedGraph();
        }

        //do dfs starting from 0
        int[] counts = new int[weightedGraph.V()];
        DepthFirstSearch check = new DepthFirstSearch(weightedGraph, 0);

        //Iterate through graph to see where we visited
        for (int i = 0; i < weightedGraph.V(); i++) {
            if (check.marked(i)) {
                for (DirectedEdge edge : weightedGraph.adj(i)) {
                    counts[i]++;;
                }
            }
        }

        //Do failure check for connectivity
        int vCounter = 0;
        for (int j = 0; j < weightedGraph.V(); j++) {
            if (counts[j] <= 2) {
                vCounter++;
            }
        }

        //Final printout
        if (vCounter >= 2) {
            System.out.println("Graph will not be connected if any 2 verticies fail.");
        } else {
            System.out.println("Graph will still be connected if any 2 verticies fail.");
        }
    }

    //private helper methods
    private void file2graph(String file) {
        //check if file exists
        try {
            BufferedReader s = new BufferedReader(new FileReader(file));

            // get count as first input and initialize vertex list
            vertexCount = Integer.parseInt(s.readLine());
            verticies = new Vertex[vertexCount];
            for (int i = 0; i < vertexCount; i++) {
                verticies[i] = new Vertex(i);
            }

            //NetworkFlow initialization
            dataFlowNetwork = new FlowNetwork(vertexCount);

            //Start reading in Edge List
            edges = new ArrayList<Edge>();
            String temp;
            while ((temp = s.readLine()) != null) {
                String[] parseArr = temp.split(" ");

                //Create new edges from file
                Edge newDuplexEdge1 = new Edge(verticies[Integer.parseInt(parseArr[0])], 
                                        verticies[Integer.parseInt(parseArr[1])], 
                                        parseArr[2], Integer.parseInt(parseArr[3]),
                                        Integer.parseInt(parseArr[4]));
                Edge newDuplexEdge2 = new Edge(verticies[Integer.parseInt(parseArr[1])], 
                                        verticies[Integer.parseInt(parseArr[0])], 
                                        parseArr[2], Integer.parseInt(parseArr[3]),
                                        Integer.parseInt(parseArr[4]));
                FlowEdge newFlowEdge1 = new FlowEdge(Integer.parseInt(parseArr[0]), 
                                        Integer.parseInt(parseArr[1]),
                                        Double.parseDouble(parseArr[3]));
                FlowEdge newFlowEdge2 = new FlowEdge(Integer.parseInt(parseArr[1]), 
                                        Integer.parseInt(parseArr[0]),
                                        Double.parseDouble(parseArr[3]));
                
                //Add new edges to adjacency List
                verticies[Integer.parseInt(parseArr[0])].addEdge(newDuplexEdge1);
                verticies[Integer.parseInt(parseArr[1])].addEdge(newDuplexEdge2);
                edges.add(newDuplexEdge1);
                dataFlowNetwork.addEdge(newFlowEdge1);
                dataFlowNetwork.addEdge(newFlowEdge2);

            }
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    private Path shortestPathFrom2Points(Vertex start, Vertex dest, String route, double length, int minBand) {
        //check for bad input or if we reached our spot
        if (start == null || dest == null || route == null || length < 0.0) {
            return null;
        } else if (start.getID() == dest.getID()) {
            return new Path(route, length, minBand);
        }

        //Now we gotta look around
        LinkedList<Edge> currentEdges = start.getConnectedEdges();
        double currentMinLength = -10.0; //Default value to know we haven't looked at anything
        String currentMinPath = "";
        for (Edge curr : currentEdges) { //Since BFS look at all edges
            Vertex end = curr.getEndVertex();
            //Check if we have already visited the vertex at the end
            if (route.contains(Integer.toString(end.getID()))) {
                continue; //Got to next vertex
            }

            String continuedPath = route + Integer.toString(end.getID());
            double continuedLength = length + curr.getTravelTime();
            int tempBandWidth = minBand;
            //Check for lower bandwidth
            if (curr.getBandwidth() < minBand || minBand <= 0 ) {
                tempBandWidth = curr.getBandwidth();
            }

            //Check next path
            Path otherPath = shortestPathFrom2Points(end, dest, continuedPath, continuedLength, tempBandWidth);
            if (otherPath == null) {
                continue;
            } else if (currentMinLength < 0 || otherPath.getLength() < currentMinLength || (otherPath.getBandwidth() > minBand && otherPath.getLength() == currentMinLength)) {
                //set current values to other path because it's better
                currentMinLength = otherPath.getLength();
                currentMinPath = otherPath.getPath();
                minBand = otherPath.getBandwidth();
            }
        }

        if (currentMinLength > -1.0) {
            return new Path(currentMinPath, currentMinLength, minBand);
        } else {
            return null;
        }
    }

    private void makeFlowNetwork() {
        //check if file exists
        try {
            BufferedReader s = new BufferedReader(new FileReader(fileName));

            //NetworkFlow initialization
            dataFlowNetwork = new FlowNetwork(vertexCount);
            s.readLine();

            String temp;
            while ((temp = s.readLine()) != null) {
                String[] parseArr = temp.split(" ");

                //Create new edges from file
                FlowEdge newFlowEdge1 = new FlowEdge(Integer.parseInt(parseArr[0]), 
                                        Integer.parseInt(parseArr[1]),
                                        Double.parseDouble(parseArr[3]));
                FlowEdge newFlowEdge2 = new FlowEdge(Integer.parseInt(parseArr[1]), 
                                        Integer.parseInt(parseArr[0]),
                                        Double.parseDouble(parseArr[3]));
                
                //Add new edges to adjacency List
                dataFlowNetwork.addEdge(newFlowEdge1);
                dataFlowNetwork.addEdge(newFlowEdge2);

            }
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    private void makeEdgeWeightedGraph() {
        //check if file exists
        try {
            BufferedReader s = new BufferedReader(new FileReader(fileName));

            //NetworkFlow initialization
            weightedGraph = new EdgeWeightedDigraph(vertexCount);
            s.readLine();

            String temp;
            while ((temp = s.readLine()) != null) {
                String[] parseArr = temp.split(" ");

                //Create new edges from file
                DirectedEdge newEdge1 = new DirectedEdge(Integer.parseInt(parseArr[0]), 
                                        Integer.parseInt(parseArr[1]),
                                        Double.parseDouble(parseArr[3]));
                DirectedEdge newEdge2 = new DirectedEdge(Integer.parseInt(parseArr[1]), 
                                        Integer.parseInt(parseArr[0]),
                                        Double.parseDouble(parseArr[3]));
                
                //Add new edges to adjacency List
                weightedGraph.addEdge(newEdge1);
                weightedGraph.addEdge(newEdge2);

            }
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

}
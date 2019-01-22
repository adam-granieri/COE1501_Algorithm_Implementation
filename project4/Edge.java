//Adam Granieri
//COE 1501 Algorithm Implementation
//Fall 2018

public class Edge implements Comparable<Edge> {
    //Data for every edge in graph
    private Vertex start;
    private Vertex end;
    private String cableType;
    private int bandwidth;
    private int cableLength;
    private double travelTime; // In Seconds

    //constants from requirements
    private final int COPPER = 230000000;
    private final int OPTICAL = 200000000;

    //Constructor
    public Edge(Vertex s, Vertex e, String t, int b, int l) {
        start = s;
        end = e;
        cableType = t;
        bandwidth = b;
        cableLength = l;

        //Calculate travelTime based on material
        if (cableType.equals("optical")) {
            travelTime = ((double) cableLength)/((double) OPTICAL);
        } else {
            travelTime = ((double) cableLength)/((double) COPPER);
        }
    }

    //Setters
    public void setStartVertex(Vertex v) {
        start = v;
    }

    public void setEndVertex(Vertex v) {
        end = v;
    }

    public void setCableType(String s) {
        if (s.equals("optical") || s.equals("copper")) {
            cableType = s;
        } else {
            return;
        }
    }

    public void setBandwidth(int b) {
        bandwidth = b;
    }

    public void setCableLength(int l) {
        cableLength = l;
    }

    public void setTravelTime(double t) {
        travelTime = t;
    }

    //Getters
    public Vertex getStartVertex() {
        return start;
    }

    public Vertex getEndVertex() {
        return end;
    }

    public String getCableType() {
        return cableType;
    }

    public int getBandwidth() {
        return bandwidth;
    }

    public int getCableLength() {
        return cableLength;
    }

    public double getTravelTime() {
        return travelTime;
    }

    //Comparto for interface
    public int compareTo(Edge other) {
        if (travelTime == other.getTravelTime()) {
            return 0;
        } else if (travelTime > other.getTravelTime()) {
            return 1;
        } else {
            return -1;
        }
    }
}
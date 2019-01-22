//Adam Granieri
//COE 1501 Algorithm Implementation
//Fall 2018

public class Path {
    //Data for Path object
    public String path;
    public double length;
    public int bandwidth;

    //constructors
    public Path() {
        this("", 0.0, 0);
    }

    public Path(String pathStr, double lengthD, int bandW) {
        path = pathStr;
        length = lengthD;
        bandwidth = bandW;
    }

    //getters
    public String getPath() {
        return path;
    }

    public double getLength() {
        return length;
    }

    public int getBandwidth() {
        return bandwidth;
    }

    //Setters
    public void setPath(String p) {
        path = p;
    }

    public void setLength(double l) {
        length = l;
    }

    public void setBandwidth(int b) {
        bandwidth = b;
    }
}
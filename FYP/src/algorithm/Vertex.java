package algorithm;

public class Vertex implements Comparable<Vertex> {
    public String name;
    public Integer distance;
    public Vertex prev;
    public Boolean visited;
    public double danger;
    public Vertex( String name, Integer distance, double danger, Vertex prev) {
        this.name = name;
        this.distance = distance;
        this.prev = prev;
        visited = false;
    }
    @Override
    public int compareTo(Vertex other) {
    	Double val = distance+danger;
        return val.compareTo(other.danger+other.distance);
    }
    
    public Vertex copy() {
        return new Vertex( name, distance, danger, prev);
    }
}
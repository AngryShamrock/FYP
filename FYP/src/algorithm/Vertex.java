package algorithm;

public class Vertex implements Comparable<Vertex> {
    public String name;
    public Integer distance;
    public Vertex prev;
    public Boolean visited;
    public Vertex( String name, Integer distance, Vertex prev) {
        this.name = name;
        this.distance = distance;
        this.prev = prev;
        visited = false;
    }
    @Override
    public int compareTo(Vertex other) {
        return distance.compareTo(other.distance);
    }
    
    public Vertex copy() {
        return new Vertex( name, distance, prev);
    }
}
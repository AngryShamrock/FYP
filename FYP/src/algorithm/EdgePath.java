package algorithm;

import java.util.ArrayList;
import java.util.List;

public class EdgePath implements Comparable<EdgePath> {
    private  int startTime;
    private Model model;
    
    private int cost;
    
    private List<Edge> edges;
    public EdgePath() {
        edges = new ArrayList<Edge>();
    }
    @Override
    public int compareTo(EdgePath other) {
        if (cost < other.cost){
            return -1;
        } else if (cost == other.cost) {
            return 0;
        } else {
            return 1;
        }
    }
    
    public EdgePath copy(){
        EdgePath clone = new EdgePath();
        clone.edges.addAll(edges);
        clone.cost = cost;
        return clone;
    }
    
    public void addEdge( Edge edge ) {
        edges.add(edge);
        cost += edge.cost;
    }
    
    public Edge tail() {
        return edges.get(0);
    }
    
}
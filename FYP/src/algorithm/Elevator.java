package algorithm;

import java.util.ArrayList;
import java.util.List;

public class Elevator {
    
    public List<Edge> edges;
    
    public int capacity;
    public String id;
    public Elevator( String id) {
        this.id = id;
        edges = new ArrayList<Edge>();
    }

}

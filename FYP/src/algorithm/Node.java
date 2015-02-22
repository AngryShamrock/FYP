package algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class Node {
    //Occupants?
    //Capacity?
    //SignalDirection?
    //DANGER? 
    /**
     * Which Edge do I send my occupants onto?
     * 0=wait
     */
    public String id;
    public int arrivals;
    public String direction = null;
    public int capacity;
    public int occupants;
    public Map<String, Edge> edges;
    /**
     * 
     * @param capacity
     * @param occupants
     */
    public Node(String id){
        this.id = id;
        edges = new HashMap<String, Edge>();
    }
}

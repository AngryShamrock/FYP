package algorithm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class Node{
    //Occupants?
    //Capacity?
    //SignalDirection?
    //DANGER? 
    /**
     * Which Edge do I send my occupants onto?
     * 0=wait
     */
    public String id;
    public String elevator = null;
    public Integer arrivals = 0;
    public String direction = null;
    public int capacity;
    public int occupants;
    public Integer costToGoal = 0;
    public Map<String, Edge> edges;
    public int t;
    /**
     * 
     * @param capacity
     * @param occupants
     */
    public Node(String id){
        this.id = id;
        edges = new HashMap<String, Edge>();
    }
    
    public static class NodeCostComparator implements Comparator<Node> {

		@Override
		public int compare(Node a, Node b) {
			return a.costToGoal.compareTo(b.costToGoal);
		}
    	
    }
    
    public static class NodeArrivalsComparator implements Comparator<Node> {

		@Override
		public int compare(Node a, Node b) {
			return a.arrivals.compareTo(b.arrivals);
		}
    	
    }
}

package algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Elevator implements Comparable<Elevator> {
    private Model model;
    public List<Edge> edges;
    public List<String> nodes;
    public Integer priority = 0;
    public int capacity;
    public String id;
    public Map<Integer, String> schedule;
    public int occupants;
    private int lookAhead = 100;
    public String location;
    public Elevator(Model model, String id) {
        this.id = id;
        edges = new ArrayList<Edge>();
        schedule = new HashMap<Integer, String>();
        nodes = new ArrayList<String>();
        this.model = model;
    }

    public String locationAtTime( int t ) {
        return schedule.get(t);
    }
    
    public void setSchedule ( int t, String id ){
        schedule.put(t, id);
    }
    
    public String getSchedule (int t) {
    	if ( schedule.containsKey(t)) {
    		return schedule.get(t);
    	} else {
    		return null;
    	}
    }

	@Override
	public int compareTo(Elevator o) {
		return priority.compareTo(o.priority);
	}
	
	
	
	/**
	 * updateAvailability
	 * @param t
	 * @returns Nodes containing new arrivals for processing
	 */
	public List<Node> updateAvailability(int t){
		List<Node> list = new ArrayList<Node>();
		for (int i = t-1; i>0 ; i--){
			for (String node : nodes){
				for (Edge edge : model.getGraphAtTime(i).get(node).edges.values()) {
					if (edge.end.elevator != null) {
						edge.blocked = true;
					} else {
						edge.blocked = false;
					}
					
				}
			}
		}
		//check conditions
		if (occupants > 6){
			list.add(dropOff(t));
		}
		
		for (int i=0; i< lookAhead; i++){
			for (String node : nodes){
				for (Edge edge : model.getGraphAtTime(i+t).get(node).edges.values()) {
					if (model.getGraphAtTime(t).get(location).edges.get(node).cost < i) {
							edge.blocked = false;
						} else {
							edge.blocked = true;
						}
					}
				}
			}

		//set available
		

		return list;
	}
	
	public Node dropOff(int t){
		
		Node node = model.getGraphAtTime(t).get(location).edges.get(bestDropOff(t)).end;
		location = node.id;
		System.out.println("Dropped off " + occupants + " people");
		String lobby = null;
		for (Edge edge : node.edges.values()){
			if (edge.end.elevator == null){
				System.out.println("opening! at " + t);
				edge.blocked=false; //open edges to rest of building
				lobby = edge.end.id;
			}
		}
		node =  model.getGraphAtTime(t).get(lobby);
		node.arrivals = occupants;
		occupants = 0;
		
		
		return node;
	}
	
	private String bestDropOff(int t){
    	int minDist = Integer.MAX_VALUE;
    	String dropOff = null;
    	
    	for (String node : nodes){
    		t = t + model.genGraph(t).get(location).edges.get(node).cost;
    		List<Vertex> path = model.findLeastCostPathToGoal( t, node, true, false);
    		int pathCost = path.get(path.size()-1).distance;
    		if (pathCost < minDist){
    			minDist = pathCost;
    			dropOff = node;
    		}
    	}
    	
    	return dropOff;
    }
}

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
    public int latestTime = 0;
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
					if (!node.equals(location)) {
						edge.blocked = true;
					}
					
					if (edge.end.elevator != null) {
						
					} else {
						if (node.equals(location)){
							edge.blocked = false;
						}
						
					}
				}
				
			}
		}
		
		//check conditions
		if (occupants >= capacity){
			Node node = dropOff(t);
			t = node.t;
			list.add(node);
			list.addAll(updateAvailability(t));
		} else {
			
			Edge self = model.getGraphAtTime(t).get(location).edges.get(location);
			self.signal = true;
			self.predictedOccupancy = occupants;
			
			for (int i=0; i< lookAhead; i++){
				for (String node : nodes ) {
					for (Edge edge : model.getGraphAtTime(i+t).get(node).edges.values()) {
						if (model.getGraphAtTime(t).get(location).edges.get(node).cost <= i || (edge.end.id.equals(location) && edge.start.id.equals(location))) {
							edge.blocked = false;
						} else {
							edge.blocked = true;
						}
					}
				}
			}
		}
		latestTime = t;
		
		

		//set available
		

		return list;
	}
	
	public Node forceDropOff() {
		Node node = dropOff(latestTime);
		updateAvailability(latestTime);
		return node;
	}
	
	public Node dropOff(int t){
		
		
		Edge route = model.getGraphAtTime(t).get(location).edges.get(bestDropOff(t));
		Node destination = route.end;
		route.signal = true;
		route.inFlow = occupants;
		for (int i = 0; i< route.cost; i++){
			model.getGraphAtTime(t+i).get(location).edges.get(destination.id).predictedOccupancy = occupants;
		}
		
		location = destination.id;
		System.out.println("Dropped off " + occupants + " people");
		String lobby = null;
		for (Edge edge : destination.edges.values()){
			if (edge.end.elevator == null){
				System.out.println("opening! at " + (t+route.cost));
				edge.blocked=false; //open edges to rest of building
				edge.signal=true;
				lobby = edge.end.id;
			}
		}
		
		for (Edge edge : model.getGraphAtTime(t+route.cost).get(lobby).edges.values()){
			if ( edge.end.elevator != null){
				edge.blocked=true;
			}
		}
		latestTime = t+route.cost;
		//put people off elevators
		destination =  model.getGraphAtTime(t+route.cost).get(lobby);
		destination.arrivals = occupants;
		occupants = 0;
		
		
		return destination;
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

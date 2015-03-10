package algorithm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;

public class SimplePlanner{

    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        SimplePlanner planner = new SimplePlanner();
        planner.execute();
    }
    
    public Set<String> goals;
    
    
    public SimplePlanner() throws IOException{
       goals = new HashSet<String>();
    }
    
    
    public void execute() throws IOException {
        Model model = new Model( "UnityResources/EvacPlanner/g.txt" );
        goals.add("E");
        goals.add("F");
        /**
         * MODIFICATION
         */
        
        int t = 0;
        /*
        while (t < targetLookahead){
            //While node has no direction
                //Set directions at each node in path
                //Set predicted occupancy of each node in path
             planPathsArbitary( model, t);
             t++;
        }
        */
        //model.elevators.values();
        //scheduleElevatorHardCode(model, t, model.elevators.get("X"));
        //planPathsArbitary(model, t);
        scheduleElevatorsSmart(model, t, model.elevators.values());
        
        printGraph( model.getGraphAtTime(12));
        model.export("UnityResources/EvacPlanner/escapeRoute.txt");
    }
    
    public void scheduleElevatorsDumb (Model model, int t, Elevator elevator) {
    	//Go to top, pick people up
    }
    
    public void scheduleElevatorHardCode( Model model, int t, Elevator elevator){
    	
    	String location = elevator.getSchedule(t);
    	//Move lift to most occupied floor (t+cost)
    	
    	if (location != null && !location.equals("BUSY")) {
    		int cost = model.getGraphAtTime(t).get(location).edges.get("X1").cost;
    		for (int i = t; i < t+cost; i++){
    			elevator.setSchedule(i, "BUSY");
    		}
    		elevator.setSchedule(t+cost, "X1");
    		t=t+cost;
    		location = "X1";
    		//wait for 6 steps
    		for (int i = 0; i <= 8; i++) {
    			model.getGraphAtTime(t+i).get(location).edges.get("X1").full = false; //Open doors
    		}
    		model.getGraphAtTime(t+8).get(location).edges.get("X0").full = false;
    	}
    	
    	//Load lift until full (t+loadtime)
    	//Move lift to nearest goal (t+cost)
    	//repeat
    	
    }
    
    public void scheduleElevatorsSmart( Model model, int t, Collection<Elevator> elevators) {
    	//plan paths
    	//FURTHEST FIRST
    	planPathsArbitary( model, t);
    	
    	PriorityQueue<Elevator> elevatorQueue = new PriorityQueue<Elevator>();
    	for (Elevator elevator : elevators){
    		elevator.priority = 0;
    		elevatorQueue.add(elevator);
    	}
    	
    	while (!elevatorQueue.isEmpty()) {
    		
    		Elevator elevator = elevatorQueue.remove();
    		System.out.println("step: t=" + elevator.priority + " and elevator '"
    		+ elevator.id + "' at '" +elevator.getSchedule(elevator.priority) +"'" );
    		
    		
    		processElevator( model, elevator.priority, elevator, elevatorQueue);
    		model.resetSignals();
    		planPathsArbitary( model, t);
    	}
    	//
    }
    
    private void processElevator( Model model, int t, Elevator elevator, PriorityQueue<Elevator> queue ){
    	//get cost to each stop
		//Grab 'capacity' people, adding exitTimes
    	
    	
    	
		String location = elevator.getSchedule(t);
		Edge route = null;
		int maxExitSum = 0;
		int capacity = 20;
		for (Edge edge : model.getGraphAtTime(t).get(location).edges.values()){
			if (edge.elevator){
				System.out.println("Evaluating edge: " + edge.start.id + "->" + edge.end.id);
				int sum = getExitSum( model, t+ edge.cost, capacity, edge.end.id);
				System.out.println( sum);
				if (sum > maxExitSum) {
					maxExitSum = sum;
					route = edge;
				}
			}
		}
		if (route == null) {
			return;
		}
		elevator.setSchedule(t+route.cost, route.end.id);
		//Wait for 5 units
		int wait = 10;
		model.getGraphAtTime(t).get(location).edges.get(route.end.id).full=false;
		System.out.println("opening edge " + location + "->" + route.end.id + " @ " + (t+route.cost)  );
		
		
		// move lift
		
		String dropOff = bestDropOff(model, t+route.cost+wait+1, route.end.id, elevator.nodes);
		/*
		for (int i = 0; i < wait ; i++){
			System.out.println("opening edge " + route.end.id + "->" + route.end.id + " @ " + (t+route.cost+i)  );
			model.getGraphAtTime(t+route.cost+i).get(route.end.id).edges.get(route.end.id).full=false;
		}
		*/
		int cost = t+route.cost+wait+1;
		Edge dropOffEdge = model.getGraphAtTime(cost).get(route.end.id).edges.get(dropOff);
		
		
		
		
		for (int i = 0; i< wait; i++){
			model.getGraphAtTime(cost-i).get(route.end.id).edges.get(route.end.id).full=false;
		}
		dropOffEdge.full=false;
		
		
		System.out.println("opening edge:" + route.end.id + "->" + dropOff + " @ " + cost);
		cost += dropOffEdge.cost+2;
		elevator.priority = cost;
		elevator.setSchedule(cost, dropOff);
		if (cost < 50) {
			queue.add(elevator);
		}
		
		
		System.out.println(elevator.schedule.keySet());
    }
    
    private int getExitSum( Model model, int t, int people, String node){
    	int currentPeople = 0;
    	int exitSum = 0;
    	int cost = 0;
    	System.out.println(t);
    	ArrayList<Edge> edgeList = new ArrayList<Edge>();
    	edgeList.addAll(model.getGraphAtTime(t).get(node).edges.values());
    	int loops = 0;
    	int maxloops = 50;
    	while (edgeList.size()>0 && currentPeople < people && loops < maxloops && exitSum == 0){
    		
    		loops++;
    		//System.out.println("Finding people " + currentPeople);
    		Edge edge = edgeList.remove(0);
    		if (isGoal(edge.end.id)) {
    			return 0;
    		}
    		if (!edge.elevator){
    			currentPeople +=edge.inFlow;
        		exitSum += (edge.exitTime*edge.predictedOccupancy);
        		edgeList.addAll(edge.end.edges.values());
        		cost += edge.cost;
        		
    		}
    		
    	}
    	return exitSum;
    }
    
    private String bestPickUp( Model model, int t, String source, List<String> nodes){
    	int people;
    	int maxExitTime = 0;
    	int distance;
    	int capacity = model.getGraphAtTime(t).get(source).edges.get(source).flowRate;
    	for (String node : nodes ) {
    		int exitTime = 0;
    		//get exitSum for node.neighbours
    		distance = 0;
    		Edge outgoing = model.getGraphAtTime(t).get(source).edges.get(node);
    		distance += outgoing.cost;
    		int i = 0;
    		while (exitTime ==0 && i<50 ) {
    			
    			i++;
    		}
    	}
		return null;
    }
    
    private String bestDropOff( Model model, int t, String source, List<String> nodes){
    	int minDist = Integer.MAX_VALUE;
    	String dropOff = null;
    	
    	for (String node : nodes){
    		t = t + model.genGraph(t).get(source).edges.get(node).cost;
    		List<Vertex> path = findLeastCostPathToGoal( model, t, node);
    		int pathCost = path.get(path.size()-1).distance;
    		if (pathCost < minDist){
    			minDist = pathCost;
    			dropOff = node;
    		}
    	}
    	
    	return dropOff;
    }
    
    
    //TODO create additional MODEL to store decisions
    public void planPathsArbitary( Model model, int t) {
        for (Node node : model.getGraphAtTime(t).values()) { /** SHOULD USE HUERISTIC FOR SELECTING NODE **/
            if (!isGoal(node.id)){
                while (node.arrivals>0) {
                    int groupSize = 1;
                    List<Vertex> path = findLeastCostPathToGoal(  model, t, node.id);
                    //System.out.println("FINDING DIR FOR:" + node.id + " WITH " + groupSize + "PEOPLE");
                    int pathCost = path.get(path.size()-1).distance;
                    for (Vertex vert : path){
                        if (vert.prev != null){
                            Node tmp = model.getGraphAtTime(t+vert.prev.distance).get(vert.prev.name);
                            Edge outgoingEdge = tmp.edges.get(vert.name);
                            outgoingEdge.signal=true;
                            outgoingEdge.exitTime = pathCost;
                            if (groupSize > outgoingEdge.flowRate){
                                groupSize = outgoingEdge.flowRate;
                            }
                            outgoingEdge.inFlow += groupSize;
                            //System.out.println("Putting " + groupSize + "people on " + outgoingEdge.start.id + "->" + outgoingEdge.end.id);
                            for ( int i = 0; i < outgoingEdge.cost; i++){
                            	model.getGraphAtTime(t+vert.prev.distance+i).
                            	get(outgoingEdge.start.id).edges.get(outgoingEdge.end.id).
                            	predictedOccupancy += groupSize;
                            }
                            if (outgoingEdge.inFlow >= outgoingEdge.flowRate){
                                outgoingEdge.full = true;
                            }
                            
                        }
                    }
                    node.arrivals -= groupSize;
                }
                
            } else {
                node.edges.get(node.id).signal= true;
            }
        }
    }

    
    private List<Vertex> findLeastCostPathToGoal( Model model, int t, String source) {
        //Initialise shortest path for every vertex
        Map<String, Map<Integer, Vertex>> verts = new HashMap<String, Map<Integer, Vertex>>();
        int offset = 0;
        PriorityQueue<Vertex> queue = new PriorityQueue<Vertex>();
        
        for (String key : model.getGraphAtTime(t).keySet()) {
            verts.put(key, new HashMap<Integer, Vertex>()); 
        }
        
        queue.add( new Vertex(source, 0, 0, null));
        // main loop
        Vertex goal = null;
        while (!queue.isEmpty()){
            Vertex u = queue.poll();
            if (isGoal(u.name)) {
                goal = u;
                break;
            } else {
                for (Edge edge : model.getGraphAtTime(t + u.distance).get(u.name).edges.values()){
                	if (!edge.full){
                		u.visited= true;
                        //System.out.println("Adding: " + edge.end.id + " at " + (u.distance + edge.cost));
                        Integer length = u.distance + edge.cost;
                        if (!verts.get(edge.end.id).containsKey(length)) {
                            //Have not met vert yet, add it to the map
                            Vertex newVert = new Vertex(edge.end.id, length, u.danger+edge.danger, u );
                            
                            verts.get(edge.end.id).put(length, newVert);
                            queue.add( newVert );
                            
                            //System.out.println("Adding: " + edge.end.id + " at " + (u.distance + edge.cost));
                        }
                	}
                    
                }
            }    
        }
        Vertex current = goal;
        Stack<Vertex> pathStack = new Stack<Vertex>();
        pathStack.push(current);
        if (current == null) {
        	System.err.println("NO PATH FOUND");
        	return new ArrayList<Vertex>();
        	
        }
        while (current.prev != null ) { 
            pathStack.push(current.prev);
            current = current.prev;
            
        }
        List<Vertex> path = new ArrayList<Vertex>();
        while (!pathStack.isEmpty()) {
            path.add(pathStack.pop());
            
        }
        return path;
    }

    private boolean isGoal(String u) {
        return goals.contains(u);
    }


    private static void printGraph( Map<String, Node> graph ) {
        Iterator<Node> iter = graph.values().iterator();
        
        while (iter.hasNext()){
            Node node = iter.next();
            System.out.println( node.id + ": " + " @" + node.arrivals);
            Iterator<Edge> jter = node.edges.values().iterator();
            
            while (jter.hasNext()) {
                Edge edge = jter.next();
                
                System.out.println(edge.start.id + " -> " + edge.end.id
                        + " ( " + edge.cost + ", " + edge.flowRate + " )" + "#" +
                		edge.predictedOccupancy + ((edge.signal) ? "<>" : "") +
                		((edge.full) ? "><" : "")  );
            }
        }
    }
}
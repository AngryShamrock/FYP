package algorithm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;

public class CombinedPlanner{

    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        CombinedPlanner planner = new CombinedPlanner();
        planner.execute();
    }
    
    public Set<String> goals;
    
    
    public CombinedPlanner() throws IOException{
    }
    
    
    public void execute() throws IOException {
        Model model = new Model( "UnityResources/EvacPlanner/g.txt" );
        goals = model.goals;
        goals.add("E");
        goals.add("F");
        /**
         * MODIFICATION
         */
        
        int t = 0;
        //model.elevators.values();
        planPathsFurthestFirst(model, t);
        //scheduleElevatorsSmart(model, t, model.elevators.values());
        
        printGraph( model.getGraphAtTime(12));
        model.export("UnityResources/EvacPlanner/ComboPlan.txt");
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
    
    
    
    
    //TODO create additional MODEL to store decisions
    public void planPathsFurthestFirst( Model model, int t) {
    	//Sort nodes in descending order according to cost
    	PriorityQueue<Node> queue = new PriorityQueue<Node>( 10, Collections.reverseOrder(new Node.NodeCostComparator())); 
    	for (Node node : model.getGraphAtTime(t).values()) {
    		List<Vertex> path = model.findLeastCostPathToGoal( t, node.id, false, true);
    		node.costToGoal = path.get(path.size()-1).distance;
    		queue.add(node);
    	}
    	
    	//make elevators available
    	for (Elevator elevator : model.elevators.values()){
    		elevator.updateAvailability(t);
    	}
    	
        while (!queue.isEmpty()) {
        	Node node = queue.remove();
        	t = node.t;
            if (!isGoal(node.id)){
                while (node.arrivals>0) {
                    int groupSize = 1;
                   // System.out.println("Finding paths for " + node.id + " at " + t);
                    List<Vertex> path = model.findLeastCostPathToGoal(t, node.id, false, false);
                    //System.out.println("FINDING DIR FOR:" + node.id + " WITH " + groupSize + "PEOPLE");
                    int pathCost = path.get(path.size()-1).distance;
                    for (Vertex vert : path){
                    	
                    	//if vert is elevator lobby
                    		//is the lift free?
                    		//Would using it make our journey shorter?
                    			//Add us to the lift
                    			//Fix the lifts position here at time(t)
                    	
                        if (vert.prev != null){
                            Node tmp = model.getGraphAtTime(t+vert.prev.distance).get(vert.prev.name);
                            
                            if (tmp.elevator != null ){
                            	System.out.println("1 taking elevator: " + tmp.elevator + " from: " + tmp.id);
                            	//Lift is free, because this edge was available
                            	//Would make our journey shorter, because it's part of the leastCostPath
                            	
                            	Elevator elevator = model.elevators.get(tmp.elevator);
                            	elevator.occupants += groupSize;
                            	elevator.location = tmp.id;
                            	
                            	List<Node> newNodes = elevator.updateAvailability(t+vert.prev.distance);
                            	if (newNodes.size()>0){
                            		System.out.println("drop off at " + newNodes.get(0).id + " " + newNodes.get(0).t + "with " + newNodes.get(0).arrivals + " people");
                            	}
                            	queue.addAll( newNodes);
                            	break;
                            }
                            
                            Edge outgoingEdge = tmp.edges.get(vert.name);
                            
                            outgoingEdge.signal=true;
                            outgoingEdge.exitTime = pathCost;
                            outgoingEdge.inFlow += groupSize;
                            
                            //UPDATE OCCUPANCY
                            for ( int i = 0; i < outgoingEdge.cost; i++){
                            	model.getGraphAtTime(t+vert.prev.distance+i).
                            	get(outgoingEdge.start.id).edges.get(outgoingEdge.end.id).
                            	predictedOccupancy += groupSize;
                            }
                            
                            //IF FULL
                            if (outgoingEdge.inFlow >= outgoingEdge.flowRate){
                                outgoingEdge.blocked = true;
                            }
                            
                        }
                    }
                    node.arrivals -= groupSize;
                }
                
            } else {
                node.edges.get(node.id).signal= true;
            }
            //IF queue is empty, clear remaining elevators
            if (queue.isEmpty()){
            	for (Elevator elevator : model.elevators.values()){
            		if (elevator.occupants>0){
            			elevator.dropOff(t);
            		}
            	}
            }
        }
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
                		((edge.blocked) ? "><" : "")  );
            }
        }
    }
}
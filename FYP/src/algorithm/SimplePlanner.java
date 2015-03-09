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
        Model model = new Model( "g.txt" );
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
        scheduleElevatorMostOccupied(model, t, model.elevators.get("X"));
        planPathsArbitary(model, t);
        
        printGraph( model.getGraphAtTime(0));
        model.export("escapeRoute.txt");
    }
    
    public void scheduleElevatorMostOccupied( Model model, int t, Elevator elevator){
    	
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
    		//wait for 4 steps
    		model.getGraphAtTime(t+4).get(location).edges.get("X0").full = false;
    	}
    	
    	//Load lift until full (t+loadtime)
    	//Move lift to nearest goal (t+cost)
    	//repeat
    	
    }
    
    
    //TODO create additional MODEL to store decisions
    public void planPathsArbitary( Model model, int t) {
        for (Node node : model.getGraphAtTime(t).values()) { /** SHOULD USE HUERISTIC FOR SELECTING NODE **/
            if (!isGoal(node.id)){
                while (node.arrivals>0) {
                    int groupSize = 1;
                    List<Vertex> path = findLeastCostPathToGoal(  model, t, node.id);
                    //System.out.println("FINDING DIR FOR:" + node.id + " WITH " + groupSize + "PEOPLE");
                    for (Vertex vert : path){
                        if (vert.prev != null){
                            Node tmp = model.getGraphAtTime(t+vert.prev.distance).get(vert.prev.name);
                            Edge outgoingEdge = tmp.edges.get(vert.name);
                            outgoingEdge.signal=true;
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
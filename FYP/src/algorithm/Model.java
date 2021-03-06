package algorithm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Stack;
import java.util.Map.Entry;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;


public class Model {

    private Map<String, Node> baseGraph;

    //The distance the 'nodes' and 'edges' lists have looked ahead
    private int graphLookAhead = -1;
    
    public List<Map<String, Node>> plan;
    
    public Map<String, Elevator> elevators;
    
    
    public List<Map<String, Map<String, Integer>>> dangerModel;
    
    public Set<String> goals;
    
    
    public Model( String path ) throws IOException{ 
    	dangerModel = new ArrayList<Map<String,Map<String, Integer>>>();
        elevators = new HashMap<String, Elevator>();
        goals = new HashSet<String>();
        baseGraph = loadScenarioFile( path );
        plan = new ArrayList<Map<String, Node>>();
        
    }
    
    public void resetSignals() {
    	List<Map<String, Node>> oldPlan = plan;
    	plan = new ArrayList<Map<String, Node>>();
    	int oldLookAhead = graphLookAhead;
    	graphLookAhead = -1;
    	copyOldElevators( oldPlan, oldLookAhead );
    }
    
    private void copyOldElevators(List<Map<String, Node>>  oldPlan, int oldLookAhead ) {
    	for (int i = 0; i <= oldLookAhead; i++) {
    		for (Elevator elevator : elevators.values()) {
        		for (String node : elevator.nodes) {
        			for (Edge edge : getGraphAtTime(i).get(node).edges.values()) {
        				edge.blocked = oldPlan.get(i).get(edge.start.id).edges.get(edge.end.id).blocked;

        			}
        		}
        	}
    		getGraphAtTime(i);
    	}
    }
    
    public Map<String, Node> genGraph(int t){
        
        Map<String, Node> graph = new HashMap<String, Node>();
        Iterator<Entry<String,Node>> iter = baseGraph.entrySet().iterator();
        // Copy nodes
        while (iter.hasNext()){
            Entry<String,Node> ent = iter.next();
            Node node = new Node( ent.getKey());
            if (t == 0 ) {
            	node.arrivals = ent.getValue().arrivals;
            }
            node.elevator = ent.getValue().elevator;
            node.t=t;
            graph.put(ent.getKey(), node);
        }
        
        iter = graph.entrySet().iterator();
        //For each node,
        while (iter.hasNext()){
            Entry<String,Node> ent = iter.next();
            Iterator<Entry<String, Edge>> edgeIter = baseGraph.get(ent.getKey()).edges.entrySet().iterator();
            //Copy each edge
            while (edgeIter.hasNext()){
                Entry<String, Edge> edgeEnt = edgeIter.next();
                Edge newEdge = new Edge( ent.getValue(),
                        graph.get(edgeEnt.getKey()), edgeEnt.getValue().cost, edgeEnt.getValue().flowRate);
                
                newEdge.elevator = edgeEnt.getValue().elevator;
                        
                ent.getValue().edges.put(edgeEnt.getKey(), newEdge );
                
            }
        }
        //Remove elevator edges
        for (Elevator elevator : elevators.values()){
            for (String nodeLabel : elevator.nodes){
            	Node node = graph.get(nodeLabel);
            	for (Edge edge : node.edges.values()){
            		if (elevator.nodes.contains(edge.end.id)){
            			edge.blocked=true;
            			edge.elevator= true;
            			//System.out.println("forbidding " + edge.start.id + "->" + edge.end.id);
            		}
            		
            		//graph.get(edge.end.id).edges.get(nodeLabel).full=true;
            	}
            }
        }
        
        return graph;
    }
    
    public Map<String, Node> getGraphAtTime( int t ) {
        
        if ( t > graphLookAhead) {
            //GenGraph until t
            for (int i = graphLookAhead+1; i <= t; i++){
                Map<String, Node> graph = genGraph(t);
                /*
                if (i>0){
                    for ( Node node : graph.values()){
                        for ( Edge edge : node.edges.values()){
                            edge.predictedOccupancy = plan.get(i-1).get(node.id).edges.get(edge.end.id).predictedOccupancy;
                            //edge.predictedOccupancy = 5;
                        }
                    }
                }
                */
                plan.add( graph );
            }
            graphLookAhead = t;
        }
        return plan.get(t);
      
    }
    
    
    public List<Vertex> findLeastCostPathToGoal(int t, String source, boolean allowBlocked, boolean blockLifts) {
        //Initialise shortest path for every vertex
        Map<String, Map<Integer, Vertex>> verts = new HashMap<String, Map<Integer, Vertex>>();
        int offset = 0;
        PriorityQueue<Vertex> queue = new PriorityQueue<Vertex>();
        
        for (String key : getGraphAtTime(t).keySet()) {
            verts.put(key, new HashMap<Integer, Vertex>()); 
        }
        
        queue.add( new Vertex(source, 0, 0, null));
        // main loop
        Vertex goal = null;
        while (!queue.isEmpty()){
            Vertex u = queue.poll();
            if (goals.contains(u.name)) {
                goal = u;
                break;
            } else {
                for (Edge edge : getGraphAtTime(t + u.distance).get(u.name).edges.values()){
                	if ((!edge.blocked || allowBlocked) && (!edge.elevator || !blockLifts )){
                		u.visited= true;
                        //System.out.println("Adding: " + edge.end.id + " at " + (u.distance + edge.cost));
                        Integer length = u.distance + edge.cost;
                        if (!verts.get(edge.end.id).containsKey(length)) {
                            //Have not met vert yet, add it to the map
                        	
                            Vertex newVert = new Vertex(edge.end.id, length, u.danger+edge.danger, u );
                            
                            verts.get(edge.end.id).put(length, newVert);
                            queue.add( newVert );
                            
                            //System.out.println("Adding: " + edge.end.id + " at " + (u.distance + edge.cost));
                        } else if (edge.end.id.equals(edge.start.id)) {
                        	queue.remove(verts.get(edge.end.id).get(length));
                        	//if it is there, remove it and re add it as a waiting edge
                        	Vertex newVert = new Vertex(edge.end.id, length, u.danger+edge.danger, u );
                            
                            verts.get(edge.end.id).put(length, newVert);
                            queue.add( newVert );
                        	
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
    
    
    //GetGraphAtTimeT
    
    //GetEdgesAtTimeT
    //Edges[Time] edges
    
    //GetDangeratTimeT
    
    //TODO EXPORT
    
    private Map<String, Node> loadScenarioFile( String path ) throws IOException{
        Map<String, Node> scenario = new HashMap<String, Node>();
        try {
            System.out.println(new File("").getAbsolutePath());
            BufferedReader in = new BufferedReader(new FileReader( new File(path)));
            
            
            //Read file
            
            //JSON
            JSONObject json = (JSONObject) JSONValue.parse(in);
            //JSON
            //Create Nodes
            for (  Object jNodeTmp : (JSONArray) json.get("nodes")){
            	JSONObject jNode = (JSONObject) jNodeTmp;
            	//Create node
            	Node node = new Node(jNode.get("id").toString());
            	
            	node.arrivals = (int) (long) jNode.get("arrivals");
            	scenario.put(node.id, node);
            	if ((boolean) jNode.get("isGoal")){
            		goals.add(node.id);
            	}
            }
            	//Foreach node
            //Create Edges
            for ( Object jNodeTmp : (JSONArray) json.get("nodes")){
            	JSONObject jNode = (JSONObject) jNodeTmp;
            	for (Object jEdgeTmp : (JSONArray) jNode.get("edges")){
            		JSONObject jEdge = (JSONObject) jEdgeTmp;
            		//create edge
            		Node start = scenario.get(jEdge.get("start").toString());
            		Node end = scenario.get(jEdge.get("end").toString());
            		int cost = (int) (long) jEdge.get("cost");
            		int flowRate = (int) (long) jEdge.get("flowRate");
            		Edge edge = new Edge(start, end, cost, flowRate);
            		start.edges.put(end.id, edge);
            	}
            	//Create node
            }
            //Create Elevators
            
            for ( Object jElevatorTmp : (JSONArray) json.get("elevators")){
            	JSONObject jElevator = (JSONObject) jElevatorTmp;
            	//Create node
            	String id = jElevator.get("id").toString();
            	Elevator elevator = new Elevator(this, id);
            	elevator.setSchedule(0, jElevator.get("initialLocation").toString());
            	elevator.location = jElevator.get("initialLocation").toString();
            	
            	for ( Object jNodeTmp : (JSONArray) jElevator.get("nodes")){
            		JSONObject jNode = (JSONObject) jNodeTmp;
            		String nodeId = jNode.get("id").toString();
            		elevator.nodes.add(nodeId);
            		scenario.get(nodeId).elevator = id;
            	}
            	elevator.capacity = scenario.get(elevator.nodes.get(0)).edges.get(elevator.nodes.get(0)).flowRate;
            	elevators.put(elevator.id, elevator);
            }
            in.close();
        } catch ( Exception e) {
            throw new IOException(e);
        }
        
        return scenario;
    }
    
    //EXPORT
    @SuppressWarnings("unchecked")
	public void export(String fileName){
    	//For each node
    			//Look at Edges
    			//If edge has occupants/signal
    				//create element for edge
    	JSONArray jsonPlan = new JSONArray();
    	int t = 0;
    	for (Map<String, Node> graph : plan){
    		JSONObject step = new JSONObject();
    		
    		step.put("t", t++);
    		JSONArray signals = new JSONArray();
    		for ( Node node : graph.values()){
    			
    			for ( Edge edge : node.edges.values() ) {
    				
    				if (edge.signal || edge.predictedOccupancy>0) {
    					JSONObject jsonEdge = new JSONObject();
        				jsonEdge.put("start",  edge.start.id);
    					jsonEdge.put("end", edge.end.id);
    					jsonEdge.put("signal", edge.signal);
    					jsonEdge.put("inFlow", edge.inFlow);
    					jsonEdge.put("predictedOccupancy", edge.predictedOccupancy);
    					signals.add(jsonEdge);
    				}
    			}
    			step.put("signals", signals);
    		}
    		if (signals.size()>0){
    			jsonPlan.add( step );
    		}
    		
    	}
    	try {
			FileWriter writer = new FileWriter(new File(fileName));
			writer.write(jsonPlan.toString());
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	System.out.println(jsonPlan);
    	
    }
}

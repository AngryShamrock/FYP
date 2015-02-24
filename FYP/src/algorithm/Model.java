package algorithm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class Model {

    private Map<String, Node> baseGraph;

    //The distance the 'nodes' and 'edges' lists have looked ahead
    private int graphLookAhead = -1;
    
    public List<Map<String, Node>> plan;
    
    public Map<String, Elevator> elevators;
    
    
    public Model( String path ) throws IOException{ 
        elevators = new HashMap<String, Elevator>();
        baseGraph = loadScenarioFile( path );
       plan = new ArrayList<Map<String, Node>>();
    }
    
    public Model ( Map<String, Node> baseGraph, Map<String, Elevator> elevators ){
    	this.elevators = elevators;
        this.baseGraph = baseGraph;
       plan = new ArrayList<Map<String, Node>>();
    }
    
    public Map<String, Node> genGraph(){
        
        Map<String, Node> graph = new HashMap<String, Node>();
        Iterator<Entry<String,Node>> iter = baseGraph.entrySet().iterator();
        // Copy nodes
        while (iter.hasNext()){
            Entry<String,Node> ent = iter.next();
            Node node = new Node( ent.getKey());
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
                ent.getValue().edges.put(edgeEnt.getKey(), new Edge( ent.getValue(),
                        graph.get(edgeEnt.getKey()), edgeEnt.getValue().cost, edgeEnt.getValue().flowRate));
            }
        }
        //Remove elevator edges
        for (Elevator elevator : elevators.values()){
            for (Edge edge : elevator.edges) {
                //System.out.println("removing edge: " + edge.start.id);
                graph.get(edge.start.id).edges.remove(edge.end.id);
            }
        }
        
        return graph;
    }
    
    public Map<String, Node> getGraphAtTime( int t ) {
        
        if ( t > graphLookAhead) {
            //GenGraph until t
            for (int i = graphLookAhead+1; i <= t; i++){
                Map<String, Node> graph = genGraph();
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
    //GetGraphAtTimeT
    
    //GetEdgesAtTimeT
    //Edges[Time] edges
    
    //GetDangeratTimeT
    
    //TODO EXPORT
    
    private Map<String, Node> loadScenarioFile( String path ) throws IOException{
        Map<String, Node> scenario = new HashMap<String, Node>();
        int lineNo = 0;
        try {
            System.out.println(new File("").getAbsolutePath());
            BufferedReader in = new BufferedReader(new FileReader( new File(path)));
            
            
            //Read file
            //Throw away first two lines
            lineNo++;
            in.readLine();
            lineNo++;
            in.readLine();
            //Create Nodes
            for (String line = in.readLine(); line != null; line = in.readLine()){
                lineNo++;
                if (line.equals(":Edges")) {
                    break;
                }
                String[] parts = line.split(", ");
               scenario.put(parts[0], new Node(parts[0]));
            }
            
            for (String line = in.readLine(); line != null; line = in.readLine()){
                lineNo++;
                if (line.equals(":Elevators")) {
                    break;
                }
                String[] parts = line.split(", ");
                Node startNode = scenario.get(parts[0]);
                Node endNode = scenario.get(parts[1]);
                startNode.edges.put(parts[1], new Edge( startNode, endNode,
                        Integer.parseInt(parts[2]), Integer.parseInt(parts[3]))); 
            }
            //Read :Edges
            //Create Edges & add to Nodes
            
            // read :Elevators
            
            Elevator elevator = null;
            for (String line = in.readLine(); line != null; line = in.readLine()){
                lineNo++;
                String[] parts = line.split(", ");
                if (line.charAt(0) == '-'){
                    elevator = new Elevator(parts[0]);
                    elevators.put(elevator.id, elevator);
                } else {
                    elevator.edges.add(scenario.get(parts[0]).edges.get(parts[1]));
                }
            }
            
            // Read new Elevator
            // Read edges
            in.close();
        } catch ( Exception e) {
            System.err.println("Line Number: " + lineNo);
            throw new IOException(e);
        }
        return scenario;
    }
}

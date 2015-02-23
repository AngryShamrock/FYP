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
        int targetLookahead = 100;
        Model model = new Model( "testGraph.txt" );
        goals.add("E");
        goals.add("F");
        
        /**
         * MODIFICATION
         */
        
        int t = 0;
        
        t++;
        model.getGraphAtTime(t).get("A").arrivals=1;
        //Put people at node A and D
        model.getGraphAtTime(t).get("D").arrivals=1;
        while (t < targetLookahead){
            //While node has no direction
                //Set directions at each node in path
                //Set predicted occupancy of each node in path
             for (Node node : model.getGraphAtTime(t).values()) {
                 if (!isGoal(node.id)){
                     while (node.arrivals>0) {
                         int groupSize = node.arrivals;
                         List<Vertex> path = findLeastCostPathToGoal(  model, t, node.id);
                         System.out.println("FINDING DIR FOR:" + node.id);
                         for (Vertex vert : path){
                             if (vert.prev != null){
                                 Node tmp = model.getGraphAtTime(t+vert.prev.distance).get(vert.prev.name);
                                 Edge outgoingEdge = tmp.edges.get(vert.name);
                                 outgoingEdge.active=true;
                                 if (groupSize > outgoingEdge.flowRate){
                                     groupSize = outgoingEdge.flowRate;
                                 }
                                 outgoingEdge.predictedOccupancy += groupSize;
                                 if (outgoingEdge.predictedOccupancy >= outgoingEdge.flowRate){
                                     tmp.edges.remove(outgoingEdge.end.id);
                                 }
                                 tmp.arrivals -= groupSize;
                             }
                         }
                     }
                     
                 } else {
                     node.edges.get(node.id).active= true;
                 }
                 /*
                 if (node.direction != null){
                     node.processArrivals();
                     for (Edge edge : node.edges.values()){
                         Node end = model.getGraphAtTime(t+edge.cost).get(edge.end.id);
                         end.arrivals += edge.predictedOccupancy;
                         if (end.arrivals>0){
                             System.out.println(end.arrivals + " people scheduled for " + t + "->" + (t+edge.cost) + " to " + end.id);
                         } 
                     }
                 }
                 */
                 //node.direction.occupancy += (flowRate/arrivals)
                 //node.self.occupancy += (arrivals-flowRate)
             }
             t++;
        }
         //####TESTING DJISTRKAS
        /*
        //model.getGraphAtTime(t + 19).get("C").edges.remove("G");
        //model.getGraphAtTime(t + 20).get("C").edges.remove("G");
        //model.getGraphAtTime(t + 21).get("C").edges.remove("G");
        //model.getGraphAtTime(t + 22).get("C").edges.remove("G");
        
        List<Vertex> path = findLeastCostPathToGoal( model, t, "B");
        for (Vertex vert : path){
            System.out.println(vert.name + ", distance: + " + vert.distance);
        }
        */
        printGraph( model.getGraphAtTime(1));
    }

    
    private List<Vertex> findLeastCostPathToGoal( Model model, int t, String source) {
        //Initialise shortest path for every vertex
        Map<String, Map<Integer, Vertex>> verts = new HashMap<String, Map<Integer, Vertex>>();
        int offset = 0;
        PriorityQueue<Vertex> queue = new PriorityQueue<Vertex>();
        
        
        for (String key : model.getGraphAtTime(t).keySet()) {
            verts.put(key, new HashMap<Integer, Vertex>()); 
        }
        
        queue.add( new Vertex(source, 0, null));
        // main loop
        Vertex goal = null;
        while (!queue.isEmpty()){
            Vertex u = queue.poll();
            if (isGoal(u.name)) {
                goal = u;
                break;
            } else {
                for (Edge edge : model.getGraphAtTime(t + u.distance).get(u.name).edges.values()){
                    u.visited= true;
                    //System.out.println("Adding: " + edge.end.id + " at " + (u.distance + edge.cost));
                    Integer length = u.distance + edge.cost;
                    if (!verts.get(edge.end.id).containsKey(length)) {
                        //Have not met vert yet, add it to the map
                        Vertex newVert = new Vertex(edge.end.id, length, u );
                        
                        verts.get(edge.end.id).put(length, newVert);
                        queue.add( newVert );
                        
                        //System.out.println("Adding: " + edge.end.id + " at " + (u.distance + edge.cost));
                    }
                }
                    /*
                    if (! verts.get(edge.end.id).containsKey(u.distance + edge.cost)) {
                        //Have not met vert yet, add it to the map
                    } else {
                        //Have met vert before, check if this path is shorter
                        
                        Vertex v = verts.get(edge.end.id).get(key);
                        
                        if (!v.visited){
                            int altDist = u.distance + edge.cost; //+ danger
                            if ( altDist < v.distance){
                                v.distance = altDist;
                                v.prev = u;
                            }
                            queue.add(v);
                           // System.out.println("Edge: " + v.name + ", " + v.distance + ", " + v.prev)
                        }
                     }
                 }
                 */
            }    
        }
        Vertex current = goal;
        Stack<Vertex> pathStack = new Stack<Vertex>();
        pathStack.push(current);
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
                        + " ( " + edge.cost + ", " + edge.flowRate + " )" + "#" + edge.predictedOccupancy + ((edge.active) ? "<>" : "")  );
            }
        }
    }
}
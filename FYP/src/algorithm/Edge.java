package algorithm;


public class Edge {
    
    
    
    public Node end;
    public Node start;
    public int cost;
    public int inFlow;
    public boolean active;
    public boolean full = false;
    public int flowRate;
    public int predictedOccupancy;
    public int danger;
    
    public Edge(Node start, Node end, int cost, int flowRate){
        this.start=start;
        this.end=end;
        this.cost=cost;
        this.flowRate=flowRate;
        inFlow = 0;
    }
    //Capacity
    
    //Cost

    //DANGER
}

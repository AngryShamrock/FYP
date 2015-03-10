package algorithm;


public class Edge {
    
    
    
    public Node end;
    public Node start;
    public int cost;
    public int inFlow;
    public boolean signal;
    public boolean full = false;
    public boolean elevator;
    public int flowRate;
    public int predictedOccupancy;
    public int danger;
    public int exitTime;
    
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

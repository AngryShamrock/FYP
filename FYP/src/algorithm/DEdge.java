package algorithm;

public class DEdge {
    public final Vertex target;
    public final double weight;
    public DEdge(Vertex argTarget, double argWeight)
    {
        target = argTarget;
        weight = argWeight;
    }
}
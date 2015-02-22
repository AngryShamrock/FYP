import java.util.ArrayList;


public class Floor {

    
    private final int LEVEL;
    private ArrayList<Person> lobbyOccupants;
    private ArrayList<Person> occupants;
    private ArrayList<EstimatedArrival> arrivals;
    
    public Floor( int level) {
        this.LEVEL = level;
        occupants = new ArrayList<Person>();
        arrivals = new ArrayList<EstimatedArrival>();
    }

    
    public String toString() {
        return "floor" + LEVEL;
    }
    
    public void printLevel() {
        System.out.print("floor" + LEVEL +": " + occupants.size() + " occupants" );
    }
    
    public int getLevel(){
        return LEVEL;
    }
    
    public ArrayList<Person> getOccupants(){
        return occupants;
    }
}

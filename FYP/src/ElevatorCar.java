import java.util.ArrayList;


public class ElevatorCar {
    private Floor location;
    private final int CAPACITY;
    private ArrayList<Person> occupants;
    //private double speed;
    private int transitCost;
    private int accelerationCost;
    private int doorCost;
    private int loadCost;
    private boolean doorsOpen = false;
    
    
    public ElevatorCar ( Floor location, int capacity, int transitCost, int accelerationCost, int doorCost, int loadCost) {
        this.location=location;
        this.CAPACITY=capacity;
        this.transitCost=transitCost;
        this.accelerationCost=accelerationCost;
        this.doorCost=doorCost;
        this.loadCost=loadCost;
        
        occupants = new ArrayList<Person>();
    }
    
    public int moveToFloor( Floor targetLevel ){
        int cost = accelerationCost*2 + (Math.abs(targetLevel.getLevel()-location.getLevel())*transitCost);
        location = targetLevel;
        return cost;
    }
    
    public int setDoorsOpen( boolean open){
        int cost = 0;
        if (doorsOpen != open){
            cost += doorCost;
            doorsOpen = open;
        }
        
        return cost;
    }
    
    public int loadPassenger( Person passenger){
        int cost = 0;
        occupants.add( passenger );
        cost = loadCost;
        return cost;
    }
    
    public int unloadPassenger( Person passenger ) {
        int cost = 0;
        location.getOccupants().add( passenger);
        cost = loadCost;
        return cost;
    }
    
    public ArrayList<Person> getOccupants(){
        return occupants;
    }

    /**
     * @return the cAPACITY
     */
    public int getCapacity() {
        return CAPACITY;
    }
    
    public Floor getFloor(){
        return location;
    }
}

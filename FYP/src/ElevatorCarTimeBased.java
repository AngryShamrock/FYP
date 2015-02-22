import java.util.ArrayList;


public class ElevatorCarTimeBased {
    private Floor location;
    private Floor targetLocation;
    private final int CAPACITY;
    private ArrayList<Person> occupants;
    //private double speed;
    private int loadRate;
    private int transitCost;
    private int accelerationCost;
    private boolean inTransit;
    private int transitTimer;
    
    public ElevatorCarTimeBased ( Floor location, int capacity, int loadRate, int transitCost, int accelerationCost) {
        this.location=location;
        this.CAPACITY=capacity;
        this.transitCost=transitCost;
        this.accelerationCost=accelerationCost;
        this.loadRate = loadRate;
        occupants = new ArrayList<Person>();
    }
    
    public void update( int currentTime) {
        if (transitTimer <=0){
            if (inTransit){
                System.out.println("Arrived at " + currentTime);
            }
            inTransit = false;
            transitTimer = 0;
        }
        transitTimer--;
    }
    
    public void moveToFloor( Floor targetLevel){
        int arrivalTime = accelerationCost*2 + (Math.abs(targetLevel.getLevel()-location.getLevel())*transitCost);
        transitTimer = arrivalTime;
        location = targetLevel;
        inTransit = true;
    }
    
    public void unloadPassengers( ArrayList<Person> passengers ){
        for (int i=0; i < loadRate && occupants.size()>0; i++){
            passengers.add(occupants.remove(0));
        }
    }
    
    public void loadPassengers( ArrayList<Person> passengers ) {
        for (int i=0; i < loadRate && passengers.size()>0; i++){
            
            if (occupants.size() < CAPACITY) {
                occupants.add(passengers.remove(0));
            }
            
        }
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

    public boolean isInTransit() {
        return inTransit;
    }
}

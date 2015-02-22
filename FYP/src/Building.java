import java.util.ArrayList;


public class Building {
    private final ArrayList<Floor> floors;
    
    
    public Building( int numberOfFloors ){
        
        floors = new ArrayList<Floor>();
        floors.add(new Floor(0)); // lobby floor
        for (int i = 1; i < numberOfFloors; i++){
            floors.add(new Floor( i ));
        }
    }
    
    public ArrayList<Floor> getFloors(){
        return floors;
    }
    
    public int getTotalOccupants(){
        int occupants = 0;
        for (int i =0; i<floors.size(); i++){
            occupants +=floors.get(i).getOccupants().size();
        }
        
        return occupants;
    }
}

import java.util.ArrayList;


public class TimeBasedSim {
    private Building building;
    private int currentTime = 0;
    private double timeCoefficient;
    public TimeBasedSim(Building building, double timeCoefficient) {
        this.building = building;
        this.timeCoefficient = timeCoefficient;
    }
    
    public void execute(){
        double cost = 0;
        
        for (int i = 0; i< building.getFloors().size(); i++){
            for (int j = 0; j <7; j++) {
                building.getFloors().get(i).getOccupants().add(new Person());
            }
        }
        ElevatorCarTimeBased elevator = new ElevatorCarTimeBased( building.getFloors().get(0), 12, 3, 12, 4);
        while (building.getTotalOccupants() + elevator.getOccupants().size()>0){
            elevator.update(currentTime);
            moveElevatorTopDown( elevator);
            
            cost +=evacuatePersons();
            currentTime++;
            /*
            if (currentTime % 100 == 0 ) {
                System.out.println("{ Time = " + currentTime  );
                System.out.println ( "  current Score : " + cost );
                System.out.println("}");
            }
            */
        }
        System.out.println("Score: " + cost/1000 );
        System.out.println("Total time to evacuate: " + currentTime );
    }
    
    public void moveElevatorTopDown( ElevatorCarTimeBased elevator){
        //If in transit, do nothing
        if (!elevator.isInTransit()){
            //if need passengers
            if ((elevator.getOccupants().size() < elevator.getCapacity()
                    && elevator.getFloor() != building.getFloors().get(0)) || elevator.getOccupants().size()==0){
                int targetLevel = building.getFloors().size()-1;
                while ( building.getFloors().get(targetLevel).getOccupants().isEmpty() && targetLevel >= 0){
                    targetLevel--;
                    if (targetLevel == 0) {
                        break;
                    }
                }
              //Go to the highest floor with people on it && load up
                if (elevator.getFloor() == building.getFloors().get(targetLevel)){
                    elevator.loadPassengers(building.getFloors().get(targetLevel).getOccupants());
                    System.out.println("Collecting. Current occupants: " + elevator.getOccupants().size());
                } else {
                    System.out.println("Moving to floor: " + targetLevel);
                    elevator.moveToFloor(building.getFloors().get(targetLevel));
                }
            } else {
                if (elevator.getFloor() == building.getFloors().get(0)){
                    elevator.unloadPassengers(building.getFloors().get(0).getOccupants());
                    System.out.println("Dropping");
                } else {
                    System.out.println("Moving to bottom floor with " + elevator.getOccupants().size() + " passengers ");
                    elevator.moveToFloor(building.getFloors().get(0));
                }
            }
        }
    }
    
    public void moveElevatorBottomUp ( ElevatorCarTimeBased elevator){
        //If in transit, do nothing
        if (!elevator.isInTransit()){
            //if need passengers
            if ((elevator.getOccupants().size() < elevator.getCapacity()
                    && elevator.getFloor() != building.getFloors().get(0)) || elevator.getOccupants().size()==0){
                int targetLevel = 1;
                while (targetLevel < building.getFloors().size()  && building.getFloors().get(targetLevel).getOccupants().isEmpty()){
                    targetLevel++;
                }
                if (targetLevel >= building.getFloors().size() ){
                    targetLevel=0;
                }
              //Go to the highest floor with people on it && load up
                if (elevator.getFloor() == building.getFloors().get(targetLevel)){
                    elevator.loadPassengers(building.getFloors().get(targetLevel).getOccupants());
                    System.out.println("Collecting. Current occupants: " + elevator.getOccupants().size());
                } else {
                    System.out.println("Moving to floor: " + targetLevel);
                    elevator.moveToFloor(building.getFloors().get(targetLevel));
                }
            } else {
                if (elevator.getFloor() == building.getFloors().get(0)){
                    elevator.unloadPassengers(building.getFloors().get(0).getOccupants());
                    System.out.println("Dropping");
                } else {
                    System.out.println("Moving to bottom floor with " + elevator.getOccupants().size() + " passengers ");
                    elevator.moveToFloor(building.getFloors().get(0));
                }
            }
        }
    }
    
    public double evacuatePersons(){
        double cost = 0;
        ArrayList<Person> persons = building.getFloors().get(0).getOccupants();
        cost += persons.size()+currentTime*timeCoefficient;
        if (persons.size()>0){
            System.out.println("Evacuating " + persons.size() + " people with a score of " + cost);
        }
        persons.clear();
        
        return cost;
    }
    
    

    public static void main(String[] args) {
        TimeBasedSim sim = new TimeBasedSim( new Building( 10 ), 0.5 );
        sim.execute();
        
    }

}

import java.util.ArrayList;


public class sim {

    public sim() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
       int totalCost = 0;
       Building building = new Building( 10 );
        for (int i = 1; i< building.getFloors().size(); i++){
            for (int j = 0; j <60; j++) {
                building.getFloors().get(i).getOccupants().add(new Person());
            }
        }
        int currentFloor = building.getFloors().size()-1;
        //int currentFloor = 1;
        
        ElevatorCar elevator = new ElevatorCar( building.getFloors().get(0), 8, 5, 2, 2, 1);
        while (currentFloor>0 && building.getFloors().get(0).getOccupants().size() < building.getTotalOccupants()){
            int costPerFloor = 0;
            while ( building.getFloors().get(currentFloor).getOccupants().size() > 0 ) {
                costPerFloor += elevator.setDoorsOpen( false );
                costPerFloor +=elevator.moveToFloor(building.getFloors().get(currentFloor));
                costPerFloor += elevator.setDoorsOpen(true);
                while (elevator.getOccupants().size() < elevator.getCapacity() && elevator.getFloor().getOccupants().size() > 0){
                    costPerFloor += elevator.loadPassenger( elevator.getFloor().getOccupants().remove(0));
                }
                costPerFloor += elevator.setDoorsOpen( false );
                costPerFloor += elevator.moveToFloor( building.getFloors().get(0) );
                costPerFloor += elevator.setDoorsOpen( true );
                while (elevator.getOccupants().size() > 0 ) {
                    costPerFloor += elevator.unloadPassenger( elevator.getOccupants().remove(0));
                }
            }
            totalCost += costPerFloor;
            
            System.out.println("Cost to evacuate floor " + currentFloor + ": " + costPerFloor);
            System.out.println("Current total: " + totalCost);
            currentFloor--;
        }
        
        System.out.println("Total cost, top to bottom: " + totalCost);
        System.out.println("Number of people on top floor: " +
                building.getFloors().get(building.getFloors().size()-1).getOccupants().size());
        System.out.println("Number of people on bottom floor: " +
                building.getFloors().get(0).getOccupants().size());
        System.out.println("Number of people in building: " + building.getTotalOccupants());

    }

}

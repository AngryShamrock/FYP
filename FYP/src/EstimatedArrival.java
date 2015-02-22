import java.util.ArrayList;
import java.util.List;


public class EstimatedArrival {
    private int arrivalTime;
    private List<Person> group;
    
    public EstimatedArrival( int arrivalTime, List<Person> group){
            this.arrivalTime = arrivalTime;
            this.group = group;
    }
    
    public EstimatedArrival( int arrivalTime, int numPeople){
        List<Person> people = new ArrayList<Person>();
        
        for (int i = 0; i< numPeople; i++){
            people.add(new Person());
        }
        
        this.arrivalTime = arrivalTime;
        this.group = people;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public List<Person> getGroup() {
        return group;
    }

    public void setGroup(List<Person> group) {
        this.group = group;
    }
}

package algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Elevator {
    
    public List<Edge> edges;
    public List<String> nodes;
    
    public int capacity;
    public String id;
    private Map<Integer, String> schedule;
    public Elevator( String id) {
        this.id = id;
        edges = new ArrayList<Edge>();
        schedule = new HashMap<Integer, String>();
        nodes = new ArrayList<String>();
    }

    public String locationAtTime( int t ) {
        return schedule.get(t);
    }
    
    public void setSchedule ( int t, String id ){
        schedule.put(t, id);
    }
    
    public String getSchedule (int t) {
    	if ( schedule.containsKey(t)) {
    		return schedule.get(t);
    	} else {
    		return null;
    	}
    }
}

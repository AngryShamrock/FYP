using UnityEngine;
using System.Collections;
using System.Collections.Generic;

public class EdgeScript : MonoBehaviour {
	public GameObject start;
	public GameObject end;
	public int cost;
	public int flowRate;
	public bool active;
	public int occupants;
	public Material offMat;
	public Material activeMat;
	public Dictionary<int, int> occupantsSchedule;
	public Dictionary<int, bool> signalSchedule;
	private int lookahead = 0;

	private PlanViewer viewer;



	LineRenderer line;
	// Use this for initialization
	void Start () {
		 viewer = GameObject.FindGameObjectWithTag ("GameController").
			GetComponent<PlanViewer>();
		occupantsSchedule = new Dictionary<int, int> ();
		signalSchedule = new Dictionary<int, bool> ();
		 line = GetComponent<LineRenderer> ();
		line.SetVertexCount (2);

		line.SetPosition (0, start.transform.position);
		line.SetPosition (1, end.transform.position);
	}
	
	// Update is called once per frame
	void Update () {
		if (start.Equals (end)){
			transform.parent.gameObject.GetComponent<NodeScript>().active=active;
	
		}
		while (lookahead<= viewer.t) {
			if (!occupantsSchedule.ContainsKey (lookahead)){
				occupantsSchedule[lookahead] = 0;
			}
			if (!signalSchedule.ContainsKey (lookahead)){
				signalSchedule[lookahead] = false;
			}
			lookahead++;
		}

		line.SetPosition (0, start.transform.position+Vector3.forward*0.5f);
		line.SetPosition (1, end.transform.position-Vector3.forward*0.5f);
		name = start.name + "->" + end.name;
		occupants = occupantsSchedule [viewer.t];
		active = signalSchedule [viewer.t];
		if (active) {
			line.material = activeMat;
		} else {
			line.material = offMat;
		}
	}
}

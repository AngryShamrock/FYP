using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using System;

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
	public Dictionary<int, int> flowSchedule;
	public Dictionary<int, bool> signalSchedule;
	private int lookahead = 0;
	public GameObject indicator;

	private Vector3 endPosition;
	private Vector3 startPosition;

	private PlanViewer viewer;

	public IndicatorScript[] flowArray;

	LineRenderer line;
	// Use this for initialization
	void Start () {

		startPosition = start.transform.position + Vector3.forward * 0.5f;
		endPosition = end.transform.position - Vector3.forward * 0.5f;

		flowArray = new IndicatorScript[cost];
		for (int i=0; i< cost; i++) {

			GameObject tmp = (GameObject) Instantiate ( indicator,
			                 startPosition + (endPosition-startPosition)*i/cost
				                          , transform.rotation);
			flowArray[i] = tmp.GetComponent<IndicatorScript>();
		}
		viewer = GameObject.FindGameObjectWithTag ("GameController").
			GetComponent<PlanViewer>();

		flowSchedule = new Dictionary<int, int> ();
		occupantsSchedule = new Dictionary<int, int> ();
		signalSchedule = new Dictionary<int, bool> ();
		 line = GetComponent<LineRenderer> ();
		line.SetVertexCount (2);

		line.SetPosition (0, startPosition);
		line.SetPosition (1, endPosition);
	}
	
	// Update is called once per frame
	void Update () {

		startPosition = start.transform.position + Vector3.forward * 0.5f;
		endPosition = end.transform.position - Vector3.forward * 0.5f;

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
			if (!flowSchedule.ContainsKey (lookahead)){
				flowSchedule[lookahead] = 0;
			}
			lookahead++;
		}
		try {
		for (int i = 0; i< cost; i++) {
			if (viewer.t-i >0 ){
				flowArray[i].quantity = flowSchedule[viewer.t-i];
			} else {
				flowArray[i].quantity = 0;
			}

			} } catch (Exception e){

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

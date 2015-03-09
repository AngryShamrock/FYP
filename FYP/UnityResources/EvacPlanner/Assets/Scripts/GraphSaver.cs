using UnityEngine;
using System.Collections;
using System.IO;
using JsonFx.Json;
using JsonFx.Model;
using JsonFx;
using System.Collections.Generic;
//using Newtonsoft.Json.Linq;
using System;

public class GraphSaver : MonoBehaviour {
	public string path;
	public string dir;
	public GameObject nodeTemp;
	public GameObject edgeTemp;
	public GameObject elevatorTemp;
	private bool loadNext;


	// Use this for initialization
	void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {
		if (loadNext) {
			loadNext = false;
						loadGraph ();
						
		}
		if (Input.GetKeyDown (KeyCode.K)) {
			SaveGraph ();		
		}

		if (Input.GetKeyDown( KeyCode.L)) {
			//Destroy Nodes and Elevators
			GameObject[] Nodes = GameObject.FindGameObjectsWithTag ("Node");
			foreach (GameObject node in Nodes) {
				Destroy (node);
			}

			foreach(GameObject elevator in GameObject.FindGameObjectsWithTag ("Elevator")){
				Destroy ( elevator );
			}

			loadNext= true;


		}
	}

	public void SaveGraph() {

		JSONObject json = JSONObject.obj;

		JSONObject nodes = JSONObject.arr;
		GameObject[] Nodes = GameObject.FindGameObjectsWithTag ("Node");
		print (Nodes.Length);
		foreach( GameObject node in Nodes ){
			NodeScript scptNode = node.GetComponent<NodeScript>();
			JSONObject jNode = JSONObject.obj;
			jNode.AddField ("id", scptNode.Id);
			JSONObject position = JSONObject.obj;
			position.AddField ("x", node.transform.position.x);
			position.AddField ("y", node.transform.position.y);
			position.AddField ("z", node.transform.position.z);
			jNode.AddField ("position", position);
			jNode.AddField ("arrivals", scptNode.occupants);
			jNode.AddField ("isGoal", scptNode.goalState);
			//EDGES
			JSONObject jEdges = JSONObject.arr;
			foreach (EdgeScript edge in scptNode.GetComponentsInChildren<EdgeScript>()) {
				//EdgeScript scptEdge = edge.GetComponent<EdgeScript>();
				NodeScript A = edge.start.GetComponent<NodeScript>();
				NodeScript B = edge.end.GetComponent<NodeScript>();

				JSONObject jEdge = JSONObject.obj;
				jEdge.AddField ("start", A.Id);
				jEdge.AddField ("end", B.Id);
				jEdge.AddField ("cost", edge.cost);
				jEdge.AddField ("flowRate", edge.flowRate);
				jEdges.Add (jEdge);
			}
			jNode.AddField ("edges", jEdges);
			nodes.Add (jNode);

		}

		json.AddField ("nodes", nodes);

		JSONObject elevators = JSONObject.arr;

		GameObject[] Elevators = GameObject.FindGameObjectsWithTag ("Elevator");
		
		foreach (GameObject elevator in Elevators) {
			ElevatorScript elevate = elevator.GetComponent<ElevatorScript>();

			JSONObject jElevator = JSONObject.obj;
			jElevator.AddField ("id", elevate.Id);
			jElevator.AddField ("initialLocation", elevate.initialLocation);
			JSONObject jNodes = JSONObject.arr;

			foreach ( GameObject node in elevate.nodes){
				JSONObject jNode = JSONObject.obj;
				NodeScript scpt = node.GetComponent<NodeScript>();

				jNode.AddField ("id", scpt.Id);
				jNodes.Add (jNode);
			}
			jElevator.AddField ( "nodes", jNodes);
			elevators.Add (jElevator);
		}

		json.AddField ("elevators", elevators);

		StreamWriter fileOut = File.CreateText (path);

		fileOut.WriteLine (json.ToString());
		fileOut.Close ();

	}

	public void loadGraph() {

		//Destroy all nodes


		//read file
		string line;
		using (StreamReader sr = new StreamReader(path)) {
				line = sr.ReadToEnd();
		}

		JsonReader reader = new JsonReader ();

		var output = reader.Read<Dictionary<string, object>>(line);
		Dictionary<string, object>[] jnode = (Dictionary<string, object>[]) output ["nodes"];
		//create nodes
		print ("CREATE NODES");
		foreach (Dictionary<string, object> jNode in jnode) {

			GameObject node = (GameObject) Instantiate (nodeTemp);


			node.name = jNode["id"].ToString();
			NodeScript scpt = node.GetComponent<NodeScript>();

			scpt.Id = jNode["id"].ToString();
			scpt.occupants = (int) jNode["arrivals"];
			scpt.goalState = (bool) jNode["isGoal"];

			Dictionary<string, object> pos =  (Dictionary<string, object>) jNode["position"];
			node.transform.position = new Vector3( float.Parse (pos["x"].ToString()),
			                                      float.Parse( pos["y"].ToString()),
			                                      float.Parse (pos["z"].ToString()));
		}
		print ("CREATE EDGES");
		//Create edges
		foreach (Dictionary<string, object> jNode in jnode) {
			try {
				foreach (Dictionary<string, object> jEdge in (Dictionary<string, object>[])  jNode["edges"]) {
					GameObject start = GameObject.Find (jEdge["start"].ToString());
					GameObject end = GameObject.Find (jEdge["end"].ToString());

					GameObject edge = (GameObject)Instantiate (edgeTemp,
					                                           start.transform.position,
					                                           start.transform.rotation);
					edge.name = jEdge ["start"].ToString () + "->" + jEdge ["end"].ToString ();
					EdgeScript edgeScpt = edge.GetComponent<EdgeScript> ();


					edgeScpt.flowRate = (int)jEdge ["flowRate"];
					edgeScpt.cost = (int)jEdge ["cost"];
					edgeScpt.start = start;
					edgeScpt.end = end;
					edge.transform.parent=start.transform;
				}
			} catch (InvalidCastException e) {
			}
		}

		//Create elevators
		try {
			foreach (Dictionary<string, object> jElevator in (Dictionary<string, object>[]) output ["elevators"]) {
				//instantiate new elevator
				GameObject elevator = (GameObject) Instantiate (elevatorTemp);
				elevator.name = jElevator["id"].ToString() + "elevator";
				ElevatorScript scpt = elevator.GetComponent<ElevatorScript>();

				Dictionary<string, object>[] jNodes = (Dictionary<string, object>[]) jElevator["nodes"];

				GameObject[] nodes = new GameObject[jNodes.Length];
				//Create new Edge array
				int index = 0;

				foreach ( Dictionary<string, object> node in jNodes){
					nodes[index] = GameObject.Find( node["id"].ToString ());
					index++;
				}

				scpt.Id = jElevator["id"].ToString();
				scpt.nodes = nodes;
				scpt.initialLocation=jElevator["initialLocation"].ToString ();
				//Find edges by name
			}
		} catch (InvalidCastException e) {

		}

	}
}

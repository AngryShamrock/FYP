using UnityEngine;
using System.Collections;
using System.IO;
using JsonFx.Json;
using JsonFx.Model;
using JsonFx;
using System.Collections.Generic;
//using Newtonsoft.Json.Linq;

public class GraphSaver : MonoBehaviour {
	public string path;
	public string dir;
	public GameObject nodeTemp;
	public GameObject edgeTemp;
	private bool loadNext;
	// Use this for initialization
	void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {
		if (loadNext) {
						loadGraph ();
						loadNext = false;
		}
		if (Input.GetKeyDown (KeyCode.S)) {
			SaveGraph ();		
		}

		if (Input.GetKeyDown( KeyCode.L)) {
			GameObject[] Nodes = GameObject.FindGameObjectsWithTag ("Node");
			foreach (GameObject node in Nodes) {
				Destroy (node);
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
			jElevator.AddField ("initialLocation", elevate.initialLocation);
			JSONObject jEdges = JSONObject.arr;

			foreach ( GameObject edge in elevate.edges){
				JSONObject jEdge = JSONObject.obj;
				EdgeScript scpt = edge.GetComponent<EdgeScript>();
				NodeScript A = scpt.start.GetComponent<NodeScript>();
				NodeScript B = scpt.end.GetComponent<NodeScript>();

				jEdge.AddField ("start", A.Id);
				jEdge.AddField ("end", B.Id);
			}
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

			Dictionary<string, object> pos =  (Dictionary<string, object>) jNode["position"];
			node.transform.position = new Vector3( float.Parse (pos["x"].ToString()),
			                                      float.Parse( pos["y"].ToString()),
			                                      float.Parse (pos["z"].ToString()));
		}
		print ("CREATE EDGES");
		foreach (Dictionary<string, object> jNode in jnode) {
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
		}

		//Create edges
	}
}

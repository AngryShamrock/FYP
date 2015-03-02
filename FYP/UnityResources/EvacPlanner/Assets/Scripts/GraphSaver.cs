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
	// Use this for initialization
	void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {
		if (Input.GetKeyDown (KeyCode.S)) {
			SaveGraph ();		
		}
	}

	public void SaveGraph() {

		/*
		JObject json = new JObject ();
		JArray nodes = new JArray ();
		GameObject[] Nodes = GameObject.FindGameObjectsWithTag ("Node");
		foreach( GameObject node in Nodes ){
			NodeScript scptNode = node.GetComponent<NodeScript>();
			JObject jNode = new JObject();
			jNode.Add ("id", scptNode.Id);
			JObject position = new JObject();
			position.Add ("x", node.transform.position.x);
			position.Add ("y", node.transform.position.y);
			position.Add ("z", node.transform.position.z);
			jNode.Add ("position", position);
			jNode.Add ("arrivals", scptNode.occupants);
			//EDGES
			JArray jEdges = new JArray ();
			foreach (GameObject edge in scptNode.Edges) {
				EdgeScript scptEdge = edge.GetComponent<EdgeScript>();
				NodeScript A = scptEdge.start.GetComponent<NodeScript>();
				NodeScript B = scptEdge.end.GetComponent<NodeScript>();

				JObject jEdge = new JObject ();
				jEdge.Add ("start", A.Id);
				jEdge.Add ("end", B.Id);
				jEdge.Add ("cost", scptEdge.cost);
				jEdge.Add ("flowRate", scptEdge.flowRate);
				jEdges.Add (jEdge);
			}

		}

		json.Add ("nodes", nodes);

		JArray elevators = new JArray ();

		GameObject[] Elevators = GameObject.FindGameObjectsWithTag ("Elevator");
		
		foreach (GameObject elevator in Elevators) {
			ElevatorScript elevate = elevator.GetComponent<ElevatorScript>();

			JObject jElevator = new JObject();
			jElevator.Add ("initialLocation", elevate.initialLocation);
			JArray jEdges = new JArray();

			foreach ( GameObject edge in elevate.edges){
				JObject jEdge = new JObject();
				EdgeScript scpt = edge.GetComponent<EdgeScript>();
				NodeScript A = scpt.start.GetComponent<NodeScript>();
				NodeScript B = scpt.end.GetComponent<NodeScript>();

				jEdge.Add ("start", A.Id);
				jEdge.Add ("end", B.Id);
			}
		}

		json.Add ("elevators", elevators);

		StreamWriter fileOut = File.CreateText (path);

		fileOut.WriteLine (json.ToString());
		fileOut.Close ();
*/

	}

	public void loadGraph( string path ) {

		//Destroy all nodes
		GameObject[] Nodes = GameObject.FindGameObjectsWithTag ("Node");
		foreach (GameObject node in Nodes) {
			Destroy (node);
		}

		//read file
		string line;
		using (StreamReader sr = new StreamReader(path)) {
				line = sr.ReadToEnd();
		}
		JsonReader reader = new JsonReader ();

		var output = reader.Read<Dictionary<string, object>>(line);
		Dictionary<string, object>[] jnode = (Dictionary<string, object>[]) output ["nodes"];
		//create nodes
		//Create edges
		//
	}
}

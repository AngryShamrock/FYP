using UnityEngine;
using System.Collections;
using System.IO;
using JsonFx.Json;
using System.Collections.Generic;
using System;

public class PlanViewer : MonoBehaviour {
	public string path;
	public int t;
	public int maxT = 0;
	// Use this for initialization
	void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {
		if (Input.GetKeyDown (KeyCode.P)) {
			loadPlan( path );
		}

		if (Input.GetKeyDown (KeyCode.RightArrow)) {
			if (t < maxT){
				t++;
				print ("t=" + t);
			}

		}
		if (Input.GetKeyDown (KeyCode.LeftArrow)){
			if (t > 0){
				t--;
				print ("t=" + t);
			}
		}
	}

	private void loadPlan( string path ) {
		print ("importing plan");
		string line;
		using (StreamReader sr = new StreamReader(path)) {
			line = sr.ReadToEnd();
		}
		
		JsonReader reader = new JsonReader ();
		
		var output = reader.Read<Dictionary<string, object>[]>(line);

		foreach (Dictionary<string, object> step in output) {
			int t = (int) step["t"];
			try {
				foreach ( Dictionary<string, object> signal in (Dictionary<string, object>[]) step["signals"] ) {
					//Find edge

					//modify occupants/signal
					//Set up cache for quickly finding edges
					GameObject edge = GameObject.Find (signal["start"].ToString () + "->" + signal["end"].ToString ());
					EdgeScript scpt = edge.GetComponent<EdgeScript>();

					scpt.occupantsSchedule[t] = (int) signal["predictedOccupancy"];
					scpt.signalSchedule[t] = (bool) signal["signal"];
				}
			} catch (Exception e) {

			}
			maxT = t-1;
		}

	}
}

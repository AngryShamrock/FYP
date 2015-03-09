using UnityEngine;
using System.Collections;

public class NodeScript : MonoBehaviour {

	public GameObject[] Edges;
	public string Id;
	public bool goalState;
	public int occupants;
	public bool active;
	public Material offMat;
	public Material activeMat;
	// Use this for initialization
	void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {
		Id = name;
		if (active) {
			renderer.material = activeMat;
		} else {
			renderer.material = offMat;
		}
	}
}

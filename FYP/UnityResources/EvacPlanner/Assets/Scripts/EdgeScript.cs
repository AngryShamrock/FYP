using UnityEngine;
using System.Collections;

public class EdgeScript : MonoBehaviour {
	public GameObject start;
	public GameObject end;
	public int cost;
	public int flowRate;
	// Use this for initialization
	void Start () {
		LineRenderer line = GetComponent<LineRenderer> ();
		line.SetVertexCount (2);

		line.SetPosition (0, start.transform.position);
		line.SetPosition (1, end.transform.position);
	}
	
	// Update is called once per frame
	void Update () {
	
	}
}

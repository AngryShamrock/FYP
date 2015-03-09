using UnityEngine;
using System.Collections;

public class BuildingViewer : MonoBehaviour {

	// Use this for initialization
	void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {
	
	}
	
	void FixedUpdate() {
		if (Input.GetKey (KeyCode.W)){
			rigidbody.AddForce (Vector3.up*3);
		}
		else if (Input.GetKey (KeyCode.S)){
			rigidbody.AddForce (-Vector3.up*3);
		}
		
		if (Input.GetKey (KeyCode.A)){
			rigidbody.AddTorque (Vector3.up*3);
		}
		else if (Input.GetKey (KeyCode.D)){
			rigidbody.AddTorque (-Vector3.up*3);
		}
		
	}
}

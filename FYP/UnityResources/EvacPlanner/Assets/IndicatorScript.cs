using UnityEngine;
using System.Collections;

public class IndicatorScript : MonoBehaviour {


	public int quantity;
	// Use this for initialization
	void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {
		if (quantity > 0) {
			this.renderer.enabled = true;
		} else {
			this.renderer.enabled = false;
		}
	
	}
}

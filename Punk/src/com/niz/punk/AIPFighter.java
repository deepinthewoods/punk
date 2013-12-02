package com.niz.punk;

public class AIPFighter extends AIProcessor {

	public AIPFighter(){
		AINeuron[] n = new AINeuron[2];
		n[0] = new AIMelee();
		n[1] = new AIRanged();
		
		neurons = n;
	}
	
	

}

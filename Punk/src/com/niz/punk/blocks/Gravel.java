package com.niz.punk.blocks;

import com.niz.punk.Block;
import com.niz.punk.PunkMap;

public class Gravel extends GravityAwareblock {
public Gravel(){

	blockType = 66;
	lightLoss = 2;
	dayLightLoss = 3;

}

@Override
public void place(PunkMap map, int x, int y, int beforeID, int beforeMeta,
		boolean propagate, Block b, boolean left) {
	
	
}
}

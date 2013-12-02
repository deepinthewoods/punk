package com.niz.punk.blocks;

import com.niz.punk.Block;
import com.niz.punk.BlockLoc;
import com.niz.punk.PunkMap;

public class PineSapling extends Sapling {
@Override
public void grow(int x, int y, Block block, PunkMap map){
		map.generateTree(x, y, PunkMap.currentPlane, block.meta);
		//map.flushChanges();
}

}

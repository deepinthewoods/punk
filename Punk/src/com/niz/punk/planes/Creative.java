package com.niz.punk.planes;

import com.niz.punk.Chunk;
import com.niz.punk.PlaneDef;
import com.niz.punk.Punk;
import com.niz.punk.PunkBodies;
import com.niz.punk.PunkMap;

public class Creative extends PlaneDef {
	public Creative(int seed){
		this.seed = seed;
		name = "creative";
		itemLimit = 0;
		creativeItems = true;
	}
	@Override
	public void generateColumn(int x, PunkMap map, Chunk c) {
		int xOffset = c.xOffset, heightID = c.heightID, yOffset = c.yOffset, chunkID = c.chunkID;
		for (int y = 0; y < Punk.CHUNKSIZE; y++){
			byte lite = 0;
			if (y+yOffset < 0) c.block(x,y).set(2,0);
			else{
				c.block(x,y).set(0,0);
				lite = 15;
			}
			c.block(x,y).setDayLight(lite);
		
		}
	}

	@Override
	public boolean setUp(PunkBodies monsterIndex, PunkMap map) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void start(Chunk c) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean finish(PunkMap map, Chunk c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public PlaneDef loadFromDisk(StringBuilder s) {
		return this;

	}

	@Override
	public void saveToDisk(StringBuilder s) {
		// TODO Auto-generated method stub

	}

}

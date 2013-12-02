package com.niz.punk.planes;

import com.niz.punk.Chunk;
import com.niz.punk.PlaneDef;
import com.niz.punk.Punk;
import com.niz.punk.PunkBodies;
import com.niz.punk.PunkMap;

public class Causeway extends PlaneDef {

	public Causeway(int seed){
		this.seed = seed;
		name = "causeway";
	}
	
	@Override
	public void generateColumn(int x, PunkMap map, Chunk c) {
		int xOffset = c.xOffset, heightID = c.heightID, yOffset = c.yOffset, chunkID = c.chunkID;
		int gh = getGroundHeight(x+xOffset);
		for (int y = Punk.CHUNKSIZE-1; y >= 0 ; y--){
			 if (Math.abs(gh - (y+yOffset)) < 10){
				 c.block(x,y).set(0,0);
				if (y+yOffset == gh-10)
					;//map.makeDoor(x, yOffset, id, destX, destY, destPlane);
			 }
			 else c.block(x,y).set(2,0);
		}
		
	}

	
	public int getGroundHeight(int i) {
		float h = 0;
		
		h += Chunk.noise.get1d(i, seed, 12) * 10;
		h += Chunk.noise.get1d(i, seed, 24) * 20;
		h += Chunk.noise.get1d(i, seed, 48) * 40;
		h += Chunk.noise.get1d(i, seed, 96) * 80;
		h /= 4f;
		
		return (int) h;
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

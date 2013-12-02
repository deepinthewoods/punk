package com.niz.punk.blocks;

import com.badlogic.gdx.physics.box2d.World;
import com.niz.punk.Block;
import com.niz.punk.BlockDef;
import com.niz.punk.BlockLoc;
import com.niz.punk.Chunk;
import com.niz.punk.PhysicsActor;
import com.niz.punk.PunkMap;
import com.niz.punk.PunkMap.BlockDamageType;

public class Mushroom extends BlockDef {
	public Mushroom(){
		blockType = 0;
		lightLoss = 1;
		dayLightLoss = 0;
		minDayLight = 5;
	}
	@Override
	public void update(BlockLoc loc, Block block, PunkMap map,
			boolean propagate) {
		//if (map.getBlock
		if (map.getBlock(loc.x, loc.y-1).blockType() < 64){
			map.destroyBlock(BlockDamageType.BLOCK, loc.x, loc.y, PunkMap.currentPlane, 0);
		}

	}

	@Override
	public void timedUpdate(BlockLoc currentBlock, Block block, PunkMap map) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unscrub(PunkMap map, int x, int y, Block b, Chunk c, boolean placing) {
		map.addLightUpdate(x+c.xOffset, y+c.yOffset);


	}

	@Override
	public void place(PunkMap map, int x, int y, int beforeID, int beforeMeta,
			boolean propagate, Block b, boolean left) {

	}

	@Override
	public void mobFeet(PunkMap map, PhysicsActor act, Block b, World world) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mobHead(PunkMap map, PhysicsActor act, Block b) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mobFeetExit(PunkMap map, PhysicsActor act, Block b) {
		// TODO Auto-generated method stub

	}
	

}

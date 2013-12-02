package com.niz.punk.blocks;

import com.badlogic.gdx.physics.box2d.World;
import com.niz.punk.Block;
import com.niz.punk.BlockDef;
import com.niz.punk.BlockLoc;
import com.niz.punk.Chunk;
import com.niz.punk.PhysicsActor;
import com.niz.punk.PunkMap;

public class GravityAwareblock extends BlockDef {

	@Override
	public void update(BlockLoc loc, Block block, PunkMap map,
			boolean propagate) {
		//look underneath
		Block bottomB = map.getBlock(loc.x, loc.y-1);
		if (bottomB.blockType() == 0){
			//fall
			map.createBlockMover(loc.x, loc.y, block, 2);
			//map.destroyBlock(true, loc.x, loc.y, PunkMap.currentPlane, 0);
			int id = block.blockID, meta = block.meta;
			
			map.changeBlock(loc.x, loc.y, 0, 0, true);
			map.changeBlock(loc.x, loc.y-1, 7, 2, true);
			
		}
	}

	@Override
	public void timedUpdate(BlockLoc currentBlock, Block block, PunkMap map) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unscrub(PunkMap map, int x, int y, Block b, Chunk c, boolean placing) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void place(PunkMap map, int x, int y, int beforeID, int beforeMeta,
			boolean propagate, Block b, boolean left) {
		// TODO Auto-generated method stub
		
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

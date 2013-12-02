package com.niz.punk.blocks;

import com.badlogic.gdx.physics.box2d.World;
import com.niz.punk.Block;
import com.niz.punk.BlockDef;
import com.niz.punk.BlockLoc;
import com.niz.punk.Chunk;
import com.niz.punk.PhysicsActor;
import com.niz.punk.PunkMap;
import com.niz.punk.PunkMap.BlockDamageType;

public class SkyBlock extends BlockDef {
	public SkyBlock(){
		blockType = 65;
		lightLoss = 1;
		dayLightLoss = 0;
		minDayLight = 15;
	}
	@Override
	public void update(BlockLoc currentBlock, Block block, PunkMap map,
			boolean propagate) {
		// TODO Auto-generated method stub
		//map.updater.updateBitmasked(10, currentBlock, map, true);
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
	public void destroy(BlockDamageType dType, int mapX, int mapY, int p,
			float angle, PunkMap map, int id, int meta) {
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

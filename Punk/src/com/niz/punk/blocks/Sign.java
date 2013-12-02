package com.niz.punk.blocks;

import com.badlogic.gdx.physics.box2d.World;
import com.niz.punk.Block;
import com.niz.punk.BlockDef;
import com.niz.punk.BlockLoc;
import com.niz.punk.Chunk;
import com.niz.punk.PhysicsActor;
import com.niz.punk.Player;
import com.niz.punk.PunkMap;

public class Sign extends BlockDef {
	public Sign(){

		blockType = 0;
		lightLoss = 3;
		dayLightLoss = 3;
	
	}
	@Override
	public void update(BlockLoc currentBlock, Block block, PunkMap map,
			boolean propagate) {
		// TODO Auto-generated method stub

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
		if (act instanceof Player){
			Player p = (Player) act;
			p.viewingSignID = b.meta;
		}

	}

	@Override
	public void mobHead(PunkMap map, PhysicsActor act, Block b) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mobFeetExit(PunkMap map, PhysicsActor act, Block b) {
		if (act instanceof Player){
			Player p = (Player) act;
			p.viewingSignID = -1;
		}

	}
	

}

package com.niz.punk.blocks;

import com.badlogic.gdx.physics.box2d.World;
import com.niz.punk.Block;
import com.niz.punk.BlockDef;
import com.niz.punk.BlockLoc;
import com.niz.punk.Chunk;
import com.niz.punk.PhysicsActor;
import com.niz.punk.PunkMap;
import com.niz.punk.PunkMap.BlockDamageType;

public class Dirt extends BlockDef {
	public Dirt(){
		blockType = 68;
		lightLoss = 3;
		dayLightLoss = 3;
	}
	public void update(BlockLoc currentBlock, Block block, PunkMap map,
			 boolean propagate){
		
	}
	public void timedUpdate(BlockLoc currentBlock, Block block, PunkMap map){
		
	}
	public void unscrub(PunkMap map, int x, int y, Block b){
		
	}

	public void place(PunkMap map, int x, int y, int beforeID, int beforeMeta,
			boolean propagate, Block b, boolean left){
		
	}
	@Override
	public void destroy(BlockDamageType dType, int mapX, int mapY, int p, float angle, PunkMap map, int id, int meta)
	{
		map.createItem(2, 1, 0, 0, mapX, mapY);
	}
	@Override
	public void unscrub(PunkMap map, int x, int y, Block b, Chunk c, boolean placing) {
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

package com.niz.punk.blocks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.World;
import com.niz.punk.Block;
import com.niz.punk.BlockDef;
import com.niz.punk.BlockLoc;
import com.niz.punk.Chunk;
import com.niz.punk.PhysicsActor;
import com.niz.punk.PunkMap;
import com.niz.punk.PunkMap.BlockDamageType;

public class Stone extends BlockDef {
	public Stone(){
		blockType = 64;
		lightLoss = 4;
		dayLightLoss = 4;
	}
	public Stone(byte i, byte j, byte k, int l) {
		super(i,j,k,l);
	}
	public void update(BlockLoc currentBlock, Block block, PunkMap map,
			 boolean propagate){
		
	}
	public void timedUpdate(BlockLoc currentBlock, Block block, PunkMap map){
		
	}
	

	public void place(PunkMap map, int x, int y, int beforeID, int beforeMeta,
			boolean propagate, Block b, boolean left){
		
	}
	@Override
	public void destroy(BlockDamageType dType, int mapX, int mapY, int p, float angle, PunkMap map, int id, int meta)
	{
		map.createItem(22, 1, 0, 0, mapX, mapY);
		Gdx.app.log("stone", "drop");
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

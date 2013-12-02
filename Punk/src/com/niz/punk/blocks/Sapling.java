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

public class Sapling extends BlockDef {
	
	public Sapling(){
		blockType = 5;
		lightLoss = 0;
		dayLightLoss = 0;
	}
	public void update(BlockLoc currentBlock, Block block, PunkMap map,
			 boolean propagate){
		
	}
	public void timedUpdate(BlockLoc currentBlock, Block block, PunkMap map){
		//block.meta -= 1;
		//if (block.meta < 1) grow(currentBlock, block, map);
		//else map.addTimedUpdate(currentBlock.x, currentBlock.y);
		//Gdx.app.log("T", "sapling timed update"+block.meta);
	}
	public void grow(int x, int y, Block block, PunkMap map) {
		
		
	}
	
	public void place(PunkMap map, int x, int y, int beforeID, int beforeMeta,
			boolean propagate, Block b, boolean left){
		
	}
	@Override
	public void destroy(BlockDamageType dType, int mapX, int mapY, int p, float angle, PunkMap map, int id, int meta)
	{
		//map.createItem(2, 1, 0, 0, mapX, mapY);
	}
	@Override
	public void unscrub(PunkMap map, int x, int y, Block b, Chunk c, boolean placing) {
		//map.addTimedUpdate(x+c.xOffset,y+c.yOffset);
		if (map.getBlock(x+c.xOffset, y+c.yOffset+3).blockID == 0 || !map.chunkPool.hasChunk(x,y+3)){
			
			grow(x+c.xOffset,y+c.yOffset, b, map);
		}
		else Gdx.app.log("sapling", "SAPLING BLOCK NOT ABLE TO GROW");
		//map.finishLight();
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
	
	public void updateSlow(BlockLoc b, Block block, PunkMap map) {
		if (map.getBlock(b.x, b.y+3).blockID == 0)grow(b.x, b.y, block, map);
	}
	

	
	
}

package com.niz.punk.blocks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.World;
import com.niz.punk.Block;
import com.niz.punk.BlockDef;
import com.niz.punk.BlockLoc;
import com.niz.punk.Chunk;
import com.niz.punk.PhysicsActor;
import com.niz.punk.Player;
import com.niz.punk.PunkMap;
import com.niz.punk.PunkMap.BlockDamageType;

public class PineLeaf extends BlockDef {
	public PineLeaf(){
		blockType = 2;
		lightLoss = 2;
		dayLightLoss = 1;
		minDayLight = 5;
		flammable = true;
	}
	public void update(BlockLoc currentBlock, Block block, PunkMap map,
			 boolean propagate){
		//map.updater.updateBitmaskedByType(2, currentBlock, propagate);

	}
	public void timedUpdate(BlockLoc currentBlock, Block b, PunkMap map){
		
		
	}
	

	public void place(PunkMap map, int x, int y, int beforeID, int beforeMeta,
			boolean propagate, Block b, boolean left){
		
	}
	@Override
	public void destroy(BlockDamageType dType, int mapX, int mapY, int p,
			float angle, PunkMap map, int id, int meta) {
		//Gdx.app.log("leaf", "destroy");
		
	}
	@Override
	public void unscrub(PunkMap map, int x, int y, Block b, Chunk c, boolean placing) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mobFeet(PunkMap map, PhysicsActor act, Block b, World world) {
		if (act instanceof Player){
			Player p = (Player) act;
			p.climbButtonUpValid = true;
		}
		
		
	}

	@Override
	public void mobHead(PunkMap map, PhysicsActor act, Block b) {
		if (act instanceof Player){
			Player p = (Player) act;
			p.climbButtonDownValid = true;
		}
		
	}
	@Override
	public void mobFeetExit(PunkMap map, PhysicsActor act, Block b) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void updateSlow(BlockLoc loc, Block b, PunkMap map) {
		
		int meta = b.meta;
		int size = 10;
		boolean updateFlag = false;
		if (meta == 15) return;
		for (int lookY = 0; lookY <= size && !updateFlag; lookY++)
			for (int lookX = -size+lookY; lookX <=size-lookY && !updateFlag; lookX++)
					
				{
					if (map.getBlock(loc.x + lookX,loc.y-lookY).blockID == b.blockID-1)
						updateFlag = true;
					//if (map.getBlock(loc.x + lookX,loc.y+lookY).blockID == b.blockID-1)
					//	updateFlag = true;
					
				}
		if (!updateFlag)//if needs to be destroyed 
			{
				map.destroyBlock(BlockDamageType.BLOCK, loc.x,loc.y, MathUtils.random(360), PunkMap.currentPlane);
				//map.updater.addTimedUpdatesSurrounding(loc);
				//Gdx.app.log("leaf", "slow update"+loc);
			}
		
	}
	
}

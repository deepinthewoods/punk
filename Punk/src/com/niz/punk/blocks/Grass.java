package com.niz.punk.blocks;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.World;
import com.niz.punk.Block;
import com.niz.punk.BlockDef;
import com.niz.punk.BlockLoc;
import com.niz.punk.Chunk;
import com.niz.punk.PhysicsActor;
import com.niz.punk.PunkMap;
import com.niz.punk.PunkMap.BlockDamageType;

public class Grass extends BlockDef {
	public Grass(){
		blockType = 68;
		lightLoss = 3;
		dayLightLoss = 3;
	}
	public void update(BlockLoc loc, Block block, PunkMap map,
			 boolean propagate){
		//2-bit meta, 1=l, 2=r
		Block lb = map.getBlock(loc.x-1, loc.y);
		Block rb = map.getBlock(loc.x+1, loc.y);
		int mask = 0;
		if (lb.blockID == 0) mask+= 2;
		if (rb.blockID == 0) mask += 1;
		block.setMeta(mask);
		if (map.getBlock(loc.x, loc.y+1).blockType() != 0) block.set(2,0);
	}
	public void timedUpdate(BlockLoc currentBlock, Block block, PunkMap map){
		
	}
	

	public void place(PunkMap map, int x, int y, int beforeID, int beforeMeta,
			boolean propagate, Block b, boolean left){
		
	}
	@Override
	public void destroy(BlockDamageType dType, int mapX, int mapY, int p, float angle, PunkMap map, int id, int meta)
	{
		//
		if (dType == BlockDamageType.FLAIL)map.changeBlock(mapX,  mapY, 2, 0, true);
		else map.createItem(2, 1, 0, 0, mapX, mapY);
	}
	@Override
	public void unscrub(PunkMap map, int x, int y, Block b, Chunk c, boolean placing) {
		map.addUpdate(x+c.xOffset,y+c.yOffset);
		
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
	
	public void updateSlow(BlockLoc loc, Block b, PunkMap map) {
		Block topB = map.getBlock(loc.x, loc.y+1);
		if ((topB.blockType() >= 64 )|| b.getDayLight() < 5){
			b.set(2,0);
			//Gdx.app.log("updater", "removed grass");
		}
		//try to grow
		int growX = MathUtils.random(loc.x-4, loc.x+4),
				growY = MathUtils.random(loc.y-4, loc.y+4);
		Block tmpBlock = map.getBlock(growX, growY);
		int top = map.getBlock(growX, growY+1).blockType();
		if (tmpBlock.blockID == 2 && (top == 0 || top == 6 || top == 2)){
			//tmpBlock.set(3,0);
			//c.grassList.addBlock(growX, growY);
			map.changeBlock(growX,  growY, 3, 0, true);
		}
	}
	
	
}

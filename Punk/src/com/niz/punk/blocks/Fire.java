package com.niz.punk.blocks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.World;
import com.niz.punk.Block;
import com.niz.punk.BlockDef;
import com.niz.punk.BlockLoc;
import com.niz.punk.Chunk;
import com.niz.punk.PhysicsActor;
import com.niz.punk.PunkMap;
import com.niz.punk.PunkMap.BlockDamageType;

public class Fire extends BlockDef {
	
	private static final int DAMAGEMAX = 16;
	public Fire(){
		blockType = 16;
		lightLoss = 0;
		dayLightLoss = 0;
		minLight = 10;
		flammable = false;
		isAutomata = true;
	}
	@Override
	public void update(BlockLoc loc, Block block, PunkMap map, boolean propagate) {
		// TODO Auto-generated method stub

	}
	BlockLoc tmpB = new BlockLoc();
	@Override
	public void timedUpdate(BlockLoc loc, Block b, PunkMap map) {
		b.setMeta(b.meta -1);
		if (b.meta <= 0){
			map.changeBlock(loc.x, loc.y, 0, 0, true);
			propagate(map, loc.x-1, loc.y);
			propagate(map, loc.x+1, loc.y);
			propagate(map, loc.x, loc.y-1);
			return;
		}
		if (b.meta % 4 == 0){
			tmpB.set(loc);
			tmpB.add(MathUtils.random(-3,3), MathUtils.random(-2,5));
			Block b2 = map.getBlock(tmpB);
			if (b2.blockType() == 0){
				//look at surrounding blocks
				
				found = false;
				check(map, tmpB.x-1, tmpB.y);
				check(map, tmpB.x+1, tmpB.y);
				check(map, tmpB.x, tmpB.y-1);
				
				if (found){
					map.changeBlock(tmpB.x, tmpB.y, 42, 0, true);
				}
			} else if (b2.blockID == b.blockID){
				b2.meta+=MathUtils.random(7);
			} else if (b2.isAutomata()){
				int met = MathUtils.random(DAMAGEMAX);//MathUtils.random(TRANSMIT);
				//Gdx.app.log("frost", "damage flow/attack"+met);
				map.damageBlock(BlockDamageType.FIRE, tmpB, met);
				b.meta -= met/2;
			}
		}
		
		
		
		map.addTimedUpdate(loc.x, loc.y);
		
		

	}
	@Override
	public boolean takeDamage(Block b, BlockDamageType dType, int mapX, int mapY, int p,
			float angle, PunkMap map, int damage) {
		//Gdx.app.log("frost", "damage"+damage);
		switch (dType){
		case FROST: 
			b.meta -= damage;
			break;
		case FIRE:
			b.meta += damage;
			break;
		case CHARGE:
			b.meta -= damage;
			break;
		}
		return false;
		
	}
	private void propagate(PunkMap map, int x, int y) {
		if (check(map, x, y))map.changeBlock(x, y, 42, 0, true);
		
	}
	boolean found = false;
	private boolean check(PunkMap map, int x, int y) {
		Block b = map.getBlock(x,y);
		if ( b.isFlammable())
			found = true;	
		return found;
	}
	@Override
	public void destroy(BlockDamageType dType, int mapX, int mapY, int p,
			float angle, PunkMap map, int id, int meta) {
		//Gdx.app.log("leaf", "destroy");
		
	}
	@Override
	public void unscrub(PunkMap map, int x, int y, Block b, Chunk c,
			boolean placing) {
		map.addTimedUpdate(x+c.xOffset, y+c.yOffset);
		b.setMeta(MathUtils.random(4, 12));
		map.addLightUpdate(x+c.xOffset, y+c.yOffset);
		//Gdx.app.log("fire", "unsc");
	}

	@Override
	public void place(PunkMap map, int x, int y, int beforeID, int beforeMeta,
			boolean propagate, Block b, boolean left) {
		
		
	}

	@Override
	public void mobFeet(PunkMap map, PhysicsActor act, Block b, World world) {
		act.isOnFire = true;

	}

	@Override
	public void mobHead(PunkMap map, PhysicsActor act, Block b) {
		act.isOnFire = true;

	}

	@Override
	public void mobFeetExit(PunkMap map, PhysicsActor act, Block b) {
		

	}
	

}

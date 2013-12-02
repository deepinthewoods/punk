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

public class Charge extends BlockDef {
	private static final int DISTANCE = 12;
	private static final int TRANSMIT = 16;
	private static final int SPAWNMIN = 11;
	private static final int SPAWNMAX = 22;
	public static boolean allowed = true;
	public Charge(){
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
			//propagate(map, loc.x-1, loc.y);
			//propagate(map, loc.x+1, loc.y);
			//propagate(map, loc.x, loc.y-1);
			return;
		}
		if (b.meta % 4 == 0){
			
			tmpB.set(MathUtils.random(-DISTANCE,DISTANCE), MathUtils.random(-DISTANCE,DISTANCE));
			if (tmpB.manhattanLen() < 3){
				//tmpB.set(MathUtils.random(-DISTANCE,DISTANCE), MathUtils.random(-DISTANCE,DISTANCE));
				map.addTimedUpdate(loc.x, loc.y);
				return;
			}
			
			tmpB.add(loc);
			
			
			Block b2 = map.getBlock(tmpB);
			if (b2.blockType() == 0){
				//look at surrounding blocks
				
				
				found = false;
				check(map, tmpB.x-1, tmpB.y);
				check(map, tmpB.x+1, tmpB.y);
				check(map, tmpB.x, tmpB.y-1);
				
				if (found){
					//map.changeBlock(tmpB.x, tmpB.y, 42, 0, true);
					propagate(map, loc.x, loc.y, tmpB.x, tmpB.y, b);
				}
			}  else if (b2.isAutomata()){
				int met = MathUtils.random(TRANSMIT);
				met = Math.min(met,  b.meta);
				propagate(map, loc.x, loc.y, tmpB.x, tmpB.y, b);
				//Gdx.app.log("frost", "damage flow/attack"+met);
				//map.damageBlock(BlockDamageType.CHARGE, tmpB, met);
				b.meta -= met;
			}
		} 

		if (b.meta % 4 == 1){
			//randomly jump
			
			switch (MathUtils.random(3)){
			
			case 0:tmpB.set(loc.x-1, loc.y);break;
			case 1:tmpB.set(loc.x+1, loc.y);break;
			case 2:tmpB.set(loc.x, loc.y-1);break;
			case 3:tmpB.set(loc.x, loc.y+1);break;
			//case 4:tmpB.set(loc.x, loc.y-1);break;
			
			}
			Block b2 = map.getBlock(tmpB);
			if (b2.isAutomata()){
				map.damageBlock(BlockDamageType.CHARGE, tmpB, b.meta);
				b.meta = 1;
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
			b.meta -= damage;
			break;
		case CHARGE:
			b.meta += damage;
			break;
		}
		return false;
		
	}
	private void propagate(PunkMap map, int x, int y, int x2, int y2, Block b) {
		if (!allowed) return;
		map.beam(b.meta, BlockDamageType.CHARGE, x, y, x2, y2);
		allowed = false;
		//if (check(map, x, y))map.changeBlock(x, y, 42, 0, true);
		
	}
	boolean found = false;
	private boolean check(PunkMap map, int x, int y) {
		Block b = map.getBlock(x,y);
		if ( b.isFlammable())
			found = true;	
		found = false;
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
		//b.setMeta(MathUtils.random(4, 12));
		//b.setMeta(MathUtils.random(SPAWNMIN, SPAWNMAX));
		map.addLightUpdate(x+c.xOffset, y+c.yOffset);
		//Gdx.app.log("fire", "unsc");
	}

	@Override
	public void place(PunkMap map, int x, int y, int beforeID, int beforeMeta,
			boolean propagate, Block b, boolean left) {
		
		
	}

	@Override
	public void mobFeet(PunkMap map, PhysicsActor act, Block b, World world) {
		//act.isOnFire = true;

	}

	@Override
	public void mobHead(PunkMap map, PhysicsActor act, Block b) {
		//act.isOnFire = true;

	}

	@Override
	public void mobFeetExit(PunkMap map, PhysicsActor act, Block b) {
		

	}
	

}

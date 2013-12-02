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

public class Frost extends BlockDef{
	private static final int UPDATERATE = 0,//11;//0 is always, 8 half, 16 1 in 16
			TRANSMIT = 2;
	public Frost(){
		blockType = 16;
		lightLoss = 1;
		dayLightLoss = 1;
		minLight = 0;
		flammable = false;
		isAutomata = true;
	}
	@Override
	public void update(BlockLoc loc, Block block, PunkMap map, boolean propagate) {
		// TODO Auto-generated method stub

	}
	public boolean updateDayLight(int x, int y, Block b, Chunk c, byte[][] lightMatrix, byte[][] dayLightMatrix){
		int highestL = (byte)  Math.max(minDayLight, getHighestLightNoTop(dayLightMatrix)-dayLightLoss);
		int topL = dayLightMatrix[1][2], currentLight = dayLightMatrix[1][1];
		if (topL >= currentLight){
			if (currentLight != topL ){
				//Gdx.app.log(TAG, "prp "+currentLight + " top "+topL);
				b.setDayLight(topL);
				
					//Gdx.app.log(TAG,"propa"+currentLight+" - "+highestL);
					return true;
				
			}
		}
		else
		if ((currentLight != highestL)){
			b.setDayLight(highestL);
			//if (currentLight != highest)
				
					//Gdx.app.log(TAG,"prop"+currentLight+" - "+highestL);
					return true;
				
		}
		return false;
	}
	BlockLoc tmpB = new BlockLoc();
	@Override
	public void timedUpdate(BlockLoc loc, Block b, PunkMap map) {
		//Gdx.app.log("frost", "metaa"+b.meta);
		//b.setMeta(b.meta -1);
		//if (MathUtils.randomBoolean())b.setMeta(b.meta -1);
		if (b.meta <= 0){
			map.changeBlock(loc.x, loc.y, 0, 0, true);
			
			return;
		}
		
		if (true || MathUtils.random(16) >= UPDATERATE){
			//Gdx.app.log("frost", "met"+((b.meta/2)%4));
			switch (MathUtils.random(4)){
			
			case 0:tmpB.set(loc.x-1, loc.y);break;
			case 1:tmpB.set(loc.x+1, loc.y);break;
			case 2:tmpB.set(loc.x, loc.y-1);break;
			case 3:tmpB.set(loc.x, loc.y+1);break;
			case 4:tmpB.set(loc.x, loc.y-1);break;
			
			}
			Block b2 = map.getBlock(tmpB);
			if (b2.blockType() == 0){
					propagate(map, tmpB.x, tmpB.y, b, b2);
					
			} else if (b2.blockID == b.blockID ){
				int met = (b2.meta + b.meta)/2;//MathUtils.random(TRANSMIT);
				b2.meta = met;
				b.meta = met;
				//Gdx.app.log("frost", "self"+met+"  "+b+"  "+b2);
				
			} else if (b2.isAutomata()){
				int met = b.meta/2;//MathUtils.random(TRANSMIT);
				Gdx.app.log("frost", "damage flow/attack"+met);
				map.damageBlock(BlockDamageType.FROST, tmpB, met);
				b.meta -= met;
			}else b.meta--;
		}
		map.addTimedUpdate(loc.x, loc.y);
	}
	
	private void propagate(PunkMap map, int x, int y, Block b, Block b2) {
		if (check(map, x, y) && b.meta > 1){
			int met = Math.min(2, b.meta/2);//1;//MathUtils.random(TRANSMIT);
			map.damageBlock(BlockDamageType.FROST, tmpB, met);
			b.meta -= met;
		} else b.meta -= 1;
		
	}
	
	boolean found = false;
	private boolean check(PunkMap map, int x, int y) {
		Block b = map.getBlock(x,y);
		if (b.blockType() == 0)
			found = true;	
		return found;
	}
	
	@Override
	public void destroy(BlockDamageType dType, int mapX, int mapY, int p,
			float angle, PunkMap map, int id, int meta) {
		//Gdx.app.log("leaf", "destroy");
		
	}
	@Override
	public boolean takeDamage(Block b, BlockDamageType dType, int mapX, int mapY, int p,
			float angle, PunkMap map, int damage) {
		//Gdx.app.log("frost", "damage"+damage);
		switch (dType){
		case FROST: 
			b.meta += damage;
			break;
		case FIRE:
			b.meta -= damage;
			break;
		case CHARGE:
			b.meta -= damage;
			break;
		}
		return false;
		
	}
	@Override
	public void unscrub(PunkMap map, int x, int y, Block b, Chunk c,
			boolean placing) {
		map.addTimedUpdate(x+c.xOffset, y+c.yOffset);
		//b.setMeta(2);
		map.addLightUpdate(x+c.xOffset, y+c.yOffset);
		//Gdx.app.log("fire", "unsc");
	}

	@Override
	public void place(PunkMap map, int x, int y, int beforeID, int beforeMeta,
			boolean propagate, Block b, boolean left) {
		
		
	}

	@Override
	public void mobFeet(PunkMap map, PhysicsActor act, Block b, World world) {
		//act.isFrozen = true;

	}

	@Override
	public void mobHead(PunkMap map, PhysicsActor act, Block b) {
		//act.isOnFire = true;

	}

	@Override
	public void mobFeetExit(PunkMap map, PhysicsActor act, Block b) {
		

	}
	

}

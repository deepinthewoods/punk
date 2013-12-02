package com.niz.punk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.World;
import com.niz.punk.PunkMap.BlockDamageType;

public abstract class BlockDef {
private static final String TAG = "blockdef";
public byte blockType, lightLoss = 0, dayLightLoss = 0, minDayLight = 0, minLight = 0;;
public boolean 

flammable = false,  isAutomata = false, 
needsFloor = false, solid = true, climbable = false,
immuneFrost = true, immuneCharge = true

;;
public int hp = 16;
public int particleType = 0;

public BlockDef(int type, int dll, int ll, int hp){
	blockType = (byte) type;
	dayLightLoss = (byte) dll;
	lightLoss = (byte) ll;
	this.hp = hp;
}
public BlockDef(){
	blockType = 0;
	lightLoss = 1;
	dayLightLoss = 1;
}

private static Block[] bmBlocks = new Block[8];
public int bm(int type, BlockLoc loc, Block block, PunkMap map){
	fillbmBlocks( loc, block, map);
	int bitmask = 0;
	
	for (int i = 0; i < 4; i++)
		if (bmBlocks[i].blockType() == type)
			bitmask += (1<<i);
	return bitmask;
}

public int bmSolid(BlockLoc loc, Block block, PunkMap map){
	fillbmBlocks( loc, block, map);
	
	int bitmask = 0;
	
	for (int i = 0; i < 4; i++)
		if (bmBlocks[i].blockType() >= 64)
			bitmask += (1<<i);
	return bitmask;
}


private void fillbmBlocks(BlockLoc loc, Block block, PunkMap map) {
	bmBlocks[0] = map.getBlock(loc.x, loc.y+1);
	bmBlocks[1] = map.getBlock(loc.x+1, loc.y);
	bmBlocks[2] = map.getBlock(loc.x, loc.y-1);
	bmBlocks[3] = map.getBlock(loc.x-1, loc.y);
	
}
public abstract void update(BlockLoc loc, Block block, PunkMap map,
		 boolean propagate);
public abstract void timedUpdate(BlockLoc currentBlock, Block block, PunkMap map);
public abstract void unscrub(PunkMap map, int x, int y, Block b, Chunk c, boolean placing);

public abstract void place(PunkMap map, int x, int y, int beforeID, int beforeMeta,
		boolean propagate, Block b, boolean left);//called before unscrub


//returns true to add updates surrounding
public boolean updateLight(int x, int y, Block b, Chunk c, boolean isOnEdge, byte[][] lightMatrix, byte[][] dayLightMatrix){
	//Gdx.app.log(TAG, "lightupdate");
	int highestL = (byte)  Math.max(minLight, getHighestLight(lightMatrix)-lightLoss);
	int currentLight = lightMatrix[1][1];
	if (currentLight != highestL){
		b.setLight((highestL));
		return true;
	}
	if (!isOnEdge){
		if (b.setLightBits(lightMatrix, dayLightMatrix))
			return true;//map.addLightUpdatesSurrounding(x,y,false, c);
	}
	return false;
}


protected byte getHighestLight(byte[][] m) {
	byte highest = 0;
	byte l = m[0][1];
	if (l > highest)highest =l;
	
	l = m[2][1];
	if (l > highest)highest =l;
	
	l = m[1][2];
	if (l > highest)highest =l;
	
	l = m[1][0];
	if (l > highest)highest =l;
	
	
	//byte high = 0;
	//for (int i = 0; i < 3; i++)
	//	for (int j = 0; j < 3; j++)
	//	if (m[i][j] > high) high = m[i][j];
	return highest;
}

protected byte getHighestLightNoTop(byte[][] m) {
	byte highest = 0;
	byte l = m[0][1];
	if (l > highest)highest =l;
	
	l = m[2][1];
	if (l > highest)highest =l;
	
	
	
	l = m[1][0];
	if (l > highest)highest =l;
	
	
	//byte high = 0;
	//for (int i = 0; i < 3; i++)
	//	for (int j = 0; j < 3; j++)
	//	if (m[i][j] > high) high = m[i][j];
	return highest;
}
public boolean updateDayLight(int x, int y, Block b, Chunk c, byte[][] lightMatrix, byte[][] dayLightMatrix){
	int highestL = (byte)  Math.max(minDayLight, getHighestLight(dayLightMatrix)-dayLightLoss);
	int topL = dayLightMatrix[1][2], currentLight = dayLightMatrix[1][1];
	
	if ((currentLight != highestL)){
		b.setDayLight(highestL);

				return true;
			
	}
	return false;
}
public boolean updateDayLight2(int x, int y, Block b, Chunk c, boolean isOnEdge, byte[][] lightMatrix, byte[][] dayLightMatrix){
	int highestL = (byte)  Math.max(minDayLight, getHighestLight(dayLightMatrix)-dayLightLoss);
	int topL = dayLightMatrix[1][2], currentLight = dayLightMatrix[1][1];

	
	
	if ((currentLight == 15 && topL != 15) || (currentLight != highestL)){
		b.setDayLight((highestL));
		
			
	} 
	
	
		if (b.setLightBits(lightMatrix, dayLightMatrix) )//|| (currentLight != highestL))
			if (!isOnEdge){
				Gdx.app.log(TAG,"prop");
				return true;
			}
				
	
	return false;
}
public boolean takeDamage(Block b, BlockDamageType dType, int mapX, int mapY, int p, float angle, PunkMap map, int damage){
	BlockDef def = b.def();
	switch (dType){
	case FROST:
		if (def.immuneFrost)return false;
		else if (damage > def.hp){
			map.changeBlock(mapX, mapY, 43, damage, true);
			return true;
		}
		
	case FIRE:
		if (!def.flammable && def.blockType != 0)return false;
		else if (damage > def.hp){
			map.changeBlock(mapX, mapY, 42, damage, true);
			return true;
		}
		break;
	case CHARGE:
		if (def.immuneCharge)return false;
		else if (damage > def.hp){
			map.changeBlock(mapX, mapY, 44, damage, true);
			return true;
		}
		break;
	}
		
	
	return true;

}
public void destroy(BlockDamageType dType, int mapX, int mapY, int p, float angle, PunkMap map, int id, int meta){
	map.createItem(id, 1, 0, 0, mapX, mapY);
}
public abstract void mobFeet(PunkMap map, PhysicsActor act, Block b, World world);
public abstract void mobHead(PunkMap map, PhysicsActor act, Block b);
public abstract void mobFeetExit(PunkMap map, PhysicsActor act, Block b);
public void updateSlow(BlockLoc loc, Block b, PunkMap map) {
	
}
public void freeze(BlockLoc loc, Block b, PunkMap map){
	
}

}

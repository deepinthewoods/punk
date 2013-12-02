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

public class Air extends BlockDef {
	private static final String TAG = "air";
	public Air(){
		blockType = 0;
		lightLoss = 1;
		dayLightLoss = 1;
		hp = 1;
		immuneFrost = false;
		immuneCharge = false;
		
	}
	public void update(BlockLoc currentBlock, Block block, PunkMap map,
			 boolean propagate){
		
	}
	public void timedUpdate(BlockLoc currentBlock, Block block, PunkMap map){
		//map.updater.checkInFlowAir(12, currentBlock, block, map);
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
	
	

	public void place(PunkMap map, int x, int y, Block b, int iID, int metaData,
			boolean propagate, int beforeID){
		if (beforeID == 9)
		b.set(-3, 0);

	if (b.meta == 0)
		return;
	if (b.meta < 24)
		b.setMeta((byte) (b.meta + MathUtils.random(3)));
	else
		b.setMeta((byte) (b.meta + MathUtils.random(5)));
	}
	@Override
	public void destroy(BlockDamageType dType, int mapX, int mapY, int p, float angle, PunkMap map, int id, int meta)
	{
		
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
	public void mobFeet(PunkMap map, PhysicsActor act, Block tmpBlock, World world) {
		if (act.isSwimming)
		;//act.stopSwimming(world);
		
	}
	@Override
	public void mobHead(PunkMap map, PhysicsActor act, Block tmpBlock) {
		// TODO Auto-generated method stub
		
		act.isHoldingBreath = false;
	}
	@Override
	public void mobFeetExit(PunkMap map, PhysicsActor act, Block b) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}

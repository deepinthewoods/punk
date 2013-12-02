package com.niz.punk.blocks;

import com.niz.punk.Block;
import com.niz.punk.BlockDef;
import com.niz.punk.BlockLoc;
import com.niz.punk.Chunk;
import com.niz.punk.PunkMap;
import com.niz.punk.PunkMap.BlockDamageType;

public abstract class Liquid extends BlockDef{

	public Liquid(){
		blockType = 1;
	}
	
	@Override
	public void update(BlockLoc currentBlock, Block block, PunkMap map,
			boolean propagate) {
		map.addTimedUpdate(currentBlock);
	}
	private BlockLoc tmpB = new BlockLoc();
	private Block tmpBlock = new Block(0,0);
	@Override
	public void timedUpdate(BlockLoc currentBlock, Block lookBlock, PunkMap map) {
		int lookY;
		int tmpMeta = 0;
		int smallestMeta = 7;
		int currentMeta = lookBlock.meta;
		boolean leftFlow = (currentMeta < 0);
		currentMeta = Math.abs(currentMeta);
		boolean  tmpInFlow;
		int fluidBlock = lookBlock.blockID;
		//if (currentMeta == 0) break;
		tmpB.set(currentBlock.x, currentBlock.y+1);
		tmpBlock = map.getBlock(tmpB.x,tmpB.y);
		if (tmpBlock.blockID == lookBlock.blockID){
			lookBlock.setMeta(1);
			//Gdx.app.log("updater, water", "flow down");
		//otherwise change to 1 higher than surrounding blocks
		} else {
			lookY = 0;
			for (int lookX = -1; lookX <=1; lookX++)
				//for (int lookY = -1; lookY <= 0; lookY++)
					if (lookX != 0 )
				{
					tmpB.set(currentBlock.x+lookX, currentBlock.y+lookY);
					tmpBlock = map.getBlock(tmpB.x, tmpB.y);
					tmpMeta = tmpBlock.getMeta();
					tmpInFlow = (tmpMeta < 0);
					tmpMeta = Math.abs(tmpMeta);
					if (tmpBlock.blockID == lookBlock.blockID)
					{
						if (tmpMeta < smallestMeta){
							smallestMeta = tmpMeta;
							leftFlow = tmpInFlow;
							fluidBlock = tmpBlock.blockID;
						}
						
					}
				}
			
			if ( currentMeta != 0){
				lookBlock.setMeta((smallestMeta +1));
				//Gdx.app.log("WATERUPDATE", "flowing in, left:"+leftFlow+"current:"+currentMeta);
			
			if (currentMeta < smallestMeta){
				//lookBlock.setMeta(biggestMeta - 1);
				map.addTimedUpdate(currentBlock);
				//addToTimedUpdateList(currentBlock.x-1, currentBlock.y);
				//addToTimedUpdateList(currentBlock.x+1, currentBlock.y);
				///addToTimedUpdateList(currentBlock.x, currentBlock.y+1);
				//addToTimedUpdateList(currentBlock.x, currentBlock.y-1);
				//Gdx.app.log("updater", "WATER REDUCING, adding updates");
			}
			//else 
			
			}
			//put back direction
			lookBlock.setMeta(Math.abs(lookBlock.meta)*(leftFlow?-1:1));
			
			
		}
		//if (currentMeta == 0) break;
		//if level > max, change to air
		int maxFluidLevel = 7;
		if (lookBlock.blockID == 11) maxFluidLevel = 4;//for lava!
		if (Math.abs(lookBlock.meta) > maxFluidLevel){
			lookBlock.set(0,0);
			//Gdx.app.log("WATERUPDATE", "unflow");
		} else{
		//check outward flow
		//only if less than max
		
	
		//find shortest fall
		//go left first
//					tmpTotal = 0;//length left
		boolean lookDone = false;
//					int lookX = 0;
		int leftLength = 0;
		int rightLength = 0;
		boolean badLeft = false;
		boolean badRight = false;
		boolean goodLeft = false;
		boolean goodRight = false;
		boolean goodBottom = false;
		int tmpTotal = 0;
		
		//maybe look underneath here
		tmpB.set(currentBlock.x, currentBlock.y-1);
		tmpBlock = map.getBlock(tmpB.x, tmpB.y);
		goodBottom = (tmpBlock.blockType() != 0 && tmpBlock.blockType() != 1);
		if (tmpBlock.blockType() ==0)
		{
			//Gdx.app.log("WATERUPDATE", "waterunder, ending "+lookBlock);
			map.changeBlock(tmpB.x, tmpB.y, fluidBlock, (leftFlow?-1:1), true );
			map.addTimedUpdate(tmpB);
			return;
		}
		
		
		tmpB.set(currentBlock.x-1, currentBlock.y);
		tmpBlock = map.getBlock(tmpB.x, tmpB.y);
		if (tmpBlock.blockType() >=1)
			badLeft = true;
		else {
			tmpBlock = map.getBlock(tmpB.x, tmpB.y-1);
			if (tmpBlock.blockType() ==0)
				goodLeft = true;
		}
			
		tmpB.set(currentBlock.x+1, currentBlock.y);
		tmpBlock = map.getBlock(tmpB.x, tmpB.y);
		if (tmpBlock.blockType() >=1)
			badRight = true;
		else {
			tmpBlock = map.getBlock(tmpB.x, tmpB.y-1);
			if (tmpBlock.blockType() ==0)
				goodRight = true;
		}
		
		
		
		
		
		if ( badLeft && badRight) 
			{
			//Gdx.app.log("WATERUPDATE", "badbpthsodes, ending "+lookBlock);
				//System.out.println("quitting");
				return;
			}
		if (!badLeft && goodBottom){//flow left
			tmpB.set(currentBlock.x-1, currentBlock.y);
			map.changeBlock(tmpB.x, tmpB.y, fluidBlock,-(Math.abs(lookBlock.meta)+1), true);
			map.addTimedUpdate(tmpB);
			//Gdx.app.log("WATERUPDATE", "flow left");
		}
		if (!badRight && goodBottom){//flow right
			tmpB.set(currentBlock.x+1, currentBlock.y);
			map.changeBlock(tmpB.x, tmpB.y, fluidBlock,lookBlock.meta+1, true);
			map.addTimedUpdate(tmpB);
			//Gdx.app.log("WATERUPDATE", "flow right");
		}
		
		}
		
		//Gdx.app.log("WATERUPDATE", "end "+lookBlock);
	
		
	}

	@Override
	public void unscrub(PunkMap map, int x, int y, Block b, Chunk c, boolean placing) {
		map.addTimedUpdate(x+c.xOffset, y+c.yOffset);
		
	}

	@Override
	public void place(PunkMap map, int x, int y, int beforeID, int beforeMeta,
			boolean propagate, Block b, boolean left) {
		if (beforeID == 11) {// water flowing into lava
			map.getBlock(x, y).set(62, 0);
		}
		map.addUpdate(x - 1, y);
		map.addUpdate(x + 1, y);
		map.addUpdate(x, y - 1);
		map.addUpdate(x, y + 1);
		map.addTimedUpdate(x, y);
		
	}

	@Override
	public void destroy(BlockDamageType dType, int mapX, int mapY, int p,
			float angle, PunkMap map, int id, int meta) {
		// TODO Auto-generated method stub
		
	}

}

package com.niz.punk;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.IntMap.Entry;

public class BlockUpdater {
	private static final int UPDATES_MIN_CAPACITY = 2000;
	private String TAG = "updater";
	private BlockLoc tmpB;
	private BlockLoc currentBlock ;
	private BlockLoc lastBlock;
	PunkBlockList blockUpdateList = new PunkBlockList(UPDATES_MIN_CAPACITY);
	PunkBlockList timedUpdateList = new PunkBlockList(1000);
	BlockListDamage blockDestructionList = new BlockListDamage(200);
	PunkBlockList treeSourceList = new PunkBlockList(50);
	private int totalMeta, currentMeta, smallestMeta, tmpMeta;
	private Vector2 tmpV = new Vector2(0,0);
	private Block tmpBlock = new Block(0,0);
	private Block lookBlock = new Block(0,0);
	private int tmpTotal, tmpI;
	private boolean lookDone = false;
	private PunkMap map;
	int lookX;
	int lookY;
	boolean fucked = false;
	private static final int  LIGHTREPS = 29, LIGHTMAX = 3400;
	private PunkBodies l_mi;
	private World l_world;

	public BlockUpdater(PunkMap map, World world, PunkBodies mi){
		this.l_mi = mi;
		this.l_world = world;
		this.map = map;
		tmpB = new BlockLoc();
		currentBlock = new BlockLoc();
		lastBlock = new BlockLoc();
		//lightUpdateList.list.ensureCapacity(200000);
		//timedUpdateList.list.ordered = true;
		//dayLightUpdateList.list.ordered = true;
		//lightUpdateList.list.ordered = true;

	}	
		
	
	public void addToTreeSourceList(int x, int y){
		treeSourceList.addBlock(x,y);
	}
	public void addToUpdateList(int x, int y){
			blockUpdateList.addBlock(x, y);
	}
	public void addToDestructionList2(int x, int y){
		//blockDestructionList.addBlock(x,y);
	}
	
	public void addLoc(BlockLoc loc){
		blockUpdateList.addBlock(loc.x, loc.y);
	}
	public void addLocT(BlockLoc loc){
		timedUpdateList.addBlock(loc.x, loc.y);
	}
	
	public void addToTimedUpdateList(int x, int y){
		timedUpdateList.addBlock(x,y);
	}
	private boolean updateFlag = false;
	
	public int lineLength(int x2, int y2){
		int length = 0;
			int x=0;
			int y=0;
		    int w = x2 - x ;
		    int h = y2 - y ;
		    int dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0 ;
		    if (w<0) dx1 = -1 ; else if (w>0) dx1 = 1 ;
		    if (h<0) dy1 = -1 ; else if (h>0) dy1 = 1 ;
		    if (w<0) dx2 = -1 ; else if (w>0) dx2 = 1 ;
		    int longest = Math.abs(w) ;
		    int shortest = Math.abs(h) ;
		    if (!(longest>shortest)) {
		        longest = Math.abs(h) ;
		        shortest = Math.abs(w) ;
		        if (h<0) dy2 = -1 ; else if (h>0) dy2 = 1 ;
		        dx2 = 0 ;            
		    }
		    int numerator = longest >> 1 ;
		    for (int i=0;i<=longest;i++) {
		        length+=1;
		        numerator += shortest ;
		        if (!(numerator<longest)) {
		            numerator -= longest ;
		            x += dx1 ;
		            y += dy1 ;
		        } else {
		            x += dx2 ;
		            y += dy2 ;
		        }
		    }
		return length;
	}
	public int getBitmask(int id, int alternateID, BlockLoc aBlock, Block b, PunkMap map){
		int bitmask = 0;
		
		if (map.getBlock(aBlock.x, aBlock.y+1).blockID == id)bitmask +=1;
		if (map.getBlock(aBlock.x+1, aBlock.y).blockID == id)bitmask +=2;
		if (map.getBlock(aBlock.x, aBlock.y-1).blockID == id)bitmask +=4;
		if (map.getBlock(aBlock.x-1, aBlock.y).blockID == id)bitmask +=8;
		//alternate
		if (map.getBlock(aBlock.x, aBlock.y+1).blockID == alternateID)bitmask +=16;
		if (map.getBlock(aBlock.x+1, aBlock.y).blockID == alternateID)bitmask +=32;
		if (map.getBlock(aBlock.x, aBlock.y-1).blockID == alternateID)bitmask +=64;
		if (map.getBlock(aBlock.x-1, aBlock.y).blockID == alternateID)bitmask +=128;
		return bitmask-128;
	}
	
	public int getLeafBitmask(int id, int woodID, BlockLoc aBlock, Block b, PunkMap map){
		int bitmask = 0;
		Block t;
		if (map.getBlock(aBlock.x, aBlock.y+1).blockID == id)bitmask +=1;
		if (map.getBlock(aBlock.x+1, aBlock.y).blockID == id)bitmask +=2;
		if (map.getBlock(aBlock.x, aBlock.y-1).blockID == id)bitmask +=4;
		if (map.getBlock(aBlock.x-1, aBlock.y).blockID == id)bitmask +=8;
		//alternate
		t = map.getBlock(aBlock.x, aBlock.y+1);
		if (t.blockID == woodID )bitmask +=16;
		t = map.getBlock(aBlock.x+1, aBlock.y);
		if (t.blockID == woodID )bitmask +=32;
		t = map.getBlock(aBlock.x, aBlock.y-1);
		if (t.blockID == woodID )bitmask +=64;
		t = map.getBlock(aBlock.x-1, aBlock.y);
		if (t.blockID == woodID )bitmask +=128;
		//(t.meta&15) == 2 || (t.meta&15) == 4 || (t.meta&15) == 8
		return bitmask-128;
	}
	
	public void updateBitmasked(int alternateID, BlockLoc aBlock, PunkMap map, boolean propagate){
		Block b = map.getBlock(aBlock);
		int bitmask = getBitmask(b.blockID, alternateID, aBlock, b, map);
		//x.app.log("updater", "bitmask"+bitmask);
		if (bitmask != b.meta){
			b.setMeta(bitmask);	
			map.chunkPool.setModified(aBlock);
			if (propagate){
				
				//Gdx.app.log("upodater", "add around"+bitmask+" met: "+b.meta);
				addToUpdateList(aBlock.x-1, aBlock.y);
				addToUpdateList(aBlock.x+1, aBlock.y);
				addToUpdateList(aBlock.x, aBlock.y+1);
				addToUpdateList(aBlock.x, aBlock.y-1);
			}
			
		}
	}
	
	public void updateBitmaskedLeaves(int alternateID, BlockLoc aBlock, PunkMap map, boolean propagate){
		Block b = map.getBlock(aBlock);
		int bitmask = getLeafBitmask(b.blockID, alternateID, aBlock, b, map);
		//x.app.log("updater", "bitmask"+bitmask);
		if (bitmask != b.meta){
			map.chunkPool.setModified(aBlock);
			b.setMeta(bitmask);
			if (propagate){
				
				//Gdx.app.log("upodater", "add around"+bitmask+" met: "+b.meta);
				addToUpdateList(aBlock.x-1, aBlock.y);
				addToUpdateList(aBlock.x+1, aBlock.y);
				addToUpdateList(aBlock.x, aBlock.y+1);
				addToUpdateList(aBlock.x, aBlock.y-1);
			}
			
		}
	}
	
	public void updatePineLeaves(BlockLoc aBlock, PunkMap map, boolean propagate){
		Block b = map.getBlock(aBlock);
		int bitmask = getBitmask(b.blockID, 14, aBlock, b, map);
		//x.app.log("updater", "bitmask"+bitmask);
		if (bitmask != b.meta){
			b.setMeta(bitmask);		
			map.chunkPool.setModified(aBlock);
			if (propagate){
				
			
				addToUpdateList(aBlock.x-1, aBlock.y);
				addToUpdateList(aBlock.x+1, aBlock.y);
				addToUpdateList(aBlock.x, aBlock.y+1);
				addToUpdateList(aBlock.x, aBlock.y-1);
			}
			
		}
	}
	
	/*private void updateLeavesTimed(int woodID, int leafID, int meta, BlockLoc aBlock, int size, PunkMap map) {
		updateFlag = false;
		if (meta == 15) return;
		for (int lookX = -size; lookX <=size; lookX++)
					for (int lookY = -size; lookY <= size; lookY++)
				{
					if (map.getBlock(aBlock.x + lookX,aBlock.y+lookY).blockID == woodID)
						updateFlag = true;
					
				}
				if (!updateFlag)//if needs to be destroyed 
				{
					map.destroyBlock(true, aBlock.x,aBlock.y, MathUtils.random(360), PunkMap.currentPlane);
					addTimedUpdatesSurrounding(aBlock);
				}
		
	}*/


	public void addTimedUpdatesSurrounding(BlockLoc l) {
		addToTimedUpdateList(l.x, l.y+1);
		addToTimedUpdateList(l.x, l.y-1);
		addToTimedUpdateList(l.x+1, l.y);
		addToTimedUpdateList(l.x-1, l.y);
	}


	public void updateLeaves(int woodID, int leafID, BlockLoc aBlock, int size, PunkMap map, boolean propagate){
		
		//Gdx.app.log("blockupdater", "leafupdate");
		{
			//meta, bitmask
			updateBitmaskedLeaves(woodID, aBlock, map, propagate);
			//int bitmask = map.getBlock(aBlock).meta;
			/*
			int newMeta = 0;
			
				
				}
			else if (leafID == 54)
				newMeta = bitmask;
				
			map.getBlock(aBlock).setMeta(newMeta);*/
		}
		
		
		
	}
	public int maxLUpdates, maxDUpdates;
	boolean logging = false;
	public void updateSky(){
		//if (lightUpdateList.size() > maxLUpdates)
		//	maxLUpdates = lightUpdateList.size();
		//if (dayLightUpdateList.size() > maxDUpdates)
		//	maxDUpdates = dayLightUpdateList.size();
		//update rays
		/*if (lightQueue.size > 0){
			if (allChunksLoaded)
				for (int m = 0; m < Math.min(lightQueue.size,lightRepeats); m++)
					doLightRays(timeOfDay, lightQueue.pop());
		} 
		//new method, do all at once, fuck it!*/

		/*if (allChunksLoaded && allChunksPostFetched && allTreesGrown &&lightQueue.size > 0){
			Gdx.app.log("map", "STARTING LIGHT RAYS");
			int x =  lightQueue.pop();
			doLightRays(timeOfDay, x);
			int leftX=x, rightX=x;
			while (lightQueue.size > 0){
				x =  lightQueue.pop();
				doLightRays(timeOfDay,x);
				if (x < leftX) leftX = x;
				if (x > rightX) rightX = x;
			}
			doTorchColumn(leftX-torchRange, rightX+torchRange);*/		
			
			//G
			//if (sunlightQueue.size > 0 && allChunksLoaded && allChunksPostFetched){
			//	makeSunlightColumn(sunlightQueue.pop());
			//}
			
			//if (!map.allChunksLoaded){
				//updateSkyPartial();
			//	return;
			//}
			//Punk.genericAirBlock.setDayLight(0);
			//Punk.genericAirBlock.setLight(0);
			//Gdx.app.log("map", "update sky "+lightUpdateList.size() + " day " + dayLightUpdateList.size());
			int count = 0;
			//int listSize = lightUpdateList.size();
			
			//if (Punk.justStarted) {}
			//else if (listSize < 800)
			//if (listSize < LIGHTMAX)
				; 
			//else
			//if (!Punk.processing){
			//	if (dayLightUpdateList.size() > LIGHTREPS/2)
			//		listSize = LIGHTREPS/2;
			//	else listSize = LIGHTREPS-Math.min(dayLightUpdateList.size(), LIGHTREPS/2);
				//Gdx.app.log("map", "STARTING LIGHT RAYS "+listSize + "  "+ lightUpdateList.size());
			//} else listSize = lightUpdateList.size();
			//else listSize =Math.min(800, listSize);
			//
			
			//Gdx.app.log(TAG, "l "+listSize);
			//if (lightUpdateList.size()>0)Gdx.app.log("map", "updatesky 1 step, size:"+lightUpdateList.size());

			while (count < LIGHTREPS && !lightUpdateList.list.isEmpty()){
				
				BlockLoc loc = lightUpdateList.removeFirst();
				
								
				boolean valid = !isOnEdge(loc);
				if (valid)//if it's a valid block
					doLightUpdateNormal(loc.x,loc.y);
				lightUpdateList.free(loc);
				count++;
			}//*/
			count = 0;
			//listSize = dayLightUpdateList.size();
			//if (listSize != 0) Gdx.app.log("main", "light list size:"+listSize);
			//if (Punk.justStarted) {}
			//else if (listSize < 100000)
			//if (!Punk.processing){
				//if (lightUpdateList.size() > 0)
				//	listSize = LIGHTREPS/2;
				//Gdx.app.log("map", "STARTING DAYLIGHT RAYS "+listSize + "  "+dayLightUpdateList.size());

				//if (listSize < LIGHTREPS/2)
				//	listSize = LIGHTREPS-listSize;
				//else listSize = LIGHTREPS/2;
			//} else listSize = dayLightUpdateList.size();
			
			//Gdx.app.log(TAG, "d "+listSize);
			//listSize = 257;
			//if (map.allChunksLoaded && map.allChunksPostFetched)
			//if (dayLightUpdateList.size()>0)Gdx.app.log("map", "updateskyday 1 step, size:"+lightUpdateList.size());

			while ( count < LIGHTREPS && !dayLightUpdateList.list.isEmpty()){
				
				BlockLoc loc = dayLightUpdateList.removeFirst();//removeIndex(0);
				
				boolean valid = !isOnEdge(loc); 
						
						
				if (valid){//if it's a valid block
					doLightUpdateDayLight(loc.x,loc.y);
					if (logging) Gdx.app.log(TAG, "light update "+loc +map.getBlock(loc));
				}
				dayLightUpdateList.free(loc);
				count++;
			}//*/
			
			
				
			
			
			
			
		}//*/
		int partInc, partDInc;
		
		public byte[][] lightMatrix = new byte[3][3], dayLightMatrix = new byte[3][3];
		
		public byte getHighestLight(int x, int y, boolean sun){
			byte highest = 0;
			Block dayBlock, block;
			
			for (int i = -1; i <=1; i++)
				for (int j = -1; j <= 1; j++){
					block = map.getBlock(x+i, y+j);//.light
					lightMatrix[1+i][1+j] = block.getLight();
					dayLightMatrix[1+i][1+j] = block.getDayLight();
				}
					
			
			byte l =sun?dayLightMatrix [0][1]:lightMatrix[0][1];
			if (l > highest)highest =l;
			
			l = sun?dayLightMatrix[2][1]:lightMatrix[2][1];
			if (l > highest)highest =l;
			
			l = sun?dayLightMatrix[1][2]:lightMatrix[1][2];
			if (l > highest)highest =l;
			
			l = sun?dayLightMatrix[1][0]:lightMatrix[1][0];
			if (l > highest)highest =l;
			//Gdx.app.log("updater. highest light", ""+highest);
			return highest;
		}
		
		private boolean canAddSurr;
		public void doLightUpdateNormal(int x, int y){}
		
		public void doLightUpdateDayLight(int x, int y){
			canAddSurr = true;
			//sunlight propogates down
			
			byte topL;
			//Gdx.app.log("map", "update@ "+x+","+y);
			Chunk c = map.chunkPool.getChunkWorld(x,y);
			
			if (c == null){
				return;
			}
			
			Block tmpB = null;//c.block[x&Punk.CHUNKSIZEMASK][y&Punk.CHUNKSIZEMASK];
			byte currentLight = tmpB.getDayLight(), highestL;
			boolean isOnEdge = //(x == map.LEFTMOSTBLOCK+1 || x == map.RIGHTMOSTBLOCK-1 || y == map.TOPBLOCK-1 || y == map.BOTTOMBLOCK+1);
			(!map.chunkPool.hasChunk(x-1,y) ||
			!map.chunkPool.hasChunk(x+1,y)  ||
			!map.chunkPool.hasChunk(x,y+1) ||
			!map.chunkPool.hasChunk(x,y-1));
			if (tmpB == Punk.genericAirBlock)Gdx.app.log("map", "update@ "+x+","+y);
			
			map.getBlockDef(tmpB.blockID).updateDayLight(x, y, tmpB, c, null, null);

			
		}
		/*public void flushLight2(int x, int y, int r, int endY, boolean dayLight, boolean torchLight){
			Gdx.app.log("map", "flush light "+x+","+y+"r:"+r+" endY"+endY);
			for (int mapX = x-r; mapX < x+r; mapX++)
				for (int mapY = y+r; mapY > endY-r; mapY--){
					//if (getBlock(mapX+x, mapY+y).blockID != 50)
					
					if (dayLight){
						map.getBlock(mapX, mapY).dayLight = 0;//mapX+x, mapY+y,(byte)0);
						dayLightUpdateList.addBlock(mapX,  mapY);
					}
					if (torchLight){
						map.getBlock(mapX, mapY).light = 0;//mapX+x, mapY+y,(byte)0);
						lightUpdateList.addBlock(mapX,  mapY);
					}
					
				}
		}*/
		
		public void updateBlockLight(int x, int y){
			
			lightUpdateList.addBlock(x,y);
			dayLightUpdateList.addBlock(x, y);

			
		}

	
	public void updateDestruction(PunkMap map, int max, long time){
		int total = Math.min(blockDestructionList.list.size, max);
		lastBlock.x = 0;
		lastBlock.y = 0;
		

		//System.out.println("timed update, size:" + total);
		if (total >0){
			//Gdx.app.log("updater", "destruction update:"+total);
			for (int i = 0; i < total; i++){

				BlockLocDamage loc =  blockDestructionList.removeFirst();//list.removeIndex(MathUtils.random(blockDestructionList.size()-1));
				//update the block here
				//System.out.println(""+currentBlock.x);
				currentMeta = map.getBlock(loc.x,loc.y).getMeta();
				lookBlock = map.getBlock(loc.x,loc.y);
				//System.out.print("timed:" + lookBlock.blockID+ " ");
				//map.damageBlock(loc.type, l_world, currentBlock.x, currentBlock.y, loc.damage, l_mi, Punk.gTime, PunkMap.currentPlane);
				blockUpdateList.free(currentBlock);
			}	
		}
			
	}
	
	public void checkInFlowAirold(int x, int y, Block b, PunkMap map){
		int bitmask = getBitmask(12, -3, currentBlock, lookBlock, map);
		if ((bitmask    &1)==1){
			b.set(12, 1);
			addSurroundingT(x,y);
			return;
		}//on top
		
		
		if (((bitmask >>1)   &1)==1 && ((bitmask >>2)   &1)!=1){
			b.set(12, (map.getBlock(x+1,y).meta+1));
			if (b.meta > 8){
				b.set(0,0);
			}
			addSurroundingT(x,y);
			return;
		}//right
		
		
		if (((bitmask >>2)   &1)==1){}//bottom
		
		
		if (((bitmask >>3)   &1)==1  && ((bitmask >>2)   &1)!=1){
			b.set(12, +map.getBlock(x-1,y).meta+1);
			if (b.meta > 8){
				b.set(0,0);
			}
			addSurroundingT(x,y);
			return;
		}//left
		
	}
	public void checkFlowWater(int x, int y, Block b, PunkMap map){
		//if (true)return;
		if (b.meta == 0) return;
		int bitmask = getBitmask(12, 0, currentBlock, lookBlock, map);
		int lowestSurr = 9;
		if (((bitmask )   &1)==1){
			b.set(12, 1);
			addSurroundingT(x,y);
			return;
		}//on top
		
		if (((bitmask >>2 )  &1)==1){
			/*Block bottomB = map.getBlock(x,y-1);
			if (bottomB.meta > 1){
				int givingM = 8-bottomB.meta;
				givingM = Math.min(givingM, 7-b.meta);
				//b.meta += givingM;
				b.set(b.blockID, b.meta+givingM);
				//bottomB.meta -= givingM;
				bottomB.set(bottomB.blockID, bottomB.meta-givingM);
				
				if (b.meta > 8) 
					b.set(0,0);
				addSurroundingT(x,y);
			}*/
			//map.changeBlock(x, y, 0, 0);
			//b.setMeta(7);
			return;
		}//bottom
		
		
		if (((bitmask >>1)   &1)==1){
			int newMeta = map.getBlock(x+1,y).meta;
			//Gdx.app.log("updater, water ", ""+newMeta+" bid "+map.getBlock(x+1,y).blockID + " x "+x+" y "+y);
			
			lowestSurr = Math.min(lowestSurr, newMeta);
			
		}//right
		
		
		
		
		if (((bitmask >>3)   &1)==1){
			//map.getBlock(x-1,y).meta+1);
			int newMeta = map.getBlock(x-1,y).meta;
			lowestSurr = Math.min(lowestSurr, newMeta);
		}//left
		
		if (b.meta != lowestSurr && b.meta != 0 && ((bitmask >>2 &1) !=1) && ((bitmask >>6 &1) !=1)){
			b.set(12, lowestSurr+1);
			if (b.meta > 8){
				b.set(0,0);
				addSurroundingT(x,y);
			}
		}
		
	}
	private void addSurrounding(int x, int y) {
		addToUpdateList(x-1, y);
		addToUpdateList(x+1, y);
		addToUpdateList(x, y-1);
		addToUpdateList(x, y+1);
	}
	private void addSurroundingT(int x, int y) {
		addToTimedUpdateList(x-1, y);
		addToTimedUpdateList(x+1, y);
		addToTimedUpdateList(x, y-1);
		addToTimedUpdateList(x, y+1);
	}


	private boolean isOnEdge(BlockLoc loc) {
		boolean valid = true;
		int ax = loc.x & Punk.CHUNKSIZEMASK, ay = loc.y & Punk.CHUNKSIZEMASK;
		if (!map.chunkPool.hasChunk(loc)) return true;
		if (ax == 0){//left
			if (!map.chunkPool.hasLeft(loc))
				valid = false;
		} else if (ax == Punk.CHUNKSIZE-1){//right
			if (!map.chunkPool.hasRight(loc))
				valid = false;
		}
		if (ay == 0){//bottom
			if (!map.chunkPool.hasDown(loc))
				valid = false;
		} else if (ay == Punk.CHUNKSIZE-1){//top
			if (!map.chunkPool.hasUp(loc))
				valid = false;
		}
		return !valid;
	}


	public void checkInFlowAir(int fID, BlockLoc currentBlock, Block b, PunkMap map) {
		tmpMeta = 0;
		smallestMeta = 7;
		boolean leftFlow = (currentMeta < 0);
		currentMeta = Math.abs(7);
		boolean  tmpInFlow;
		int fluidBlock = fID;
		if (currentMeta == 0) return;
		tmpB.set(currentBlock.x, currentBlock.y+1);
		tmpBlock = map.getBlock(tmpB.x,tmpB.y);
		if (tmpBlock.blockID == fluidBlock){
			lookBlock.set(fluidBlock, 1);
			addLocT(currentBlock);
			//addToTimedUpdateList(currentBlock.x-1, currentBlock.y);
			//addToTimedUpdateList(currentBlock.x+1, currentBlock.y);
			//addToTimedUpdateList(currentBlock.x, currentBlock.y+1);
			//addToTimedUpdateList(currentBlock.x, currentBlock.y-1);
		}
		//otherwise change to 1 higher than surrounding blocks
		else if (map.getBlock(currentBlock.x, currentBlock.y-1).blockType() >1){
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
					if (tmpBlock.blockID == fluidBlock)
					{
						if (tmpMeta < smallestMeta){
							smallestMeta = tmpMeta;
							leftFlow = tmpInFlow;
							//fluidBlock = tmpBlock.blockID;
						}
						
					}
				}
			
			//if (currentMeta != 0 && smallestMeta != 9){
				
				//addLocT(currentBlock);
			if (smallestMeta != 7){
				b.set(fluidBlock, smallestMeta +1);
				//Gdx.app.log("WATERUPDATE, checkairinflow", "flowing in, left:"+leftFlow+"current:"+currentMeta+"smallest:"+smallestMeta);
				//lookBlock.setMeta(biggestMeta - 1);
				addLocT(currentBlock);
				//addToTimedUpdateList(currentBlock.x-1, currentBlock.y);
				//addToTimedUpdateList(currentBlock.x+1, currentBlock.y);
				//addToTimedUpdateList(currentBlock.x, currentBlock.y+1);
				//addToTimedUpdateList(currentBlock.x, currentBlock.y-1);
				//Gdx.app.log("updater", "WATER REDUCING, adding updates");
			}
			//else 
			
		
		//put back direction
		b.setMeta(Math.abs(b.meta)*(leftFlow?-1:1));
		
		
		}
		//if level > max, change to air
		int maxFluidLevel = 7;
		if (b.blockID == -3) maxFluidLevel = 4;//for lava!
		if (Math.abs(b.meta) > maxFluidLevel-1){
			b.set(0,0);
			//Gdx.app.log("WATERUPDATE", "unflow");
		} else{}
		//check outward flow
		
	}

	public int maxUpdates = 0;
	public boolean updateBlocks(int count, PunkMap map){
		//System.out.println("queue size:" + blockUpdateList.size()());
		//these updates will be called when the player breaks a block, or  when a lbock "wakes up"
		
		
		//Gdx.app.log(TAG, "size:"+blockUpdateList.size());
		boolean flipped = false;
		//count = blockUpdateList.size();
		//for (int i = 0; i<count; i++)
		while (!blockUpdateList.list.isEmpty()){

			currentBlock= blockUpdateList.removeFirst();
			if (blockUpdateList.prog == 0) flipped = true;
			if (!map.chunkPool.hasChunk(currentBlock)){
				blockUpdateList.free(currentBlock);
				continue;
			}
			//update the block here
			//System.out.println(""+currentBlock.x);
			
			//Block b = map.getBlock(currentBlock);
			int x = currentBlock.x, y = currentBlock.y;
			Chunk c = map.chunkPool.getChunkWorld(x,y);
			
			if (c == null){
				return false;
			}
			
			Block b = null;//c.block[x&Punk.CHUNKSIZEMASK][y&Punk.CHUNKSIZEMASK];
			updateBlock(currentBlock, b, map, true);
			//c.modified = true;
			
			updateBlockLight(currentBlock.x,currentBlock.y);
			//if (fucked) Gdx.app.log("updater, fin ", " "+currentBlock+lookBlock);
			//blockUpdateList.removeLast();
			blockUpdateList.free(currentBlock);
			
		}
		//if (blockUpdateList.size() == 0) flipped = true;
		//Gdx.app.log(TAG, "flipped:"+flipped);
		return blockUpdateList.list.isEmpty();
	}
	
	public void updateBlock(BlockLoc currentBlock, Block block, PunkMap map,
			 boolean propagate) {
		//currentMeta = map.getBlock(currentBlock.x,currentBlock.y).getMeta();
		lookBlock = block;
		currentMeta = lookBlock.meta;
		//Gdx.app.log("updater", "update"+lookBlock);
		map.getBlockDef(lookBlock.blockID).update(currentBlock, block, map, propagate);
		if (false)switch(lookBlock.blockID){
		
		
		
		
		case 88:
				
			break;
		case 10:
			updateBitmaskedLeaves(10, currentBlock, map, propagate);
			break;
		case 13://updateLeaves(15, 13, currentBlock, 6, map, propagate);
			
		case 14://updateLeaves(16,14,currentBlock, 10, map, propagate);
				//updateBitmaskedLeaves(16, currentBlock, map);
			
		case 55://updateLeaves(54, 55, currentBlock, 6, map, propagate);
			
		case 54: //updateBitmasked(54, currentBlock, map, propagate);

		case 18://leaves
				//updateLeaves(17, 18, currentBlock, 4, map, propagate);
			//break;
		case 17:
			//updateBitmasked(18, currentBlock, map);
			updateBitmaskedByType(2, currentBlock, propagate);
			break;
		case -3://llava
		case 12://water
				addLocT(currentBlock);
			break;
		case -2://fire
				addLocT(currentBlock);
			break;
		case 29://snow
		case 20://sand
		case 21://gravel
				//check underneath, if nothing create sticky block
				//and update above
				tmpB.set(currentBlock.x, currentBlock.y-1);
				tmpBlock = map.getBlock(tmpB);
				if (tmpBlock.blockID == 0){
					//map.destroyBlock(true, currentBlock.x, currentBlock.y, 0, time);
					
					PhysicsActor stick = map.chunkActors.add(23, lookBlock.blockID, l_world, l_mi, tmpV.set(currentBlock.x, currentBlock.y));
					map.getBlock(currentBlock).set(0,0);
					stick.health = 20;
					stick.state = 0;
					stick.lastx = currentBlock.x;
					stick.lasty = currentBlock.y+1;
					if (propagate){
						addToUpdateList(currentBlock.x, currentBlock.y+1);
						addToUpdateList(currentBlock.x, currentBlock.y-1);
						addToUpdateList(currentBlock.x+1, currentBlock.y);
						addToUpdateList(currentBlock.x-1, currentBlock.y);
					}
					
				}
			break;
		
		case 41://bed
			boolean left = currentMeta%2 == 0;
			tmpB.set(currentBlock.x+(left?1:-1), currentBlock.y);
			tmpBlock = map.getBlock(tmpB);
			if (tmpBlock.blockID != 41 || map.getBlock(currentBlock.x, currentBlock.y-1).blockType() == 0) {
				lookBlock.set(0,0);
				if (left) map.createItem(41, 1, 0, 0, currentBlock.x, currentBlock.y);
				if (propagate){
					addToUpdateList(currentBlock.x, currentBlock.y+1);
					addToUpdateList(currentBlock.x, currentBlock.y-1);
					addToUpdateList(currentBlock.x+1, currentBlock.y);
					addToUpdateList(currentBlock.x-1, currentBlock.y);
				}
			}
			
			break;
				
		}
		
	}


	/*public void updateTimed(PunkMap map, int reps){
			int total = reps;// Math.min(timedUpdateList.size(), reps);
			lastBlock.x = 0;
			lastBlock.y = 0;
			//System.out.println("timed update, size:" + total);
			if (total >0)
				for (int i = 0; i < total; i++){
					currentBlock= timedUpdateList.removeOrdered();
					if (isOnEdge(currentBlock)){
						
						blockUpdateList.free(currentBlock);
						continue;
					}
					
					//update the block here
					//System.out.println(""+currentBlock.x);
					currentMeta = map.getBlock(currentBlock.x,currentBlock.y).getMeta();
					lookBlock = map.getBlock(currentBlock.x,currentBlock.y);
					// Gdx.app.log("updater", "updatingT "+lookBlock.blockID);
					//System.out.print("timed:" + lookBlock.blockID+ " ");
					map.getBlockDef(lookBlock.blockID).timedUpdate(currentBlock, lookBlock, map);
					
					blockUpdateList.free(currentBlock);
				}	
		}*/


	public void updateBitmaskedByType(int bt, BlockLoc aBlock,
			boolean propagate) {
		
		int bitmask = 0;
		Block t;
		if (map.getBlock(aBlock.x, aBlock.y+1).blockType() == bt)bitmask +=1;
		if (map.getBlock(aBlock.x+1, aBlock.y).blockType() == bt)bitmask +=2;
		if (map.getBlock(aBlock.x, aBlock.y-1).blockType() == bt)bitmask +=4;
		if (map.getBlock(aBlock.x-1, aBlock.y).blockType() == bt)bitmask +=8;
		
		//(t.meta&15) == 2 || (t.meta&15) == 4 || (t.meta&15) == 8
		//return bitmask;
		
		
		Block b = map.getBlock(aBlock);
		//int bitmask = getLeafBitmask(b.blockID, alternateID, aBlock, b, map);
		//x.app.log("updater", "bitmask"+bitmask);
		if (bitmask != b.meta){
			map.chunkPool.setModified(aBlock);
			b.setMeta(bitmask);
			if (propagate){
				
				//Gdx.app.log("upodater", "add around"+bitmask+" met: "+b.meta);
				addToUpdateList(aBlock.x-1, aBlock.y);
				addToUpdateList(aBlock.x+1, aBlock.y);
				addToUpdateList(aBlock.x, aBlock.y+1);
				addToUpdateList(aBlock.x, aBlock.y-1);
			}
			
		}
		
	}


	public int getSolidBitmask(BlockLoc aBlock, boolean propagate) {
		
		int bitmask = 0;
		int bt = 1;
		Block t;
		if (
				//map.getBlock(aBlock.x, aBlock.y+1).blockType() == bt ||
				map.getBlock(aBlock.x, aBlock.y+1).blockType() >63)
			bitmask +=1;
		if (
				//map.getBlock(aBlock.x+1, aBlock.y).blockType() == bt || 
				map.getBlock(aBlock.x+1, aBlock.y).blockType() >63)
			bitmask +=2;
		if (
				//map.getBlock(aBlock.x, aBlock.y-1).blockType() == bt || 
				map.getBlock(aBlock.x, aBlock.y-1).blockType() >63)
			bitmask +=4;
		if (
				//map.getBlock(aBlock.x-1, aBlock.y).blockType() == bt || 
				map.getBlock(aBlock.x-1, aBlock.y).blockType() >63)
			bitmask +=8;
		
		//(t.meta&15) == 2 || (t.meta&15) == 4 || (t.meta&15) == 8
		//return bitmask;
		
		
		/*Block b = map.getBlock(aBlock);
		//int bitmask = getLeafBitmask(b.blockID, alternateID, aBlock, b, map);
		//x.app.log("updater", "bitmask"+bitmask);
		if (bitmask != b.meta){
			map.chunkPool.setModified(aBlock);
			b.setMeta(bitmask);
			if (propagate){
				
				//Gdx.app.log("upodater", "add around"+bitmask+" met: "+b.meta);
				addToUpdateList(aBlock.x-1, aBlock.y);
				addToUpdateList(aBlock.x+1, aBlock.y);
				addToUpdateList(aBlock.x, aBlock.y+1);
				addToUpdateList(aBlock.x, aBlock.y-1);
			}
			
		}*/
		return bitmask;
	}


	
	

	private boolean isInBounds(BlockLoc loc) {
		return ((currentBlock.x > map.LEFTMOSTBLOCK && currentBlock.x < map.RIGHTMOSTBLOCK &&
					currentBlock.y >map.BOTTOMBLOCK && currentBlock.y < map.TOPBLOCK));
	}


	public void destroyAll() {
		blockUpdateList.clear();
		timedUpdateList.clear();
		treeSourceList.clear();
	}
	
	public PunkBlockList 
	lightUpdateList = new PunkBlockList(2000), 
	dayLightUpdateList = new PunkBlockList(2000);

	public boolean lightFinished() {
		return (dayLightUpdateList.list.isEmpty() && lightUpdateList.list.isEmpty());
	}


	/*public void removeDupes() {
		dayLightUpdateList.removeDupes();
		lightUpdateList.removeDupes();
		blockUpdateList.removeDupes();
		timedUpdateList.removeDupes();
	}*/
	public void clear(){
		dayLightUpdateList.clear();
		lightUpdateList.clear();
		blockUpdateList.clear();
		timedUpdateList.clear();
	}


	public void addLightUpdatesSurrounding(int x, int y, boolean day, Chunk c){
		addLightUpdatesSurrounding(x,y,day);
		c.modified = true;
		
		
	}


	public void addLightUpdatesSurrounding(int x, int y, boolean day) {
		if (!canAddSurr) return;
		PunkBlockList list = (day?dayLightUpdateList:lightUpdateList);
			if (map.chunkPool.hasChunk(x-1,y) ) list.addBlock(x-1, y);
			if (map.chunkPool.hasChunk(x+1,y) ) list.addBlock(x+1, y);
			if (map.chunkPool.hasChunk(x,y+1) ) list.addBlock(x, y+1);
			if (map.chunkPool.hasChunk(x,y-1) ) list.addBlock(x, y-1);
		canAddSurr = false;
		
	}


	
}

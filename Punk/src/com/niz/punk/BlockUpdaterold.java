package com.niz.punk;

import com.badlogic.gdx.math.Vector2;

public class BlockUpdaterold {
	private BlockLoc tmpB;
	private BlockLoc currentBlock ;
	private BlockLoc lastBlock;
	PunkBlockList blockUpdateList = new PunkBlockList();
	PunkBlockList timedUpdateList = new PunkBlockList();
	private int totalMeta, currentMeta, biggestMeta, tmpMeta;
	private Vector2 tmpV = new Vector2(0,0);
	private Block tmpBlock = new Block(0,0);
	private Block lookBlock = new Block(0,0);
	private int tmpTotal, tmpI;
	private boolean lookDone = false;
	
	public BlockUpdaterold(){
		tmpB = new BlockLoc();
		currentBlock = new BlockLoc();
		lastBlock = new BlockLoc();
	}	
	public void addToUpdateList(int x, int y){
				blockUpdateList.addBlock(x, y);
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
	
	public void updateTimed(PunkMap map){
		int total = 1;//timedUpdateList.list.size();
		lastBlock.x = 0;
		lastBlock.y = 0;
		//System.out.println("timed update, size:" + total);
		if (total >0)
			for (int i = 0; i < total; i++){

				//currentBlock= timedUpdateList.list.remove(0);
				//update the block here
				//System.out.println(""+currentBlock.x);
				currentMeta = map.getBlock(currentBlock.x,currentBlock.y).getMeta();
				lookBlock = map.getBlock(currentBlock.x,currentBlock.y);
				//System.out.println("timed block being updated:" + lookBlock.blockID);
				switch(lookBlock.blockID){
				
					
					
				case 8: //water
					//check inward flow
					//check above ,set meta 1 if water
					tmpMeta = 0;
					biggestMeta = 0;
					tmpB.set(currentBlock.x, currentBlock.y+1);
					tmpBlock = map.getBlock(tmpB.x,tmpB.y);
					if (tmpBlock.blockID == 8 || tmpBlock.blockID == 9)
						lookBlock.setMeta(1);
					//otherwise change to 1 higher than surrounding blocks
					else {
						
						for (int lookX = -1; lookX <=1; lookX++)
							for (int lookY = 0; lookY <= 1; lookY++)
								if (lookX == 0 | lookY == 0)
							{
								tmpB.set(currentBlock.x+lookX, currentBlock.y+lookY);
								tmpBlock = map.getBlock(tmpB.x, tmpB.y);
								tmpMeta = tmpBlock.getMeta();
								if (tmpBlock.blockID == 8)
								{
									if (tmpMeta <= biggestMeta) biggestMeta = tmpMeta;
									
								}
							}
						if (currentMeta < biggestMeta )lookBlock.setMeta(Math.max(0,biggestMeta +1));
					}
					
					
					
					
					//if level > max, change to air
					if (lookBlock.getMeta() > 8)
					{
						lookBlock.set(0,0);
					
					} else
						
					
					//check outward flow
					//only if less than max
					{
				
					//find shortest fall
					//go left first
//					tmpTotal = 0;//length left
					lookDone = false;
//					int lookX = 0;
					int leftLength = 0;
					int rightLength = 0;
					boolean badLeft = false;
					boolean badRight = false;
					tmpTotal = 0;
					tmpB.set(currentBlock.x, currentBlock.y);
					while (tmpTotal < 4 && !lookDone)
					{
						tmpB.x-=1;
						tmpTotal +=1;
						tmpBlock = map.getBlock(tmpB.x, tmpB.y);
						if (tmpBlock.blockID == 0)//if left is air, check underneath
						{
							//System.out.println("left start");
							tmpBlock = map.getBlock(tmpB.x, tmpB.y-1);
							if (tmpBlock.blockID == 0 )//if underneath is air
							{
								lookDone = true;
								leftLength = tmpTotal;
								//System.out.println("left done");
							}
						}else {
							if (tmpTotal == 1) 
							{
								//System.out.println("left bad");
								badLeft = true;
								lookDone = true;
							}

						}
					}
					
					tmpTotal = 0;
					tmpB.set(currentBlock.x, currentBlock.y);
					badRight = false;
					while (tmpTotal < 4 && !lookDone)
					{
						tmpB.x+=1;
						tmpTotal +=1;
						tmpBlock = map.getBlock(tmpB.x, tmpB.y);
						if (tmpBlock.blockID == 0)//if left is air, check underneath
						{
							//System.out.println("left start");
							tmpBlock = map.getBlock(tmpB.x, tmpB.y-1);
							if (tmpBlock.blockID == 0 )//if underneath is air
							{
								lookDone = true;
								rightLength = tmpTotal;
								//System.out.println("left done");
							}
						}else {
							if (tmpTotal == 1) 
							{
								System.out.println("right bad");
								badRight = true;
								lookDone = true;
							}

						}
					}
					//REMEMBER TO UPDATE BOTH
					//maybe look underneath here
					tmpB.set(currentBlock.x, currentBlock.y-1);
					tmpBlock = map.getBlock(tmpB.x, tmpB.y);
					if (tmpBlock.blockID == 0)
					{
						map.changeBlock(tmpB.x, tmpB.y, 8, 1, true );
						addLocT(tmpB);
						break;
					}
					
					//it'll be 0 if it didn't dind a dip
					if ( badLeft && badRight) 
						{
							System.out.println("quitting");
							break;
						}
					if (badLeft)tmpB.set(currentBlock.x+1, currentBlock.y);
					else if (badRight)tmpB.set(currentBlock.x-1, currentBlock.y);
					else if (leftLength > rightLength)
					{
						 tmpB.set(currentBlock.x+1, currentBlock.y);
					} else tmpB.set(currentBlock.x-1, currentBlock.y);
					//convert to water, 1 more than current
					map.changeBlock(tmpB.x, tmpB.y,8,lookBlock.meta+1, true);
					addLocT(tmpB);
					
					
					//source creation?
					
					}
					
					
					break;
					
				case 9://still water
					lookBlock.setMeta(currentMeta-1);
					if (lookBlock.getMeta() < 0) lookBlock.set(0,0);
					if (lastBlock.x != currentBlock.x || lastBlock.y != currentBlock.y)
					{
						//System.out.println("lastBlock:"+lastBlock.x + lastBlock.y + " current:"+currentBlock.x+currentBlock.y);
						lastBlock.x =currentBlock.x;
						lastBlock.y = currentBlock.y;
						

					}
					
				break;
				
				}
				
			}
	}
	
	public void updateBlocks(int count, PunkMap map){
		//System.out.println("queue size:" + blockUpdateList.list.size());
		for (int i = 0; i<count; i++)
		if (1 > 0){

			//currentBlock= blockUpdateList.list.remove(0);
			//update the block here
			//System.out.println(""+currentBlock.x);
			currentMeta = map.getBlock(currentBlock.x,currentBlock.y).getMeta();
			lookBlock = map.getBlock(currentBlock.x,currentBlock.y);
			System.out.println("block being updated:" + currentBlock.x + ":" + currentBlock.y);
			switch(lookBlock.blockID){
			case 0: //air. looks at 8 adjacent blocks, sets light level 1 lower.
				System.out.println("aitr block start");

					tmpMeta = 0;
					biggestMeta = 0;
					for (int lookX = -1; lookX <=1; lookX++)
						for (int lookY = -1; lookY <= 1; lookY++)
							if (lookX == 0 | lookY == 0)
						{
							tmpB.set(currentBlock.x+lookX, currentBlock.y+lookY);
							tmpBlock = map.getBlock(tmpB.x, tmpB.y);
							tmpMeta = tmpBlock.getMeta();
							if (tmpBlock.blockID == 0)
							{
								if (tmpMeta >= biggestMeta) biggestMeta = tmpMeta;
									
								
							}
						}
					if (currentMeta < biggestMeta )lookBlock.setMeta(Math.max(0,biggestMeta -1));
					//System.out.println("lX:"+currentMeta +"big: "+biggestMeta);
					
					//System.out.println("torch updated at:" + currentBlock.x + " " + currentBlock.y);;

					
				break;
			case 18://leaves
					//look within 4 blocks, destroy leaf if no wood blocks there
					updateFlag = false;
					for (int lookX = -4; lookX <=4; lookX++)
						for (int lookY = -4; lookY <= 4; lookY++)
					{
						if (map.getBlock(currentBlock.x + lookX,currentBlock.y+lookY).blockID == 17)
							updateFlag = true;
						System.out.print("looking");
					}
					if (!updateFlag) 
						{
						map.getBlock(currentBlock.x,currentBlock.y).set(0,0);
							System.out.println("leaves deswtroyed at:" + currentBlock.x + " " + currentBlock.y);;

						}
				
				break;
			case 50://torch
				//torch. spreads light everywhere
				for (int lookX = -8; lookX <=8; lookX++)
					for (int lookY = -8; lookY <= 8; lookY++)
					{
						tmpB.set(currentBlock.x+lookX, currentBlock.y+lookY);
						tmpBlock = map.getBlock(tmpB.x, tmpB.y);
						if (tmpBlock.blockID == 0)
							{
							tmpV.set(lookX, lookY);
								
								tmpBlock.setMeta(
										Math.max(8-(int)tmpV.len(),tmpBlock.getMeta())
										);
								
								blockUpdateList.addBlock(tmpB.x, tmpB.y);
							}
					}
				
				
				
				
				break;
			}
		}
	}
	
}

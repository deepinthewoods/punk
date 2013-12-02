package com.niz.punk;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class ItemPool extends Pool<Item> {
	protected Item newObject()
	{
		return new Item();
	}
	public Array<Item> itemList= new Array<Item>();
	private Item tmpItem = new Item();
	private Iterator<Item> iter;
	private Vector3 tmpVector  = new Vector3(0,0,0);
	private Vector2 tmpV2 = new Vector2(0,0);
	private int tmpi;
	public ItemPool(){
		
	}
	public void update(PunkMap map, World world, float deltaTime, Player player, PunkBodies monsterIndex, long time)
	{
		iter = itemList.iterator();
		while (iter.hasNext())
		{	tmpItem = (Item)iter.next();
			if (tmpItem.actorID == 1)//ignore BBs
				
			tmpItem.update(map, world, deltaTime, player, monsterIndex, time);
		}
	}
	public void updateRemovals(World world){
		/*Iterator<Item> iter = itemList.iterator();
		while (iter.hasNext()){
			tmpItem = iter.next();
			if (tmpItem.updateRemoval(world)){
				//tmpItem.boundBoxB.deactivate();
				//tmpItem.boundBoxS.deactivate();
				itemList.removeValue(tmpItem, true);
				free(tmpItem);
			}
			
			
			
		}*/
		for (int i = 0; i < itemList.size; i++){
			//if (monsterList.size > 0){
				tmpItem = itemList.get(i);
				if (tmpItem.updateRemoval(world)){
					//monsterList.add(tmpMonster);
					
					
					
					 free(itemList.removeIndex(i));
					 i--;

					////Gdx.app.log(TAG,"update. removing actor:"+tmpMonster.actorID);
				}
			}
	}
	
	public Item createItem(int iID, int iAm, int meta, long durability, World world, PunkBodies monsterIndex,  PunkMap map, Vector2 spawnPos){
		if (iID > 0){
			tmpItem = obtain();
			itemList.add(tmpItem);
			
			//tmpItem.boundBoxB = map.chunkActors.add(27, 0, world, monsterIndex, spawnPos);
			
			//tmpItem.boundBoxS = map.chunkActors.add(27, 0, world, monsterIndex, spawnPos);
			//tmpItem.createBody(1,world, monsterIndex, spawnPos);
			tmpItem.set(iID, iAm, meta, durability);
			//tmpItem.setItemID(iID);
			//tmpItem.state = iAm;
			tmpItem.lifeTimer = 300;
			tmpItem.position.set(spawnPos);
			//Gdx.app.log("itempool", "itemCreated @ "+tmpItem.position);
			//tmpItem.body.setFixedRotation(false);
			//optional steps for special blocks
			/*switch (iID)
			{
			case 1: //stone
				tmpItem.setItemID(256);
				tmpItem.state = iAm*4;
				break;
			case 8://water
				tmpItem.deactivate();
				break;
			}*/
			//BB
			
			//tmpItem.boundBoxS = itemList.get(itemList.size-1);
			//tmpItem.boundBoxS.createBody(27, world, monsterIndex, spawnPos);
			tmpItem.hasHitPlayer = false;
			tmpItem.animTimer = Punk.gTime;
		}
		return tmpItem;
		
	}
	public void drawItems(Camera camera, SpriteBatch batch, PunkBodies monsterIndex){
		iter = itemList.iterator();
		while ( iter.hasNext() ) 
			{
				tmpItem = (Item)iter.next();
				if (tmpItem.isVisible){
					float angle = 0;// tmpItem.stateTime*200;
					//Sprite s = tmpItem.getFrame(monsterIndex);
					//s.setPosition(tmpItem.position.x,tmpItem.position.y);
					//s.setRotation(angle+45);
					//s.draw(batch);
					//tmpVector.set(tmpItem.position.x-.5f, tmpItem.position.y-.5f, 0);
					//camera.project(tmpVector, 0, 0, Punk.RESX, Punk.RESY);
					//Gdx.app.log("itemp", "updte item");
					batch.draw(monsterIndex.getItemFrame(tmpItem.id, tmpItem.meta), tmpItem.position.x-.5f, tmpItem.position.y-.5f,
							.5f, .5f, // the rotation center relative to the bottom left corner of the box
							  1, 1, // the width and height of the box
							  .75f, .75f, // the scale on the x- and y-axis
							  angle);
							 // angle);
				}
				//batch.draw(tmpItem.getFrame(monsterIndex), tmpVector.x-Punk.TILESIZE2, tmpVector.y-Punk.TILESIZE2);
			}
	}
	public void clear(){
		for (int i = 0; i < itemList.size; i++)
			itemList.get(i).deactivate();
		updateRemovals(null);
	}
	
}

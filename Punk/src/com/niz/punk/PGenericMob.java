package com.niz.punk;

import java.util.Iterator;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.LongMap;
import com.badlogic.gdx.utils.Pool;

public class PGenericMob extends Pool<GenericMob>{
	protected GenericMob newObject()
	{
		return new GenericMob();
	}
	public Array<GenericMob> monsterList= new Array<GenericMob>();
	private GenericMob tmpMonster = new GenericMob();
	private Iterator<GenericMob> iter;
	private Vector3 tmpVector  = new Vector3(0,0,0);
	private Vector2 tmpVector2 = new Vector2(0,0);
	private int tmpi;
	public int poolSize = 6;//6;
	public IntArray spawnBlocks = new IntArray();
	public PGenericMob(){
		spawnBlocks.add(64);
		spawnBlocks.add(68);
		spawnBlocks.add(66);
	}
	public void updateMove(float deltaTime)
	{
		iter = monsterList.iterator();
		while (iter.hasNext())
		{	tmpMonster = iter.next();
			tmpMonster.update(deltaTime);
		}
	}
	
	public void updateRemovals(World world, Player player){
		//handling despawning here as well as destroying the bodies
		iter = monsterList.iterator();
		while (iter.hasNext())
		//for (tmpi = 0; tmpi < monsterList.size; tmpi++)
			{
				tmpMonster = iter.next();
				//if (tmpMonster.x < player.outerSpawnLimitL || tmpMonster.x > player.outerSpawnLimitR) tmpMonster.deactivate();
				if (tmpMonster.updateRemoval(world))
				{
					iter.remove();
					monsterList.removeValue(tmpMonster, true);
					GenericMob.mobs.remove(tmpMonster.hash);
					free(tmpMonster);
					
					//System.out.println("zombie removed");
				}
			}
		
	}
	
	
	
	
	/*public void updateSpawns(World world, PunkBodies monsterIndex, PunkMap map, Player player){
		//this just spawns any vacant slots
		
		byte progress = 0;
		//int rx = player.x - (monsterIndex.SPAWNMAX + (int)(Math.random() * (monsterIndex.SPAWNMAX - monsterIndex.SPAWNMIN))) 
		//* (XRand.get(1,player.x) >0 ?1:-1);
		int rx = player.x + (
				MathUtils.random(monsterIndex.SPAWNMIN, monsterIndex.SPAWNMAX)
		) * (MathUtils.randomBoolean()?1:-1);
		
		int ry = player.y - 20;// + (int)(Math.random()*40);
		//if (map.getBlock(rx, ry).blockID == 0 && map.getBlock(rx,ry).getMeta() < 2 && monsterList.size() < poolSize)
		
		//System.out.println("!!!!!!!!!!!!!rx:" + rx + "ry:" + ry);
		
		while (ry < player.y + 20)
		{	
			switch (map.getBlock(rx, ry).blockID)
			{
			case 3://grass
			case 2://look for dirt
					progress = 1;

				break;
			case 0:
				if (progress == 1 && map.getBlock(rx,ry).getMeta() < 2) progress = 2; 
				else if (progress == 2 
						&& map.getLightLevel(rx,ry+2) > monsterIndex.SPAWNLIGHTLEVEL 
						&& monsterList.size < poolSize)
				{
					createMonster(world, monsterIndex, tmpVector2.set(rx,ry));
					progress = 0;
				}
				break;
			default : progress = 0;
			break;
			}
				
			ry++;
		
			
		}
		
		
	}*/
	public Array<MobInfo> infoList = new Array<MobInfo>();
	
	public void addInfo(MobInfo info){
		infoList.add(info);
	}
	
	public void destroyAll(){
		iter = monsterList.iterator();
		while ( iter.hasNext() ) 
			{
			tmpMonster = iter.next();
			tmpMonster.deactivate();
			}
	}
	
	public GenericMob createMonster(int x, int y, int classID, int raceID, int genderID, int fac, World world, PunkBodies monsterIndex, Player player, PunkMap map){
		long hash = MathUtils.random(165000*80000);
		GenericMob mob =  obtain();
		monsterList.add(mob);
		mob.hash = hash;
		mob.create(classID, raceID, genderID, fac, map, player, world, monsterIndex, x, y);
		
		return mob;
	}
	
	public void drawMonsters(Camera camera, SpriteBatch batch, PunkBodies monsterIndex){
		iter = monsterList.iterator();
		while ( iter.hasNext() ) 
			{
			tmpMonster = iter.next();
			
				
				if (tmpMonster.isVisible){
					tmpMonster.draw(batch);
					if (tmpMonster.isOnFire){
						Sprite fs = monsterIndex.getFireFrame(tmpMonster.stateTime);
						fs.setPosition(tmpMonster.position.x-1, tmpMonster.position.y);
						//if (!tmpMonster.isLeft)fs.setScale(-1,1);
						//else
							fs.setScale(1,1);
						fs.draw(batch);
					}
				}
				
			//	//Gdx.app.log("zombie:", "bulletV:"+tmpVector);
			}
	}
	public void clear(PunkMap map, World world) {
		//Gdx.app.log("pzombie", "clearing, size:" + monsterList.size);
		iter = monsterList.iterator();
		while (iter.hasNext()){
			tmpMonster = iter.next();
			if (tmpMonster instanceof Player)continue;
			tmpMonster.die(world, map);
			if (tmpMonster.updateRemoval(world)){
				tmpMonster.destroyBBs(world);
			}
		}
		monsterList.clear();
		
		
		
	}
	public void setPoolSize(int size)
	{
		poolSize = size;
		//TODO remove extra monsters if made smaller
	}
	public int getPoolSize(){
		return poolSize;
	}
	public void drawMonsterItems(Camera camera, SpriteBatch batch,
			PunkBodies monsterIndex) {
		iter = monsterList.iterator();
		while ( iter.hasNext() ) 
			{
			tmpMonster = iter.next();
			
				
				if (tmpMonster.isVisible){
					tmpMonster.drawItem(batch);
				}
				
			//	//Gdx.app.log("zombie:", "bulletV:"+tmpVector);
			}
		
	}
	public void drawMonsterParticles(Camera camera, SpriteBatch batch,
			PunkBodies monsterIndex) {
		iter = monsterList.iterator();
		while ( iter.hasNext() ) 
			{
			tmpMonster = iter.next();
			
				
				if (tmpMonster.isVisible){
					tmpMonster.drawParticles(batch);
					
				}
			}
	}
	
}

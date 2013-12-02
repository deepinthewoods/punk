package com.niz.punk;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.niz.punk.PhysicsActor.ExplosionType;
import com.niz.punk.PunkMap.BlockDamageType;

public class PExplosion extends Pool<Explosion>{
	protected Explosion newObject()
	{
		return new Explosion();
	}
	private Array<Explosion> monsterList= new Array<Explosion>();
	private Explosion tmpMonster = new Explosion();
	private Iterator iter;
	private Vector3 tmpVector  = new Vector3(0,0,0);
	private Vector2 tmpV2 = new Vector2(0,0);
	private Vector2 placeV = new Vector2(0,0);
	private int tmpi;
	private int poolSize = 12;
	private int EXPLOSIONTIME = 4000;
	private Block tmpBlock;
	//private ExplosionListener exListener;
	//private int lx, ly;
	//
	private Vector2 xPosition = new Vector2(0,0);
	private int tx, ty, startx, starty;
	
	public int size(){
		return monsterList.size;
	}
	public Explosion getLast(){
		if (monsterList.size == 0) return null;
		return monsterList.get(monsterList.size-1);
	}
	public PExplosion(){
		
	}
	//public int lx, ly, xSize;
	//private Vector2 tmpV = new Vector2(0,0);
	
	
	public void update(PunkMap map, World world, float deltaTime, Player player, long time, PunkBodies monsterIndex)
	{
		iter = monsterList.iterator();
		while (iter.hasNext())
		{	tmpMonster = (Explosion)iter.next();
			tmpMonster.updateMove(map, world, deltaTime, player, time, monsterIndex);
		}
	}
	
	public void updateRemovals(World world, Player player){
		//handling despawning here as well as destroying the bodies
		for (tmpi = 0; tmpi < monsterList.size; tmpi++)
			{
				tmpMonster = monsterList.get(tmpi);
				//if (tmpMonster.x < player.outerSpawnLimitL 
					//|| tmpMonster.x > player.outerSpawnLimitR) 
				//		tmpMonster.deactivate();
				//deactivate in th e indiv. updates!
				if (tmpMonster.markedForRemoval)
				{
					world.destroyBody(tmpMonster.body);
					monsterList.removeIndex(tmpi);	
					tmpMonster.body = null;
					free(tmpMonster);
					tmpMonster.markedForRemoval = false;
					//System.out.println("zombie removed");
				}
			}
		
	}
	public Vector2 tmpV = new Vector2(0,0);
	
	
	
public PhysicsActor createExplosion(ComponentExplosion info, World world, PunkMap map, PunkBodies monsterIndex, Vector2 pos, Player player, BlockDamageType dType){
		
		//monsterList.add(obtain());
	//tmpMonster = monsterList.get(monsterList.size-1);
		
		//tmpMonster.createBody(18,world, monsterIndex, pos);
		//tmpMonster.animTimer = Punk.gTime+2000;
		//choose corrent emitter
		ParticleEmitter emit;
		monsterIndex.playExplosionSound(info);
		
		/*float accum = Math.abs(health);
		accum = Math.min(20, accum);
		accum /= 20f;
		accum+=1;
		tmpMonster.set(info, accum, pos, player);*/
		//Gdx.app.log("grenade", "damage blockkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk"+accum);
		//map damage
		startx = (int)pos.x;
		starty = (int)pos.y;
		int accum = 1;
		for (tx = (int) (-info.size*(accum)); tx <= info.size*(accum); tx++)
			for (ty = (int) (info.size*(accum)); ty > -info.size*(accum); ty--)
			{
				//map.addTimedUpdate(startx+tx, starty+ty);
				tmpBlock = map.getBlock(startx + tx, starty + ty);
				tmpV2.set(tx, ty);
				int dam = info.strength;
				float alpha = tmpV2.len()/info.size;
				alpha = 1-alpha;
				alpha = Math.max(0, alpha);
				
				dam = (int) (dam * alpha);
				Gdx.app.log("explosion", "alpha"+alpha+"  dam"+dam);
				if (dam > 0)
						map.damageBlock(dType, placeV.set(startx+tx, starty+ty), 
								dam);//Math.max(0,info.strength - (int)((tmpV2.len()*(info.strength/info.size) ) ))//)+ Math.random()*size) , 
								
								
						
						//tmpBlock.set(-2, MathUtils.random(16));
			}
		
		//start emitter and timer
		//tmpMonster.explode(world,Punk.gTime);
		//player.headTarget = tmpMonster.body;
		
		player.updateBottom(map);
		player.updateRight(map);
		player.updateLeft(map);
		//TODO callback for turning hostile
		
		//TODO maybe init other stuff here?
		//monsterList.get(monsterList.size()-1).reset(monsterIndex);
		
		//map.doLightMap();
		return tmpMonster;
	}
	
	public void draw(Camera camera, SpriteBatch batch, PunkBodies monsterIndex, float deltaTime){
		iter = monsterList.iterator();
		while ( iter.hasNext() ) 
			{
			tmpMonster = (Explosion)iter.next();
				
				//the other pools could do this too. small sa ving tho...
				if (tmpMonster.isVisible) {
					tmpVector.set(tmpMonster.position.x-1, tmpMonster.position.y, 0);
					camera.project(tmpVector);
					//tmpMonster.emitter.setPosition(tmpVector.x, tmpVector.y);
					//tmpMonster.emitter.draw(batch, deltaTime);
				}
					
				//System.out.println("dwarf @ " + tmpMonster.x + "," + tmpMonster.y);
			}
	}
	
	public void setPoolSize(int size)
	{
		poolSize = size;
		//TODO remove extra monsters if made smaller
	}
	public int getPoolSize(){
		return poolSize;
	}
}

package com.niz.punk;


import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;

public class BulletPool {
	private static int POOLSIZE = 20;
	private Bullet[] pool = new Bullet[POOLSIZE];
	private int counter;
	private int tmpi = 0;
	private Vector3 tmpVector = new Vector3();
	
	public void updateRemovals(){
		for (tmpi = 0; tmpi < POOLSIZE; tmpi++ ) if (pool[tmpi].markedForRemoval){
			pool[tmpi].body.setTransform(new Vector2(1,1),0);
			pool[tmpi].markedForRemoval = false;
		}
	}
	
	public void drawBullets(Camera camera, SpriteBatch batch, PunkBodies monsterIndex){
		for (tmpi = 0; tmpi < POOLSIZE; tmpi++ ) if (pool[tmpi].body.isActive()) 
			{
				tmpVector.set(pool[tmpi].position.x-0.5f, pool[tmpi].position.y, 0);
				camera.project(tmpVector);
				batch.draw(pool[tmpi].getFrame(monsterIndex), tmpVector.x, tmpVector.y);
			}
	}
	
	public BulletPool(int type, World world, PunkBodies monsterIndex, Vector2 spawnPos){
		counter = 0;
		for (tmpi = 0; tmpi < POOLSIZE; tmpi++ ) 
			{
				pool[tmpi] = new Bullet(type, world, monsterIndex, spawnPos);
				//pool[tmpi].shoot(0,0,0, new Vector2(0,0));
				pool[tmpi].body.setActive(false);
			}
	}
	
	public Bullet getBullet(){
		counter+=1;
		if (counter>=POOLSIZE) counter = 0;
		return pool[counter];
	}
	
	public void updateBullets(PunkMap map, World world, float deltaTime, Player player, PunkBodies monsterIndex, long time){
		for (tmpi = 0; tmpi < POOLSIZE; tmpi++ )
			if (pool[tmpi].body.isActive())
			pool[tmpi].updateBullet(map, world, deltaTime, player, monsterIndex, time);
	}
	
}

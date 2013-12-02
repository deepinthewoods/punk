package com.niz.punk;

import java.util.Iterator;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class BossHandler {
private BulletPool bossBullets;// = new BulletPool(8);
private Array<Boss> bossList = new Array<Boss>();
private Vector2 tmpV =  new Vector2(0,0);
//multiple bosses? why not?
public BossHandler(World world, PunkBodies monsterIndex){
	bossBullets = new BulletPool(1, world, monsterIndex, tmpV);
}

public void clearBosses(PunkMap map, World world){
	Iterator<Boss> iter = bossList.iterator();
	while (iter.hasNext()){
		Boss aBoss = iter.next();
		aBoss.clear(map, world);
		
	}
	bossList.clear();
}

public void update(PunkMap map, World world, float deltaTime, Player player, long time, PunkBodies monsterIndex){
	bossBullets.updateBullets(map, world, deltaTime, player, monsterIndex, time);
	Iterator<Boss> iter = bossList.iterator();
	while (iter.hasNext()){
		Boss aBoss = iter.next();
		aBoss.update(map, world, deltaTime, player, time, monsterIndex, bossBullets);
	}
	bossBullets.updateRemovals();
}

public BChunkActors addChunkActors(Vector2 position,World world, PunkBodies monsterIndex){
	BChunkActors chunkA = null;//new BChunkActors();
	chunkA.init(23, tmpV.set(0,0), world, monsterIndex);
	bossList.add(chunkA);
	return chunkA;
}

public void add(int ID, Vector2 position,World world, PunkBodies monsterIndex){
		switch (ID){
		
		case 23: BChunkActors chunkA = null;//new BChunkActors();
				chunkA.init(23, tmpV.set(0,0), world, monsterIndex);
				bossList.add(chunkA);
			break;
		}
		
		
}
public void draw(Camera camera, SpriteBatch batch, BitmapFont font, PunkBodies monsterIndex){
	Iterator<Boss> iter = bossList.iterator();
	while (iter.hasNext()){
		Boss aBoss = iter.next();
		aBoss.draw(camera, batch, font, monsterIndex);
	}
}


}

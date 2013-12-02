package com.niz.punk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.World;

public class SSizeChicken implements SpawnStrategy {
public int size, maxSize, spawnHeight, spawnInterval, updateTotal;
public void set(int initialSize, int maxSize, int height, int interval){
	size = initialSize;
	spawnHeight = height;
	spawnInterval = interval;
	this.maxSize = maxSize;
}

	@Override
	public void attemptSpawn(PunkMap map, Player player, World world, PunkBodies monsterIndex) {
		//Gdx.app.log("spawner", "attempting...");
		
		if (updateTotal % spawnInterval == 0){
			//progress = 0;
			if (size < maxSize) size++;
		}
		//if (map.chunkActors.chickenPool.monsterList.size >= size) return;
		
		int x = 15;
		if (player.x > 64) x*=-1;
		x += 64;
		int y = spawnHeight;
		//Gdx.app.log("map", "trying to spawn");
		if (map.getBlock(x,y+1).blockType() ==0 && map.getBlock(x,y+2).blockType() == 0){
			int progress = 0;
			while (map.getBlock(x,y).blockType() == 0 && progress < 12){
				x--;
				progress++;
			}
			Block b = map.getBlock(x,y);
			//map.chunkActors.spawnChicken(x,y, world, monsterIndex);
			updateTotal++;
		}
	
		
	}

}
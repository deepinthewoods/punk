package com.niz.punk;

import com.badlogic.gdx.physics.box2d.World;

public interface ControllableByPlayer {
	
	public void run(GenericMob mob, int direction, long time,float deltaTime, PunkMap map, World world);
	public void stopRunning(GenericMob mob, int direction, long time,float deltaTime, PunkMap map, World world);
	public void startRunning(int direction, long time,float deltaTime, PunkMap map, World world);
	
	public void jump(GenericMob mob, int touch, PunkMap map, World world, PunkBodies monsterIndex, int gameMode, long time);
	
	//public void stopJumping(Body body, int direction, PunkMap map, World world, PunkBodies monsterIndex, int gameMode, long time);
	

	//update
	
}

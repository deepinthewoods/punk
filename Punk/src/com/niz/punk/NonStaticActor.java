package com.niz.punk;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;

public interface NonStaticActor{
	public void updatePA(PunkMap map, World world, float deltaTime, 
			Player player, long time, PunkBodies monsterIndex);
	public TextureRegion getFrame(PunkBodies monsterIndex);
	public void resetValues(int iID, int meta);
	public void createBBs(World world, PunkBodies monsterIndex);
	public void destroyBBs(World world, PunkBodies monsterIndex);
}

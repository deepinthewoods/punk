package com.niz.punk;

import com.badlogic.gdx.physics.box2d.World;

public class CUpdateBlock extends ComponentUpdate {

	@Override
	public void act(GenericMob mob, PunkMap map, Player player,
			PunkBodies monsterIndex, World world) {
		// TODO Auto-generated method stub
		if (mob.distanceFromPlayer > Punk.CHUNKSIZE*2)
			mob.deactivate();
	}

}

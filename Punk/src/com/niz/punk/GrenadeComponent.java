package com.niz.punk;

import com.badlogic.gdx.physics.box2d.World;

public interface GrenadeComponent {

	public abstract void act(Grenade mob, PunkMap map, Player player, PunkBodies monsterIndex,
			World world);

}

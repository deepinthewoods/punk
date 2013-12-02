package com.niz.punk;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.physics.box2d.World;
import com.niz.punk.PunkMap.BlockDamageType;

public class ComponentExplosion{
	public int size, strength;
	public BlockDamageType dType;
	public Sound sound;
	public ComponentExplosion(int size, int strength, BlockDamageType dtype){
		this.size = size;
		this.strength = strength;
		
		this.dType = dtype;
	}
	public void act(Grenade mob, PunkMap map, Player player,
			PunkBodies monsterIndex, World world) {
		
		PhysicsActor ex = map.explosionPool.createExplosion(this, world, map, monsterIndex, mob.position, player, dType);
		if (player.headTarget == mob)player.headTarget = ex;
		if (sound != null)monsterIndex.playExplosionSound(mob.info);
	}


}

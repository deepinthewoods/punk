package com.niz.punk;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.World;
import com.niz.punk.PunkMap.BlockDamageType;

public class CTrailS implements GrenadeComponent {
	int damage;
	public BlockDamageType type;
	public void set(int damage, BlockDamageType type){
		//this.id = id;
		this.damage =damage;
		this.type =type;
	}
	@Override
	public void act(Grenade mob, PunkMap map, Player player,
			PunkBodies monsterIndex, World world) {
		if (mob.distanceFromPlayer <4)return;
		if (MathUtils.randomBoolean()) return;
		int tx = mob.x;//MathUtils.random(mob.x-1, mob.x+1);
		int ty = MathUtils.random(mob.y-1, mob.y+1);
		map.damageBlock(type, tx, ty, damage);
		//map.changeBlock(tx, ty, id, MathUtils.random(8,16));
	}

}

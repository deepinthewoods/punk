package com.niz.punk;

public class AIMelee extends AINeuron {

	@Override
	public int assess(GenericMob mob) {
		GenericMob enemy = GenericMob.mobs.get(mob.nearestEnemy);
		int dist = enemy.distanceTo(mob);
		dist = Math.min(32, dist);
		return 32-dist;
	}

	@Override
	public boolean move(GenericMob mob) {
		GenericMob enemy = GenericMob.mobs.get(mob.nearestEnemy);
		mob.targetV.set(enemy.position);
		return false;
	}

	@Override
	public int act(GenericMob mob) {
		// TODO Auto-generated method stub
		return 0;
	}

}

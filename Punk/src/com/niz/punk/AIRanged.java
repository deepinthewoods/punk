package com.niz.punk;

public class AIRanged extends AINeuron {

	@Override
	public int assess(GenericMob mob) {
		GenericMob enemy = GenericMob.mobs.get(mob.nearestEnemy);
		int dist = enemy.distanceTo(mob);
		dist = Math.min(32, dist);
		return dist;

	}

	@Override
	public boolean move(GenericMob mob) {
		GenericMob enemy = GenericMob.mobs.get(mob.nearestEnemy);
		mob.targetV.set(enemy.position).sub(mob.position).mul(-1).add(mob.position);
		return false;
	}

	@Override
	public int act(GenericMob mob) {
		// TODO Auto-generated method stub
		return 0;
	}

}

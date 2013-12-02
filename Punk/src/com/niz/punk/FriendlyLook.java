package com.niz.punk;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class FriendlyLook implements QueryCallback{
	private GenericMob mob;
	private PunkMap map;
	public boolean success;
	public void check(PunkMap map, World world, GenericMob m){
		mob = m;
		success = false;
		this.map = map;
		mob.nearestFriend = 0;
		world.QueryAABB(this, mob.x - mob.info.visualRange, mob.y - mob.info.visualRange, mob.x + mob.info.visualRange, mob.y + mob.info.visualRange);
	}
	@Override
	public boolean reportFixture(Fixture fixture) {
		Object o = fixture.getBody().getUserData();
		GenericMob act;
		if (o instanceof GenericMob)
			act = (GenericMob) o;
		else return true;
		if (act.faction == null) throw new GdxRuntimeException("null faction : ");
		if (act.faction.id > 0){
			if (act.hash != mob.hash && mob.faction.opinion.get(act.faction.id) > 0){
				if (GenericMob.mobs.containsKey(mob.nearestFriend)){
					if (GenericMob.mobs.get(mob.nearestFriend).position.dst2(mob.position) > mob.position.dst2(act.position))
						mob.nearestFriend = act.hash;
				}else mob.nearestFriend = act.hash;
				//setAllFactions(mob, act.faction);
				success = true;
				//return true;
			}			
		}
		return true;
	}
}
package com.niz.punk;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;

public class PeersLook implements QueryCallback {

	private GenericMob mob;
	
	public boolean success;
	public void check(World world, GenericMob m){
		mob = m;
		success = false;
		
		world.QueryAABB(this, mob.x - mob.info.visualRange, mob.y - mob.info.visualRange, mob.x + mob.info.visualRange, mob.y + mob.info.visualRange);
	}
	@Override
	public boolean reportFixture(Fixture fixture) {
		Object o = fixture.getBody().getUserData();
		GenericMob act;
		if (o instanceof GenericMob)
			act = (GenericMob) o;
		else return false;
		if (act != null && act.faction.id == mob.faction.id){
			if (!mob.peers.contains(act.hash)){//if hostile
				mob.peers.add(act.hash);
				if (!act.peers.contains(mob.hash)) act.peers.add(mob.hash);
				//setAllFactions(mob, act.faction);
				success = true;
				
			}			
		}
		return false;
	}

}

package com.niz.punk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class EnemyLook implements QueryCallback{
	private GenericMob mob;
	private PunkMap map;
	public boolean success;
	public void check(PunkMap map, World world, GenericMob m){
		mob = m;
		success = false;
		this.map = map;
		mob.nearestEnemy = 0;
		world.QueryAABB(this, mob.x - mob.info.visualRange, mob.y - mob.info.visualRange, mob.x + mob.info.visualRange, mob.y + mob.info.visualRange);
		/*if (success)
			Gdx.app.log("enemy look", "successful");//+GenericMob.mobs.get(mob.nearestEnemy).position);
		else Gdx.app.log("enemy look", "unsuccessful");*/
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
			
			if (mob.position.dst(act.position)< mob.info.visualRange && act.hash != mob.hash && mob.faction.opinion.get(act.faction.id) < 0){
				if (GenericMob.mobs.containsKey(mob.nearestEnemy)){
					if (mob.position.dst2(GenericMob.mobs.get(mob.nearestEnemy).position) > mob.position.dst2(act.position))
							{
								mob.nearestEnemy = act.hash;
								//Gdx.app.log("enemy look", "near"+act.position.dst(mob.position) + " "+mob.info.visualRange);
								//if (!GenericMob.mobs.containsKey(act.hash)) Gdx.app.log("look", "error");
								success = true;
							}
				}else {
					mob.nearestEnemy = act.hash;
					//Gdx.app.log("enemy look", "near:"+act.position.dst(mob.position));
					success = true;
				}
			}
				//setAllFactions(mob, act.faction);
			//success = true;
				//return true;
			
		}
		return true;
	}
}

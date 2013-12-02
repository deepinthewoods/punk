package com.niz.punk;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class ComponentRanged implements Component {
	public static Vector2 destV = new Vector2(0,0), srcV = new Vector2();;
	public GrenadeInfo info = new GrenadeInfo(null, null, null);
	
	public ComponentRanged(){
		
	}
	@Override
	public void act(GenericMob mob, PunkMap map, Player player,
			PunkBodies monsterIndex, World world) {
		//Gdx.app.log("ranged", "shooting");
		destV.set(player.position).add(0,Player.EYEHEIGHT);
		srcV.set(mob.position).add(0, 2);
		map.shootTargetedGrenade(mob.faction.id, srcV, GenericMob.mobs.get(mob.nearestEnemy).position, info, world, monsterIndex, mob);
	}

}

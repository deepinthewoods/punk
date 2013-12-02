package com.niz.punk;

import com.badlogic.gdx.physics.box2d.World;

public class CAttackMelee1 extends ComponentStates {
	public CAttackMelee1(){
		
		ComponentRanged rangedComp = new ComponentRanged();
		//set(health, meleeDamage, meleeDamageType, rangedComp);
		//updateInterval = 8;
	}

	@Override
	public void act(GenericMob mob, PunkMap map, Player player, PunkBodies monsterIndex, World world){
		//Gdx.app.log("attack", "tick");
		//if (!hasRanged) return;
		//mob.attackTimer++;
		//Gdx.app.log("attack", "tick");

		/*if (mob.attackTimer > updateInterval){
			//fire
			//Gdx.app.log("attack", "fire");

			if (mob.isHostile)
				rangedAttack.act(mob, map, player, monsterIndex, world);
			mob.attackTimer = 0;
		}*/
	}
}

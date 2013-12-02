package com.niz.punk;

import com.badlogic.gdx.physics.box2d.World;
import com.niz.punk.PhysicsActor.ArmorType;

public class ComponentStates  implements Component {
	
	
	
	
	
	//public ComponentRanged rangedAttack = new ComponentRanged();
	
	public ComponentStates(){
	
	}
	
	

	public void set(int health, int meleeDamage, int updateInterval, DamageType meleeType){
		
		//if (health > 20) throw new GdxRuntimeException("health too big");
	}
	
	

	@Override
	public void act(GenericMob mob, PunkMap map, Player player,
			PunkBodies monsterIndex, World world) {
		
		if (!mob.isLongUpdate) {
			//Gdx.app.log("attack", "skip"+mob.attackTimer);
			return;
		}
		
		
		
	}
	
	public void onStats(GenericMob mob, PunkMap map, Player player,
			PunkBodies monsterIndex, World world){
		
	}
	
	
}

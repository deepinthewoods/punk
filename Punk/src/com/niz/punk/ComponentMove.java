package com.niz.punk;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.World;

public class ComponentMove implements Component {
	public void start(GenericMob mob){};
	
	@Override
	public void act(GenericMob mob, PunkMap map, Player player,
			PunkBodies monsterIndex, World world) {
		// TODO Auto-generated method stub
		
	}
	public void collide(GenericMob mob, PunkMap map, Player player,
			PunkBodies monsterIndex, World world){
		//mob.body.applyLinearImpulse(MathUtils.random(-11,11),MathUtils.random(-12,12), mob.position.x, mob.position.y);
		
	}
}

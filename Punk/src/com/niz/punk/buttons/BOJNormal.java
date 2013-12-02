package com.niz.punk.buttons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.niz.punk.ButtonOverride;
import com.niz.punk.GenericMob;
import com.niz.punk.PunkBodies;
import com.niz.punk.PunkMap;

public class BOJNormal extends ButtonOverride{
	private static final float JUMPTIME = .38f;
	private static final float JUMP_SPEED = 9f;//3.75f;
	

	@Override
	public void unPress(PunkMap map, GenericMob mob, PunkBodies mi, boolean twoPresses) {
		
		if (mob.state == 4 || mob.state == 5 || mob.state == 9){
			if (mob.body.linVelWorld.y > 0f)
				mob.body.setLinearVelocity(mob.body.linVelWorld.x, 0);
				mob.state = 10;
		}
		
	}

	@Override
	public void pressed(GenericMob mob, PunkMap gMap, World world,
			PunkBodies mi, boolean pressed, float deltaTime) {
		//Gdx.app.log("jump", "tick"+mob.stateTime);
		if (mob.hasHitGround && (mob.state == 0 || mob.state == 1 || mob.state == 2 || mob.state == 3)) {
			mob.state = 4;
			
			//mi.playJumpSound(mob);
		}
		
		if (mob.state == 4){
			//if (!isGliding)
				//mob.jumpAccumulator += deltaTime;
			//else jumpAccumulator += deltaTime / 2f;
			
			if (mob.stateTime < JUMPTIME){
				//Gdx.app.log("pla", "jump:"+mob.stateTime);
				//body.applyForce(tmpV.set(0,16).mul(1f/deltaTime), position);
				mob.body.setLinearVelocity(mob.body.getLinearVelocity().x, JUMP_SPEED);
			} else mob.state = 5;
		}else if (mob.state == 5){//looking for downwards vel
			if (mob.body.getLinearVelocity().y < 0){//initiate phase 2
				mob.state = 9;
			}
			
		}else if (mob.state == 9){
			//mob.body.setLinearVelocity(mob.body.getLinearVelocity().x, JUMP_SPEED);
		}
		
	}

	

}

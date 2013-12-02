package com.niz.punk.buttons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.niz.punk.ButtonOverride;
import com.niz.punk.GenericMob;
import com.niz.punk.PunkBodies;
import com.niz.punk.PunkMap;

public class BOJBarbarian extends ButtonOverride{
	
	

	@Override
	public void unPress(PunkMap map, GenericMob mob, PunkBodies mi, boolean twoPresses) {
		
		if (mob.state == 4 || mob.state == 5 || mob.state == 9 || mob.state == 36){
			if (mob.body.linVelWorld.y > 0f)
				mob.body.setLinearVelocity(mob.body.linVelWorld.x, 0);
				mob.state = 10;
				mob.hasHitGround = false;
		}
		
	}

	@Override
	public void pressed(GenericMob mob, PunkMap gMap, World world,
			PunkBodies mi, boolean pressed, float deltaTime) {
		//Gdx.app.log("jump", "tick"+mob.stateTime);
		switch (mob.state){
		case 0:case 1:case 2:case 3:case 7:case 18:
			if (mob.hasHitGround){
				mob.state = 4;
			}
			if (!mob.runPressed){
				//mob.body.setLinearVelocity(0,0);
				
			}
			break;
		case 10://break;
		case 9:
			if (mob.blocksFallen < -3)
				mob.state = 36;
			break;
		case 4:
			if (mob.jumpStamina > 0f){
				//Gdx.app.log("pla", "jump:"+mob.stateTime);
				mob.body.setLinearVelocity(mob.body.getLinearVelocity().x, mob.jumpSpeed);
				mob.jumpStamina -= deltaTime;
			} else mob.state = 5;
			break;
		case 5:
			if (mob.body.getLinearVelocity().y < 0){//initiate phase 2
				mob.state = 36;
				mob.hasHitGround = false;
			}
			break;
		}
		
		
	}

}

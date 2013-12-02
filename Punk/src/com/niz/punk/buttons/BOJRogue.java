package com.niz.punk.buttons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.niz.punk.ButtonOverride;
import com.niz.punk.GenericMob;
import com.niz.punk.PunkBodies;
import com.niz.punk.PunkMap;


public class BOJRogue extends ButtonOverride{
	private static final float JUMPTIME = .32f;
	private static final float JUMP_SPEED = 9.75f;
	
	private Vector2 tmpV = new Vector2();
	@Override
	public void unPress(PunkMap map, GenericMob mob, PunkBodies mi, boolean twoPresses) {
		switch (mob.state){
		case 4:
		case 5:
		case 9:
			if (mob.body.linVelWorld.y > 0f)
				mob.body.setLinearVelocity(mob.body.linVelWorld.x, 0);
				//if (mob.state == 32)
					mob.state = 10;
				mob.hasHitGround = false;
				break;
		case 45:
			mob.invertStateTime();
			mob.state = 47;
			mob.lastState = 47;
			//tmpV.set(mob.body.)
			break;
		case 46:
			tmpV.set(mob.body.getLinearVelocity());
			if (tmpV.y > mob.jumpSpeed)
				mob.body.setLinearVelocity(tmpV.x, mob.jumpSpeed);
			mob.state = 47;
			break;
		case 47:
			
			break;
			
		}
		
		
	}

	@Override
	public void pressed(GenericMob mob, PunkMap gMap, World world,
			PunkBodies mi, boolean pressed, float deltaTime) {
		//Gdx.app.log("jump", "tick"+mob.stateTime);
		switch (mob.state){
		case 0:case 1:case 2:case 3:case 7:case 18:
			if (mob.hasHitGround){
				mob.state = 45;
				mob.setPolymorph(1);
			}
			if (!mob.runPressed){
				//mob.body.setLinearVelocity(0,0);
				if (mob.state == 33) mob.state = 32;
			}
			break;
		case 5:
			if (mob.body.getLinearVelocity().y < 0){//initiate phase 2
				mob.state = 9;
				//
			}
			break;
		case 9:case 10:
			
			mob.state = 45;
			mob.setPolymorph(1);
			break;
		case 32:
		case 33:case 34:case 35:
				//mob.hasHitGround = false;
				//Gdx.app.log("jumpwiz", "jjj");
			break;
		case 46:
		case 47:
		case 45:
			if (mob.jumpStamina > 0f){
				//Gdx.app.log("pla", "jump:"+mob.stateTime);
				mob.body.setLinearVelocity(mob.body.getLinearVelocity().x, mob.jumpSpeed);
				mob.jumpStamina -= deltaTime;
			}// else mob.state = 5;
			break;
		case 4:
			if (mob.jumpStamina > 0f){
				//Gdx.app.log("pla", "jump:"+mob.stateTime);
				mob.body.setLinearVelocity(mob.body.getLinearVelocity().x, mob.jumpSpeed);
				mob.jumpStamina -= deltaTime;
			} else mob.state = 5;
			break;
		
		}
		
		
		if (mob.state == 44444){
			//mob.hasHitGround = false;
			
		}else if (mob.state == 5){//looking for downwards vel
			
			
		}else if (mob.state == 9 || mob.state == 10){
			
			//mob.body.setLinearVelocity(mob.body.getLinearVelocity().x, JUMP_SPEED);
		} else if (mob.state == 32){
			//mob.jumpData += Punk.gTimeIncrement;
			
		}
	}
	public void stats(GenericMob mob){
		mob.particleBits.set(16);
	}
}

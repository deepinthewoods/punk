package com.niz.punk.buttons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.niz.punk.ButtonOverride;
import com.niz.punk.GenericMob;
import com.niz.punk.PunkBodies;
import com.niz.punk.PunkMap;

public class BOJWizard extends ButtonOverride{
	private static final float JUMPTIME = .32f;
	private static final float JUMP_SPEED = 9.75f;
	
	private Vector2 tmpV = new Vector2();
	@Override
	public void unPress(PunkMap map, GenericMob mob, PunkBodies mi, boolean twoPresses) {
		
		if (mob.state == 4 || mob.state == 5 || mob.state == 9 || mob.state == 30 | mob.state == 32 || mob.state == 33 || mob.state == 34){
			if (mob.body.linVelWorld.y > 0f)
				mob.body.setLinearVelocity(mob.body.linVelWorld.x, 0);
				//if (mob.state == 32)
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
			if (mob.hasHitGround)mob.state = 33;
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
			mob.state = 32;
			break;
		
				//mob.hasHitGround = false;
				//Gdx.app.log("jumpwiz", "jjj");
		//	break;
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

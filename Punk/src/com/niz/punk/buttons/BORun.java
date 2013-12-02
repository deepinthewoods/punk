package com.niz.punk.buttons;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.niz.punk.ButtonOverride;
import com.niz.punk.GenericMob;
import com.niz.punk.Punk;
import com.niz.punk.PunkBodies;
import com.niz.punk.PunkMap;

public class BORun extends ButtonOverride {
	private static final String tag = "miner run";
	
	private static final float ACCEL_RUN = 40, ACCEL_JUMP = 80;
	
	private static final float GRAVITY_RUN = 5f;
	private static final float GRAVITY_SWIMMING = 1f;
	static final float GRAVITY_GLIDE = 1f;
	private static final float MAX_RUN_SPEED = 8;
	private Vector2 tmpVP = new Vector2(), tmpV = new Vector2();;
	public boolean isOnLeft = true;

	public BORun(boolean b) {
		isOnLeft = b;
	}

	public boolean press(PunkMap map, GenericMob mob, PunkBodies mi) {
		if (sibling.pressed){
			sibling.unPress(map, mob, mi, true);
			
		}
		
		return false;
	}

	@Override
	public void unPress(PunkMap map, GenericMob mob, PunkBodies mi, boolean twoPresses) {
		switch (mob.state){
		
		case 2:
			mob.state = 10;
			mob.hasHitGround = false;
		
			mob.body.setLinearVelocity(0, mob.body.linVelWorld.y);
			break;
		case 32:
			
			break;
		case 33://fw
				//change to 34,keep stateTime
			mob.invertStateTime();
			mob.state = 34;
			mob.lastState = 34;
			
			break;
		case 34://back
				
			break;
		case 35://max fw
				mob.state = 34;
			break;
			//mob.stateTime = 0f;
		}
		//if ((mob.onCornerR || mob.onCornerL || mob.isOnSolidGround) && mob.hasHitGround)
			//..mob.body.setLinearVelocity(0, mob.body.linVelWorld.y);
	}
	
	@Override
	public void pressed(GenericMob mob, PunkMap gMap, World world, PunkBodies mi, boolean pressed, float deltaTime) {
		//Gdx.app.log(tag, "run"+mob.state+mob.hasHitGround);
		
		if (mob.state == 0 || mob.state == 7 || mob.state == 8 || mob.state == 18 || mob.state == 19 || mob.state == 41 || mob.state == 42){
			mob.state = 2;
			mob.frictionState = 1;
			//mob.body.applyLinearImpulse(0, 2f, mob.position.x, mob.position.y);
		}
		switch (mob.state){
		case 1:
			break;
		case 2:
			if (mob.hasHitGround)
			{
				tmpV.set(mob.body.getLinearVelocity());
				mob.isLeft = isOnLeft;
				int direction = (mob.isLeft?-1:1);
				float maxRunSpeed = MAX_RUN_SPEED*mob.speedMultiplier;//, accelerationSpeed = ACCEL_RUN;
				//mob.body.setLinearVelocity(maxRunSpeed*direction, tmpV.y);
				if (!Punk.timeValid)return;
				if ((tmpV.x < maxRunSpeed && direction ==1)|| (tmpV.x > -maxRunSpeed && direction == -1))
					mob.body.applyForce(700 * direction, 0, mob.position.x, mob.position.y);
				//mob.hasHitGround = false;
			} else mob.state = 10;
			break;
		case 32:
			mob.state = 33;
			mob.isLeft = isOnLeft;
		case 33:case 35:
			if (mob.isLeft != isOnLeft)
				mob.state = 34;
			else	
			{
				
				//mob.isLeft = isOnLeft;
				tmpV.set(mob.body.getLinearVelocity());
				int direction = (isOnLeft?-1:1);
				float maxRunSpeed = MAX_RUN_SPEED*mob.speedMultiplier*1.5f;
				
				if ((tmpV.x < maxRunSpeed && direction ==1)|| (tmpV.x > -maxRunSpeed && direction == -1))
					mob.body.applyForce(500 * direction, -.0f, mob.position.x, mob.position.y);
			}
		break;
		case 34:
			if (mob.isLeft == isOnLeft){
				mob.invertStateTime();
				mob.state = 33;
				mob.lastState = 33;
			}
			{
				tmpV.set(mob.body.getLinearVelocity());
				int direction = (isOnLeft?-1:1);
				float maxRunSpeed = MAX_RUN_SPEED*mob.speedMultiplier*1.5f;
				
				if ((tmpV.x < maxRunSpeed && direction ==1)|| (tmpV.x > -maxRunSpeed && direction == -1))
					mob.body.applyForce(500 * direction, -.0f, mob.position.x, mob.position.y);
			}
		case 47:
			
			
			break;
		case 45:case 46:
			float mul= 210200;
			mob.body.applyTorque(isOnLeft?deltaTime*mul:-deltaTime*mul);
			float damp = mob.body.getAngularVelocity();
			if (Math.abs(damp) > 10){
				mob.body.setAngularVelocity(damp>0?10:-10);
			}
			mob.isLeft = (damp > 0);
			{
				
				
				tmpV.set(mob.body.getLinearVelocity());
				int direction = (isOnLeft?-1:1);
				float maxRunSpeed = MAX_RUN_SPEED*mob.speedMultiplier;
				
				if ((tmpV.x < maxRunSpeed*2 && direction ==1)|| (tmpV.x > -maxRunSpeed*2 && direction == -1)){
					mob.body.applyForce(500 * direction , -.0f, mob.position.x, mob.position.y);
					//Gdx.app.log("run", "run");
				}
			}
			break;
		case 4:case 5:case 9:case 10:case 30:case 36:
			{
			
				mob.isLeft = isOnLeft;
				tmpV.set(mob.body.getLinearVelocity());
				int direction = (mob.isLeft?-1:1);
				float maxRunSpeed = MAX_RUN_SPEED*mob.speedMultiplier;
				
				if ((tmpV.x < maxRunSpeed && direction ==1)|| (tmpV.x > -maxRunSpeed && direction == -1))
					mob.body.applyForce(5000 * direction, -.0f, mob.position.x, mob.position.y);
			}
			break;
		case 3332:
			mob.isLeft = isOnLeft;
			break;
		case 3333:
			
			break;
		case 3334:
			
			break;
		}
		/*if (mob.state == 1){
			
		
		}//walking
		else if (mob.state == 2){
			
			
		}//running
		else if(mob.state == 4 || mob.state == 5 || mob.state == 9 || mob.state == 10 || mob.state == 30){
			
			
			
		} */

	}

	
	
}
	
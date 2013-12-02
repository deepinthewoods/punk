package com.niz.punk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class CMoveChicken extends ComponentMove {
	private Vector2 tmpV = new Vector2(), vel = new Vector2();
	private final int flapDelay = 100;
	@Override
	public void act(GenericMob mob, PunkMap map, Player player,
			PunkBodies monsterIndex, World world) {
		// TODO Auto-generated method stub
		if (!mob.isLongUpdate && !mob.hasMoved) return;
		
		///targetV from actor
		//if (mob.targetActor == null){
		//}
		//else mob.targetV.set(mob.targetActor.position);
		
		//avoid nearest enemy, 
		boolean runningAway = false;
		if (GenericMob.mobs.containsKey(mob.nearestEnemy)){
			//Gdx.app.log("sheep", "seen enemy");
			runningAway = true;
			tmpV.set(mob.position);
			tmpV.sub(GenericMob.mobs.get(mob.nearestEnemy).position);
			if (tmpV.len()< mob.info.visualRange ){
				tmpV.nor().mul(16);
				mob.targetV.set(mob.position);
				mob.targetV.add(tmpV);
				//mob.targetV.set(GenericMob.mobs.get(mob.nearestEnemy).position);
				
			}else{
				//mob.targetV.set(mob.position).add(0,1);
				//Gdx.app.log("sheep", "not run away"+mob.position.dst(GenericMob.mobs.get(mob.nearestEnemy).position));
			}
			
		}// else 
		//Gdx.app.log("sheep", "no enemy"+mob.targetV);
		//go to allies
		else if (GenericMob.mobs.containsKey(mob.nearestFriend)){
			//Gdx.app.log("sheep", "seen enemy");
			tmpV.set(mob.position);
			tmpV.sub(GenericMob.mobs.get(mob.nearestFriend).position);
			//if (tmpV.len()< mob.info.visualRange ){
				tmpV.nor().mul(-16);
				
				mob.targetV.set(GenericMob.mobs.get(mob.nearestFriend).position);
				mob.targetV.sub(mob.position);
				//if (mob.targetV.len2() < 36)
				//	tmpV.mul(-1);
				mob.targetV.nor().mul(8).add(mob.position);
				mob.targetV.add(mob.targetB.x, mob.targetB.y).mul(.5f);
			//}else{
				//mob.targetV.set(mob.position).add(0,1);
				//Gdx.app.log("sheep", "not run away"+mob.position.dst(GenericMob.mobs.get(mob.nearestEnemy).position));
			//}
			
		}// else Gdx.app.log("sheep", "no enemy"+mob.targetV);
		else mob.targetV.set(mob.targetB.x, mob.targetB.y);
		boolean movingx = true, movingy = true;;
		//x direction
		float dx = Math.abs(mob.targetV.x- mob.x);
		if (dx < mob.info.idealRange){//move towards
			mob.isLeft = (mob.targetV.x > mob.position.x);
		}else if (dx > mob.info.idealRange+mob.info.rangeVariance) mob.isLeft = (mob.targetV.x < mob.position.x);
		else movingx = false;
		
		//Y direction
		//float dy = Math.abs(mob.targetV.y- mob.y);
		if (mob.y < mob.targetV.y+ mob.info.idealRange){//move towards
			//mob.isUp = (mob.targetV.y < mob.position.y);
			mob.isUp = true;
		}else if (mob.y > mob.targetV.y + mob.info.idealRange+mob.info.rangeVariance) mob.isUp = false;//mob.isUp = (mob.targetV.y > mob.position.y);
		else {
			//mob.isUp = false;
			movingy = false;
		}
		
		
		
		////////////////////////////////////////////////////////////////////////////////////////
		
		//jump?
		if (mob.hasHitGround && (map.getBlock(mob.x-1,mob.y).blockType() != 0 || map.getBlock(mob.x+1, mob.y).blockType() != 0)&& map.getBlock(mob.x,mob.y-1).blockType() != 0)
		{
			mob.actorMeta = (int) ((Punk.gTime&32767) + flapDelay);
			mob.body.setLinearVelocity
				(tmpV.set(mob.isLeft?-3:3, MathUtils.random(7,13)));
					
		}else 
		
		//run/launch
		if (mob.isLongUpdate){
			if (mob.hasHitGround  )
			{
				mob.hasHitGround = false;
				mob.actorMeta = (int) ((Punk.gTime&32767) + flapDelay);
				if (map.getBlock(mob.x,mob.y-1).blockType() != 0){
					
					tmpV.set(mob.isLeft?-2.5f:2.5f,MathUtils.random(0f, 2f));
					if (mob.isHostile){
						//tmpV.mul(2);
					}
					mob.body.setLinearVelocity(tmpV);
				}
			} else {
				tmpV.set(mob.body.getLinearVelocity());
				if (tmpV.x > 0){
					if (mob.isLeft) mob.body.setLinearVelocity(tmpV.x-MathUtils.random(1f,2f), tmpV.y+1f);
				}else if (!mob.isLeft) mob.body.setLinearVelocity(tmpV.x + MathUtils.random(1f,2f), tmpV.y+1f);
				
			}

		} else
			{//flap
				mob.actorMeta = (int) ((Punk.gTime&32767) + flapDelay);
				boolean flyingUp = false; ;
				if ( (runningAway || movingx || movingy) && ((mob.actorMeta&32767) < (Punk.gTime&32767) )) flyingUp = true;
				float speedLimit = 16, xLimit = 3;
				
				vel.set(mob.body.getLinearVelocity());
				//tmpV.set(vel.x,vel.y+1);
				
				tmpV.set(vel.x, vel.y);
				if (tmpV.y < -4) tmpV.y *= -1;
				float flyX = MathUtils.random(2,4f), flyY = MathUtils.random(0f,3f);
				if (!mob.isUp)flyY  = tmpV.y; 
				if (mob.isLeft){
					flyX *= -1;
					if (tmpV.x < -xLimit) tmpV.x = -xLimit;
				} else 
					if (tmpV.x > xLimit) tmpV.x = xLimit;
				
				
				if (flyingUp)tmpV.add(flyX,flyY);
				else tmpV.add(flyX, 0);
				
				//tmpV.rotate(MathUtils.random(35)*(mob.isLeft?1:-1));
				
				if (Math.abs(vel.y) < speedLimit){
					//mob.body.applyLinearImpulse(tmpV, mob.position);
					mob.body.setLinearVelocity(tmpV);
					mob.animTimer = 0;
					//Gdx.app.log("", "flap"+flyingUp);
				}else{
					//tmpV.x = MathUtils.clamp(tmpV.x, -4, 4);
					mob.body.setLinearVelocity(vel.x, 5);
					
				}
			
		}
		
			
		
		
	}
	@Override
	public void start(GenericMob mob) {
		// TODO Auto-generated method stub
		
	}

}

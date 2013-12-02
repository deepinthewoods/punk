package com.niz.punk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.niz.punk.PunkMap.DayTime;

public class CMoveHop extends ComponentMove{
private Vector2 tmpV = new Vector2();
	@Override
	public void act(GenericMob mob, PunkMap map, Player player, PunkBodies monsterIndex, World world) {
		//if (!mob.isLongUpdate && !mob.hasMoved) return;
		// TODO Auto-generated method stub
		//if (!mob.isLongUpdate) return;
		
		
		//run
		/*if (mob.isPendingJump && map.getBlock(mob.x,mob.y-1).blockType() >=64)
		{
			mob.isPendingJump = false;
			tmpV.set(mob.isLeft?-1.5f:1.5f,4f);
			if (mob.isHostile){
				if (mob.isVisible)tmpV.x *=2;
				else {//off screen
					//tmpV.set(isLeft?MathUtils.random(-3.5f, 1f):MathUtils.random(-1f, 3.5f), 6f);
				}
				//velV.mul(2);
				
			}
			mob.body.setLinearVelocity(tmpV);

		}
		*/
		//jump?
		if (map.getBlock(mob.x,mob.y-1).blockType() >= 64 && (mob.hasHitGround || mob.isLongUpdate))
		{
			mob.body.setLinearVelocity
				(tmpV.set(0, MathUtils.random(6)+6));
			mob.hasHitGround = false;
			
		}		
		else {
			Vector2 vel = mob.body.getLinearVelocity();
			//Gdx.app.log("mobhpop", "vel:"+vel);
			if (vel.y < -2.5f)
				mob.body.setLinearVelocity(vel.x, Math.max(-2,vel.y));
			}
		
			
		
		
	}
	@Override
	public void start(GenericMob mob) {
		// TODO Auto-generated method stub
		
	}

}

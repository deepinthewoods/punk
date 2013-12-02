package com.niz.punk;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class CMoveRunShortJump extends ComponentMove{
private Vector2 tmpV = new Vector2();

	@Override
	public void act(GenericMob mob, PunkMap map, Player player, PunkBodies monsterIndex, World world) {
		// TODO Auto-generated method stub
		if (!mob.isLongUpdate && !mob.hasMoved) return;
		
		///targetV from actor
		//if (mob.targetActor == null){
		//}
		//else mob.targetV.set(mob.targetActor.position);
		
		//x direction
		if (Math.abs(mob.targetV.x- mob.x) < mob.info.idealRange){//move away
			mob.isLeft = (mob.targetV.x > mob.position.x);
		}else mob.isLeft = (mob.targetV.x < mob.position.x);
		
		//Y direction
		if (Math.abs(mob.targetV.y- mob.y) < mob.info.idealRange){//move away
			mob.isUp = (mob.targetV.y > mob.position.y);
		}else mob.isUp = (mob.targetV.y < mob.position.y);
		
		//jump?
		if (mob.hasHitGround && (map.getBlock(mob.x-1,mob.y).blockType() >= 64 || map.getBlock(mob.x+1, mob.y).blockType() >= 64)&& map.getBlock(mob.x,mob.y-1).blockType() >=64)
		{
			mob.body.setLinearVelocity
				(tmpV.set(mob.isLeft?-3:3, 3+MathUtils.random(12)));
					
		}	
		
		//run
		if (mob.isLongUpdate && mob.hasHitGround && map.getBlock(mob.x,mob.y-1).blockType() >=64 )
		{
			mob.hasHitGround = false;
			tmpV.set(mob.isLeft?-1.5f:1.5f,(mob.isHostile?5f:1f));
			if (mob.isHostile){
				if (mob.isVisible)tmpV.x *=2;
				else {//off screen
					//tmpV.set(isLeft?MathUtils.random(-3.5f, 1f):MathUtils.random(-1f, 3.5f), 6f);
				}
				//velV.mul(2);
				
			}
			mob.body.setLinearVelocity(tmpV);

		}
		
			
		
		
	}

	@Override
	public void start(GenericMob mob) {
		// TODO Auto-generated method stub
		mob.targetV.set(mob.position.tmp().add(MathUtils.random(-10,10), MathUtils.random(-10,10)));
	}

}

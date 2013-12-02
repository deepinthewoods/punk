package com.niz.punk;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.niz.punk.PunkMap.DayTime;

public class CMovePressButtons extends ComponentMove{
private Vector2 tmpV = new Vector2();
	@Override
	public void act(GenericMob mob, PunkMap map, Player player, PunkBodies monsterIndex, World world) {
		
		if (!mob.isLongUpdate && !mob.hasMoved) return;
		
		//jump?
		/*if (mob.hasHitGround && (map.getBlock(mob.x-1,mob.y).blockType() >= 64 || map.getBlock(mob.x+1, mob.y).blockType() >= 64)&& map.getBlock(mob.x,mob.y-1).blockType() >=64)
		{
			mob.body.setLinearVelocity
				(tmpV.set(mob.isLeft?-3:3, mob.body.linVelWorld.y+3+MathUtils.random(12)));
			
		}	
		//run
		if (mob.isLongUpdate && mob.hasHitGround && map.getBlock(mob.x,mob.y-1).blockType() >=64 )
		{
			mob.hasHitGround = false;
			tmpV.set(mob.isLeft?-1.5f:1.5f,6f);
			if (mob.isHostile){
				if (mob.isVisible)tmpV.x *=2;
				else {//off screen
					//tmpV.set(isLeft?MathUtils.random(-3.5f, 1f):MathUtils.random(-1f, 3.5f), 6f);
				}
				//velV.mul(2);
				
			}
			mob.body.setLinearVelocity(tmpV);

		}
		
		//movement restrictions
		if (mob.isHeld || mob.isFrozen) mob.body.setLinearVelocity(0,0);
		if (mob.isSlowed)mob.body.setLinearVelocity(mob.body.getLinearVelocity().mul(.5f)); 
			
		*/
		int l = 0, u = 0;
		if (mob.targetV.x < mob.position.x-1)l = -1;
		else if (mob.targetV.x > mob.position.x + 1) l = 1;
		
		if (mob.targetV.y < mob.position.y-1) u = -1;
		else if (mob.targetV.y > mob.position.y + 1) u = 1;
		
		if (l == -1){
			mob.pressed[0] = true;
			mob.pressed[3] = false;
		}
		else if (l == 1){
			mob.pressed[0] = false;
			mob.pressed[3] = true;
		} else {
			mob.pressed[0] = false;
			mob.pressed[3] = false;
		}
		
		if (u == -1){
			mob.pressed[1] = true;
			mob.pressed[4] = false;
		}
		else if (u == 1){
			mob.pressed[1] = false;
			mob.pressed[4] = true;
		} else {
			mob.pressed[1] = false;
			mob.pressed[4] = false;
		}
		
	}
	
	@Override
	public void start(GenericMob mob) {
		// TODO Auto-generated method stub
		mob.targetV.set(mob.position.tmp().add(MathUtils.random(-10,10), MathUtils.random(-10,10)));
	}

}

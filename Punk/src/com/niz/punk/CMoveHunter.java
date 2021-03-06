package com.niz.punk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class CMoveHunter extends ComponentMove {
	private Vector2 tmpV = new Vector2();
	@Override
	public void act(GenericMob mob, PunkMap map, Player player,
			PunkBodies monsterIndex, World world) {
		// TODO Auto-generated method stub
		if (!mob.isLongUpdate && !mob.hasMoved) return;
		
		///targetV from actor
		//if (mob.targetActor == null){
		//}
		//else mob.targetV.set(mob.targetActor.position);
		
		//target nearest enemy, 
		mob.targetV.set(mob.position);
		if (GenericMob.mobs.containsKey(mob.nearestEnemy)){
			tmpV.set(mob.position);
			tmpV.sub(GenericMob.mobs.get(mob.nearestEnemy).position);
			tmpV.nor().mul(-8);
			
			//tmpV.add(mob.position);
			mob.targetV.add(tmpV);
		} 
		//avoid allies
		else if (GenericMob.mobs.containsKey(mob.nearestFriend)){
			tmpV.set(mob.position);
			tmpV.sub(GenericMob.mobs.get(mob.nearestFriend).position);
			tmpV.nor().mul(4);
			//if (mob.targetV.dst2(mob.position) < mob.info.idealRange) tmpV.mul(-1);
			//tmpV.add(mob.position);
			mob.targetV.add(tmpV);
		} 
		
		
		
		//x direction
				float dx = Math.abs(mob.targetV.x- mob.x);
				if (dx < mob.info.idealRange){//move towards
					mob.isLeft = (mob.targetV.x > mob.position.x);
				}else if (dx > mob.info.idealRange+mob.info.rangeVariance) mob.isLeft = (mob.targetV.x < mob.position.x);
				
				//Y direction
				float dy = Math.abs(mob.targetV.y- mob.y);
				if (dy < mob.info.idealRange){//move towards
					mob.isUp = (mob.targetV.y > mob.position.y);
				}else if (dy > mob.info.idealRange+mob.info.rangeVariance) mob.isUp = (mob.targetV.y < mob.position.y);
				
				
				//jump?
				if (mob.hasHitGround && (map.getBlock(mob.x-1,mob.y).blockType() >= 64 || map.getBlock(mob.x+1, mob.y).blockType() >= 64)&& map.getBlock(mob.x,mob.y-1).blockType() >=64)
				{
					mob.body.setLinearVelocity
						(tmpV.set(mob.isLeft?-3:3, MathUtils.random(7,13)));
							
				}	
				
				//run
				if (mob.isLongUpdate){
					if (mob.hasHitGround  )
				
					{
						if (map.getBlock(mob.x,mob.y-1).blockType() >=64){
							mob.hasHitGround = false;
							float b = MathUtils.random(4f, 12f);
							tmpV.set(mob.isLeft?-b/3:b/3,b);
							
							if (mob.isHostile){
								//tmpV.set(8,16);
							}
							//if (mob.isLeft)tmpV.x = tmpV.x * -1;
							mob.body.setLinearVelocity(tmpV);
						
						} 
					}

				} else {
					tmpV.set(MathUtils.random(4f, 6f), 0);
					tmpV.set(10,0);
					tmpV.mul(mob.isLeft?-1:1);
					mob.body.applyLinearImpulse(tmpV, mob.position);
					//if (tmpV.x > 0){
						//if (mob.isLeft) mob.body.setLinearVelocity(tmpV.x-MathUtils.random(0f,2f), tmpV.y+1f);
					//}
						//else 
						//if (!mob.isLeft) 
							//mob.body.setLinearVelocity(tmpV.x + MathUtils.random(0f,2f), tmpV.y+1f);
					
				}
				
	}
	@Override
	public void start(GenericMob mob) {
		// TODO Auto-generated method stub
		
	}

}

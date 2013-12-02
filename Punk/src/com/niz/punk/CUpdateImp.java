package com.niz.punk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;

public class CUpdateImp extends ComponentUpdate {
	private Vector2 tmpV = new Vector2();
	public void passiveTargetAcquisition(PunkMap map, GenericMob mob){
		
		if (mob.peers.size <2){
			//Gdx.app.log("h", "random target");
			mob.targetV.set(mob.position.tmp().add(MathUtils.random(-10,10), MathUtils.random(-10,10)));
		} else {//average peers location
			tmpV.set(mob.position);
			for (int i = 0; i < mob.peers.size; i++)
				tmpV.add(GenericMob.mobs.get(mob.peers.get(i)).position);
			mob.targetV.set(tmpV.mul(1f/(mob.peers.size+1)));
			//Gdx.app.log("h", "peers average position target");
		}
	}
	
	
	
	@Override
	public void act(GenericMob mob, PunkMap map, Player player,
			PunkBodies monsterIndex, World world) {
		if (mob.health < 0) {
			mob.die(player);
			return;
		}
		
		mob.distanceTravelled ++;
		
		if (!mob.isHostile)mob.isHostile = (Math.abs(player.x-mob.x) < 8 && Math.abs(player.y-mob.y) < 8);
		
		if (mob.isLongUpdate){
			//mob.acquireTarget(world);;//passiveTargetAcquisition(map, mob);;
			mob.ai();
			
		}
		
		//if (!mob.hasMoved) mob.hasHitGround = true;
		
		
		
		if (mob.isVisible && mob.light > 0)mob.distanceTravelled = 0;
		
		if (mob.distanceTravelled > 32){
			mob.distanceTravelled = 0;
			
			if (mob.distanceFromPlayer < PunkBodies.SPAWNMIN){
			} else {
				if (mob.distanceFromPlayer > PunkBodies.SPAWNMAX){
						mob.deactivate();
						//Gdx.app.log("imp", "die of range");
				}// else mob.distanceTravelled = 0;
			}
		}
		
	}

	
	
	
	
	



	
}

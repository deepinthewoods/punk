package com.niz.punk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.niz.punk.PunkMap.DayTime;

public class CUpdateDog extends ComponentUpdate {
	public Vector2 tmpV = new Vector2();
	public void passiveTargetAcquisition(PunkMap map, GenericMob mob){
		//Gdx.app.log("ch", "passive target acq");
		if (mob.peers.size <2){
			//Gdx.app.log("sh", "random target");
			int li = map.getBlock(tmpV).getLight();
			tmpV.set(mob.position.tmp().add(MathUtils.random(-30,30), MathUtils.random(-30,15)));
			if ((li < 8 && li > 4)|| map.getBlock(mob.targetV).getLight() ==0){
				mob.targetV.set(tmpV);
				//Gdx.app.log("ch", "random target");
			}
		} else {//average peers location
			tmpV.set(mob.position);
			int tot = 0;
			for (int i = 0; i < mob.peers.size; i++){
				long mobHash = mob.peers.get(i);
				if (GenericMob.mobs.containsKey(mobHash)){
					tmpV.add(GenericMob.mobs.get(mobHash).position);
					tot++;
				}
				
			}
			mob.targetV.set(tmpV.mul(1f/(tot)));
		//	Gdx.app.log("ch", "peers average position target");
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
			//mob.isLeft = mob.isHostile?(player.position.x<mob.position.x):(MathUtils.random(16) == 1?mob.isLeft:!mob.isLeft);
			if (!mob.isHostile)mob.animTimer += mob.info.updateInterval;
			if (!mob.acquireTarget(world));
			passiveTargetAcquisition(map, mob);
		}
		
		if (!mob.hasMoved) mob.hasHitGround = true;
		
		
		
		if (mob.isVisible && mob.light > 0)mob.distanceTravelled = 0;
		
		if (mob.distanceTravelled > 34){
			mob.distanceTravelled = 0;
			//if (!mob.acquireTarget(world));//passiveTargetAcquisition(map, mob);;
			
			if (mob.distanceFromPlayer < PunkBodies.SPAWNMIN){
			} else {
				if (mob.distanceFromPlayer > PunkBodies.SPAWNMAX){
						mob.deactivate();
						//Gdx.app.log("imp", "die of range");
				}// else mob.distanceTravelled = 0;
			}
		} else if (mob.distanceTravelled == 1){
			GenericMob.factionCallback.check(world, mob);
		}
	
	}
}

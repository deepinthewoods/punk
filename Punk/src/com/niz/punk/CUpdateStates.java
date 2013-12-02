package com.niz.punk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class CUpdateStates extends ComponentStates {
	private static final String tag = "attck";
	public CUpdateStates(){
		
		//ComponentRanged rangedComp = new ComponentRanged();
		//set(health, meleeDamage, 1, meleeDamageType, rangedComp);
		
	}
	private void assessPriorities(GenericMob mob){ 
		//starts firing if ranged/spell
		boolean done = false;
		//look for melee first
		int meleeRange = 4, rangedRange = 400;//squared
		if (mob.mobs.get(mob.nearestEnemy)== null) return;
		float enemyD = mob.mobs.get(mob.nearestEnemy).position.tmp().sub(mob.position).len2();
		/*if (enemyD < meleeRange){
			//go thru inv and find a melee weap
			boolean foundWeapon = false;
			int foundWeaponSlot = 0;
			int i = 0;
			while (i < mob.inv.size){
				Item item = mob.inv.get(i);
				ItemInfo inf = PunkBodies.getItemInfo(item.id, item.meta);
				if (inf.gameMode == 81){
					foundWeapon = true;
					foundWeaponSlot = i;
					
				}
						
						
				i++;
			}
			if (foundWeapon){
				mob.state = 1;
				mob.activeInvSlot = foundWeaponSlot;
				done = true;
				Gdx.app.log("attmol", "melee");
			}
		}
		
		if (!done && enemyD < rangedRange){
			//find ranged weapon
			boolean foundWeapon = false;
			int foundWeaponSlot = 0;
			int i = 0;
			while (i < mob.inv.size){
				Item item = mob.inv.get(i);
				ItemInfo inf = PunkBodies.getItemInfo(item.id, item.meta);
				Gdx.app.log("attmol", "ranged gm"+inf.gameMode);
				if (inf.gameMode == 1){
					foundWeapon = true;
					foundWeaponSlot = i;
					
				}
						
						
				i++;
			}
			if (foundWeapon){
				mob.state = 8;
				mob.activeInvSlot = foundWeaponSlot;
				done = true;
				Gdx.app.log("attmol", "ranged");
			}
			
			//mob.state = 2;
		}*/
		
		if (!done){//default/idle. needs default/fist weapon if none have been found
			Gdx.app.log("attmol", "fist");
			mob.state = 0;
		}
		
	}
	private static Vector2 srcV = new Vector2(), destV = new Vector2();
	
	public void onStats(GenericMob mob, PunkMap map, Player player,
			PunkBodies monsterIndex, World world){
		switch (mob.state){
		case 30:mob.applyBlessingsOnStats();
		//Gdx.app.log("stats", "bless"+mob.blessings.size);
			break;
		case 46:
				mob.bounceMultiplier = 2f;
			break;
		case 32:
		case 33:case 34:case 35:
			mob.particleBits.set(16+3);
			mob.particleBits.set(32+3);
			break;
		}
	}
	@Override
	public void act(GenericMob mob, PunkMap map, Player player, PunkBodies monsterIndex, World world){
		//Gdx.app.log("attack", "tick"+mob.stateTime + "  / "+mob.getMaxStateTime());
		//if (mob.state == 2){//ranged, update and do timer
		boolean timeUp = mob.stateTime > mob.getMaxStateTime();
		switch (mob.state){
		case 30:
				if (timeUp){
					mob.state = -1;
				}
			break;
		case 7:
			if (timeUp)mob.stopped = true;
			else {
				//Gdx.app.log("update", "not stopped");
				mob.stopped = false;
				mob.lastpx -= .00001f;
			}
				
				break;
		case 28://throw followthru closest enemy
			if (timeUp){

				GenericMob enemy = GenericMob.mobs.get(mob.nearestEnemy);
				destV.set(enemy.position).add(0,enemy.ySize+1);
				srcV.set(mob.position).add(0, mob.ySize+1);
				Item it = mob.inv.getItem(mob.activeInvSlot);;
				ItemDef inf = PunkBodies.getItemInfo(it.id, it.meta);
				if (inf.gameMode == 1){
					GrenadeInfo info = (GrenadeInfo) inf.data;
					map.shootTargetedGrenade(mob.faction.id, srcV, destV, info, world, monsterIndex, mob);
					it.amount--;
					it.checkValidity();
				}
					
					
					
					
					//mob.state = 8;
				mob.state = -1;
			}
					
				
				
			break;
			case 8://throw
				if (timeUp){

					//GenericMob enemy = GenericMob.mobs.get(mob.nearestEnemy);
					//destV.set(enemy.position).add(0,enemy.ySize+1);
					srcV.set(mob.position).add(0, mob.ySize+1);
					Item it = mob.inv.getItem(mob.activeInvSlot);;
					ItemDef inf = PunkBodies.getItemInfo(it.id, it.meta);
					if (inf.gameMode == 1){
						GrenadeInfo info = (GrenadeInfo) inf.data;
						map.shootGrenade(mob.faction.id, srcV, mob.angle, info, world, monsterIndex, mob);
						it.amount--;
						it.checkValidity();
					}
					
					
					
					
					//mob.state = 8;
					mob.state = 29;
					mob.lastState = 29;
				}
					
				
				break;
			case 29:
				if (timeUp){
					mob.state = 7;
					mob.lastState = 7;
				}
				break;
			case 18:
				if (timeUp)mob.stopped = true;
				else {
					//Gdx.app.log("update", "not stopped");
					mob.stopped = false;
					mob.lastpx -= .00001f;
				}
				break;
			case 50:
				Punk.queueBlockHighlight(mob.targetB);
				if (timeUp){
					
					Item item = mob.inv.getItem(mob.activeInvSlot);
					if (mob.targetB.x != -1 || mob.targetB.y != -1 
							&& mob.inv.getItemAmount(mob.activeInvSlot) > 0 
							&& map.getBlock(MathUtils.floor(mob.targetB.x), MathUtils.floor(mob.targetB.y)).blockID == 0)
					{
						int blockID = item.id, blockMeta = item.meta;
						map.changeBlock(mob.targetB.x, mob.targetB.y, 
									blockID, 
									blockMeta, true, mob.isLeft);
						map.addLightUpdate(mob.targetB.x, mob.targetB.y);
						map.addUpdate(mob.targetB.x, mob.targetB.y);
						

						mob.inv.useUpItem(mob.activeInvSlot);
						// main.changeBeltSlot(player.activeQuickSlot, false);
						
					}
					mob.state = 51;
					mob.lastState = 51;
				}
				break;
			case 51:
				if (timeUp){
					mob.state = 49;
					mob.lastState = 49;
				}
				break;
			case 39:
				Punk.queueBlockHighlight(mob.targetB);
				if (timeUp){
					
					Item item = mob.inv.getItem(mob.activeInvSlot);
					if (mob.targetB.x != -1 || mob.targetB.y != -1 
							&& mob.inv.getItemAmount(mob.activeInvSlot) > 0 
							&& map.getBlock(MathUtils.floor(mob.targetB.x), MathUtils.floor(mob.targetB.y)).blockID == 0)
					{
						int blockID = item.id, blockMeta = item.meta;
						map.changeBlock(mob.targetB.x, mob.targetB.y, 
									blockID, 
									blockMeta, true, mob.isLeft);
						map.addLightUpdate(mob.targetB.x, mob.targetB.y);
						map.addUpdate(mob.targetB.x, mob.targetB.y);
						

						mob.inv.useUpItem(mob.activeInvSlot);
						// main.changeBeltSlot(player.activeQuickSlot, false);
						
					}
					mob.state = 20;
					mob.lastState = 20;
				}
				
				break;
			case 20:
				if (timeUp){
					mob.state = 18;
					mob.lastState = 18;
				}
				break;
				
			case 41://18:
				if (timeUp)mob.stopped = true;
				else {
					//Gdx.app.log("update", "not stopped");
					mob.stopped = false;
					mob.lastpx -= .00001f;
				}
				break;
			case 42://39:
				Punk.queueBlockHighlight(mob.targetB);
				if (timeUp){
					
					Item item = mob.inv.getItem(mob.activeInvSlot);
					if (mob.targetB.x != -1 || mob.targetB.y != -1 
							//&& mob.inv.getItemAmount(mob.activeInvSlot) > 0 
							//&& map.getBlock(MathUtils.floor(mob.targetB.x), MathUtils.floor(mob.targetB.y)).blockType() == 0
							)
					{
						ToolInfo info = (ToolInfo) PunkBodies.getItemData(item.id, item.meta);
						map.damageBlock(info.blockDamageType, mob.targetB.x, mob.targetB.y, info.damage);
						map.addLightUpdate(mob.targetB.x, mob.targetB.y);

						if (mob.inv.reduceDurability(mob.activeInvSlot));
						// main.changeBeltSlot(player.activeQuickSlot, false);
						
					}
					mob.state = 43;
					mob.lastState = 43;
				}
				
				break;
			case 43://20:
				if (timeUp){
					mob.state = 41;
					mob.lastState = 41;
				}
				break;
				
				
			case 0:
				if (timeUp){
					mob.state = -1;//max state time is very very long for the player
				}
				if (!mob.hasHitGround) mob.state = 10;
				break;
			case 1://walk
				if (timeUp){
					mob.state = 2;
				}
				
				
			break;
			case 2:
				if (mob.hasMoved){
					if (mob.blockB.blockType() >= 64){//continue running
						
					}else 
						if (mob.blockB.blockType() != 2){//jump
							
							if (player.controllingMob != mob)mob.state = 6;
							else mob.state = 10;
							mob.hasHitGround = false;
						}
						else {//climb
						
						}
				}
				
				if (!mob.hasHitGround){
					mob.state = 10; 
					mob.stateTime *= HumanoidAnimation.JUMPWALK;
				}
				break;
			case 4:
			case 5:
					mob.hasHitGround = false;
					//mob.activeJump.phase1(mob);
				
				break;
			case 10:
			case 3335:
			//case 30:
				if (mob.hasHitGround){
					mob.state = 0;
					mob.stateTime /= HumanoidAnimation.JUMPWALK;
					//mob.body.setLinearVelocity(mob.body.linVelWorld.rotate(-mob.body.linVelWorld.angle()-90));
					//Gdx.app.log(tag, " to 0");
				}
				break;
			case 6:
					//if player, keep running; if ai assess map and jump 
				
				//needs to look at currently active jump and do path prediction based on it.
				
				break;
			case 11://broom
			case 22:
				
				mob.blocksFallen = 0;
				break;
			case 12:case 13:case 14:
			case 15:
				if (timeUp){
					mob.state = 0;
					mob.stateTime = 0;
					//Gdx.app.log(tag, " to 0");
				}
				break;
			case 34:
				
				if (timeUp) mob.state = 32;
			
			case 33:
			case 32:
				if (mob.hasHitGround)mob.state = 10;
			case 35:
				mob.hasHitGround = false;
				float delta = Punk.deltaTime;
				if (mob.jumpStamina > 0f){
					//Gdx.app.log("pla", "jump:"+mob.stateTime);
					tmpV.set(mob.body.getLinearVelocity());
					if (tmpV.y < 5.5f)mob.body.applyForce(0,46500f, mob.position.x, mob.position.y);
					mob.jumpStamina -= delta;
					
				} else
				{
					tmpV.set(mob.body.getLinearVelocity());
					float var = Chunk.noise.get1d(mob.stateTime*20,(int) mob.hash, 2)+.5f;
					var /=2f;
					float x = (tmpV.x < 0?200:-200);
					if (tmpV.y < -2.5f)mob.body.applyForce(
							
							x
							
							
							,100f+var*12500, mob.position.x, mob.position.y);
				}//else mob.state = 32;
				break;
			case 36:
				if (mob.hasHitGround){
					if (mob.blocksFallen > 0){
						mob.state = 37;
						mob.blocksFallen = 0;
					} else {
						mob.state = 4;
					}
				} 
				
				break;
			case 44:
			case 37:
				if (timeUp) mob.state = 0;
				break;
			case 38:
				
				if (mob.isLeft){
					mob.body.setLinearVelocity(tmpV);  
				}
				break;
			case 40:
				
				break;
			case 45:if (timeUp) mob.state = 46;
			Gdx.app.log("states", ""+mob.originOffset);
			break;
			case 46:
				mob.angle = mob.body.getAngle()*MathUtils.radiansToDegrees;
				Gdx.app.log("states", ""+mob.originOffset);
			break;
			case 47:
				Gdx.app.log("states", ""+mob.originOffset);
				mob.body.setAngularVelocity(mob.isLeft?18:-18);
				float newAngle = mob.body.getAngle()*MathUtils.radiansToDegrees;
				//Gdx.app.log("updatestas", "dsklj"+mob.body.getGravityScale());
				if (mob.isLeft){
					if ((int)newAngle / 360 != (int)mob.angle/360) {
						mob.unPolymorph(false);
						//tmpV.set(mob.)
						//mob.body.setLinearVelocity(tmpV);
						mob.state = 10;
						
					}
				} else {
					if ((int)newAngle / 180 != (int)mob.angle/180 && !((int)newAngle / 360 != (int)mob.angle/360)) {
						mob.unPolymorph(false);
						mob.state = 10;
						
					}
				}
				mob.angle = newAngle;
				break;
				
		}
		
		
		if (mob.state == -1){
			if (player.controllingMob == mob) mob.state = 0;
			else mob.ai();//assessPriorities(mob);
			//Gdx.app.log("attack", "assess"+mob.state);
		}
		
			
		if (!mob.isLongUpdate) return;
			//if (mob.isHostile && mob.distanceFromPlayer < Punk.BWIDTH && mob.distanceFromPlayer > 3)
				
			
		
	}
	private static Vector2 tmpV = new Vector2();
}

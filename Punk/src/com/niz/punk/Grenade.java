package com.niz.punk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.World;

public class Grenade extends PhysicsActor{
	Vector2 velV = new Vector2(0,0);
	//maybe some flags?
	public GenericMob source;
	
	//public boolean isSticky = false, isTimed = false, 
	//isHeatSeeking = false, isAutoExplode = false, 
	long lifeTimer = 0;
	public int accum = 0, factionID = 0;
	public GrenadeInfo info;
	public boolean hasExploded = false; 
	boolean isArmed = false;
	public float angle;
	public BlockLoc target = new BlockLoc();
	public PhysicsActor boundBoxB = new PhysicsActor(27,4), boundBoxL = new PhysicsActor(27,3), boundBoxR = new PhysicsActor(27,2), boundBoxT = new PhysicsActor(27,1);
	
	public Grenade(PunkMap map, PunkBodies mi, World world){
		health = 1000000;
		info = new GrenadeInfo(map, mi, world);
	}
	public void draw(SpriteBatch batch){
		
		if (!info.hasDirection)angle = MathUtils.radiansToDegrees *body.getAngle();
		if (info.hasParticle){
			info.particle.setPosition(position.x, position.y);
			info.particle.draw(batch);
			//Gdx.app.log("grenade", "drew");
		} 
		
		{
			Sprite s = info.s;
			s.setOrigin(.5f, .5f);
			s.setRotation(angle);
			s.setPosition(position.x-.5f,position.y-.5f);
			s.draw(batch);
		}
		
		
		
		
		
	}
	@Override
	public void doCollision(PhysicsActor col, long time, Player player, PunkMap map, World world, PunkBodies monsterIndex, Contact contact){
		//Gdx.app.log("grenade", "collision: "+col.actorID+","+col.position);
		if (info.bounces && col.actorID == 27){
			switch (col.actorMeta){
			
			
			}
		
			
		}
		
		if (col instanceof GenericMob){
			
			 {
				//scale damage
				if (!isArmed){
					float speed = body.getLinearVelocity().len2();
					float scalar;
					if (speed < info.minVelocity) scalar = 0;
					else if (speed > info.maxVelocity) scalar = 1;
					else {
						scalar = (speed-info.minVelocity)/(info.maxVelocity - info.minVelocity);
					}
					

					boolean friend = false;
					GenericMob gCol = (GenericMob) col;
					if (PunkBodies.factions[factionID].opinion.get(gCol.faction.id) > 0) friend = true;
					//if (friend && !info.collidesWithFriends) 
					boolean disabled = (friend && !info.collidesWithFriends) || (!friend && !info.collidesWithEnemies);
					//Gdx.app.log("grenade", "grenade "+friend+"   "+info.collidesWithFriends + "fac "+PunkBodies.factions[factionID].opinion.get(gCol.faction.id)+"   facid"+gCol.faction.id);
					int damage = (int) (info.damage);//no more scaling
					//disabled = false;
					if (speed > info.minVelocity && !disabled){
						col.takeDamage(damage, info.damageType);
						if (info.explodesOnMobContact){
							isArmed = true;
							Gdx.app.log("grenade", "arm from mob"+friend);
							animTimer = time + info.release;
						}
						
					}
					
				}
				
			}
		}
		
		
		
		if (info.explodesOnGroundContact && col.actorID == 27) 
			if (!isArmed && animTimer+300 < Punk.gTime){
				isArmed = true;
				animTimer = time + info.release;
				Gdx.app.log("grenade", "arm from ground");
			}
		
		/*if (info.explodesOnFriendContact &&  col.actorID == 0) 
			if (!isArmed){
				isArmed = true;
				animTimer = time + info.release;
				//Gdx.app.log("grenade", "arm from player");

			}
		
		if (info.explodesOnEnemyContact && col.actorID == 55) 
			if (!isArmed){
				isArmed = true;
				animTimer = time + info.release;
				//Gdx.app.log("grenade", "arm from mob");

			}*/
	}
	
	@Override
	public void createBBs(World world, PunkBodies monsterIndex){
		//if (boundBoxB == null)boundBoxB = new PhysicsActor();
		//boundBoxB.createBody(27,world, monsterIndex, tmpV.set(position.x, position.y+10));
		//boundBoxL = new PhysicsActor();
		//boundBoxL.createBody(27,world, monsterIndex, tmpV.set(position.x, position.y+10)));
		//if (boundBoxS == null)boundBoxS = new PhysicsActor();
		//boundBoxS.createBody(27,world, monsterIndex, tmpV.set(position.x, position.y+10));
		
		boundBoxB.body = world.createBody(
				monsterIndex.getBodyDef(27, tmpV.set(position.x, position.y+10)));
		boundBoxB.body.createFixture(monsterIndex.fixtures[27]).setUserData(boundBoxB);
		boundBoxB.body.setUserData(boundBoxB);
		
		boundBoxT.body = world.createBody(
				monsterIndex.getBodyDef(27, tmpV.set(position.x, position.y+10)));
		boundBoxT.body.createFixture(monsterIndex.fixtures[27]).setUserData(boundBoxT);
		boundBoxT.body.setUserData(boundBoxT);
		
		boundBoxL.body = world.createBody(
				monsterIndex.getBodyDef(27, tmpV.set(position.x, position.y+10)));
		boundBoxL.body.createFixture(monsterIndex.fixtures[27]).setUserData(boundBoxL);
		boundBoxL.body.setUserData(boundBoxL);
		
		boundBoxR.body = world.createBody(
				monsterIndex.getBodyDef(27, tmpV.set(position.x, position.y+10)));
		boundBoxR.body.createFixture(monsterIndex.fixtures[27]).setUserData(boundBoxR);
		boundBoxR.body.setUserData(boundBoxR);
		
		boundBoxR.actorID = 27;
		boundBoxL.actorID = 27;
		boundBoxT.actorID = 27;
		boundBoxB.actorID = 27;
	}
	public void destroyBBs(World world){
		//Gdx.app.log("grenade", "destroy bbs");
		world.destroyBody(boundBoxB.body);
		world.destroyBody(boundBoxR.body);
		world.destroyBody(boundBoxT.body);
		world.destroyBody(boundBoxL.body);
		//boundBoxB.deactivate();
		//boundBoxS.deactivate();
	}
	public void resetValues(int iID, int meta){
		health = 0;
		actorID = iID;
		actorMeta = meta;
		//actorMeta = 12;
		state = 0;
		//isSticky = false; isTimed = false;
		//isHeatSeeking = false; isAutoExplode = false;
		health = 10000;
		hasExploded = false;
		isArmed = false;
		lifeTimer = 0;
	}
		
	
	public void deactivate(Player player){
		if (player.getHeadTarget() == this) player.resetHead();
		if (info.hasParticle)
			info.particle.allowCompletion();
		super.deactivate();
	}
	public void blowUp(PunkMap map, World world, PunkBodies monsterIndex,long time,Player player){
		if (hasExploded) {
			//Gdx.app.log("grenade", "not exploding, type:"+actorMeta);
			return;
		}
		
		//make explosions here. 
		//Gdx.app.log("grenade", "exploding, type:"+actorMeta);
		switch (actorMeta){
	
		case 12:
			//Gdx.app.log("grenade", "making explosion!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

			info.explosion.act(this, map, player, monsterIndex, world);// health, world, map, monsterIndex, position, player);
			deactivate(player);
			//map.makeAFire(x, y);
			break;
		default: deactivate(player);

		}
		hasExploded = false;
		isArmed = false;
		//deactivate();
		health = 1;
	};
	public Sprite getFrame(PunkBodies monsterIndex){
		/*switch (actorMeta){
		case 0:case 1:case 2:case 3:case 4:case 5:case 6:case 7:
			return  monsterIndex.getItemFrame(300+actorMeta, 0);
		case 10:case 11:case 12:
			return monsterIndex.getItemFrame(320+actorMeta, 0);
		default:		return monsterIndex.getItemFrame(3, 0);

		}*/
		return info.s;
		
		
		
	}
 	public void shootInto(){};

 	public void updateLeft(PunkMap map, Vector2 vec, int x, int y){
		switch (map.getBlock(x-1,y).blockType())//left
		{
		//air
		case 69:
		case 0 : 
		case 4:case 5:case 6:case 7:case 8:case 9:case 10:case 16:case 15:
		//water
		case 1 :  
		
		//leaves
		case 2 :  vec.set(16, -100000f);
		break;
		//solid
		case 65:
		case 66:
		case 67:
		case 68:
		case 64 :  vec.set(x-.5f, y+0f);
		break;	
		}
		
		//if (map.getBlock(x-1, y+1).blockType() > 2)
		//	vec.set(x-0.5f, y+1f);
	}
 	public void updateTop(PunkMap map, Vector2 vec){
 		Block b = map.getBlock(x,y+1);
 		lightBits = b.getLightBits();
 		dayBits = b.getDayBits();
		switch (b.blockType())
		{
		//air
		case 4:case 5:case 6:case 7:case 8:case 9:case 10:case 16:case 15:
		case 69:
		case 0 :   //if (map.getBlock(x+1, y-1).blockType() >=64) 
					//	vec.set(x+1.5f, y-1f); 
					 vec.set(x+16, -200000f);
			break;
		//water
		case 1 :  vec.set(1.5f, -100000f);
			break;
		//leaves
		case 2 :  vec.set(x+16, -100000f);
			break;
		//solid
		case 65:
		case 66:
		case 67:
		case 68:
		case 64 :  vec.set(x+.5f, y+1f);
			break;	
		}
		//if (map.getBlock(x+1, y+1).blockType() > 2) vec.set(x+1.5f, y+1f);
		
	}
 	public void updateBottom(PunkMap map, Vector2 vec, boolean climb){
		//bottom
		switch (map.getBlock(x,y-1).blockType())
		{case 4:case 5:case 8:case 10:case 16:case 15:
			case 69:
			case 7:
			case 9:
			case 1: 
			case 6:
			case 0 : vec.set(x+16, -200000f);
				break;
	//		case 1 :  tmpV.set(x+0.5f, y-2f);
	//			break;
	//		case 2 :  tmpV.set(x+0.5f, y-1f);
	//			break;
			case 2:if (!climb){
				vec.set(0,111110);
				break;
			}
			default:
			case 68:
			case 65:
			case 66:
			case 67:
			case 64 :  vec.set(x+0.5f, y-1f);
			
			break;	
		}
	}
	public void updateRight(PunkMap map, Vector2 vec, int x, int y){
		switch (map.getBlock(x+1,y).blockType())
		{
		//air
		case 4:case 5:case 6:case 7:case 8:case 9:case 10:case 16:case 15:
		case 69:
		case 0 :   //if (map.getBlock(x+1, y-1).blockType() >=64) 
					//	vec.set(x+1.5f, y-1f); 
					 
			
		//water
		case 1 :  
			
		//leaves
		case 2 :  vec.set(x+16, -100000f);
			break;
		//solid
		case 65:
		case 66:
		case 67:
		case 68:
		case 64 :  vec.set(x+1.5f, y+0f);
			break;	
		}
		//if (map.getBlock(x+1, y+1).blockType() > 2) vec.set(x+1.5f, y+1f);
		
	}
	
 	
 	@Override
 	public void updatePA(PunkMap map, World world, float deltaTime, 
			Player player, long time, PunkBodies monsterIndex){
		
		
		checkPosition(player, deltaTime, map, 0f);
		
		if (x != lastx || y != lasty || updateBBsQueued ){
			updateBBsQueued = false;
			Block currentB = map.getBlock(x,y);
			//light = currentB.effectiveLight();
			//tmpV.y -=1;
			updateLeft(map, tmpV, x, y);
			boundBoxL.body.setTransform(tmpV, 0);
			
			updateRight(map, tmpV, x, y);			
			boundBoxR.body.setTransform(tmpV, 0);
			
			
			updateTop(map, tmpV);
			boundBoxT.body.setTransform(tmpV, 0);
			updateBottom(map, tmpV, false);
			boundBoxB.body.setTransform(tmpV, 0);
			
			if(info.flying && info.bounces){
				if (map.getBlock(x,y).blockType() >= 64){
					tmpV.set(body.getLinearVelocity());
					if (x != lastx){
						tmpV.x *= -1;
						body.setLinearVelocity(tmpV);
					}//reversex
					else {
						tmpV.y *= -1;
						body.setLinearVelocity(tmpV);
					}//reveresy
				}
			}
			info.trail.act(this, map, player, monsterIndex, world);
			lastx = x;
			lasty = y;
			if (position.dst2(player.position)> 1000000 ) deactivate();
			if (lifeTimer % 2 == 0) angle = body.getLinearVelocity().angle();
			
		}//end if changed
		
		//per-frame checks
		//states are gonna be just a counter(or negative for other stuff, -1=stuck
		if (time > lifeTimer){
			
			//play sound
			if (isArmed){
				//Gdx.app.log("grenade", "Blow up from armed");

				blowUp(map, world, monsterIndex, time, player);
				return;
			}
			//Gdx.app.log("grenade", "state:"+state+"ticks"+info.lifeTicks);
			state++;
			lifeTimer = time + 250;
			//Gdx.app.log("grenade", "TICK");
			if (state >= info.lifeTicks || health < 0){
				if (!info.explodes){ 
					deactivate(player);
					hasExploded = false;
					isArmed = false;
					health = 1;
					return;
				}else {
					if (!isArmed){
						isArmed = true;
						animTimer = time+info.release;
						Gdx.app.log("grenade", "arm from life/health"+health+","
								+state);

					}
					
				}
				//Gdx.app.log("grenade", "BOOM, timed");
			} 
			//if (info.hasParticle)
			//	info.particle.update(deltaTime);
			
			//if (state > 3000)
			//	deactivate();
		}
		//if (info.hasParticle)
		//	Gdx.app.log("grenade", "pp");
		
	}
 	public void set(GrenadeInfo info, int factionID, GenericMob src){
 		this.factionID = factionID;
 		this.info.setInfo(info);
 		actorMeta = 12;
 		state = 0;
 		lifeTimer = 0;
 		health = 1000000;
 		source = src;
 		//hasHitGround = false;
 	}
}

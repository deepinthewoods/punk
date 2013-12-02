package com.niz.punk;

import java.io.Serializable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;

public class PhysicsActor implements Serializable, NonStaticActor{
	public int lastx;
	public int lasty;
	public int lightBits, dayBits;
	public float stateTime = (float)Math.random();
	public int poisonDamage = 0;
	protected int actorID=-1;
	protected int actorMeta = 0;
	public int state=0;	
	int distanceFromPlayerHead, distanceFromPlayer;
	public transient Body body;
	public Vector2 position = new Vector2(0,0);;
	public int x=Punk.CHUNKSIZE/2, y=Punk.CHUNKSIZE/2, p=0;
	public boolean isLeft = true, isInsideSolid = false;
    public boolean isVisible = false;
    public boolean isHostile = false, isSwimming = false, isOnFire = false,
    isHoldingBreath = false;
	public int health = 6;
	public long animTimer;
	int[] tmpArray = new int[]{0,0};
	public static Vector2 tmpV = new Vector2(0,0);
	private Block tmpBlock;
	private String TAG = "PhysicsActor";
	public enum ExplosionType {COPPER_GRENADE, GOLD_GRENADE, MITHRIL_GRENADE, GRENADE, SMALLGRENADE, TNT, DWARFGRENADE, LAUNCHER, MOLOTOV}
	
	public enum ArmorType {NONE, LEATHER, BRONZE, GOLD, MITHRIL, CHAIN};
	public ArmorType equippedArmor = ArmorType.NONE;
	public long flashTimeout = 0;
	public long breathTimeout = 0;
 	public boolean isFlashing = false, markedForRemoval = false, updateBBsQueued = false;;
 	float len;//for explosions
 	private float damageChance;
 	//public int light=0;//light level
 	protected PunkBodies mi;
 	public PhysicsActor BB;
 	public int swimCurrent = 0;
 	//private transient Sound playerDamageSnd;
 	//private Preferences prefs;
 	public boolean isImmuneToFire = false;
 	public Block blockC;
 	
 	public boolean takeDamage(int damage, int dType){
 		if (damage == 0) return false;
 		
 		
 		
 		
 		
 		
 		
 		//if (MathUtils.random() > damageChance)
 		{
 			
 			//if (!isFlashing ){//&& (actorID == 2 || actorID == 3 || actorID == 10 || actorID == 11 || actorID == 28 || actorID == 38)){
 	 			//if (actorID == 0)
 				//Gdx.app.log(TAG, "damage taken:" + damage + " actorID:"+actorID + "type:" + dType+"loc:"+position + " health now "+(health-damage));
 	 		if (isVisible)mi.playMonsterHurtSound(actorID, dType);
 	 		if (actorID == 2 || actorID == 3 || actorID == 47 || actorID == 49 || actorID == 10 || actorID == 11 || actorID == 55){
 	 			Player.particles.enemyBlood(position.x, position.y);
 	 		} else if (actorID == 0){
 	 			Player.particles.blood(position.x,position.y+1);
 	 		}
 	 		//stateTime = 0;
 	 		switch (dType){
 	 		case DamageType.FIRE:
 	 				health -= damage;
 	 				flashTimeout = Punk.gTime+1000;
 	 				if (MathUtils.random(12) == 0) isOnFire = false;
 	 			break;
 	 		case DamageType.BREATH:
 	 				health -= damage;
 	 				flashTimeout = Punk.gTime+1000;
 	 			break;
 	 		case DamageType.BLUNT: 	 					
 	 					health -=damage;
 	 					flashTimeout = Punk.gTime+1000;
 	 			break;
 	 		case DamageType.SLASHING:
 	 			//if (actorID != 11)
 	 			health -=damage;
 	 			flashTimeout = Punk.gTime+1400;
 	 			break;
 	 		case DamageType.PIERCING: 
 	 			//if (actorID != 11)
 	 			health -= damage;
 	 			flashTimeout = Punk.gTime+400;
 	 			break;
 	 		case DamageType.EXPLOSION:
 	 				health -=damage;
 	 				flashTimeout = Punk.gTime+1400;
 	 			break;
 	 		case DamageType.POISON:	
 	 			//if (actorID != 11)
 	 			poisonDamage += damage;
 	 			flashTimeout = Punk.gTime+700;
 	 			break;
 	 		case DamageType.ZAP:
 	 			health -=damage;
				flashTimeout = Punk.gTime+1500;
				break;
 	 			
 	 		}
 	 		
 		}
 		return true;
 	}
 	public void updatePoisonDamage(long time){
 		if (poisonDamage > 0 && flashTimeout < time){
 			health-=1;
 			poisonDamage -=1;
 			isFlashing = true;
 			flashTimeout = time + 1000;//TODO adjust
 			
 		}
 		if (poisonDamage < 0) 
 			if (this instanceof Player){
 				Player play = (Player)this;
 				if (play.isTripping && poisonDamage > -2){//end of trip
 					play.isTripping = false;
 					play.isDoneTripping = true;
 	}
 				else if (!play.isTripping && poisonDamage < -12){//start trip
 					
 				}
 				
 			}
 	}
 	
 	
  	public void doExplosionForces(Vector2 force){
 		switch (actorID){
			case 5://bullets. gonna make them less succeptible
				force.mul(0.5f);
			
			case 1:
			case 2:
			case 3:
			
			case 6:
			case 7:
			case 11:
			case 16:
			case 19:
			case 23:
			case 47:
	 			body.setLinearVelocity(force);
	 			break;
	 			
			case 0: Player play = (Player)this;
			play.resetHead();
			body.setLinearVelocity(force);
			break;
			}
 	}
 	
 	public void doExplosionDamage(Vector2 distV, ComponentExplosion info, float accum, Player player){
 		int flameyness = 1;
 		int damage = 0;
 		int maxDist = info.size;
 		
 			maxDist = (int)((1f+accum)*maxDist);
 			damage = Math.max(maxDist - (int)distV.len(), 0);
			tmpV.set(distV);
			len = tmpV.len();
			//Gdx.app.log(TAG, "explosion, length:"+len+"maxdist:"+maxDist);
			if (len >10) tmpV.mul(0.5f);
			////Gdx.app.log(TAG, "explosion, before:"+tmpV);
			tmpV.mul(1f/len);
			////Gdx.app.log(TAG, "explosion, after:"+tmpV);
			distV.set(tmpV);
			distV.mul(-10f);
			if (damage > 0)
				doExplosionForces(distV);
			////.println("actor damage:" + distV);
 		takeDamage(damage, DamageType.EXPLOSION);
 		
	}
 	
 	public void doFallingDamage(GenericMob mob){
 		Vector2 vel = body.getLinearVelocity();
 		
 		float adjustedDamage = (vel.y)/4f;
 		if (vel.y < -40)
 		{
 			//Gdx.app.log("PA fallingDamage", "velY:"+vel.y);
	 		adjustedDamage /= 4;
	 		switch (equippedArmor){
	 		case LEATHER: adjustedDamage -= 1;
	 			break;
	 		case BRONZE: adjustedDamage -= 3;
	 			break;
	 		case GOLD: adjustedDamage -= 2;
	 			break;
	 		case MITHRIL: adjustedDamage -= 4;
	 			break;
	 		case CHAIN: adjustedDamage -= 2;
	 			break;
	 		}
	 		adjustedDamage = Math.abs(adjustedDamage);
	 		adjustedDamage = Math.min(adjustedDamage, 5);
	 		takeDamage(MathUtils.floor(adjustedDamage), DamageType.BLUNT);
 		}
 	}
 	public void enforceTerminalVelocity(PunkBodies monsterIndex){
 		//if (tmpV.set(body.getLinearVelocity()).len() > monsterIndex.max_velocity)
		//	body.setLinearVelocity(tmpV.mul(.8f));
 	}
 	int colID, colMeta;
	public void doCollision(PhysicsActor col, long time, Player player, PunkMap map, World world, PunkBodies monsterIndex, Contact contact)
	{
		colID = col.actorID;
		colMeta = col.actorMeta;
		boolean colIsFlammable = 
			(colID == 0 || colID == 2 || colID == 3 || colID == 11 || colID == 38);
		if (isOnFire && colIsFlammable) col.isOnFire = true;
		switch (actorID)
		{//player
		
		
		/*case 1://ITEM
				if (colID == 0 || colID == 2 || colID == 3 || colID == 11 || colID == 38){
					Item it = (Item)this;
					//falling damage!
					if (body.getLinearVelocity().y <-4){
						//col.takeDamage(2, DamageType.BLUNT, player);
						
						it.hasHitPlayer = true;
						it.animTimer = time;
					}
					if (colID == 0 && time > animTimer  && !it.hasHitPlayer){
						if (player.inv.hasFreeSlot() || player.inv.hasSpaceFor(it.actorMeta, it.state)){
							Gdx.app.log("PA", "collision with item, meta:"+it.meta);
							player.inv.addItem(it);
							
							it.hasHitPlayer = false;
							it.animTimer = time;
							it.deactivate();
							//it.boundBoxB.deactivate();
							//it.boundBoxS.deactivate();
						}
						//monsterIndex.playItemSound();
					}

					//remove body
					//deactivate();
					////.println(" item collected?");
				}
					
			break;
		//zombie
		case 2: switch (colID)
				{
					case 0: state = 6;
							animTimer = time+100;
							col.takeDamage(4, DamageType.SLASHING);
							////.println("zombie touched by player");
						break;
					case 3://
							state = 3;
							animTimer = time + 2000;
							//.println("zombie eating pig");
							health +=3;

						
						break;
					
					case 9://bottomBB
						//jump
						//body.setLinearVelocity(isLeft?-1.3f:1.3f, 4);
						
					break;
				}
			
		break;
		
		
		case 13:
		case 14:
		case 9:
		case 8:
		case 4: if (colID == 0 || colID == 45){
			col.doFallingDamage((GenericMob) col);
		}//col.doFallingDamage(player);
				else 
				{//player falling damage
						//if (!player.isClimbingRope)
					
					
					
				}
		break;
		
		case 22:
		/*case 5://bullet
			//.println("bullet collision with "+colID + " actor meta:" + actorMeta);
			
			switch (actorMeta){//different explosions here
			case 1:	col.takeDamage(1,DamageType.BLUNT, player);
				break;
			
				
			case 2:
				break;
				
			case 3:	map.explosionPool.createExplosion(ExplosionType.GRENADE, health, world, map, monsterIndex, position, time, player);
				break;
			case 4: map.explosionPool.createExplosion(ExplosionType.DWARFGRENADE, health, world, map, monsterIndex, position, time, player);
			break;
			case 5:	col.takeDamage(2,DamageType.POISON, player);
				break;
			case 6: col.takeDamage(3, DamageType.POISON, player);
					deactivate();
					break;
			}
			deactivate();
			if (player.headTarget == body)player.headTarget= player.body;
			
			//body.getWorld().destroyBody(body);
		break;*/
		
		/*case 16://dwarf
		
			if (colID == 17 && state == 0)//side BBs
			{
				//need to check for multiple hits
				////.println("sideBB, jumping dwarf");
				body.setLinearVelocity(tmpV.set(body.linVelWorld.x+(isLeft?-XRand.get(2, x):XRand.get(2, x)), 6));
				state = 1;
				animTimer = time + 500;
			}
			//bottom BB, resets state
			if (  colID == 9 && animTimer < time) 
				{
					if (state == 1 || state == 2) state = 0;
					else if (state == 7){
						state = 6;
						//Gdx.app.log("dwarf phyciscactor", "resetting angry state");
					}
					
					
				}
			
			break;
		case 20:
			if (colID == 17 && state == 0)//side BBs
			{
				//need to check for multiple hits
				////.println("sideBB, jumping dwarf");
				if (actorMeta == 1)//ninjas
				{
					body.setLinearVelocity(tmpV.set(body.linVelWorld.x+(isLeft?-XRand.get(2, x):XRand.get(2, x)), 8));
					state = 1;
					animTimer = time + 500;
				}
				isLeft = !isLeft;
			}
			//bottom BB, resets state
			if (  colID == 9 && animTimer < time) 
				{
					if (state == 1 || state == 2) state = 0;
					else if (state == 7){
						state = 6;
						//Gdx.app.log("dwarf phyciscactor", "resetting angry state");
					}
					
					
				}
			
			break;
		/*case 11://slime
				if (colID ==0)
					col.takeDamage(4, DamageType.POISON, player);
				switch (colID){
					case 17://slime+side
							if (animTimer < time){
								body.setLinearVelocity(tmpV.set(0,1));
								animTimer = time + 200;
							}
							
							
						break;
				
				}
				
			break;*/
		case 26://head button
				if (colID == 0){
					//make a beanstalk, destroy button
					deactivate();
					PhysicsActor act = map.chunkActors.add(24, 0, world, monsterIndex, tmpV.set(x,y+1));
					act.body.setLinearVelocity(tmpV.set(0,2));
					act.animTimer = time + 100000;
					
				}
			break;
		
		/*case 28: 
				Gdx.app.log("PA gren", "wrong!!!!!!!!!!!!!!!!!!!!!!!!!!");
			if (colID == 27 && (actorMeta == 6 || actorMeta == 12 || actorMeta == 10)){//grens hitting BBs
						Grenade aGren = (Grenade)this;
						aGren.blowUp(map, world, monsterIndex, time, player);
					}
				Grenade aGren = (Grenade)this;
				if (body.getLinearVelocity().len() > 5  &&(colID == 3 || colID == 2 || colID == 11 || colID == 10 || colID == 38 || (colID == 0&& state > 0) )){
					//collisions with monsters at speed
					switch (actorMeta){
					case 3: //dirt
							col.takeDamage(1, DamageType.BLUNT);
							
							//aGren.blowUp(map, world, monsterIndex,time, player);
							aGren.state = 14;
							break;
					case 4://rock
							col.takeDamage(2,DamageType.BLUNT);
							aGren.state = 14;
							//aGren = (Grenade)this;
							//aGren.blowUp(map, world, monsterIndex,time, player);
							break;
					case 6://slime projectile
						if (colID == 0){
							col.takeDamage(3, DamageType.POISON);
							aGren.blowUp(map, world, monsterIndex,time, player);
						}
								
							break;
					case 5:
						
						break;
					case 10:
						if (col.actorID != 38){
							col.takeDamage(1, DamageType.PIERCING);
							aGren.deactivate();
							Gdx.app.log("pa", "shuriken hit!");
						}
					case 12://ninja molotov
						if (colID == 0 || colID == 2 || colID == 3){
							aGren.blowUp(map, world, monsterIndex,time, player);
							col.isOnFire = true;
						}
						break;
				}
					
				 
				 }
				
				if (colID == 39 && breathTimeout < time && !player.isAimingPoi){//TODO this is strange
					body.setLinearVelocity(body.getLinearVelocity().mul(-2));
					breathTimeout = time + 500;
					//Gdx.app.log("PA collision ninja", "reverswing1111111111111111111111111111111111111111111111111111111111111");
				} 
			break;
	/*	case 39:
				if (!player.isAimingPoi && !player.isPrePoi && !player.isPostPoi && Punk.gameMode == 81){
					
					if (colID == 2 || colID == 3 || colID == 10 || colID == 11 || colID == 38 || colID == 55){
				
					//col.takeDamage(1, DamageType.PIERCING, player);
					float damage = player.activeTool.damage;
					
					
					col.takeDamage(damage, player.activeTool.type);
					Gdx.app.log("PA", "flail hit mob"+damage+ "id:"+player.getActiveID() + "after "+col.health);
					player.inv.reduceDurability(player.activeQuickSlot);
					if (!player.isPostPoi){
						player.isPostPoi = true;
						player.shootTimer = time + 300;
					}
					//kickback
					
					} else if (colID == 28){
						//aGren = (Grenade)col;
						//aGren.blowUp(map, world, monsterIndex, time, player);
						//col.deactivate();
						//that doesn't work for some reason. reersing them instead
						
					}
				}
			break;*/
		case 44://axe
			if (colID == 2 || colID == 3 || colID == 10 || colID == 11 || colID == 38 || colID == 55){
				float axeSpeed = Math.abs(body.getAngularVelocity());
				player.shootTimer = time + 40;
				int axeDamage = (int)((axeSpeed+1)/2)+PunkInventory.axeDamages[player.activeAxe];
				axeDamage = Math.max(5,axeDamage);
				if (!player.isPostAxe){
					if (col.takeDamage(axeDamage, DamageType.SLASHING)) player.inv.reduceDurability(player.activeQuickSlot);;
					player.isPostAxe = true;
				}
				
				Gdx.app.log("PA", "axe damage:"+axeDamage);
			}
			
			break;
			
		case 45:
			if (colID == 4)player.bridgeTimer = time + Player.JUMPLEEWAY;
			break;
		case 55: 
			
					Gdx.app.log("PA", "wrong collision update!!!!!!!!!!!!!!");////.println("zombie touched by player");
				break;
			
			
		
		}
	}
	
	
	
	public boolean updateRemoval(World world){
		if (markedForRemoval)
		{
			if (body != null)world.destroyBody(body);
			destroyBBs(world);
			body = null;
			markedForRemoval = false;
			
			//.println("PA removed:"+actorID);
			return true;
		}
		return false;
	}
	
	
	
	
	protected void destroyBBs(World world) {
		
		if (actorID == 53) BB.deactivate();
	}
	public PhysicsActor()
	{
		isFlashing = false;
	}
	
	public void deactivate(){
		isHostile = false;
		poisonDamage = 0;
		isOnFire = false;
		markedForRemoval = true;
		//body.setActive(false);
		
		//Gdx.app.log("physicsActor", "deactivating, actorid:" + actorID);
	}
	public PhysicsActor(int id, int meta){
		actorID = id;
		actorMeta = meta;
		
	}
	//public PhysicsActor(int iID, World world, PunkBodies monsterIndex, int spawnX, int spawnY){
	//	this(iID, world, monsterIndex, tmpV.set(spawnX, spawnY));
	//}
	public PhysicsActor(int ID, World world, PunkBodies monsterIndex, Vector2 spawnPos){
		body = world.createBody(monsterIndex.getBodyDef(ID, spawnPos));
		body.createFixture(monsterIndex.getFixture(ID));//.setUserData(this);
		actorID = ID;
		body.setUserData(this);
		//uniqueID = 
		position.set(body.getPosition());
		lastx = (int)position.x % 32;
		lasty = (int)position.y % 32;
		//body.setUserData(uniqueID);
		if (ID == 5) body.setBullet(true);
		mi = monsterIndex;
	}
	
	public void destroyBody(World world){
		world.destroyBody(body);
		//destroyBBs(world);
	}
	public Fixture bodyF;
	public void createBody(int ID, World world, PunkBodies monsterIndex, Vector2 spawnPos){
		//tmpV = monsterIndex.tmpV;
		body = world.createBody(monsterIndex.getBodyDef(ID, spawnPos));
		bodyF = body.createFixture(monsterIndex.getFixture(ID));//.setUserData(this);
		actorID = ID;
		body.setUserData(this);
		//uniqueID = 
		mi = monsterIndex;
		position.set(body.getPosition());
		x = MathUtils.floor(position.x);
		y = MathUtils.floor(position.y);
		//light = 15;
		lastx = (int)position.x % 32;
		lasty = (int)position.y % 32;
		body.setActive(true);
		//body.setLinearVelocity(tmpV.set(0,1));
		//body.setUserData(uniqueID);
		if (ID == 5) body.setBullet(true);
		//createBBs(world, monsterIndex);
		//if (ID == 21) System.printStackTrace();
		//playerDamageSnd = monsterIndex.hurtSnd;
	}
	public boolean updateDamage(long time, Player player, PunkBodies monsterIndex){
		//returns true if changing back from flashing
		if (isHoldingBreath && time > breathTimeout && !isFlashing && actorID != 11){
			takeDamage(1, DamageType.BREATH);
		}
		if (isOnFire && isImmuneToFire) isOnFire = false;
		
		if (isOnFire && !isFlashing) takeDamage(1,DamageType.FIRE);
		updatePoisonDamage(time);
		if (isFlashing && time > flashTimeout){
			isFlashing = false;
			 //body.setLinearDamping(monsterIndex.getBodyDef(actorID, position).linearDamping);
			return true;
		}
		return false;
	}
	public Block blockI;
	public void updateInside(PunkMap map, Vector2 vec, long time, float deltaTime, World world){
		tmpBlock = map.getBlock(x,y);
		blockI = tmpBlock;
		if (blockC != null){
			map.getBlockDef(blockC.blockID).mobFeetExit(map, this, blockC);
		}
		blockC = tmpBlock;
		//if (tmpBlock.blockType() ==0 || tmpBlock.blockType() == 2)
		//	stopSwimming(world);
			
		//if (x != lastx || y != lasty)
		//if (PunkMap.timeOfDay == DayTime.NIGHT) light = tmpBlock.light;
		//else light = Math.max(tmpBlock.light, tmpBlock.dayLight);
		
		map.getBlockDef(tmpBlock.blockID).mobFeet(map, this, tmpBlock, world);
		
		lightBits = tmpBlock.getLightBits();
		dayBits = tmpBlock.getDayBits();
		if (tmpBlock.blockType() >= 64)
			isInsideSolid = true;
		tmpBlock = map.getBlock(x,y+1);
		map.getBlockDef(tmpBlock.blockID).mobHead(map, this, tmpBlock);
		
		
	}
	
	
	static Vector2 tV = new Vector2(0,0);
	static Vector2 srcV = new Vector2(0,0);
	public static Vector2 getTargetPt(Vector2 source, Vector2 target, float s)
	{
		srcV.set(source);
		srcV.sub(target);
		float x = srcV.x;
		float y = srcV.y;//+2;
		float g = 10;
		float t = 0;
		//(g*g) * u2 + (x*g-s2)*u+x*x = 0
		//a = g*g
		//b = (x*g-s*S)
		//c = x*x
		
		//sovting for t;
		
		float a = g*g;
		//double b = x*g-s*s;
		//double c = x*x;
		
		float b = y*g-s*s;
		float c = y*y;
		
		float disc = b*b-4*a*c;
		
		float u1 = (float) ((-b+Math.sqrt(b*b-4*a*c))/(2*a));
		float u2 = (float) ((-b-Math.sqrt(b*b-4*a*c))/(2*a));
		float t1 = (float) -Math.sqrt(u1);
		float t2 = (float) -Math.sqrt(u2);
		
		if (t1 > 0 && t2 > 0)
			t = Math.max(t1,t2);
		////Gdx.app.log(TAG, "FS: time = "+a+" "+b+" "+c + "d:"+disc);
		
		tV.x = (float)(x + (g*t*t)/2);
		tV.y = (float)(y + (g*t*t)/2);
		////Gdx.app.log(TAG, "FS: target = "+targetV);
		
		float len = tV.len();
		tV.mul((float)(s/len));
		tV.mul(-1);
		return tV;
		
	}
	
	public void updateLeft(PunkMap map, Vector2 vec){
		updateLeft( map,  vec, x, y);
	}
	public void updateRight(PunkMap map, Vector2 vec){
		updateRight( map,  vec, x, y);
	}
	
	public void updateTop(PunkMap map, Vector2 vec){
		updateTop( map, vec, x, y+1);//
	}
	
	public void updateLeft(PunkMap map, Vector2 vec, int x, int y){
		switch (map.getBlock(x-1,y).blockType())//left
		{
		//air
		case 7:
		case 9:
		case 6:
		case 0 : vec.set(x+16, -200000f);
			break;
		//water
		case 1 :  vec.set(x-2.5f, y+000000f);
		break;
		//leaves
		case 2 :  vec.set(x+16, -100000f);
		break;
		//solid
		default:
		case 69:
		case 65:
		case 66:
		case 67:
		case 68:
		case 64 :  vec.set(x-0.5f, y+0f);
		break;	
		}
		
		if (map.getBlock(x-1, y+1).blockType() > 4)
			vec.set(x-0.5f, y+1f);
	}

	
	public void updateRight(PunkMap map, Vector2 vec, int x, int y){
		switch (map.getBlock(x+1,y).blockType())
		{
		//air
		case 7:
		case 9:
		case 6:
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
		default:
		case 69:
		case 65:
		case 66:
		case 67:
		case 68:
		case 64 :  vec.set(x+1.5f, y+0f);
			break;	
		}
		if (map.getBlock(x+1, y+1).blockType() > 4) vec.set(x+1.5f, y+1f);
		
	}
	
	public void updateBottom2(PunkMap map, Vector2 vec){

		//bottom
		vec.set(0,-0.0f);
		if (isLeft)
		{
			if (map.getBlock(x-1, y-1).blockType() >=64) 
				vec.add(x-0.5f, y-1f);
			
		} else
		{
			if (map.getBlock(x+1, y-1).blockType() >=64) 
				vec.add(x+1.5f, y-1f);
			
		}
	}
	
	public void updateTop(PunkMap map, Vector2 vec, int x, int y){
		//top
		vec.set(+10f, 13f);
		switch (map.getBlock(x,y+2).blockType())
		{
		case 7:
		case 9:
		case 6:
		//air
		case 0 :  
		//water
		case 1 :  
		//leaves
		case 2 :
				if (map.getBlock(x-1,y+2).blockType() >=64) vec.set(x-0.5f, y+1);
				else if (map.getBlock(x+1, y+2).blockType() >= 64) vec.set(x+1.5f, y+1);
			break;
		//solid
		case 3 :  vec.set(+111110.5f, y+2f);
		break;
		default:
		case 65:case 66:case 67:case 68:case 69:
		case 64: vec.set(x+0.5f, y+2f);
		break;	
		}
	}
	
	public void updatePA(PunkMap map, World world, float deltaTime, 
			Player player, long time, PunkBodies monsterIndex){
		
		if (health <0) 
		{
			//die(world, map);
			//Gdx.app.log("PA", "dead:"+actorID);
			return;
		}
		checkPosition(player, deltaTime, map, 0f);
		isLeft = (player.x<position.x);
		if (Math.random() > 0.53f) isLeft = !isLeft;
		//isVisible = (x > player.position.x-Punk.BRADIUSX && x >= player.position.x-Punk.BRADIUSX && y > player.position.y -Punk.BRADIUSY-2 && y<player.position.y + Punk.BRADIUSY);
		
	
		//Gdx.app.log(TAG, "updating PA:"+actorID);
		if (x != lastx || y != lasty){
			switch (actorID){
			case 23: //sticky blocks. stick when they hit a solid block
				
				//Gdx.app.log("PA", "sticky update "+position);
				if (map.getBlock(x,y).blockType() >= 64)
			{
				if (map.getBlock(lastx, lasty).blockType() <2){
					map.changeBlock(lastx, lasty, actorMeta, 0, true);
					die(world, map);
				}else {//travel up
					int prog = 0;
					while (prog < 64 && map.getBlock(x,y).blockType() >= 2){
						y--;
					}
					if (prog < 64){
						map.changeBlock(x, y,  actorMeta, 0, true);
					}
						
				}
				
			}break;
			case 24:if (actorMeta == 0){
					//write beanstalks
					map.changeBlock(x,y, 46, 0, true);
					//leaves
					
					if (y > 230){
						deactivate();
						//make a 26
						//PhysicsActor bean = new PhysicsActor(26, world, monsterIndex, tmpV.set(x,y));
						//bean.actorMeta = 0;
						//bean.body.setLinearVelocity(tmpV.set(0,2));
						//map.chunkActors.add(bean);
					}	
			}
			else if (actorMeta == 1){
				
			}
			
			break;
			/*case 5: //bullet metas
				switch(actorMeta)
				{
				case 5:
				case 1: if (map.getBlock(x,y).blockType() >3)
					{
						deactivate();
						if (player.headTarget == body)player.resetHead();
					}
					break;		
				case 3: 
					if (isFlashing || map.getBlock(x,y).blockType() >2) 
					{
						deactivate();
						if (player.getHeadTarget() != player.body) player.setHeadTarget(map.explosionPool.getLast().body);
						map.makeAFire(x, y);
					}
				break;
				case 6:if (isFlashing || map.getBlock(x,y).blockType() >2) 
				{
					
					map.explosionPool.createExplosion(ExplosionType.DWARFGRENADE, health, world, map, monsterIndex, position, time, player);
					deactivate();
					if (player.getHeadTarget() != player.body) player.setHeadTarget(map.explosionPool.getLast().body);
					map.makeAFire(x, y);
				}
				break;
				}//end of bullet meta 
			break;*/
			case 34:
				break;
			
			}
			
			lastx = x;
			lasty = y;
			
		}//end if changed
		//per-frame checks
		switch (actorID){
		case 29:
		case 24://animation dummy, just dies if it's done
				if (time > animTimer) die(world, map);
			break;
		
		//spawner
		case 42:
			if (time > animTimer){
				checkPosition(player, deltaTime, map, 0f);
				animTimer = time + 5000;
				if (isVisible){
					map.chunkActors.addFromSpawner(actorMeta, x, y, world, monsterIndex, map, time);
					//Gdx.app.log("PA", "making PA from spawner"+actorMeta);
				}
			}
			break;
		case 23:
			
		break;
		case 53:
			if (animTimer+PunkBodies.GIBTIME < time) deactivate();

			break;
		}
		
		
	}
	public void die(World world, PunkMap map){
		deactivate();
		//drop items/place blocks!
		switch (actorID){
		case 23://sticky block
			break;
		}
	}
	public void checkPlayerDista23213213213212nce(Player player){
		distanceFromPlayer = player.distanceToPlayer(x, y);
		distanceFromPlayerHead = player.distanceToPlayerHead(x,y);
	}
	public void checkPositionNoBody(Player player, float deltaTime){
		stateTime += deltaTime;
		distanceFromPlayer = player.distanceToPlayer(x, y);
		distanceFromPlayerHead = player.distanceToPlayerHead(x,y);
		isVisible = (distanceFromPlayerHead < Punk.visibleDistanceFromPlayer);
	}
	public boolean checkPosition(Player player, float deltaTime, PunkMap map, float f){
		tmpV.set(body.getPosition());
		position.lerp(tmpV, .00000000000001f);
		position.set(tmpV);
		x = MathUtils.floor(position.x);
		y = MathUtils.floor(position.y-f);
		stateTime += deltaTime;
		distanceFromPlayer = player.distanceToPlayer(x, y);
		distanceFromPlayerHead = player.distanceToPlayerHead(x,y);
		isVisible = (distanceFromPlayerHead < Punk.visibleDistanceFromPlayer);
		return true;
	}
	
	
	/*public static boolean checkVisible(Player player, int x, int y){
		return (player.distanceToPlayerHead(x,y) < player.zoomLevel * ((Punk.BWIDTH)>>1+2));
		
	}*/
	
	
	public TextureRegion getFrame(PunkBodies monsterIndex){
		
		return null;
	}
	public Vector2 getPos(){
		return body.getPosition();
	}
	public void setPos(Vector2 newpos){
		
	}
	public void setID(int newid){
		actorID = newid;
	}
	public int getID(){
		return actorID;
	}
	
	public int getstate(){
		return state;
	}
	public void setgghstate(int newst){
		state = newst;
	}
	@Override
	public void resetValues(int iID, int meta) {
		// TODO Auto-generated method stub
		actorID = iID;
		actorMeta = meta;
		isFlashing = false;
		isOnFire = false;
		isSwimming = false;
		
		switch (actorID){
		case 2: health = 4;
			break;
		case 3: health = 4;
			break;
		}
		
	}
	@Override
	public void createBBs(World world, PunkBodies monsterIndex) {
		
	}
	
	@Override
	public void destroyBBs(World world, PunkBodies monsterIndex) {
		
			if (actorID == 53 ) BB.deactivate();
		
	}
	
	
}

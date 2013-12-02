package com.niz.punk;


import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.JointEdge;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.niz.punk.PunkMap.DayTime;

public class BChunkActors extends Pool<PhysicsActor> implements Boss {
	
	protected PhysicsActor newObject()
	{
		return new PhysicsActor();
	}
	private Array<PhysicsActor> monsterList= new Array<PhysicsActor>();
	private PhysicsActor tmpMonster;// = new NonStaticActor();
	private Iterator<PhysicsActor> iter;
	//private Array<PhysicsActor> addQueue = new Array<PhysicsActor>();
	private Vector3 tmpVector  = new Vector3(0,0,0);
	private Vector2 tmpVector2 = new Vector2(0,0);
	private int tmpi;
	private int poolSize = 0;
	private String TAG = "ChunkActors";
	public BulletPool dwarfPool;
	public PGrenade grenadePool;//they get checked+updated by this pool. the rest have their own thing
	
	
	
	//public PNinja ninjaPool = new PNinja();
	//public PPlank plankPool = new PPlank();
	//public PSlowBullet slowBulletPool = new PSlowBullet();
	//public PTurret turretPool = new PTurret();
	public PGenericMob[] mobPool = {new PGenericMob(), new PGenericMob(), new PGenericMob() , new PGenericMob()};
	public PGenericMob[] passiveMobPool = {new PGenericMob(), new PGenericMob(), new PGenericMob() };
	//public PDoor doorPool = new PDoor();
	private World l_world;private PunkMap l_map;private Player l_player;private PunkBodies l_mi;
	//public MobInfo zombieInfo = new MobInfo();
	
	//public Array<MobInfo> mobInfos = new Array<MobInfo>();
	
	public BChunkActors(PunkBodies monsterIndex, World world, PunkMap map, Player player){
		l_player = player;
		l_map = map;
		l_world = world;
		l_mi = monsterIndex;
		//
		//mobPool[0] = new PGenericMob();
		//mobPool[1] = new PGenericMob();
		//mobPool[2] = new PGenericMob();
		//mobPool[1].addInfo(monsterIndex.enemyMobs[4]);
		//mobPool[1].addInfo(monsterIndex.enemyMobs[5]);
		
		//mobPool[2].addInfo(monsterIndex.enemyMobs[8]);
		//mobPool[2].addInfo(monsterIndex.enemyMobs[9]);
		
		//mobPool[0].addInfo(monsterIndex.enemyMobs[12]);
		//mobPool[0].addInfo(monsterIndex.enemyMobs[13]);
		for (int i = 0; i < 95; i++){
			//mobPool[0].addInfo(monsterIndex.enemyMobs[i]);
		}
		
		
//		passiveMobPool[0].addInfo(monsterIndex.sheepInfo[0]);
	}
	
	
	
	public Array<PhysicsActor> getMonsterList(){
		return monsterList;
	}
	
	@Override
	public void update(PunkMap map, World world, float deltaTime,
			Player player, long time, PunkBodies monsterIndex,
			BulletPool bulletP) {
		
		//Gdx.app.log(TAG, "chunkActors updating!!!!!!!!!!!!");
		iter = monsterList.iterator();
		while (iter.hasNext()){
			tmpMonster = iter.next();
			//Gdx.app.log(TAG, "update actor: "+tmpMonster.actorID);
			tmpMonster.updatePA(map, world, deltaTime, player, time, monsterIndex);
		}
		//Gdx.app.log(TAG, "chickens");
		int c = 0;
		
		
		//ninjaPool.updateMove(map, world, deltaTime, player, time, monsterIndex);
		for (int i = 0; i < mobPool.length; i++){
			mobPool[i].updateMove(deltaTime);
		}
		
		
		//doorPool.updateMove(deltaTime);
		//Gdx.app.log(TAG, ""+c++);
		
		//Gdx.app.log(TAG, ""+c++);
		//ropeEndPool.updateMove(map, world, deltaTime, player, time, monsterIndex);
		//Gdx.app.log(TAG, ""+c++);
		//plankPool.updateMove(map, world, deltaTime, player, time, monsterIndex);
		//turretPool.updateMove(map, world, deltaTime, player, time, monsterIndex);
		//slowBulletPool.updateMove(map, world, deltaTime, player, time, monsterIndex);
		
		grenadePool.updateMove(map, world, deltaTime, player, time, monsterIndex);
		//Gdx.app.log(TAG, ""+c++);
		//ropeLinkPool.update();
		//removals
		//ropePool.updateRemovals( world,  player,  map,  monsterIndex, time);
		//Gdx.app.log(TAG, "removals");
		updateRemovals(player, world);
		//Gdx.app.log(TAG, "removals done");
	}

	public void updateRemovals(Player player, World world){
		for (int i = 0; i < monsterList.size; i++){
		//if (monsterList.size > 0){
			tmpMonster = monsterList.get(i);
			if (tmpMonster.updateRemoval(world)){
				//monsterList.add(tmpMonster);
				;
				//if (tmpMonster.actorID == 28) grenadePool.free((Grenade)monsterList.removeIndex(i));
				//else 
				
				
				{
					free(monsterList.removeIndex(i));
					i--;
				}

				////Gdx.app.log(TAG,"update. removing actor:"+tmpMonster.actorID);
			}
		}
		
		/*iter = monsterList.iterator();
		while (iter.hasNext()){
			tmpMonster = (PhysicsActor)iter.next();
			////Gdx.app.log("chunkActors", "removing "+tmpMonster.actorID);
			if (tmpMonster.updateRemoval(world)){//free etc
				monsterList.removeValue(tmpMonster, true);
				if (tmpMonster.actorID != 28) free(tmpMonster);
				else grenadePool.free((Grenade)tmpMonster);
				
			}
				
			
		}*/
		
		
		//ninjaPool.updateRemovals(world, player);
		for (int i = 0; i < mobPool.length; i++){
			mobPool[i].updateRemovals(world, player);
		}
		
		
		//doorPool.updateRemovals(world, player);
		
		grenadePool.updateRemovals(world, player);
		
	//	ropeEndPool.updateRemovals(world, player);
		//plankPool.updateRemovals(world, player);
		//turretPool.updateRemovals(world, player);
		//slowBulletPool.updateRemovals(world, player);

		//grenadePool.updateRemovals(world, player);
	}
	
	



	@Override
	public void init(int ID, Vector2 position, World world,
			PunkBodies monsterIndex) {
		// load from disk?
		grenadePool = new PGrenade(l_map, monsterIndex, world);
		dwarfPool = new BulletPool(5, world, monsterIndex, tmpVector2);
	}

	@Override
	public void draw(Camera camera, SpriteBatch batch, BitmapFont font, PunkBodies monsterIndex) {
		// TODO Auto-generated method stub
		 iter = monsterList.iterator();
		while (iter.hasNext()){
			tmpMonster = iter.next();
			switch (tmpMonster.actorID){
			case 23: //tmpVector.set(tmpMonster.position.x-.5f, tmpMonster.position.y-.5f, 0);
			
			if (tmpMonster.isVisible) {
				//camera.project(tmpVector, 0, 0, Punk.RESX, Punk.RESY);
				Sprite s = monsterIndex.getBlockSprites(tmpMonster.actorMeta, 0);
				s.setPosition(tmpMonster.position.x-.5f, tmpMonster.position.y-.5f);
				s.setSize(1,1);
				s.draw(batch);
			}
			break;
			
		
			case 54:
			case 28:
						
						
						////Gdx.app.log("grenadePA:", "bulletV:"+tmpVector);
						
				break;
			
			case 29:
				if (tmpMonster.isVisible){
					float angle = MathUtils.radiansToDegrees *tmpMonster.body.getAngle();
					//tmpVector.set(tmpMonster.position.x-.5f, tmpMonster.position.y-.5f, 0);
					//camera.project(tmpVector, 0, 0, Punk.RESX, Punk.RESY);
					batch.draw(monsterIndex.bloodAnim.getKeyFrame(tmpMonster.stateTime, false),  tmpMonster.position.x-.5f, tmpMonster.position.y-.5f,
							.5f, 0, // the rotation center relative to the bottom left corner of the box
							  .5f, 2, // the width and height of the box
							  1, 1, // the scale on the x- and y-axis
							  angle);
				}
				break;
			case 30:
				
					float angle = MathUtils.radiansToDegrees *tmpMonster.body.getAngle();
					//tmpVector.set(tmpMonster.position.x-.5f, tmpMonster.position.y-.5f, 0);
					//camera.project(tmpVector, 0, 0, Punk.RESX, Punk.RESY);
					Sprite floatyS = monsterIndex.getItemFrame(tmpMonster.actorMeta, 0);
					/*floatyS.setPosition(tmpMonster.position.x-.5f, tmpMonster.position.y-.5f);
					floatyS.setRotation(angle);
					floatyS.setScale(2);
					floatyS.setOrigin(.5f,.5f);
					floatyS.draw(batch);*/
					batch.draw(floatyS, tmpMonster.position.x-.5f, tmpMonster.position.y-.5f,
							.5f,.5f, // the rotation center relative to the bottom left corner of the box
							  1,1, // the width and height of the box
							  2, 2, // the scale on the x- and y-axis
							  angle);
							
					//font.draw(batch, ""+tmpMonster.state, tmpMonster.position.x-.5f, tmpMonster.position.y+.5f);
				break;
			
			
			case 53:
				Sprite gibS = monsterIndex.gibSprites[tmpMonster.actorMeta][15];
				gibS.setPosition(tmpMonster.position.x-.5f, tmpMonster.position.y-.5f);
				gibS.draw(batch);
			break;	
				
			}
			
		}
		
		//ninjaPool.drawMonsters(camera, batch, monsterIndex);
		for (int i = 0; i < mobPool.length; i++){
			mobPool[i].drawMonsterItems(camera, batch, monsterIndex);

		}
		for (int i = 0; i < mobPool.length; i++){
			mobPool[i].drawMonsters(camera, batch, monsterIndex);
		}
		
		for (int i = 0; i < mobPool.length; i++){
			mobPool[i].drawMonsterParticles(camera, batch, monsterIndex);
		}
		
		//doorPool.drawMonsters(camera, batch, monsterIndex);
		
		//ropeEndPool.drawMonsters(camera, batch, monsterIndex);
		//plankPool.drawMonsters(camera, batch, monsterIndex);
		//turretPool.drawMonsters(camera, batch, monsterIndex);
		//slowBulletPool.drawMonsters(camera, batch, monsterIndex);
		grenadePool.draw(batch);
	}
	

	
	@Override
	public void clear(PunkMap map, World world) {
		Gdx.app.log(TAG, "clearing, size:" + monsterList.size);
		iter = monsterList.iterator();
		while (iter.hasNext()){
			tmpMonster = iter.next();
			tmpMonster.die(world, map);
			if (tmpMonster.updateRemoval(world)){
				tmpMonster.destroyBBs(world);
			}
		}
		monsterList.clear();
		
		
		//ninjaPool.clear(map, world);

		//ropeEndPool.clear(map, world);
	}
	public void resetHostile(){
		iter = monsterList.iterator();
		while ( iter.hasNext() ) 
			{
				tmpMonster = iter.next();
				tmpMonster.isHostile = false;
			}
		
		
		/*Iterator<MNinja> in = ninjaPool.monsterList.iterator();
		while ( in.hasNext() ) 
			{
				tmpMonster = in.next();
				tmpMonster.isHostile = false;
			}*/
		//TODO I could just do this with physicsactors
	}
	
	public PhysicsActor add(int ID, int meta, World world, PunkBodies monsterIndex, Vector2 spawnPos){
		PhysicsActor act = obtain();
		spawnPos.x += .5f;
		spawnPos.y += .5f;
		act.createBody(ID, world, monsterIndex, spawnPos);
		act.actorMeta = meta;
		act.resetValues(ID, meta);
		//act.body.setLinearVelocity(tmpVector2.set(0,2));
		monsterList.add(act);
		//Gdx.app.log(TAG,"adding actor:"+ID);
		return act;
	}
	
	
	public int validRopes = 2, validBridges = 2;
	/*public void updateRopes(long x, int y, PunkMap map, PunkBodies monsterIndex, World world){
		//remove if far away
		Iterator<Rope> iter = ropePool.list.iterator();
		int totRopes= 0, totBridges = 0;
		while (iter.hasNext()){
			Rope aRope = iter.next();
			if (aRope.isActive){
				//check for removals
				if (!aRope.inRange(x, y)){
					aRope.destroyLinks();
					aRope.isActive = false;
				} else {
					if (aRope.isRope)totRopes ++;
					else totBridges++;
				}
			}
		}
		//totals now in place
		iter = ropePool.list.iterator();
		//int totRopes= 0, totBridges = 0;
		while (iter.hasNext()){
			Rope aRope = iter.next();
			if (!aRope.isActive && aRope.inRange(x, y)){
				if (aRope.isRope && totRopes < validRopes){
					makeRopeLinks(aRope, map, monsterIndex, world);
					totRopes++;
					aRope.isActive = true;
				}
				if (!aRope.isRope && totBridges < validBridges){
					makeRopeLinks(aRope, map, monsterIndex, world);
					totBridges++;
					aRope.isActive = true;

				}
			}
		}
		
	}
	public Rope addRope(){
		return ropePool.addRope();
	}*/
	Vector2 srcLoc = new Vector2();
	Vector2 destLoc = new Vector2();
	Vector2 tmpV = new Vector2();
	
	//RevoluteJointDef jd = new RevoluteJointDef();
	/*public void makeRopeLinks(Rope aRope, PunkMap map, PunkBodies monsterIndex, World world){
		
		RopeLink link = null;
		int numberOfJoints = 8;
		FixtureDef fix = monsterIndex.getRopeFixture(aRope.isRope);
		int linkID = aRope.isRope?31:35;
		srcLoc.set(aRope.src.position);
		destLoc.set(aRope.dest.position);
		aRope.linkLength = destLoc.dst(srcLoc)/numberOfJoints;
		float angle = (srcLoc.tmp().sub(destLoc).angle())*MathUtils.degreesToRadians;

		for (int i = 0; i < numberOfJoints; i++){
			link = addRopeLink(aRope.isRope);
			aRope.links[i] = link;
			link.parentRope = aRope;

			tmpV.set(aRope.src.position);
			float lerpFactor = (float)i/(float)numberOfJoints;
			tmpV.lerp(aRope.dest.position, lerpFactor);
			//Gdx.app.log("grenade", "lerp:"+lerpFactor+"dest"+aRope.dest.position+" src"+aRope.src.position);
			link.body = world.createBody(monsterIndex.getRopeBodyDef(aRope.linkLength, tmpV, angle));
			link.body.createFixture(fix);
			link.actorID = linkID;
			link.body.setUserData(link);
			link.mi = monsterIndex;
			link.position = link.body.getPosition();
			link.x = MathUtils.floor(link.position.x);
			link.y = MathUtils.floor(link.position.y);

			link.lastx = (int)link.position.x % 32;
			link.lasty = (int)link.position.y % 32;
			link.body.setActive(true);
			link.body.setFixedRotation(false);
			link.body.setTransform(link.position,angle);
			link.actorMeta = i;
			link.createBBs(world, monsterIndex);
			tmpV.set(link.position);		
			if (i>0){
				aRope.links[i-1].linkBack = aRope.links[i];
				jd.initialize(aRope.links[i-1].body, link.body, tmpV);
				aRope.joints[i] = (RevoluteJoint)world.createJoint(jd);
			} else {	
				jd.initialize(aRope.src.body, aRope.links[0].body, tmpV);
				aRope.joints[0] =(RevoluteJoint)world.createJoint(jd);
			}
		}
		tmpV.set(aRope.dest.position);
		link.linkBack = aRope.dest;
		jd.initialize(aRope.dest.body, link.body, tmpV);
		aRope.joints[numberOfJoints] = (RevoluteJoint)world.createJoint(jd);
	}*/
	
	/*public void createRope(boolean isRope, PhysicsActor blockSrc, PhysicsActor blockDest, boolean attachSrc, boolean attachDest, World world, PunkBodies monsterIndex, PunkMap map){
		//FixtureDef fix = isRope?monsterIndex.getRopeFixture(0):monsterIndex.fixtures[35];;
		//int linkID = isRope?31:35;
		
		RopeEnd srcEnd = spawnRopeEnd(34, 0, world, monsterIndex, blockSrc.position);
		//PhysicsActor src = ropeEnd;
		
		RopeEnd destEnd = spawnRopeEnd(34, 0, world, monsterIndex, blockDest.position);
		//PhysicsActor dest = destRopeEnd;
		
		
		int numberOfJoints = Gdx.app.getPreferences("MMPrefs").getInteger("ropeJoints");
		numberOfJoints = 8;
		srcLoc.set(blockSrc.position);
		destLoc.set(blockDest.position);
		//float angle = (srcLoc.tmp().sub(destLoc).angle())*MathUtils.degreesToRadians;
		
		//if (destLoc.dst(srcLoc) > 80) return;
		
		float linkLength = destLoc.dst(srcLoc)/numberOfJoints;
		Gdx.app.log("chunkActors", "making rope:"+linkLength+"src:"+srcEnd.position+" dest: "+destEnd.position+"attach:"+attachSrc+attachDest);
		
		Rope aRope = addRope();
		//aRope.linkLength = linkLength;
		aRope.set(srcEnd, destEnd, linkLength, isRope);

		///////TODO
	
		{//if (attachSrc){
			jd.initialize(blockSrc.body, srcEnd.body, blockSrc.position);
			world.createJoint(jd);
			if (map.getBlock(srcEnd.x, srcEnd.y).blockType() != 4) map.changeBlock(srcEnd.x, srcEnd.y, 38, 0, true);//blockType() != 4?
		}
		{//if (attachDest){
			jd.initialize(blockDest.body, destEnd.body, blockDest.position);
			world.createJoint(jd);
			if (map.getBlock(destEnd.x, destEnd.y).blockType() != 4) map.changeBlock(destEnd.x, destEnd.y, 38, 0, true);
		}
		
		//makeRopeLinks( aRope,  map,  monsterIndex,  world);
		aRope.isActive = false;
		
		
	}*/
	public void add(PhysicsActor actor){
		monsterList.add(actor);
	}
	
	
	
	public PhysicsActor getBullet(int type){
		PhysicsActor act = obtain();
		monsterList.add(act);
		act.actorID = 5;
		act.actorMeta = type;
		act.isFlashing = false;
		return act;
	}
	
	public PhysicsActor getGrenadeold(int type){
		Grenade act = grenadePool.obtain();
		monsterList.add(act);
		act.resetValues(28, type);
		switch (type){
		default: act.info.lifeTicks = 16;
		
		case 10: act.info.lifeTicks = 24;
		}
		act.hasExploded = false;
		return act;
	}
	public PhysicsActor getGrenade(){
		
		Grenade act = grenadePool.obtain();
		//act.set(info, factionID);
		//act.isArmed = false;
		grenadePool.add(act);
		
		return act;
	}
	
	
	
	
	@Override
	public void goHostile() {
		// TODO Auto-generated method stub

	}
	
	
	/*public boolean spawnSlowBullet(float x, float y, float angle, float speed, int type, World world, PunkBodies monsterIndex){
		
		if (slowBulletPool.monsterList.size < slowBulletPool.poolSize ){
			MSlowBullet bul = slowBulletPool.createMonster(world, monsterIndex, tmpVector2.set(x,y), angle, speed, type);
			bul.animTimer = Punk.gTime + 12000;
			//bul.body.setLinearVelocity(MathUtils.random(-8,8), 25);
			//Gdx.app.log("chunkActors", "spawn pig "+pigPool.monsterList.size+" "+pigPool.poolSize);
		}
			
		
		else return false;
		return true;
	}*/
	public PhysicsActor spawnGib(float x, float y, int type, World world, PunkBodies monsterIndex){
		
		PhysicsActor act = add(53, type, world, monsterIndex, tmpV.set(x,y));
		act.animTimer = Punk.gTime+MathUtils.random(1500);;
		act.BB = add(27,0,world, monsterIndex, tmpV);
		return act;
	}
	public boolean spawnTurret(float x, float y, int type, World world, PunkBodies monsterIndex){
		
		/*if (turretPool.monsterList.size < turretPool.poolSize ){
			MTurret tur = turretPool.createMonster(world, monsterIndex, tmpVector2.set(x,y), type);
			//bul.body.setLinearVelocity(MathUtils.random(-8,8), 25);
			//Gdx.app.log("chunkActors", "spawn pig "+pigPool.monsterList.size+" "+pigPool.poolSize);
		}
			
		
		else return false;*/
		return true;
	}
	public boolean spawnPlank(boolean isDiagonal, Vector2 position, float angle, float length, float width, int type, World world, PunkBodies monsterIndex){
		//MPlank plank = plankPool.createMonster(isDiagonal, angle, length, width, type, world, monsterIndex, position);
		
		//Gdx.app.log("chunkActors", "spawned plank "+length);
		return true;
	}
	//spawns return true if successful
	
	/*public void spawnSingleNinja(long x, int y, World world, PunkBodies monsterIndex, long time){
		MNinja nin =ninjaPool.createMonster(world, monsterIndex, tmpVector2.set(x,y));
		nin.boundBoxB = 
			add(9, 0, world, monsterIndex, tmpVector2);
		nin.body.setLinearVelocity(MathUtils.random(-8,8), 5);
		nin.health = PunkBodies.NINJAHEALTH;
		
		Gdx.app.log("actors", "spawned ninja @ "+x+":"+y);
		nin.actorMeta = 0;
		nin.weaponBits = MathUtils.random(2);
		nin.bodyBits = MathUtils.random(2);
		nin.moveBits = MathUtils.random(2);
		nin.animTimer = time+500;
		nin.isSpawning = true;
	}
	public boolean spawnNinja(int x, int y, World world, PunkBodies monsterIndex, PunkMap map, long time){
		if (ninjaPool.monsterList.size < ninjaPool.poolSize ){
			Gdx.app.log("actotrs", "spawning ninja");
			int ninjaCount = Math.abs(map.chunkC.heightID-1);
			ninjaCount = Math.min(ninjaCount, 6);
			
			//look around randomly for spots
			int iterations = 0;
			while (ninjaCount > 0 && iterations < 30){
				int lookX = MathUtils.random(-iterations-10,iterations+10);
				lookX += x;
				int lookY = MathUtils.random(-20,20);
				lookY += y;
				if (map.getBlock(lookX, lookY).blockType() <4 && map.getBlock(lookX, lookY+1).blockType() <4){
					spawnSingleNinja(lookX,lookY, world, monsterIndex, time);

					ninjaCount--;
				}
				
				
				iterations++;
			}
				
			
		}
		else {
			Gdx.app.log("actors", "can't spawn ninja!"+ninjaPool.poolSize);
			return false;
		}
		return true;
	}*/
	

	
	
	/*public void trySpawnGenericMob(int x, int y, Block b, World world, PunkBodies monsterIndex, Player player, PunkMap map){
		int zone = 0;
		if (y < PunkMap.averageGroundHeight-300) zone = 2;
		else if (y < PunkMap.averageGroundHeight-16) zone = 1;
		
		if (mobPool[zone].monsterList.size >= mobPool[zone].poolSize) return;
		
		//start at random position, check for validity, loop back to start pt
		int size = mobPool[zone].infoList.size;
		int start = MathUtils.random(size);
		int progress = start;
		boolean done = false;
		
		//Gdx.app.log("actors", "trying to spawnw, zone: "+zone);
		while (progress < size && !done){
			
			//Gdx.app.log("actors", "looking"+x+","+y);
			if (mobPool[zone].
					infoList.get(progress).canSpawn(b, zone)){
				done = true;
				//mobPool[zone].createMonster(x,y, mobPool[zone].infoList.get(progress), null, world, monsterIndex, player, map);
				//map.getBlock(x,y).set(11,0);
				//Gdx.app.log("actors", "found");
				return;
			}
			progress++;
		}
			
		progress = 0;
		while (progress < start && !done){
			
			//Gdx.app.log("actors", "looking"+x+","+y);
			if (mobPool[zone].
					infoList.get(progress).canSpawn(b, zone)){
				done = true;
				//mobPool[zone].createMonster(x,y, mobPool[zone].infoList.get(progress), null, world, monsterIndex, player, map);
				//map.getBlock(x,y).set(11,0);
				//Gdx.app.log("actors", "found");
				return;
			}
			progress++;
		}
		
		
		
	}
	
	
	
	public void trySpawnPassiveMob(int x, int y, Block b, World world,
			PunkBodies monsterIndex, Player player, PunkMap map) {
		
		int zone = 0;
		if (y < PunkMap.averageGroundHeight-300) zone = 2;
		else if (y < PunkMap.averageGroundHeight-16) zone = 1;
		
		if (passiveMobPool[zone].monsterList.size >= passiveMobPool[zone].poolSize) return;
		
		//start at random position, check for validity, loop back to start pt
		int size = passiveMobPool[zone].infoList.size;
		int start = MathUtils.random(size);
		int progress = start;
		boolean done = false;
		
		//Gdx.app.log("actors", "trying to spawnw, zone: "+zone);
		while (progress < size && !done){
			
			//Gdx.app.log("actors", "looking"+x+","+y +  " bl "+b.blockID);
			if (passiveMobPool[zone].
					infoList.get(progress).canSpawn(b, zone)){
				done = true;
				//passiveMobPool[zone].createMonster(x,y, passiveMobPool[zone].infoList.get(progress), null, world, monsterIndex, player, map);
				//map.getBlock(x,y).set(11,0);
				//Gdx.app.log("actors", "spawninf passive");
				return;
			}
			progress++;
		}
			
		progress = 0;
		while (progress < start && !done){
			
			//Gdx.app.log("actors", "looking"+x+","+y+  " bl "+b.blockID);
			if (passiveMobPool[zone].
					infoList.get(progress).canSpawn(b, zone)){
				done = true;
				//passiveMobPool[zone].createMonster(x,y, passiveMobPool[zone].infoList.get(progress), null, world, monsterIndex, player, map);
				//map.getBlock(x,y).set(11,0);
				//Gdx.app.log("actors", "found");
				return;
			}
			progress++;
		}
		
		
		
	}*/
	//RevoluteJointDef jd = new RevoluteJointDef();
	
	public void destroyPhysicsBlocks(long x, int y, World world, PunkBodies mi){
		Iterator<PhysicsActor> iter = monsterList.iterator();
		while (iter.hasNext()){
			PhysicsActor act = iter.next();
			if (act.x == x && act.y == y){
				if (act.actorID == 26 || act.actorID == 33 || act.actorID == 42 ) act.deactivate();
				else if (act.actorID == 7 ){
					act.deactivate();
					//world.destroyJoint(act.body.getJointList().get(0).joint);
				
				} 
			}
		}
		
		Iterator<GenericMob> it = mobPool[3].monsterList.iterator();
		while (it.hasNext()){
			PhysicsActor act = it.next();
			if (act.x == x && act.y == y){
				act.deactivate();
			}
		}
	}



	public void destroyAll(Player player, World world) {
		mobPool[0].destroyAll();
		mobPool[1].destroyAll();
		mobPool[2].destroyAll();
		
		grenadePool.destroyAll();
		//player.deactivate();
		/*if (player.boundBoxB != null 
				&& player.boundBoxB.body != null && player.boundBoxB.body.getFixtureList().size() > 0)
			world.destroyBody(player.boundBoxB.body);//deactivate();
		if (player.boundBoxT.body != null && player.boundBoxT != null && player.boundBoxT.body.getFixtureList().size() > 0)
			world.destroyBody(player.boundBoxT.body);//deactivate();
		if (player.boundBoxL.body != null && player.boundBoxL != null && player.boundBoxL.body.getFixtureList().size() > 0)
			world.destroyBody(player.boundBoxL.body);//deactivate();
		if (player.boundBoxR.body != null && player.boundBoxR != null && player.boundBoxR.body.getFixtureList().size() > 0)
			world.destroyBody(player.boundBoxR.body);//deactivate();*/
		//if (player.brake != null)world.destroyBody(player.brake.body);//deactivate();
		//if (player.hand != null)world.destroyBody(player.hand.body);//deactivate();
		if (player.head != null)world.destroyBody(player.head.body);//deactivate();
		//if (player.body != null)world.destroyBody(player.body);//deactivate();

		updateRemovals(player, world);
		monsterList.clear();
		//removeAll();
		//zombiePool.monsterList.clear();
		//ninjaPool.drawMonsters(camera, batch, monsterIndex);
		//mobPool.drawMonsters(camera, batch, monsterIndex);
		//chickenPool.drawMonsters(camera, batch, monsterIndex);
		//slimePool.monsterList.clear();
		//snakePool.monsterList.clear();
		//spiderPool.monsterList.clear();
		//ropeEndPool.monsterList.clear();
		//plankPool.drawMonsters(camera, batch, monsterIndex);
		//turretPool.drawMonsters(camera, batch, monsterIndex);
		//slowBulletPool.drawMonsters(camera, batch, monsterIndex);
		/*Iterator<Body> bodyI = world.getBodies();
		while (bodyI.hasNext()){
			Body b = bodyI.next();
			world.destroyBody(b);
		}*/
		Gdx.app.log(TAG, "DESTROY ALL ");
	}



	



	public void createRubberBlock(int x, int y, PunkMap map) {
		mobPool[3].createMonster(x, y, 32, 0, 0, 0, l_world, l_mi, l_player, map);
		
	}



	/*public boolean spawnFromFlower(int x, int y, World world, PunkBodies mi, byte meta, Player player, PunkMap map) {
		// TODO Auto-generated method stub
		
		if (mobPool[0].monsterList.size >= mobPool[0].poolSize) return false;
		//Gdx.app.log("TAG", "spawning from flower" + (meta/4));
		if (player.distanceToPlayer(x, y) < PunkBodies.SPAWNMIN) return false;
		switch (Chunk.getTemp(x)){
		case 6:case 5:
			if ((meta % 4) == 0)mobPool[0].createMonster(x, y, l_mi.basiliskInfo[MathUtils.random(3)], PunkBodies.factions[89], world, mi, player, map);
			else mobPool[0].createMonster(x, y, l_mi.scorpionInfo[MathUtils.random(4)], PunkBodies.factions[88], world, mi, player, map);
			break;
			
		case 4:case 3:case 2:
			if ((meta % 4) == 0)mobPool[0].createMonster(x, y, l_mi.wolfInfo[MathUtils.random(1)], PunkBodies.factions[85], world, mi, player, map);
			else mobPool[0].createMonster(x, y, l_mi.chickenInfo[0], PunkBodies.factions[84], world, mi, player, map);
			break;
		
		 case 1:case 0:case -1:case -2:case -3:
			if ((meta % 4) == 0)mobPool[0].createMonster(x, y, l_mi.dogInfo[MathUtils.random(5)], PunkBodies.factions[87], world, mi, player, map);
			else mobPool[0].createMonster(x, y, l_mi.sheepInfo[0], PunkBodies.factions[86], world, mi, player, map);
			break;
		case -4:case -5:case -6:
			if ((meta % 4) == 0)mobPool[0].createMonster(x, y, l_mi.yetiInfo[MathUtils.random(5)], PunkBodies.factions[91], world, mi, player, map);
			else mobPool[0].createMonster(x, y, l_mi.reindeerInfo[0], PunkBodies.factions[90], world, mi, player, map);
			break;
		}

		return true;
	}*/



	public void addFromSpawner(int actorMeta, int x, int y, World world,
			PunkBodies monsterIndex, PunkMap map, long time) {
		// TODO Auto-generated method stub
		
	}



	/*public void trySpawn(int x, int y, Block tmpB) {
		//MobInfo inf = l_mi.getRandomMobInfo();
		Iterator<MobInfo> i = mobPool[0].infoList.iterator();
		while (i.hasNext()){
			MobInfo inf = i.next();
			if ((inf.timeFlags & PunkMap.currentTimeFlag) != 0
					&&
					inf.canSpawn(tmpB)
					
					)
			mobPool[0].createMonster(x, y, inf, PunkBodies.factions[inf.defaultFaction], l_world, l_mi, l_player, l_map);

		}
		
	}*/
	
	
}

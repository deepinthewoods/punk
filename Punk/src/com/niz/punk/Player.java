package com.niz.punk;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.utils.Array;
import com.niz.punk.PunkMap.BlockDamageType;


public class Player extends GenericMob
{
	public static final String PATH_STORED_ITEMS = "storeditems.inv";
	public static final String PATH_STATS = "player.stats";;
	public static final String PATH_GAME_INFO = "player.inf";

	private String TAG = "player";
	public static PlayerPermissions permissions = new PlayerPermissions();
	public final float GRAPPLEFREQ = .1f, POIKICKBACK = 3f;
	public static final int MAXHEALTH = 16, JUMPLEEWAY = 100;
	//private static int[] inventory = new int[256];
	public static int RANGE = 100, AXEBLOCKDAMAGE = 800, FLAILBLOCKDAMAGE = 900, HANDBLOCKDAMAGE = 600;
	public final int DIGRANGE = 16;
	
	public static final int GRAPPLECOUNT = 2;
	private static final float JUMPSIMULATETIME = .5f;
	public static final float THROWHANDDISTANCE = 2;
	
	
	
	public static final int PREDELAY = -5, POIPREDELAY = -5;
	
	public static final int PLACE_RANGE = 64;
	public static final float THROW_REACH = .2f;
	
	
	
	private float speedLimitNormal = 18, speedLimitNormal2 = speedLimitNormal * speedLimitNormal,
			speedLimitSwimming = 10, speedLimitSwimming2 = speedLimitSwimming * speedLimitSwimming	
			, speedLimit = speedLimitNormal, speedLimit2 = speedLimitNormal2;
	
 	public int leftLoad = 0;
 	public int rightLoad = Punk.CHUNKSIZE-1;
 	public int topLoad = Punk.CHUNKSIZE;
 	public int bottomLoad = 0;
 	public int activeAxe, activeWand;
 	//public WeaponInfo activeFlail;
 
 	//public boolean isLeft = false;
 	private int moveDirection;
 	
 	
 	private Vector2 lastVelocity = new Vector2();
 	//public int state = 0;	
 	//public int spawnLimitLastX = 120;
 	public FlightInfo activeFlight;// = new FlightInfo();
 	public int activeFlightSlot;
 	public boolean poiIsUp, poiIsLeft, isDisabled;
 	public boolean isShootingWand = false, isGliding = false, isFlying, isThrusting;
 	public boolean isDigging = false, isGrappling = false, isAimingGrapple = false, isClimbingRope = false, 
 			isHoldingRope = false, isStoppedClimbing = false
 	, playerPressed = false, isDoneDigging = false, 
 	climbButtonUpValid = false, climbButtonDownValid = false,
 	isShooting = false, hops = false, isWallSliding = false, wallSlideL = true, wallSlide1 = false;
	public boolean isSlinging = false, isAiming = false, isFalling = false, isClimbing = false, isJumping = false;
	public boolean  isTripping = false, isDoneTripping = false,
	isOnChest = false, isOnChestBlock = false, isFallingThroughBridge = false;
	public boolean isPoiing = false, isAimingPoi = false, isPostPoi = false, isPrePoi = false, isAimingAxe= false, isAxeing= false, isPostAxe,
			axeIsLeft = false, isThrowing = false, isDoneThrowing = false, isSwimmingUp;
	//isThrowing: touch released, throw happens when hand gets close to body, isDoneThrowing: used for emptying the hand, uses animTimer
	private float swimDamping = 3f;
	
	public float throwLength, throwAngle;
	public int viewingSignID = -1;
	//public long aimTimer = 0;
	public long headTimer = 0;
	public float jumpTimer = 0;
	public long shootTimer;
	public long stunTimer;
	public long climbTimer = 0, touchTimer = 0;
	public long damageTimer = 0;
	public int jumpRunCount = 0;
	public float aimStrength = 0;
	public boolean isPlacingBlock = false;
	//public boolean isDonePlacingBlock = false;
 	public long digTimeout = 0;
 	public static float zoomLevel = 2f;
 	public float maxZoomLevel = 3f;
	public float minZoomLevel = .5f;
 	//public static PunkInventory inventory  = new PunkInventory(0);
 	//public static PunkInventory storedInventory = new PunkInventory(0);
	//public float stateTime = (float)Math.random();
	private Vector2 tmpVP = new Vector2(0,0);
	private Vector2 tmpV2 = new Vector2(0,0);
	public Vector2 handTarget = new Vector2();
	public Vector2 screenPosition = new Vector2(0,0);
	public int outerSpawnLimitL = 0, outerSpawnLimitR = 0;
	
	public static int stats_deaths = 0, stats_kills = 0, stats_pigs_saved = 0;
	public float globalTime;
	public int globalMinute;
	public float slingScale = 1;
	public PhysicsActor head;
	public PhysicsActor headTarget = null;
	public transient DistanceJointDef neckDef;
	public transient DistanceJoint neck;
	public transient int activeQuickSlot = 0, activeAmmoSlot = 1;
	//public BlockLoc spawnPt = new BlockLoc();
	public static GameInfo gameInfo;
	//String saveLoc = new String();
	public long bridgeTimer;
	public int digProgress = 0;
	

	public PhysicsActor poi = new PhysicsActor();
	public Body poiBody2;
	public PhysicsActor axe = new PhysicsActor();
	public RevoluteJointDef axeJointD = new RevoluteJointDef();
	public RevoluteJoint axeJoint;
	public Mesh poiString;
	public float[] poiStringVertices = new float[8];
	public short[] poiStringIndices = new short[]{0,1};
	public float poiAngle;
	public long poiTimer;
	public PhysicsActor hand;
	public static ParticlePool particles;
	public Stats stats;
	private PunkBodies l_mi;
	private Punk main;

	
	public BlockLoc[] grappleLocs = new BlockLoc[GRAPPLECOUNT];
	public BlockLoc grappleLoc = new BlockLoc();
	public boolean[] grappleValid = new boolean[GRAPPLECOUNT];
	public Body grappleAnchorBody;
	public float grappleAccumulator = 0;
	public int lastCreatedGrapple = 0;
	public float grappleLength;
	public Component deathComponent;
	//public Fixture footF, footF2;
	public float lastPoiDistance = 10, lastPoiTargetDistance = 10, lastLiftAngle;;
	public ParticleEffect wandParticle, slideParticle;
	public float jumpMultiplier = 1f, speedMultiplier = 1.0f;
	public boolean stopsTime = false;
	public float simulateTime;
	public int doubleJumpsUsed, wallJumpsUsed, doubleJumpsTot = 0, wallJumpsTot = 2;
	
	public static boolean targettingMob = false;
	public static float flyTouchAngle;
	//public MeleeWeaponInfo activeMelee;
	
	
	
	public void initPlayer(int gameid, int seedid){
		gameInfo.gameID = gameid;
		gameInfo.gameSeed = seedid;
		
	}
	public void mergeStats(GameInfo old, GameInfo ne){
		
		old.set(ne);
		old.savedPosition.set(x,y);
		
	}
	
	static StringBuilder pathBuilder = new StringBuilder();
	public void saveToDisk(boolean putInv){
		
		if (putInv)
		{
				
				//Gdx.app.log("Player", "cache info:\n\n"+oldInfo);
				//modify to suit current info
				gameInfo.seconds = (long)globalTime;
				//globalTime = 0;//globalTime % 60;
				
				//Gdx.app.log("player", "before merge, gi\n\n"+gameInfo);
				
				
				//Gdx.app.log("player", "after merge, gi\n\n"+gameInfo);
				stats.set(inv);
				pathBuilder.setLength(0);
				Punk.getSaveLoc(pathBuilder);
				pathBuilder.append(PATH_STATS);
				stats.writeToFile(Gdx.files.external(pathBuilder.toString()));
				;
				//pathBuilder.setLength(0);
				//Punk.getSaveLoc(pathBuilder);
				//pathBuilder.append(PATH_STORED_ITEMS);
				//FileHandle storedInvHandle = Gdx.files.external(pathBuilder.toString());
				//Gdx.app.log("player", "saving stored items"+pathBuilder.toString());
				//storedInventory.writeToFile(storedInvHandle);

			
		} else {
			
		}
		
		pathBuilder.setLength(0);
		Punk.getSaveLoc(pathBuilder);
		pathBuilder.append(PATH_GAME_INFO);
		FileHandle storedInvHandle = Gdx.files.external(pathBuilder.toString());
		Gdx.app.log("player", "saving gameinfo"+pathBuilder.toString());
		gameInfo.savedPosition.set(x,y);
		Gdx.app.log(TAG, "saving info"+pathBuilder.toString());

		gameInfo.writeToFile(Gdx.files.external(pathBuilder.toString()));
		
		//saveLoc = "mysticalmayhem/saves/" +gameInfo.gameType+"/"+"game"+ gameInfo.gameID + "/player.stats";
		
		//gameInfo.clear();
		
		
	}
	
	public void setInfo(GameInfo info, Stats stats){
		gameInfo = info;
		pathBuilder.setLength(0);
		Punk.getSaveLoc(pathBuilder);
		pathBuilder.append(PATH_GAME_INFO);
		FileHandle f = Gdx.files.external(pathBuilder.toString());
		gameInfo.writeToFile(f);
		this.stats = stats;
		//inventory = new PunkInventory(this);
		//inventory.rb.addCrafts();
		
		//inventory.clear();
		inv.set(stats);
		//health = info.health;
		//Gdx.app.log("player", "playerinfo:"+gameInfo.spawnPosition);
		stats_kills = 0;
		globalTime = gameInfo.seconds;
		globalMinute = (int) (globalTime / 60);
		calculatePerks();
		
	}
	
	public Player(Vector2 vec, Punk main){
		//this(vec.set(0,0), info.gameID, info.gameSeed);
		super();
		//info = new MobInfo();
		//info.set(0, new ComponentAttack(), new CMoveNone(), new CUpdateDoor(-199999), new ComponentAnimation(), false, false, 10000000, 0, 1, main.monsterIndex.hurtSndP, false, false);
		this.main = main;
		
			//globalTime = 0;
		
		
		grappleLocs[0] = new BlockLoc();
		grappleLocs[1] = new BlockLoc();
		
		/*x = MathUtils.floor(vec.x);
		y = MathUtils.floor(vec.y);
		position = new Vector2(0,0);
		position.set(vec.x, vec.y);*/
		//gameID = gameid;
		//gameSeed = seed;
		//spawnLimitLastX = (int)spawnPos.x;
		
		

		

		//gameInfo = new GameInfo(gameid, seedid);
		poiString = new Mesh(false,2,2,
				new VertexAttribute(Usage.Position, 3, "a_pos"),
				new VertexAttribute(Usage.ColorPacked, 4, "a_color"));
		//Gdx.app.log("player", "made mesh");
		/*fHop = new Filter();
		fHop.categoryBits = 1;
		fHop.maskBits = 36;
		fDontHop = new Filter();
		fDontHop.maskBits = 64+63;
		fDontHop.categoryBits = 1;*/
		
	}
	public Player(Vector2 spawnPos, int gameid, int seedid)
	{
		super();
		//super(0, world, monsterIndex, spawnPos);
		//this.body.position
		//this.health = gameInfo.health;
		x = MathUtils.floor(spawnPos.x);
		y = MathUtils.floor(spawnPos.y);
		position = new Vector2(0,0);
		position.set(spawnPos.x, spawnPos.y);
		//gameID = gameid;
		//gameSeed = seed;
		//spawnLimitLastX = (int)spawnPos.x;
		
		

		//gameInfo = new GameInfo(gameid, seedid);
		poiString = new Mesh(false,2,2,
				new VertexAttribute(Usage.Position, 3, "a_pos"),
				new VertexAttribute(Usage.ColorPacked, 4, "a_color"));
		//Gdx.app.log("player", "made mesh");
		

	}
	
	public void createAxe(World world, PunkBodies monsterIndex, float angle){
		axeIsLeft = (angle > 90 && angle < 270);
		tmpV.set(position);
		tmpV.add(0,1f);
		axe.createBody(43, world, monsterIndex, tmpV);
		monsterIndex.fixtures[44].shape = axeIsLeft?monsterIndex.axeHeadL:monsterIndex.axeHead;
		axe.body.createFixture(monsterIndex.fixtures[44]);
		axe.body.setTransform(tmpV, MathUtils.degreesToRadians * angle);
		
		
		
		axeJointD.initialize(axe.body, body, tmpV);
		axeJointD.motorSpeed = axeIsLeft?10f:-10f;
		axeJointD.maxMotorTorque = 100f;
		axeJointD.enableMotor = true;
		
		axeJoint = (RevoluteJoint)world.createJoint(axeJointD);
		shootTimer = Punk.gTime+1300;
		//Gdx.app.log("player", "create axe, angle:"+angle);
	}
	
	public void destroyAxe(World world){
		isAxeing = false;
		axe.deactivate();
		axe.updateRemoval(world);
	}
	
	public void createGrapple(World world, PunkBodies monsterIndex, PunkMap map){
		boolean done = false;
		for (int i = 0; i < GRAPPLECOUNT && !done; i++){
			if (!grappleValid[i]){
				grappleValid[i] = true;
				grappleLocs[i].set(grappleLoc);
				done = true;
				lastCreatedGrapple = i;
			}
		}
		if (!done){//destroy/crteate
			if (lastCreatedGrapple == 0){
				grappleLocs[1].set(grappleLoc);
				done = true;
				lastCreatedGrapple = 1;
			} else{
				grappleLocs[0].set(grappleLoc);
				done = true;
				lastCreatedGrapple = 0;
			}
		}
		//grappleAnchorBody = world.createBody(monsterIndex.getBodyDef(7, tmpV.set(grappleLoc.x, grappleLoc.y)));
		isGrappling = true;
		isAimingGrapple = false;
		
		
	}
	public void destroyLGrapple(){
		grappleValid[0] = false;
		if (!grappleValid[1])isGrappling = false;
	}
	public void destroyRGrapple(){
		grappleValid[1] = false;
		if (!grappleValid[0])isGrappling = false;
	}
	
	public void createPlayer(World world, PunkBodies monsterIndex, PunkMap map, int classID, int raceID, int genderID){
		
		
		position.set(gameInfo.savedPosition.x+.5f, gameInfo.savedPosition.y);
		//PunkMap.currentPlane = gameInfo.savedPlane;
		//Gdx.app.log("player", "playercreate "+gameInfo.spawnPosition+gameInfo.savedPosition);
		//if (body == null){
			//this.createBody(19, world, monsterIndex, position);
			//Fixture footF = body.createFixture(monsterIndex.fixtures[45]);
			//foot = new PhysicsActor(45, 0);
			//footF.setUserData(foot);
			//Fixture footF2 = body.createFixture(monsterIndex.footFixt);
			//setHop(true);
			//body.setSleepingAllowed(false);
			//body.setBullet(true);
			this.actorID = 0;
			controllingMob = this;
			//brake = new PhysicsActor(8, world, monsterIndex, position);
			//createBBs(world, monsterIndex, map);
			head = new PhysicsActor(15, world, monsterIndex, tmpVP.set(position.x, position.y));
			neckDef = new DistanceJointDef();
			
			create(classID, raceID, genderID, 0, map, this, world, monsterIndex, gameInfo.savedPosition.x, gameInfo.savedPosition.y);
			
			Gdx.app.log(TAG, "START AFTER CREATE position "+position);
			
			body.setTransform (position,0);
			
			//ropeClimber = new PhysicsActor(32, 0);
			//ropeClimber.createBody(32, world, monsterIndex, position);
			//weightDummy = new PhysicsActor(36, 0);
			//weightDummy.createBody(36, world, monsterIndex, position);
			//monsterIndex.ropeJointDef.initialize(body, ropeClimber.body, tmpV);
			
			//hand = new PhysicsActor(46, 0);
			//hand.createBody(46, world, monsterIndex, position);
		//}
		//body.setTransform(position, 0);
		faction = PunkBodies.factions[1];
		//climbJoint = (RevoluteJoint)world.createJoint(monsterIndex.ropeJointDef);//TODO
		l_mi = monsterIndex;
		checkPosition(0);
		
		actorID = 0;
		name = Punk.gameName;
		headTarget = this;
		map.chunkActors.mobPool[0].monsterList.add(this);
		//activeFlight = new FlightInfo(main.monsterIndex.atlas, 1);
		//physicsNormal(world);
		Gdx.app.log(TAG, "START AFTER CHECK POS position "+position);
	}
	@Override
 	public boolean takeDamage(int damage, int dType){
		//Gdx.app.log("player", "player take damage");
		damageTimer = Punk.gTime;
		if (health-damage < 8 && stats.stats[12] < 8){
			//main.openMessageWindow(Punk.popupMessages[12]);
			stats.stats[12]++;
		}
		boolean damaged = super.takeDamage(damage, dType);
		if (damaged)l_mi.playPlayerHurtSound();
		return damaged;
 	}
	
	public void destroyClimbJoint(World world, long time){
		isHoldingRope = false; 
		body.setTransform(position, 0);
		body.setAngularVelocity(0);
		body.setFixedRotation(true);
	}
	
	private Vector2 tmpVPector = new Vector2();
	
	
	
	public int distanceToPlayerHead(int cx, int cy){//returns largest distance
		//if (true) return false;
		return Math.max(Math.abs(cx-head.x),Math.abs(cy-head.y));//TODO check performance/optimize
		//return (cx > head.x - Punk.BRADIUSX && cx < head.x+Punk.BRADIUSX && 
		//		cy > head.y-Punk.BRADIUSY && cy < head.y+Punk.BRADIUSY);
		
	}
	
	public int distanceToPlayer(int cx, int cy) {
		return Math.max(Math.abs(cx-x),Math.abs(cy-y));
		
	}

	
	public void run(int direction, long time, float deltaTime, PunkMap map, World world){
		
		
	}
	
	
	public void jump(int direction, PunkMap map, World world, PunkBodies monsterIndex, int gameMode, long time){
		
		Block lowerBlock = map.getBlock(x,y-1);
		//if (lowerBlock.blockID == 20 || lowerBlock)
		 if (isWallSliding && wallJumpsUsed < wallJumpsTot){
			wallJumpsUsed++;
			body.setLinearVelocity(tmpVP.set(isLeft?7:-7,10f*jumpMultiplier));
			isLeft = !isLeft;
			isWallSliding = false;
			monsterIndex.playJumpSound();
			//jumpAccumulator = 0;
			isJumping = true;
		}
		
		
		else if (doubleJumpsUsed < doubleJumpsTot){
			doubleJumpsUsed++;
			body.setLinearVelocity(tmpVP.set(body.getLinearVelocity().x,10f*jumpMultiplier));
			monsterIndex.playJumpSound();
			particles.doubleJump(position.x, position.y);
			//jumpAccumulator = 0;
			isJumping = true;
		}
		
		//Gdx.app.log(TAG, "jump "+bridgeTimer + " t "+time);
		
		/*else if (lowerBlock.blockType() == 2 || lowerBlock.blockType() == 4){//trees, snow etc
			body.setLinearVelocity(tmpVP.set(body.getLinearVelocity().x,10f));
			monsterIndex.playJumpSound();
			jumpAccumulator = 0;
			isJumping = true;
		}*/
		
		
		if (isSwimming){
			//body.applyLinearImpulse(0,180, position.x, position.y);
			isSwimmingUp = true;
		}
		
		isFallingThroughBridge = false;
		//isFalling = true;
		//body.setFixedRotation(false);
		
		
		jumpTimer = JUMPSIMULATETIME;
		//state = 2;
	}
	/*@Override
	public void startSwimming() {
		if (!isSwimming){
			body.setLinearDamping(swimDamping);
		}
		isSwimming = true;
		isGliding = false;
		gravityState = 3;
	}
	@Override
	public void stopSwimming(World world) {
		if (isSwimming) body.setLinearDamping(0f);
		isSwimming = false;
		gravityState = 0;
		//physicsNormal(world);
	}*/
	
	
	public int getDirection(){
		return moveDirection;
	}
	public int getState(){
		return state;
	}
	public void setLeft(boolean left){
		isLeft = left;
	}
	public boolean getLeft(){
		return isLeft;
	}
	/*public void startRunning(int direction, long time,float deltaTime, PunkMap map, World world){
		//body.getFixtureList().get(0).setFriction(0);
		//body.getFixtureList().get(1).setFriction(0);
		//footF.setFriction(0);
		//Gdx.app.log("player", "run running");
		float f = FRICTION_RUN;
		footF.setFriction(f);footF2.setFriction(f);
		//footF.refilter();
		List<Contact> lis = world.getContactList();
		//Gdx.app.log(TAG, "size "+lis.size());
		while (lis.size() > 0){
			Contact c = lis.remove(lis.size()-1);
			c.resetFriction();
			
		}
	
		isRunning = true;
		moveDirection = direction;
		
		//little push
		//Vector2 vel = body.getLinearVelocity();
		//if (Math.abs(vel.x) < 1f)
		//	body.setLinearVelocity(direction, vel.y+.1f);
		updateWallSliding(map);
	}
	public void stopRunning(long time, PunkMap map, World world){
		
		
		isRunning = false;
		if (bridgeTimer > time && !isOnSolidGround){
			tmpVP = body.getLinearVelocity();
			if (tmpVP.len() > 1)//if going fast, slow down
			{
				tmpVP.x =0;
				
			}
		}
	}*/
	public void resetHead(){
		if (headTarget != this)
		//head.body.setTransform(position, 0);
		headTarget = this;
		//headTimer = 
	}
	
	
	float headAccum = 0;
	public boolean draggedR;
	public boolean draggedL;
	public Door activeDoor;
	public void updateNeck(float deltaTime, PunkMap map){
		if (headTarget == null)
			headTarget = this;
		tmpVP.set(headTarget.position).add(0,2);
		//head.body.setTransform(tmpVP, 0);
		tmpVP.sub(head.body.getPosition());
		tmpVP.mul(13.5f);
		//if (tmpVP.len() > 30)
		//	tmpVP.mul(30/tmpVP.len());
		//else if (tmpVP.len2() < .5f) tmpVP.set(0,0);
		
		/*if (headTarget.distanceFromPlayerHead > 8){
			//if (headAccum < )
			//Gdx.app.log("player", "incrimenting"+headAccum);
			headAccum += deltaTime*2;
			headAccum = Math.min(8,headAccum);
		}
		else{}*/ //headAccum = 0f;
		
		//Gdx.app.log("player", "timer " +headAccum+" delta "+deltaTime);
		//if (headAccum > 0){
			//headAccum -= deltaTime;
			//float factor =headTarget.position.dst(head.position.tmp().add(0,1));
			//factor = Math.max(1f, factor);
			//factor *= .5f;
			//Gdx.app.log("player", "factor:"+factor);
			//tmpVP.mul(factor);
		//}
		
		if (state != 21) head.body.setTransform(position.tmp().add(0, originOffset.y), 0);
		else 
			head.body.setLinearVelocity(tmpVP);
		//head.position = head.body.getPosition();
		head.checkPosition(this, deltaTime, map, 0f);
		//head.position.set(position);
		//head.body.applyForce(tmpVP.mul(deltaTime*2560000), head.position);
		//head.x = MathUtils.floor(head.position.x);
		//head.y = MathUtils.floor(head.position.y);
	}

	public PhysicsActor getHeadTarget(){
		return headTarget;
	}

	public void setHeadTarget(PhysicsActor target){
		headTarget = target;
	}
	
	
	
	public void climbDown(PunkMap map, World world, long time){
		//System.out.println("climbing down" + map.getBlock(x,y-1).blockType());
		//if (isClimbing)
		//isClimbing = true;
		if (climbButtonDownValid && map.getBlock(x,y-1).blockType() <5) 
		{
			blocksFallen = 0;
			//brake.body.setTransform(tmpVP.set(boundBoxB.position.x,boundBoxB.position.y-3 ), 0);
			boundBoxB.body.setTransform(tmpVP.set(0.5f, y-2000000), 0);
			//tmpV.set(body.getLinearVelocity());
			//tmpV.y = Math.max(-4, tmpV.y);
			tmpV.set((state == 2)?moveDirection*5:0,-5);
			body.setLinearVelocity(tmpV);
		} else if ( bridgeTimer > time){
			
		}
			
	}
	
	public void climbUp(PunkMap map, World world, long time){
		//isClimbing = true;
		if (climbButtonUpValid && map.getBlock(x,y).blockType() ==2) 
		{
			
			blocksFallen = 0;
			//tmpV.set(body.getLinearVelocity());
			//tmpV.y = 4;
			tmpV.set(0,4);
			float off = position.x-MathUtils.floor(position.x);
			if (off < .4f) tmpV.rotate(-30);
			else if (off >.6f) tmpV.rotate(30);
			body.setLinearVelocity(tmpV);
		} else if ( bridgeTimer > time){
			
		}
			
	}
	

	
	public void die(Player player){
		
		//map.saveCurrentChunk();
		//Gdx.app.log("p", "saved");
		//TODO this fucks up
		tmpVP.set(gameInfo.spawnPosition.x, gameInfo.spawnPosition.y);
		//tmpVP.set(gameInfo.spawnPosition.x, gameInfo.spawnPosition.y);
		//Gdx.app.log("p", "tele");

		//map.teleportTo(tmpVP, this);
		//Gdx.app.log("p", "teled");

		body.setTransform(tmpVP, 0);
		head.body.setTransform(tmpVP, 0);
		body.setAngularVelocity(0);
		body.setLinearVelocity(0,0);
		body.setFixedRotation(true);
		//body.getFixtureList().get(0)
		//health = gameInfo.health;
		poisonDamage = 0;
		isOnFire = false;
		//isSwimming = false;
		
		//Gdx.app.log("player", "player die");
		//checkPosition(this, 0, map);
		//map.chunkActors.resetHostile();
		mi.playerHasDied = true;
		stats.stats[1]++;
		/*if (stats.stats[1] < 1){
			main.openMessageWindow(Punk.popupMessages[1]);
		} else main.openDieDialog();*/
	
	}
	
	
	
	public float lastHandDist, handDist;
	public void updateHand(PunkMap map, float deltaTime, World world, PunkBodies monsterIndex){
		hand.checkPosition(this, deltaTime, map, 0f);
		if (isThrowing){
			
			tmpV.set(position.x, position.y).add(handTarget).sub(hand.position).add(body.linVelWorld);
			lastHandDist = handDist;
			handDist = tmpV.len2();
			if (handDist < 1) tmpV.nor().mul(8);
			hand.body.setLinearVelocity(tmpV.mul(44));
			return;
		}
//		if (isPrePoi || (isPoiing && !isAimingPoi)){
//			hand.body.setTransform(poi.position, 0);
//			
//			return;
//		}
		
		tmpV.set(position).add(isLeft?-.6f:.6f, 0);
		if (isPlacingBlock){
			tmpV.add(Punk.touchLoc);
		}
		tmpV.sub(hand.position).add(0,.8f);
		tmpV.y*=2;
		
		//if (!isAimingPoi && !isAiming){
			//Gdx.app.log(TAG, "hand"+tmpV);
			hand.body.setLinearVelocity(tmpV.mul(12));
		//}
		//else hand.body.setLinearVelocity(0,0);
	}
	
	
	
	private void updateWallSliding(PunkMap map) {
		if (isWallSliding){//try to cancel
			wallSlide1 = false;
			Block sideBL = map.getBlock(x-1, y);
			Block sideBR = map.getBlock(x+1,y);
			
			if (isOnSolidGround || onCornerL || onCornerR || (sideBL.blockType() < 64 && sideBR.blockType() < 64)){
				
				if (isOnSolidGround || onCornerL || onCornerR)wallJumpsUsed = 0;
				
				isWallSliding = false;
				slideParticle.allowCompletion();
				slideParticle.update(1f);
				slideParticle = null;
			}
		} else {//try to init
			if (wallJumpsTot > 0)
			{//slow slide
				Block sideBL = map.getBlock(x-1, y);
				Block sideBR = map.getBlock(x+1,y);
				if (!isOnSolidGround && (sideBL.blockType() >= 64 || sideBR.blockType() >=64)) {
					isWallSliding = true;
					wallSlide1 = true;
					wallSlideL = (sideBL.blockType() >= 64);
					doInitialWallSlide();
					blocksFallen = 0;
					if (slideParticle == null)slideParticle = particles.wallSlide(x, y);
				}
				//else isWallSliding = false;
			} else {/*//normal slide
				Block sideBL = map.getBlock(x-1, y);
				Block sideBR = map.getBlock(x+1,y);
				if (!isOnSolidGround && (sideBL.blockType() >= 64 || sideBR.blockType() >=64)) {
					isWallSliding = true;
					wallSlide1 = true;
					wallSlideL = (sideBL.blockType() >= 64);
					//blocksFallen = 0;
					if (slideParticle == null)slideParticle = particles.wallSlide(x, y);
				}
			*/}
		}
			
		
		
			
		
		
	}

	private void doInitialWallSlide() {
		if (wallJumpsTot <=0) return;
		Vector2 vel = body.getLinearVelocity();
		 tmpV.set(vel);
		 //tmpV.x = Math.max(-.5f, tmpV.x);
		// tmpV.x = Math.min(.5f, tmpV.x);
		 if (wallSlideL){
			 tmpV.x = Math.min(-.5f, tmpV.x);
			// Gdx.app.log(TAG, "ONCORNERL");
		 }
		 else tmpV.x = Math.max(.5f, tmpV.x);
		 tmpV.y = Math.max((wallJumpsTot>0?-8.5f:-18.5f), tmpV.y);
		 body.setLinearVelocity(tmpV);
		
	}


	
	
	public void updateold(PunkMap map, World world, float deltaTime, long time, PunkBodies monsterIndex)
	{
		//if (!isTripping)
		globalTime += deltaTime;
		jumpTimer -= deltaTime;
		if (health <= 0){
			if (!isDisabled) {
				stun(map, world, monsterIndex, time);
				particles.blood(position.x, position.y+2);
				particles.blood(x, y+1);
				particles.blood(position.x+MathUtils.random(-1.2f,1.2f), position.y+1+MathUtils.random(-1f, 1f));
				particles.blood(position.x+MathUtils.random(-1.2f,1.2f), position.y+1+MathUtils.random(-1f, 1f));
				particles.blood(position.x+MathUtils.random(-1.2f,1.2f), position.y+1+MathUtils.random(-1f, 1f));
			}
			if (body.getLinearVelocity().y < 5)body.applyForce(tmpV.set(0,5), position);
		}
		//Gdx.app.log("player", "health#:"+health);
		//Log.d(Punk.TAG, "playerV(before)"+body.linVelWorld);
		checkPosition(this, deltaTime, map, 0f);
		if (x%1f < .5){//left
			if (!leftHalf){
				leftHalf = true;
				updateCorner(map);
			}
		}else {//r
			if (leftHalf){
				leftHalf = false;
				updateCorner(map);
			}
		}
		//position.y -= .5f;
		updateNeck(deltaTime, map);
		////Gdx.app.log("player", "fixed rotation:" +body.isFixedRotation());
		updateDamage(time, this, monsterIndex);
		
		enforceTerminalVelocity(monsterIndex);
		
		updateHand(map, deltaTime, world, monsterIndex);
		
		//rise if inside a block
		
		isClimbing = ((main.climbDownPressed && climbButtonDownValid) || (main.climbUpPressed && climbButtonUpValid));
		
		
		
		if (isGrappling){
			
			grappleAccumulator -= deltaTime;
			if (grappleAccumulator < 0){
				grappleAccumulator += GRAPPLEFREQ;
				int midX=0, midY=0;
				boolean invalid = false;
				if (grappleValid[0] && grappleValid[1]){
					midX = (grappleLocs[0].x +grappleLocs[1].x )/2;
					midY = (grappleLocs[0].y +grappleLocs[1].y )/2;
					
				} else if (grappleValid[0]){
					midX = grappleLocs[0].x;
					midY = grappleLocs[0].y;
				} else if (grappleValid[1]){
					midX = grappleLocs[1].x;
					midY = grappleLocs[1].y;
				} else invalid = true;
				if (!invalid){
					tmpV.set(midX, midY).sub(position);
					tmpV.mul(1200);
					body.applyForce(tmpV, position);
					//Gdx.app.log("player", "applied force for grapples"+grappleValid[0]+grappleValid[1]);
				}
			}
			
			
		}
		
		if (isSlinging)
		{
			slingScale = slingScale / 5;
			if (slingScale < 0.2f) slingScale = 0;
			if (animTimer < time) isSlinging = false;
		}else		
		if (isDigging) {
			if (digTimeout < time)
			{
				isDigging = false;
				isDoneDigging = true;
				
			}
		}
		if (isSwimming && isHoldingBreath){// || (playerPressed && touchTimer+200 < time))){
			if ((isSwimmingUp))
				tmpVP.set(0,28);
			else tmpVP.set(0,1);
			tmpVP.mul(body.getMass());
			//tmpV.mul(body.getFixtureList().get(0).getDensity());
			body.applyForce(tmpVP, position);
			
		}
		if (isDisabled && time > stunTimer) wakeUp(map, world, monsterIndex, time);
		
		
		
		if (isPoiing || isAimingPoi){
			{
				if (poi.body == null){
					Gdx.app.log("player", "null poi in player update !!!!!!!");
					isPostPoi = false;
					isPoiing = false;
					isAimingPoi =false;
				} else {

					poi.checkPosition(this, deltaTime, map, 0f);
					tmpV.set(position.x, position.y+1.5f);
					float poiDist = tmpV.dst(poi.position);
//					if (!isPostPoi && isPoiing && !isPrePoi && (poiDist > 5 ||(!isAimingPoi && getActiveID() ==0 && poiDist > 3f)) ){
//						//poi.deactivate();
//						isPostPoi = true;
//						shootTimer = time + 200;
//						//gets changed in collisions too
//						//poi.body.setType(BodyType.DynamicBody);
//						//poi.body.setActive(false);
//						//poi.body.setActive(true);
//					
//						tmpV.set(position);
//						tmpV.sub(poi.position);
//						tmpV.nor().mul(3);
//						poi.body.setLinearVelocity(tmpV);
//						//poi.body.
//					} 
					if (isPostPoi){
						if (time > shootTimer) poi.deactivate();
						else poi.body.setLinearVelocity(position.tmp().add(0,EYEHEIGHT).sub(poi.position).mul(activeTool.speed));
						//if (y-poi.y > 2) poi.deactivate();
					} else {
						
						
						if (isPrePoi){
							float newPoiDistance = poi.position.dst2(position.tmp().add(0,1));
							boolean goingAway = false;
							if (newPoiDistance > lastPoiDistance)goingAway = true;
							lastPoiDistance = newPoiDistance;
							/*boolean pLeft = poi.position.x < position.x;
							boolean pUp = poi.position.y > position.y+1 ;
							if (Math.abs(poi.position.x-position.x) < .3f && Math.abs(position.y+1 - poi.position.y) < .3f) goingAway = true;
							if (( pLeft ==  poiIsLeft) && (pUp == poiIsUp)) goingAway = true;
							//if  goingAway = true;*/
							//Gdx.app.log("player", "prePoi check"+pUp+"L:"+pLeft+"poiL:"+poiIsLeft+"poiUp:"+poiIsUp);
							if (goingAway )//&& poi.position.dst2(position.tmp().add(0,1)) < 1)
								isPrePoi = false;
							
							
						}
					}
							
					
					
					if (!isPrePoi && !isPostPoi && !isAimingPoi && isPoiing){
						float newPoiDistance = poi.position.dst2(Punk.digTargetBlock);
						boolean goingAway = false;
						if (newPoiDistance > lastPoiTargetDistance)goingAway = true;
						lastPoiTargetDistance = newPoiDistance;
						//Gdx.app.log("player", "distance : "+newPoiDistance + " last: "+lastPoiDistance);
						if (goingAway ){
							
							boolean hitBlock = true;
							int blockID = map.getBlock(Punk.digTargetBlock).blockID;
							////////if (!targettingMob) hitBlock = map.damageBlock(BlockDamageType.FLAIL, world, Punk.digTargetBlock, FLAILBLOCKDAMAGE, monsterIndex, time);
							if (hitBlock){//hit block
								inv.reduceDurability(activeInvSlot);
								isPostPoi = true;
								shootTimer = time + 300;
								PunkBodies.playBlockBreakSound(blockID);
								tmpV.set(position);
								tmpV.sub(poi.position);
								tmpV.nor().mul(POIKICKBACK);
								poi.body.setLinearVelocity(tmpV);
								//Gdx.app.log("player", "poi returning");
							}
							
							
							
						}
					}
				}
			}
		}
		/* if (isAxeing){
			 
			 //check for trees
			 tmpV.set(3,0).rotate(axe.body.getAngle()*MathUtils.radiansToDegrees);
			 tmpV.add(position.x, position.y+1);
			 int axeX = MathUtils.floor(tmpV.x);
			 int axeY = MathUtils.floor(tmpV.y);
			 if (axe.lastx != axeX || axe.lasty != axeY){
				 if (Math.abs(axe.body.getAngularVelocity()) > 9){
					if (map.damageBlock(BlockDamageType.AXE, world, tmpV, AXEBLOCKDAMAGE, monsterIndex, time)){
						axeJoint.setMotorSpeed(0);
						axe.body.applyAngularImpulse(axeIsLeft?80:-80);
						inv.reduceDurability(activeInvSlot);
					}
				 }
					
				 axe.lastx = axeX;
				 axe.lasty = axeY;
			 }
			 if (shootTimer < time) destroyAxe(world);
			 
		 }*/
	
		 
		 if (getActiveID() == 50){
				//map.getBlock(x,y+1).light = 15;
		 }
		 
		//if position changed
		if ( (x != this.lastx) || (y != this.lasty) )
		{
			//isLeft = (body.getLinearVelocity().x < 0.1f);
			
			
		
			
			if (getActiveID() == 50){
				//map.getBlock(x,y+1).light = 11;
				
			}
			
			
			updateBBs(map, world, time, deltaTime, monsterIndex);
			if (y > lasty)blocksFallen = 0;
			else if (y < lasty && !isWallSliding){
				blocksFallen++;
				
				
				
			}

			updateWallSliding(map);
				
			
			Block tmpB = map.getBlock(x,y-1);
			if (tmpB.blockType() != 2 && tmpB.blockType() != 4) isClimbing = false;
	
			
			
			lasty = y;
			lastx = x;
			outerSpawnLimitL = x - monsterIndex.SPAWNMAX;
			outerSpawnLimitR = x + monsterIndex.SPAWNMAX;
			//fall out the top
			//if (y < -50) body.setTransform(tmpVP.set(position.x, 256), 0);
			//body.applyForce(tmpVP.set(-1, 0), position);
			//boundBoxR.body.setLinearVelocity(tmpVP.set(0.01f,-0.01f));
			//boundBoxL.body.setLinearVelocity(tmpVP.set(-0.01f,-0.01f));
			float ang = body.getAngularVelocity();
			if (ang > 32) body.setAngularVelocity(32);
			else if (ang < -32) body.setAngularVelocity(-32);
			
		}//end: if block has changed
		if (isFlashing && time > flashTimeout) isFlashing = false;
		
		
	
	
	if (blockI.blockType() >= 64) body.setLinearVelocity(body.getLinearVelocity().x, 4);//, position.x, position.y);
	
	
	
	/*if (isWallSliding){
		
			if (!isRunning){
				doInitialWallSlide();
			
		} else {
			 Vector2 vel = body.getLinearVelocity();
			 tmpV.set(vel);
			 //tmpV.x = Math.max(-.5f, tmpV.x);
			// tmpV.x = Math.min(.5f, tmpV.x);
			 //if (wallSlideL){
			//	 tmpV.x = Math.min(-.5f, tmpV.x);
			//	 Gdx.app.log(TAG, "ONCORNERL");
			 //}
			 //else tmpV.x = Math.max(.5f, tmpV.x);
			 tmpV.y = Math.max((wallJumpsTot>0?-8.5f:-18.5f), tmpV.y);
			 body.setLinearVelocity(tmpV);
		}
	 }*/
	

	//if (!Punk.processing && Punk.deltaTime > .0000000005f)
	/*if (isGliding){} else {//not gliding
		//speed limit - now terminal velocity only
		Vector2 speedV = body.getLinearVelocity();
		float speed2 = speedV.len2();
		if (speedV.y < -speedLimit){
			//body.setLinearVelocity(speedV.x, -speedLimit);
			body.setLinearVelocity(speedV.tmp().nor().mul(speedLimit));
			
		}

	}*/
	
		
		//Log.d(Punk.TAG, "playerV(after)"+body.linVelWorld);
	
	}

	Vector2 tmpD = new Vector2(), tmpT = new Vector2();
	
	public void stun(PunkMap map, World world, PunkBodies mi, long time){
		//state = 8;
		isDisabled = true;
		isOnFire = false;
		stunTimer = time + 3000;
		body.setFixedRotation(false);
		body.applyAngularImpulse(isLeft?-120f:120f);
		//drop items
		/*for (int i = 0; i < inventory.INVENTORYSIZE; i++){
			if (inventory.getItemAmount(i) > 0){
				tmpV.set(position);
				tmpV.add(tmpV.tmp().set(1+MathUtils.random(3f),0).rotate(MathUtils.random(0,360)));
				Item item = map.itemPool.createItem(inventory.getItemID(i), inventory.getItemAmount(i), world, mi, map, tmpV);
				item.itemMeta = inventory.getItemMeta(i);
				//item.body.setLinearVelocity(tmpV.set(MathUtils.random(-6f, 6f), MathUtils.random(-6f, 6f)));
				item.animTimer = time + 1000;
			}
			inventory.deleteItem(i);
		}*/
		mi.playDieSound();
		//health = -50;
		//body.applyLinearImpulse(position, tmpV.set(0,5));
		////Gdx.app.log(TAG, "stunned");
	}

	public void wakeUp(PunkMap map, World world, PunkBodies mi, long time){
		////Gdx.app.log(TAG, "waking up");
		
		isDisabled = false;
		//body.setTransform(body.getPosition(), 0);
		//body.setFixedRotation(true);
		if (health <= 0) die(this);
		//else state = 7;
	}
	public boolean checkPosition(float deltaTime){
		position = body.getPosition();
		position.y -=.1f;
		x = MathUtils.floor(position.x);
		y = MathUtils.floor(position.y);
		stateTime += deltaTime;
		
		return true;
	}
	
	
	
	
	
	public boolean useAmmo(){//true if weapon is broke
		inv.useUpItem(activeAmmoSlot);
		//meta duration stuff for launchers
		//if (inventory.getItemID(activeInvSlot) >= 320 && inventory.getItemID(activeInvSlot) <= 402)
		inv.reduceDurability(activeInvSlot);	
		return (inv.getItemAmount(activeAmmoSlot)<=0);
	}
	
	
	

	public void resetAmmoSlot(){
		//start at 0, find first ammo
		int i = 0;
		boolean done = false;
		while (i < 5 && !done){
			if (true){//inventory.isValidAmmo(i)){
				activeAmmoSlot = i;
				done = true;
			}
			i++;
		}
		if (!done) activeAmmoSlot = activeInvSlot;
	}
	public int getActiveID(){
		return inv.getItemID(activeInvSlot);
	}
	public int getActiveMeta(){
		return inv.getItemMeta(activeInvSlot);
	}
	
	public int getMeleeDamage(){
		float initialDamage;
		//from the item
		switch (getActiveID()){
			case 399:
			case 400:
			case 304:
				
				initialDamage = 2;
				break;
			case 323://br sword
			case 401: initialDamage = 3;
				break;
			case 324://ir sword
			case 402: initialDamage = 4;
				break;
			case 325://mithril sword
						initialDamage = 5;
				break;
			default: initialDamage = 1;
		}
		//modify initialDamage according to?
		
		return MathUtils.floor(initialDamage);
	}
	
	
	public void doFallingDamage() {
		if (blocksFallen > 12)
			takeDamage(blocksFallen/4, DamageType.BLUNT);
		blocksFallen = 0;
		if (isOnSolidGround || onCornerR || onCornerL)doubleJumpsUsed = 0;
		//isWallSliding = false;
		//wallJumpsUsed = 0;
		//Gdx.app.log("player", "falling damage "); 
	}
	
	
	public void calculateItemBonuses() {
		// look in belt, do bonuses
		//doubleJumpsTot = 0;
		//wallJumpsTot = 0;
		//speedMultiplier = 1f;
		//jumpMultiplier = 1f;
		for (int i = 0; i < 6; i++){
			switch (inv.getItemID(i)){
			case 292:
				stopsTime = true;
				//speedMultiplier = 1.3f;
				break;
			case 290:
				//doubleJumpsTot++;
				break;
			case 291:
				//wallJumpsTot++;
				break;
			}
		}
		
	}
	
	public void calculatePerks(){
		resetSkills();
		//Gdx.app.log(TAG, "PERKS");
		//for (int c = 0; c < 4; c++)
			//for (int d = 0; d < 16; d++){
				//if (gameInfo.skills[c][d]) processSkill(c,d);
			//}
		
		calculateItemBonuses();
	}

	private void resetSkills() {
		doubleJumpsTot = 0;
		wallJumpsTot = 0;
		regenMultiplier = 0;
		additionalHealth = 0;
		ironFist = false;
		goldSmithing = 0.5f;
		copperSmithing = .5f;
		ironSmithing = .5f;
		diamondSmithing = 0.5f;
	}
	public float regenMultiplier, additionalHealth, goldSmithing, copperSmithing, ironSmithing, diamondSmithing;
	public boolean ironFist;
	
	private void processSkill(int c, int d) {
		if (c == 0)
			switch (d){
			case 5:
			case 0:
				Gdx.app.log(TAG, "double jump");
				doubleJumpsTot++;
				break;
			case 6:
			case 1:
				wallJumpsTot++;
				break;
			case 2:
				regenMultiplier += 1;
				break;
			case 3:
				additionalHealth++;
				break;
			case 4:
				ironFist = true;
				break;
			
			}
		else if (c == 1)//crafting
			switch (d){
			case 0:
				
				break;
			case 1:
				
				break;
			case 2:
				
				break;
			case 3:
				
				break;
			case 4:
				
				break;
			case 5:
				
				break;
			case 6:
				
				break;
			case 7:
				
				break;
			case 8:
				
				break;
			}
		else if (c == 2)//magic
			switch (d){
			case 0:
				
				break;
			case 1:
				
				break;
			case 2:
				
				break;
			case 3:
				
				break;
			case 4:
				
				break;
			case 5:
				
				break;
			case 6:
				
				break;
			case 7:
				
				break;
			case 8:
				
				break;
			}
		else if (c == 3)//combat
			switch (d){
			case 0:
				
				break;
			case 1:
				
				break;
			case 2:
				
				break;
			case 3:
				
				break;
			case 4:
				
				break;
			case 5:
				
				break;
			case 6:
				
				break;
			case 7:
				
				break;
			case 8:
				
				break;
			}
		
	}

	public void addXP(int state) {
		xp++;
		checkForLevelUp();
		
	}
	private int[] levelXP = {4, 8, 16, 32, 64, 128, 256, 512, 1024};
	private int levelCap = 8;
	public float lastAngle;
	public boolean isAimingBow;
	public GrenadeInfo activeGrenade;
	public ItemDef activeItem;
	public WeaponInfo activeTool;
	//public GenericMob controllingMob;

	private void checkForLevelUp() {
//		if (gameInfo.level <= levelCap){
//			if (gameInfo.xp > levelXP[gameInfo.level+1]){
//				gameInfo.level++;
//				main.showLevelupMessage(gameInfo.level);
//				//gameInfo.skillPoints++;
//				Gdx.app.log(TAG, "levelllllllllllllllllllllllllll");
//				calculatePerks();
//				saveToDisk(true);
//			}
//				
//		}
		
	}

	public void useArmor(float damage) {
		// TODO Auto-generated method stub
		
	}

	public boolean canGlide() {
		return !isOnSolidGround || climbButtonDownValid || blockB.blockType() < 6;
	}

	public Sprite getFlyArrowSprite(PunkBodies monsterIndex) {
		return monsterIndex.flyArrowS;
	}

	
	public void startGliding(World world) {
		// TODO Auto-generated method stub
		
	}

	

	

	
	
		
	
}

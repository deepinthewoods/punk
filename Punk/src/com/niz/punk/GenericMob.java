package com.niz.punk;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.LongArray;
import com.badlogic.gdx.utils.LongMap;

public class GenericMob extends PhysicsActor {
	public boolean leftHalf;
public Vector2 handROffset = new Vector2(), handLOffset = new Vector2(), originOffset = new Vector2(), direction = new Vector2();;
//private static final float[] MAX_STATETIME = {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
private static final float GROUND_TIME_LIMIT = .2f;
protected static final int STATS_COUNT = 4, WEAPON_PROFICIENCIES_COUNT = 14;
protected static final String[] STAT_NAMES = new String[]{"Strength", "Dexterity", "Constitution", "Faith", "Magick"};
public static final int S_STRENGTH = 0, S_DEXTERITY = 1, S_CONSTITUTION = 2, S_FAITH = 3, S_MAGICK=4;
private static String TAG = "genericMob";
private long moveTimer = new Long(0);
public long hash;
float lastpx, lastpy;
private static Vector2 tmpV2 = new Vector2();
//long attackTimer =0;
//private SpriteAnimation[] anim;
private ComponentMove move;
private ComponentStates statesComponent;
private Component update;
public LayeredAnimation anim, defaultAnim;
public Block blockBL = Punk.genericAirBlock, blockBR = Punk.genericAirBlock;
private boolean hasBBs, isRandomUpdate, hatesLight = false, hasWallJumps=true;;
public boolean isLongUpdate, stopped;
public GenericMob controllingMob = this;
private int updateInterval;
private PunkMap map;
private Player player;
private PunkBodies monsterIndex;
private World world;
public boolean hasHitGround = true;
public PhysicsActor boundBoxL= new PhysicsActor(), boundBoxR= new PhysicsActor(), boundBoxT= new PhysicsActor(), boundBoxC= new PhysicsActor();
public PhysicsActor boundBoxB = new PhysicsActor();
boolean leftBBValid, rightBBValid;
public boolean onCornerR;
public boolean onCornerL;
public int xSize;
public int ySize;
public boolean hasMoved = true;;
public boolean isOnSolidGround;
public int distanceTravelled;
public LongArray peers = new LongArray();
protected MobInfo info;
public byte light = 15;
private float coldTimer, heldTimer, slowTimer;
public boolean isFrozen, isHeld, isSlowed;
public int level;
long xp;
//public PhysicsActor targetActor;
public float mana, manaRegenRate, jumpStamina, maxStamina, jumpSpeed;
public Vector2 targetV = new Vector2();
public BlockLoc targetB = new BlockLoc();
public long nearestFriend, nearestEnemy;
public Faction faction;
public boolean isUp;
public float angle = 0;
public int activeInvSlot = 0;
//public float runButtonTimer, jumpButtonTimer;
//public int jumpData = 0;
public Block blockB, blockL, blockR, blockT, blockCL, blockCR;
boolean qHitGround;
boolean touchMale = true;

public float speedMultiplier = 1f, bounceMultiplier;;
public int lastState;
public int frictionState = 0, lastFrictionState = 0;
protected int gravityState = 0;
public int stateData;
private int lastGravityState=1;
private boolean climbButtonDownValid;
public boolean isClimbing = false;
public ButtonOverride[] buttons = new ButtonOverride[Punk.BUTTONOVERRIDECOUNT];
boolean[] pressed = new boolean[Punk.BUTTONOVERRIDECOUNT], lastPressed = new boolean[Punk.BUTTONOVERRIDECOUNT];
Vector2 touchLoc = new Vector2();
private float[] overrideTimer = new float[Punk.BUTTONOVERRIDECOUNT];
public int blocksFallen = 0;
private static Faction[] factions;
//private int factionID;
//private int classID;
static MobInfo[] classInfos;
static RaceDef[] raceInfos;
int frame, stateFrame;


byte cloth1= 26, cloth2=27, skin = 6, hair = 0;
int genderID;
public int[] baseStats = new int[STATS_COUNT];
public int[] stats = new int[STATS_COUNT];
public int attack, damage, defense, maxHP;
public int[] weaponProficiencies = new int[WEAPON_PROFICIENCIES_COUNT];


boolean  rotateArm = false, rotateBody = false;

int attacksRemaining = 0;
public Item activeItem;
public IntArray drawQ = new IntArray();
public PunkInventory inv = new PunkInventory();
public Item[] wearables = new Item[11];
public Array<Effect>  temporaryEffects = new Array<Effect>(), permanentEffects = new Array<Effect>();
public Array<TimedEffect> timedEffects = new Array<TimedEffect>();
public IntArray skills = new IntArray(), learnableSkills = new IntArray();

public static Skill[] skillInfos;
public static ButtonOverride[] jumpInfos;
public static Blessing[] blessingInfos;
public Array<Effect> passiveSkills = new Array<Effect>();
public Array<ItemDef> useableSkills = new Array<ItemDef>();;
public Array<Effect> jumpSkills = new Array<Effect>();
public IntArray blessings = new IntArray(true,0);

public int activeJump, selectedJump;
private static String defaultName = "default name";
String name = defaultName;
public boolean save, dead;
public static final float EYEHEIGHT = 1f;
public int[] belt = new int[Punk.BELTSLOTCOUNT];
public GenericMob(){
	
	//particles.add(new Array<ParticleEffect>());
}
public void save(){
	Punk.getSaveLoc(saveLoc);
	saveLoc.append(NPC_PATH);
	saveLoc.append(name );
	saveLoc.append(NPC_SUFFIX);
	FileHandle file = Gdx.files.external(saveLoc.toString());
	DataOutputStream os = new DataOutputStream(new BufferedOutputStream(file.write(false)));
	try {
		os.writeInt(Punk.versionNumber);
		os.writeInt(x);
		os.writeInt(y);
		os.writeInt(p);
		os.writeInt(level);
		os.writeLong(xp);
		os.writeInt(classID);
		os.writeInt(raceID);
		os.write(genderID);
		os.writeByte(skin);
		os.writeByte(hair);
		os.writeByte(cloth1);
		os.writeByte(cloth2);
		//stats
		for (int i = 0; i < STATS_COUNT; i++)
			os.write(baseStats[i]);
		for (int i = 0; i < WEAPON_PROFICIENCIES_COUNT; i++){
			os.writeInt(weaponProficiencies[i]);
		}
		os.writeInt(faction.id);
		os.writeInt(selectedJump);
		//skills
		int skillCount = skills.size;
		os.writeInt(skillCount);
		for (int i = 0, n = skills.size;i<n;i++){
			os.writeInt(skills.get(i));
		}
		
		int lskillCount = learnableSkills.size;
		os.writeInt(lskillCount);
		for (int i = 0, n = learnableSkills.size;i<n;i++){
			os.writeInt(learnableSkills.get(i));
		}
		
		
		skillCount = blessings.size;
		os.writeInt(skillCount);
		for (int i = 0, n = blessings.size;i<n;i++){
			os.writeInt(blessings.get(i));
		}
		//if (count != skillCount) throw new GdxRuntimeException("fail writing skills");
		
		os.close();
	} catch (Exception ex){
		Gdx.app.log(TAG, "error saving npc data");
	}
}

public void load(){
	save = true;
	Punk.getSaveLoc(saveLoc);
	saveLoc.append(NPC_PATH);
	saveLoc.append(name );
	saveLoc.append(NPC_SUFFIX);
	FileHandle file = Gdx.files.external(saveLoc.toString());
	DataInputStream is = new DataInputStream(new BufferedInputStream(file.read()));
	try {
		int version = is.readInt();
		x = is.readInt();
		y = is.readInt();
		p = is.readInt();
		level = is.readInt();
		xp = is.readLong();
		classID = is.readInt();
		skin = is.readByte();
		hair = is.readByte();
		cloth1 = is.readByte();
		cloth2 = is.readByte();
		for (int i = 0; i < STATS_COUNT; i++)
			baseStats[i] = is.readInt();
		for (int i = 0; i < WEAPON_PROFICIENCIES_COUNT; i++){
			weaponProficiencies[i] = is.readInt();
		}
		faction = factions[is.readInt()];
		selectedJump = is.readInt();
		int skillCount = is.readInt();
		skills.clear();
		for (int i = 0; i < skillCount; i++)
			skills.add(is.readInt());
		
		int lskillCount = is.readInt();
		learnableSkills.clear();
		for (int i = 0; i < lskillCount; i++)
			learnableSkills.add(is.readInt());
		
		int blessingCount = is.readInt();
		blessings.clear();
		for (int i = 0; i < blessingCount; i++)
			blessings.add(is.readInt());
		
		
	} catch (IOException e) {
		e.printStackTrace();
		throw new GdxRuntimeException("fail loading npc");
	}
}

public static void initInfos(MobInfo[] inf, RaceDef[] races, Skill[] skills, ButtonOverride[] jump, Blessing[] bless){
	classInfos = inf;
	factions = PunkBodies.factions;
	raceInfos = races;
	skillInfos = skills;
	jumpInfos = jump;
	blessingInfos = bless;
	
}

public void sortSkills(){
	passiveSkills.clear();
	useableSkills.clear();
	jumpSkills.clear();
	{
		for (int i = 0, n = skills.size;i<n;i++){
			
			int id = skills.get(i);
			Skill s = skillInfos[id];
			if (s != null)
			switch (s.type){
			case 0:
				passiveSkills.add((Effect) s.data);
				break;
			case 1:
				jumpSkills.add((Effect) s.data);
				
				((Effect)s.data).onStats(this);
				Gdx.app.log(TAG,  "JUMP SKILL"+activeJump);
				break;
			case 2:
				useableSkills.add((ItemDef) s.data);
				break;
			}
		}
	}
	
	if (jumpSkills.size >= selectedJump)selectedJump = jumpSkills.size - 1;
	//activeJump = selectedJump;
	//buttons[0] = jumpSkills.get(activeJump);
	
}


public void createBBs(World world, PunkBodies monsterIndex, PunkMap map){
	//boundBoxB = world.createBody(
	//		monsterIndex.getBodyDef(9, tmpV.set(position.x, position.y+10)));
	//Gdx.app.log("mob", "create BBS");
	//boundBoxB = map.chunkActors.add(9, 0, world, monsterIndex, position);
	
	boundBoxB.createBody(9, world, monsterIndex, position);
	boundBoxC.createBody(9, world, monsterIndex, position);
	boundBoxR.createBody(27, world, monsterIndex, position);
	boundBoxT.createBody(27, world, monsterIndex, position);
	boundBoxL.createBody(27, world, monsterIndex, position);
	
	//boundBoxB =
			//world.createBody(
			//monsterIndex.getBodyDef(9, tmpV.set(position.x, position.y+10)));
	//boundBoxB.createFixture(monsterIndex.fixtures[27]);
	//boundBoxB.setUserData(null);
	
	/*boundBoxL = world.createBody(
			monsterIndex.getBodyDef(27, tmpV.set(position.x, position.y+10)));
	boundBoxL.createFixture(monsterIndex.fixtures[27]);
	boundBoxL.setUserData(null);
	
	boundBoxC = world.createBody(
			monsterIndex.getBodyDef(9, tmpV.set(position.x, position.y+10)));
	boundBoxC.createFixture(monsterIndex.fixtures[27]);
	boundBoxC.setUserData(boundBoxB);
	
	boundBoxR = world.createBody(
			monsterIndex.getBodyDef(27, tmpV.set(position.x, position.y+10)));
	boundBoxR.createFixture(monsterIndex.fixtures[27]);
	boundBoxR.setUserData(null);
	
	boundBoxT = world.createBody(
			monsterIndex.getBodyDef(27, tmpV.set(position.x, position.y+10)));
	boundBoxT.createFixture(monsterIndex.fixtures[27]);
	boundBoxT.setUserData(null);*/
	
}

public void destroyBBs(World world){
	//world.destroyBody(boundBoxB);
	//Gdx.app.log("mob", "destroy bbs");
	if (hasBBs){
		//boundBoxB.deactivate();
		world.destroyBody(boundBoxB.body);

		world.destroyBody(boundBoxR.body);
		
		world.destroyBody(boundBoxL.body);
		world.destroyBody(boundBoxT.body);
		world.destroyBody(boundBoxC.body);
		//boundBoxR= null;
		//boundBoxL= null;
		//boundBoxT= null;
		//boundBoxC= null;
	}
}
public void draw(SpriteBatch batch){
	anim.draw(this,  batch);
	PunkBodies.dot.setSize(2f/16f, 2f/16f);
	PunkBodies.dot.setPosition(position.x+handROffset.x, position.y+handROffset.y);
	//PunkBodies.dot.draw(batch);
	PunkBodies.dot.setPosition(position.x+originOffset.x, position.y+originOffset.y);
	//PunkBodies.dot.draw(batch);
	PunkBodies.dot.setPosition(point1.x, point1.y);
	//PunkBodies.dot.setPosition(testPt.x, testPt.y);
	//PunkBodies.dot.draw(batch);
	PunkBodies.dot.setPosition(point2.x, point2.y);
	//PunkBodies.dot.draw(batch);
	
}

public void die(Player player){
	//Player.stats_kills++;
	
	if (health < -3)
	for (int i = 0; i < 5; i++){
		map.chunkActors.spawnGib(position.x, position.y+1, i, world, monsterIndex).body.setLinearVelocity(tmpV.set(MathUtils.random(-5.5f, 5.5f), MathUtils.random(-5.5f, 5.5f)));
		
	}
	else for (int i = 0; i < Math.abs(health*2); i++)
		player.particles.enemyBlood(x+MathUtils.random(-.5f,1.5f),position.y+MathUtils.random(3f));
	
	deactivate();
	dead = true;
	
	//drop inv items that are droppable
	for (int i = 0; i < inv.INVENTORYSIZE; i++){
		Item it = inv.getItem(i);
		ItemDef d = PunkBodies.getItemInfo(it.id, it.meta);
		if (it.id > 0 && d.droppable){
			map.createItem(it, x, y);
		}
	}
}

@Override
public void deactivate(){
	//remove from peers list
		for (int i = 0; i < peers.size; i++){
			if (GenericMob.mobs.containsKey(peers.get(i)))GenericMob.mobs.get(peers.get(i)).peers.removeValue(hash);
		}
		peers.clear();
		
	super.deactivate();
}



public void updateLeft1(PunkMap map, Vector2 vec, int x, int y){
	blockL = map.getBlock(x-1,y);
	switch (blockL.blockType())//left
	{
	//air
	case 16:case 15:
	case 0 : vec.set(16, -200000f);
		break;
	//water
	case 1 :  vec.set(x-2.5f, y+000000f);
	break;
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
	boundBoxL.body.setTransform(vec, 0);
	boundBoxL.checkPosition(player, 0, map, 0);
	//if (map.getBlock(x-1, y+1).blockType() > 2)
	//	vec.set(x-0.5f, y+1f);
}
	//if (map.getBlock(x+1, y+1).blockType() > 2) vec.set(x+1.5f, y+1f);
	


public void updateRight1(PunkMap map, Vector2 vec, int x, int y){
	blockR = map.getBlock(x+1,y);
	switch (blockR.blockType())
	{
	//air
	case 16:case 15:
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
	case 64 :  vec.set(x+1.5f, y+0f);
		break;	
	}
	//if (map.getBlock(x+1, y+1).blockType() > 2) vec.set(x+1.5f, y+1f);
	boundBoxR.body.setTransform(vec, 0);
	boundBoxR.checkPosition(player, 0, map, 0);
}
	

public void updateLeft(PunkMap map, Vector2 vec){
	if (info.sizeY == 0)updateLeft1(map, vec, x, y);
	else updateLeft( map,  vec, x-xSize, y);
}
public void updateRight(PunkMap map, Vector2 vec){
	if (info.sizeY == 0)updateRight1(map, vec, x, y);
	else updateRight( map,  vec, x, y);
}
public void updateBottom(PunkMap map, Vector2 vec, boolean climb){
	updateBottom( map,  vec,  climb, x, y);
}
public void updateBottom(PunkMap map, Vector2 vec, boolean climb, int x, int y){
	//bottom
	blockB = map.getBlock(x,y-1);
	switch (blockB.blockType())
	{
	case 16:case 15:
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
		case 69:
		case 68:
		case 65:
		case 66:
		case 67:
		case 64 :  vec.set(x+0.5f, y-1f);
		
		break;	
	}
}
public void updateLeft(PunkMap map){
	onCornerL = false;
	leftBBValid = false;
	blockBL = map.getBlock(x-1,y);
	switch (blockBL.blockType())//left
	{
	//air
	case 16:case 15:
	case 7:
	case 9:
	case 72:
	case 0 :  tmpV.set(-0.5f, -200000f);
		break;
	//water
	case 1 :  tmpV.set(-2.5f, +100000f);
	break;
	//leaves
	case 4:
	case 2 :  tmpV.set(-2.5f, +100000f);
	break;
	//solid
	//case 4:
	case 69:
	case 65:
	case 66:
	case 67:
	case 68:
	case 64 :  tmpV.set(x-0.5f, y+0f);
	leftBBValid = true;
	break;	
	}
	
	if (map.getBlock(x-1, y+1).blockType() > 16){
		tmpV.set(x-0.5f, y+1f);
		onCornerL = false;
	} 
	boundBoxL.body.setTransform(tmpV, 0);
	boundBoxL.checkPosition(player, 0, map, 0);
}


public void updateRight(PunkMap map){
	onCornerR = false;
	blockBR = map.getBlock(x+1,y);
	rightBBValid = false;
	switch (blockBR.blockType())
	{
	case 7:
	case 9:
	case 72:
	case 16:case 15:
	//air
	case 0 :   tmpV.set(+10000000.5f, -200000f);
		break;
	//water
	
	case 1 :  tmpV.set(+1.5f, -100000f);
		break;
	//leaves
	case 4:
	case 2 :  tmpV.set(+2.5f, -100000f);
		break;
	//solid
	//case 4:
	case 69:
	case 65:
	case 66:
	case 67:
	case 68:
	case 64 :  tmpV.set(x+1.5f, y+0f);
	rightBBValid = true;
		break;	
	}
	if (map.getBlock(x+1, y+1).blockType() > 16){
		tmpV.set(x+1.5f, y+1f);
		onCornerR = false;
	}
	boundBoxR.body.setTransform(tmpV, 0);
	boundBoxR.checkPosition(player, 0, map, 0);
}
public void updateBottom(PunkMap map){
	//bottom
	//climbButtonValid = false;
	//Gdx.app.log("player", "bottom, blockType():"+map.getBlock(x,y-1).blockType());
	climbButtonDownValid = false;
	boolean emptyfloor = false;
	switch (map.getBlock(x,y-1).blockType())
	{
	case 16:case 15:
	case 7: //Gdx.app.log(TAG, "player on rubber");
	case 9:
	case 72:
	case 0 : tmpV.set(+0.5f, -200000f);
	emptyfloor = true;
		break;
	case 1 :  tmpV.set(0.5f, -100002f);
		break;
	case 4:
	case 2 :  if (!isClimbing)tmpV.set(x+0.5f, y-1f);else tmpV.set(100000, 1000000);
	climbButtonDownValid = true;
		break;
	case 69:
	case 68:
	case 65:
	case 66:
	case 67:
	case 64 :  tmpV.set(x+0.5f, y-1f);
	
	break;	
	}
	/*if (emptyfloor){
		if (position.x % 1 < .5f){//left
			if (map.getBlock(x-1, y-1).blockType() < 64){}
			else tmpV.set(x-.5f, y-1f);
		} else {
			if (map.getBlock(x+1, y-1).blockType() < 64){}
			else tmpV.set(x+1.5f, y-1f);
		}
		
	} */
	boundBoxB.body.setTransform(tmpV, 0);
	
}
public void updateFloor(PunkMap map, World world, long time, boolean update)
{
	
	isOnSolidGround = false;
	climbButtonDownValid = false;
	//Gdx.app.log(TAG, "pbottom "+map.getBlock(x,y-1).blockType() + "   id "+map.getBlock(x,y-1).blockID);
	blockB = map.getBlock(x,y-1);
	switch (blockB.blockType())
	{
	case 16:case 15:
	//air
	//case 3:
	case 7:
	case 1 :
	case 0 : if (map.getBlock(lastx,lasty).blockType() >= 64)boundBoxB.body.setTransform(tmpV.set(lastx+0.5f, lasty-1f), 0);
	else boundBoxB.body.setTransform(tmpV.set(0.5f, -10000000f), 0);
		break;
	//fwater
	  //boundBoxB.body.setTransform(tmpVP.set(x+0.5f, y-2f), 0);
	//leaves
	case 4:
	case 2 :  if (isClimbing)  boundBoxB.body.setTransform(tmpV.set(0.5f, 1000000f), 0);
	else boundBoxB.body.setTransform(tmpV.set(x+0.5f, y-1f), 0);
	isOnSolidGround = true;
	climbButtonDownValid = true;
	break;
	 
	//solid
	case 69:
	case 68:
	case 65:
	case 66:
	case 67:
	case 64 :  boundBoxB.body.setTransform(tmpV.set(x+0.5f, y-1f), 0);
		isOnSolidGround = true;
	break;	
	}
	//body.applyForce(tmpVP.set(0,0.11f), position);
}
public void updateCorner(PunkMap map){
	//bottom
	//climbButtonValid = false;
	//Gdx.app.log("player", "bottom, blockType():"+map.getBlock(x,y-1).blockType());
	//climbButtonDownValid = false;
	int xOff = 1;
	boolean lef = position.x % 1f < .5f;
	Block b;
	if (lef){
		xOff = -1;
		b =blockCL;
	}else b = blockCR;
	if (lef){
		if (leftBBValid){
			boundBoxC.body.setTransform(tmpV.set(1, 100000000), 0);
			boundBoxC.checkPosition(player, 0, map, 0);
			return;
		}
	} else if (rightBBValid){
		boundBoxC.body.setTransform(tmpV.set(1, 100000000), 0);
		boundBoxC.checkPosition(player, 0, map, 0);
		return;
	}
	if (isClimbing){
		boundBoxC.body.setTransform(tmpV.set(1, 100000000), 0);
		boundBoxC.checkPosition(player, 0, map, 0);
		return;
	}
	switch (b.blockType())
	{
	case 16:case 15:
	case 7: //Gdx.app.log(TAG, "player on rubber");
	case 9:
	case 72:
	case 0 : tmpV.set(+0.5f, -200000f);
	
		break;
	case 1 :  tmpV.set(0.5f, -100002f);
		break;
	case 4:
	case 2 :  tmpV.set(0.5f+xOff, 10000000f);
	//climbButtonDownValid = true;
		break;
	case 69:
	case 68:
	case 65:
	case 66:
	case 67:
	case 64 :  tmpV.set(x+0.5f+xOff, y-1f);
				//isOnSolidGround = true;
	break;	
	}
	/*if (emptyfloor){
		if (position.x % 1 < .5f){//left
			if (map.getBlock(x-1, y-1).blockType() < 64){}
			else tmpV.set(x-.5f, y-1f);
		} else {
			if (map.getBlock(x+1, y-1).blockType() < 64){}
			else tmpV.set(x+1.5f, y-1f);
		}
		
	} */
	boundBoxC.body.setTransform(tmpV, 0);
	boundBoxC.checkPosition(player, 0, map, 0);
	
}
public void updateTop(PunkMap map){
	//bottom
	//climbButtonValid = false;
	//Gdx.app.log("player", "bottom, blockType():"+map.getBlock(x,y-1).blockType());
	//climbButtonDownValid = false;
	switch (map.getBlock(x,y+2).blockType())
	{
	case 16:case 15:
	case 7:
	case 9:
	case 72:
	case 0 : tmpV.set(+0.5f, -200000f);
		break;
	case 1 :  tmpV.set(0.5f, -100002f);
		break;
	case 4:
	case 2 :  tmpV.set(x+0.5f, +40000000f);
	//climbButtonDownValid = true;
		break;
	case 69:
	case 68:
	case 65:
	case 66:
	case 67:
	case 64 :  tmpV.set(x+0.5f, y+2f);
	
	break;	
	}
	boundBoxT.body.setTransform(tmpV, 0);
	boundBoxT.checkPosition(player, 0, map, 0);
}
public void updateTop(PunkMap map, Vector2 vec){
	updateTop( map, vec, x, y);
}
public void updateTop(PunkMap map, Vector2 vec, int x, int y){
	//top
	vec.set(+111111f, 1111111);
	blockT = map.getBlock(x,y+1+ySize);
	switch (blockT.blockType())
	{
	case 16:case 15:
	case 9:
	//air
	case 0 :  
	//water
	case 1 :  
	//leaves
	case 2 :
			if (map.getBlock(x-1,y+1+ySize).blockType() >=64) vec.set(x-0.5f, y+1+ySize);
			else if (map.getBlock(x+1, y+1+ySize).blockType() >= 64) vec.set(x+1.5f, y+1+ySize);
		break;
	
	case 3 : // vec.set(+111110.5f, y+2f);
	break;
	//solid
	case 65:case 66:case 67:case 68:case 69:
	case 64: vec.set(x+0.5f, y+1+ySize);
	break;	
	}
}

public void update(float deltaTime){
	//Gdx.app.log("mob", "update"+x+","+y);
	
	boolean newState = false, wasLeft = isLeft;
	if (state != lastState){
		newState = true;
		stateFrame = 0;
		Gdx.app.log("mob", "change astate"+state);
		if (anim.states[state].resetStateTime)
			stateTime = 0f;
		lastState = state;
		frictionState = frictionStates[state];
		
		gravityState = gravityStates[state];
		updateParticleState();
	}
	if (frictionState != lastFrictionState){
		setFrictionState();
		lastFrictionState = frictionState;
	}
	
	if (gravityState != lastGravityState){
		setGravityState();
		lastGravityState = gravityState;
	}
	
	//hasHitGround
	if (qHitGround){
		body.setTransform(position.x, (int)(position.y)+.5f, 0);
		body.setLinearVelocity(0,0);
		qHitGround = false;
		hasHitGround = true;
	}
	
	statesComponent.act(this, map, player, monsterIndex, world);
	/*if (hasHitGround){
		groundTimer += deltaTime;
		if (groundTimer > GROUND_TIME_LIMIT){
			hasHitGround = false;
		}
	}*/
	
	//stateTime += deltaTime;
	if (updateTimedEffects(deltaTime))calculateStats();
	
	
	updateDamage(Punk.gTime, player, monsterIndex);
	if (coldTimer > 0){
		coldTimer -= deltaTime;
		isFrozen = true;
	} else isFrozen = false;
	if (heldTimer > 0){
		heldTimer -= deltaTime;
		isHeld = true;
	} else isHeld = false;
	if (slowTimer > 0){
		slowTimer -= deltaTime;
		isSlowed = true;
	} else isSlowed = false;
	
	//if (player.controllingMob == this)Gdx.app.log(TAG, "pos"+position);
	checkPosition(player, deltaTime, map, .5f);
	//if (player.controllingMob == this)Gdx.app.log(TAG, "p  "+position);
	if (lastpx == position.x && lastpy == position.y) 
	 {
		
			if (!stopped && body.getLinearVelocity().len2() < 0.00000001f){
				//Gdx.app.log(TAG, "actually stopped");
				stopped = true;
			}
			
			
		
		
	}else{
		//Gdx.app.log(TAG, "not stopped");
		stopped = false;
		
		lastpx = position.x;
		lastpy = position.y;
	}//*/
	//if (body.linVelWorld.x != body.linVelWorld.x && body.linVelWorld.y != body.linVelWorld.y)stopped = true;
	//wwelse stopped = false;
	
	
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
	hasMoved = (x != lastx || y != lasty);
	if (Punk.gTime > animTimer){
		isLongUpdate = true;
		animTimer = Punk.gTime + updateInterval;
		calculateStats();
	}else isLongUpdate = false;
	
	if (hasMoved || isLongUpdate || updateBBsQueued){
		updateBBsQueued = false;
		updateInside(map, tmpV, Punk.gTime, deltaTime, world);
		//light = 0;
		//if (hatesLight && PunkMap.timeOfDay ==DayTime.DAY && light > 14){
			//isOnFire = true;
			//}
		
		if (hasBBs){
			updateBBs(map, world, Punk.gTime, deltaTime, monsterIndex);
			//if (state == 11)blocksFallen = 0;
			
		}
		update.act(this, map, player, monsterIndex, world);
		if (hasMoved){
			
			if (y < lasty && state != 11 ){
				if (blocksFallen < 0 )blocksFallen = 0;
				blocksFallen++;
				//Gdx.app.log(TAG, "fall");
				
			}
			else if (y > lasty){
				if (blocksFallen > 0) blocksFallen = 0;
				blocksFallen--;
			}
			
			lastx = x;
			lasty = y;
			if (hasWallJumps){
				if (state == 38 || state == 40){
					if (blockL.blockType() <64)
						state = 10;
				}
			}
		}//else {
	
		
		
		//if (info.burnsInDaylight && PunkMap.timeOfDay == DayTime.DAY && map.getBlock(x,y).dayLight > 5) isOnFire = true;
		
		
		
		
	}
	
	
	
	screenTouches();
	
	
	
	
	pressButtons(deltaTime);
	
	if (player.controllingMob != this)		
		move.act(this, map, player, monsterIndex, world);
	
	int newFrame = anim.states[state].getFrameIndex(stateTime);
	
	if (newFrame != frame || newState || touchMoved || isLeft != wasLeft){
		calculateStats();
		if (newFrame != frame) stateFrame++;
		//Gdx.app.log("gm", "stage"+state);
		if (newState) jitter = MathUtils.random(-.5f, .5f);
		
		
		drawItem = anim.states[state].drawItem;
		frame = newFrame;
		setHandPositions();
		anim.states[state].newFrame(this, map, world);
		rotateLayers.clear();
		
		anim.states[state].addRotateLayers(rotateLayers);
		
		touchMoved = false;
		
	}
	
	
	updateParticles(deltaTime);
	//Gdx.app.log("gm", "fr "+frame  + "   st"+state+"   "+originOffset);
	
}
public void updateBBs(PunkMap map, World world, long time, float deltaTime, PunkBodies monsterIndex){
		blockCL = map.getBlock(x-1, y-1);
		blockCR = map.getBlock(x+1, y-1);
		updateFloor(map, world, time, true);
		updateCorner(map);
		updateLeft(map);
		updateRight(map);
		
		
	
		climbButtonUpValid = false;
	//updateInside(map, tmpV, time, deltaTime, world);
	
	
	
	/*if (activeDoor == null){
		if (Chunk.checkPlayerDoorCollisions(this)) Punk.openDoorButtons(activeDoor);
	}else if (!Chunk.checkPlayerDoorCollisions(this)) {
		activeDoor = null;
		Punk.closeAction();
	}*/
	
	
	Block headB = map.getBlock(x,y+1);
	
	boolean headTorch = true;
	if (headB.blockID != 0 && headB.blockID != 51) headTorch = false;
	
	Block lastB = map.getBlock(lastheadx, lastheady+1);
	if (lastB.blockID == 51){
		lastB.set(0,lastB.meta);
		//Gdx.app.log(TAG, "erase");
		
	}
	map.addLightUpdate(lastheadx, lastheady+1);
	map.addLightUpdate(lastheadx, lastheady-1);
	lastheadx = x;
	lastheady = y;
	if (false && getActiveID() == 50){
		
		if (headTorch){
			headB.set(51, headB.meta);
			map.addLightUpdate(x,y+1);
			//Gdx.app.log(TAG, "light");
			
		}
		
	}
	

	
	Block tmpB = map.getBlock(x,y);
	
	updateTop(map);
	//boundBoxT.body.setTransform(tmpVP, 0);
	 
	boundBoxB.checkPosition(player, deltaTime, map, 0f);
	

	
}

private boolean updateTimedEffects(float deltaTime) {
	boolean changed = false;
	Iterator<TimedEffect> i = timedEffects.iterator();
	while (i.hasNext()){
		TimedEffect e = i.next();
		e.time -= deltaTime;
		if (e.time <0f){
			i.remove();
			TimedEffect.pool.free(e);
			e = null;
			changed = true;
		}
		
	}
	return changed;
}
public boolean touchMoved;
private void setHandPositions() {
	StateFrameInfo s = anim.states[state];
	
	s.setOffsets(this);
	
	
}
public IntArray rotateLayers = new IntArray();
private int lastheadx, lastheady;
private boolean climbButtonUpValid;


private int getActiveID() {
	return activeItem.id;
}

private void setGravityState() {
	switch (gravityState){
	case 0: body.setGravityScale(5);break;
	case 1: body.setGravityScale(5);break;
	case 2: body.setGravityScale(5);break;
	case 3: body.setGravityScale(.5f);break;
	case 4: body.setGravityScale(.2f);break;
	case 5: 
		body.setGravityScale(.1f);//paladin
		break;
	case 6: body.setGravityScale(2);break;
	case 7: body.setGravityScale(30);break;
	}
	
}


private static final float FRICTION_RUN = 0f;
private static final float FRICTION_NORMAL = 8000000f;


public int[] frictionStates = {0,1,1,1,1,1,0,0,1,1,1//10
		,1,0,0,0,0,0,0,0,0,0//20
		,0,1,0,0,0,0,0,0,0,2//30
		,0,0,0,0,0,0,0,0,0,0//40
		,0,0,0,0,0,0,0,0,0,0}, 
		 gravityStates = {0,0,0,0,0,0,0,0,0,1,1//10
				,2,0,0,0,0,0,0,0,0,0//20
				,0,2,0,0,0,0,0,0,0,5//30
				,0,0,0,0,0,7,0,0,0,0//40
				,0,0,0,0,1,1,1,0,0,0};;
public int placeRange;
int classID;
private int raceID;
private void setFrictionState() {
	switch (frictionState){
	case 0:
	{
		float f = FRICTION_NORMAL;
		bodyF.setFriction(f);
		body.setLinearDamping(0f);
		//footF.refilter();
		List<Contact> lis = world.getContactList();
		//Gdx.app.log(TAG, "size "+lis.size());
		while (lis.size() > 0){
			Contact c = lis.remove(lis.size()-1);
			c.resetFriction();
			
		}
	}
		
		break;
	case 1:
	{
		float f = FRICTION_RUN;
		bodyF.setFriction(f);
		body.setLinearDamping(0f);
		//footF.refilter();aa
		List<Contact> lis = world.getContactList();
		//Gdx.app.log(TAG, "size "+lis.size());
		while (lis.size() > 0){
			Contact c = lis.remove(lis.size()-1);
			c.resetFriction();
			
		}
	}
		
		break;
	case 2://paladin
	{
		float f = FRICTION_NORMAL;
		bodyF.setFriction(f);
		body.setLinearDamping(3.5f);
		//footF.refilter();
		List<Contact> lis = world.getContactList();
		//Gdx.app.log(TAG, "size "+lis.size());
		while (lis.size() > 0){
			Contact c = lis.remove(lis.size()-1);
			c.resetFriction();
			
		}
	}
		
		
		break;
	case 3:
		float f = FRICTION_NORMAL;
		bodyF.setFriction(f);
		body.setLinearDamping(2.5f);
		//footF.refilter();
		List<Contact> lis = world.getContactList();
		//Gdx.app.log(TAG, "size "+lis.size());
		while (lis.size() > 0){
			Contact c = lis.remove(lis.size()-1);
			c.resetFriction();
			
		}
		
		
	}
	
}

@Override
public void doCollision(PhysicsActor col, long time, Player player, PunkMap map, World world, PunkBodies monsterIndex, Contact contact){
	if (col.actorID == 0) {
		//col.takeDamage(statesComponent.meleeDamage, statesComponent.meleeDamageType);
		
	}
	else if (col.actorID == 9){
		switch (state){//if (state == 10 || state == 11 || state == 22 || state == 30 || state == ){
			//
		case 10:case 11:case 22:case 30:case 32:case 33:case 34:case 35:case 36:case 38:case 37:
		case 45:case 47:
		
			hasHitGround = true;
			jumpStamina = maxStamina;

		}
		
			//Gdx.app.log(TAG, "hitground");
		
		
			//body.setGravityScale(GRAVITY_GROUND);
		
	}
	else if (col.actorID != 55)col.doCollision(this, time, player, map, world, monsterIndex, contact);
	else if (col.actorID == 55){
		GenericMob mob = (GenericMob) col;
		mob.info.move.collide(mob, map, player, monsterIndex, world);
		info.move.collide(this, map, player, monsterIndex, world);
		if (faction.opinion.get(mob.faction.id) < 0){//bump enemy
			bump(mob);
			
		}
		
	}
	else if (col.actorID == 27){
		Gdx.app.log(TAG, "walljump");
		if (hasWallJumps){
			if (col.x < x){
				if (isLeft)state = 38;
			} else if (!isLeft) state = 40;
			
		}
	}
	//TODO catch on fire from conbtact
}

public void createFromSave(String name, Player player, World world, PunkBodies monsterIndex, PunkMap map){
	this.name = name;
	load();
	create(player, world, monsterIndex, map);
}

public void create(int classID, int raceID, int genderID, int faction, PunkMap map,
		Player player, World world, PunkBodies monsterIndex, int x, int y
		){
	this.faction = factions[faction];
	this.classID = classID;
	this.raceID = raceID;
	this.info = classInfos[classID];
	this.genderID = genderID;
	//basestats
	
	for (int i = 0; i < STATS_COUNT; i++){
		baseStats[i] = info.minStats[i];
	}
	for (int i = 0; i < WEAPON_PROFICIENCIES_COUNT; i++){
		weaponProficiencies[i] = info.weaponProficiencies[i];
	}
	
	this.x = x;
	this.y = y;
	autoSelectJump();
	setSkills(info.defaultSkills);
	health = info.hp;
	create(player, world, monsterIndex, map);
	
}
private void autoSelectJump() {
	selectedJump = 0;
	//activeJump = 0;
	
}

public void create(Player player, World world, PunkBodies monsterIndex, PunkMap map){
	this.map = map;
	this.world = world;
	this.monsterIndex = monsterIndex;
	this.player = player; 
	if (info.hasBBs) createBBs(world, monsterIndex, map);
	create();
	
}
public void create(){
	
	//this.factionID = faction;
	this.statesComponent = (ComponentStates) info.states;
	this.move = info.move;
	this.update = info.update;
	this.anim = (LayeredAnimation) PunkBodies.anims[info.animID];
	this.update = info.update;
	dead = false;
	this.hasBBs = info.hasBBs;
	this.updateInterval = info.updateInterval;
	ai = info.ai;
	this.isRandomUpdate = info.isRandomUpdate;
	
	
	for (int i = 0, n = skills.size;i<n;i++){
		//Gdx.app.log(TAG, "ONcREATE"+skills.get(i));
	}
	level = info.level;
	distanceTravelled = 0;
	
	createBody(info.bodyID, world, monsterIndex, tmpV.set(x+.5f,y+.5f));
	
	actorID = 55;
	checkPosition(player, 1f, map, 0f);
	//health = raceInfos[raceID].;
	isImmuneToFire = info.isImmuneToFire;
	Gdx.app.log("genericMob", "created"+x+","+y+position);
	//if (this.faction == null) throw new GdxRuntimeException("null faction on create: "+((ComponentAnimation)(info.anim)).name);
	//targetFaction = -1;
	targetV.set(position);
	mobs.put(hash, this);
	isHostile = false;
	frictionState = 0;
	lastFrictionState = 0;
	selectedJump = 0;
	save = false;
	hasHitGround = false;
	classInfos[classID].onSpawn(this, map);
	//raceInfos[raceID].
	addAllBlessings();
	sortSkills();
	calculateStats();
	updateBBs(map, world, 0, 0, mi);
	Gdx.app.log(TAG, "create body ---------------------- "+x+","+y);
	checkPosition(player, 0, map, 0);
}





void addAllBlessings() {
	for (int i = 0; i < 6; i++){
		PunkBodies.deities[i].grantBlessings(this);
	}
	
}
public void calculateStats(){
	touchMale = true;
	speedMultiplier = 1f;
	placeRange = 18;
	manaRegenRate = 1f;
	bounceMultiplier = 0f;
	//activeJump = selectedJump;
	//draw layer queue, check wearables and change
	RaceDef raced = raceInfos[raceID];
	for (int i = 0; i < STATS_COUNT; i++){
		stats[i] = baseStats[i]+raced.statBonusses[i];
	}
	attack = stats[S_STRENGTH];
	damage = 0;
	defense = stats[S_DEXTERITY];
	maxHP = stats[S_CONSTITUTION]*classInfos[classID].hp;
	maxStamina = info.baseStamina;
	jumpSpeed = info.jumpSpeed;
	tints[0].clear();
	tints[1].clear();
	tints[2].clear();
	tints[3].clear();
	tints[4].clear();
	tints[5].clear();
	copy = 0;
	particleBits.clear();
	
	
	hasWallJumps = true;
	//Effects from various places
	jumpSkills.get(selectedJump).onStats(this);
	{
		Iterator<TimedEffect> i = timedEffects.iterator();
		while (i.hasNext()){
			Effect e = i.next().e;
			e.onStats(this);
			e.draw(this, drawQ);
		}
	}
	{
		Iterator<Effect> i = temporaryEffects.iterator();
		while (i.hasNext()){
			Effect e = i.next();
			e.onStats(this);
			e.draw(this, drawQ);
		}
	}
	{
		Iterator<Effect> i = permanentEffects.iterator();
		while (i.hasNext()){
			Effect e = i.next();
			e.onStats(this);
			e.draw(this, drawQ);
		}
	}
	statesComponent.onStats(this, map, player, monsterIndex, world);
	
	for (int i = 0; i < 6; i++){
		if (tints[i].size == 0)tint[i] = 0;
		else 
			tint[i] = tints[i].get((Punk.incrementer)%(tints[i].size));
	}
	anim.queueDrawLayers(this, drawQ, wearables);
	//Gdx.app.log(TAG, "activeJump"+activeJump);
	
	buttons[1] = jumpInfos[activeJump];
	buttons[4] = jumpInfos[activeJump];
}

public BitSet particleBits = new BitSet(64);
BitSet lastParticleBits = new BitSet(64);
BitSet tmpBits = new BitSet(64);
Array<PositionedParticle> particles = new Array<PositionedParticle>(); 
private void updateParticles(float delta){
	
		for (int i = 0,n = particles.size; i < n; i++){
			PositionedParticle p = particles.get(i);
			p.position(this);
			p.e.update(delta);
		}
	
}

private void updateParticleState(){
	if (particleBits.equals(lastParticleBits)) return;
	tmpBits.clear();
	tmpBits.or(particleBits);
	tmpBits.xor(lastParticleBits);
	int prevBit = 0;
	int i = tmpBits.nextSetBit(prevBit);
	
	while (i != -1){
		
		boolean p = false,l = false;
		
		p = (particleBits.get(i));
		l = (lastParticleBits.get(i));
		//Gdx.app.log("tag", "p"+i);
		if (p){//add
			Player.particles.addMob(particles,i);
			particleBits.set(i);
		} else {//remove,add to particle pool, set completeable true
			Player.particles.removeMobFX(particles, i);
			particleBits.set(i,false);
			//Gdx.app.log("tag", "REMOVEPARTICLEPARTEUIRUEIDSDFHL");
		}
		prevBit = i;
		i = tmpBits.nextSetBit(prevBit+1);
	}
	lastParticleBits.clear();
	lastParticleBits.or(particleBits);
}

private void setSkills(int[] s) {
	skills.clear();
	for (int i = 0; i < s.length; i++){
		skills.add(s[i]);
		//Gdx.app.log("tag", "skilllllllllll "+s[i]);
	}
}

@Override
public boolean takeDamage(int damage, int dType){
	if (dType == DamageType.FIRE && isImmuneToFire){
		//Gdx.app.log("mob", "not taking fire damage");
		isOnFire = false;
		return false;
	}
	boolean successful = super.takeDamage(damage, dType);
	
	//if (successful && isVisible && info.hurtSnd != null) info.hurtSnd.play(PunkBodies.volume);
	return successful;
}

public int maxHealth(){
	return info.maxHealth;
}


public static EnemyLook enemyCallback = new EnemyLook();
public static FriendlyLook friendlyCallback = new FriendlyLook();
public static PeersLook factionCallback = new PeersLook();
public static LongMap<GenericMob> mobs = new LongMap<GenericMob>();

public IntArray minions = new IntArray();
public int leader;

public boolean acquireTarget(World world) {
	friendlyCallback.check(map, world, this);
	enemyCallback.check(map, world, this);
	return enemyCallback.success;
		
}

public float getMaxStateTime() {
	//Gdx.app.log("mob", ""+ (anim.states[state].getMaxTime()*speedMultiplier) );
	return anim.states[state].getMaxTime()*speedMultiplier;
}


public void updateOverrides(float delta){
	for (int i = 0; i < Punk.BUTTONOVERRIDECOUNT; i++){
		if (buttons[i] != null){
			overrideTimer[i] -= delta;
			if (overrideTimer[i] < 0f){
				buttons[i] = null;
			}
		}
	}
}
public void pressButtons(float delta){
	if (activeTouchAction != null && activeTouchAction.selectable){
		activeTouchAction.beltSelected(this);
	}
	runPressed = false;
	if (pressed[0] || pressed[3]) runPressed = true;
	for (int i = 0; i < Punk.BUTTONOVERRIDECOUNT; i++){
		//Gdx.app.log(TAG, "press"+i);
		if (buttons[i] == null) {
			if (pressed[i]){
				if (info.defaultButtons[i] != null)info.defaultButtons[i].pressed(this, map, world, monsterIndex, climbButtonDownValid, delta);
				lastPressed[i] = true;
			}
			else if (lastPressed[i]){
				if (info.defaultButtons[i] != null)info.defaultButtons[i].unPress(map, this, monsterIndex, (i>2?pressed[i-3]:pressed[i+3]));
				lastPressed[i] = false;
			}
		}else {
			if (pressed[i]){
				buttons[i].pressed(this, map, world, monsterIndex, climbButtonDownValid, delta);
				lastPressed[i] = true;
			}
			else if (lastPressed[i]){
				buttons[i].unPress(map, this, monsterIndex, (i>2?pressed[i-3]:pressed[i+3]));
				lastPressed[i] = false;
			}
		}
		
	}

	
}



private TouchAction activeTouchAction;
public boolean screenTouched, screenWasTouched;
public float jitter;
public boolean drawItem;
public IntArray[] tints = {new IntArray(), new IntArray(), new IntArray(), new IntArray(), new IntArray(), new IntArray() };
public int[] tint = new int[6];
public int copy = 0;

public void touchBelt(int slot){
	TouchAction but;
	if (belt[slot] < 0){//skill
		but = null;
	} else {//item
		Item i = inv.getItem(belt[slot]);
		but = PunkBodies.getItemData(i.id, i.meta);;
	}
	
	buttons[7] = but;
	//pressed[7] = true;
	//Gdx.app.log(TAG, "select"+slot);
	if (but == null)return;
	
	if (but.selectable){
		if (activeTouchAction != null){
			activeTouchAction.onBeltUnselect(this);
		}
		this.activeInvSlot = belt[slot];
		activeTouchAction = but;
		//Gdx.app.log(TAG, "selectafter"+slot+(activeTouchAction == null));

	}
}



public void putInBelt(int index) {///inv index, or -ve for powers
	//check if it's there already
	//boolean there = false;
	int sp = -1;
	for (int i = 0; i < Punk.BELTSLOTCOUNT; i++){
		if (belt[i] == index) sp = i;
	}
	if (sp != -1){
		//int val = belt[sp];
		for (int i = sp; i > 0 ; i--){
			belt[i] = belt[i-1];
		}
		belt[0] = index;
		return;
	}
	for (int i = Punk.BELTSLOTCOUNT-1; i > 0 ; i--){
		belt[i] = belt[i-1];
	}
	belt[0] = index;
}
public void screenTouches() {
	if (screenTouched){
		screenWasTouched = true;
		if (activeTouchAction != null){
			activeTouchAction.touchDown(this);
		}
	} else if (screenWasTouched){
		screenWasTouched = false;
		if (activeTouchAction != null){
			activeTouchAction.touchUp(this);
		}
	}
	
	
	
}

public void onScreenUnTouched(){
	screenWasTouched = false;
	if (activeTouchAction != null){
		
	}
}



public void clipAnimTimer() {
	if (stateTime > getMaxStateTime()) stateTime = getMaxStateTime();	
}


public void drawParticles(SpriteBatch batch){
	
		for (int j = 0,n=particles.size;j<n;j++){
			particles.get(j).e.draw(batch);
			//Gdx.app.log(TAG, "particels");
		}
	
}
public void drawItem(SpriteBatch batch) {
	anim.drawItem(this, batch);
	CorneredSprite s = PunkBodies.dot;
	s.setPosition(targetV.x, targetV.y);
	s.draw(batch);
	Vector2 v = touchLoc.tmp().mul(5).add(position).add(0,Player.EYEHEIGHT);
	//PunkBodies.dot.setPosition(v.x, v.y);
	//PunkBodies.dot.draw(batch);
	
}


public boolean runPressed;

static String NPC_PATH = "/npcs/", NPC_SUFFIX = ".npc";
static StringBuilder saveLoc = new StringBuilder();

public int getMaxStat(int index) {
	return raceInfos[raceID].maxStats[index];
	
}

public boolean hasLevelUp() {
	
	return (xp > (int)(Math.pow(level, 1.5) * 20));
}

public void gainXPFromKilling(GenericMob mob){
	int amount = mob.maxHP
			+ mob.attack + mob.defense - level*2;
	xp += amount;
}
private static final int ATTACK_ROLL = 20;

public void attackMelee(GenericMob mob){
	int id = inv.getItemID(activeInvSlot), meta = inv.getItemMeta(activeInvSlot);
	WeaponInfo inf = (WeaponInfo) PunkBodies.getItemData(id, meta);
	float d = mob.health;
	int attackRoll = MathUtils.random(ATTACK_ROLL);
	attackRoll += attack;
	//crit hits
	
	boolean hit = false;
	if (attackRoll > mob.defense)hit = true;
	
	if (hit && !mob.dead){
		
		mob.applyWeaponEffects(inf, this);
		if (mob.dead){
			gainXPFromKilling(mob);
		}
		d -= mob.health;
		Gdx.app.log(TAG, "hit"+d);
		increment(inf.prof, d);
		attacksRemaining--;
	}
}

private void applyWeaponEffects(WeaponInfo inf, GenericMob src) {
	//damage first
	int dam = inf.getDamage();
	takeDamage(dam, inf.damageType);
	Gdx.app.log(TAG, "damage"+dam);
	for (int i = 0; i < WeaponInfo.EFFECTS_COUNT; i++){
		Effect e = inf.effects[i];
		if (e == null) continue;
		if (src == null)
			e.applyTo(this, this);
		else
			e.applyTo(this, src);
	}
	
}



static Vector2 point0 = new Vector2(), point1 = new Vector2(), point2 = new Vector2(), testPt = new Vector2();
public void attackRaycast() {
	//raycast for breaking blocks
	
	if (attacksRemaining == 0) return;
	int id = inv.getItemID(activeInvSlot), meta = inv.getItemMeta(activeInvSlot);
	WeaponInfo inf = (WeaponInfo) PunkBodies.getItemData(id, meta);
	point0.set(position).add(0,originOffset.y);
	point1.set(position).add(handROffset);
	testPt.set(position).add(handROffset);
	float ang = anim.handRAngles[frame];
	if (!isLeft) ang = 180 - ang;
	point2.set(-inf.range, 0).rotate(
			(ang)
			
			).add(point1);
	
	world.rayCast(meleeCallback, point0
			, point1);
	
	world.rayCast(meleeCallback, point1
			, point2);
}

private RayCastCallback meleeCallback = new RayCastCallback(){

	@Override
	public float reportRayFixture(Fixture fixture, Vector2 point,
			Vector2 normal, float fraction) {
		Object data = fixture.getBody().getUserData();
		if (data instanceof GenericMob){
			boolean friend = false;
			GenericMob gCol = (GenericMob) data;
			if (faction.opinion.get(gCol.faction.id) > 0) friend = true;
			if (!friend){
				attackMelee(gCol);
				return 0;
			} else {//"go hostile?"
				
			}
			
			return 1;
		}
		return 1;
	}

	
	
};
AIProcessor ai;// = new AIProcessor();
public int aiActiveNeuron;
public boolean[] aiValid = new boolean[Punk.BELTSLOTCOUNT];
public void ai(){
	if (player.controllingMob == this) return;
	this.acquireTarget(world);
	ai.act(this, map);
}
private void bump(GenericMob mob) {
	ai.bump(this, mob);	
}
public void transmuteTo(int transmuteID){
	//controllingMob = ;
}
public void unTansmute(){
	controllingMob = this;
}

public void faceAngle(boolean b) {
	angle %= 360;
	if (angle < 90 || angle > 270) isLeft = b;
	else isLeft = !b;
	
}

public void addTimedEffect(Effect e) {
	//look for same id
	for (int i = 0,n=timedEffects.size;i<n;i++){
		TimedEffect te = timedEffects.get(i);
		if (te.e.id == e.id){
			te.time += e.time;
			if (te.time > e.maxTime)te.time = e.maxTime;
			return;
		}
	}
	timedEffects.add(TimedEffect.get(e));
	
}

public void applyBlessingsOnStats() {
	for (int i = 0,n=blessings.size;i<n;i++){
		Effect e = blessingInfos[blessings.get(i)];
		e.onStats(this);
		//e.draw(this, drawQ);
		//Gdx.app.log("stats", "bless");
	}
}
public void prayForBlessings(){
	for (int i = 0,n=blessings.size;i<n;i++){
		blessingInfos[blessings.get(i)].applyTo(this, this);
		//Gdx.app.log(TAG, "bless");
	}
}
public void doFallingDamage(GenericMob mob){
	if (blocksFallen > 6) super.doFallingDamage(mob);
}
public void addPermanentEffect(Effect e) {
	permanentEffects.add(e);
	
}
public void invertStateTime() {
	stateTime = Math.max(getMaxStateTime()-stateTime,0);
	
}
int polymorphState = 0, lastPolymorphState = 0;
public void setPolymorph(int type){
	if (polymorphState != type) unPolymorph(false);
	polymorphState = type;
	switch (type){
	case 1:
		//tmpV2.set(body.getLinearVelocity());
		Gdx.app.log(TAG, "get"+tmpV2);
		world.destroyBody(body);
		createBody(11, world, monsterIndex, tmpV.set(x+.5f,y+.5f));
		actorID = 55;
		body.setLinearVelocity(tmpV2);
		//Gdx.app.log(TAG, "set"+tmpV2);
		body.setFixedRotation(false);
		//body.setAngularVelocity(isLeft?15f:-15f);
		
		ySize = 0;
		break;
	}
	
	lastGravityState = -1;
	lastFrictionState = -1;
}
public void unPolymorph(boolean arrestUpwardMotion){
	switch (polymorphState){
	
	}
	info = classInfos[classID];
	tmpV2.set(body.getLinearVelocity());
	world.destroyBody(body);
	create();
	if (arrestUpwardMotion) tmpV2.y = Math.min(tmpV2.y, 0);
	body.setLinearVelocity(tmpV2.x, tmpV2.y);
	//actorID = 55;
	//createBody(info.bodyID, world, monsterIndex, tmpV.set(x+.5f,y+.5f));
	polymorphState = 0;
	lastGravityState = -1;
	lastFrictionState = -1;
}


public void increment(int index, float amount){
	weaponProficiencies[index] += amount;
}
public int distanceTo(GenericMob mob) {
	return Math.max(Math.abs(mob.x-x),Math.abs(mob.y-y));
}




}

package com.niz.punk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntMap;
import com.niz.punk.PunkMap.BlockDamageType;
import com.niz.punk.buttons.BOJBarbarian;
import com.niz.punk.buttons.BOJPaladin;
import com.niz.punk.buttons.BOJRogue;
import com.niz.punk.buttons.BOJWizard;
import com.niz.punk.deities.GVoid;
import com.niz.punk.deities.Graviticus;
import com.niz.punk.deities.Ignis;
import com.niz.punk.deities.Knossos;
import com.niz.punk.deities.Lumiera;
import com.niz.punk.deities.Vakava;
import com.niz.punk.mobs.BarbarianInfo;
import com.niz.punk.mobs.BerserkerInfo;
import com.niz.punk.mobs.BuilderInfo;
import com.niz.punk.mobs.ClericInfo;
import com.niz.punk.mobs.DruidInfo;
import com.niz.punk.mobs.JugglerInfo;
import com.niz.punk.mobs.NecromancerInfo;
import com.niz.punk.mobs.NinjaInfo;
import com.niz.punk.mobs.PaladinInfo;
import com.niz.punk.mobs.RangerInfo;
import com.niz.punk.mobs.RogueInfo;
import com.niz.punk.mobs.SmithInfo;
import com.niz.punk.mobs.WildMageInfo;
import com.niz.punk.mobs.WizardInfo;

public class PunkBodies {
	
	public static float volume = 1f;
	public float defaultVolume = 1f;
	public float musicVolume = 1f;
	public Music currentMusicLoop, queuedMusicLoop;
	public boolean playerHasDied = false;
	//public Music playingMusic, queuedMusic; //TODO
	
	
	
	public static final int DARKFRAMES = 16;

	private static final String TAG = "monsterIndexc";
	private static final int SPELLCOUNT = 10;
	private static final int SPELLSCHOOLCOUNT = 3;;
	
	
	public NameGenerator nameGen;
	
	public Sprite circleSprite;
	public NinePatch buttonSelected9, button9, invBack9, invInvalid9, chestBack9, invSelected9, beltSelected9, buttonBlack9;
	public CircleShape medCircle, smallCircle;
	public BitmapFont screenFont;
	//public SpriteAnimation[][] dwarfAnim = new SpriteAnimation[4][DARKFRAMES], dwarfAnimR = new SpriteAnimation[4][DARKFRAMES];
	public static CorneredSprite[] tree0 = new CorneredSprite[16], leaf0 = new CorneredSprite[16];
	
	public static NinePatch jump9, runL9, runR9, climbDown9, climbUp9;
	public static NinePatch text9;
	public static NinePatch textSelected9;
	public Sprite durability;
	//public TextureRegion[] itemTex = new TextureRegion[256];
	//private static CorneredSprite[] itemS = new CorneredSprite[256];
	public static Array<CorneredSprite>[] terrainS = new Array[256], terrainItemS = new Array[256], itemS = new Array[256];
	
	public float[] lightToFloat = new float[DARKFRAMES];                                               
	public int MAPTILESIZE;// = Punk.TILESIZE;
	public int ITEMTILESIZE;// = Punk.TILESIZE;
	//public static int TILESIZE = Punk.TILESIZE;
	public static int GIBTIME = 3000;
	public static int SPIDERHEALTH = 16;
	public static int SPIDERHITTIME = 1000;
	public static int ZOMBIEHEALTH = 9;
	public static int ZOMBIEHITTIME = 1000;
	public static int CHICKENHEALTH = 7;
	public static int PIGHITTIME = 500;
	public static int PIGVERTICALRANGE = 30;
	public static int SLIMEHEALTH = 4;
	public static int NINJAHEALTH = 15;
	public final int max_velocity = 12;
	public static int BIRDCOUNT =  20;
	public static int FLOCKCOUNT = 2;
	public static int BIRDHEALTH = 2;
	public static int TURRETHEALTH = 6;
	//number of body classifications
	public static int BODYCOUNT = 62;
	
	public final int TIPTIMEOUT = 5000;
	
	public GrenadeInfo[] playerGrenades = new GrenadeInfo[20];
	GrenadeInfo[] playerArrows = new GrenadeInfo[20];
	public GrenadeInfo[] playerWands = new GrenadeInfo[30];
	/*public static Color[] colors = new Color[]{
		new Color(0,0,0,1),
		new Color(.0625f,.0625f,.0625f,1), 
		new Color(.125f,.125f,.125f,1), 
		new Color(.1875f,.1875f,.1875f,1), 
		new Color(.25f,.25f,.25f,1),
		new Color(.3125f,.3125f,.3125f,1),
		new Color(.375f,.375f,.375f,1),
		new Color(.4375f,.4375f,.4375f,1),
		new Color(.5f,.5f,.5f,1),
		new Color(.5625f,.5625f,.5625f,1),
		new Color(.625f,.625f,.625f,1),
		new Color(.6875f,.6875f,.6875f,1),
		new Color(.75f,.75f,.75f,1),
		new Color(.8125f,.8125f,.8125f,1),
		new Color(.875f,.875f,.875f,1),
		new Color(.9375f,.9375f,.9375f,1)};*/
	public FixtureDef footFixt;
	public boolean soundEnabled = true;
	public static int SPAWNMAX = 40, SPAWNMIN = 8;
	public static int SPAWNLIGHTLEVEL = 14;
	//public static int SNAKEFLOOR = 100; it's in the snake class
	private PolygonShape[] bodyShapes = new PolygonShape[BODYCOUNT];
	private CircleShape grenadeShape; 
	private Vector2[] verts = new Vector2[4];
	private Vector2[] verts5 = new Vector2[5];
	private Vector2[] verts6 = new Vector2[6];
	public Vector2 tmpV = new Vector2();//used by all the physicsActors
	public Sprite[][] perkS = new Sprite[4][16];
	private CircleShape circleShape;
	//private PolygonShape birdShape;
	private BodyDef[] bodyDefs = new BodyDef[BODYCOUNT];
	private Vector2 playerCenter = new Vector2(0.0f,0.5f);
	public FixtureDef[] fixtures = new FixtureDef[BODYCOUNT];
	public Sprite[][] plankSprite = new Sprite[4][DARKFRAMES];
	public Sprite[][] gibSprites = new Sprite[5][DARKFRAMES];
	private BodyDef[] ropeDefs = new BodyDef[10];
	//private PolygonShape[] ropeShapes = new PolygonShape[10];
	//public FixtureDef[] ropeFixtures = new FixtureDef[10];
	//public RevoluteJointDef ropeJointDef = new RevoluteJointDef();
	public static Sound tommySnd, launcherSnd1, launcherSnd2, launcherSnd3, digSnd, pigSnd, 
	zombieSnd, explosionSnd1, explosionSnd2, explosionSnd3, 
	hurtSndS, hurtSndM, hurtSndL, hurtSndP, throwSnd, healSnd1, healSnd2, tripSnd, deathSnd,
	flailSnd, jumpSnd, nightfallSnd, sunriseSnd, itemSnd, slimeSnd, spiderAttackSnd, spiderHurtSnd, failedSnd,
	breakSnd1, breakSnd2, breakSnd3, spellSnd0;
	public Music music, menuMusic;
	//grapple stuff
	//public FixtureDef[] grappleFixtures = new FixtureDef[4];
	//public RevoluteJointDef grappleMotorDef = new RevoluteJointDef();
	//public DistanceJointDef ropeJointDef = new DistanceJointDef();
	public Sprite[] helpScreens = new Sprite[6];
	public Sprite messageBackS;
	//public Sprite axeIcon, flailIcon;
	public final int FLAILCOUNT = 10;
	public Sprite[] axeS = new Sprite[4], axeSL = new Sprite[4];
	public Sprite[] flailS = new Sprite[FLAILCOUNT], flailSL = new Sprite[FLAILCOUNT];
	//public BodyDef birdBody;
	//public FixtureDef birdFixture;
	public Sprite[] prefsBtn = new Sprite[8];
	public Sprite[] prefsBtnPressed = new Sprite[8];
	//public BodyDef snakeBody;
	//public FixtureDef snakeFixture;
	//private PolygonShape snakeShape;
	
	//public TextureRegion ropeTex, bridgeTex;
	
	public static Array<CorneredSprite> doorSpritesClosed = new Array<CorneredSprite>(), torchSprites = new Array<CorneredSprite>();
	
	public TextureRegion aimerTex;// = new Texture(Gdx.files.internal("data/aimer32.png"));
	public Sprite[] aimer= new Sprite[12];
	//public Sprite grapple;
	//public Texture rockTex ;//= new Texture(Gdx.files.internal("data/rockanim"+Punk.TILESIZE+".png"));
	//public Texture uiLineTex;// = new Texture(Gdx.files.internal("data/uiline.png"));
	//public Sprite uiLine;// = new Sprite(uiLineTex);
	//public TextureRegion uiLineV;// = new TextureRegion(uiLineTex);
	//public TextureRegion uiButtonTex;// = new Texture(Gdx.files.internal("data/uiinv.png"));
	//public Texture beeTexture;// = new Texture(Gdx.files.internal("data/bee.png"));
	public Sprite dashedLineSprite;
	public SpriteAnimation dashedLineAnimation;
	public final int ZCOUNT = 4;
	//public SpriteAnimation[][] zombieWalkAnim2 = new SpriteAnimation[ZCOUNT][DARKFRAMES], zombieWalkAnimR2 = new SpriteAnimation[ZCOUNT][DARKFRAMES];
	
	//public SpriteAnimation[][] zombieWalk = new SpriteAnimation[ZCOUNT][DARKFRAMES], zombieWalkR = new SpriteAnimation[ZCOUNT][DARKFRAMES];

	//public SpriteAnimation[][] zombieHeadAnim = new SpriteAnimation[ZCOUNT][DARKFRAMES], zombieHeadAnimR = new SpriteAnimation[ZCOUNT][DARKFRAMES];
	//public Sprite[][] zombieHeadSprite = new Sprite[ZCOUNT][DARKFRAMES], zombieHeadSpriteR = new Sprite[ZCOUNT][DARKFRAMES];
	//public SpriteAnimation[][] zombieEatAnim = new SpriteAnimation[DARKFRAMES];// = new Animation(0.05f, TextureRegion.split(zombieEatTexture, Punk.TILESIZE, Punk.TILESIZE*2)[0]);	
	//public SpriteAnimation[] zombieWalkAnimR = new SpriteAnimation[DARKFRAMES];
	public SpriteAnimation beeWalkAnimR;
	//public Sprite[] cloudS = new Sprite[2];
	public Sprite arrowSprite;
	public Sprite arrowSpriteR;// = new Sprite(arrowTexture);
	public Sprite jumpSprite, climbUpSprite, climbDownSprite;
	//public Sprite pickSprite;// = new Sprite(new Texture(Gdx.files.internal("data/pickaxe.png")));
	public Sprite healthSprite;// = new Sprite(new Texture(Gdx.files.internal("data/heart"+Punk.TILESIZE+".png")));
	public Sprite fullHealthSprite;
	public NinePatch healthBack9, health9, mana9;
	//public Texture selectTexture ;//= new Texture(Gdx.files.internal("data/select"+Punk.TILESIZE+".png"));
	//public Sprite selectSprite;// = new Sprite(selectTexture);
	//public Sprite ammoSelectSprite;
	public Sprite invBack;
	//public Sprite PHurt;// = new Sprite(PHurtTex);
	//public SpriteAnimation[] pigFlyAnim = new SpriteAnimation[DARKFRAMES], pigFlyAnimR = new SpriteAnimation[DARKFRAMES];// = new Animation(0.5f,TextureRegion.split(PFlyTex, Punk.TILESIZE*2, Punk.TILESIZE)[0]);
	//public Sprite spiderHurtSprite[] = new Sprite[DARKFRAMES];
	//public SpriteAnimation[] spiderWalkAnim = new SpriteAnimation[DARKFRAMES];// = new Animation(0.5f,TextureRegion.split(SpiderTex, Punk.TILESIZE*2, Punk.TILESIZE)[0]);
	//public SpriteAnimation[] spiderPhaseAnim = new SpriteAnimation[DARKFRAMES];// = new Animation(.6f, TextureRegion.split(SPhaseTexture, Punk.TILESIZE*2, Punk.TILESIZE)[0]);
	
	//public Sprite SnakeSprite;// = new Sprite[4];
	//public Sprite slimeSprite;// = new Sprite(SlimeTex);
	//public Sprite dwarfSprite;
	//public Texture blockselTex;
	//public SpriteAnimation[] slimeAnim = new SpriteAnimation[DARKFRAMES];
	//public SpriteAnimation[] beanstalkAnim = new SpriteAnimation[DARKFRAMES];
	public SpriteAnimation digAnim;
	public SpriteAnimation fireAnim;
	
	public SpriteAnimation bloodAnim;
	public Sprite blockSelSprite, crossSelSprite;;
	//public TextureRegion bedTex;
	
	//public TextureRegion gooTex;
	private static int WALLTILES = 42;
	//public static CorneredSprite[] wallS = new CorneredSprite[WALLTILES];
	
	public static TextureAtlas atlas;
	//public TextureAtlas itemAtlas;
	public static CorneredSprite crossS;
	//Texture charTexture ;//= new Texture(Gdx.files.internal("data/player4frame"+Punk.TILESIZE+".png"));
	//Texture charJumpTexture ;//= new Texture(Gdx.files.internal("data/player2frame.png"));
	//public CorneredSpriteAnimation[] charWalkAnim = new CorneredSpriteAnimation[CLASSES];// = new Animation(.25f, TextureRegion.split(charTexture, 32, 64)[0]);
	//public CorneredSpriteAnimation[] charClimbAnim = new CorneredSpriteAnimation[CLASSES];// = new Animation(.25f, TextureRegion.split(charTexture, 32, 64)[0]);

	//public SpriteAnimation charJumpAnim ;//= new Animation(5f, TextureRegion.split(charJumpTexture, 32, 64)[0]);
	//public CorneredSpriteAnimation[] charWalkAnimR = new CorneredSpriteAnimation[CLASSES];
	//public CorneredSprite[] charHeadSprite = new CorneredSprite[CLASSES], charHeadSpriteR = new CorneredSprite[CLASSES];
	//public CorneredSprite[] charStandSprite = new CorneredSprite[CLASSES], charStandSpriteR = new CorneredSprite[CLASSES];
	//public CorneredSprite[] charHandSprite = new CorneredSprite[CLASSES], charHandSpriteR = new CorneredSprite[CLASSES];
	
	
	
	//public CorneredSprite[] doorS = new CorneredSprite[DOORTILES], dungeonS = new CorneredSprite[DUNGEONTILES];
	
	
	public Sprite dotSprite;
	//public Sprite[] uiButtons = new Sprite[4];
	public Sprite beltHideButton, beltShowButton, beltHideButtonBottom;
	public Sprite invBtn, craftBtn, perkBtn;
	public Sprite helpBtn, loadingBtn, lightBtn;
	public ParticleEmitter explosion1, explosion2, whiteSmoke;
	public Sprite whitesmokeSprite;
	public Sprite tmpSprite;
	public TextureRegion tmpTexR;
	public float textureScale = 1/(float)( MAPTILESIZE/Punk.TILESIZE );
	public Preferences prefs;
	public Pixmap tmpPixmap;// = new Pixmap();
	public SpriteAnimation ninjaSmokeAnim;
	public PolygonShape axeHead, axeHeadL;
	//public static CorneredSprite[] mushrooms = new CorneredSprite[16], flowers = new CorneredSprite[20]; 
	//public static CorneredSprite[][] missiles = new CorneredSprite[12][8]; 
	//public static CorneredSprite[] skyTiles = new CorneredSprite[16];
	public static Faction[] factions = new Faction[Faction.FACTIONCOUNT];
	public static Faction PhysicsBlockFaction = new Faction(-2);
	private static Array<ItemDef>[] info = new Array[512];
	public PunkBodies(){
		
	}
	
	public Spell[][] spells = new Spell[SPELLCOUNT][SPELLSCHOOLCOUNT];

	

	Array<TouchAction>[] melee = new Array[17];

	private void initItemDefs() {
		//info = new ItemInfo[512];
		
		for (int i = 0; i < 512; i++){
			info[i] = new Array<ItemDef>();
			info[i].ordered = true;
		}
		for (int i = 1; i < 256; i++){
			ItemDef def = new ItemDef(2);
			//def.data = new PlaceBlock(map, mi, world);
			info[i].add(def);
			
		}
		//if (true)throw new GdxRuntimeException("done");
		//broomsticks
		for (int i = 0; i < 10; i++){
			ItemDef inf = new ItemDef(80);
			inf.data = playerFlights[i];
			info[313].add(inf);
			
			
			if (playerFlights[i] != null){
				inf.durability = playerFlights[i].durability;
				inf.scale = 2f;
				inf.xOff = .75f;
				inf.yOff = .75f;
				inf.angle = 135f;
			}
				//else Gdx.app.log(TAG, "null flight"+i);
		}
		
		//wands
		for (int i = 0; i < 20; i++){
			ItemDef inf = new ItemDef(9);
			inf.data = playerWands[i];
			info[14].add(inf);
		}
		info[310].add(new ItemDef(7));//bucket
		
		
		for (int i = 0; i < 6; i++){//weapons n tools
			ItemDef inf = new ItemDef(11);
			inf.data = playerTools[i];
			
			if (playerTools[i] != null)
				inf.durability = playerTools[i].durability;
			inf.xOff = .75f;
			inf.yOff = .25f;
			inf.angle = -90;
			info[256+19].add(inf);
			
		}
		
		for (int i = 0; i < 20; i++){//arrows
			ItemDef inf = new ItemDef(15);
			GrenadeInfo gr = playerArrows[i];;
			
				
			
			inf.data = gr;
			inf.xOff = .5f;
			inf.yOff = .5f;
			info[102+256].add(inf);
		}
		
		/*for (int i = 0; i < 17; i++)
			for (int m = 0; m < melee[i].size; m++)
			{//melee
				
				
			}*/
		//initMelee(null, null);
		
		
		
		for (int i = 200+256; i < 512; i++){//food
			ItemDef inf = new ItemDef(10);
			info[i].add(inf);
		}
		//info[50].gameMode =
		info[291].add(new ItemDef(21));//map
		
	}



	public final int MOBS = 100;
	MobInfo[] enemyMobs = new MobInfo[MOBS];
	//ComponentRanged[] enemyRanged = new ComponentRanged[MOBS];
	ComponentStates[] enemyAttack = new ComponentStates[MOBS];
	ComponentAnimation[] enemyAnim = new ComponentAnimation[MOBS];
	
	GrenadeComponent trailNone = new GrenadeComponent(){
		@Override
		public void act(Grenade mob, PunkMap map, Player player,
				PunkBodies monsterIndex, World world) {	
		}
	};
	CTrailS trailFire = new CTrailS(); 
	
	
	public void initFactions(){
		//everyone hates everyone
		for (int i = 0; i < Faction.FACTIONCOUNT; i++){
			factions[i] = new Faction(i);
			
			for (int j = 0; j < Faction.FACTIONCOUNT; j++){
				if (j != i)factions[i].opinion.set(j, -2);
				else factions[i].opinion.set(j,300);
				//if (j == 1) factions[i].opinion.set(j, 20);
			}
		}
		//deities neutral
		for (int i = 2; i < 11;i++){
			factions[i].opinion.set(1, 0);
			factions[1].opinion.set(i, 0);
		}
	}
	public void initPlayerWands2(PunkMap map, World world){
		ComponentExplosion explNone = new ComponentExplosion(0,0, BlockDamageType.FROST);
		ComponentExplosion explS = new ComponentExplosion(2,10, BlockDamageType.FROST);
		ComponentExplosion explM = new ComponentExplosion(4,120, BlockDamageType.FROST);
		ComponentExplosion explL = new ComponentExplosion(6,240, BlockDamageType.FROST);
		
		trailFire.set(2, BlockDamageType.FIRE);
		
		
		playerWands[0] = new WandInfo(map, this, world).set(//dirt
				0,//
				
				24,//, 
				
				-1,//damageType
				12,
				1,//src height
				DamageType.BLUNT,//release
				explNone,//maxVelocity  (squared)
				2f,//minVelocity   (squared)
				000, 16, 200,
				PunkBodies.getGrenadeFrame(2), trailNone, false
				);
		playerWands[0].particleIndex = 0;
		
		playerWands[1] = new WandInfo(map, this, world).set(//dirt
				0,//			
				8,//, 
				
				-1,//damageType
				12,
				1,//src height
				DamageType.BLUNT,//release
				explM,//maxVelocity  (squared)
				2f,//minVelocity   (squared)
				000, 16, 750,
				PunkBodies.getGrenadeFrame(2), trailFire, false
				);
		playerWands[1].particleIndex = 0;
		
		explS.sound = PunkBodies.explosionSnd1;
		explM.sound = PunkBodies.explosionSnd2;
		explL.sound = PunkBodies.explosionSnd3;
	}
	ComponentExplosion explNone = new ComponentExplosion(0,0, BlockDamageType.PICKAXE);
	private static GrenadeInfo defaultThrownGrenade;
	GrenadeInfo[][] grenades = new GrenadeInfo[12][21];
	public void initPlayerWeapons(PunkMap map, World world){
		//initPlayerWands(map, world);
		ComponentExplosion explS = new ComponentExplosion(5,80, BlockDamageType.FROST);
		ComponentExplosion explM = new ComponentExplosion(2,20, BlockDamageType.CHARGE);
		ComponentExplosion explL = new ComponentExplosion(3,14, BlockDamageType.CHARGE);
		int iid = 60, im = 0;
		grenades[iid-60][im] = new GrenadeInfo(map, this, world).setThrown(
				new ComponentExplosion(3,20,
						BlockDamageType.FIRE), trailNone, itemS[iid].get(im))
						.explodesGround().explodesMobs();
		im++;
		grenades[iid-60][im] = new GrenadeInfo(map, this, world).setThrown(
				new ComponentExplosion(3,20, 
						BlockDamageType.FROST), trailNone, itemS[iid].get(im))
						.explodesGround().explodesMobs();
		im++;
		grenades[iid-60][im] = new GrenadeInfo(map, this, world).setThrown(
				new ComponentExplosion(3,5, 
						BlockDamageType.CHARGE), trailNone, itemS[iid].get(im))
						.explodesGround().explodesMobs();
		
		
		
		iid = 61; im = 0;
		grenades[iid-60][im] = new GrenadeInfo(map, this, world).setThrown(
				new ComponentExplosion(3,20,
						BlockDamageType.FIRE), trailNone, itemS[iid].get(im))
						.explodesGround().explodesMobs();
		im++;
		grenades[iid-60][im] = new GrenadeInfo(map, this, world).setThrown(
				new ComponentExplosion(3,20, 
						BlockDamageType.FROST), trailNone, itemS[iid].get(im))
						.explodesGround().explodesMobs();
		im++;
		grenades[iid-60][im] = new GrenadeInfo(map, this, world).setThrown(
				new ComponentExplosion(3,5, 
						BlockDamageType.CHARGE), trailNone, itemS[iid].get(im))
						.explodesGround().explodesMobs();
		
		
		iid = 62; im = 0;
		grenades[iid-60][im] = new GrenadeInfo(map, this, world).setThrown(
				new ComponentExplosion(3,20,
						BlockDamageType.FIRE), trailNone, itemS[iid].get(im))
						.explodesGround().explodesMobs();
		im++;
		grenades[iid-60][im] = new GrenadeInfo(map, this, world).setThrown(
				new ComponentExplosion(3,20, 
						BlockDamageType.FROST), trailNone, itemS[iid].get(im))
						.explodesGround().explodesMobs();
		im++;
		grenades[iid-60][im] = new GrenadeInfo(map, this, world).setThrown(
				new ComponentExplosion(3,5, 
						BlockDamageType.CHARGE), trailNone, itemS[iid].get(im))
						.explodesGround().explodesMobs();
		
		
		
		defaultThrownGrenade = new GrenadeInfo(map, this, world).set(//dirt
				0,//id,  b 
				
				32,//, lifeticks
				
				-1,//damageType
				12,
				1,//
				DamageType.BLUNT,//
				explNone,//  srcHeight
				2f,//   (release)
				2000, //maxV
				400, //minV
				200,
				PunkBodies.getGrenadeFrame(0), trailNone, false
				);
		
		playerGrenades[0] = new GrenadeInfo(map, this, world).set(//dirt
				0,//id, 
				
				32,//, lifeticks
			
				-1,//damageType
				12,
				1,//
				DamageType.BLUNT,//
				explNone,//  srcHeight
				2f,//   (release)
				2000, //maxV
				400, //minV
				150,
				PunkBodies.getGrenadeFrame(0), trailNone, false
				);
		explS.sound = PunkBodies.explosionSnd1;
		explM.sound = PunkBodies.explosionSnd2;
		explL.sound = PunkBodies.explosionSnd3;
		playerGrenades[1] = new GrenadeInfo(map, this, world).set(//rock
				431,//id
				
				32, //ticks
			 
				-1,//
				12,
				3,//
				DamageType.BLUNT,//
				explNone,//maxVelocity  (squared)
				2f,//minVelocity   (squared)
				2000, 400, 200,
				PunkBodies.getGrenadeFrame(1), trailNone, false
				);
		
		playerGrenades[2] = new GrenadeInfo(map, this, world).set(//
				0,//lifeTicks, 
				
				32,//explodesOnGroundContact, 
			
				-1,//
				12,
				0,//src height
				DamageType.BLUNT,//release
				explNone,//maxVelocity  (squared)
				2f,//minVelocity   (squared)
				200, 24, 2,
				PunkBodies.getGrenadeFrame(2), trailNone, false
				);
		playerGrenades[3] = new GrenadeInfo(map, this, world).set(//gold gren
				3,//, 
				
				32,//, 
				
				-1,//
				12,
				0,//src height
				DamageType.EXPLOSION,//release
				explS,//maxVelocity  (squared)
				2f,//minVelocity   (squared)
				500, 24, 2,
				PunkBodies.getGrenadeFrame(3), trailNone, false
				)
				.explodesGround()
				;
		playerGrenades[4] = new GrenadeInfo(map, this, world).set(//copp gren
				4,//, 
				
				32,//, 
		
				-1,//
				12,
				0,//
				DamageType.EXPLOSION,//
				explM,//  ()
				2f,//   ()
				500, 24, 2,
				PunkBodies.getGrenadeFrame(4), trailNone, false
				)
				.explodesGround().explodesMobs()
				;
		playerGrenades[5] = new GrenadeInfo(map, this, world).set(//giron gren
				5,//, 
				
				32,//, 
				
				-1,//
				12,
				0,//
				DamageType.EXPLOSION,//
				explL,//  ()
				2f,//   ()
				500, 24, 2,
				PunkBodies.getGrenadeFrame(5), trailNone, false
				)
				.explodesGround().explodesMobs()
				;
		
		//pipe bombs
		
		playerGrenades[6] = new GrenadeInfo(map, this, world).set(//gold pipe
				6,//lifeTicks, 
				
				32,//, 
				 
				-1,//damageType
				12,
				0,//src height
				DamageType.BLUNT,//release
				explS,//maxVelocity  (squared)
				2f,//minVelocity   (squared)
				200, 24, 2,
				PunkBodies.getGrenadeFrame(6), trailNone, false
				)
				.explodesTimer()
				;
		playerGrenades[7] = new GrenadeInfo(map, this, world).set(//copp pipe
				7,//lifeTicks, 
				
				32,//explodesOnGroundContact, 
				
				-1,//damageType
				12,
				0,//src height
				DamageType.BLUNT,//release
				explM,//maxVelocity  (squared)
				2f,//minVelocity   (squared)
				200, 24, 2,
				PunkBodies.getGrenadeFrame(7), trailNone, false
				)
				.explodesTimer()
				;
		playerGrenades[8] = new GrenadeInfo(map, this, world).set(//giron pipe
				8,//lifeTicks, 
				
				32,//explodesOnGroundContact, 
				
				-1,//damageType
				12,
				0,//src height
				DamageType.BLUNT,//release
				explL,//maxVelocity  (squared)
				2f,//minVelocity   (squared)
				500, 24, 2,
				PunkBodies.getGrenadeFrame(8), trailNone, false
				)
				.explodesTimer()
				;
		
		playerGrenades[9] = new GrenadeInfo(map, this, world).set(//dirt
				439,//id, 
				
				32000,//, 
				
				-1,//damageType
				12,
				2,//src height
				DamageType.BLUNT,//release
				explNone,//maxVelocity  (squared)
				2f,//minVelocity   (squared)
				2000, 400, 200,
				PunkBodies.getGrenadeFrame(9), trailNone, false
				)
				.bounces()
				;
		
		playerGrenades[10] = new GrenadeInfo(map, this, world).set(//rock
				440,//id
				
				3200, //ticks
				
				-1,//
				12,
				4,//
				DamageType.BLUNT,//
				explNone,//maxVelocity  (squared)
				2f,//minVelocity   (squared)
				2000, 400, 200,
				PunkBodies.getGrenadeFrame(10), trailNone, false
				)
				.bounces();
		
		playerGrenades[11] = new GrenadeInfo(map, this, world).set(//rock
				441,//id
				
				3200, //ticks
				
				-1,//
				12,
				4,//
				DamageType.BLUNT,//
				explNone,//

				2f,//

				2000, 400, 
				200,
				PunkBodies.getGrenadeFrame(10), trailNone, false
				)
				.bounces()
				;
		//playerGrenades[11].setDirectional(missiles[0]);
		
		
		
		int n = 0;
		playerArrows[n] = new GrenadeInfo(map, this, world);
		playerArrows[n].set(
				102//id 
				,512//lifeTicks, 
				
				,-1//particleIndex,
				,4//throwStrength, 
				,1//damage,
				,DamageType.PIERCING//damageType,
				,explNone//explosion,
				,Player.EYEHEIGHT//srcHeight, 
				,2000//release, 
				,200//maxVelocity,
				,2000//minVelocity, 
				,getItemFrame(102+256, n)//s, 
				, trailNone//trail);
				, true//isDirectional
				);
		initMelee(map, world);
	}
	
	
	public void initMelee(PunkMap m, World w){
		initItemDefs();
		
		for (int i = 0; i < 17; i++){
			melee[i] = new Array<TouchAction>(true, 1);
		}
		
		readWeapons(info[256+0], "data/items/256.txt", m, w);
		
		readBlocks("data/items/blocks.txt");
		
		
		
		for (int i =0 ; i<= 19; i++){//projectiles
			ItemDef inf = new ItemDef(1);
			
			inf.data = playerGrenades[i];
			inf.xOff = .5f;
			inf.yOff = .5f;
			
			info[103+256].add(inf);
		}
		for (int i = 60; i <= 67; i++)
			for (int j = 0; j < 20; j++){
				if (grenades[i-60][j] == null) continue;
				ItemDef inf = new ItemDef(1);
				inf.data = grenades[i-60][j];
				inf.xOff = .5f;
				inf.yOff = .5f;
				info[i+256].add(inf);
			}
		
		for (int i = 1; i < 256; i++)
			for (int j = 0; j < info[i].size; j++)
			{
				ItemDef def = info[i].get(j);
				def.data = new PlaceBlock(m, this, w);
				def.xOff = .5f;
				def.yOff = .5f;
			}
		
		
		
		//finalize
		for (int i = 0; i < 512; i++){
			for (int j = 0; j < info[i].size; j++){
				info[i].get(j).finalizeDescription();
			}
				
		}
		
		
		
	}
	
	
	
	
	private void readBlocks(String string) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(Gdx.files.internal(string).read()));
		//Gdx.app.log(TAG, "read weapons");
		try {
			String line = "";
			//do{
			int index = -1;
			while (line != null){
				line = reader.readLine();
				if (line == null){//maybe finish up?
				
					break;
				}
				///loop of some kind here
				{
					
					if (line.indexOf('#') != -1) {}//comments
					else {
						switch (readLine(line)){
						case 'N':

							index = Integer.parseInt(tuple[0]);
							if (info[index].size > 0){
								info[index].get(0).name = tuple[1];
								info[index].get(0).desc = tuple[2];
								int count = 3;
								while (tuple[count] != null && tuple[count+1] != null){
									info[index].get((count-1)/2).name = tuple[count];
									info[index].get((count-1)/2+1).desc = tuple[count+1];
									count+=2;
								}
							}
							
							break;
						
							
						}
						
						
						
					}
					
					
				}
				
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		
	}
	
	private void readWeapons(Array<ItemDef> array, String string, PunkMap map, World world) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(Gdx.files.internal(string).read()));
		Gdx.app.log(TAG, "read weapons");
		try {
			String line = "";
			//do{
			int index = -1;
			String name = "name", desc = "desc";
			int damage = 0, damageDistribution = 0, prof = 0, damageType = 0;
			float speed = 0, range = 0;
			int[] attacks = new int[1];
			int count = 0;
			while (line != null){
				count++;
				line = reader.readLine();
				if (line == null && count > 1){//maybe finish up?
					WeaponInfo action = new WeaponInfo(map, this, world, damage, damageDistribution, speed, range, damageType, prof);
					ItemDef inf = new ItemDef(81);
					//TODO use index for something
					action.modes = attacks;
					inf.data = action;;		
					inf.name = name;
					inf.desc = desc;
					array.add(inf);
					//Gdx.app.log(TAG, "write final item");
					break;
				}
				
				{
					
					if (line.indexOf('#') != -1) {}//comments
					else {
						switch (readLine(line)){
						case 'N'://writes previous item
							if (index != -1){
								WeaponInfo action = new WeaponInfo(map, this, world, damage, damageDistribution, speed, range, damageType, prof);
								ItemDef inf = new ItemDef(81);
								//TODO use index for something
								action.modes = attacks;
								inf.data = action;;		
								inf.name = name;
								inf.desc = desc;
								array.add(inf);
								//Gdx.app.log(TAG, "write item"+min+" "+max+" "+speed+" "+range+" "+damageType+" "+prof);
								
								//reset defaults after so there's defaults if stuff isn't init'd
								attacks = new int[1]; damage = 0; damageDistribution = 0; prof = 0; damageType = 0;
								index++;
							}
						
							//array.add(action);
							
							
							index = Integer.parseInt(tuple[0]);
							name = tuple[1];
							desc = tuple[2];
							
							break;
						case 'D':
							damage = Integer.parseInt(tuple[0]);
							damageDistribution = Integer.parseInt(tuple[1]);
							speed = Float.parseFloat(tuple[2]);
							range = Float.parseFloat(tuple[3]);
							break;
						case 'A':
							int i = 1;
							prof = Integer.parseInt(tuple[0]);
							while (tuple[i] != null){
							i++;	
							}
							i--;
							attacks = new int[i];
							for (int c = 0; c < i; c++){
								attacks[c] = Integer.parseInt(tuple[c+1]);
								//Gdx.app.log(TAG, "anim mode"+attacks[c]);
							}
							
							
							
							break;
							
						case 'F':
							
							
							break;
						
							
						}
						
						
						
					}
					
					
				}
				
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public char readLine(String line){
		char ch = '!';
		
		int colon = line.indexOf(':');
		int i = 0, lastMatch = colon + 1;
		for (i = 0; i < 8; i++) {
			int comma = line.indexOf(';', lastMatch);
			if (comma == -1) {
				//if (i == 0) throw new GdxRuntimeException("Invalid line: " + line);
				break;
			}
			tuple[i] = line.substring(lastMatch, comma).trim();
			lastMatch = comma + 1;
		}
		tuple[i] = line.substring(lastMatch).trim();
		tuple[i+1] = null;
		//return i + 1;
		
		if (line.length() > 0)return line.charAt(0);
		return ' ';
	}
	String[] tuple = new String[10];



	private static int SWORDS = 5, FLAILS = 5, AXES = 5, TOOLS = 20;
	private WeaponInfo[] swords = new WeaponInfo[SWORDS], flails = new WeaponInfo[SWORDS], axes = new WeaponInfo[AXES], tools = new WeaponInfo[TOOLS];
	public ToolInfo[] playerMelee = new ToolInfo[20];

	public final static int CLASSTOTAL = 14, RACETOTAL = 1, USEABLESKILLCOUNT = 10;
	private static final int SKILLTOTAL = 20, JUMPTOTAL = 9, BLESSINGTOTAL = 10;
	private static final int TILERES = 16;;
	private static ItemDef[] useableSkills = new ItemDef[USEABLESKILLCOUNT];

	public void initEnemyWeapons(){}
	public void initMobInfos(){
	//	initEnemyWeapons();
		nightMobs = new Array<MobInfo>();
		dayMobs = new Array<MobInfo>();
		sunsetMobs = new Array<MobInfo>();
		sunriseMobs = new Array<MobInfo>();;
		ComponentAnimation anim = new ComponentAnimation();
		
		//MobInfo mobInfo = new MobInfo();	
		anim.set(.2f, false, atlas, "imp1body", 2, 2);
		//update.setDrop(450,450,0,1,0,0);
		int[] spawnBlocks = new int[]{2,3,3};	
		//mobInfo.setSpawnBlockIDs(true, 1, spawnBlocks);
		
		
		
		MobInfo[] classInfos = new MobInfo[16];
		
		classInfos[0] = new BarbarianInfo();
		classInfos[1] = new PaladinInfo();
		classInfos[2] = new BerserkerInfo();

		//barbarian
		classInfos[3] = new RogueInfo();
		//ninja
		classInfos[4] = new NinjaInfo();
		//clerics
		classInfos[5] = new ClericInfo();
		
		//druid
		classInfos[6] = new DruidInfo();
		//ranger
		classInfos[7] = new RangerInfo();
		classInfos[8] = new WizardInfo();
		//wild mage
		classInfos[9] = new WildMageInfo();
		//necromancer
		classInfos[10] = new NecromancerInfo();
		
		classInfos[11] = new JugglerInfo();
		classInfos[12] = new BuilderInfo();
		classInfos[13] = new SmithInfo();
		
		
		
		MobInfo[] classes = new MobInfo[CLASSTOTAL];
		for (int i = 0; i < CLASSTOTAL; i++){
			/*MobInfo mobInfo = classInfos[i];
			anim = enemyAnim[i];
			update = new CUpdateSheep();		
			move = new  CMoveSheep();
			
			
			//anim.set(.2f, false, atlas, "skeleton1body", 2, 2);
			update.setDrop(450,450,0,1,0,0);
			mobInfo.setSpawnBlockIDs(true, 0, spawnBlocks);
			
			mobInfo.set(2, new CUpdateStates(), move, update, humanAnim, true, true, 300, 
					
					hurtSndM, true, false);
			mobInfo.setImmunity(true);//fire*/
			classes[i] = classInfos[i];//mobInfo;
			//enemyMobs[i].defaultButtons = classButtons;
		}
		RaceDef[] races = new RaceDef[RACETOTAL];
		races[0] = new RaceDef("Human", new int[]{}, new int[]{});
		
		
		Skill[] skills = new Skill[SKILLTOTAL];
		skills[0] = new Skill(0);
		skills[0].data = new Effect(){
			public void onStats(GenericMob mob){
				mob.activeJump = 0;
			}
		};
		skills[0].type = 1;
		skills[1] = new Skill(1);
		skills[1].type = 1;
		skills[1].data = new Effect(){
			public void onStats(GenericMob mob){
				mob.activeJump = 1;
				
				
				//Gdx.app.log(TAG, "wiz");
			}
		};
		skills[2] = new Skill(1);
		skills[2].type = 1;
		skills[2].data = new Effect(){
			public void onStats(GenericMob mob){
				mob.activeJump = 3;
				//Gdx.app.log(TAG, "wiz");
			}
		};
		skills[7] = new Skill(1);
		skills[7].type = 1;
		skills[7].data = new Effect(){
			public void onStats(GenericMob mob){
				mob.activeJump = 7;
				//Gdx.app.log(TAG, "wiz");
			}
		};
		ButtonOverride[] jumps = new ButtonOverride[JUMPTOTAL];
		jumps[0] = new BOJPaladin();
		jumps[1] = new BOJBarbarian();
		jumps[2] = new BOJWizard();
		jumps[3] = new BOJRogue();
		jumps[4] = new BOJPaladin();
		jumps[5] = new BOJPaladin();
		jumps[6] = new BOJPaladin();
		jumps[7] = new BOJWizard();
		//jumps[2] = new BOJWizard();
		
		
		Blessing[] blessings = new Blessing[BLESSINGTOTAL];
		blessings[0] = new Blessing(0){
			@Override
			public void onStats(GenericMob mob){
				mob.stats[GenericMob.S_STRENGTH]+= 5;
			}
			public boolean draw(GenericMob mob, IntArray array){
				mob.tints[3].add(-1);
				//Gdx.app.log(TAG, "TINTTTNTNTNTNITNITNTITNITNITNITNITNTI");
				return false;
			}
			
		};
		blessings[0].time(1f,5f);
		
		
		
		
		GenericMob.initInfos(classes, races, skills, jumps, blessings);
		
		
		deities[0] = new Lumiera();
		deities[1] = new Vakava(); 
		deities[2] = new Ignis(); 
		deities[3] = new Knossos();
		deities[4] = new GVoid();
		deities[5] = new Graviticus();
		//deities[6] = new ;
		//deities[7] = new ;
		//deities[8] = new ;
		
				
		
		initPassiveMobInfos();
		
	}
	
	//public static StateFrameInfo[] states;
	public static Deity[] deities = new Deity[9];



	public MobInfo[] sheepInfo, reindeerInfo, chickenInfo, basiliskInfo, scorpionInfo, dogInfo, wolfInfo, yetiInfo;
	public MobInfo[] doorInfo = new MobInfo[16];
	public MobInfo rubberInfo;
	private void initPassiveMobInfos() {
		

		
		//////
		
		int d = 0;
		Sound doorSnd = null;
		ComponentStates doorAtt = new ComponentStates();
		
		
		ComponentAnimation doorAnim = new ComponentAnimation();
		doorAnim.set(.5f, true, atlas, "door"+d+"v", 2, 2);
		ComponentUpdate doorUpd = new CUpdateDoor(d);
			
		
		
		
		
		rubberInfo = new MobInfo(){

			@Override
			public void onSpawn(GenericMob mob, PunkMap map) {
				// TODO Auto-generated method stub
				
			}
			
		};
		rubberInfo.set(
				57,//bodyID, 
				doorAtt,//attack, 
				new CMoveNone(),//move, 
				doorUpd,//update, 
				//anim, 
				false,//hasBBs, 
				false,//isRandomUpdate, 
				10000,//updateInterval, 
				//hurt, 
				false//burns
				, false, null
				);
		rubberInfo.sizeY = 0;
		
		
	}



	public final int TOOLCOUNT = 30;
	public ToolInfo[] playerTools = new ToolInfo[TOOLCOUNT];
	public void initPlayerTools(PunkMap map, World world){
		for (int i = 0; i < 5; i++){
			playerFlights[i] = new FlightInfo(map, world, this, atlas, i);
		}
		//initPlayerWeapons(map, world);
		//pickaxes
		//bigger number is slower
		
		//7 8 9 knuckledusters
		
		//pickaxes
		playerTools[0] = new ToolInfo(map, this, world, 100, 20, 3f);
		playerTools[1] = new ToolInfo(map, this, world, 750, 40, 1f);
		playerTools[2] = new ToolInfo(map, this, world, 500, 180, 1.3f);
		playerTools[3] = new ToolInfo(map, this, world, 750, 200, 1f);
		playerTools[4] = new ToolInfo(map, this, world, 1300, 500, .75f);
		
		/*//shovels
		playerTools[5] = new ToolInfo(map, this, world, .007f, .045f, .0245f);
		playerTools[6] = new ToolInfo(map, this, world, .0037f, .043f, .023f);
		playerTools[7] = new ToolInfo(map, this, world, .0062f, .041f, .0241f);
		playerTools[8] = new ToolInfo(map, this, world, .0042f, .039f, .0239f);
		playerTools[9] = new ToolInfo(map, this, world, .003f, .037f, .0237f);
		
		//handaxes
		playerTools[10] = new ToolInfo(map, this, world, .0145f, .045f, .007f);
		playerTools[11] = new ToolInfo(map, this, world, .014f, .043f, .006f);
		playerTools[12] = new ToolInfo(map, this, world, .0135f, .041f, .0065f);
		playerTools[13] = new ToolInfo(map, this, world, .013f, .039f, .0057f);
		playerTools[14] = new ToolInfo(map, this, world, .0125f, .037f, .0045f);
		*/
		initPlayerWeapons(map, world);
		
		
	}
	public static CorneredSprite transS, dot;
	public static LayeredAnimation[] anims = new LayeredAnimation[16]; 
	public Sprite[] uiShapeSprites = new Sprite[8];
	private TextureAtlas humanAtlas;
	private LayeredAnimation humanAnim;
	public static Sprite[] colorPixels = new Sprite[CorneredSprite.COLOR_TOTAL]; 
	public void initSprites(TextureAtlas... atlasses){
		FileHandle externalSkin = Gdx.files.external("PocketMiner/tiles.txt");
		//if (externalSkin.exists()) atlas = new TextureAtlas(externalSkin);
		//else atlas = new TextureAtlas(Gdx.files.internal("data/tiles.txt"));
		atlas = atlasses[0];
		humanAtlas = atlasses[1];
		humanAnim = new OgreAnimation(atlasses, 2);//new HumanoidAnimation(humanAtlas);
		
		if (Start.saveData){
			anims[0] = new HumanoidAnimation(atlasses, 1, 9f);
			((HumanoidAnimation) anims[0]).save(Gdx.files.external(Punk.saveDir+"human.dat"));
			//anims[1] = new OgreAnimation(atlasses, 1);
			//((HumanoidAnimation) anims[1]).save(Gdx.files.external(Punk.saveDir+"ogre.dat"));
			
		}else {
			Texture tex = new Texture(Gdx.files.internal("data/humans.png"));
			anims[0] = new HumanoidAnimation(Gdx.files.external(Punk.saveDir+"human.dat"),tex, 1);
		}
		
		//humanAnim.states = initStateAnims();
		
		
		dot = atlas.createCorneredSprite("dot");
		dot.setSize(0.1875f,.1875f);
		healthBack9 = new NinePatch(atlas.createSprite("healthbacknine"), 1,1,1,1);
		health9 = new NinePatch(atlas.createSprite("healthnine"), 1,1,1,1);
		mana9 = new NinePatch(atlas.createSprite("mananine"), 1,1,1,1);
		
		transS = atlas.createCorneredSprite("trans");
		button9 = new NinePatch(atlas.createSprite("btngrey9p"), 8,8,8,8);
		buttonBlack9 = new NinePatch(atlas.createSprite("btnblack9p"), 8, 8, 8, 8);
		buttonSelected9 = new NinePatch(atlas.createSprite("btngreen9p"), 8,8,8,8);
		invBack9 = new NinePatch(atlas.createSprite("btninvback9p"), 8,8,8,8);
		invInvalid9 = new NinePatch(atlas.createSprite("btnblack9p"), 8,8,8,8);
		chestBack9 = new NinePatch(atlas.createSprite("btnchestback9p"), 8,8,8,8);
		invSelected9 = new NinePatch(atlas.createSprite("btninvselected9p"), 8,8,8,8);
		beltSelected9 = new NinePatch(atlas.createSprite("btnbeltselected9p"), 8,8,8,8);
		text9 = new NinePatch(atlas.createSprite("btntext9p"), 8,8,8,8);
		textSelected9 = new NinePatch(atlas.createSprite("btntextselected9p"), 8,8,8,8);
		screenFont = new BitmapFont(Gdx.files.internal("data/font16.fnt"), Gdx.files.internal("data/font16.png"),false);
		screenFont.setScale(.1f);
		TextureRegion runTex = atlas.findRegion("leftnine");
		TextureRegion runTexR = atlas.findRegion("leftnine");
		runTexR.flip(true,  false);
		jump9 = new NinePatch(atlas.createSprite("jumpnine"), 8,8,8,8);
		
		runL9 = new NinePatch(runTex, 5, 27, 5, 27);
		runR9 = new NinePatch(runTexR, 5, 27, 5, 27);
		climbDown9 = new NinePatch(atlas.createSprite("climbdownnine"), 2, 30, 2, 30);
		climbUp9 = new NinePatch(atlas.createSprite("climbupnine"), 2, 30, 2, 30);
		circleSprite = atlas.createSprite("circle");
		messageBackS = atlas.createSprite("messageback");
		dashedLineSprite= atlas.createSprite("dashedline");
		TextureRegion tmpR = atlas.findRegion("dashedline");
		TextureRegion[][] keyFrames = tmpR.split(16, 1);
		Sprite[] tmpKeyFrames = new Sprite[keyFrames.length];
		for (int i = 0; i < 4; i++){
			tmpKeyFrames[i] = new Sprite(keyFrames[i][0]);
			   //if (flipx) tmpKeyFrames[i].flip(true, false);
		}
		dashedLineAnimation = new SpriteAnimation(.125f,tmpKeyFrames, 16,1);
		solidLine = atlas.createSprite("solidline");
		//for (int i = 0; i < DARKFRAMES; i++)
		//	colors[i] = new Color((float)i/DARKFRAMES, (float)i/DARKFRAMES, (float)i/DARKFRAMES, 1);
			
		flyArrowS = atlas.createCorneredSprite("flyarrow");
		
		flyArrowS.setSize(1, .5f);
		flyArrowS.setOrigin(0, .25f);
		
		
		/*uiShapeSprites[0] = atlas.createSprite("uisquare");
		uiShapeSprites[1] = atlas.createSprite("uicircle");
		uiShapeSprites[2] = atlas.createSprite("uidiamond");
		uiShapeSprites[3] = atlas.createSprite("uicross");
		uiShapeSprites[4] = atlas.createSprite("uisquarefill");
		uiShapeSprites[5] = atlas.createSprite("uicirclefill");
		uiShapeSprites[6] = atlas.createSprite("uidiamondfill");*/
		//uiShapeSprites[7] = atlas.createSprite("");
		
		for (int i  = 0; i < CorneredSprite.COLOR_TOTAL; i++){
			colorPixels[i] = atlas.createSprite("pixel");
			
		}
		
		
		//anims[0][0] = new HumanoidAnimation(atlas);
		{
				boolean done = false;
				int k = 0;
				doorSpritesClosed.ordered = true;
			do{
				
				if (atlas.findRegion("door"+k+"v0f") == null) {
					done = true;
				} else {
					CorneredSprite s = atlas.createCorneredSprite("door"+k+"v0f");
					s.setSize(2,2);
					s.setOrigin(0,0);
					doorSpritesClosed.add(s);
				}
				
				k++;
			} while (!done);
		}
			
		{
			boolean done = false;
			int k = 0;
			torchSprites.ordered = true;
			do{
				
				if (atlas.findRegion("torch"+k+"v") == null) {
					done = true;
				} else {
					CorneredSprite s = atlas.createCorneredSprite("torch"+k+"v");
					s.setSize(2,2);
					s.setOrigin(0,0);
					torchSprites.add(s);
				}
				
				k++;
			} while (!done);
		}
			
			
		/*	
		for (int i = 0; i < 16; i++){
			skyTiles[i] = atlas.createCorneredSprite("sky"+i+"f");
			skyTiles[i].setSize(1,1);
		}
		
		grapple = atlas.createSprite("grapple");
		ropeTex = atlas.createSprite("rope");
		bridgeTex =  atlas.createSprite("bridge");
*/
		durability = atlas.createSprite("durability");
		healthSprite = atlas.createSprite("heart0h");
		healthSprite.setSize(Punk.MAPTILESIZE/2, Punk.MAPTILESIZE/2);
		fullHealthSprite = atlas.createSprite("heart1h");
		fullHealthSprite.setSize(Punk.MAPTILESIZE/2, Punk.MAPTILESIZE/2);
		loadingBtn = atlas.createSprite("loading");
		lightBtn = atlas.createSprite("light");
		loadingBtn.setSize(16,16);
		lightBtn.setSize(16,16);
		loadingBtn.setPosition(64, 0);
		lightBtn.setPosition(32+16, 0);
		
		//selectSprite = atlas.createSprite("select");
		//selectSprite.setSize(Punk.MAPTILESIZE*2, Punk.MAPTILESIZE*2);
		
		//ammoSelectSprite.setColor(0,1,0,1);
		//ZHurtTex = new Texture(Gdx.files.internal("data/zombiehurt"+Punk.TILESIZE+".png"));
		
		//SHurtTex = new Texture(Gdx.files.internal("data/spiderhurt"+Punk.TILESIZE+".png"));
		//SHurt = atlas.createSprite("spiderhurt"+Punk.TILESIZE);
		//PHurtTex = new Texture(Gdx.files.internal("data/pighurt"+Punk.TILESIZE+".png"));
		//PHurt = atlas.createSprite("pighurt");
		//PHurt.setSize(2,2);
		//PFlyTex = new Texture(Gdx.files.internal("data/pigfly"+Punk.TILESIZE+".png"));
		//SWalkTex = new Texture(Gdx.files.internal("data/slime"+Punk.TILESIZE+".png"));
		//SWalk = new Animation(0.5f,TextureRegion.split(SWalkTex, Punk.TILESIZE, Punk.TILESIZE)[0]);
		//SpiderTex = new Texture(Gdx.files.internal("data/spider2frame"+Punk.TILESIZE+".png"));
		
		//spiderPhaseTex = new Texture(Gdx.files.internal("data/spiderphase"+Punk.TILESIZE+".png"));
		//SnakeTex = new Texture(Gdx.files.internal("data/snakeface"+Punk.TILESIZE+".png"));
		//SnakeTexUp = new Texture(Gdx.files.internal("data/snakefaceup"+Punk.TILESIZE+".png"));
		//SnakeSprite = new Sprite[4];
		//TODO snakes
		//SnakeSprite = atlas.createSprite("snakeface");
		//SnakeSprite.setOrigin(1f, 1f);
		//SnakeSprite.setSize(2,2);
		
		//cloudS[0] = atlas.createSprite("cloud0v");
		//cloudS[1] = atlas.createSprite("cloud1v");
		
		//plankSprite[0] = atlas.createSprite("plank1v");
		
		//SlimeTex = new Texture(Gdx.files.internal("data/slime4frame"+Punk.TILESIZE+".png"));
		
		//Texture fireFrames = new Texture(Gdx.files.internal("data/fireanim"+Punk.TILESIZE+".png"));
		
		fireAnim = new SpriteAnimation(0.2f, atlas.findRegion("fireanim"), 32, 32);
		fireAnim.setSize(2,2);
		//FireAnim.setSize(2,2);
		//fireAnim.setPositioin()
		
		for (int i = 0; i < SKILLTOTAL; i++){
			//if (atlas.findRegion("skill", i) == null)Gdx.app.log(TAG, "didn't find region"+i);
			skillS[i] = atlas.createSprite("skill", i);
		}
		
		//TextureRegion tmpR = atlas.findRegion("fireanim"+Punk.TILESIZE);
		
		//digAnim = new SpriteAnimation(false, .125f, tmpR.split(Punk.TILESIZE, Punk.TILESIZE)[0]);
		
		
		//bloodAnim = new SpriteAnimation(0.125f, atlas.findRegion("bloodanim")
		///	, 1, 2);
		//bloodAnim.setSize(Punk.MAPTILESIZE/2, Punk.MAPTILESIZE*2);
		//bedTex = new TextureRegion(new Texture(Gdx.files.internal("data/bed"+Punk.TILESIZE+".png")));
	
		for (int i = 0; i < 8; i++){
			prefsBtn[i] = atlas.createSprite("prefs"+i+"v");
			if (prefsBtn[i] == null)throw new GdxRuntimeException("error v button"+i);
			prefsBtnPressed[i] = atlas.createSprite("prefs"+i+"p");	
			if (prefsBtn[i] == null)throw new GdxRuntimeException("error p button"+i);
		}
		
		
		blockSelSprite = atlas.createSprite("select");
		blockSelSprite.setSize(Punk.MAPTILESIZE*2, Punk.MAPTILESIZE*2);
		crossSelSprite = atlas.createSprite("selectcross");
		crossSelSprite.setSize(Punk.MAPTILESIZE*2, Punk.MAPTILESIZE*2);
		invBtn = atlas.createSprite("invbutton");
		invBtn.setSize(Punk.MAPTILESIZE*2, Punk.MAPTILESIZE*2);
		invBtn.setPosition( 0, Punk.RESY-Punk.MAPTILESIZE*2);
		craftBtn = atlas.createSprite("craftbutton");
		craftBtn.setSize(Punk.MAPTILESIZE*2, Punk.MAPTILESIZE*2);
		//perkBtn = atlas.createSprite("charsheetbutton");
		//perkBtn.setSize(Punk.MAPTILESIZE*2, Punk.MAPTILESIZE*2);
		
		helpBtn = atlas.createSprite("helpbutton");
		helpBtn.setSize(Punk.MAPTILESIZE*2, Punk.MAPTILESIZE*2);
		
		/*for (int i = 0; i < 6; i++){
			helpScreens[i] = new Sprite(new Texture(Gdx.files.internal("data/help"+i+"v.png")));
			helpScreens[i].setSize(256, 128);
			helpScreens[i].setPosition(Punk.RESX/2-128, Punk.RESY-132);
		}*/
		
		beltHideButton =  atlas.createSprite("belthide");
		beltHideButton.setSize(Punk.MAPTILESIZE*2, Punk.MAPTILESIZE);
		
		beltShowButton =  atlas.createSprite("belthide");
		beltShowButton.setSize(Punk.MAPTILESIZE*2, Punk.MAPTILESIZE);
		beltShowButton.flip(false, true);;
		
		beltHideButtonBottom = new Sprite(beltHideButton);
		beltHideButtonBottom.setSize(Punk.MAPTILESIZE*2, Punk.MAPTILESIZE);
		beltHideButtonBottom.flip(false, true);
		
		//public Animation charJumpAnimR;
		
		//TextureRegion charTexReg;
		//for(int s = 0; s < 4; s++) {
		//charTexReg = ;//, 0, Punk.TILESIZE, Punk.TILESIZE*2);
//		for (int cl = 0; cl < 2; cl++){//player class
//			//for (int s = 0; s < DARKFRAMES; s++){
//				charWalkAnim[cl] = new CorneredSpriteAnimation(.06f, atlas.findRegion("player4frame"+cl+"v"), 32, 32);
//				//charWalkAnim[cl].setDarkness(s/(float)DARKFRAMES);
//				charWalkAnim[cl].setOrigin(1f, .1f);
//				charWalkAnim[cl].setSize(2,2);
//				
//				/*charHeadSprite[s] = atlas.createSprite("playerhead");
//				charHeadSprite[s].setColor(s/(float)DARKFRAMES, s/(float)DARKFRAMES, s/(float)DARKFRAMES, 1);
//				charHeadSprite[s].setSize(1, 1);
//				charHeadSprite[s].setOrigin(1f, .1f);
//				charHeadSprite[s].flip(true,  false);*/
//				
//				charStandSprite[cl] = atlas.createCorneredSprite("stand"+cl+"v");
//				//charStandSprite[cl][s].setColor(s/(float)DARKFRAMES, s/(float)DARKFRAMES, s/(float)DARKFRAMES, 1);
//				charStandSprite[cl].setSize(2,2);
//				charStandSprite[cl].setOrigin(1f, .1f);
//				//charStandSprite[s].flip(true,  false);
//				
//				charHandSprite[cl] = atlas.createCorneredSprite("hand"+cl+"v");
//				//charHandSprite[cl][s].setColor(s/(float)DARKFRAMES, s/(float)DARKFRAMES, s/(float)DARKFRAMES, 1);
//				charHandSprite[cl].setSize(1, 1);
//				
//				charHandSprite[cl].flip(true,  false);
//				charHandSprite[cl].setOrigin(.5f,.5f);
//				
//				charClimbAnim[cl] = new CorneredSpriteAnimation(.1f, atlas.findRegion("playerclimb"+cl+"v"), 32, 32);
//				//charClimbAnim[cl][s].setDarkness(s/(float)DARKFRAMES);
//				charClimbAnim[cl].setOrigin(1f, .1f);
//				charClimbAnim[cl].setSize(2,2);
//			//}
//		
//			
//			//for (int s = 0; s < DARKFRAMES; s++){
//				charWalkAnimR[cl] = new CorneredSpriteAnimation(.06f, atlas.findRegion("player4frame"+cl+"v"), 32,32);
//				//charWalkAnimR[cl][s].setDarkness(s/(float)DARKFRAMES);
//				
//				charWalkAnimR[cl].setSize(2,2);
//				charWalkAnimR[cl].flip(true, false);
//				charWalkAnimR[cl].setOrigin(1, .1f);
//				/*charHeadSpriteR[s] = atlas.createSprite("playerhead");
//				charHeadSpriteR[s].setColor(s/(float)DARKFRAMES, s/(float)DARKFRAMES, s/(float)DARKFRAMES, 1);
//				charHeadSpriteR[s].setSize(1, 1);
//				charHeadSpriteR[s].setOrigin(.5f, .1f);*/
//				
//				charStandSpriteR[cl] = atlas.createCorneredSprite("stand"+cl+"v");
//				//charStandSpriteR[cl][s].setColor(s/(float)DARKFRAMES, s/(float)DARKFRAMES, s/(float)DARKFRAMES, 1);
//				charStandSpriteR[cl].setSize(2, 2);
//				
//				charStandSpriteR[cl].flip(true,  false);
//				charStandSpriteR[cl].setOrigin(1,.1f);
//				
//				charHandSpriteR[cl] = atlas.createCorneredSprite("hand"+cl+"v");
//				//charHandSpriteR[cl][s].setColor(s/(float)DARKFRAMES, s/(float)DARKFRAMES, s/(float)DARKFRAMES, 1);
//				charHandSpriteR[cl].setSize(1, 1);
//				charHandSpriteR[cl].setOrigin(.5f, .5f);
//				charHandSpriteR[cl].flip(true,  false);
//			//}
//			
//			
//			
//		}
//		
		for (int s = 0; s < DARKFRAMES; s++)
		for (int t = 0; t < 4; t++){
			plankSprite[t][s] = atlas.createSprite("plank1v");
			plankSprite[t][s].setColor(s/(float)DARKFRAMES,s/(float)DARKFRAMES,s/(float)DARKFRAMES, 1);
			plankSprite[t][s].setOrigin(0, 0);
			plankSprite[t][s].setSize(1,1);
			//plankSprite[t][s].flip(true, false);
			//plankSprite[t][s] = atlas.createSprite("plank1v");
		}
		
		
		//zombie anim
		Sprite[] zomFrames = new Sprite[2];
		
		/*for (int i = 0; i < DARKFRAMES; i++){
			zomFrames[0] = atlas.createSprite("z0body0f");
			zomFrames[0].flip(true, false);
			zomFrames[1] = atlas.createSprite("z0body1f");
			zomFrames[1].flip(true, false);
			zombieWalk[0][i] = new SpriteAnimation(.25f, zomFrames);
			zombieWalk[0][i].setDarkness((float)i/(float)DARKFRAMES);
			zombieWalk[0][i].setSize(2,2);
			zombieWalk[0][i].setOrigin(.5f, 0);
		}*/
		
		

		
		
		
		/*for (int s = 0; s < DARKFRAMES; s++){
			zombieWalkAnim[1][s] = new SpriteAnimation(.25f, zomFrames, Punk.TILESIZE, Punk.TILESIZE*2);
			
			//zombieWalkAnim[1][s].setOrigin(Punk.TILESIZE2, 0);
			zombieWalkAnim[1][s].setSize(1,2);
			zombieWalkAnim[1][s].setOrigin(.5f, 0);
			zombieWalkAnim[1][s].setDarkness(s/(float)DARKFRAMES);
			//zombieWalkAnim[1][s].flip(true, false);
		}*/
		
		//zomFrames[0].flip(true, false);
		/*for (int s = 0; s < DARKFRAMES; s++){
			zombieWalkAnimR[1][s] = new SpriteAnimation(.25f, zomFrames, Punk.TILESIZE, Punk.TILESIZE*2);
			//zombieWalkAnimR[1][s].setOrigin(Punk.TILESIZE2, 0);
			zombieWalkAnimR[1][s].setSize(1,2);
			zombieWalkAnimR[1][s].setOrigin(.5f, 0);
			zombieWalkAnimR[1][s].setDarkness(s/(float)DARKFRAMES);
//zombieWalkAnimR[1][s].flip(false, false);
		}*/
		
		/*for (int i = 0; i < 4; i++){
			Sprite[]headFrames = new Sprite[3];
			headFrames[0] = atlas.createSprite("z"+(i+1)+"head0f");
			headFrames[1] = atlas.createSprite("z"+(i+1)+"head1f");
			headFrames[2] = atlas.createSprite("z"+(i+1)+"head2f");
			headFrames[0].flip(true,  false);
			headFrames[1].flip(true,  false);
			headFrames[2].flip(true,  false);
			for (int s = 0; s < DARKFRAMES; s++){
				zombieHeadAnim[i][s] = new SpriteAnimation(.025f, headFrames, Punk.TILESIZE, Punk.TILESIZE);
				zombieHeadAnim[i][s].setDarkness(s/(float)DARKFRAMES);
				zombieHeadAnim[i][s].setSize(1,1);
				zombieHeadAnim[i][s].setOrigin(.5f, 0);
				zombieHeadAnim[i][s].flip(true, false);
				
				zombieHeadSprite[i][s] = atlas.createSprite("z"+(i+1)+"head0f");
				zombieHeadSprite[i][s].setSize(1,1);
				zombieHeadSprite[i][s].setOrigin(.5f,0);
				zombieHeadSprite[i][s].flip(true, false);

			}
			
			headFrames[0] = atlas.createSprite("z"+(i+1)+"head0f");
			headFrames[1] = atlas.createSprite("z"+(i+1)+"head1f");
			headFrames[2] = atlas.createSprite("z"+(i+1)+"head2f");
			for (int s = 0; s < DARKFRAMES; s++){
				zombieHeadAnimR[i][s] = new SpriteAnimation(.025f, headFrames, Punk.TILESIZE, Punk.TILESIZE);
				zombieHeadAnimR[i][s].setDarkness(s/(float)DARKFRAMES);
				zombieHeadAnimR[i][s].setSize(1,1);
				zombieHeadAnimR[i][s].setOrigin(.5f, 0);
				
				zombieHeadSpriteR[i][s] = atlas.createSprite("z"+(i+1)+"head0f");
				zombieHeadSpriteR[i][s].setSize(1,1);
				zombieHeadSpriteR[i][s].setOrigin(.5f,0);
			}
		}
		*/
		
		for (int v = 0; v < 5; v++){
			for (int s = 0; s < DARKFRAMES; s++){
				gibSprites[v][s] = atlas.createSprite("guts"+(v+1)+"v");
				gibSprites[v][s].setSize(.5f,.5f);
				gibSprites[v][s].setOrigin(.5f,.5f);
				//gibSprites[v][s].setDarkness(s/(float)DARKFRAMES);
			}
		}
		/*
		//slimeSprite = atlas.findRegion("slime"+Punk.CHUNKSIZE);
		for (int s = 0; s < DARKFRAMES; s++){
			slimeAnim[s] = new SpriteAnimation(0.25f, atlas.findRegion("slime4frame"), Punk.TILESIZE, Punk.TILESIZE);
			slimeAnim[s].setDarkness(s/(float)DARKFRAMES);
			slimeAnim[s].setSize(1,1);
		}
		//pig
		for (int s = 0; s < DARKFRAMES; s++){
			pigFlyAnim[s] = new SpriteAnimation(0.5f, atlas.findRegion("pigfly"), 32, 32);			
			pigFlyAnim[s].setSize(2,2);
			pigFlyAnim[s].setDarkness(s/(float)DARKFRAMES);
			pigFlyAnim[s].flip(true);
			
			pigFlyAnimR[s] = new SpriteAnimation(0.5f, atlas.findRegion("pigfly"), 32, 32);			
			pigFlyAnimR[s].setSize(2,2);
			pigFlyAnimR[s].setDarkness(s/(float)DARKFRAMES);
			
//			zomFrames[0] = atlas.createSprite("z0body0f");
//			zomFrames[0].flip(true, false);
//			zomFrames[1] = atlas.createSprite("z0body1f");
//			zomFrames[1].flip(true, false);
//			
//			zombieWalk[0][s] = new SpriteAnimation(.25f, zomFrames);
//			zombieWalk[0][s].setDarkness(s/(float)DARKFRAMES);
//			zombieWalk[0][s].setSize(2,2);
//			zombieWalk[0][s].setOrigin(.5f, 0);
			
		}
		//spider
		//SpiderWalk = new Animation(0.25f,TextureRegion.split(SpiderTex, Punk.TILESIZE*2, Punk.TILESIZE)[0]);
		//spiderHurtTex = new TextureRegion(new Texture(Gdx.files.internal("data/spiderhurt"+Punk.TILESIZE+".png")));
		for (int s = 0; s < DARKFRAMES; s++){
			spiderWalkAnim[s]= new SpriteAnimation(0.5f, atlas.findRegion("spider2frame"), Punk.TILESIZE, Punk.TILESIZE);
			spiderHurtSprite[s] = atlas.createSprite("spiderhurt");//, Punk.TILESIZE, Punk.TILESIZE)[0]);
			spiderPhaseAnim[s] = new SpriteAnimation(0.5f, atlas.findRegion("spiderphase"), Punk.TILESIZE, Punk.TILESIZE);
			spiderWalkAnim[s].setDarkness(s/(float)DARKFRAMES);
			spiderHurtSprite[s].setColor(s/(float)DARKFRAMES,s/(float)DARKFRAMES,s/(float)DARKFRAMES,1);
			spiderPhaseAnim[s].setDarkness(s/(float)DARKFRAMES);
			spiderPhaseAnim[s].setSize(2,1);
			spiderWalkAnim[s].setSize(2,1);
			spiderHurtSprite[s].setSize(2,1);
		}
		//SPHASE = new Animation(.6f, TextureRegion.split(SPhaseTexture, Punk.TILESIZE*2, Punk.TILESIZE)[0]);

		*/
		
		
		//ZWalk = new Animation(0.75f, TextureRegion.split(zombieTexture, Punk.TILESIZE, Punk.TILESIZE*2)[0]);
		
		
		
			
		
		//slingTexture = new Texture(Gdx.files.internal("data/sling.png"));
		//slingSprite = new Sprite(slingTexture);
		//slingshotTexture = new Texture(Gdx.files.internal("data/slingshot.png"));
		//slingshotSprite = new Sprite(slingshotTexture);
		//gooTex = atlas.findRegion("goo"+Punk.TILESIZE);
		
	//	dwarfTex = new Texture(Gdx.files.internal("data/dwarf"+Punk.TILESIZE+".png"));
		//dwarfSprite = new Sprite(dwarfTex);
		
		/*explosionSprite1 = new Sprite(new Texture(Gdx.files.internal("data/explosion1.png")));
		whitesmokeSprite = new Sprite(new Texture(Gdx.files.internal("data/whitesmoke.png")));
		try{
			explosion1 = new ParticleEmitter(new BufferedReader(new InputStreamReader(Gdx.files.internal("data/explosion1.emm").read())));
			whiteSmoke = new ParticleEmitter(new BufferedReader(new InputStreamReader(Gdx.files.internal("data/whitesmoke.emm").read())));
		} catch (IOException ex){
			System.out.println("explosion file error");
		}
		//explosion1.setPosition(25,120);
		explosion1.setSprite(explosionSprite1);
		explosion2 = new ParticleEmitter(explosion1);
		explosion2.setPosition(100, 100);
		whiteSmoke.setSprite(whitesmokeSprite); 
*/
		//explosion1.addParticles(20);
		dotSprite = new Sprite(new Texture(Gdx.files.internal("data/dot.png")));
		
		
		//projectileAtlas = new TextureAtlas();
		//grenadeAnims
		
		//ninjas
		
		
		
		
		crossS = atlas.createCorneredSprite("t1l");
		for (int x = 0; x<256; x++)
		{
			
			//for (int s = 0; s < DARKFRAMES; s++){
				
				terrainS[x] = new Array<CorneredSprite>();
				terrainS[x].ordered = true;
				terrainItemS[x] = new Array<CorneredSprite>();
				terrainItemS[x].ordered = true;
				CorneredSprite s  =  atlas.createCorneredSprite("t"+(x)+"l");

				
				if (s == null){//try metas
					int m = 0;
					boolean done = false;
					while (!done){
						s  =  atlas.createCorneredSprite("t"+(x)+"l"+m+"m");
						
						if (s == null){
							s =  crossS;
							done = true;
						}else {
							s.setSize(1,1);
							s.setOrigin(.5f,.5f);
							terrainS[x].add(s);
							terrainItemS[x].add(new CorneredSprite(s));
						}
						m++;
					}
					if (m == 1){
						terrainS[x].add(crossS);
						terrainItemS[x].add(crossS);
					}
					
					////gdx.app.log("bodies", "null terrain!"+x);
				} else {
					s.setSize(1,1);
					s.setOrigin(.5f,.5f);
					terrainS[x].add(s);
					terrainItemS[x].add(new CorneredSprite(s));
				}
				
				if (terrainS[x].size > 0){
					Sprite sp =  new Sprite(terrainS[x].get(0));;
					sp.setU(sp.getU() + (TILERES/2-1)/1024f);
					sp.setV(sp.getV() + (TILERES/2-1)/1024f);
					sp.setU2(sp.getU2() - (TILERES/2+1)/1024f);
					sp.setV2(sp.getV2() - (TILERES/2+1)/1024f);
					destructionParticles[x] = sp;
					
				
				
				}
			
		}
		
		//items
		for (int x = 0; x<256; x++)
		{
			
				itemS[x] = new Array<CorneredSprite>();
				itemS[x].ordered = true;
				CorneredSprite s  =  atlas.createCorneredSprite("i"+(x)+"m");
				
				Array<AtlasRegion> regions = atlas.findRegions("i"+x+"m");
				for (int i = 0, c = regions.size; i < c; i++){
					CorneredSprite cs = new CorneredSprite(regions.get(i), 0, 0);
					cs.setSize(1f, 1f);
					cs.setOrigin(.5f, .5f);
					itemS[x].add(cs);
				}
				if (regions.size == 0)
					itemS[x].add(crossS);
				//Gdx.app.log(TAG, "item id "+x+" metas:"+regions.size);
				
				/*if (s == null){//try metas
					int m = 0;
					boolean done = false;
					while (!done){
						s  =  atlas.createCorneredSprite("i"+(x)+"m"+m+"m");
						
						if (s == null){
							s =  crossS;
							done = true;
						}else {
							s.setSize(1,1);
							s.setOrigin(.5f,.5f);
							itemS[x].add(s);
						}
						m++;
					}
					if (m == 1)
						itemS[x].add(crossS);
					
					////gdx.app.log("bodies", "null terrain!"+x);
				} else {
					s.setSize(1,1);
					s.setOrigin(.5f,.5f);
					itemS[x].add(s);
				}*/
				
			
			
		}
		
		
		
		//}
		//Air blocks
		for (int s = 0; s < DARKFRAMES; s++){
			//terrainS[0][s] = new Sprite(terrainS[217][0]);
			//terrainS[0][s].setColor(0,0,0,0);
			
			lightToFloat[s] = s/(float)DARKFRAMES;
		}
		
		
		/*for (int i = 0; i < 4; i++){
			axeS[i] = atlas.createSprite("axe"+i+"v");
			axeS[i].setSize(4,1);
			axeS[i].setOrigin(0, 0);
			axeS[i].setScale(1,-1);
			axeSL[i] = atlas.createSprite("axe"+i+"v");
			axeSL[i].setSize(4,1);
			axeSL[i].setOrigin(0,0);
			axeSL[i].setScale(1,1);
		}*/
		
		/*for (int i = 0; i < FLAILCOUNT; i++){
			flailS[i] = atlas.createSprite("flail"+i+"v");
			flailS[i].setSize(1,1);
			flailS[i].setOrigin(0.5f, 0.5f);
			//flailS[i].setScale(1,-1);
			
		}*/
		
		//chop up tiles
		
		/*for (int i = 0; i < DOORTILES; i++){			
			CorneredSprite s = atlas.createCorneredSprite("doors");			
			s.setRegion(s.getOriginX()+16*(i % 16), s.getOriginY()+16*(i/16), 16, 16);			
			doorS[i] = s;
		}*/
		
		/*for (int i = 0; i < WALLTILES; i++){			
			CorneredSprite s = atlas.createCorneredSprite("walls"+i+"v");			
			wallS[i] = s;
		}*/
		
		/*for (int i = 0; i < DUNGEONTILES; i++){			
			CorneredSprite s = atlas.createCorneredSprite("dungeon"+i+"f");			
			
		}
		
		for (int i = 0; i < TOWNTILES; i++){			
			CorneredSprite s = atlas.createCorneredSprite("town");			
			s.setRegion(s.getOriginX()+16*(i % 16), s.getOriginY()+511-16*((i+1)/16), 16, 16);			
			townS[i] = s;
		}*/
		
		/*
		for (int i = 0; i < 16; i++){
			mushrooms[i] = atlas.createCorneredSprite("mushroom"+i+"v");
			if (mushrooms[i] == null) mushrooms[i] = crossS;
			mushrooms[i].setSize(1,1);
			mushrooms[i].setOrigin(.5f,.5f);
			
			
		}
		for (int i = 0; i < 20; i++){
			flowers[i] = atlas.createCorneredSprite("flower"+i+"v");
			if (flowers[i] == null) flowers[i] = crossS;
			flowers[i].setSize(1,1);
			flowers[i].setOrigin(.5f,.5f);
		}
		*/
		
	}
	
	public Sprite[] destructionParticles = new Sprite[256];
	long[][] explosionVib = new long[3][100];
	public void initSounds(){
		//public Sound tommySnd, launcherSnd1, launcherSnd2, launcherSnd3, digSnd, pigSnd, 
		//zombieSnd1, zombieSnd2, explosionSnd1, explosionSnd2, explosionSnd3, 
		//pigSnd1, pigSnd2, hurtSnd, hurtSnd2, throwSnd;
		digSnd = Gdx.audio.newSound(Gdx.files.internal("data/dig.ogg"));
		throwSnd = Gdx.audio.newSound(Gdx.files.internal("data/throw.ogg"));
		tommySnd = Gdx.audio.newSound(Gdx.files.internal("data/gun6.ogg"));
		healSnd1 = Gdx.audio.newSound(Gdx.files.internal("data/heal1.ogg"));
		healSnd2 = Gdx.audio.newSound(Gdx.files.internal("data/heal2.ogg"));
		explosionSnd1= Gdx.audio.newSound(Gdx.files.internal("data/explosion1.ogg"));
		explosionSnd2= Gdx.audio.newSound(Gdx.files.internal("data/explosion2.ogg"));
		explosionSnd3= Gdx.audio.newSound(Gdx.files.internal("data/explosion3.ogg"));
		//pigSnd1 = Gdx.audio.newSound(Gdx.files.internal("data/oink1.ogg"));
		//pigSnd2= Gdx.audio.newSound(Gdx.files.internal("data/oink2.ogg"));
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 100; j++)
				explosionVib[i][j] = MathUtils.random(1, 20);
		zombieSnd = Gdx.audio.newSound(Gdx.files.internal("data/zombie.ogg"));
		flailSnd = Gdx.audio.newSound(Gdx.files.internal("data/flail.ogg"));
		jumpSnd= Gdx.audio.newSound(Gdx.files.internal("data/jump.ogg"));
		nightfallSnd = Gdx.audio.newSound(Gdx.files.internal("data/nightfall.ogg"));
		sunriseSnd = Gdx.audio.newSound(Gdx.files.internal("data/sunrise.ogg"));
		itemSnd = Gdx.audio.newSound(Gdx.files.internal("data/powerup.ogg"));
		slimeSnd = Gdx.audio.newSound(Gdx.files.internal("data/slime.ogg"));
		spiderAttackSnd = Gdx.audio.newSound(Gdx.files.internal("data/spiderattack.ogg"));
		spiderHurtSnd = Gdx.audio.newSound(Gdx.files.internal("data/spiderhurt.ogg"));
		pigSnd = Gdx.audio.newSound(Gdx.files.internal("data/pig.ogg"));

		spellSnd0 = Gdx.audio.newSound(Gdx.files.internal("data/spell0.ogg"));
		
	//	launcherSnd1 = Gdx.audio.newSound(Gdx.files.internal("data/launcher1.ogg"));
		//launcherSnd2 = Gdx.audio.newSound(Gdx.files.internal("data/launcher2.ogg"));
		//launcherSnd3 = Gdx.audio.newSound(Gdx.files.internal("data/launcher3.ogg"));
		hurtSndS = Gdx.audio.newSound(Gdx.files.internal("data/hurts.ogg"));
		hurtSndM = Gdx.audio.newSound(Gdx.files.internal("data/hurtm.ogg"));
		hurtSndL = Gdx.audio.newSound(Gdx.files.internal("data/hurtl.ogg"));
		hurtSndP = Gdx.audio.newSound(Gdx.files.internal("data/hurtp.ogg"));
		tripSnd = Gdx.audio.newSound(Gdx.files.internal("data/trip.ogg"));
		music = Gdx.audio.newMusic(Gdx.files.internal("data/music.ogg"));
		menuMusic = Gdx.audio.newMusic(Gdx.files.internal("data/menumusic.ogg"));
		music.setLooping(true);
		menuMusic.setLooping(true);
		failedSnd = Gdx.audio.newSound(Gdx.files.internal("data/fail.ogg"));
		breakSnd1 = Gdx.audio.newSound(Gdx.files.internal("data/break1.ogg"));
		breakSnd2 = Gdx.audio.newSound(Gdx.files.internal("data/break2.ogg"));
		breakSnd3 = Gdx.audio.newSound(Gdx.files.internal("data/break3.ogg"));
		//dayMusicSlow.setLooping(false);
		//dayMusicSlow.setVolume(1);
		
		//currentMusicLoop.play();
		//dayMusic.play();
		
		//volume = 0;
	}
	
	
	
	public static final float SQRT2 = (float)Math.sqrt(2);
	public FixtureDef getPlankFixtureDef(float length, float width, boolean angled){
		float PLANKOFFSET = width/2f;
		float tiny = 0f;
		if (angled){
			width /= SQRT2;
			verts6[0].set(0,0);
			verts6[1].set(width,0);
			verts6[2].set(length-tiny, length-width);
			verts6[3].set(length-tiny,length);
			verts6[4].set(length-tiny-width, length);
			verts6[5].set(0,width);
			//rotate
			for (int i = 0; i < 6; i++)
				verts6[i].add(-PLANKOFFSET,-PLANKOFFSET);
			for (int i = 0; i < 6; i++){
				verts6[i].rotate(-45);
			}
			for (int i = 0; i < 6; i++)
				
			//	//gdx.app.log("bodies", "verts "+i+":"+verts6[i]);
			bodyShapes[47].set(verts6);
		}
		else {
			
			verts[0].set(0,0);
			verts[1].set(0,-width);
			verts[2].set(length-tiny,-width);
			verts[3].set(length-tiny,0);
			for (int i = 0; i < 4; i++)
				verts[i].add(-PLANKOFFSET,PLANKOFFSET);
			//for (int i = 0; i < 4; i++)
			//	//gdx.app.log("bodies", "verts "+i+":"+verts[i]);
			bodyShapes[47].set(verts);
			
		}
		return fixtures[47];
	}
	
	
	public BodyDef getBodyDef(int index, Vector2 spawnPos){
		//bodyDefs[index].position.set((int)(Math.random()*40+120),(int)(Math.random()*50)+140);
		
		bodyDefs[index].position.set(spawnPos);
		return bodyDefs[index];
	}
	public PolygonShape getBodyShape(int index){
		return bodyShapes[index];
	}
	public FixtureDef getFixture(int index){
		
		return fixtures[index];
	}
	public static CorneredSprite getItemFrame(int itemID, int meta){
		
		if (itemID > 255)
		return itemS[itemID-256].get(Math.min(meta<0?itemS[itemID-256].size-meta:meta, itemS[itemID-256].size-1));
;
		if (itemID>=0) return getBlockItemSprites(itemID, meta);
		return terrainItemS[0].get(0);
	}
	public TextureRegion getAirFrame(int met){
		
		return terrainS[0].get(0);
	}
	
	/*public Sprite getSpecialBlockTex(int blockID, int meta, byte light){
		//////gdx.app.log("special fram lookup:","ID:"+blockID);
		switch (blockID){
		//case 0: return getAirFrame()
			//break;
		case -1://water
				////gdx.app.log("bodies", "light:"+light);
				//return terrainS[Math.abs(meta)+218][light];
				if (meta == 0) terrainS[218][light].setSize(1,1);
				else terrainS[218][light].setSize(1f, 1f/(Math.abs(meta)));
				return terrainS[218][light];
				
		case -2://fire
				return terrainS[226+meta%6][14];
		case -3:
				return terrainS[245+Math.abs(meta)][light];
		
		case -4:
		case -5:
		case -6:
		case -7:
		case -8:
			return terrainS[1][light];
		case -9:
			////gdx.app.log("bodies","drawing -9");
			//return terrainS[1][16];
			
			switch (meta){
			case 0:terrainS[219][light].setScale(-1,1);
					return terrainS[219][light];
			case 1:terrainS[219][light].setScale(1,1);
					return terrainS[219][light];
			case 2: terrainS[220][light].setScale(1,1);
					return terrainS[220][light];
			case 3: terrainS[220][light].setScale(-1,1);
					return terrainS[220][light];
			case 4:terrainS[220][light].setScale(1,-1);
					return terrainS[220][light];
			case 5: terrainS[220][light].setScale(-1,-1);
					return terrainS[220][light];
				}////////
		}
		return terrainS[10][16];
	}
	*/
	
	
	public static CorneredSprite getBlockSprites(int blockID, int meta){
		//////gdx.app.log("special fram lookup:","ID:"+blockID);
		switch (blockID){
		case 43:return terrainS[blockID].get(meta<0?0:Math.min(meta, terrainS[blockID].size-1));
		
		
		default: return terrainS[blockID].get(meta<0?(terrainS[blockID].size-meta)% terrainS[blockID].size:meta% terrainS[blockID].size);
	
		case -4:
		case -5:
		case -6:
		case -7:
		case -8:
			return terrainS[2].get(0);
		case -9:
			
		case -10:
		case -11:
		case -12: 
			break;
		}
		return terrainS[10].get(0);
	}
	
	
	public static CorneredSprite getBlockItemSprites(int blockID, int meta){
		//////gdx.app.log("special fram lookup:","ID:"+blockID);
		switch (blockID){
		default: return terrainItemS[blockID].get(meta<0?(terrainItemS[blockID].size-meta)% terrainItemS[blockID].size:meta% terrainItemS[blockID].size);
	
		case -4:
		case -5:
		case -6:
		case -7:
		case -8:
			return terrainItemS[2].get(0);
		case -9:
			
		case -10:
		case -11:
		case -12: 
			break;
		}
		return terrainItemS[10].get(0);
	}
	

	/*private static CorneredSprite leafBM(int type, int meta) {
		int newMeta;
		meta += 128;
		//meta |= (meta>>4);
	//Gdx.app.log("bodies", "leaf meta:"+meta+"filtered:"+(meta&15));
		if (type == 0){
			int treeM = meta>>4;
			//if (treeM == 2 || treeM == 4 || treeM == 8)
				meta |= treeM;
				
			
			switch ((meta) & 15){
			default:
			case 0:newMeta = 15;break;
			case 5:newMeta = 13;break;
			case 7:newMeta = 9;break;
			case 10:newMeta = 14;break;
			case 11:newMeta = 12;break;
			case 13:newMeta = 10;break;
			case 14:newMeta = 11;break;
			case 15:newMeta = 0;break;
			
			case 1:newMeta = 1;break;
			case 3:newMeta = 5;break;
			
			case 6:newMeta = 6;break;
			case 9:newMeta = 7;break;
			case 12:newMeta = 8;break;
			
			
			case 2:
				
				newMeta = 2;break;
			case 4:
				
				newMeta = 3;break;
			case 8:
				
				newMeta =4;break;

			
			}
			return leaf0[newMeta];
		}
		if (type == 1){
			switch (meta & 15){
			default:
			case 0:case 5:case 7:case 10:case 11:case 13:case 14:case 15:newMeta = 0;
			break;
			case 1:newMeta = 1;break;
			case 2:newMeta = 2;break;
			case 3:newMeta = 5;break;
			case 4:newMeta = 3;break;
			case 6:newMeta = 6;break;
			case 8:newMeta =4;break;
			case 9:newMeta = 7;break;
			case 12:newMeta = 8;break;
			}
			return terrainS[141].get(newMeta);
			
		} else if (type == 2){//pine
			int treeM = meta>>4;
			//if (treeM == 2 || treeM == 4 || treeM == 8)
			meta |= treeM;
			switch (meta & 15){
			default:
			case 0:case 5:case 13:case 15:
			case 7:
				newMeta = 0;break;
			
			case 1:newMeta = 1;break;
			case 2:newMeta = 2;break;
			case 3:newMeta = 5;break;
			case 14:case 4:newMeta = 3;break;
			case 6:newMeta = 6;break;
			case 8:newMeta =4;break;
			case 9:newMeta = 7;break;
			case 12:newMeta = 8;break;
			case 10:newMeta = 1;break;
			case 11:
				//if (){//tree under
				//newMeta = 0;break;	
				//}
				newMeta = 1;break;
			}
		return terrainS[150].get(newMeta);
		} else {//if (type == 3){
			switch (meta & 15){
			default:
			case 0:case 5:case 7:case 10:case 11:case 13:case 14:case 15:newMeta = 0;
			break;
			case 1:newMeta = 1;break;
			case 2:newMeta = 2;break;
			case 3:newMeta = 5;break;
			case 4:newMeta = 3;break;
			case 6:newMeta = 6;break;
			case 8:newMeta =4;break;
			case 9:newMeta = 7;break;
			case 12:newMeta = 8;break;
			}
			return terrainS[141+newMeta];
			
		}
	}

	private static CorneredSprite treeBM(int type, int meta) {//in bitmask
		////gdx.app.log("bodies", "bitmask"+meta);
		switch ((meta+128)&15){
		case 0:return tree0[0];
		case 1:return tree0[0];//1
		case 2:return tree0[6];//9
		case 3:return tree0[11];
		case 4:return tree0[2];//1
		case 5:return tree0[1];
		case 6:return tree0[13];
		case 7:return tree0[4];
		case 8:return tree0[7];//9
		case 9:return tree0[10];
		case 10:return tree0[9];
		case 11:return tree0[14];
		case 12:return tree0[12];
		case 13:return tree0[3];
		case 14:return tree0[15];
		case 15:return tree0[5];
		
		}
		return terrainS[159+meta];
	}
*/
	public Sprite getFireFrame(float stT){
		return fireAnim.getKeyFrame(stT, true);
	}
	public long[] explosionVib1 = {0,100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100};
	public FlightInfo[] playerFlights = new FlightInfo[10];
	
	
	
	
	
	public void playExplosionSound(ComponentExplosion info){
		
		/*switch (info){
		case GRENADE:
		case COPPER_GRENADE:
			explosionSnd2.play();
			Gdx.app.getInput().vibrate(explosionVib[MathUtils.random(0,2)], -1);
			break;
		case GOLD_GRENADE:
			explosionSnd1.play();
			Gdx.app.getInput().vibrate(explosionVib[MathUtils.random(0,2)], -1);
			break;
		case MITHRIL_GRENADE:
			explosionSnd3.play();
			break;
		}*/
		
		
	}
	public void playTommySound(int gun){
		tommySnd.play(volume);
	}
	public void playLauncherSound(int gun){
		switch (gun){
		case 320:launcherSnd1.play(volume);
		break;
		case 321: launcherSnd2.play(volume);
		break;
		case 322: launcherSnd3.play(volume);
		break;
		}
	}
	public static void playHealSound (int i){
		if (i >0) healSnd1.play(volume);
		//else healSnd2.play(volume);
	}
	
	public void playMonsterHurtSound(int id, int dType){
		switch (id){
		case 2: zombieSnd.play(volume);
		break;
		case 3: hurtSndS.play(volume);
		break;
		case 10: spiderHurtSnd.play(volume);
		break;
		case 11: slimeSnd.play(volume);
		break;
		case 38:// ninjaSnd.play(volume);
		break;
		}
	}
	public void playDigSound(int iID){
		digSnd.play(volume);
	}
	public void playThrowSound(){
		throwSnd.play(volume);
		////gdx.app.log("bodies", "volume:"+volume);
	}
	public void playDieSound(){
		tripSnd.play(volume);
	}
	public void playFlailSound(){
		flailSnd.play(volume);
	}
	public void playJumpSound(){
		jumpSnd.play(volume);
		////gdx.app.log("bodies", "volume:"+volume);
	}
	
	public void playTripSound(){
		tripSnd.play(volume);
	}

	
	public void playExplosionSound(GrenadeInfo info) {
		// TODO Auto-generated method stub
		info.explosion.sound.play(volume);
		Gdx.app.getInput().vibrate(explosionVib[MathUtils.random(0,2)], -1);
	}

	public static CorneredSprite getGrenadeFrame(int i) {
		//if (itemS[103+i].size < 1)
		return itemS[103].get(0);
		
		
	}
	public void playFailedSound() {
		// TODO Auto-generated method stub
		
	}

	public void playPlayerHurtSound() {
		hurtSndP.play(volume);
	}

	public static void playItemSound() {
		itemSnd.play(volume);
		
	}
	public static void playBlockBreakSound(int blockID){
		//gdx.app.log("bodies", "break sound id:"+blockID);
		switch (blockID){
		default: breakSnd1.play(volume);
		break;
		case 21: case 20: case 2: case 3:
		case 13:case 14:case 18:case 55://leaves
			breakSnd2.play(volume);
			break;
		case 8:case 15:case 16:case 17:case 54://wood
		case 37:case 19:case 50:case 4:case 5:case 6:case 53:
			breakSnd3.play(volume);
			break;
		}
	}

	public void playSpiderAttackSound() {
		spiderAttackSnd.play(volume);
		
	}
	
	public void playSpellSound(GrenadeInfo info){
		spellSnd0.play(volume);
	}

	public Sprite getPerkFrame(int id, int subType) {
		return perkS[subType][id];
	
	}



	public static TouchAction getItemData(int id, int meta) {
		return getItemInfo(id, meta).data;
	}

	
	
	private IntMap<Sprite> wayS = new IntMap<Sprite>();
	public CorneredSprite flyArrowS;
	
	public static Sprite getWaypointSprite(int x, int y, int id, int plane) {
		//Sprite s = atlas.createSprite("waypt"+id+"f");
		Sprite s = atlas.createSprite("surfacewaypt"+id+"f");
		if (s == null){
			Gdx.app.log("bodieees", "waypt not added"+x+","+y+" id:"+id);
			return getBlockSprites(id, 0);
		}
		s.setSize(2, 2);
		s.setPosition(x, y);
		s.setOrigin(1,1);
		
		//Gdx.app.log("bodieees", "waypt added"+x+","+y+" id:"+id);
		return s;//wayS.get(id+(plane<<8));
	
		
	}


	private static ItemDef defaultThrownItem = new ItemDef(1);
	public static ItemDef getItemInfo(int id, int meta) {
		if (id < 0) return useableSkills[-id];
		if (info[id].size == 0) {//return a default thrown object
			defaultThrownGrenade.s = getItemFrame(id, meta);
			defaultThrownGrenade.id = id;
			defaultThrownGrenade.meta = meta;
			defaultThrownItem.data = defaultThrownGrenade;
			defaultThrownItem.amount = 20;
			
			
			//defaultThrownGrenade.
			defaultThrownItem.gameMode =0;
			return defaultThrownItem;
		}
		return info[id].get(Math.min(meta<0?info[id].size-meta:meta, info[id].size-1));

	}


	private Array<MobInfo> nightMobs, dayMobs, sunsetMobs, sunriseMobs;
	public Sprite solidLine;
	
	public MobInfo getRandomMobInfo() {
		switch (PunkMap.timeOfDay){
		case DAY: return dayMobs.get(MathUtils.random(dayMobs.size));
		
		case NIGHT: return nightMobs.get(MathUtils.random(nightMobs.size));
			
			
		case SUNSET: return sunsetMobs.get(MathUtils.random(sunsetMobs.size));
			
		
		case SUNRISE: return sunriseMobs.get(MathUtils.random(sunriseMobs.size));
			
		}
		return null;
	}







	public static CorneredSprite getBlockBGSprites(byte bg) {
		// TODO Auto-generated method stub
		return getBlockSprites(11,0);
	}







	public void start(TextureAtlas... atlass) {

		nameGen = new NameGenerator();
		prefs = Gdx.app.getPreferences("MMprefs");
		soundEnabled = prefs.getBoolean("soundOn");
		MAPTILESIZE = Punk.TILESIZE;
		ITEMTILESIZE = Punk.TILESIZE;
		//System.out.println("tilesiez:" + MAPTILESIZE);
		
		initSprites(atlass);
		CorneredSprite.makeLookup();
		initSounds();
	
		//uiLineV.rotate90(true);
		/*aimerTex = atlas.findRegion("aimer");
		for (int x = 0; x < 12; x++)
		{
			aimer[x] = new Sprite(aimerTex, 0, 0, (x*Punk.TILESIZE)/3, Punk.TILESIZE);
			//aimer[x].setRegion(0,0,x/3f*Punk.TILESIZE, Punk.TILESIZE);
			aimer[x].setSize(x/3f, 1);
			aimer[x].setOrigin(0, .5f);
		}*/
		
		/*terrain = new Texture(Gdx.files.internal("data/terrain"+MAPTILESIZE+".png" ));
		terrain.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		terrain.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		tmpPixmap = new Pixmap(Punk.TILESIZE+2, Punk.TILESIZE+2, Pixmap.Format.RGBA4444);
		*/
	
		
		//itemTexture = new Texture(Gdx.files.internal("data/items"+ITEMTILESIZE+".png"));
		
		
		verts6[0] = new Vector2(0,0);
		verts6[1] = new Vector2(0,0);
		verts6[2] = new Vector2(0,0);
		verts6[3] = new Vector2(0,0);
		verts6[4] = new Vector2(0,0);
		verts6[5] = new Vector2(0,0);
	
		
		//SnakeSprite[1].flip(true,false);
		
		for (int i =0; i<BODYCOUNT; i++)
		{
			
			bodyShapes[i] = new PolygonShape();
			bodyDefs[i] = new BodyDef();
			bodyShapes[i].setAsBox(1,1,playerCenter, 0);
			fixtures[i] = new FixtureDef();
			
		}		
		medCircle = new CircleShape();
		smallCircle = new CircleShape();
		medCircle.setRadius(.4f);
		smallCircle.setRadius(.25f);
		
		
		//these go by actor ids
		//System.out.println("PunkBodies: generating shapes and bodyDefs");
		bodyShapes[0].setAsBox(.3f,.8f, playerCenter, 0);
		
		bodyShapes[15].setAsBox(.3f,.3f);
		bodyShapes[18].setAsBox(1f,1f);
		bodyShapes[30].setAsBox(1,1);
		bodyShapes[1].setAsBox(.5f,.5f);
		bodyShapes[23].setAsBox(.45f,.45f);
		bodyShapes[2].setAsBox(.3f,.7f, playerCenter, 0);
		bodyShapes[38].setAsBox(.3f,.7f, playerCenter, 0);
		bodyShapes[3].setAsBox(.3f,.3f);
		bodyShapes[4].setAsBox(.5f,.5f, playerCenter, 0);
//		bodyShapes[13].setAsBox(.5f,.5f, playerCenter, 0);
//		bodyShapes[14].setAsBox(.5f,.5f, playerCenter, 0);
		bodyShapes[5].setAsBox(.2f,.2f, playerCenter, 0);
		bodyShapes[22].setAsBox(.2f,.2f, playerCenter, 0);
		bodyShapes[6].setAsBox(.002f,.002f, playerCenter, 0);
		
		bodyShapes[8].setAsBox(.49f,.27f, playerCenter, 0);
		bodyShapes[9].setAsBox(.6f,.5f, playerCenter, 0);
		bodyShapes[10].setAsBox(.4f,.4f, playerCenter, 0);
		bodyShapes[11].setAsBox(.45f,.45f, playerCenter.set(0f,0f), 0);
		bodyShapes[7].setAsBox(.45f,.45f, playerCenter, 0);
		bodyShapes[16].setAsBox(.3f,.8f, playerCenter.set(0,0.5f), 0);
		bodyShapes[19].setAsBox(.3f,.8f, playerCenter.set(0,0.5f), 0);
		bodyShapes[20].setAsBox(.3f,.8f, playerCenter.set(0,0.5f), 0);
		bodyShapes[40].setAsBox(1f, .5f);
		bodyShapes[48].setAsBox(.5f, .5f);
		bodyShapes[49].setAsBox(.5f, .5f);
		bodyShapes[48].setAsBox(.5f, .5f);
		bodyShapes[57].setAsBox(.505f, .505f);
		
		//bodyShapes[27].setAsBox(.5f,.5f);
		
		verts[0] = new Vector2(.5f,.99f);
		verts[1] = new Vector2(-.5f,.99f);
		verts[2] = new Vector2(-.5f,-1);
		verts[3] = new Vector2(.5f,-1);
		//verts[4] = new Vector2(-0.5f,-0.5f);
		bodyShapes[13].set(verts);
		//bodyShapes[35].set(verts);
		verts[0] = new Vector2(.5f,.99f);
		verts[1] = new Vector2(-.5f,.99f);
		verts[2] = new Vector2(-.5f,0);
		verts[3] = new Vector2(.5f,0);
		bodyShapes[14].set(verts);
		
		verts[0] = new Vector2(.5f,1.000002f);
		verts[1] = new Vector2(-.5f,1.000002f);
		verts[2] = new Vector2(-.5f,0f);
		verts[3] = new Vector2(.5f,0f);
		bodyShapes[4].set(verts);
		bodyShapes[9].set(verts);
		bodyShapes[27].set(verts);
		bodyShapes[42].set(verts);
		
		verts[0] = new Vector2(.3f,.6f);
		verts[1] = new Vector2(-.3f,.6f);
		verts[2] = new Vector2(-.3f,0f);
		verts[3] = new Vector2(.3f,0f);
		bodyShapes[3].set(verts);
		
		verts5[0] = new Vector2(.5f,0.9f);
		verts5[1] = new Vector2(0,.95f);
		verts5[2] = new Vector2(-.5f,0.9f);
		verts5[3] = new Vector2(-.5f,0f);
		verts5[4] = new Vector2(.5f,0f);
		bodyShapes[17].set(verts5);
		//bb to side of npcs
		
		verts5[0] = new Vector2(.33f,.23f);
		verts5[1] = new Vector2(.33f,1.5f);
		verts5[2] = new Vector2(-.33f,1.5f);
		verts5[3] = new Vector2(-.33f,.23f);
		//verts7[4] = new Vector2(-.1,-.1f);
		verts5[4] = new Vector2(0,.0f);
		bodyShapes[19].set(verts5);
		//TODO
		
		verts[0].set(.45f,.5f);
		verts[1].set(-.45f,.5f);
		verts[2].set(-.45f,.0f);
		verts[3].set(.45f, .0f);
		
		bodyShapes[45].set(verts);//feet
		
		verts[0] = new Vector2(.4f,1.1f);
		verts[1] = new Vector2(-.4f,1.1f);
		verts[2] = new Vector2(-.25f,0f);
		verts[3] = new Vector2(.25f,0f);
		//bodyShapes[16].set(verts);
		
		verts[0] = new Vector2(.25f,2f);
		verts[1] = new Vector2(-.25f,2f);
		verts[2] = new Vector2(-.25f,0f);
		verts[3] = new Vector2(.25f,0f);
		bodyShapes[29].set(verts);

		verts[0] = new Vector2(.5f,.5f);
		verts[1] = new Vector2(-2.5f,.5f);
		verts[2] = new Vector2(-2.5f,-.5f);
		verts[3] = new Vector2(.5f,-.5f);
		bodyShapes[31].set(verts);
		
		verts5[0].set(.3f, 1f);
		verts5[1].set(-.3f, 1f);
		verts5[2].set(-.3f, -.495f);
		verts5[3].set(0f, -.5f);
		verts5[4].set(.3f, -.495f);
		bodyShapes[2].set(verts5);
		
		//Vector2[] v7 = new Vector2[7];
		//8 verts for axe head, 7 for shaft
		verts[0] = new Vector2(1,.0f);
		verts[1] = new Vector2(4,0);
		verts[2] = new Vector2(4,.05f);
		verts[3] = new Vector2(1,.05f);
		bodyShapes[43].set(verts);
				
		//Vector2[] v8 = new Vector2[8];

		verts[0] = new Vector2(2,.5f);
		verts[1] = new Vector2(4,.5f);
		verts[2] = new Vector2(3.5f,1);
		verts[3] = new Vector2(2.5f,1);
		axeHead = new PolygonShape();
		axeHead.set(verts);//head
		
		verts[0].set(2,-.5f);
		verts[1].set(2.5f,-1);
		verts[2].set(3.5f,-1);
		verts[3].set(4,-.5f);
		axeHeadL = new PolygonShape();
		axeHeadL.set(verts);
		
		
		
		verts[0].set(1.8f,1);
		verts[1].set(-.8f,1);
		verts[2].set(-.8f,0);
		verts[3].set(1.8f,0);
		bodyShapes[48].set(verts);//plank bb
		
		for (int i =0; i<BODYCOUNT; i++)
			{
				bodyDefs[i] = new BodyDef();
				bodyDefs[i].position.x = 0;
				bodyDefs[i].position.y = 0;
				bodyDefs[i].fixedRotation = true;
				fixtures[i].shape = bodyShapes[i];
				fixtures[i].restitution = 0;
				fixtures[i].density = 50;
				fixtures[i].shape = bodyShapes[i];
				
			}
		
		//fixtures[0].friction = .2f;
		//fixtures[0].density = 50;
		
		fixtures[4].restitution = 0.000f;
		fixtures[13].restitution = .0f;
		fixtures[14].restitution = .0f;
		
		circleShape = new CircleShape();
		circleShape.setRadius(0.5f);
		//fixtures[14].shape = BBShape;
		//fixtures[13].shape = BBShape;
		
		//bodyDefs[0].type = BodyType.DynamicBody;
	

		bodyDefs[1].type = BodyType.DynamicBody;
		bodyDefs[2].type = BodyType.DynamicBody;
		fixtures[2].filter.categoryBits = 8;
		fixtures[2].filter.maskBits = 21+8;
		fixtures[2].restitution = 0;
		fixtures[2].friction = 0f;
		bodyDefs[2].linearDamping = .0f;
		bodyDefs[3].type = BodyType.DynamicBody;
		
		//3 pig
		fixtures[3].filter.categoryBits = 0x0008;
		fixtures[3].filter.maskBits = 27+4;
		fixtures[3].friction = .1f;
		fixtures[3].restitution = 000;
		fixtures[3].density = 150f;
		
		
		
		bodyDefs[9].type = BodyType.KinematicBody;
		bodyDefs[17].type = BodyType.KinematicBody;
		fixtures[9].restitution = 0f;
		fixtures[17].restitution = .1f;
		fixtures[9].friction = .1f;
		fixtures[17].friction = .00000000f;
		fixtures[16].friction = .00000000f;
		fixtures[9].filter.categoryBits = 0x0004;
		fixtures[9].filter.maskBits = 8;
		fixtures[17].filter.categoryBits = 0x0004;
		fixtures[17].filter.maskBits = 8+1;
		
		//23 sticky block
		fixtures[23].restitution = .0001f;
		fixtures[23].friction = .2f;
		bodyDefs[23].type = BodyType.DynamicBody;
		fixtures[23].filter.categoryBits = 2;
		fixtures[23].filter.maskBits = 10;
		
		//19 body, new
		fixtures[19].restitution = .0000f;
		fixtures[19].friction = 8888.99f;
		bodyDefs[19].angularDamping = 0f;
		bodyDefs[19].linearDamping = .0f;
		bodyDefs[19].type = BodyType.DynamicBody;
		fixtures[19].filter.categoryBits = 0x0001;
		fixtures[19].filter.maskBits = 64+63;
		
		//18 explosion
		fixtures[18].restitution = .0001f;
		fixtures[18].friction = .2f;
		bodyDefs[18].type = BodyType.KinematicBody;
		fixtures[18].filter.categoryBits = 0x0002;
		fixtures[18].filter.maskBits = 0;
		//15 head
		fixtures[15].filter.categoryBits = 0x0004;
		fixtures[15].filter.maskBits = 0;
		bodyDefs[15].type = BodyType.KinematicBody;
		fixtures[15].friction = .2f;
		fixtures[15].density = 50;
		fixtures[15].filter.categoryBits = 0x0001;
		//16 dwarf
		bodyDefs[16].position.x = 0;
		bodyDefs[16].position.y = 0.5f;
		fixtures[16].filter.categoryBits = 0x0008;
		fixtures[16].filter.maskBits = 7;
		bodyDefs[16].type = BodyType.DynamicBody;
		//20 dwarf warrior
		fixtures[20].restitution = .0001f;
		fixtures[20].friction = .2f;
		bodyDefs[20].position.x = 0;
		bodyDefs[20].position.y = 0.5f;
		fixtures[20].filter.categoryBits = 0x0008;
		fixtures[20].filter.maskBits = 7;
		bodyDefs[20].type = BodyType.DynamicBody;
		//11 slime
		bodyDefs[11].position.x = 0;
		bodyDefs[11].position.y = 0f;
		fixtures[11].filter.categoryBits = 8;
		fixtures[11].filter.maskBits = 29;
		bodyDefs[11].type = BodyType.DynamicBody;
		CircleShape ballShape = new CircleShape();
		ballShape.setRadius(.5f);
		ballShape.setPosition(tmpV.set(0,0f));
		fixtures[11].shape = ballShape;
		fixtures[11].friction = 0f;
		fixtures[11].restitution = 1.2f;
		//5Bullet
		bodyDefs[5].type = BodyType.DynamicBody;
		fixtures[5].filter.maskBits = 8;
		fixtures[5].filter.categoryBits = 0x0002;
		
		
		//22 enemy Bullet
		bodyDefs[22].type = BodyType.DynamicBody;
		fixtures[22].filter.maskBits = 3;
		fixtures[22].filter.categoryBits = 0x0002;
		
		//24 animation dummy
		fixtures[24].restitution = .0001f;
		fixtures[24].friction = .2f;
		bodyDefs[24].type = BodyType.KinematicBody;
		fixtures[24].filter.categoryBits = 0x0002;
		fixtures[24].filter.maskBits = 0;
		bodyShapes[24].setAsBox(.5f,.5f, playerCenter.set(0,0.5f), 0);
		
		//26 head button
		fixtures[26].restitution = .0001f;
		fixtures[26].friction = .2f;
		bodyDefs[26].type = BodyType.KinematicBody;
		fixtures[26].filter.categoryBits = 32;
		fixtures[26].filter.maskBits = 1;
		verts[0].set(+.15f,.01f);
		verts[1].set(-.15f,.01f);
		verts[2].set(-.15f,-.6f);
		verts[3].set(+.15f,-.6f);
		bodyShapes[26].set(verts);
		//bodyShapes[26].setAsBox(.05f,.2f, playerCenter.set(0,-0.3f), 0);
		
		//27 item/zombie bb
		fixtures[27].restitution = .01f;
		fixtures[27].friction = 0f;
		fixtures[27].filter.categoryBits = 0x0004;
		fixtures[27].filter.maskBits = 64+16+8;
		bodyDefs[27].type = BodyType.KinematicBody;		
		//fixtures[27].density = 8f;
		
		
		//28 grenade
		fixtures[28].restitution = .01f;
		fixtures[28].friction = 1f;
		bodyDefs[28].type = BodyType.DynamicBody;
		fixtures[28].filter.categoryBits = 16;
		fixtures[28].filter.maskBits = 64+8+4+1;
		//fixtures[28].density = 1f;
		grenadeShape = new CircleShape();
		grenadeShape.setRadius(.5f);
		fixtures[28].shape = medCircle;
		bodyDefs[28].linearDamping = 0;//.5f;
		bodyDefs[28].angularDamping = .5f;
		//fixtures[28] 64+16+8+4+1;
		
		//29 blood splatter
		fixtures[29].restitution = .0001f;
		fixtures[29].friction = .2f;
		bodyDefs[29].type = BodyType.KinematicBody;
		fixtures[29].filter.categoryBits = 0x0002;
		fixtures[29].filter.maskBits = 0;
		
		//30 floaty block
		fixtures[30].restitution = .0001f;
		fixtures[30].friction = .2f;
		bodyDefs[30].type = BodyType.KinematicBody;
		bodyDefs[30].fixedRotation = false;
		fixtures[30].filter.categoryBits = 0x0002;
		fixtures[30].filter.maskBits = 0;
		
		//31 rope link
		fixtures[31].restitution = .0001f;
		fixtures[31].friction = .2f;
		bodyDefs[31].linearDamping = 2f;
		bodyDefs[31].angularDamping = 20f;
		bodyDefs[31].type = BodyType.DynamicBody;
		fixtures[31].filter.categoryBits = 0x0001;
		fixtures[31].filter.maskBits = 63;
		
		//32 ropeClimber
		verts[0].set(+.5f,.5f);
		verts[1].set(-.5f,.5f);
		verts[2].set(-.5f,-.5f);
		verts[3].set(+.5f,-.5f);
		
		bodyShapes[32].set(verts);
		fixtures[32].restitution = .0001f;
		fixtures[32].friction = .2f;
		bodyDefs[32].type = BodyType.KinematicBody;
		fixtures[32].filter.categoryBits = 0x0001;
		fixtures[32].filter.maskBits = 0;
		
		
		verts[0].set(+1.1f,1.1f);
		verts[1].set(-1.1f,1.1f);
		verts[2].set(-1.1f,-1.1f);
		verts[3].set(+1.1f,-1.1f);
		bodyShapes[33].set(verts);
		fixtures[33].restitution = .9f;
		fixtures[33].friction = .2f;
		bodyDefs[33].type = BodyType.KinematicBody;
		fixtures[33].filter.categoryBits = 0x0001;
		fixtures[33].filter.maskBits = 0;
		
		//34 rope end
		verts[0].set(+.1f,.1f);
		verts[1].set(-.1f,.1f);
		verts[2].set(-.1f,-.1f);
		verts[3].set(+.1f,-.1f);
		bodyShapes[34].set(verts);
		//bodyShapes[34].set();
		fixtures[34].restitution = .01f;
		fixtures[34].friction = .9f;
		bodyDefs[34].type = BodyType.DynamicBody;
		fixtures[34].filter.categoryBits = 16;
		fixtures[34].filter.maskBits = 4;
		fixtures[34].shape = circleShape;
		
		//37 rope BB
		fixtures[37].restitution = .01f;
		fixtures[37].friction = .9f;
		bodyDefs[37].type = BodyType.KinematicBody;
		fixtures[37].filter.categoryBits = 16;
		fixtures[37].filter.maskBits = 4;
		fixtures[37].shape = circleShape;
		
		//38 Ninja
		fixtures[38].restitution = .2f;
		fixtures[38].friction = .99f;
		bodyDefs[38].linearDamping = .2f;
		bodyDefs[38].angularDamping = 20f;
		bodyDefs[38].type = BodyType.DynamicBody;
		fixtures[38].filter.categoryBits = 0x0008;
		fixtures[38].filter.maskBits = 23;
		
		//39 poi head
		Shape poiShape = new CircleShape();
		poiShape.setRadius(.3f);
		fixtures[39].restitution = 0;
		fixtures[39].friction = .99f;
		bodyDefs[39].linearDamping = 0f;
		bodyDefs[39].angularDamping = 20f;
		bodyDefs[39].type = BodyType.DynamicBody;
		fixtures[39].filter.categoryBits = 16;
		fixtures[39].filter.maskBits = 64+16+8;
		fixtures[39].shape = poiShape;
		fixtures[39].density = 18.5f;
		
		//61 arrows
		Shape poiShape2 = new CircleShape();
		poiShape2.setRadius(.1f);
		
		//28 grenade
				fixtures[61].restitution = .01f;
				fixtures[61].friction = 1f;
				bodyDefs[61].type = BodyType.DynamicBody;
				fixtures[61].filter.categoryBits = 16;
				fixtures[61].filter.maskBits = 64+8+4+1;
				//fixtures[28].density = 1f;
				grenadeShape = new CircleShape();
				grenadeShape.setRadius(.5f);
				fixtures[61].shape = poiShape2;
				bodyDefs[61].linearDamping = 0;//.5f;
				bodyDefs[61].angularDamping = .5f;
				//fixtures[28] 64+16+8+4+1;
		
//40 cloud
		fixtures[40].restitution = .2f;
		fixtures[40].friction = .99f;
		bodyDefs[40].linearDamping = .2f;
		bodyDefs[40].angularDamping = 20f;
		bodyDefs[40].type = BodyType.DynamicBody;
		fixtures[40].filter.categoryBits = 0x0008;
		fixtures[40].filter.maskBits = 23;
		
		//43 axe shaft
		fixtures[43].restitution = .2f;
		fixtures[43].friction = .99f;
		bodyDefs[43].linearDamping = 0f;
		bodyDefs[43].angularDamping = 0f;
		bodyDefs[43].type = BodyType.DynamicBody;
		bodyDefs[43].fixedRotation = false;
		fixtures[43].filter.categoryBits = 16;
		fixtures[43].filter.maskBits = 8;
		//fixtures[43].shape = grenadeShape;
		fixtures[43].density = .1f;
		
		//44 axe head
		fixtures[44].restitution = 5f;
		fixtures[44].friction = .99f;
		bodyDefs[44].linearDamping = 0f;
		bodyDefs[44].angularDamping = 0f;
		bodyDefs[44].type = BodyType.DynamicBody;
		bodyDefs[44].fixedRotation = false;
		fixtures[44].filter.categoryBits = 16;
		fixtures[44].filter.maskBits = 8;
		fixtures[44].density = 1f;
		//fixtures[43].shape = grenadeShape;
		
		//45 feet
		fixtures[45].restitution = .0001f;
		fixtures[45].friction = .0f;
		bodyDefs[45].linearDamping = 0f;
		bodyDefs[45].angularDamping = 0f;
		bodyDefs[45].type = BodyType.DynamicBody;
		bodyDefs[45].fixedRotation = false;
		fixtures[45].filter.categoryBits = 1;
		fixtures[45].filter.maskBits = 64+63-8;//36;
		fixtures[45].density = 1f;
		CircleShape sh = new CircleShape();
		sh.setRadius(.33f);
		sh.setPosition(tmpV.set(0,.23f));
		footFixt = new FixtureDef();
		footFixt.friction = .07f;
		footFixt.restitution = -.1f;
		
		footFixt.filter.categoryBits = 1;
		footFixt.filter.maskBits = 64+63-8;
		PolygonShape squareSh = new PolygonShape();
		squareSh.setAsBox(.33f, .4f, new Vector2(0,.32f), 0);;
		//squareSh.setPosition();
		footFixt.shape = sh;//squareSh;//bodyShapes[45];;

		fixtures[45].shape = squareSh;//sh;//bodyShapes[45];
		//fixtures[45].shape = new CircleShape();
		//((CircleShape)fixtures[45].shape).setRadius(.5f);
		//((CircleShape)fixtures[45].shape).setPosition(tmpV.set(0,.3f));
		
		fixtures[46].restitution = .000f;
		fixtures[46].friction = .2f;
		bodyDefs[46].linearDamping = 0f;
		bodyDefs[46].angularDamping = 0f;
		bodyDefs[46].type = BodyType.KinematicBody;
		bodyDefs[46].fixedRotation = false;
		fixtures[46].filter.categoryBits = 0;
		fixtures[46].filter.maskBits = 0;
		fixtures[46].density = 1f;
		fixtures[46].shape = circleShape;
		//47 plank
		fixtures[47].restitution = .00000001f;
		fixtures[47].friction = 1f;
		bodyDefs[47].linearDamping = 1f;
		bodyDefs[47].angularDamping = 1f;
		bodyDefs[47].type = BodyType.DynamicBody;
		bodyDefs[47].fixedRotation = false;
		fixtures[47].filter.categoryBits = 64;
		fixtures[47].filter.maskBits = 64+16+8+4+1;
		fixtures[47].density = 3f;
		
		fixtures[48].restitution = 0.1f;
		fixtures[48].friction = 1f;
		bodyDefs[48].linearDamping = 9f;
		bodyDefs[48].angularDamping = 9f;
		bodyDefs[48].type = BodyType.KinematicBody;
		bodyDefs[48].fixedRotation = false;
		fixtures[48].filter.categoryBits = 64;
		fixtures[48].filter.maskBits = 64;
		fixtures[48].density = 1f;
		//49 turret
		fixtures[49].restitution = 0.00000001f;
		fixtures[49].friction = 1f;
		bodyDefs[49].linearDamping = 0f;
		bodyDefs[49].angularDamping = 0f;
		bodyDefs[49].type = BodyType.DynamicBody;
		bodyDefs[49].fixedRotation = false;
		fixtures[49].filter.categoryBits = 8;
		fixtures[49].filter.maskBits = 1+8+4+16+64;
		fixtures[49].density = 16f;
		
		fixtures[50].restitution = 0.00000001f;
		fixtures[50].friction = 1f;
		bodyDefs[50].linearDamping = 0f;
		bodyDefs[50].angularDamping = 0f;
		bodyDefs[50].type = BodyType.DynamicBody;
		bodyDefs[50].fixedRotation = false;
		fixtures[50].filter.categoryBits = 16;
		fixtures[50].filter.maskBits = 16+1;
		fixtures[50].density = .1f;
		
		//51 blade
		fixtures[51].restitution = 0.00000001f;
		fixtures[51].friction = 1f;
		bodyDefs[51].linearDamping = 0f;
		bodyDefs[51].angularDamping = 0f;
		bodyDefs[51].type = BodyType.DynamicBody;
		bodyDefs[51].fixedRotation = false;
		fixtures[51].filter.categoryBits = 8;
		fixtures[51].filter.maskBits = 16+1;
		fixtures[51].density = .1f;
		//52 slow bullet
		fixtures[52].restitution = 0.00000001f;
		fixtures[52].friction = 1f;
		bodyDefs[52].linearDamping = 0f;
		bodyDefs[52].angularDamping = 0f;
		bodyDefs[52].type = BodyType.KinematicBody;
		bodyDefs[52].fixedRotation = false;
		fixtures[52].filter.categoryBits = 16;
		fixtures[52].filter.maskBits = 16+1;
		fixtures[52].density = .1f;
		fixtures[52].shape = circleShape;
		//53 gib
		fixtures[53].restitution = 0.00000001f;
		fixtures[53].friction = 1f;
		bodyDefs[53].linearDamping = 1.4f;
		bodyDefs[53].angularDamping = 0f;
		bodyDefs[53].type = BodyType.DynamicBody;
		bodyDefs[53].fixedRotation = false;
		fixtures[53].filter.categoryBits = 16;
		fixtures[53].filter.maskBits = 16+4+1;
		fixtures[53].density = .1f;
		fixtures[53].shape = smallCircle;
	
		//28 grenade
		fixtures[54].restitution = .01f;
		fixtures[54].friction = 1f;
		bodyDefs[54].type = BodyType.DynamicBody;
		fixtures[54].filter.categoryBits = 16;
		fixtures[54].filter.maskBits = 64+16+8+4+1;
		grenadeShape = new CircleShape();
		grenadeShape.setRadius(.5f);
		fixtures[54].shape = medCircle;
		//bodyDefs[54].linearDamping = .5f;
		bodyDefs[54].angularDamping = .5f;
		
		//56 door
		fixtures[56].restitution = .01f;
		fixtures[56].friction = 1f;
		bodyDefs[56].type = BodyType.KinematicBody;
		fixtures[56].filter.categoryBits = 16;
		fixtures[56].filter.maskBits = +1;
		bodyShapes[56].setAsBox(1f,1f);
		fixtures[56].shape = bodyShapes[56];
		//bodyDefs[54].linearDamping = .5f;
		bodyDefs[56].angularDamping = .5f;
		
		//57 rubber block
		fixtures[57].restitution = 2.2f;
		fixtures[57].friction = 1.5f;
		bodyDefs[57].type = BodyType.KinematicBody;
		fixtures[57].filter.categoryBits = 16;
		fixtures[57].filter.maskBits = 8+1;
		bodyShapes[57].setAsBox(.4951f,.4951f);
		bodyDefs[57].angularDamping = .5f;
		//fixtures[57].shape = bodyShapes[57];
		//bodyDefs[54].linearDamping = .5f;
		
		//58 small mob
		fixtures[58].restitution = .1f;
		fixtures[58].friction = .16f;
		bodyDefs[58].type = BodyType.DynamicBody;
		fixtures[58].filter.categoryBits = 8;
		fixtures[58].filter.maskBits = 21+8;
		bodyShapes[58].setAsBox(.34f,.34f);
		//fixtures[58].shape = medCircle;
		bodyDefs[58].linearDamping = .1f;
		
		//59 flying
		fixtures[59].restitution = .0f;
		fixtures[59].friction = .5f;
		bodyDefs[59].type = BodyType.DynamicBody;
		fixtures[59].filter.categoryBits = 8;
		fixtures[59].filter.maskBits = 21+8;
		//bodyDefs[59].gravityScale = .25f;
		bodyShapes[59].setAsBox(.34f,.34f);
		//fixtures[58].shape = medCircle;
		//bodyDefs[59].linearDamping = 4.75f;
		
		//60 sheep
		fixtures[60].restitution = .0f;
		fixtures[60].friction = 16f;
		bodyDefs[60].type = BodyType.DynamicBody;
		fixtures[60].filter.categoryBits = 8;
		fixtures[60].filter.maskBits = 21+8;
				//bodyDefs[59].gravityScale = .25f;
		bodyShapes[60].setAsBox(.34f,.34f);
				//fixtures[58].shape = medCircle;
		//bodyDefs[60].linearDamping = 4.75f;
		
		
		//55 generic
		fixtures[55] = fixtures[2];
		bodyDefs[55] = bodyDefs[2];
		
		//35 bridge link
		fixtures[35].restitution = .0001f;
		fixtures[35].friction = .9f;
		bodyDefs[35].type = BodyType.DynamicBody;
		fixtures[35].filter.categoryBits = 0x0001;
		fixtures[35].filter.maskBits = 1;
		
		
		//36 weight dummy
		verts[0].set(+.05f,.5f);
		verts[1].set(-.05f,.5f);
		verts[2].set(-.05f,-.1f);
		verts[3].set(+.05f,-.1f);
		
		bodyShapes[36].set(verts);
		fixtures[36].restitution = .0001f;
		fixtures[36].friction = .2f;
		bodyDefs[36].type = BodyType.DynamicBody;
		fixtures[36].filter.categoryBits = 0x0001;
		fixtures[36].filter.maskBits = 0;
		
		//42 spawner
		bodyDefs[42].type = BodyType.KinematicBody;
		fixtures[42].filter.categoryBits = 0x0008;
		fixtures[42].filter.groupIndex = -1;
		
		fixtures[42].density = 0;//0.00000001f;
		fixtures[42].friction = 0;
		//aaaaafixtures[6].
		fixtures[42].filter.maskBits = 0;
		
		
		//Player BB (bottom)
		fixtures[4].filter.categoryBits = 32;
		fixtures[4].filter.maskBits = 33;
		
		//player old
		fixtures[0].filter.categoryBits = 0x0001;
		fixtures[0].filter.maskBits = 63;
		//9 npc bb
		
		//10 spiderdw
		bodyDefs[10].type = BodyType.DynamicBody;
		bodyDefs[10].gravityScale = 0;
		fixtures[10].filter.maskBits = 21;
		fixtures[10].filter.categoryBits = 8;
//		1 item
		fixtures[1].filter.maskBits = 21;
		fixtures[1].filter.categoryBits = 16;
		fixtures[1].friction = .9f;
		fixtures[1].restitution = -0.8f;
		bodyDefs[1].linearDamping = 1;
		bodyDefs[1].fixedRotation = false;
		fixtures[1].shape = grenadeShape;
		
		
		bodyDefs[4].type = BodyType.KinematicBody;
		fixtures[4].friction = 5.5f;
		bodyDefs[4].fixedRotation = false;
		bodyDefs[13].type = BodyType.KinematicBody;
		fixtures[13].friction = .0f;
		bodyDefs[13].fixedRotation = false;
		bodyDefs[14].type = BodyType.KinematicBody;
		fixtures[14].friction = .0f;
		bodyDefs[14].fixedRotation = false;
		
		
		bodyDefs[8].type = BodyType.KinematicBody;
		fixtures[8].friction = 128f;
		fixtures[8].filter.categoryBits = 0x00032;
		fixtures[8].filter.maskBits = 33;

		
		

		
		//FILTERS
		
		
		
		
		fixtures[13].filter.categoryBits = 0x0004;
		fixtures[14].filter.categoryBits = 0x0004;
		
		//fixtures[1].filter.categoryBits = 0x0008;
	
		
		fixtures[13].filter.maskBits = 33;
		fixtures[14].filter.maskBits = 33;
		
		
		//mask bit 1s set collisions on
		
		
		
		//beeWalkAnimR = new Animation(0.05f, FramesBuffer);
		
		
		//ZWalkFR.flip(true, false);
		///grapple
		
		//grappleMotorDef.enableLimit = true;
		//grappleMotorDef.lowerAngle =
		//grappleMotorDef.upperAngle =
		//grappleMotorDef.
		bodyDefs[6].type = BodyType.DynamicBody;
		fixtures[6].filter.categoryBits = 0x0008;
		fixtures[6].filter.groupIndex = -1;
		//ropeJointDef = new DistanceJointDef(); 
		//ropeJointDef.frequencyHz = 15;
		//ropeJointDef.dampingRatio = 1f;
		
		fixtures[6].density = 0;//0.00000001f;
		fixtures[6].friction = 0;
		//aaaaafixtures[6].
		fixtures[6].filter.maskBits = 0;
		
		bodyDefs[7].type = BodyType.KinematicBody;
		fixtures[7].filter.categoryBits = 0x0008;
		fixtures[7].filter.groupIndex = -1;
		
		fixtures[7].density = 0;//0.00000001f;
		fixtures[7].friction = 0;
		//aaaaafixtures[6].
		fixtures[7].filter.maskBits = 0;
		
		//3 pigs
		fixtures[6].friction = 0;
		
		
		//bodyDefs[9] = bodyDefs[3];
		//fixtures[9] = fixtures[3];
		
		//bodyDefs[10] = bodyDefs[3];
		//fixtures[10] = fixtures[3];
//		fixtures[10].filter.categoryBits = 0x0008;
//		fixtures[10].filter.maskBits = 11;
//		bodyDefs[10].linearDamping = 01f;
//		bodyDefs[3].linearDamping = 00f;
//		bodyDefs[10].type = BodyType.DynamicBody;
		
		//bodyDefs[11].type = BodyType.KinematicBody;

		
		//TextureRegion arrowFlip = new TextureRegion(arrowTexture);
		//arrowFlip.flip(true,true);

		arrowSprite = atlas.createSprite("arrow");
		arrowSpriteR = atlas.createSprite("arrow");
		arrowSprite.flip(true, false);
		arrowSprite.setSize(Punk.MAPTILESIZE*2, Punk.MAPTILESIZE*4);
		arrowSpriteR.setSize(Punk.MAPTILESIZE*2, Punk.MAPTILESIZE*4);
		arrowSpriteR.setPosition(Punk.RESX-Punk.MAPTILESIZE*2, 0);
		jumpSprite = atlas.createSprite("jump");
		jumpSprite.setSize(Punk.MAPTILESIZE*2, Punk.MAPTILESIZE*4);
		climbUpSprite = atlas.createSprite("climbup");
		climbDownSprite = atlas.createSprite("climbdown");
		climbUpSprite.setSize(Punk.MAPTILESIZE*2, Punk.MAPTILESIZE*4);
		climbDownSprite.setSize(Punk.MAPTILESIZE*2, Punk.MAPTILESIZE*4);
		climbUpSprite.setPosition(Punk.RESX-Punk.MAPTILESIZE*2, Punk.MAPTILESIZE*8);
		climbDownSprite.setPosition(0, Punk.MAPTILESIZE*8);
		
		/*birdFixture = new FixtureDef();
		birdBody = new BodyDef();
		
		birdBody.type = BodyType.KinematicBody;
		birdBody.position.x = 2000;
		birdBody.position.y = 150;
		birdBody.fixedRotation = false;
		birdFixture.density = 10;*/
		
		//birdShape = new PolygonShape();
		//birdShape.setAsBox(1f,0.5f,playerCenter, 0);
		
		//birdFixture.shape = birdShape;
		
		fixtures[12] = new FixtureDef();
		bodyDefs[12] = new BodyDef();
		//snakeBody = new BodyDef();
		
		bodyDefs[12].type = BodyType.KinematicBody;
		bodyDefs[12].position.x = 2000;
		bodyDefs[12].position.y = 150;
		bodyDefs[12].fixedRotation = false;
		fixtures[12].density = 10;
		fixtures[12].friction = 0.95f;
		fixtures[12].filter.maskBits = 0; 
		bodyShapes[12] = new PolygonShape();
		bodyShapes[12].setAsBox(0.5f,0.5f,playerCenter.set(0.5f,0.5f), 0);
		
		fixtures[12].shape = bodyShapes[12];
		
		//bodyDefs[16] = bodyDefs[0];
		fixtures[21] = null;
		
		/*for (int i = 0; i < 10; i++){
			verts[0].set(.5f,i);
			verts[1].set(-.5f,i);
			verts[2].set(-.5f,-.5f);
			verts[3].set(.5f,-.5f);
			
			//verts[0].set(2.5f,2.5f);
			//verts[1].set(-2.5f,2.5f);
			//verts[2].set(-2.5f,-2.5f);
			//verts[3].set(2.5f,-2.5f);
			
			ropeShapes[i] = new PolygonShape();
			ropeShapes[i].set(verts);
			ropeDefs[i] = new BodyDef();
			ropeDefs[i].position.x = 0;
			ropeDefs[i].position.y = 0;
			ropeDefs[i].fixedRotation = true;
			ropeFixtures[i] = new FixtureDef();
			ropeFixtures[i].shape = ropeShapes[i];
			ropeFixtures[i].restitution = 0;
			ropeFixtures[i].density = 5;//50;
			//ropeFixtures[i].shape = ropeShapes[i];
		}
		for (int i =0; i<10; i++)
		{
			
			ropeFixtures[i].restitution = .01f;
			ropeFixtures[i].friction = 1f;
			ropeDefs[i].type = BodyType.DynamicBody;
			ropeFixtures[i].filter.categoryBits = 16;
			ropeFixtures[i].filter.maskBits = 13;
			ropeFixtures[i].shape = ropeShapes[i];
			ropeDefs[i].linearDamping = .5f;
			ropeDefs[i].angularDamping = .5f;	
		}
		ropeJointDef.collideConnected = false;
		
		fixtures[35].shape = ropeShapes[0];
		*/
		
		initMobInfos();
		
		
		
	
	
		
	}






	private static Sprite[] skillS = new Sprite[SKILLTOTAL];
	
	public static Sprite getSkillSprite(int id) {
		if (skillS[id] == null)Gdx.app.log(TAG, "didn't find region"+id);
		return skillS[id];
	}
	public static String factionString() {
		String s = "factions:\n";
		for (int i = 0; i < 10; i++)
		for (int j = 0; j < 10; j++){
			s += "f"+i+" vs "+j+" = ";
			s += factions[i].opinion.get(j);
			s += "\n";
		}
		return s;
		
	}


	
	



	
}

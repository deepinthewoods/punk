package com.niz.punk;

//import android.util.Log;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.PauseableThread;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.niz.punk.blocks.Charge;

public abstract class Punkesque implements Screen, InputProcessor{
	private static String TAG = "main";
	/** the camera **/
	protected OrthographicCamera camera, UICamera;

	/** the renderer **/
	protected Box2DDebugRenderer renderer;

	/** our box2D world **/
	protected World world;

	/** ground body to connect the mouse joint to **/
	protected Body groundBody;

	/** our mouse joint **/
	protected MouseJoint mouseJoint = null;

	/** a hit body **/
	protected Body hitBody = null;

	//protected abstract void createWorld (World world);

	/** temp vector **/
	protected Vector2 tmp = new Vector2();

	public Game game;
	
	///MY STUFF//////////////////////////////////////////////////////////////////////////////////////
	public static int gameMode = 1;
	public enum GameType {CREATIVE, SURVIVAL, PSYCHONAUT};
	//
	
	protected abstract void drawHelp();
	protected abstract void closePerks();
	protected abstract void makeMenuActors();
	protected abstract void openMenu();
	protected abstract void createPhysicsWorld (World world, boolean isFirst);
	protected abstract void createTextureWorld (World nworld);
	protected abstract void renderTextures(World world, SpriteBatch batch, Vector3 offsetVector);
	protected abstract void renderMap(World world, SpriteBatch batch, Vector3 origin, Vector3 camPos, PunkBodies monsterIndex, Camera camera);
	//protected abstract void updatePhysics(World world, PunkMap map);
	protected abstract boolean damageBlock(World world, PunkMap map, Vector2 target, int damage, ItemPool itemPool);
	protected abstract Vector2 findDigTarget();
	protected abstract Vector2 findBlockTarget(World world, PunkMap map, Vector2 direction, int range);
	//protected abstract Vector2 findBucketTarget(World world, PunkMap map, Vector2 direction);
	protected abstract void renderFarBackground();
	protected abstract void drawMonsters();
	protected abstract void renderNearBackground();
	protected abstract void drawBlockHighlight(BlockLoc p) ;
	protected abstract void drawInvalidBlockHighlight(BlockLoc p) ;
	protected abstract void drawUI();
	protected abstract void drawCrafting();
	protected abstract void drawCraftingConfirm();
	protected abstract void drawSystem();
	protected abstract void drawAimingLine(boolean b);
	protected abstract void drawPlacingBlockLine();


	protected abstract void closeInv();
	protected abstract void closeCrafting();
	protected abstract void closeConfirm();
	protected abstract void closeMenu();
	protected abstract void closeBelt(boolean buttonAlso);
	//protected abstract void closeAction();

	protected abstract void openBelt();
	protected abstract void openMessageWindow(String message);
	protected abstract void openChestButton();

	
	
	protected abstract void drawInv(Player player);
	protected abstract void drawPlayer();
	protected abstract void drawLoadingScreen();
	protected abstract void updateMonsters();
	protected abstract void updateSpawns();
	protected abstract void updateControls();
	

	protected abstract void doChunkShift (int direction);
	protected abstract void updateMonsterRemovals();
	//protected abstract void getGraphics();
	protected abstract void changeBeltSlot(int slotID, boolean b);
	protected abstract int getTouchSlot(int x, int y);
	protected abstract void doInvButton();
	protected abstract void doActionButton();
	protected abstract void doPlayerTouch();
	protected abstract void doMenuButton();
	protected abstract void updateActionButton();
	protected abstract int getActionButton();
	protected abstract void saveGame(boolean pl);
	protected abstract void adjustBackgroundLighting(float l);
	protected abstract void openInv();

	//protected abstract int getDigTime(int id);
	public Array<PhysicsActor> selectedItems = new Array<PhysicsActor>();
	public Array<PhysicsActor> availableItems = new Array<PhysicsActor>();//for beginning bit

	public Player player;
	//public FPSLogger fpsLog = new FPSLogger();
	public static Preferences prefs;
	public boolean DEBUGRENDER = false;                                   //TODO
	public static boolean saveAllQueued = false;
	public Group invGroup, craftGroup, beltGroup, perkGroup;
	public Stage stage;
	public Actor invActor, craftActor;
	public InputMultiplexer inputMultiplexer;
	protected Array<Portal> portalList = new Array<Portal>(); 

	static Vector2 lineSrc = new Vector2();
	static Vector2 lineDest = new Vector2();
	protected static boolean drawFinger, drawAngle;
	
	public boolean paused = false;
	public Start starter;
	public boolean[]pointerUp = new boolean[10];
	public int[] pointerAction = new int[10];
	public long[] pointerTimer = new long[10];
	public StringObj queuedTip = new StringObj();
	public int fps = 0;
	public static final String[] popupMessages = {
		"Welcome! \nUse the buttons in the corners to move. \nInventory and Crafting screens can be accessed with the back button.\n",//TODO 
		"You have died.", 
		"Blocks\n Touch and drag to change placement, release to place.", 
		"Flail\n Destroys blocks and hurts mobs. Touch and drag to aim.", 
		"Fist\n Destroys blocks and hurts mobs. Touch and drag to aim.", 
		"Axe\n Destroys wooden blocks and hurts mobs. Touch and drag to set start position.", 
		"Torch\n Provides light. Touch and drag to change placement", "Bucket. Places and picks up fluid blocks.", 
		"Guns\n Touch to shoot.", 
		"Projectiles\n Touch and drag to aim. Release touch to throw.", 
		"Grenade\n Damages blocks and enemies. Starts fires.",
		"Bridge\n Projectile. Makes a bridge from the player to it's landing point.", 
		"Health is low. Eat some food to restore it."};
	
	public static float aspectRatio;
	//public long tipTimer;
	//public CloudsBlock[] clouds = new CloudsBlock[10];
	//public MZombie[] zombies = new MZombie[ZOMBIECOUNT];
	//public static int ZOMBIECOUNT = 0;
	//public PZombie zombiePool = new PZombie();
	//public PSlime slimePool;// = new PSlime();
	//public PSnake snakePool = new PSnake();
	//public PPig pigPool = new PPig();
	//public PSpider spiderPool = new PSpider();
	
	public BossHandler bHandler;// = new BossHandler();
	public String saveStr = "Saving";
	//public BulletPool bulletPool;	
	public static Setting prefs_buttons 
	, prefs_sound 
	//, prefs_zoomBtn = new Setting("zButtons: On", "zButtons: Off")
	, prefs_music
	, prefs_background
	,prefs_debug
	, prefs_place_mode;
	//public Setting prefs_menuBtn, prefs_jumpBtn;
	public boolean beltHidden = false;

	public static int MAXAIMSTRENGTH = 6, PLAYERAIMTIME = 150;
	
	public boolean isDone = false;;
	public SpriteBatch batch;
	
	//public Sprite controlsL,controlsR;
	//public Texture controlsTex;
	public PunkMap gMap;
	public BitmapFont font;
	public static long gTime = 0, deltaMilli = 0;;
	public static float deltaTime = 0;
	
	public long nextAITime = 0;
	public int blockHP = 5;
	public static Vector2 targetBlock = new Vector2(0,0), placeTargetBlock = new Vector2(0,0), digTargetBlock = new Vector2(0,0);
	private Vector2 lastTarget = new Vector2(0,0);
	public static Block genericAirBlock = new Block(0,0);
	public static Block genericBedrockBlock = new Block (1,0);
	
	public static int availableRAM;
	
	
	static Array<BlockLoc> blockHighlightQ = new Array<BlockLoc>(), invalidBlockHighlightQ = new Array<BlockLoc>();
	//static boolean drawDirection = false;
	//static Vector2 directionArrowSrc = new Vector2();
	//static float directionArrowAngle = 0;
	public abstract void drawDirectionArrow(float x, float y, float angle);

	public Vector2 mapTmp = new Vector2(0,0);
	public Vector3 transTmp = new Vector3();
	private Vector3 endPointTmp = new Vector3();
	public static int CHUNKSIZE = 64;//128;
	public static int CHUNKSIZEMASK = 63;//127;
	public static byte CHUNKBITS = 6;//7;
	public static byte TILESIZE;// = 32;
	public static byte TILESIZE2;// = (byte)(TILESIZE / 2);
	public static int RESX, RESY, PRESX, PRESY;// = 480;
	public static int BWIDTH, BHEIGHT,  PRADIUSX, PRADIUSY;
	//public static int BREPEATSX, BREPEATSY;
	public static int MAPTILESIZE;// = 32;
	public static int ITEMTILESIZE;// = 32;
	//public static int BWIDTH=20, BHEIGHT=15, BRADIUSX=BWIDTH/2-1, BRADIUSY=BHEIGHT/2, PRADIUSX = RESX/2, PRADIUSY = RESY/2;
	
	//public static int QOFFSET1 = 100, QOFFSET2 = 200, QOFFSET3 = 300, QOFFSET4 = 400, QOFFSET5 = 500, QOFFSET6 = 600, QOFFSET7 = 700;
	public static int BUTTONBUFFER;// = 80;//side buttons
	public static int BELTBUFFERX,BELTBUFFERY, PBELTBUFFERX, PBELTBUFFERY, PBELTOFFSETX, PBUTTONBUFFER;// = 64;//quickslots
	public static int BELTOFFSETX;//, EXTRASPACEX;
	public static int JUMPBUTTONSIZE;// = 30;
	public static int UILINECOUNTX;// = RESX / 128+1;
	//public static int JUMPBUTTONX1;// = PRADIUSX-JUMPBUTTONSIZE;
	//public staticint JUMPBUTTONX2;// = PRADIUSX+JUMPBUTTONSIZE;
	//public int JUMPBUTTONY1;//= PRADIUSY-JUMPBUTTONSIZE;
	//public int JUMPBUTTONY2;// = PRADIUSY+JUMPBUTTONSIZE;
	public static int BUTTONBUFFERL, BUTTONBUFFERR;// = RESX - BUTTONBUFFER; = 63,
	private int AITicker= 0;
	public PunkBodies monsterIndex;
	
	public PunkContactListener listener;// = new PunkContactListener();
	public static int targetedBlockX = 0, targetedBlockY = 0;
	public static Vector2 touchLoc = new Vector2(0,0), worldTouchLoc = new Vector2(0,0);
	public static Vector2 targetBlockV = new Vector2();
	public int pressedQuickSlot = -1;
	public int touchSlotUp = 1, touchSlotDown = 1;
	public boolean touchDragging = false;
	public static boolean touchDisabled = false;
	public boolean beltOnTop = true;
	public int touchX, touchY;
	public int craftingPage = 0, selectedRecipe = 0, 
				craftingRecipeCount = 0, craftingRecipeMax;
	public int chunkQueue = 0;
	public float deltaLimit = 1/15f;
	public static float skyColor;
	public int deadFloatys = 0;
	public boolean menuPressed = false, zoomPressed = false, climbDownPressed = false, climbUpPressed = false;
	public long zoomTimer;
	public PunkInventory activeChest;
	//public int selectedSign;
	public String signText;
	public String[] tooltips = new String[16];
	public boolean backToGamePending = false;
	public static boolean processing = true;
	public static int processingInc;
	public float backToGameTimer;
	
	public BlockLoc activeSignLoc = new BlockLoc();
	public Vector2 tmpV = new Vector2(0,0);
	public GL20 gl;

	public float[] aimerLineVerts = new float[10];
	public short[] aimerLineIndices = new short[]{0,1};
	static final int touchHighlightSides = 10;

	public float[] fingerVerts = new float[5 * touchHighlightSides+15];
	public short[] fingerIndices = new short[2 * touchHighlightSides+4], filledIndices = new short[2 * touchHighlightSides+2];//{0,1,1,2,2,3,3,4,4,5,5,6,6,7,7,8,8,9,9,10,10,11,11,12,12,13,13,14,14,15};
	public Mesh aimerLineMesh, fingerMesh;
	public float[]aimerArrowVerts = new float[12];
	public short[]aimerArrowIndices = new short[]{0,1,2};
	public Mesh aimerArrowMesh;
	static FloatArray directionArrowQ = new FloatArray(true,8);;

	public static float adjustedTouchAngle;
	
	private float gTimeAccumulator = 0, torchAccumulator = 0;
	public static long screenTime;
	public static int gTimeIncrement;
	public static int incrementer;
	public boolean debug = true, worldGenQueued = true;
	public int worldGenInc;// = -Chunk.primeMaterialPlane.wWidth, worldGenIncY;;// = getGroundHeight(-Chunk.primeMaterialPlane.wWidth)/Punk.CHUNKSIZE;
	public float renderTimeLeft, timeStep = 1/30f;
	public long genTimeLast, genTimeDelta, genTime;
	public int genProgress = 0, genTotal;
	public static int accum16, accum30, accum8;
	public float acctime16, acctime30, acctime8;
	public StringBuilder genString;
	int jumpCacheProgress;
	PunkBlockList[] jumpData = {new PunkBlockList(), new PunkBlockList(), new PunkBlockList(), new PunkBlockList(),
			new PunkBlockList(), new PunkBlockList(), new PunkBlockList(), new PunkBlockList(), new PunkBlockList(),
			new PunkBlockList(), new PunkBlockList(), new PunkBlockList()};
	boolean cacheJumps = true;
	
	public static boolean timeValid, fixedTimeStep = false;;
	public void render(float delta) {
	
		//Gdx.app.log(TAG, "" + ( 1 << 5              ));
		//fixedTimeStep = true;
		//timeStep = 1f/3000f;
		if (cacheJumps){
			
			
			
		}
		
		//delta *= .1f;
		
		timeValid = true;
		if (!paused){
			timeValid = true;
			deltaTime = delta;
			if (deltaTime > deltaLimit){
				Gdx.app.log(TAG, Punk.numberStrings[gMap.updateState]);
				deltaTime = deltaLimit;
				delta = deltaLimit;
			}
			
			{
				int s = player.controllingMob.state;
				if (s==0)
					timeValid = !(player.controllingMob.stopped);
				else if (s == 18 || s == 7 || s == 41)
					timeValid = !(player.controllingMob.stopped);
			}
			
			renderTimeLeft += deltaTime;
			
			if (fixedTimeStep){
				if (timeValid) 
				
					while (renderTimeLeft > 0){
						renderTimeLeft -= timeStep;
						
						world.step(timeStep, 1, 1);
						deltaTime = timeStep;
						if (renderTimeLeft > 0){
							if (world.getContactCount() >0 && timeValid)listener.processContacts(world.getContactList(), player, gTime, gMap, world, monsterIndex);
							gMap.itemPool.updateRemovals(world);
							gMap.chunkActors.updateRemovals(player, world);
							updateMonsterRemovals();
							//Gdx.app.log(TAG, "physics tick"+renderTimeLeft);
							updateMonsters();
							
						}
						
					}
				else {
					renderTimeLeft = 0f;
					deltaTime = 0f;
				}
				//Gdx.app.log(TAG, "physics tick end ------------------");
				
			}
			
			else {
			//deltaTime *= .2f;
				if (timeValid){
					world.step(deltaTime, 8, 2);
					
				}
				else deltaTime = 0f;
			}
		}
		if (!gMap.fetchDone) timeValid = false;
		
		

		
		if (saveAllQueued){
			gMap.chunkPool.saveAll();
			//gMap.chunkPool.removeFarChunks();
			saveAllQueued = false;
			return;
		}
		gTimeAccumulator += deltaTime;
		gTimeIncrement = (int)(gTimeAccumulator * 1000);
		gTimeAccumulator -= (gTimeIncrement/1000f);
		gTime += gTimeIncrement;
		deltaMilli = gTimeIncrement;
		
		screenTime += delta*1000;
		//fpsLog.log();
		//TICK
		
		// if (false && prefs_music && !monsterIndex.currentMusicLoop.isPlaying()){//music stopped
		 
		//	monsterIndex.queueMusic(player, gMap, gTime);
		//	monsterIndex.currentMusicLoop = monsterIndex.queuedMusicLoop;
		//	monsterIndex.currentMusicLoop.play();
		//}
		torchAccumulator -= delta;
		if (torchAccumulator < 0){
			CorneredSprite.t++;
			if (CorneredSprite.t >= 16)
				CorneredSprite.t = 0;
			torchAccumulator += .25f;//MathUtils.random(.15f,.2f);
		
		}
		acctime8 -= delta;
		acctime16 -= delta;
		acctime30 -= delta;
		if (acctime8 < 0f){
			accum8++;
			acctime8 = .015f;
		}
		if (acctime16 < 0f){
			accum16++;
			acctime16 = .0625f;
		}
		if (acctime30 < 0f){
			accum30++;
			acctime30 = .0333333333333f;
		}
		

		listener.time = gTime;
		gMap.itemPool.updateRemovals(world);
		gMap.chunkActors.updateRemovals(player, world);
		updateMonsterRemovals();
		if (world.getContactCount() >0 && timeValid)listener.processContacts(world.getContactList(), player, gTime, gMap, world, monsterIndex);
		
		if (gTime > nextAITime)
		{
			if (AITicker % 1 == 0)incrementer++;
			//if (debug) Gdx.app.log("main", "render 3 long update");
			fps = Gdx.graphics.getFramesPerSecond();
			availableRAM = Start.mi.getAvailMegs();
			////Gdx.app.log("player", "left:"+player.isLeft);
			nextAITime = gTime+250;
			AITicker += 1;
			if (true || MathUtils.randomBoolean()){
				Charge.allowed = true;
			}
			gMap.chunkPool.timedUpdatesQueued++;
			if (timedThread.isPaused()) timedThread.onResume();
			
			if (AITicker>16)
				{
					AITicker = 0;

						//Gdx.app.log("MAIN", "globalTime:"+player.globalTime+" moniutes:"+player.globalMinute);
						Player.gameInfo.minutes = (int) (player.globalTime / 60);
						player.globalMinute = (int) (player.globalTime / 60);
						gMap.updateTimeOfDay(Player.gameInfo.minutes, true);

					if (Player.gameInfo.difficulty == 0){
						
					} else if (Player.gameInfo.difficulty != 3){
						if (gTime-10000 > player.damageTimer && player.health < player.maxHealth()-1)player.health+=2;
					}
					
				}
			//updateSpawns();
			gMap.updateTimed();
		
			adjustSkyColor();
			adjustBackgroundLighting(skyColor);
			
			//gMap.updateSlow();
			
		}		
		
		
		gMap.blockMoverPool.update(gMap, deltaTime);
		gMap.updateMain();
		//if (!fixedTimeStep)
			updateMonsters();

		gMap.itemPool.update(gMap, world, deltaTime, player, monsterIndex, gTime);
		
		if (chunkQueue != 0) {
			if (PunkMap.openWorld) 
				doChunkShift(chunkQueue);			
			chunkQueue = 0;
		}

		if (!player.isOnChest && player.isOnChestBlock){//first entering chest block
			
			Block chestBlock = gMap.getBlock(player.x, player.y);
			if (chestBlock.blockID == 8)
			//start chest
			activeChest = gMap.getChest(player.x, player.y, chestBlock.meta);
			Gdx.app.log("main", "open chest");
			//populateFloatyItems(activeChest, true);
			openChestButton();
			player.isOnChest = true;
			//gameMode = 14;
		}
		if (player.isOnChest && !player.isOnChestBlock){
			gMap.chunkPool.saveChest(activeChest);
			Punk.closeAction();
			Gdx.app.log("main", "close");
			player.isOnChest = false;
		}
	
		if (climbDownPressed){
			player.climbDown(gMap, world, gTime);
		}  else if (climbUpPressed){
			player.climbUp(gMap, world, gTime);
		}
		
		
		Chunk.updateDoors(player, gMap);
		
		if (player.isGliding){
			setFlyTouchAngle();
		}
		
		
		if (zoomPressed && zoomTimer+200 < gTime){
			player.zoomLevel -=deltaTime;
			player.zoomLevel = Math.max(player.zoomLevel, player.minZoomLevel);
			player.zoomLevel = Math.min(player.zoomLevel, player.maxZoomLevel);
		}
		//if (debug) Gdx.app.log("main", "render 7 player update");
		//updateButtonOverrides();
		//player.update(deltaTime);
		player.updateNeck(delta, gMap);
		//updateControls();
		

		//if (debug) Gdx.app.log("main", "render 8 player stuff done, chunk stuff");
		if (player.x > player.rightLoad){
			chunkQueue = 1;
			//gameMode = 20;
			//Gdx.app.log("main", "shift 1");
		}
		else if (player.x < player.leftLoad){
			chunkQueue = -1;
			//Gdx.app.log("main", "shift -1");

			//gameMode = 20;
		} else if (player.y > player.topLoad){
			chunkQueue = 2;
			//Gdx.app.log("main", "shift 2 "+player.topLoad + " player "+player.y);

			//gameMode = 20;
		} else if (player.y < player.bottomLoad){
			chunkQueue = -2;
			//Gdx.app.log("main", "shift -2 "+player.bottomLoad + " player "+player.y);

			//gameMode = 20;
		}
		//gMap.updater.updateSky();
		
		
		if (gameMode >= 16)
			stage.act(delta);
		else {
			//beltGroup.act(deltaTime);
			//TODO
			
			//btnMessageText.act(deltaTime);
		}
		//if (debug) Gdx.app.log("main", "render 9 particles");

		Player.particles.update(deltaTime);
		Punk.visibleDistanceFromPlayer = (int)(player.zoomLevel * (Punk.BWIDTH>>1))+2;
		//DRAWING
		//if (debug) Gdx.app.log("main", "render 10 draw");
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		//batch.setBlendFunction(gl.GL_SRC_ALPHA, gl.GL_ONE_MINUS_SRC_ALPHA);
		
		//camera.projection.setToOrtho2D(0f,0f,25f,15f);//does nothing?
		camera.update();
		//camera.apply(gl);
		if (saveAllQueued){
			batch.begin();
			font.draw(batch, saveStr, Punk.RESX/2, Punk.RESY/2);
			//saveAllQueued = false;
			batch.end();
			return;
		}

		switch (gameMode)
		{
		case 67://world generation
			break;
		case 80:
		
		//case 18:
		case 0:case 12:case 11:case 2:	case 3:case 4:case 5000:case 6:case 7:case 8:case 9:
		case 10:case 1:case 20:case 14: case 800:case 81:case 82:case 83:case 84:case 66:
		case 15:case 13:case 16: default:
			
			if (prefs_background.value == 1){// && gMap.CHUNKOFFSETY > -120){
				MiniMap miniMap = Chunk.planes.get(gMap.currentPlane).miniMap;
				miniMap.render(batch, camera, player, gMap);
				
				
				if (true || player.zoomLevel > 16f){
					//miniMap.checkZoom(player.zoomLevel);
					
					camera.zoom = player.zoomLevel/miniMap.parallaxNearRatio;
					camera.update();
					//miniMap.render(batch, camera, player, gMap, gl);
					batch.setProjectionMatrix(camera.combined);
					batch.begin();
					for (int i = -1; i < 2; i++)
						for (int j = -1; j < 2; j++){
							Chunk c = gMap.chunkPool.getChunkWorld(player.controllingMob.x+i*Punk.CHUNKSIZE, player.controllingMob.y+j*Punk.CHUNKSIZE);
							if (c != null)c.drawClouds(batch, deltaTime,0);
						}
					batch.end();
					camera.zoom = player.zoomLevel/(miniMap.parallaxNearRatio+.2f);
					camera.update();
					batch.setProjectionMatrix(camera.combined);
					batch.begin();
					for (int i = -1; i < 2; i++)
						for (int j = -1; j < 2; j++){
							Chunk c = gMap.chunkPool.getChunkWorld(player.controllingMob.x+i*Punk.CHUNKSIZE, player.controllingMob.y+j*Punk.CHUNKSIZE);
							if (c != null)c.drawClouds(batch, deltaTime,1);
						}
					batch.end();
					camera.zoom = player.zoomLevel/(miniMap.parallaxNearRatio+.51f);
					camera.update();
					batch.setProjectionMatrix(camera.combined);
					batch.begin();
					for (int i = -1; i < 2; i++)
						for (int j = -1; j < 2; j++){
							Chunk c = gMap.chunkPool.getChunkWorld(player.controllingMob.x+i*Punk.CHUNKSIZE, player.controllingMob.y+j*Punk.CHUNKSIZE);
							if (c != null)c.drawClouds(batch, deltaTime,2);
						}
					batch.end();
					camera.zoom = player.zoomLevel/(miniMap.parallaxNearRatio+.33f);
					camera.update();
					batch.setProjectionMatrix(camera.combined);
					batch.begin();
					for (int i = -1; i < 2; i++)
						for (int j = -1; j < 2; j++){
							Chunk c = gMap.chunkPool.getChunkWorld(player.controllingMob.x+i*Punk.CHUNKSIZE, player.controllingMob.y+j*Punk.CHUNKSIZE);
							if (c != null)c.drawClouds(batch, deltaTime,3);
						}
					batch.end();
					//batch.begin();
					//break;
				}
			
				
				
				
			}
			
			camera.zoom = player.zoomLevel;
			tmpV.set(0,1);
			camera.up.set(tmpV.x, tmpV.y, 0);
			
			transTmp.set(player.head.position.x, player.head.position.y, 0);
			camera.position.set(camera.position.tmp().lerp(transTmp, .5f));
			//float headx = MathUtils.floor(camera.position.x * 16)*Punk.PIXELSIZE, heady = MathUtils.floor(camera.position.y * 16)*Punk.PIXELSIZE;
			//camera.position.set(headx, heady, 0);
			

			camera.update();
			//camera.apply(gl);
			batch.getProjectionMatrix().set(camera.combined);

			transTmp.set(0,0,0);
			camera.unproject(transTmp);
			endPointTmp.set(PRESX, PRESY, 0);
			camera.unproject(endPointTmp);
			//Gdx.app.log("main", "bottom-right corner:"+endPointTmp.x+","+endPointTmp.y);
			batch.begin();
			
			renderMap(world, batch, transTmp, endPointTmp, monsterIndex, camera);
			
			batch.end();

			batch.begin();
			
			Chunk.drawDoors(batch);
			//batch.enableBlending();
			
			drawPlayer();
			drawMonsters();
			//batch.dia

			////Gdx.app.log("render", "player pos:"+player.position);
			gMap.explosionPool.draw(camera, batch, monsterIndex, deltaTime);
			gMap.itemPool.drawItems(camera, batch, monsterIndex);
			Player.particles.draw(batch,  deltaTime);
			batch.end();

			if (player.isAiming && gameMode == 1){
				//draw aiming line
				drawAimingLine(true);
				//aimerArrowMesh.render(GL10.GL_LINE_LOOP);
			}
			if (player.isPlacingBlock && placeTargetBlock.x != -1 && placeTargetBlock.y != -1) drawPlacingBlockLine();
			if (DEBUGRENDER)renderer.render(world, camera.combined);
			while (blockHighlightQ.size > 0){
				drawBlockHighlight(blockHighlightQ.pop());
			}
			while (invalidBlockHighlightQ.size > 0){
				drawInvalidBlockHighlight(invalidBlockHighlightQ.pop());
			}
			if (drawAngle){
				drawAimingLine(lineSrc, lineDest);
				drawAngle = false;
			}
			while (directionArrowQ.size > 0){
				float a = directionArrowQ.pop(), y = directionArrowQ.pop(), x = directionArrowQ.pop();;
				drawDirectionArrow(x,y,a);//(blockHighlightQ.pop());
			}
			batch.getProjectionMatrix().setToOrtho2D(0f,0f,RESX,RESY);
			batch.begin();
			drawUI();
			//Table.drawDebug(stage);
			if (gameMode == 66)font.draw(batch, loadingStr, 40,100);
			//drawExplosions();
		break;
		
		
		case 30://text input to sign
			camera.zoom = player.zoomLevel;
			camera.position.set(player.head.position.x, player.head.position.y, 0);
			
			camera.update();
			//camera.lookAt(player.head.position.x, player.head.position.y, 0);
			transTmp.set(0,0,0);
			camera.unproject(transTmp);
			endPointTmp.set(RESX, RESY, 0);
			camera.unproject(endPointTmp);
			batch.getProjectionMatrix().set(camera.combined);
			batch.setColor(1,1,1,.5f);
			batch.begin();
			renderMap(world, batch, transTmp, endPointTmp, monsterIndex, camera);
			batch.end();
			batch.getProjectionMatrix().setToOrtho2D(0f,0f,RESX,RESY);
			batch.begin();
			font.drawMultiLine(batch, signText, 0, RESY-Punk.TILESIZE*2, RESX, HAlignment.CENTER);
			break;
		} 
			
		batch.end();
		
		//camera.zoom = 1f;
		//batch.getProjectionMatrix().setToOrtho2D(0f,0f,RESX,RESY);
		//camera.position.set(0,0,0);
		camera.update();
		//camera.apply(gl);
		//stage.setCamera(camera);
		//stage.setViewport(RESX, RESY, false);
		//if (gameMode >=16 && gameMode < 80)
			stage.draw();
			Table.drawDebug(stage);
		
		
		//Gdx.app.log("mainp2", "batch:"+batch.getProjectionMatrix());
		// render the world using the debug renderer
		//camera.projection.setToOrtho2D(0f,0f,25f,15f);
		camera.update();
		//camera.apply(gl);
		gl.glLineWidth(1f);//*/
		//player.update(gMap, world, delta, gTime, monsterIndex);
				
		//updatePhysics(world,gMap);
		
		
	}
	
	protected abstract void drawAimingLine(Vector2 lineSrc2, Vector2 lineDest2);
	protected abstract void drawChunkMap() ;
	protected abstract void renderMiniMap() ;
	protected abstract void adjustSkyColor() ;
	ShaderProgram shader;
	String loadingStr = "Processing...";
	//public void load(SpriteBatch mainBatch, Preferences pref,)
	
	static PauseableThread thread, timedThread;
	public class UpdateThread implements Runnable{
		@Override
		public void run() {
			gMap.updateThreadedFetch(player);
			//Gdx.app.log(TAG, "up");
		}
	}
	
	public class TimedUpdateThread implements Runnable{
		@Override
		public void run() {
			if (gMap.chunkPool.updateTimed())timedThread.onPause();
			//Gdx.app.log(TAG, "timed update thread");
		}
	}
	
	public void create (Preferences pref, Start start, PunkBodies monsterIndex) {
		thread = new PauseableThread(new UpdateThread());
		thread.setPriority(1);
		Timer.schedule(new Task(){

			@Override
			public void run() {
				
				//Gdx.app.log(TAG, "timer");
			}
			
		}, 0f, .25f);
		timedThread = new PauseableThread(new TimedUpdateThread());
		timedThread.setPriority(4);
		//thread.stopThread();
		//thread.start();
		//logs Log.v("start", "generating world");
		this.monsterIndex = monsterIndex;
		this.starter = start;
		//getGraphics();
		//prefs = pref;
		if (starter == null)Gdx.app.log("main", "starter null!");
		
		gl = Gdx.app.getGraphics().getGL20();
		genString = new StringBuilder();
		genString.setLength(0);
		genString.append("Map generation progress:");
		//camera follows player
		camera = new OrthographicCamera(BWIDTH, BHEIGHT);
		UICamera = new OrthographicCamera(BWIDTH, BHEIGHT);
		camera.position.set(64, 64, 0);
		renderer = new Box2DDebugRenderer();
		//renderer.batch = batch;
		//Gdx.input.addProcessor(this);
		//Gdx.input.setInputProcessor(inputMultiplexer);
		world = new World(new Vector2(0, -10), true);
		BodyDef bodyDef = new BodyDef();
		groundBody = world.createBody(bodyDef);
		//createPhysicsWorld(world);
		createTextureWorld(world);

		gTime = 0;
		nextAITime = gTime + 1000;
		gl.glClearColor(0,0,0,0);
		gl.glLineWidth(Punk.TILESIZE/8);
	
		Gdx.input.setCatchBackKey(true);
		getPrefs();
		Gdx.graphics.setVSync(false);
		
		aimerLineMesh  = new Mesh(false,2,2,
				new VertexAttribute(Usage.Position, 2, "a_pos"),
				new VertexAttribute(Usage.ColorPacked, 4, "a_color"), 
				new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoords"));
		aimerArrowMesh  = new Mesh(false,3,3,
				new VertexAttribute(Usage.Position, 3, "a_pos"),
				new VertexAttribute(Usage.ColorPacked, 4, "a_color"));
		
		fingerMesh  = new Mesh(false,touchHighlightSides+3,touchHighlightSides*2+6,
				new VertexAttribute(Usage.Position, 2, "a_pos"),
				new VertexAttribute(Usage.ColorPacked, 4, "a_color"), 
				new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoords"));
		//stage, menu
		//new Mesh(null, DEBUGRENDER, AITicker, AITicker, null);
		stage = new Stage(PRESX, PRESY, false);
		inputMultiplexer = new InputMultiplexer();
		
		
		inputMultiplexer.addProcessor(stage);
		inputMultiplexer.addProcessor(this);

		
		//
		batch = stage.getSpriteBatch();
		//stage.getRoot().debug = true;
		///batch.getProjectionMatrix().setToOrtho2D(0f,0f,RESX,RESY);
		makeMenuActors();
		
		Player.particles = new ParticlePool(monsterIndex);
		
		
		String vertexShader = "attribute vec4 a_position; \n" + "attribute vec4 a_color;\n" //+ "attribute vec2 a_texCoord0;\n"
				+ "uniform mat4 u_worldView;\n" + "varying vec4 v_color;" //+ "varying vec2 v_texCoords;"
				+ "void main() \n" + "{ \n" + " v_color = vec4(1, 1, 1, 1); \n"
				//+ " v_texCoords = a_texCoord0; \n" 
				+ " gl_Position = u_worldView * a_position; \n"
				+ "} \n";
				String fragmentShader = "#ifdef GL_ES\n" + "precision mediump float;\n" + "#endif\n" + "varying vec4 v_color;\n"
				//+ "varying vec2 v_texCoords;\n" + "uniform sampler2D u_texture;\n" 
						+ "void main() \n"
				+ "{ \n" + " gl_FragColor = v_color;\n"
				+ "}";

				shader = new ShaderProgram(vertexShader, fragmentShader);
				if (shader.isCompiled() == false) {
					Gdx.app.log("ShaderTest", shader.getLog());
					Gdx.app.exit();
				}
		
		
	}

	public void dispose () {		
		//player.saveToDisk();
		//gMap.saveCurrentChunk();
		starter.mode = 1;
		if (renderer != null)renderer.dispose();
		world.dispose();
		if (batch != null)batch.dispose();
		renderer = null;
		world = null;
		mouseJoint = null;
		hitBody = null;
	}

	public void getPrefs(){
		//prefs = Gdx.app.getPreferences("MMprefs");
		//prefs_buttons.value = (prefs.getInteger("buttonsOn"));
		//prefs_buttons.
		//prefs_
		//prefs_difficulty = prefs.getInteger("difficulty");
		//prefs_zoomBtn.value = prefs.getInteger("zoomBtn");
		//prefs_zoomBtn
		//prefs_sound.value = prefs.getInteger("soundOn");
		//prefs_sound
		
		//prefs_music.value = prefs.getInteger("musicOn");
		//prefs_music
		//prefs_place_mode.value = prefs.getInteger("placeMode");
		//prefs_place_mode
		//Gdx.app.log(TAG, "prefs");
		prefs_buttons = new Setting("buttonsOn", prefs, "Buttons: Off", "Buttons: On")
		; prefs_sound = new Setting("soundOn", prefs, "Sound: Off", "Sound: On")
		//, prefs_zoomBtn = new Setting("zButtons: On", "zButtons: Off")
		; prefs_music = new Setting("musicOn", prefs, "Music: Off", "Music: On")
		; prefs_background = new Setting("backgroundOn", prefs, "Background: Off", "Background: On")
		; prefs_place_mode = new Setting("placeMode", prefs, "Place Mode: Raycast", "Place Mode: Drag and Tap", "Place Mode: Tap");
		; prefs_debug = new Setting("debugbuttons", prefs, "Debug Buttons: On", "Debug Buttons: Off");

		
		if (prefs_sound.value == 1) PunkBodies.volume = monsterIndex.defaultVolume;
		else PunkBodies.volume = 0;
	}
	boolean portalFirst = false, debugTextOn = false;;
	
	@Override public boolean keyDown (int keycode) {
		
		switch (keycode)
		{
		case Keys.C:gMap.makeHumanCity(player.x, player.y, 0);
		break;
		case Keys.B:DEBUGRENDER = !DEBUGRENDER;break;
		case Keys.NUM_1:
				gMap.chunkActors.mobPool[0].createMonster(player.x+2, player.y+1, 1, 0, 0, 3, world, monsterIndex, player, gMap);

				
			break;
		case Keys.NUM_2:
			//quit
				//gMap.saveAllChunks();
				//player.saveToDisk();
				saveGame(true);
				gMap.chunkPool.clear();
				monsterIndex.music.stop();
				if (prefs_music.value == 1) monsterIndex.menuMusic.play();
				starter.newG.showMenuButtons();
				closeMenu();
				//main.starter.setScreen(main.starter.newG);
				starter.newG.resetValues();
				gMap.chunkPool.clear();
				starter.setScreen(starter.newG);
				
				
				break;

		
		case Keys.NUM_3:
			player.body.setTransform(0,Chunk.getGroundHeight(0), 0);
		break;
		case Keys.I:
			debugTextOn = !debugTextOn;
			break;
		case Keys.NUM_4:
			starter.setScreen(this);
		break;
		case Keys.NUM_5:

		break;
		//case Keys.NUM_9:MemInfoDesktop.returnMegs = 91;
		//	break;
		//case Keys.NUM_0:MemInfoDesktop.returnMegs = 66;
		//	break;
		case Keys.NUM_7:
				portalFirst = !portalFirst;
				Portal port;
				if (portalList.size <2 ){
					port = new Portal();
					
					port.type = 0;//MathUtils.random(2);
					
					if (portalList.size == 1){
						//port.target = portalList.get(0);
						//portalList.get(0).target = port;
					}
					
					portalList.add(port);
				}
				else{
					port = portalList.get(portalFirst?0:1);
					portalList.get(1).target = portalList.get(0);
					portalList.get(0).target = portalList.get(1);
				}
				
				port.position.set(player.x, player.y);
				
				Gdx.app.log(TAG, ""+portalList.get(0).target);
			break;
		case Keys.NUM_8:
			
			break;
		case Keys.NUM_9:
			
			break;
		case Keys.NUM_0:
			
			break;
		case Keys.Z:
				//gMap.chunkActors.trySpawnGenericMob(player.x+1, player.y, gMap.getBlock(player.x+1, player.y-1), world, monsterIndex, player, gMap);

			break;
		case Keys.M:
			if (player.zoomLevel > 1.5f) player.zoomLevel = 1f; else player.zoomLevel = Chunk.primeMaterialPlane.wWidth/10f;
			break;
		case Keys.N:
				//miniMap.makeMesh(player.x<<CHUNKBITS, player.gameInfo.gameSeed);
			break;
		case Keys.G:
			Punk.gameMode = 66;
			Punk.processingInc = 0;
			Punk.processing = true;
			Punk.saveAllQueued = true;
			break;
		//case Keys.V:
		//	break;
			
		case Keys.Y:
			//gMap.updater.logging = !gMap.updater.logging;
			break;
			
		case Keys.D:checkForButtonOverridePress(3);
			break;
		case Keys.A:
			checkForButtonOverridePress(0);
			break;
		case Keys.W:
			checkForButtonOverridePress(1);
			//if (player.climbButtonUpValid)climbUpPressed = true;
		//else
			player.jump(1, gMap, world, monsterIndex, gameMode, gTime); 
					
			break;
		case Keys.S:checkForButtonOverridePress(2);
			break;
		case Keys.P : player.body.setLinearVelocity(new Vector2(0,224));
			break;
		case Keys.F: player.body.setLinearVelocity(tmp.set(player.body.linVelWorld.x*5, player.body.linVelWorld.y+9));
		break;
		
		
		case Keys.V:player.body.setTransform(player.x+.5f, Chunk.getGroundHeight(player.x), 0);
		break;
		case Keys.AT:player.body.setLinearVelocity(new Vector2(100, 3));	
		break;
		case Keys.L:player.body.setLinearVelocity(new Vector2(-100, 3));	
		break;
		
		case Keys.E:player.zoomLevel += .25f;
		break;
		case Keys.Q:player.zoomLevel  -= .25f;
		break;
		case Keys.O: //Gdx.app.log("main", "up:"+gMap.updater.blockUpdateList.max+"li:"+gMap.updater.lightUpdateList.max+"day:"+gMap.updater.dayLightUpdateList.max);
		break;
		////case Keys.M:openMessageWindow("testing jhljkljljkjljkjl k jkhlkjlyuymjhb uty dt rs sfe gfj");
		//break;
			
		case Keys.H:player.health+=4;
		break;
		/*case Keys.X:for (int i = 0; i < gMap.chunkActors.zombiePool.monsterList.size; i++){
			Gdx.app.log("main", "zombie location:"+gMap.chunkActors.zombiePool.monsterList.get(i).position);
		}
		break;*/
		case Keys.J:Gdx.app.log("main", "player"+player.position+" hand"+player.hand.position);
			break;
		
		case Keys.ESCAPE:
		
		case Keys.BACK: 
			if (gameMode < 16 || gameMode >= 80) openInv();else
			{
				//Punk.btnHelp.mode = -1;
				closeInv();
				//player.controllingMob.touchBelt(player.controllingMob.activeInvSlot);
				/*closeConfirm();
				closeCrafting();
				closeMenu();
				//
				closePerks();
				openBelt();
				if (beltHidden) closeBelt(false);
				//if (gameMode != 19)
					//changeBeltSlot(player.activeQuickSlot);
				backToGameTimer = Punk.STAGEDURATION;
				backToGamePending = true;
				Punk.myRequestHandler.hide();*/
			}
			
		break;
		case Keys.MENU: //beltHidden = !beltHidden;
				menuPressed = true;
			break;
		case Keys.T:
				//gMap.chunkActors.spawnZombie(player.x+5, player.y+2, world, monsterIndex);
				Player.particles.blood(player.position.x, player.position.y+1);
			break;
		case Keys.U:
			break;
		
		case Keys.F12:
		break;
		case Keys.F11:
		break;
		
		
		}

		return false;
	}

	@Override public boolean keyTyped (char character) {
		if (gameMode == 30){
				if(character == '\b' && signText.length() >= 1) {
					signText = signText.substring(0, signText.length()-1);
	        } else if(character == '\n' ) {
	                Gdx.input.setOnscreenKeyboardVisible(false);
	        } else {                
	                signText += character;
	        }
        
		}
		//System.out.println(character);
		return false;
	}

	@Override public boolean keyUp (int keycode) {
		switch (keycode)
		{
		//case Keys.D: player.stopRunning();
		//	break;
		case Keys.MENU: if (menuPressed)//beltHidden = !beltHidden;
							doMenuButton();
			//menuPressed = true;
		break;	
		case Keys.U:case Keys.J:
		break;
		case Keys.W:checkForButtonOverrideUnPress(1);
			player.isJumping = false;
			climbUpPressed = false;
			player.isSwimmingUp = false;
		break;
		case Keys.A:checkForButtonOverrideUnPress(0);
		break;	
		case Keys.D:checkForButtonOverrideUnPress(3);//player.stopRunning(gTime, gMap, world);
		break;
		
		case Keys.S:climbDownPressed = false;
		}
		//player.state = 0;
		
		//System.out.println("up:" + keycode);
		return false;
	}

	/** we instantiate this vector and the callback here so we don't irritate the GC **/
	Vector3 testPoint = new Vector3();
	QueryCallback callback = new QueryCallback() {
		@Override public boolean reportFixture (Fixture fixture) {
			// if the hit point is inside the fixture of the body
			// we report it
			if (fixture.testPoint(testPoint.x, testPoint.y)) {
				hitBody = fixture.getBody();
				return false;
			} else
				return true;
		}
	};
	public void cancelTouchDown(int pointer){
		cancelButtonOverrides();
		
		if (false)switch (gameMode)
		{
		
		case 80://flight
			player.isThrusting = false;
		
		break;
		
		case 1: 
				player.isAiming = false;	
				player.isThrowing = false;
			break;
		case 8: 
				player.isPlacingBlock = false;		
			break;
		
		case 2:
				
							
			break;
		case 3:				
				player.isDigging = false;
				
			break;
		case 4: 
			player.isAiming = false;
			player.isThrowing = false;
			break;
		case 5:
			player.isSlinging = false;
			player.isAiming = false;
			break;
		case 6:
				player.isShooting = false;
			break;
		case 7:
			player.isPlacingBlock = false;
		break;
		case 9:
			player.isShootingWand = false;
			break;
		case 81:
		case 11:
			player.isAimingPoi = false;
			player.isPostPoi = false;
			player.poi.deactivate();
			player.shootTimer = gTime;
			break;
		case 12:
			player.isAimingAxe = false;
			player.shootTimer = gTime;
			break;
		case 17://craft
		case 27://craft conf
		
		case 10:
			
		break;
			
			
		}//switch
	}
	
	
	private void cancelButtonOverrides() {
		// TODO Auto-generated method stub
		
	}

	int numberOfFingers = 0;
	 int fingerOnePointer;
	 int fingerTwoPointer;
	 float firstDistance = 0;
	 float firstZoom = 1;
	 Vector3 fingerOne = new Vector3();
	 Vector3 fingerTwo = new Vector3();
	
	
	 Group menuGroup;
	 
	public boolean checkForButtonOverridePress(int i) {
		player.controllingMob.pressed[i] = true;
		return false;
	}

	public boolean checkForButtonOverrideUnPress(int i) {
		player.controllingMob.pressed[i] = false;
		return false;
	}
		
	@Override public boolean touchDown (int x, int y, int pointer, int button) {
		//String crash = null;
		//Gdx.app.log("main", "TouchDown");
		
		pointerTimer[pointer] = screenTime;
		if (gameMode >= 16 && gameMode < 80){
			
			
			
			return false;
		}
		
		/*int slot = slotInBelt(x,y);
		
			
		if (slot != -1){
			pointerAction[pointer] = 70;
			checkForButtonOverridePress(7);
			player.controllingMob.touchBelt(slot);
			return true;
		}*/
		
		
		if (touchInRunL(x,y) ){
			//check for overrides
			pointerAction[pointer] = 32;
			checkForButtonOverridePress(0);
			
			
			
			return true;
		}
		if (touchInButton2L(x,y)){	
			pointerAction[pointer] = 65;
			if (checkForButtonOverridePress(1));// return true;
							
			//player.jump(0, gMap, world, monsterIndex, gameMode, gTime);			
			return true;
		}
		
		if (touchInButton3L(x,y)){
			pointerAction[pointer] = 23;
			checkForButtonOverridePress(2);
			
		}
		
		if (touchInRunR(x,y)){
			pointerAction[pointer] = 33;
			checkForButtonOverridePress(3);
			
			

			return true;
		}		
		if (touchInButton2R(x,y)){
			pointerAction[pointer] = 66;
			if (checkForButtonOverridePress(4));// return true;
				
				
				//player.jump(0, gMap, world, monsterIndex, gameMode, gTime);
			
			
			return true;
		}
		if (touchInButton3R(x,y)){
			pointerAction[pointer] = 22;
			checkForButtonOverridePress(5);
			if (player.isGrappling){
				player.destroyRGrapple();
				return true;
			}
		
			else if (player.climbButtonUpValid){
				
				climbUpPressed = true; 
				return true;
			}
		}
		
		
		actualTouchLoc.set(x,y);
		
		
		/*if (touchInInvButton(x,y)){
			doInvButton();
			pointerAction[pointer] = 16;
			Gdx.app.log("main", "inv button touch");
			return true;
		}
		if (gameMode == 19 || gameMode == 26){ //item picker
			pointerAction[pointer] = 19;
			doItemPickerTouch(x,y);
			return true;
		}		
			
		
		
		if (touchInActionButton(x,y)) {
			doActionButton();
			pointerAction[pointer] = 34;

			return true;
		}*/
		
		/*if (touchInPlayer(x,y) && gameMode < 15 ) {
			pointerAction[pointer] = 65;
			player.playerPressed = true;
			if (gMap.getBlock(player.x, player.y-1).blockType() == 2 || gMap.getBlock(player.x, player.y-1).blockType() == 4 ) player.isClimbing = true;
			if (gameMode < 16)return true;
		}*/
		
		/*if (touchInBeltMover(x,y)){
			pointerAction[pointer] = 63;
			return true;
		}*/
		/*if (beltHidden && touchInBeltHider(x,y)){
			pointerAction[pointer] = 24;
			beltHidden = !beltHidden;
			if (beltHidden) closeBelt();
			else openBelt();
			Gdx.app.log("main", "belthider");
			return true;
		}*/
		
		/*if (touchInWeaponButton(x,y)){
			pointerAction[pointer] = 21;
			//weaponSlotOpen = true;
			return true;
		}*/
		
		
		
		/*if (touchInBelt(x,y) && gameMode < 16 && !beltHidden)
		{
			touchSlotDown = slotInBelt(x,y);
			//touchSlotDown = (x - BUTTONBUFFER) / BELTBUFFERX;
			System.out.println("touch on belt");
			if (gameMode < 16)pointerAction[pointer] = 64;
			else {
				pointerAction[pointer] = gameMode;//inv etc
				touchSlotDown = getTouchSlot(x,y);
			}
			
		} *///screen touch
			{
				
				/*if (pointersOnScreen == 0){
					pointersOnScreen = 1;
					first_pointer = pointer;
				}
				else if (pointersOnScreen == 1){//pinch
					pointersOnScreen = 2;
					second_pointer = pointer;
					initialDistance = tmpV.set(x,y).dst(Gdx.input.getX(first_pointer), Gdx.input.getY(first_pointer));
					initialZoomLevel = player.zoomLevel;
					cancelTouchDown(first_pointer);
					pointerAction[first_pointer] = 28;
					return true;
				}*/
			numberOfFingers++;
			if(numberOfFingers == 2)
			{
				cancelTouchDown(fingerOnePointer);
			    fingerTwoPointer = pointer;
			    fingerTwo.set(x, y, 0);
			       
			    float distance = fingerOne.dst(fingerTwo);
			    firstDistance = distance;
			    firstZoom = player.zoomLevel;
			    return true;
			}
			
			if(numberOfFingers == 1)
			{
			       fingerOnePointer = pointer;
			       fingerOne.set(x, y, 0);
			}
			 
				/*pointersOnScreen = 0;
				for (int i = 0; i > 7; i++){
					if (Gdx.input.isTouched(i) && i != pointer){
						pointersOnScreen=1;
						first_pointer = i;
					}
				}
				if (pointersOnScreen == 1){//pinch
					pointersOnScreen = 2;
					second_pointer = pointer;
					initialDistance = tmpV.set(x,y).dst(Gdx.input.getX(first_pointer), Gdx.input.getY(first_pointer));
					initialZoomLevel = player.zoomLevel;
					cancelTouchDown(first_pointer);
					pointerAction[first_pointer] = 28;
					return true;
				}*/
			
					actualTouchLoc.set(x,y);
					touchLoc.set((x-PRADIUSX)/(float)PRADIUSY, (PRADIUSY - y)/(float)PRADIUSY);
					setTouchInfo(touchLoc, pointer);
					//touchLoc.set((x-PRADIUSX)/(float)PRADIUSX, (PRADIUSY - y)/(float)PRADIUSY);
					//setTouchInfo(touchLoc, pointer);
					//worldTouchLoc.set(transTmp.x, transTmp.y);
					pointerAction[pointer] = gameMode;
					///touchSlotDown = (x - BUTTONBUFFER) / BELTBUFFERX;
		//on screen touch:
					
					//belt touch down
					
					if (false) switch (gameMode)
					{
					case 80:
						if (!player.isGliding && player.canGlide())player.startGliding(world);
						
						player.isThrusting = true;
						flyTouchLoc.set(actualTouchLoc);
						setFlyTouchAngle();
						break;
					//case 4: player.isAimingGrapple = true;
					case 81:
					case 11:
						player.isAimingPoi = true;
						player.isPostPoi = false;
						player.lastPoiDistance = 100;
						break;
					case 15:
					case 1:
						if (player.headTarget != player) ;//player.resetHead();
					else {
							player.isAiming = true;
							player.shootTimer = screenTime;
							player.aimStrength = 1;
					}
							//player.isLeft = (touchLoc.x >0.1f);
						break;
					case 13:
							player.resetHead();
							player.isAimingBow = true;
							player.shootTimer = screenTime;
							player.aimStrength = 1;
						break;
					//player.isLeft = (touchLoc.x >0.1f);
					
					case 8: 
							player.isPlacingBlock = true;
					
							player.isLeft = touchLeftOfPlayer;
						break;
					case 2:
							
						break;
					
					case 3: 
							if (!player.isDigging)
						{	transTmp.set(x,y,0);
							camera.unproject(transTmp);
							targetedBlockX = (int)transTmp.x;
							targetedBlockY = (int)transTmp.y;
							player.isDigging = true;
							player.digTimeout = gTime+100;
						}
							player.isLeft = touchLeftOfPlayer;
						break;
					
						
					case 5:
						player.isSlinging = true;
						//player.aimTimer = gTime + 100;
						player.isAiming = true;
						//player.aimTimer = gTime + PLAYERAIMTIME;
						player.aimStrength = 1;
						break;
					case 6:
							player.isShooting = true;
							//player.shootTimer = gTime+player.getShootTime();
							player.isLeft = !touchLeftOfPlayer;
						break;
					case 7:
						player.isPlacingBlock = true;
						player.isLeft = touchLeftOfPlayer;
					break;
					case 9:
						//player.isAiming = true;
						player.isShootingWand = true;
						//touchLoc.set((x-PRADIUSX)/(float)PRADIUSX, (PRADIUSY - y)/(float)PRADIUSY);
						//setTouchInfo(touchLoc);
						player.wandParticle = Player.particles.grenadeParticle(player.hand.position.x, player.hand.position.y, monsterIndex.playerWands[player.activeWand].particleIndex);
						player.shootTimer = Punk.gTime + monsterIndex.playerWands[player.activeWand].minVelocity;
						break;
					case 12:
						//Gdx.app.log("main", "start aiming axe");
						player.isAimingAxe = true;
						player.shootTimer = gTime;
						if (player.isAxeing){
							player.destroyAxe(world);
						}
						
						break;
					case 17://craft
					case 27://craft conf
					case 18:
							
						break;
					case 16:
							touchSlotDown = getTouchSlot(x,y);
							touchLoc.set((x-PRADIUSX)/(float)PRADIUSX, (PRADIUSY - y)/(float)PRADIUSY);
						break;
					
					case 10:
						
					break;
						
						
					}//switch
		}
		if (!paused){
			pointerAction[pointer] = 69;
			//checkForButtonOverridePress(6);
		
			float vx = touchLoc.x*touchScalar,vy = touchLoc.y * touchScalar;
			player.controllingMob.touchLoc.set(vx,vy);
			//player.controllingMob.angle = player.controllingMob.touchLoc.angle();
			player.controllingMob.screenTouched = true;
		}
		touchX = x;
		touchY = PRESY-y;
		//player.Run(1);
		//System.out.println(""+player.getPos());
		//player.resetHead();
		return false;
	}
	

	//private ButtonOverride[] activeButtonOverrides = new ButtonOverride[Punk.BELTSLOTCOUNT];
	/*private boolean checkForButtonOverridePress(int type) {
		//Gdx.app.log(TAG, "pressed "+type);
		//0==left run, 2=left clibm
		//		4 = r run 5 = r climb
		for (int i = 0; i < Punk.BELTSLOTCOUNT; i++){
			Item item = player.inventory.getItem(i);;
			ItemInfo itemI = PunkBodies.getItemInfo(item.id, item.meta);
			
			if (itemI.buttonOverrides[type] != null && itemI.buttonOverrides[type].press(gMap, player, monsterIndex)){
				activeButtonOverrides[type] = itemI.buttonOverrides[type];
				return true;
			}
		}
		return false;
	}
	
	private boolean checkForButtonOverrideUnPress(int type) {
		//Gdx.app.log(TAG, "unpressed "+type);
		//0==left run, 2=left clibm
		//		4 = r run 5 = r climb
		//for (int i = 0; i < Punk.BELTSLOTCOUNT; i++){
			//Item item = player.inventory.getItem(i);;
			//ItemInfo itemI = PunkBodies.getItemInfo(item.id, item.meta);
			
			if (activeButtonOverrides[type] != null){
				activeButtonOverrides[type].unPress(gMap, player, monsterIndex, false);
				activeButtonOverrides[type] = null;
				return true;
			}
		//}
		return false;
	}
	
	private boolean updateButtonOverrides() {
		//0==left run, 2=left clibm
		//		4 = r run 5 = r climb
		//for (int t = 0; t < 6; t++)
		for (int i = 0; i < 6; i++){
			Item item = player.inventory.getItem(i);;
			ItemInfo itemI = PunkBodies.getItemInfo(item.id, item.meta);
			
			if (activeButtonOverrides[i] != null){
				activeButtonOverrides[i].update(player, gMap, world, monsterIndex);
				//return true;
			}
		}
		return false;
	}

	protected Vector2 flyTouchLoc = new Vector2();
	private void cancelButtonOverrides() {
		for (int i = 0; i < 6; i++){
			Item item = player.inventory.getItem(i);;
			ItemInfo itemI = PunkBodies.getItemInfo(item.id, item.meta);
			
			if (activeButtonOverrides[i] != null){
				activeButtonOverrides[i].unPress(gMap, player, monsterIndex, false);
				activeButtonOverrides[i] = null;
				//return true;
			}
		}
		
	}*/
	protected Vector2 flyTouchLoc = new Vector2();
	public void setFlyTouchAngle() {
		
		
		transTmp.set(flyTouchLoc.x, flyTouchLoc.y, 0);
		camera.zoom = Player.zoomLevel;
		camera.update();
		//camera.apply(gl);
		camera.unproject(transTmp);
		Player.flyTouchAngle = tmpV.set(player.position).add(0,Player.EYEHEIGHT).sub(transTmp.x, transTmp.y).mul(-1).angle();;
		
	}

	boolean touchLeftOfPlayer;
	public static Vector2 actualTouchLoc = new Vector2(), screenSpaceTouch = new Vector2();;
	void setTouchInfo(Vector2 touchLoc, int pointer) {
		//transTmp.set(PRESX/2+(touchLoc.x*PRESX)/2, 
		//		PRESY/2-(touchLoc.y*PRESY)/2 , 0);
		transTmp.set(actualTouchLoc.x, actualTouchLoc.y, 0);
		screenSpaceTouch.set((actualTouchLoc.x / PRESX) * RESX, RESY-( (actualTouchLoc.y / PRESY) * RESY));
		camera.zoom = Player.zoomLevel;
		camera.update();
		//camera.apply(gl);
		camera.unproject(transTmp);
		//camera.un
		//camera.un
		tmpV.set(player.position).add(0,Player.EYEHEIGHT).sub(transTmp.x, transTmp.y).mul(-1);
		adjustedTouchAngle = tmpV.angle();
		targetBlockV.set(transTmp.x, transTmp.y);
		targetBlock.set(targetBlockV.x, targetBlockV.y);
		touchLeftOfPlayer = (targetBlockV.x < player.position.x);//(transTmp.x < player.position.x);//(tmpV.x <0);
		if (player.isGliding && gameMode == 80 && pointer != -1 && pointerAction[pointer] == 80){
			flyTouchLoc.set(actualTouchLoc);
			setFlyTouchAngle();
		}
		
	}
	
	/** another temporary vector **/
	Vector2 target = new Vector2();	
	float touchScalar = .8f;
	
@Override public boolean touchDragged (int x, int y, int pointer) {
	
	if (pointerAction[pointer] == 69){
		actualTouchLoc.set(x,y);
		touchLoc.set((x-PRADIUSX)/(float)PRADIUSY, (PRADIUSY - y)/(float)PRADIUSY);
		setTouchInfo(touchLoc, pointer);
			
		//Gdx.app.log("main", "touchBlockV:"+touchBlockV);

		float vx = touchLoc.x*touchScalar,vy = touchLoc.y * touchScalar;
		player.controllingMob.touchMoved = true;
		player.controllingMob.touchLoc.set(vx,vy);
		//player.controllingMob.angle = player.controllingMob.touchLoc.angle();
	}
		//System.out.println(""+slingLoc.angle());
		//player.slingScale = touchLoc.len()*3;
		
	if (pointer == fingerOnePointer) {
	        fingerOne.set(x, y, 0);
		}
		if (pointer == fingerTwoPointer) {
	        fingerTwo.set(x, y, 0);
		 }
	
	 float distance = fingerOne.dst(fingerTwo);
	 float factor = distance / firstDistance;
	 
	 if (numberOfFingers == 2){
		if (firstDistance > distance) {
			player.zoomLevel = firstZoom / factor;
			player.zoomLevel = Math.max(player.zoomLevel, player.minZoomLevel);
			player.zoomLevel = Math.min(player.zoomLevel, player.maxZoomLevel);
			//lastDistance = distance;
		 } else {
			 player.zoomLevel = firstZoom / factor;
			 player.zoomLevel = Math.max(player.zoomLevel, player.minZoomLevel);
			player.zoomLevel = Math.min(player.zoomLevel, player.maxZoomLevel);
			//lastDistance = distance;
		 }
	 }
		
		/*if (pointersOnScreen == 2){//pinch
			float currentDistance =
					tmpV.set(Gdx.input.getX(second_pointer),Gdx.input.getY(second_pointer)).dst(Gdx.input.getX(first_pointer), Gdx.input.getY(first_pointer));
			player.zoomLevel = initialZoomLevel * (initialDistance / currentDistance);
			player.zoomLevel = Math.max(player.zoomLevel, player.minZoomLevel);
			player.zoomLevel = Math.min(player.zoomLevel, player.maxZoomLevel);
		}*/
	 
	 	//draggged
	 	
	 
	 
		
		if (false)switch (pointerAction[pointer]){
		case 1:
		case 6:
		case 4:
			player.isLeft = (!touchLeftOfPlayer);
			break;
		case 8:
		case 9:
		case 3:
		case 7:
			player.isLeft = (!touchLeftOfPlayer);
			break;
		case 81:
		case 11:
			/*tmpV.set(player.position).add(tmpV.tmp().set(touchLoc).nor().mul(3f));
			tmpV.y+=1f;
			player.poiAngle = touchLoc.angle();
			if (player.isAimingPoi)
				player.poi.body.setTransform(tmpV, 0);
			player.isLeft = (touchLoc.x >0.1f);*/
			player.isLeft = (!touchLeftOfPlayer);
			break;
		case 16:/* if (getTouchSlot(x,y) == -1){
			gameMode = 1;
			player.isAiming = true;
			pointerAction[pointer] = 1;
			if (touchSlotDown != -1)player.activeQuickSlot = touchSlotDown;}*/
			//player.isShooting = true;
		break;
		case 64://belt drag, change ammo if it's a weapon
			
		break;
		
		case 2:
			
			
			break;
		case 80:
			
			break;
		}
		
		
		
		//touchDragging = true;
		touchX = x;
		touchY = RESY - y;
		return false;
	}
	public void doTouchUp(int x, int y, int pointer){
		player.draggedR = false;
		player.draggedL = false;
		//really only need location stuff for inventory
		//pointersOnScreen -= 1;//TODO
		//Gdx.app.log("touch", "touchUP started, pointerAction:" + pointerAction[pointer]);
		
		// for pinch-to-zoom           
		 if(numberOfFingers == 1)
		 {
		        Vector3 touchPoint = new Vector3(x, y, 0);
		        camera.unproject(touchPoint);
		 }
		 numberOfFingers--;
		 
		// just some error prevention... clamping number of fingers (ouch! :-)
		 if(numberOfFingers<0){
		        numberOfFingers = 0;
		 }
		firstDistance = 0;
		
		
		switch (pointerAction[pointer])
		{
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
		case 10:
		case 11:
		case 81:
		case 12:
		case 13:
		case 14:
		
		case 16:
		case 26:
		case 17:
		case 18:
		case 27:
		case 15:
		case 80:
			if (checkForButtonOverrideUnPress(6));
			//doScreenTouchUp(x,y,pointer);
	//	System.out.println("GLLLLLLLLLLLLDSFADJKSDJLKDSAFJLKDSAFLKJFDSAKJFDSALK");

			break;
		case 32:
			if (checkForButtonOverrideUnPress(0));
			
		break;
				
		case 33:if (checkForButtonOverrideUnPress(3)); 
		
			break;
	
		case 34://action btn
			//Gdx.app.log("main", "zoom button touchedup");
				/*if (prefs_zoomBtn.value == 1 && zoomPressed && zoomTimer > gTime - 200){
					player.zoomLevel +=.15f;
					player.zoomLevel = Math.max(player.zoomLevel, player.minZoomLevel);
					player.zoomLevel = Math.min(player.zoomLevel, player.maxZoomLevel);
				}
				zoomPressed = false;*/
			
				/*player.isClimbing = false;
				player.isFallingThroughBridge = false;*/
				
		break;
		
		case 22: if (checkForButtonOverrideUnPress(2));//break; 
			
			climbUpPressed = false;
			break;
		case 23: 
			if (checkForButtonOverrideUnPress(5));//break; 
			climbDownPressed = false;
			break;
		case 63: if (touchInBeltMover(x,y)) beltOnTop = !beltOnTop;
		case 64://belt
				//if on belt. only need to do something if the same button was pressed down last
				touchSlotUp = slotInBelt(x,y);
				//System.out.println("changing mode$$$$$$$$$$$$$$$$"+touchSlotUp);
	
				if (touchSlotUp == touchSlotDown)
				{
					changeBeltSlot(touchSlotUp, false);
				}
				
			break;
		case 65:
			if (checkForButtonOverrideUnPress(1));//break; 
			player.isJumping = false;
				player.isSwimmingUp = false;
			break;
		case 66:
			if (checkForButtonOverrideUnPress(4));//break; 
			player.isJumping = false;
				player.isSwimmingUp = false;
			break;
		case 69:
			
			checkForButtonOverrideUnPress(6);
			player.controllingMob.screenTouched = false;
			break;
		}
		
		touchX = -40;
		touchY = -40;//so hacky!
		touchSlotDown = -1;
		
		player.isPlacingBlock = false;
	}
	
//	public boolean doScreenTouchUp(int x, int y, int pointer){
//		//not on buttons
//		
//		//touch up
//		
//		switch (pointerAction[pointer]){//corresponds with gameMode
//		case 80://flight
//				player.isThrusting = false;
//			
//			break;
//		
//		case 11:
//			if (pointerTimer[pointer]+Player.POIPREDELAY < screenTime){
//				if (player.isAimingPoi){
//					player.lastPoiTargetDistance = 100;
//					player.isAimingPoi = false;
//					player.isAiming = false;
//					player.isPoiing = true;
//					player.isPrePoi = true;
//					//launch poi
//					tmpV.set(player.poi.position);
//			
//					float breakTime = 0;
//					float speed = 8;
//					Block tmpB = gMap.getBlock(MathUtils.floor(digTargetBlock.x), MathUtils.floor(digTargetBlock.y));
//					
//					ToolInfo activeTool = (ToolInfo) player.activeTool;
//					boolean noTarget = false;
//					switch (tmpB.blockType()){//check break type here = monsters, dirt, stone, etc. needs a callback.
//					//case 0://mob
//					//case 1:
//					//	noTarget = true;
//					//	break;
//					case 68:
//					case 66://sand/dirt
//						breakTime = activeTool.speedVDirt * tmpB.getBlockHP();
//
//						break;
//					case 2://wood/trees
//						breakTime = activeTool.speedVWood * tmpB.getBlockHP();
//
//						break;
//					default://everything else - stone
//						breakTime = activeTool.speedVStone * tmpB.getBlockHP();
//
//						break;
//					}
//						
//					
//					speed = player.poi.position.tmp().add(0,0).dst(MathUtils.floor(digTargetBlock.x)+.5f, MathUtils.floor(digTargetBlock.y)+.5f) / breakTime;
//					//Gdx.app.log("main", "break time: "+breakTime + " speed " + speed + "dist "+player.position.tmp().add(0,Player.EYEHEIGHT).dst(targetBlock) + " target" + targetBlock);
//					//Gdx.app.log("main", "break Time:" + breakTime);
//					player.poi.body.setGravityScale(0);
//					player.poi.body.setLinearVelocity(
//							tmpV.sub(digTargetBlock).nor().mul(-1).mul(speed)
//							);
//					//player.poi.body.getFixtureList().get(0).s;
//					player.poiIsUp = (tmpV.y > 0);
//					player.poiIsLeft = (tmpV.x < 0);
//					//player.poi.body.setType(BodyType.DynamicBody);
//					//player.poi.body.getFixtureList().get(0).setFilterData();//TODO
//					monsterIndex.playFlailSound();
//					
//				}
//				
//			} else {
//				player.isAimingPoi = false;
//				player.isPoiing = false;
//				player.isPrePoi = false;
//				player.shootTimer = gTime+50;
//			}
//		
//		break;
//		case 81:
//			if (pointerTimer[pointer]+Player.POIPREDELAY < screenTime){
//				if (player.isAimingPoi){
//					player.lastPoiTargetDistance = 100;
//					player.isAimingPoi = false;
//					player.isAiming = false;
//					player.isPoiing = true;
//					player.isPrePoi = true;
//					//launch poi
//					tmpV.set(player.poi.position);
//
//					float speed = player.activeTool.speed;
//					player.targettingMob = true;
//					digTargetBlock.set(player.position).add(0, Player.EYEHEIGHT).add(tmp.set(5,0).rotate(adjustedTouchAngle+180));
//
//					player.poi.body.setGravityScale(0);
//					player.poi.body.setLinearVelocity(
//							tmpV.sub(digTargetBlock).nor().mul(-1).mul(speed)
//							);
//					player.poiIsUp = (tmpV.y > 0);
//					player.poiIsLeft = (tmpV.x < 0);
//					player.poi.body.setType(BodyType.DynamicBody);
//					//player.poi.body.getFixtureList().get(0).setFilterData();//TODO
//					monsterIndex.playFlailSound();
//					
//				}
//				
//			} else {
//				player.isAimingPoi = false;
//				player.isPoiing = false;
//				player.isPrePoi = false;
//				player.shootTimer = gTime+50;
//			}
//			
//		break;
//		case 15:
//		case 4:
//		case 1 : 	
//		
//				if (player.isAiming && pointerTimer[pointer]+Player.PREDELAY < screenTime){
//					player.throwAngle = adjustedTouchAngle;
//					//boolean throwLeft = (270 < player.throwAngle && player.throwAngle > 90);
//					player.handTarget.set(Player.THROW_REACH,0);
//					player.handTarget.rotate(player.throwAngle+180).add(0,1);
//					player.throwLength = Math.min(touchLoc.len(), 1);
//					player.isThrowing = true;
//					player.isAiming = false;
//					player.lastHandDist = 990;
//				} else{
//					player.isAiming = false;
//				}
//				
//			
//					
//				break;
//		
//		case 2:
//			//Gdx.app.log("main", "place block"+player.isPlacingBlock+" "+targetBlock);
//			placeBlockAction.touchUp(x, y, player, gMap, world, monsterIndex);		
//
//			
//			break;
//		
//		case 3: player.isDigging = false;
//				player.isDoneDigging = false;
//				System.out.println("digging over");
//				lastTarget.x = 0;//to reset progress when you let go
//				//player.isDigging = false;
//				//player.lastx = player.x-2;
//				//player.update(gMap, world, deltaTime, gTime);
//			break;
//		
//		case 5://slinging
//				player.isSlinging = true;
//				player.animTimer = gTime + 500;
//				//bulletPool.getBullet().shoot(touchLoc, player.aimStrength,1, player, world);
//				//player.isSlinging = false;
// 			break;
//		case 6: player.isShooting = false;
//			break;
//		case 7://placing special items, buckets etc
//			if (player.isPlacingBlock)
//				if (placeTargetBlock.x != 0 && placeTargetBlock.y != 0 
//						&& player.inventory.getItemAmount(player.activeQuickSlot) > 0 
//						&& gMap.getBlock((int)placeTargetBlock.x, (int)placeTargetBlock.y).blockType() < 3)
//				{
//					switch (player.inventory.getItemID(player.activeQuickSlot)){
//					case 310://bucket
//						//Gdx.app.log("gameMode 7 up", "bucket");
//							if (player.inventory.getItemMeta(player.activeQuickSlot) == 1){
//								player.inventory.setItemMeta(player.activeQuickSlot, 0);
//								gMap.changeBlock((int)placeTargetBlock.x, (int)placeTargetBlock.y, 
//										12, 
//										0, true);
//								
//							}else if (player.inventory.getItemMeta(player.activeQuickSlot) ==2){
//								player.inventory.setItemMeta(player.activeQuickSlot, 0);
//								gMap.changeBlock((int)placeTargetBlock.x, (int)placeTargetBlock.y, 
//										-3, 
//										0, true);
//								
//							}
//							
//							else{
//								if (gMap.getBlock((int)placeTargetBlock.x, (int)placeTargetBlock.y).blockID == 12 && gMap.getBlock((int)placeTargetBlock.x, (int)placeTargetBlock.y).meta == 0 ){
//									gMap.changeBlock((int)placeTargetBlock.x, (int)placeTargetBlock.y, 0, 0, true);
//									player.inventory.setItemMeta(player.activeQuickSlot, 1);
//									//Gdx.app.log("gameMode 7 up", "filling bucket, water");
//								} else
//									if (gMap.getBlock((int)placeTargetBlock.x, (int)placeTargetBlock.y).blockID == -3 && gMap.getBlock(MathUtils.floor(placeTargetBlock.x), MathUtils.floor(placeTargetBlock.y)).meta == 0 ){
//										gMap.changeBlock((int)placeTargetBlock.x, (int)placeTargetBlock.y, 0, 0, true);
//										player.inventory.setItemMeta(player.activeQuickSlot, 2);
//										//Gdx.app.log("gameMode 7 up", "filling bucket, lava");
//										
//									}
//								
//							}
//							for (int i = -1; i <=1;i++)
//								for (int j = -1;j<=1;j++)
//									gMap.addTimedUpdate(MathUtils.floor(placeTargetBlock.x+i), MathUtils.floor(placeTargetBlock.y+j));
//							break;
//					case 43://sign
//							//break if player standing in different chunk
//							if (player.x % Punk.CHUNKSIZE == (long)(placeTargetBlock.x) % Punk.CHUNKSIZE) break;
//							//find first available key
//							int signID = 0;
//							//while (gMap.chunkC.info.signs.containsKey(signID)) signID++;
//							//if (signID >= 32) break;//TODO extend intmap for more
//							//gMap.changeBlock(MathUtils.floor(targetBlock.x), MathUtils.floor(targetBlock.y), 43, signID);
//							activeSignLoc.set(MathUtils.floor(placeTargetBlock.x), MathUtils.floor(placeTargetBlock.y));
//							//selectedSign = signID;
//							signText = "";
//							gameMode = 30;
//						break;
//					}
//					
//					player.isPlacingBlock = false;
//					
//					//player.inventory.useUpItem(activeQuickSlot);
//				}
//			break;
//		/*case 8: //planting
//			if (player.isPlacingBlock)
//				if (targetBlock.x != 0 && targetBlock.y != 0 
//						&& player.inventory.getItemAmount(player.activeQuickSlot) > 0 
//						)
//				{
//					switch (player.inventory.getItemID(player.activeQuickSlot)){
//					case 313:
//						//Gdx.app.log("gameMode 8 up", "plant bean");
//							
//								if (gMap.plantBlock((int)targetBlock.x, (int)targetBlock.y, 
//										45, 0, world, monsterIndex))
//								player.inventory.useUpItem(player.activeQuickSlot);
//						break;	
//								
//								
//							
//					}
//				}
//					player.isPlacingBlock = false;
//					player.isDonePlacingBlock = false;
//		break;*/
//		case 9: player.isShootingWand = false;
//				player.wandParticle.allowCompletion();
//			break;
//		case 10:
//			if (player.inventory.getItemID(player.activeQuickSlot) >= 450)
//			{//food
//				if (player.health < player.maxHealth()){
//					player.health = Math.min(player.health+3, player.maxHealth());
//					player.inventory.useUpItem(player.activeQuickSlot);
//					//Gdx.app.log("changebeltslot", "eating");
//					PunkBodies.playHealSound(1);
//				}
//			} else if (player.inventory.getItemID(player.activeQuickSlot) == 33 || player.inventory.getItemID(player.activeQuickSlot) == 34){//mushrooms
//				player.poisonDamage -= 6;
//				player.inventory.useUpItem(player.activeQuickSlot);
//				PunkBodies.playHealSound(2);
//				//Gdx.app.log("changebeltslot", "eating mushroom");
//				if (player.poisonDamage < -32){//limit is also in PA update
//					//init trip
//					gameMode = 19;
//					//gMap.lightQueue.clear();
//					//gMap.resetAllLightMaps((byte)16);
//					//gMap.makeATorch(player.x, player.y);
//					//gMap.placedBlocks.clear();//TODO record this
//					player.isTripping = true;
//					//populateFloatyItems(player.gameInfo, false);
//					monsterIndex.playTripSound();
//				}
//			}
//			break;
//		
//		case 12:
//			//Gdx.app.log("main", "CHOP");
//			if (player.isAimingAxe && player.shootTimer+Player.PREDELAY < gTime){
//				player.shootTimer = gTime;
//				player.isAxeing = true;
//				player.isPostAxe = false;
//				//TODO create axe, start motor etc.
//				player.createAxe(world, monsterIndex, adjustedTouchAngle);
//			}
//			player.isAimingAxe = false;
//			break;
//		case 27://craft conf
//			
//				touchSlotUp = slotInMenu(x,y);
//				//Gdx.app.log("crafting conf", "MENU SLOT:"+touchSlotUp);
//				
//					//only actions in certain slots
//					//Gdx.app.log("craftingConf", "got click"+craftingRecipeMax);
//					switch (touchSlotUp){
//					//bottom button
//					case 5://craft maximum
//							if (player.inventory.hasFreeSlot()&& craftingRecipeMax > 1){
//								player.inventory.craftItem
//								(player.inventory.rb.validRecipes.get(selectedRecipe),
//										craftingRecipeMax);
//								gameMode = 16;
//							}
//							//craftingRecipeCount = player.inventory.rb.getValidSize(player.inventory);
//							
//						break;
//						
//					//middle button
//					case 7://craft half
//						if (player.inventory.hasFreeSlot() && craftingRecipeMax > 1){
//							player.inventory.craftItem
//							(player.inventory.rb.validRecipes.get(selectedRecipe),
//									craftingRecipeMax/2);
//							gameMode = 16;
//						}
//						//craftingRecipeCount = player.inventory.rb.getValidSize(player.inventory);
//					
//						break;
//					
//						
//						
//						
//					//top button
//					case 9://craft one
//						if (player.inventory.hasFreeSlot()){
//							player.inventory.craftItem
//							(player.inventory.rb.validRecipes.get(selectedRecipe),
//									1);
//						}
//						//craftingRecipeCount = player.inventory.rb.getValidSize(player.inventory);
//						gameMode = 16;
//						
//					break;
//					
//					//cancel button
//					case 1:
//							craftingRecipeCount = player.inventory.rb.getValidSize(player.inventory);
//							gameMode = 17;
//						break;
//					}//switch for slots
//			break;
//			
//		case 18://System  menu
//			touchSlotUp = slotInMenu(x,y);
//			if (touchSlotUp == touchSlotDown){
//				//Gdx.app.log("menu", "SLOT TOUCHED:"+touchSlotUp);
//				
//				}
//			
//			
//			break;
//		case 17://craft
//				//selectedRecipe = touchSlotDown/2;
//			
//				touchSlotUp = slotInMenu(x,y);
//				if (touchSlotUp == touchSlotDown){
//					selectedRecipe = touchSlotUp;
//					//Gdx.app.log("main", "craftingrecipecount = "+craftingRecipeCount);
//					//Gdx.app.log("crafting", "selected recipe:"+selectedRecipe + "gm:" + craftingRecipeCount);;
//					if (selectedRecipe < craftingRecipeCount && selectedRecipe >= 0){
//						gameMode = 27;
//						
//					}
//				}
//				
//			break;
//		
//	
//		case 65: //if (player.isClimbing)player.isClimbing = false;//TODO check if this works
//			break;
//		
//		}
//		return false;
//		
//	}
	@Override public boolean touchUp (int x, int y, int pointer, int button) {
		

		doTouchUp(x,y,pointer);
		
		
		return false;
	}
	@Override
	public boolean mouseMoved(int x, int y) {
		// TODO Auto-generated method stub
		return false;
	}
	public void drawItemUI() {
		
		switch (gameMode){
		
		}
		
	}
	
	
	@Override public boolean scrolled(int amount) {
		player.zoomLevel *= 1+amount/4f;
		player.zoomLevel = Math.max(player.zoomLevel, player.minZoomLevel);
		player.zoomLevel = Math.min(player.zoomLevel, player.maxZoomLevel);
		return false;
	}
	
	@Override public void hide(){
		
	}
	@Override public void show(){
		Gdx.input.setInputProcessor(inputMultiplexer);
	}
	@Override
	public void pause() {
		//Gdx.app.log("punkesque", "PAUSE");
		//saveGame();
		numberOfFingers = 0;
		saveGame(true);
		zoomPressed = false;
		climbDownPressed = false;
		climbUpPressed = false;
	}
	
	public void resume() {
		
	}
	
	public void resize(int width, int height) {
		Punk.getGraphics();
	}
	
	
	
	public boolean touchInBelt(int x, int y){
		if (beltOnTop) return (
		y < PBELTBUFFERY &&
		x > PBELTOFFSETX &&
		x < PBELTOFFSETX+PBELTBUFFERX*4
		
		
		);//else 
		return (
				y > PRESY-PBELTBUFFERY &&
				x > PBELTOFFSETX &&
				x < PBELTOFFSETX+PBELTBUFFERX*4
				
				
				);
	}
	public int slotInBelt(int tx, int ty){
		/*
		
		float x = (PRESX / BELTSLOTCOUNT) * beltXa, w = PRESX/BELTSLOTCOUNT, h = PRESY * beltYa;
		if (ty > h) return -1;
		for (int i = 0; i < BELTSLOTCOUNT; i++){
			
			
			//Item item = player.controllingMob.inv.getItem(i);
			float c = PRESX/2;
			float x2 = c+(-3+i)*x,  h2 = Math.min(w, h);
			Gdx.app.log(TAG, "x2  "+x2+"  ");
			if (tx > x2 && tx < x2+x) return i;
			//monsterIndex.beltSelected9.draw(batch, x2,y2,h2,h2);//Gdx.app.log(TAG, "belt"+x2+y2+h2);
			
		}*/
		return -1;
	}
	public int slotInWeaponButton(int x, int y){
		if (beltOnTop) {
			if (y < PBELTBUFFERY) return -1;
			if (y < PBELTBUFFERY*2) return -2;
			if (y < PBELTBUFFERY*3) return -3;
			return -4;
		}
		if (y < RESY-PBELTBUFFERY) return -1;
		if (y < RESY-PBELTBUFFERY*2) return -2;
		if (y < RESY-PBELTBUFFERY*3) return -3;
		return -4;
			
		
	}
	public boolean touchInWeaponButton(int x, int y){
		if (beltOnTop) return (
				y < PBELTBUFFERY &&
				x > PBELTOFFSETX-PBELTBUFFERX &&
				x < PBELTOFFSETX
		); 
		return (
				y > RESY - PBELTBUFFERY &&
				x > PBELTOFFSETX-PBELTBUFFERX &&
				x < PBELTOFFSETX
		);
	}
	
	public boolean touchInBeltHider(int x, int y){
		return (y < PBELTBUFFERY && x < PBELTOFFSETX);
		/*return 	y < PBELTBUFFERY && 
				x > PBELTOFFSETX+PBELTBUFFERX+PBELTBUFFERX/2 &&
				x < PBELTOFFSETX+PBELTBUFFERX*2+PBELTBUFFERX/2;
			
		/*return 	y < PBELTBUFFERY+PBELTBUFFERY/2 && 
				y > PBELTBUFFERY &&
				x > PBELTOFFSETX-PBELTBUFFERX/2 &&
				x < PBELTOFFSETX+PBELTBUFFERX/2;*/
		
	}
	
	public boolean touchInBeltMover(int x, int y){
		if (beltOnTop) return (
				y < PBELTBUFFERY &&
				x > PBELTOFFSETX+PBELTBUFFERX*4 &&
				x < PBELTOFFSETX+PBELTBUFFERX*5
		); 
		return (
				y > RESY - PBELTBUFFERY &&
				x > PBELTOFFSETX+PBELTBUFFERX*4 &&
				x < PBELTOFFSETX+PBELTBUFFERX*5
		);
	}
	/*public boolean touchInPlayer(int x, int y){
		transTmp.set(player.position.x, player.position.y, 0);
		camera.project(transTmp, 0, 0, PRESX, PRESY);
		return (
			x > transTmp.x - JUMPBUTTONSIZE-10*prefs_jumpBtn &&
			x < transTmp.x + JUMPBUTTONSIZE + 10*prefs_jumpBtn &&
			y > PRESY-transTmp.y - JUMPBUTTONSIZE - 10*prefs_jumpBtn&&
			y < PRESY-transTmp.y + JUMPBUTTONSIZE + 10*prefs_jumpBtn
		);
	}*/
	public boolean touchInActionButton(int x, int y){
		return (
				x >PRESX-PBUTTONBUFFER && y < PBUTTONBUFFER
		);
	}
	public boolean touchInMenu(int x, int y){
		return (y > PRESY-PBELTBUFFERY*6);
		//12 buttons
	}
	public boolean touchInInvButton(int x, int y){
		return (
				x < PBUTTONBUFFER && y < PBUTTONBUFFER
		);
	}
	public boolean touchInButton3R(int x, int y){
		return (
				x >PRESX-PBUTTONBUFFER && y > PRESY-PBELTBUFFERY*6

		 );
	}
	
	public boolean touchInButton3L(int x, int y){
		return (
				x < PBUTTONBUFFER && y>PRESY-PBELTBUFFERY*6
		);
	}
	
	public boolean touchInButton2R(int x, int y){
		return (
				x >PRESX-PBUTTONBUFFER && y > PRESY-PBELTBUFFERY*4

		 );
	}
	public boolean touchInButton2L(int x, int y){
		return (
				x < PBUTTONBUFFER && y>PRESY-PBELTBUFFERY*4
		);
	}
	
	public static boolean touchInRunR(int x, int y){
		return (
				x >PRESX-PBUTTONBUFFER && y > PRESY-PBELTBUFFERY*2

		 );
	}
	public static boolean touchInRunL(int x, int y){
		return (
				x < PBUTTONBUFFER && y>PRESY-PBELTBUFFERY*2
		);
	}
	public static float beltXa = 1f, beltYa = .15f;
	public static final int BELTSLOTCOUNT = 6;
	
	public MobRayCast mobRay = new MobRayCast();
	public class MobRayCast implements RayCastCallback{

		@Override
		public float reportRayFixture(Fixture fixture, Vector2 point,
				Vector2 normal, float fraction) {
			PhysicsActor hitAct = (PhysicsActor)fixture.getBody().getUserData();
			if (hitAct == null) return -1;
			tmpV.set(player.position).add(0, Player.EYEHEIGHT);
			if ((hitAct.actorID == 3 || hitAct.actorID == 10 || hitAct.actorID == 11 || hitAct.actorID == 55) && tmpV.dst(point) < tmpV.dst(digTargetBlock)){
				digTargetBlock.set(point);
				Player.targettingMob = true;
				return -1;
			}
			return -1;
		}
		
	}
	
	
	
	
}

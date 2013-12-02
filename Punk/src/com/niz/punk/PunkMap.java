package com.niz.punk;

import java.io.BufferedInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;
import java.util.zip.Deflater;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.JointEdge;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.PauseableThread;
import com.niz.punk.Punk.RayType;
import com.niz.punk.PunkMap.BlockDamageType;
import com.niz.punk.blocks.Automata;

public class PunkMap {
	static String creativeGameName = "default";
	// public Chunk chunkL, chunkC, chunkR, chunkT, chunkB, chunkTL, chunkTR,
	// chunkBL, chunkBR;
	public static Deflater deflater = new Deflater(Deflater.BEST_SPEED);
	public SpawnStrategy customSpawnStrategy = null;
	public static boolean allChunksLoaded = false;
	public static boolean allChunksPostFetched = false;
	public boolean sunlightDone = false;
	public boolean sunlightPending = false;;
	public static int currentChunk, currentPlane = 0;
	public static int currentChunkHeightID;
	public int CHUNKOFFSET = currentChunk * Punk.CHUNKSIZE, CHUNKOFFSETY = 0,
			CHUNKTOPY = Punk.CHUNKSIZE;
	public int RIGHTMOSTBLOCK, LEFTMOSTBLOCK, TOPBLOCK, BOTTOMBLOCK;
	// private Block tmpB = new Block(0,0);

	
	// private String saveLoc = "";//tmp

	// private BufferedOutputStream saveOutputStream, monOutputStream,
	// infoOutputStream;
	// private BufferedInputStream loadInputStream, monInputStream,
	// infoInputStream;
	//public static int gameID;
	protected int gameSeed;
	private String TAG = "MAP";
	//public static int current_gameType;// = current_gameType.CREATIVE;
	// private FileOutputStream fos;
	// private byte[][] savedata = new byte[Punk.CHUNKSIZE*2][Punk.CHUNKSIZE];
	public ItemPool itemPool = new ItemPool();
	//public BlockUpdater updater;
	// public byte[][] lightMap = new byte[Punk.CHUNKSIZE][Punk.CHUNKSIZE];
	//public byte[][] flareTable = new byte[9][9];
	//public byte[][] torchTable = new byte[33][33];
	//public byte[][] fireTable = new byte[25][25];
	public Vector2 tmpV = new Vector2(0, 0);

	public enum DayTime {
		DAY, SUNSET, NIGHT, SUNRISE, START
	};

	public static DayTime timeOfDay = DayTime.START;
	public BChunkActors chunkActors;
	public boolean PARTICLESON = true;
	//public RayBulletCallback rayCall;
	// public IntArray lightQueue;
	public PExplosion explosionPool;// = new PExplosion();
	private int lightProgress = 0;
	//public byte[][] targetLightMap = new byte[Punk.CHUNKSIZE][Punk.CHUNKSIZE];
	// public PunkBlockList lightFadeQueue;
	//public Array<BlockLoc> doneFadeQueue = new Array<BlockLoc>();
	//public PunkBlockList placedBlocks = new PunkBlockList();

	//public IntArray sunlightQueue = new IntArray();
	private Player l_player;
	public PunkInventory tripInv = new PunkInventory(l_player);
	public RecipeBook tripRecs;
	private World l_world;
	private PunkBodies l_mi;
	//public float skyTarget;
	private Camera l_camera;
	//private MiniMap miniMap;
	private Vector3 tmpV3 = new Vector3();
	public Preferences prefs;
	public boolean isWaitingForSurvival = false,
			isWaitingForPsychonaut = false;

	private DestroyBlockQuery destroyBlockQuery;
	// public DayTime queuedTime;
	private RevoluteJointDef jd = new RevoluteJointDef();
	public static boolean allTreesGrown = false;
	public static boolean openWorld, dungeonMode;

	private int mapY, mapTreeI, mapLeafX, mapLeafY;
	private int mapX;
	public static int minChunkRange = 3, maxChunkRange = 5;
	private final int chunkFetchRepeats = 1;
	public static Chunk nullChunk;
	public ChunkPool chunkPool;// = new ChunkPool();
	private final int BLOCKUPDATEREPS = 2200000;

	public void start(boolean openWorld, int id) {
		this.openWorld = openWorld;
		dungeonMode = false;
		Punk.genericAirBlock.set(0, 0);
		// Chunk.reset();
		//updater.dayLightUpdateList.clear();
		//updater.lightUpdateList.clear();
		Player player = l_player;
		currentChunkHeightID = player.gameInfo.savedPosition.y / Punk.CHUNKSIZE;
		currentChunk = (player.gameInfo.savedPosition.x / Punk.CHUNKSIZE);
		// System.out.println("starting map" + currentChunk + " height: " +
		// currentChunkHeightID);
		updateState = 0;
		//current_gameType = player.gameInfo.gameType;
		// Gdx.app.log("punkMap", "initializing chunks in constructor");
		// queueLightRayUpdates();
		recalculatePositionConstants(player);
		player.leftLoad = CHUNKOFFSET - 10;
		player.rightLoad = CHUNKOFFSET + Punk.CHUNKSIZE + 10;
		player.topLoad = CHUNKTOPY + 10;
		player.bottomLoad = CHUNKOFFSETY - 10;
		Chunk.seed = player.gameInfo.gameSeed;
		//gameID = player.gameInfo.gameID;

		// Gdx.app.log("map", "reloading all chunks");
		if (openWorld) {
			//updater.lightUpdateList.clear();
			//updater.dayLightUpdateList.clear();
			startLoadAllChunks(currentChunk, currentChunkHeightID, player);
		} else if (id == 1) {// creative

			recalculatePositionConstants(player);
			// fetchCreativeChunk(chunkC);
			// loadEverything(player);
			// allChunksLoaded = true;
			// allChunksPostFetched = true;
		} else {
			// chunkC.setForMiniGame(true);
			// chunkC.setAsMiniGame(this, id);
			// chunkT.setForMiniGame(true);
			// chunkTL.setForMiniGame(true);
			// chunkTR.setForMiniGame(true);
			// chunkL.setForMiniGame(true);
			// chunkR.setForMiniGame(true);
			// chunkB.setForMiniGame(true);
			// chunkBL.setForMiniGame(true);
			// chunkBR.setForMiniGame(true);

			recalculatePositionConstants(player);
			loadEverything();
			// allChunksLoaded = true;
			// allChunksPostFetched = true;
			// allChunks
		}

		// loadEverything(player);
		gameSeed = player.gameInfo.gameSeed;

	}
	
	public PunkMap(Player player, BossHandler bHandler,
			World world, PunkBodies monsterIndex, Camera camera, boolean isFirst) {
		
		
		blockMoverPool = new BlockMoverPool();
		//this.miniMap = miniMap;
		//updater = new BlockUpdater(this, world, monsterIndex);
		explosionPool = new PExplosion();
		CorneredSprite.makeLookup();
		
		// GenericMob.enemyCallback = new EnemyLook(this);
		//rayCall = new RayBulletCallback();
		// lightQueue = new IntArray(Punk.CHUNKSIZE);
		l_world = world;
		l_mi = monsterIndex;
		l_player = player;
		chunkPool = new ChunkPool(l_player, this);
		l_camera = camera;
		chunkActors = new BChunkActors(monsterIndex, world, this, player);
		chunkActors.init(23, tmpV.set(CHUNKOFFSET, 0), world, monsterIndex);
		// int spawnOffset =
		// Chunk.getGroundHeight(player.gameInfo.spawnPosition.x,
		// player.gameInfo.gameSeed), solidTot = 0;
		// spawnOffset +=50;
		// Gdx.app.log("map", "spawn offset:"+spawnOffset);
		//queuedChanges.ordered = true;

		ChunkPool.enc.init();
		ChunkPool.encBG.init();
		populateChunks();
		//for (int i = 0; i < 100000; i++)
		//	PunkBlockList.pool.free(PunkBlockList.pool.obtain());
		/*
		 * chunkR = new Chunk(current_gameType); chunkL = new
		 * Chunk(current_gameType); chunkC = new Chunk(current_gameType); chunkT
		 * = new Chunk(current_gameType); chunkB = new Chunk(current_gameType);
		 * chunkTR = new Chunk(current_gameType); chunkBR = new
		 * Chunk(current_gameType); chunkTL = new Chunk(current_gameType);
		 * chunkBL = new Chunk(current_gameType);
		 * 
		 * queuedSaveChunks[0] = new Chunk(current_gameType);
		 * queuedSaveChunks[1] = new Chunk(current_gameType);
		 * queuedSaveChunks[2] = new Chunk(current_gameType);
		 */
		/*
		 * fetchChunk(currentChunk, currentChunkHeightID, chunkC, player);
		 * fetchChunk(currentChunk-1, currentChunkHeightID,chunkL, player);
		 * fetchChunk(currentChunk+1, currentChunkHeightID,chunkR, player);
		 * fetchChunk(currentChunk, currentChunkHeightID+1, chunkT, player);
		 * fetchChunk(currentChunk, currentChunkHeightID-1, chunkB, player);
		 * chunkR.setGenQueue(true); chunkL.setGenQueue(true);
		 * chunkC.setGenQueue(true); chunkT.setGenQueue(true);
		 * chunkB.setGenQueue(true); /* for (int i = 0; i < Punk.CHUNKSIZE; i++)
		 * chunkC.fetchAColumn(player.gameInfo.gameSeed);
		 */

		// Gdx.app.log("map", "making tables");

		/*
		 * BufferedOutputStream os = new
		 * BufferedOutputStream(Gdx.files.external("test.rle").write(false));
		 * ChunkPool.enc.clear(); Block b = new Block(0,0); for (int i = 0; i <
		 * 20; i++){ b.set(i/4, i/5); ChunkPool.enc.add(b); Gdx.app.log(TAG,
		 * "adding "+b); } ChunkPool.enc.finish(); while
		 * (ChunkPool.enc.hasNext){ int i = ChunkPool.enc.next();
		 * Gdx.app.log(TAG, "writing "+i); try { os.write(i); } catch
		 * (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } } try { os.close(); } catch (IOException e) {
		 * // TODO Auto-generated catch block e.printStackTrace(); }
		 * 
		 * BufferedInputStream is = new
		 * BufferedInputStream(Gdx.files.external("test.rle").read());
		 * Chunk.dec.set(is); for (int i = 0; i < 30; i++){ int r =
		 * Chunk.dec.get(); Gdx.app.log(TAG, i+": "+r); }
		 */

		//makeTorchTable();
		//makeFireTable();
		//makeFlareTable();

		// Gdx.app.log("map", "making recipes&monuments");

		tripRecs = new RecipeBook();
		tripRecs.addMonuments();

		// lightUpdateList.list.ordered = false;

		prefs = Gdx.app.getPreferences("MMprefs");
		int valid = prefs.getInteger("validGameTypes");
		if (valid == 0)
			isWaitingForSurvival = true;

		// Gdx.app.log("map", "done initializing map, doing randomx");

	}

	private void populateChunks() {
		// TODO Auto-generated method stub
		for (int x = -minChunkRange; x <= minChunkRange; x++)
			for (int y = -minChunkRange; y <= minChunkRange; y++) {
				chunkPool.createChunk();
			}
		nullChunk = new Chunk(true);
	}

	public void loadEverything() {
		// Gdx.app.log("main", "load everything");
		// long tim = System.nanoTime();
		// allChunksLoaded = false;
		// allChunksPostFetched = false;
		// while (!allChunksLoaded || !allChunksPostFetched){
		// for (int i = 0; i < Punk.CHUNKSIZE*10; i++){
		// updateFetch(l_player);
		// checkPostFetch(false);
		// }

		// growTrees();
		// updateTimeOfDay(l_player.gameInfo.minutes, false);
		// for (int i = 0; i < 100; i++)
		// while (updater.lightUpdateList.size() > 0 ||
		// updater.dayLightUpdateList.size() > 0)
		// updater.updateSky();
		// tim = System.nanoTime() - tim;
		// Gdx.app.log("main", "load everything done: " + (tim/1000000000));
		// while (!allChunksLoaded) update();
		// updateBlocks(-1, l_world, l_mi);
	}

	/*public void finishLight() {
		updateTimeOfDay(l_player.gameInfo.minutes, false);
		// for (int i = 0; i < 100; i++)
		// while (updater.lightUpdateList.size() > 0 ||
		// updater.dayLightUpdateList.size() > 0)
		while (!updater.lightFinished())updater.updateSky();
	}*/

	// NAME!! We have recieved a strange report from our watcher on the Other
	// World.
	// You must travel there in astral form and investigate.
	// Beware! All is not well.

	public Vector2 getFreeSpace(int x, int y) {
		// check if valid, if not go up
		if (getBlock(x, y).blockType() > 4) {
			while (!(getBlock(x, y).blockType() == 0 && getBlock(x, y + 1)
					.blockType() == 0))
				y++;

		}
		// else while (!(getBlock(x,y).blockType() == 0 &&
		// getBlock(x,y+1).blockType() == 0 && getBlock(x,y-1).blockType() >4))
		// y--;
		if (getBlock(x, y - 1).blockID == 0)
			while (getBlock(x, y - 1).blockID == 0 && y > CHUNKOFFSETY)
				y--;

		return tmpV.set(x, y);

	}

	public void removeSpawnersNear(int x, int y) {
		int r = 30;
		for (int sx = x - r; sx <= x + r; sx++)
			for (int sy = y - r; sy <= y + r; sy++) {
				if (getBlock(sx, sy).blockID == 42)
					getBlock(sx, sy).set(0, 0);
				// Gdx.app.log("map",
				// "checking spawners to remove"+getBlock(sx,sy).blockID);
			}
	}

	public void unScrubItem(long x, int y, int actorID, int actorMeta,
			int state, int itemMeta, int dur) {
		// Gdx.app.log("map",
		// "item values, id:"+actorMeta+"amount:"+state+"meta:"+itemMeta);
		itemPool.createItem(actorMeta, state, itemMeta, dur, l_world, l_mi,
				this, tmpV.set(x, y));
	}

	StringBuilder saveOutput = new StringBuilder();
	// FileHandle actorsHandle;
	Scanner scanner;

	public void unScrubActors(BufferedInputStream actorStream) {
		// Gdx.app.log("map", "unscrub actors");
		scanner = new Scanner(actorStream);
		while (scanner.hasNext()) {
			String name = scanner.next();
			// Gdx.app.log("map", "unscrub actor:"+name);

			if (name == "actor") {
				long x = scanner.nextLong();
				int y = scanner.nextInt();
				int actorID = scanner.nextInt();
				int actorMeta = scanner.nextInt();
				int state = scanner.nextInt();
				// unScrubActor(x,y,actorID, actorMeta, state);
				// TODO this does nothing atm
			} /*
			 * else if (name.contains("item")){ long x = scanner.nextLong(); int
			 * y = scanner.nextInt(); int actorID= scanner.nextInt(); int
			 * actorMeta= scanner.nextInt(); int state= scanner.nextInt(); int
			 * itemMeta = scanner.nextInt(); unScrubItem(x,y,actorID, actorMeta,
			 * state, itemMeta); }
			 */
		}
		scanner.close();
	}

	public String scrubActors(int cID, Chunk chunk) {

		saveOutput.setLength(0);
		// ropes
		/*Iterator<Rope> ropeIter = chunkActors.ropePool.list.iterator();
		// Gdx.app.log("map", "rope total:"+chunkActors.ropePool.size());
		while (ropeIter.hasNext()) {
			Rope r = ropeIter.next();
			// Gdx.app.log("map", "precrash:"+r.src.x+":"+r.src.y);
			r.src.checkPosition(l_player, 0f, this);
			r.dest.checkPosition(l_player, 0f, this);
			// check for nulls
			if (r.inChunk(cID)) {
				saveOutput.append("rope ");
				saveOutput.append(r.isRope);
				saveOutput.append(" ");
				saveOutput.append(r.src.x);
				saveOutput.append(" ");
				saveOutput.append(r.src.y);
				saveOutput.append(" ");
				saveOutput.append(r.dest.x);
				saveOutput.append(" ");
				saveOutput.append(r.dest.y);
				saveOutput.append("\n");
				
				  r.destroyLinks(); r.src.deactivate(); r.dest.deactivate();
				  chunkActors.ropePool.free(r);
				 
			}
		}// ropes*/
		Iterator<PhysicsActor> actIter = chunkActors.getMonsterList()
				.iterator();
		while (actIter.hasNext()) {
			PhysicsActor a = actIter.next();
			switch (a.actorID) {
			case 1:
				saveOutput.append("actor ");
				saveOutput.append(a.x);
				saveOutput.append(" ");
				saveOutput.append(a.y);
				saveOutput.append(" ");
				saveOutput.append(a.actorID);
				saveOutput.append(" ");
				saveOutput.append(a.actorMeta);
				saveOutput.append(" ");
				saveOutput.append(a.state);
				saveOutput.append("\n");
				break;
			}

		}
		// items
		Iterator<Item> iter = itemPool.itemList.iterator();
		while (iter.hasNext()) {
			Item item = iter.next();
			if (chunk.containsPosition(item.x, item.y)) {
				saveOutput.append("item ");
				saveOutput.append(item.x);
				saveOutput.append(" ");
				saveOutput.append(item.y);
				saveOutput.append(" ");
				saveOutput.append(item.actorID);
				saveOutput.append(" ");
				saveOutput.append(item.actorMeta);
				saveOutput.append(" ");
				saveOutput.append(item.state);
				saveOutput.append(" ");
				saveOutput.append(item.meta);
				saveOutput.append("\n");
			}
		}

		// Gdx.app.log("map", "actorstream: "+saveOutput.toString());
		return saveOutput.toString();
	}

	/*public void saveCurrentChunk() {
		chunkPool.saveChunk(
				chunkPool.getChunk(currentChunk, currentChunkHeightID), false);
	}*/

	public void saveAllChunks() {
		
		chunkPool.saveAll();
	}

	private MonumentInfo tmpMonument;
	private String monumentLoc = new String();
	private String infoLoc = new String(), actorsLoc = new String();
	// Chunk tmpChunk = new Chunk();

	//private Chunk activeFetchChunk, nearC;
	boolean fetchDone = false;
	public boolean updatePF(Player player){
		
		return false;
	}
	public boolean updateFetch222(Player player) {// return true if it finished a
												// chunk

		/*
		 * if (!openWorld) { allChunksLoaded = true; return; }
		 */
		fetchDone = false;
		Chunk c = chunkPool.unFetchedChunks.peek();
		if (c != null) {
			for (int r = 0; r < chunkFetchRepeats; r++)
				//if (c.fetchAColumn(player.gameInfo.gameSeed, this)) 
				{
					chunkPool.doPostFetch(c, this);
					fetchDone = true;
					//activeFetchChunk = null;
					return true;
				}
		} /*else {// look for new chunk to fetch
			int nearDist = 9999;
			Iterator<Chunk> iter = chunkPool.entries().iterator();
			boolean done = false;
			// Gdx.app.log(TAG, "checking chunk srats"+iter.hasNext());
			while (!done && iter.hasNext()) {
				Chunk ch = iter.next();
				if (!ch.unScrubFinished && ch.distanceFromPlayer() < nearDist) {
					nearDist = ch.distanceFromPlayer();
					nearC = ch;
					// Gdx.app.log(TAG, "fetching"+c.chunkID+"h"+c.heightID);

				}

			}
			if (nearDist == 9999) {
				allChunksLoaded = true;
				return true;
			}
			activeFetchChunk = nearC;
			if (nearDist == 0) {

			}
			return false;
		}*/

		return false;

	}
	private boolean needsQ = true;
	public int threadRuns = 0;
	private Object lock = new Object();
	public void updateThreadedFetch(Player player){
		threadRuns++;
		//if (needsQ) return;
		/*Chunk activeFetchChunk = chunkPool.unFetchedChunks.peek();
		updateFetch(player);
		if (fetchDone && !activeFetchChunk.isPostFetched) 
			if (activeFetchChunk.doPostFetch(this)){
				chunkPool.completedFetch();
				needsQ = true;
			
			}*/
			
			//Gdx.app.log(TAG, "stop thread");
	
		
		
		//does any updates/destruction/placement! immediately/first. leaving oput for now.
		/*
		 * then fetches all chunks
		 * then light.
		 * 
		 * already got placepackets and blockLocDamage for storing them
		 */
		//Gdx.app.log(TAG, "thread");
		if (chunkPool.doModifications()) return;
		//Gdx.app.log(TAG, "mods done");
		//fetching chunks is easy
		if (chunkPool.doFetch()) return;
		//Gdx.app.log(TAG, "fewtch done");
		if (chunkPool.doLight()) return;
		
		if (chunkPool.updateSave()) return;
		
		if (!chunkPool.updateFetchQueue(player)) {
			return;
		} 
		
		//light is harder, need to look at chunks
		
		//block updates are one list for the whole world
		
		//light updates are per chunk. chunks can handle lrtb connection as well.
		try {
			PauseableThread.sleep(2);
			//Gdx.app.log(TAG, "sleep");
			fetchDone = true;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void updateMain(){
		if (needsQ){
			if (!chunkPool.updateFetchQueue(l_player)) {
				saturated = true;
			} else{
				saturated = false;
				needsQ = false;
				//Punk.thread.start();
				//Gdx.app.log(TAG, "start thread");
			}
		}
	}

	public boolean checkPostFetch() {
		// Gdx.app.log("map", "checkPF");
		/*
		 * if (!chunkC.isPostFetched && chunkC.lightFinished){
		 * //Gdx.app.log("map", "before:"+chunkC.isPostFetched);
		 * doPostFetch(chunkC); //Gdx.app.log("map",
		 * "after:"+chunkC.isPostFetched); }else if (!chunkR.isPostFetched &&
		 * chunkR.lightFinished) doPostFetch(chunkR); else if
		 * (!chunkL.isPostFetched && chunkL.lightFinished) doPostFetch(chunkL);
		 * else if (!chunkT.isPostFetched && chunkT.lightFinished)
		 * doPostFetch(chunkT); else if (!chunkB.isPostFetched &&
		 * chunkB.lightFinished) doPostFetch(chunkB);else if
		 * (!chunkTR.isPostFetched && chunkTR.lightFinished)
		 * doPostFetch(chunkTR); else if (!chunkTL.isPostFetched &&
		 * chunkTL.lightFinished) doPostFetch(chunkTL); else if
		 * (!chunkBR.isPostFetched && chunkBR.lightFinished)
		 * doPostFetch(chunkBR); else if (!chunkBL.isPostFetched &&
		 * chunkBL.lightFinished) doPostFetch(chunkBL); else { if
		 * (!allChunksPostFetched && light){ addLightUpdatesOnJoins(); }
		 * allChunksPostFetched = true; // }
		 */

		Iterator<Chunk> iter = chunkPool.entries().iterator();
		boolean done = false;
		while (!done && iter.hasNext()) {

			Chunk c = iter.next();
			if (c.unScrubFinished && !c.isPostFetched) {
				chunkPool.doPostFetch(c, this);
				if (c.isPostFetched)done = true;
				// queueLightInside(c);
			}

		}
		;
		if (!done) {

			allChunksPostFetched = true;
			return true;
		}
		return false;
	}

	public boolean checkInsideLight(Chunk c) {
		
		boolean done = false;
		{
			boolean l = chunkPool.hasLeft(c) && !c.l, r = chunkPool.hasRight(c)
					&& !c.r, u = chunkPool.hasUp(c) && !c.u, d = chunkPool
					.hasDown(c) && !c.d;
			if ((l || r || u || d) && c.isPostFetched) {
				if (l && !c.l) {
					for (int i = 0; i < Punk.CHUNKSIZE; i++) {
						addLightUpdate(c.xOffset, c.yOffset + i);
						addLightUpdate(c.xOffset - 1, c.yOffset + i);
						addDayLightUpdate(c.xOffset, c.yOffset + i);
						addDayLightUpdate(c.xOffset - 1, c.yOffset + i);
						//addTimedUpdate(c.xOffset, c.yOffset + i);
						//addTimedUpdate(c.xOffset - 1, c.yOffset + i);
					}
					c.l = true;
				}

				if (r && !c.r) {
					for (int i = 0; i < Punk.CHUNKSIZE; i++) {
						addLightUpdate(c.xOffset + Punk.CHUNKSIZE - 1,
								c.yOffset + i);
						addLightUpdate(c.xOffset + Punk.CHUNKSIZE,
								c.yOffset + i);
						addDayLightUpdate(c.xOffset + Punk.CHUNKSIZE - 1,
								c.yOffset + i);
						addDayLightUpdate(c.xOffset + Punk.CHUNKSIZE,
								c.yOffset + i);
						//addTimedUpdate(c.xOffset + Punk.CHUNKSIZE - 1,
						//		c.yOffset + i);
						//addTimedUpdate(c.xOffset + Punk.CHUNKSIZE,
						//		c.yOffset + i);
					}
					c.r = true;
				}

				if (u && !c.u) {
					for (int i = 0; i < Punk.CHUNKSIZE; i++) {
						addLightUpdate(c.xOffset + i, c.yOffset
								+ Punk.CHUNKSIZE - 1);
						addLightUpdate(c.xOffset + i, c.yOffset
								+ Punk.CHUNKSIZE);
						addDayLightUpdate(c.xOffset + i, c.yOffset
								+ Punk.CHUNKSIZE - 1);
						addDayLightUpdate(c.xOffset + i, c.yOffset
								+ Punk.CHUNKSIZE);
						//addTimedUpdate(c.xOffset + i, c.yOffset
						//		+ Punk.CHUNKSIZE - 1);
						//addTimedUpdate(c.xOffset + i, c.yOffset
						//		+ Punk.CHUNKSIZE);
					}
					c.u = true;
				}

				if (d && !c.d) {
					for (int i = 0; i < Punk.CHUNKSIZE; i++) {
						addLightUpdate(c.xOffset + i, c.yOffset);
						addLightUpdate(c.xOffset + i, c.yOffset - 1);
						addDayLightUpdate(c.xOffset + i, c.yOffset);
						addDayLightUpdate(c.xOffset + i, c.yOffset - 1);
						//addTimedUpdate(c.xOffset + i, c.yOffset);
						//addTimedUpdate(c.xOffset + i, c.yOffset - 1);
					}
					c.d = true;
				}

				done = true;
				// queueLightInside(c);
			}

		}
		;
		if (!done) {

			// allChunksLightAdded = true;
			return true;
		}
		return false;
	}
	
	/*public void unScrubOres(Chunk chunk) {
		while (chunk.oreSourceList.size() > 0) {
			BlockLoc loc = chunk.oreSourceList.removeFirst();
			// make a deposit around source block.
			int oreTypeID = getBlock(loc.x, loc.y).meta;
			if (oreTypeID < 3) {
				int crystID = crystals[oreTypeID];
				changeBlock(loc.x, loc.y, 1, 0, true);
				for (int x = -5; x <= 5; x++)
					for (int y = -5; y <= 5; y++) {
						Block tmpB = getBlock(loc.x + x, loc.y + y);
						if (tmpB.blockID == 0
								&& getBlock(loc.x + x, loc.y + y + 1).blockID == 1) {
							changeBlock(loc.x + x, loc.y + y, crystID, 0, true);
							//addUpdate(loc.x + x, loc.y + y);
							//updateTrollBlock(loc.x + x, loc.y + y);
						}

					}
			} else {
				int oreID = ores[oreTypeID - 3];
				changeBlock(loc.x, loc.y, oreID, 0, true);
				for (int x = -2; x <= 2; x++)
					for (int y = -2; y <= 2; y++) {
						Block tmpB = getBlock(loc.x + x, loc.y + y);
						if (tmpB.blockID == 1) {
							if (MathUtils.randomBoolean()) {
								changeBlock(loc.x + x, loc.y + y, oreID, 0,
										true);
								// Gdx.app.log("map", "made ore!!!!!!!!!!!!!!");
								//addUpdate(loc.x + x, loc.y + y);
								//updateTrollBlock(loc.x + x, loc.y + y);
							}

						}

					}

			}

			chunk.oreSourceList.free(loc);
		}
		//Punk.genericAirBlock.set(0, 0);
	}*/

	public int[] groundOres = { 30, 30, 30, 30, 30, 31, 31, 31, 31 };
	public int[] midOres = { 31, 30 };
	public int[] lowOres = { 30, 31, 31, 32, 32, 32 };
	/*
	 * public int getCrystalID(int x, int y){ //int offset =
	 * Chunk.getGroundHeight(x, gameSeed); if (y -Chunk.getElevation(x) < -300)
	 * return MathUtils.random(3)==0?32:30; return
	 * (MathUtils.randomBoolean()?30:31); }
	 * 
	 * 
	 * public int getOreID(int y){ if (y > 50) return -5; if (y > -300) return
	 * (MathUtils.randomBoolean()?-5:-6); return -7; }
	 */

	public DayTime targetTime = DayTime.START;

	public void updateTimeOfDay(int minute, boolean queue) {
		// Gdx.app.log("map, updateTimeofDay", "checking time"+minute);
		setTimeFlags(minute);
		
	}

	public static int currentTimeFlag;
	private void setTimeFlags(int minute) {
		currentTimeFlag = 1<< minute;
		
		
		
	}

	/*
	 * public void queueSideLightRayUpdates(boolean left){
	 * //resetSideLightMaps(left,(byte)0); int lastX =
	 * (currentChunk+(left?0:2))*Punk.CHUNKSIZE +(left?0:64); for (int i =
	 * (currentChunk+(left?-1:1))*Punk.CHUNKSIZE+ (left?0:-64); i < lastX; i++)
	 * lightQueue.add(i); lightQueue.shuffle();
	 * 
	 * ///updateSky();
	 * 
	 * //Gdx.app.log("map", "queued side lightmaps");
	 * 
	 * } public void queueTopLightRayUpdates(){ /*resetAllLightMaps((byte)0);
	 * int lastX = currentChunk+(2*Punk.CHUNKSIZE); for (int i =
	 * CHUNKOFFSET-Punk.CHUNKSIZE; i < lastX; i++) lightQueue.add(i);
	 * lightQueue.shuffle(); //Gdx.app.log("map", "queued top lightmaps");
	 * ///updateSky(); //queueAllLightRayUpdates(); }
	 */

	/*public boolean updateBlocks(int count, World world, PunkBodies monsterIndex) {
	//	if (count >= 0) {
			updater.updateBlocks(count, this);

		//} else {
			//updater.updateBlocks(updater.blockUpdateList.size(), this);
		//}
		return updater.blockUpdateList.list.isEmpty();
	}*/

	int timedUpdatesLeft = 0;

	public void updateTimed() {
		

	}

	public boolean doTimedUpdates() {
		if (timedUpdatesLeft > 0) {
			//updater.updateTimed(this, BLOCKUPDATEREPS);
			timedUpdatesLeft -= BLOCKUPDATEREPS;
			// Gdx.app.log(TAG, "timed left: "+timedUpdatesLeft);
			return true;
		}
		return false;
	}

	public void addUpdate(int x, int y) {
		// System.out.println( "A~DDED" +x + " "+y);

		chunkPool.addToUpdateList(x, y);
	}

	public void addTimedUpdate(int x, int y) {
		chunkPool.addToTimedUpdateList(x, y);
		// System.out.println("timed add:" + x + y);

	}

	public PunkInventory getChest(int x, int y, int meta) {
		return getValidChunk(x, y).chests.get(meta);
	}

	public Chunk getValidChunk(int x, int y) {
		Chunk validChunk = chunkPool.getChunkWorld(x, y);
		return validChunk;
	}

	public Chunk getValidChunk(BlockLoc loc) {
		return getValidChunk(loc.x, loc.y);
	}

	public boolean isOnValidChunk(int x, int y) {
		return (x >= LEFTMOSTBLOCK && x <= RIGHTMOSTBLOCK && y >= BOTTOMBLOCK && y <= TOPBLOCK);
	}

	public boolean isOnValidChunk(BlockLoc loc) {
		return isOnValidChunk(loc.x, loc.y);
	}

	public void addSign(BlockLoc loc, String text) {
		Chunk validChunk = getValidChunk(loc);
		// get location

		// find first available sign slot
		int firstFreeSlot = 0;
		while (validChunk.signs.containsKey(firstFreeSlot))
			firstFreeSlot++;
		if (firstFreeSlot >= 32)
			return;// TODO extend intmap for more

		validChunk.signs.put(firstFreeSlot, text);
		// change block
		changeBlock(loc.x, loc.y, 43, firstFreeSlot, true);

	}

	public boolean plantBlock(int x, int y, int iID, int metaData, World world,
			PunkBodies monsterIndex) {
		tmpBlock = getBlock(x, y);
		// beans/cotton seeds just need to be in dirt
		switch (tmpBlock.blockID) {
		case 2:
		case 3:// dirt/grass
			if (getBlock(x, y + 1).blockType() < 2) {
				tmpBlock.set(45, metaData);
				PhysicsActor button = chunkActors.add(26, 0, world,
						monsterIndex, tmpV.set(x, y));
				return true;
			}

		case 21:// sand

		}
		return false;
	}

	public int putChest(int x, int y) {
		return getValidChunk(x, y).putChest(x, y);

	}

	public void loadChests() {
		// populates chests[]

	}

	public boolean isInCurrentChunk(int x, int y) {
		return (x >= CHUNKOFFSET && x < CHUNKOFFSET + Punk.CHUNKSIZE
				&& y >= CHUNKOFFSETY && y < CHUNKTOPY);
	}

	public Block getBlock(Vector2 pos) {
		return getBlock(MathUtils.floor(pos.x), MathUtils.floor(pos.y));
	}

	public Block getBlock(BlockLoc loc) {
		return getBlock(loc.x, loc.y);
	}

	/*
	 * public Block getBlockoldfast(long x, int y){ //start with the y coords >=
	 * ? //Gd//Gdx.app.loggetBlock", "xloc:"+(x&255)); if (y>=CHUNKTOPY) return
	 * chunkT.block[(int)x&Punk.CHUNKSIZEMASK][y&Punk.CHUNKSIZEMASK]; if
	 * (y<CHUNKOFFSETY) return
	 * chunkB.block[(int)x&Punk.CHUNKSIZEMASK][y&Punk.CHUNKSIZEMASK]; //if (y <
	 * Punk.CHUNKSIZE && y >0 && x > CHUNKOFFSET-Punk.CHUNKSIZE && x <
	 * CHUNKOFFSET+Punk.CHUNKSIZE*2)//if y is valid { try{ //y = Math.abs(
	 * y&=Punk.CHUNKSIZEMASK; if (x >= CHUNKOFFSET + Punk.CHUNKSIZE) return
	 * chunkR.block[(int)x& Punk.CHUNKSIZEMASK][y]; if (x >= CHUNKOFFSET) return
	 * chunkC.block[(int)x& Punk.CHUNKSIZEMASK][y]; return chunkL.block[(int)x&
	 * Punk.CHUNKSIZEMASK][y]; }catch (NullPointerException ex) {
	 * //System.out.println("bad getBlock!");
	 * 
	 * return Punk.genericAirBlock; }
	 * 
	 * 
	 * } //return Punk.genericAirBlock; //return R, C, L
	 * 
	 * 
	 * // if (x<CHUNKOFFSET) return
	 * x>CHUNKOFFSET-Punk.CHUNKSIZE-1?chunkL.block[(
	 * x-CHUNKOFFSET)+Punk.CHUNKSIZE
	 * ][Math.max(y>Punk.CHUNKSIZE-1?Punk.CHUNKSIZE-
	 * 1:y,0)]:Punk.genericBedrockBlock; // if (x>=CHUNKOFFSET+Punk.CHUNKSIZE) {
	 * // //System.out.println("X:"+(Punk.CHUNKSIZE-x+CHUNKOFFSET)); // return
	 * x<CHUNKOFFSET+Punk.CHUNKSIZE*2-1? //
	 * chunkR.block[x-CHUNKOFFSET-Punk.CHUNKSIZE
	 * ][Math.max(y>Punk.CHUNKSIZE-1?Punk.CHUNKSIZE-1:y,0)]: //
	 * Punk.genericBedrockBlock; // }//x<CHUNKOFFSET+Punk.CHUNKSIZE*2 ? // // if
	 * (y>=Punk.CHUNKSIZE) return Punk.genericAirBlock; // if (y<0) // { //
	 * return Punk.genericBedrockBlock; // } // return
	 * chunkC.block[x-CHUNKOFFSET][y]; //
	 * 
	 * }
	 * 
	 * public Block getBlock(long x, int y){ long lChunkID = x >>
	 * Punk.CHUNKBITS; int lHeightID = y >> Punk.CHUNKBITS; if (lChunkID ==
	 * currentChunk){ if (lHeightID == currentChunkHeightID) return
	 * chunkC.block[(int)(x&Punk.CHUNKSIZEMASK)+
	 * ((y&Punk.CHUNKSIZEMASK)<<Punk.CHUNKBITS)]; if (lHeightID ==
	 * currentChunkHeightID+1) return chunkT.block[(int)(x&Punk.CHUNKSIZEMASK)+
	 * ((y&Punk.CHUNKSIZEMASK)<<Punk.CHUNKBITS)]; if (lHeightID ==
	 * currentChunkHeightID-1) return chunkB.block[(int)(x&Punk.CHUNKSIZEMASK)+
	 * ((y&Punk.CHUNKSIZEMASK)<<Punk.CHUNKBITS)]; } else if (lChunkID ==
	 * currentChunk+1){ if (lHeightID == currentChunkHeightID) return
	 * chunkR.block
	 * [(int)(x&Punk.CHUNKSIZEMASK)+((y&Punk.CHUNKSIZEMASK)<<Punk.CHUNKBITS)];
	 * if (lHeightID == currentChunkHeightID+1) return
	 * chunkTR.block[(int)(x&Punk.CHUNKSIZEMASK)+
	 * ((y&Punk.CHUNKSIZEMASK)<<Punk.CHUNKBITS)]; if (lHeightID ==
	 * currentChunkHeightID-1) return chunkBR.block[(int)(x&Punk.CHUNKSIZEMASK)+
	 * ((y&Punk.CHUNKSIZEMASK)<<Punk.CHUNKBITS)]; }else if (lChunkID ==
	 * currentChunk-1){ if (lHeightID == currentChunkHeightID) return
	 * chunkL.block[(int)(x&Punk.CHUNKSIZEMASK)+
	 * ((y&Punk.CHUNKSIZEMASK)<<Punk.CHUNKBITS)]; if (lHeightID ==
	 * currentChunkHeightID+1) return chunkTL.block[(int)(x&Punk.CHUNKSIZEMASK)+
	 * ((y&Punk.CHUNKSIZEMASK)<<Punk.CHUNKBITS)]; if (lHeightID ==
	 * currentChunkHeightID-1) return chunkBL.block[(int)(x&Punk.CHUNKSIZEMASK)+
	 * ((y&Punk.CHUNKSIZEMASK)<<Punk.CHUNKBITS)]; } //Gdx.app.log("map",
	 * "invalid getblock @ "+x+","+y); return Punk.genericAirBlock; }
	 */
	public Block getBlock(int x, int y) {
		/*
		 * long lChunkID = x >> Punk.CHUNKBITS; int lHeightID = y >>
		 * Punk.CHUNKBITS; if (lChunkID == currentChunk){ if (lHeightID ==
		 * currentChunkHeightID) return
		 * chunkC.block[(x&Punk.CHUNKSIZEMASK)][(y&Punk.CHUNKSIZEMASK)]; if
		 * (lHeightID == currentChunkHeightID+1) return
		 * chunkT.block[(x&Punk.CHUNKSIZEMASK)][(y&Punk.CHUNKSIZEMASK)]; if
		 * (lHeightID == currentChunkHeightID-1) return
		 * chunkB.block[(x&Punk.CHUNKSIZEMASK)][(y&Punk.CHUNKSIZEMASK)]; } else
		 * if (lChunkID == currentChunk+1){ if (lHeightID ==
		 * currentChunkHeightID) return
		 * chunkR.block[(x&Punk.CHUNKSIZEMASK)][(y&Punk.CHUNKSIZEMASK)]; if
		 * (lHeightID == currentChunkHeightID+1) return
		 * chunkTR.block[(x&Punk.CHUNKSIZEMASK)][(y&Punk.CHUNKSIZEMASK)]; if
		 * (lHeightID == currentChunkHeightID-1) return
		 * chunkBR.block[(x&Punk.CHUNKSIZEMASK)][(y&Punk.CHUNKSIZEMASK)]; }else
		 * if (lChunkID == currentChunk-1){ if (lHeightID ==
		 * currentChunkHeightID) return
		 * chunkL.block[(x&Punk.CHUNKSIZEMASK)][(y&Punk.CHUNKSIZEMASK)]; if
		 * (lHeightID == currentChunkHeightID+1) return
		 * chunkTL.block[(x&Punk.CHUNKSIZEMASK)][(y&Punk.CHUNKSIZEMASK)]; if
		 * (lHeightID == currentChunkHeightID-1) return
		 * chunkBL.block[(x&Punk.CHUNKSIZEMASK)][(y&Punk.CHUNKSIZEMASK)]; }
		 */
		return chunkPool.getBlock(x, y);
		// Gdx.app.log("map", "invalid getblock @ "+x+","+y);
		// return Punk.genericAirBlock;
	}

	/*public void makeALightFlare(long tx, int ty) {
		for (int x = -4; x < 4; x++)
			for (int y = -4; y < 4; y++) {
				// int c = flareTable[x+16][y+16];
				int nx = (int) ((tx + x) & Punk.CHUNKSIZEMASK);
				int ny = ty + y & Punk.CHUNKSIZEMASK;
				Block tmpB = getBlock(nx, ny);
				tmpB.setLight((byte) Math.max(tmpB.getLight(),
						flareTable[x + 4][y + 4]));

				// System.out.println("c:"+c);
				// if (c != lightMap[x][y])
				// System.out.println("light changed for torch");
			}
	}

	/*public void makeATorch(int tx, int ty) {

		getBlock(tx, ty).setLight((byte) 14);
		updater.addLightUpdatesSurrounding(tx, ty, false);
	}

	public void makeAGlowStone(int tx, int ty) {

		getBlock(tx, ty).setLight((byte) 12);
		updater.addLightUpdatesSurrounding(tx, ty, false);
	}*/

	/*public void makeAMushroom(int tx, int ty) {
		getBlock(tx, ty).setDayLight(13);
		// getBlock(tx,ty+1).dayLight = 10;
		// updater.updateBlockLight(tx, ty+1);
		updater.addToUpdateList(tx, ty + 1);
		updater.addToUpdateList(tx + 1, ty);
		updater.addToUpdateList(tx, ty - 1);
		updater.addToUpdateList(tx - 1, ty);
	
		updater.addToUpdateList(tx, ty);
		updater.addLightUpdatesSurrounding(tx, ty, true);
	}

	public void makeASkyGlowStone(int tx, int ty) {

		getBlock(tx, ty).setDayLight((byte) 15);
		updater.addLightUpdatesSurrounding(tx, ty, true);
		updater.dayLightUpdateList.addBlock(tx, ty);
	}

	public void makeAFire(int tx, int ty) {

		System.out.println("making fire");
		// for (int x = Math.max(-16+tx, 0); x < Math.min(tx+17, 0); x++)
		for (int x = -12; x < 13; x++)
			for (int y = -12; y < 13; y++) {
				int c = torchTable[x + 12][y + 12];
				Block tmpB = getBlock(tx + x, ty + y);
				tmpB.setLight((byte) Math.max(tmpB.getLight(),
						fireTable[x + 12][y + 12]));
				addLightUpdate(tx + x, ty + y);

			}
	}

	private int lx, ly;
	private byte lightTot;

	public void updateTrollBlock(int x, int y) {
		// if air, look around and activate any trollblocks
		// Gdx.app.log("map", "troll check");

		// if (getBlock(x, y).blockType() == 0){
		/*Block tmp = getBlock(x, y);

		boolean isExposed = (getBlock(x - 1, y).blockType() == 0
				|| getBlock(x + 1, y).blockType() == 0
				|| getBlock(x, y + 1).blockType() == 0 || getBlock(x, y - 1)
				.blockType() == 0);

		// Gdx.app.log("map", "troll block updating"+tmp.blockID);
		switch (tmp.blockID) {
		case -4:
			if (isExposed)
				tmp.set(-3, 0);
			break;
		case -5:
			if (isExposed)
				tmp.set(25, 0);
			break;
		case -6:
			if (isExposed)
				tmp.set(26, 0);
			break;
		case -7:
			if (isExposed)
				tmp.set(27, 0);
			break;
		case -8:
			if (isExposed)
				tmp.set(28, 0);
			break;
		case -3:
			if (!isExposed)
				tmp.set(-4, 0);
			break;
		case 25:
			if (!isExposed)
				tmp.set(-5, 0);
			break;
		case 26:
			if (!isExposed)
				tmp.set(-6, 0);
			break;
		case 27:
			if (!isExposed)
				tmp.set(-7, 0);
			break;
		case 28:
			if (!isExposed)
				tmp.set(-8, 0);
			break;

		}*/

	

	/*
	 * public void processMidd2leSky(){
	 * 
	 * for (int i = CHUNKOFFSET; i < CHUNKOFFSET+Punk.CHUNKSIZE; i++)
	 * lightQueue.add(i); lightQueue.shuffle(); while (lightQueue.size > 0)
	 * doLightRays(timeOfDay, lightQueue.pop()); }
	 */
	IntArray possibleSpawnIDs = new IntArray();

	public void spawnMonster(int x, int y, Block b) {
		possibleSpawnIDs.clear();
		boolean isDay = (timeOfDay != DayTime.NIGHT);
		int effectiveLight = b.getLight();
		if (isDay)
			effectiveLight = Math.max(effectiveLight, b.getDayLight());
		boolean lowLight = (effectiveLight < PunkBodies.SPAWNLIGHTLEVEL);
		// chickens
		// if (b.blockID)
		// zombies
		// spiders

		// snake/clouds

		// slimes
		// roaches

		if (possibleSpawnIDs.size == 0)
			return;
		int m = MathUtils.random(possibleSpawnIDs.size - 1);
		switch (possibleSpawnIDs.get(m)) {
		case 2:

			break;

		case 3:
			break;

		case 11:

			break;
		}

	}

	public void updateSpawns() {
		if (customSpawnStrategy != null) {
			customSpawnStrategy.attemptSpawn(this, l_player, l_world, l_mi);
			return;
		}

		for (int i = 0; i < 4; i++)
			if (allChunksLoaded && allChunksPostFetched) {
				// pick a random block and try to spawn
				// if (customSpawnStrategy != null){
				// customSpawnStrategy.attemptSpawn(this, l_player, l_world,
				// l_mi);

				// } else
				{
					int x = MathUtils.random(PunkBodies.SPAWNMIN,
							PunkBodies.SPAWNMAX);
					if (MathUtils.randomBoolean())
						x *= -1;
					x += l_player.x;
					int y = MathUtils.random(PunkBodies.SPAWNMIN,
							PunkBodies.SPAWNMAX);
					if (MathUtils.randomBoolean())
						y *= -1;
					y += l_player.y;
					// Gdx.app.log("map", "trying to spawn");
					if (getBlock(x, y + 1).blockType() == 0
							&& getBlock(x, y + 2).blockType() == 0) {
						int progress = 0;
						while (getBlock(x, y).blockType() == 0 && progress < 24) {
							y--;
							progress++;
						}
						if (progress > 22)
							return;
						Block tmpB = getBlock(x, y);
						;

						if (MathUtils.randomBoolean())
						{
							
						}
						
						///////////////////////////////////////////////////chunkActors.trySpawn(x,y+1, tmpB);
							//chunkActors.trySpawnPassiveMob(x, y, tmpB, l_world,
							//		l_mi, l_player, this);
						//else
						
							
							//chunkActors.trySpawnGenericMob(x, y + 1, tmpB,
							//		l_world, l_mi, l_player, this);
					}
				}
			}
	}



	// NAME!! We have recieved a strange report from our watcher on the Other
	// World.
	// You must travel there in astral form and investigate.
	// Beware! All is not well.




	int towerWidth_s = 7, towerWidth_m = 10, towerWidth_l = 15;

	int[] towerCA = new int[100];
	int[] crossbarCA = new int[100];

	public void makeTower(Vector2 position, int id, Player player) {
		int type, density, bladeRatio, seed0 = player.gameInfo.gameSeed, seed1 = player.gameInfo.gameSeed + 2048, mutationRatio = 0, arg0, arg1, attackStrength;

		switch (id / 4) {
		case 0:
			type = 0;
			density = MathUtils.random(-32, 0);
			attackStrength = 1;
			arg0 = MathUtils.random(4, 6);
			arg1 = MathUtils.random(3, 5);

			break;
		case 1:
		case 2:
			type = 1;
			density = MathUtils.random(-32, 0);
			attackStrength = 3;
			arg0 = MathUtils.random(4, 12);
			arg1 = MathUtils.random(3, 6);
			break;
		case 3:
		case 4:
			type = 2;
			density = MathUtils.random(-32, 0);
			attackStrength = 5;
			arg0 = MathUtils.random(4, 16);
			arg1 = MathUtils.random(5, 7);
			break;
		default:
			type = 3;
			density = MathUtils.random(-32, 0);
			attackStrength = 8;
			arg0 = MathUtils.random(4, 16);
			arg1 = MathUtils.random(3, 8);

			break;

		}
		bladeRatio = MathUtils.random(0, attackStrength);

		makeTower(id, arg0, arg1, type, density, bladeRatio, seed0, seed1,
				mutationRatio, attackStrength, position);

	}

	public void makeTower(int id, int arg0, int arg1, int type, int density,
			int bladeRatio, int seed0, int seed1, int mutationRatio,
			int attackStrength, Vector2 position) {
		int posX = MathUtils.floor(position.x);
		int posY = MathUtils.floor(position.y);
		position.x = posX;
		position.y = posY;// +PunkBodies.PLANKOFFSET;;

		int height, width;
		float plankThickness = 1f;// MathUtils.random(.8f, 1.4f);
		float gridSize = MathUtils.random(4, 7);
		switch (type) {
		default:
		case 0:
			height = arg1;
			width = arg0;

			break;

		}

		int n = 2;
		while (n < width) {
			crossbarCA[n] = 6;
			n++;
		}
		Gdx.app.log("map", "make towerrrrererre");
		int leftmost = 1, rightmost = width - 1;
		for (int h = 0; h < height; h++) {

			// random distribution
			int x = leftmost;
			boolean done = false;
			int blockByte = XRand.getByte(seed0 + x + h << 6);
			int blockByte2 = XRand.getByte(seed1 + x + h << 6);
			try {
				blockByte = (blockByte * mutationRatio + blockByte2
						* mutationRatio)
						/ (mutationRatio << 1);
			} catch (ArithmeticException ex) {
				blockByte = MathUtils.random(-127, 127);
			}
			// System.out.println();
			while (!done && x <= rightmost) {
				blockByte = XRand.getByte(seed0 + x + (h << 6));
				blockByte2 = XRand.getByte(seed1 + x + (h << 6));
				try {
					blockByte = (blockByte * mutationRatio + blockByte2
							* mutationRatio)
							/ (mutationRatio << 1);
				} catch (ArithmeticException ex) {
					blockByte = MathUtils.random(-127, 127);
				}
				// System.out.print("random:"+blockByte);
				// Gdx.app.log("map",
				// "block byte:"+blockByte+"from "+(x+(h<<6)));
				if (crossbarCA[x] == 6)
					towerCA[x] = ((blockByte < 0) ? 1 : 2);
				else
					towerCA[x] = 0;
				x++;
			}
			// /XRand.getByte(seed0+x+height<<6)
			// make 0s according to density
			x = leftmost;
			done = false;
			blockByte = XRand.getByte(seed0 + x + h << 5);
			blockByte2 = XRand.getByte(seed1 + x + h << 5);
			try {
				blockByte = (blockByte * mutationRatio + blockByte2
						* mutationRatio)
						/ (mutationRatio << 1);
			} catch (ArithmeticException ex) {
				blockByte = MathUtils.random(-127, 127);
			}
			// System.out.println();
			while (!done && x <= rightmost) {
				blockByte = XRand.getByte(seed0 + x + (h << 5));
				blockByte2 = XRand.getByte(seed1 + x + (h << 5));
				try {
					blockByte = (blockByte * mutationRatio + blockByte2
							* mutationRatio)
							/ (mutationRatio << 1);
				} catch (ArithmeticException ex) {
					blockByte = MathUtils.random(-127, 127);
				}
				// System.out.print("density:"+blockByte);
				if (blockByte > density || crossbarCA[x] == 0) {
					towerCA[x] = 0;

				}
				x++;
			}
			// updates
			x = leftmost;
			done = false;
			// System.out.println();
			while (!done && x <= rightmost) {
				// System.out.print("updates:"+x);
				if (towerCA[x - 1] == 2)
					towerCA[x] = 3;

				/*
				 * if (towerCA[x] == 1){ boolean leftSlant = (towerCA[x-1] == 2
				 * || towerCA[x-1] == 3); boolean rightSlant = (towerCA[x+1] ==
				 * 2 || towerCA[x+1] == 3); if (rightSlant && leftSlant)
				 * towerCA[x] = 4; else if (rightSlant) towerCA[x] = 5; }
				 */
				x++;
			}

			// crossbars

			// recalculate sides
			x = 1;
			rightmost = 0;

			boolean leftDone = false, rightDone = false;
			while (x <= width) {
				// System.out.print("sides:"+towerCA[x]);
				if (towerCA[x] == 0) {
					if (!leftDone)
						leftmost = x + 1;
				} else {// filled block
					if (x > rightmost)
						rightmost = x;
					leftDone = true;
				}

				x++;
			}
			Gdx.app.log("map, recalc sides", "left " + leftmost + " right "
					+ rightmost + "done" + rightDone);
			// leftmost++;
			// place 4s ||

			// clear
			for (int i = 0; i < width + 10; i++)
				crossbarCA[i] = 0;

			// Gdx.app.log("map","\nleft::"+leftmost+" right: "+rightmost);
			/*
			 * if (rightmost - leftmost <= 2){ //Gdx.app.log("map",
			 * "\nline too thin, skipping"); for (int i = 0; i <= rightmost+2;
			 * i++){ crossbarCA[i] = 0; towerCA[i] = 0; }
			 * 
			 * }
			 */

			if (leftDone) {

				x = leftmost;
				while (x <= rightmost) {
					crossbarCA[x] = 6;
					x++;
				}
				// structural support
				for (int i = leftmost + 1; i <= rightmost; i++) {
					if (towerCA[i] == 0 || towerCA[i] == 1) {
						boolean needsL = (towerCA[i - 1] == 2 || towerCA[i - 1] == 3);
						boolean needsR = (towerCA[i + 1] == 2 || towerCA[i + 1] == 3);
						if (needsL && needsR)
							towerCA[i] = 4;
						else if (needsL)
							towerCA[i] = 1;
						else if (needsR)
							towerCA[i] = 5;
					}
				}
				towerCA[leftmost] = 4;
				towerCA[rightmost] = 4;
			}

			System.out.println("\nplanks:" + leftmost);
			for (int i = 0; i <= rightmost + 15; i++)
				System.out.print("" + towerCA[i]);
			System.out.println("\ncrossbars:");// */
			for (int i = 0; i <= rightmost + 15; i++)
				System.out.print("" + crossbarCA[i]);

			// make physics blocks
			Vector2 plankPos = tmpV;
			for (int i = 0; i <= rightmost; i++) {
				switch (towerCA[i]) {
				case 1:
					plankPos.set(position);
					plankPos.add(gridSize * i, (gridSize + plankThickness) * h
							+ plankThickness / 2);
					chunkActors.spawnPlank(false, plankPos, 90, gridSize,
							plankThickness, type, l_world, l_mi);
					break;
				case 4:
					plankPos.set(position);
					plankPos.add(gridSize * i, (gridSize + plankThickness) * h
							+ plankThickness / 2);
					chunkActors.spawnPlank(false, plankPos, 90, gridSize,
							plankThickness, type, l_world, l_mi);

					// doesn't break
				case 5:
					plankPos.set(position);
					plankPos.add(gridSize * (i + 1) - plankThickness,
							(gridSize + plankThickness) * h + plankThickness
									/ 2);
					chunkActors.spawnPlank(false, plankPos, 90, gridSize,
							plankThickness, type, l_world, l_mi);
					break;
				case 2:// /
					plankPos.set(position);
					plankPos.add(gridSize * i, (gridSize + plankThickness) * h
							+ plankThickness / 2);
					chunkActors.spawnPlank(true, plankPos, 45, (gridSize),
							plankThickness, type, l_world, l_mi);
					break;
				case 3:// \
					plankPos.set(position);
					plankPos.add(gridSize * (i + 1) - plankThickness,
							(gridSize + plankThickness) * h + plankThickness
									/ 2);
					chunkActors.spawnPlank(true, plankPos, 135, (gridSize),
							plankThickness, type, l_world, l_mi);
					break;// */
				}
			}

			// crossbars
			boolean started = false;
			boolean finished = false;
			int startPos = 0;
			for (int i = 0; i <= rightmost + 2; i++) {
				if (crossbarCA[i] > 5) {// solid
					if (!started) {
						started = true;
						startPos = i;
					}
				} else {// space
					if (started) {
						plankPos.set(position);
						plankPos.add(gridSize * (startPos) - plankThickness,
								(gridSize + plankThickness) * (h + 1)
										- plankThickness + plankThickness / 2);
						chunkActors.spawnPlank(false, plankPos, 0,
								(rightmost + 1 - startPos) * gridSize
										+ plankThickness * 2, plankThickness,
								type, l_world, l_mi);
						started = false;
						Gdx.app.log("map", "planbk thicknessL" + plankThickness);
					}
				}
			}
			startPos = leftmost;
			plankPos.set(position);
			plankPos.add(gridSize * (startPos) - plankThickness,
					(gridSize + plankThickness) * (h + 1) - plankThickness
							+ plankThickness / 2);
			// chunkActors.spawnPlank(false, plankPos, 0,
			// (rightmost+1-startPos)*gridSize+plankThickness*2, plankThickness,
			// type, l_world, l_mi);

		}// for each level
		makePlatform(posX - 2, posY - 1, (int) ((width + 1) * gridSize) + 4,
				(int) (height * (gridSize + plankThickness)), 24);

		// monsters
		for (int i = 0; i < attackStrength; i++) {
			if (i < bladeRatio) {
				// make blade!!!
				int turretType = 0;// TODO
				int x = MathUtils.random(2, width - 2);
				int y = MathUtils.random(height + 1);
				chunkActors.spawnTurret(x * gridSize + .5f + posX, y
						* (gridSize + plankThickness) + posY, turretType,
						l_world, l_mi);
			} else {
				int turretType = 0;// TODO
				int x = MathUtils.random(2, width - 2);
				int y = MathUtils.random(height + 1);
				chunkActors.spawnTurret(x * gridSize + .5f + posX, y
						* (gridSize + plankThickness) + posY, turretType,
						l_world, l_mi);
			}
		}
		// -(int)(gridSize+plankThickness+1)

	}

	public void makePlatform(int x, int y, int radius, int topSpace, int blockID) {
		// clear above
		for (int posx = x; posx <= x + radius; posx++) {
			int posy = y;

			while (posy < y + topSpace) {
				Block tmpB = getBlock(posx, posy);
				if (tmpB.blockType() >= 64)
					tmpB.set(0, 0);
				posy++;
			}
		}
		// fill below
		for (int posx = x; posx <= x + radius; posx++) {
			int posy = y;
			Block tmpB = getBlock(posx, posy);
			while (tmpB.blockID == 0) {
				tmpB = getBlock(posx, posy);
				tmpB.set(blockID, 0);
				posy--;
			}
		}
		// flushLight((x+radius)/2, y+topSpace, radius/2+1, y, true, true);

	}

	public int rSize = 64;
	public int[][] mapCA = new int[rSize][rSize];
	public IntArray distributedRandoms = new IntArray();
	public PunkBlockList turretSites = new PunkBlockList();

	public int blockTotal(int f) {
		int tot = 0;
		for (int x = 1; x < f; x++)
			for (int y = 1; y <= x; y++)
				tot++;
		return tot;
	}

	public void makeRorschach(int x, int y, int id) {
		makeRorschachBlocks(x, y, 24, 24, 4, 3);
	}

	public void makeRorschachBlocks(int x, int y, int block1, int block2,
			int size, int turrets) {
		// clear
		for (int i = 0; i < rSize; i++) {
			for (int j = 0; j < rSize; j++) {
				mapCA[i][j] = 0;
			}
		}
		turretSites.clear();
		// generate int array
		distributedRandoms.clear();
		int count = blockTotal(size);
		for (int i = 0; i < count; i++)
			distributedRandoms.add(i);
		distributedRandoms.shuffle();
		int threshold1 = count / 2 - 1, threshold2 = count / 4 * 3 - 1;
		// fill first octant
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < i; j++) {
				int val = distributedRandoms.removeIndex(0);
				mapCA[i][j] = val;

			}
		}
		// mirror first octant
		for (int i = 1; i < size; i++) {
			for (int j = 0; j < i; j++) {
				mapCA[j][i] = mapCA[i][j];
			}
		}

		// mirror quadrant up
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				mapCA[i][(size * 2 - 1) - j] = mapCA[i][j];
			}
		}
		// mirror both across
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size * 2; j++) {
				mapCA[size * 2 - 1 - i][j] = mapCA[i][j];
			}
		}

		// print blocks
		for (int i = 0; i < size * 2; i++) {
			for (int j = 0; j < size * 2; j++) {
				if (mapCA[i][j] > threshold2)
					changeBlock(x + i, y + j, block2, 0, true);
				else if (mapCA[i][j] > threshold1)
					changeBlock(x + i, y + j, block1, 0, true);
				else
					turretSites.addBlock(x + i, y + j);

			}
		}
		// insert turrets
		/*
		 * turretSites.list.shuffle(); int c = 0; while (turrets > 0 && c <
		 * turretSites.size()){ BlockLoc loc = turretSites.list.get(c); c++;
		 * if (getBlock(loc.x, loc.y-1).blockType() >= 64){ turrets--;
		 * changeBlock(loc.x, loc.y, 44, 0); } }
		 */

	}

	public void updateActorSourceBlocks(Chunk chunk) {
	/*	// if (!allChunksLoaded || chunk.actorSourceBlockList.size() <=0)
		// return;
		chunk.spawnersChecked = true;
		for (int n = Math.min(1, chunk.actorSourceBlockList.size() - 1), i = n; i >= 0; i--) {
			// BlockLoc loc = chunk.actorSourceBlockList.list.get(i);
			BlockLoc loc = chunk.actorSourceBlockList.getNext();
			// Gdx.app.log("map",
			// "actor source blocks. "+chunk.actorSourceBlockList.size());
			if (l_player.distanceToPlayerHead(loc.x, loc.y) < Punk.BWIDTH * 2) {// if
																				// in
																				// range
				// Gdx.app.log("map", "actorupdate: in range");
				Block spawnB = getBlock(loc);
				if (spawnB.blockID == 44) {
					switch (spawnB.meta) {
					case 1:
					case 2:
					case 0:
						chunkActors.spawnTurret(loc.x, loc.y, spawnB.meta,
								l_world, l_mi);
						changeBlock(loc.x, loc.y, 0, 0, true);
						chunk.actorSourceBlockList.removeLast();
						// Gdx.app.log("map",
						// "actorupdate: spawned!!!!!!!!!!!!!!!!!!"+loc.x+","+loc.y);
						break;
					}
				} else if (spawnB.blockID == 90) {// campfire
					// chunkActors.spawnBarbarians(loc.x, loc.y, l_world, l_mi);
					chunk.actorSourceBlockList.removeLast();
				} else if (spawnB.blockID == 89) {
					if (chunkActors.spawnFromFlower(loc.x, loc.y, l_world,
							l_mi, spawnB.meta, l_player, this))
						chunk.actorSourceBlockList.removeLast();
				}
			}
		}*/

	}

	private boolean updateActorSourceBlocks() {
		Iterator<Chunk> iter = chunkPool.entries().iterator();
		boolean done = false;
		while (!done && iter.hasNext()) {

			Chunk c = iter.next();
			if (!c.spawnersChecked) {
				updateActorSourceBlocks(c);
				done = true;

			}

		}
		;
		if (!done) {
			// reset all chunks
			Iterator<Chunk> it = chunkPool.entries().iterator();
			// boolean done = false;
			while (it.hasNext()) {
				Chunk c = it.next();
				c.spawnersChecked = false;
				// Gdx.app.log(TAG, "all chunks check for spawners");

			}
			;

			return true;
		}
		return false;

	}

	int torchRange = 8;

	private Block tmpBlock, tmpLookBlock;

	private void chopTree(int mapX, int mapY, Vector2 dig, int woodBlock,
			int leafBlock, PunkBodies monsterIndex, World world) {
		mapY++;
		Block b = getBlock(mapX, mapY);
		// if (b.blockID != woodBlock) return;//nothing above
		if (getBlock(mapX - 1, mapY - 1).blockID == woodBlock
				|| getBlock(mapX + 1, mapY - 1).blockID == woodBlock) {
			// Gdx.app.log("map", "NOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
			return;
		}

		tmpLookBlock = getBlock(mapX, mapY);
		while (tmpLookBlock.blockID == woodBlock
				&& getBlock(mapX - 1, mapY).blockID != woodBlock
				&& getBlock(mapX + 1, mapY).blockID != woodBlock) {

			itemPool.createItem(woodBlock, 1, 0, 0, world, monsterIndex, this,
					tmpV.set(mapX + 0.5f, mapY + 0.5f));
			getBlock(mapX, mapY).set(0, 0);
			addLightUpdate(mapX, mapY);

			// destroyBlock( true, mapX, mapY, 0);
			if (getBlock(mapX - 1, mapY).blockID == woodBlock) {
				itemPool.createItem(woodBlock, 1, 0, 0, world, monsterIndex,
						this, tmpV.set(mapX - 0.5f, mapY + 0.5f));
				getBlock(mapX - 1, mapY).set(0, 0);
				addLightUpdate(mapX, mapY);
			}
			if (getBlock(mapX + 1, mapY).blockID == woodBlock) {
				itemPool.createItem(woodBlock, 1, 0, 0, world, monsterIndex,
						this, tmpV.set(mapX + 1.5f, mapY + 0.5f));
				getBlock(mapX + 1, mapY).set(0, 0);
				addLightUpdate(mapX + 1, mapY);
			}

			/*
			 * if (getBlock(mapX, mapY-1).blockID == woodBlock) destroyBlock(
			 * true, mapX, mapY-1, 0); if (getBlock(mapX-1, mapY-1).blockID ==
			 * woodBlock) destroyBlock( true, mapX-1, mapY, 0); if
			 * (getBlock(mapX+1, mapY-1).blockID == woodBlock) destroyBlock(
			 * true, mapX+1, mapY, 0);
			 */
			// tmpLookBlock.set(0,0);
			// System.out.println("destroying wood block, " + mapX + " " +
			// mapY);

			for (int j = -5; j <= 5; j++)
				if (getBlock(mapX + j, mapY).blockID == leafBlock)
					addUpdate(mapX + j, mapY);

			mapY++;
			tmpLookBlock = getBlock(mapX, mapY);
		}
		for (int i = -4; i < 10; i++)
			for (int j = -8; j <= 8; j++)
				addUpdate(mapX + j, mapY + i);
	}

	Vector2 dig = new Vector2();
	public void changeBlock(int x, int y, int iID, int metaData,
			boolean propagate) {
		changeBlock(x,y,iID, metaData, propagate, false);
	}
	public void changeBlock(int x, int y, int iID, int metaData,
			boolean propagate, boolean left) {

		// this is for placing blocks
		
		Chunk tmpC = chunkPool.getChunkWorld(x, y);
		if (tmpC == null) {
			//chunkPool.fetchWorld(x, y, currentPlane, l_player);
			//tmpC = chunkPool.getChunkWorld(x, y);
			 //Gdx.app.log(TAG, "change block: null chunk");

		
		 
			chunkPool.addPlaceQueue(x, y, iID, metaData, currentPlane);
			// Gdx.app.log(TAG, "q "+x+","+y);
			return;
		}
		// Gdx.app.log(TAG, "put first "+x+","+y+"id:"+iID);
		tmpC.modified = true;
		Block b = chunkPool.getBlockActual(x, y);
		if (b == Punk.genericAirBlock)
			Gdx.app.log(TAG, "!!!!!!!!!!!!!!");
		// pre-change checking
		change(b, getValidChunk(x,y), x,y,iID, metaData, propagate, left);
		// Gdx.app.log(TAG, "put end "+x+","+y+"id:"+getBlock(x,y)+" b "+b);
		// getValidChunk(x,y).unScrubBlock(this, x&Punk.CHUNKBITS,
		// y&Punk.CHUNKBITS);
		// add to list for trips, or maybe fancy stuff later
		//placedBlocks.addBlock(x, y);
	}

	public void change(Block b, Chunk c, int x, int y, int iID, int metaData, boolean propagate, boolean left) {
		int beforeID = b.blockID, beforeMeta = b.meta;
		b.set(iID, metaData);
		// System.out.println( "PLACING BLOCK" +x);
		// add updates all around
		if (propagate)
			addBlockUpdatesSurrounding(x, y);// TODO this adds extra updates
		
		BlockDef bd = getBlockDef(b.blockID), beforebd = getBlockDef(beforeID);
		bd.place(this, x, y, beforeID, beforeMeta, propagate, b, left);
		c.unScrubBlock(this, x & Punk.CHUNKSIZEMASK,
				y & Punk.CHUNKSIZEMASK);
		if (beforebd.lightLoss == bd.lightLoss && beforebd.dayLightLoss == bd.dayLightLoss);else
			b.setDayLightAtCreation(bd.minDayLight);
		addUpdate(x, y);		
	}

	private void addBlockUpdatesSurrounding(int x, int y) {
		//for (int i = -1; i <= 1; i += 1)
			//for (int j = -1; j <= 1; j += 1)
				//if (i != 0 || j != 0)//&&
		int i = -1, j = 0;
					if (chunkPool.hasChunk(x + i, y + j))
						addUpdate(x + i, y + j);
					
					i = 1; j = 0;
					if (chunkPool.hasChunk(x + i, y + j))
						addUpdate(x + i, y + j);
					
					i = 0; j = 1;
					if (chunkPool.hasChunk(x + i, y + j))
						addUpdate(x + i, y + j);
					
					i = 0; j = -1;
					if (chunkPool.hasChunk(x + i, y + j))
						addUpdate(x + i, y + j);

	}

	public void destroyBlock(BlockDamageType dType, int mapX, int mapY, int p, float angle) {
		// diggin means it's not fire
		Item it;
		dig.set(mapX, mapY);
		Block b = getBlock(mapX, mapY);
		Chunk tmpC = chunkPool.getChunkWorld(mapX, mapY);
		
		if (tmpC == null || tmpC.nullChunk) {
			chunkPool.fetchWorld(mapX, mapY, currentPlane, l_player);
			tmpC = chunkPool.getChunkWorld(mapX, mapY);
		}
		if (!tmpC.isPostFetched) {// queue
			tmpC.addPlaceQueue(mapX, mapY, 0, 0, p);
			return;
		}
		tmpC.modified = true;
		// destroy actors inside block

		// query for hits
		// Gdx.app.log("punk", "QUERY STARTED QUERY STARTED");
		// world.QueryAABB(destroyBlockQuery, mapX+.2f, mapY+.2f, mapX+2.8f,
		// mapY+2.8f);
		chunkActors.destroyPhysicsBlocks(mapX, mapY, l_world, l_mi);
		// Gdx.app.log("map", "destruction query "+mapX + " "+mapY);
		// blocks that can't be destroyed
		
		if (b.blockType() == 1)
			return;
		int id = b.blockID, meta = b.meta;
		BlockDef bd = getBlockDef(id);
		switch (dType){
			case HAND:
			case PICKAXE:
			case SHOVEL:
			case BLOCK:
			case FLAIL:
				//Gdx.app.log(TAG, "destroy"+dType);
				b.set(0,0);
				
				break;
				
			case AXE:
				b.set(0,0);
	
				break;
			case FIRE:if (!b.isFlammable()) return;
			case EXPLOSION:
				changeBlock(mapX, mapY, 42, 0, true);
				//tmpBlock.set(42, MathUtils.random(16, 127));
				//tmpBlock.setLight(15);
				//updater.addLightUpdatesSurrounding(mapX, mapY, true);
				//updater.addLightUpdatesSurrounding(mapX, mapY, false);
				break;
			case FROST:
				if (bd instanceof Automata){
					//bd.flowIn();
				}
				//if (!bd.immuneFrost)
					//changeBlock(mapX, mapY, 43, 76, true);
				else return;
				break;
			}
		
		Player.particles.blockDestruction(mapX, mapY, id);
		
		bd.destroy(dType, mapX, mapY, p, angle, this, id, meta);
		
		// update blocks around
		addUpdate(mapX - 1, mapY);
		addUpdate(mapX + 1, mapY);
		addUpdate(mapX, mapY + 1);
		addUpdate(mapX, mapY - 1);
		addUpdate(mapX, mapY);
		//addDayLightUpdate(mapX, mapY);
		// return true;
		l_player.updateBBs(this, l_world, Punk.gTime, 0, l_mi);

	}

	public enum BlockDamageType {
		EXPLOSION, FLAIL, AXE, HAND, BLOCK,  PICKAXE, SHOVEL, FIRE, FROST, CHARGE
	}

	public boolean damageBlock(BlockDamageType dType,  int mapX, int mapY,
			int damage, int p) {
		Block tmpB = getBlock(mapX, mapY);
		int blockHP = tmpB.getBlockHP();
		blockHP -= damage;
		//Gdx.app.log(TAG, "blockhp"+blockHP+"  damage"+damage);
		
		if (tmpB.def().takeDamage(tmpB, dType, mapX, mapY, p, 0, this, damage))
			destroyBlock(dType, mapX, mapY, p, l_player.stateTime);
				
		

		return false;
		// return blockHP;
	}

	public boolean damageBlock(BlockDamageType dType,  Vector2 loc,
			int damage) {
		return damageBlock(dType, MathUtils.floor(loc.x), MathUtils.floor(loc.y), damage, currentPlane);
		
	}

	public boolean damageBlock(BlockDamageType dType, 
			BlockLoc loc, int damage) {
		return damageBlock(dType,  MathUtils.floor(loc.x), MathUtils.floor(loc.y), damage, currentPlane);
		
	}
	public boolean damageBlock(BlockDamageType dType, 
			int x, int y, int damage) {
		return damageBlock(dType,  MathUtils.floor(x), MathUtils.floor(y), damage, currentPlane);
		
	}
	
	public void recalculatePositionConstants(Player player) {
		currentChunk = player.x >> Punk.CHUNKBITS;
		currentChunkHeightID = player.y >> Punk.CHUNKBITS;
		CHUNKOFFSET = currentChunk * (Punk.CHUNKSIZE);
		CHUNKOFFSETY = currentChunkHeightID * Punk.CHUNKSIZE;
		CHUNKTOPY = CHUNKOFFSETY + Punk.CHUNKSIZE;
		RIGHTMOSTBLOCK = CHUNKOFFSET + Punk.CHUNKSIZE * 2;
		LEFTMOSTBLOCK = CHUNKOFFSET - Punk.CHUNKSIZE;
		TOPBLOCK = CHUNKOFFSETY + Punk.CHUNKSIZE * 2;
		BOTTOMBLOCK = CHUNKOFFSETY - Punk.CHUNKSIZE;

	}

	public void reloadAllChunks(int chunkID, int heightID, Player player) {

		// Gdx.app.log("map", "reload all chunks");
		shiftChunk(0, player);

		loadEverything();

		// while (!allChunksLoaded)updateFetch(l_player);
		// growTrees();
		// checkPostFetch();

	}

	public void startLoadAllChunks(int chunkID, int heightID, Player player) {

		// currentChunk = chunkID;
		// currentChunkHeightID = heightID;
		//Gdx.app.log("map", "start load all chunks" + chunkID + "," + heightID);
		// player.x = chunkID << Punk.CHUNKBITS;
		// player.y = heightID << Punk.CHUNKBITS;
		shiftChunk(0, player);

		// queueAllLightRayUpdates();
		// queueSunlightAll();
		// recalculatePositionConstants();
		// loadEverything();
		// updateBlocks(-1, l_world, l_mi);updateBlocks(-1, l_world, l_mi);
		// updateSpawns();updateSpawns();updateSpawns();updateSpawns();
		// updateSpawns();updateSpawns();updateSpawns();updateSpawns();
		// updateSpawns();updateSpawns();updateSpawns();updateSpawns();

	}

	public void teleportTo(Vector2 teleportPos, Player player) {
		int teleportChunkID = (int) (teleportPos.x) / Punk.CHUNKSIZE;
		int teleportHeightID = (int) (teleportPos.y) / Punk.CHUNKSIZE;
		loadEverything();
		// if adjacent, use shiftChunk, otherwise load all of them
		/*
		 * if (teleportChunkID == currentChunk){ if (teleportHeightID ==
		 * currentChunkHeightID) return; if (teleportHeightID ==
		 * currentChunkHeightID-1){shiftChunk(2, player);return;} if
		 * (teleportHeightID == currentChunkHeightID+1){shiftChunk(-2,
		 * player);return;} } else if (teleportHeightID ==
		 * currentChunkHeightID){ if (teleportChunkID == currentChunk) return;
		 * if (teleportChunkID == currentChunk+1){shiftChunk(1,player); return;}
		 * if (teleportChunkID == currentChunk-1){shiftChunk(-1,player);
		 * return;} }
		 */

		reloadAllChunks(teleportChunkID, teleportHeightID, player);
	}

	public Chunk tmpChunkC, tmpChunk0, tmpChunk1;

	public boolean shiftChunk(int direction, Player player) {
		// saveCurrentChunk();
		// loadEverything();
		// finishLight();
		recalculatePositionConstants(player);
		// completeAllSaves();
		// System.out.println("Shifting Chunk" + direction + " current:" +
		// currentChunk);
		// Gdx.app.log("map", "SHIFTCHUNKKKKK");

		/*
		 * while (!allChunksLoaded) updateFetch(player); while
		 * (!allChunksPostFetched)checkPostFetch(); growTrees(); while
		 * (sunlightQueue.size > 0 || lightUpdateList.size() > 0||
		 * dayLightUpdateList.size() > 0) updateSky();
		 */

		// saveChunk( chunkC);
		// TODO save current chunk?
		// growTrees();
		// currentChunk += direction;

		// Check for out of range. and save

		/*
		 * Iterator<Chunk> iter = chunkPool.entries().iterator(); //boolean done
		 * = false; while (iter.hasNext()) { //Entry<Chunk> e = iter.next();
		 * Chunk c = iter.next();//e.value; //c.distanceFromPlayer =
		 * c.distanceTo(currentChunk, currentChunkHeightID); if
		 * (c.distanceFromPlayer() > minChunkRange+1){ //Gdx.app.log(TAG,
		 * "deleting"+c.chunkID+","+c.heightID + " distance "
		 * +c.distanceTo(currentChunk, currentChunkHeightID) +
		 * "current: "+currentChunk+","+currentChunkHeightID);
		 * //chunkPool.saveChunk(c, true); }
		 * 
		 * 
		 * } ;
		 */

		// start fetching in range

		/*
		 * for (int x = -minChunkRange; x <= +minChunkRange; x++) for (int y =
		 * -minChunkRange; y <= + minChunkRange; y++){ if (Math.abs(x) !=
		 * minChunkRange || Math.abs(y) !=
		 * minChunkRange)chunkPool.fetch(currentChunk+x,currentChunkHeightID+y,
		 * currentPlane, player); }
		 */

		// fetchChunk(currentChunk+1, currentChunkHeightID, chunkR , player);

		// queueSideLightRayUpdates(false);
		// queueAllLightRayUpdates();
		// makeSunlightSide(true);
		// queueSunlightSide(false);

		// light map
		// resetLightMap();
		// doLightMap();

		// player.saveToDisk(false);

		// queueLightRayUpdates();
		player.leftLoad = CHUNKOFFSET;
		player.rightLoad = CHUNKOFFSET + Punk.CHUNKSIZE;
		player.topLoad = CHUNKTOPY;
		player.bottomLoad = CHUNKOFFSETY;

		/*if (!chunkPool.hasChunk(player.x, player.y)) {
			while (!updateFetch(player))
				;
			while (!updateFetch(player))
				;
			Punk.processing = true;
			;
			Punk.processingInc = 1;
			Punk.gameMode = 66;
		}*/
		return false;

	}

	/*private void completeAllSaves() {
		while (chunkPool.updateSave(true)) {
		}

	}*/

	private Chunk[] queuedSaveChunks = new Chunk[3];
	private Chunk tmpch;

	private int nextSave;

	boolean[][] CA = new boolean[128][128];
	public int[] pointsStore = new int[128];

	private int CALargest9(int x, int y, int[][] CA) {
		int largest = CA[x][y];
		for (int i = x - 1; i <= x + 1; i++)
			for (int j = i == x ? y - 1 : y; j <= y + 1; j += 2)
				if (CA[i][j] > largest)
					largest = CA[i][j];
		return largest;
	};

	private int CALargest5(int x, int y, int[][] CA) {
		// Gdx.app.log("map", "CA start: "+x+", "+y);
		int largest = CA[x][y];
		for (int i = x - 1; i <= x + 1; i++)
			for (int j = i == x ? y - 1 : y; j <= y + 1; j += 2) {
				// Gdx.app.log("map", "CA check "+i+", "+j);
				if (CA[i][j] > largest)
					largest = CA[i][j];
			}

		return largest;
	};

	private int CAAverage9(int x, int y, int[][] CA) {
		int total = 0;
		for (int i = x - 1; i <= x + 1; i++)
			for (int j = y - 1; j <= y + 1; j++)
				total += CA[i][j];
		return total / 9;
	};

	private int CAAverage5(int x, int y, int[][] CA) {
		int total = CA[x][y];
		for (int i = x - 1; i <= x + 1; i++)
			for (int j = i == x ? y - 1 : y; j <= y + 1; j += 2)
				total += CA[i][j];
		return total / 5;
	};

	private void CAPrint5X(int x, int y, int value, int[][] CA) {
		CA[x - 1][y] = value;
		CA[x + 1][y] = value;
		CA[x][y - 1] = value;
		CA[x][y + 1] = value;
	}

	private void makeLeaves(int offsetX, int offsetY, int size, int leafID,
			Random seeded, int plane) {
		for (int x = 0; x <= size * 2 + 3; x++)
			for (int y = 0; y <= size * 2 + 3; y++) {
				// distributedRandoms.add(x+y*size*2);
				mapCA[x][y] = 0;
			}
		// for (int x = 0; x <= size*2; x++)
		// for (int y = 0; y <= size*2; y++){
		// if (x == 0 || y == 0 || x == size*2-1 || y == size*2-1) mapCA[x][y] =
		// 0;
		// }

		// insert points. 32, threshold 1-8 (0.25 to 0.01)
		int numberOfPoints = size;
		for (int r = 0; r < numberOfPoints; r++) {
			pointsStore[r * 2] = MathUtils.random(-3, 4) + size;
			pointsStore[r * 2 + 1] = MathUtils.random(-3, 4) + size;
		}

		int thresh = 1;
		// blur
		for (int r = 0; r < 1; r++) {

			CAPrint5X(size, size, 32, mapCA);

			for (int x = 1; x <= size * 2 - 1; x++)
				for (int y = 1; y <= size * 2 - 1; y++) {
					mapCA[x][y] = CAAverage5(x, y, mapCA);
				}

			for (int p = 0; p < numberOfPoints; p++) {
				mapCA[pointsStore[p * 2]][pointsStore[p * 2 + 1]] = 28;
			}
			CAPrint5X(size, size, 25, mapCA);

			for (int x = 1; x <= size * 2 - 1; x++)
				for (int y = 1; y <= size * 2 - 1; y++) {
					mapCA[x][y] = CAAverage5(x, y, mapCA);
				}

			CAPrint5X(size, size, 16, mapCA);
			for (int p = 0; p < numberOfPoints; p++) {
				mapCA[pointsStore[p * 2]][pointsStore[p * 2 + 1]] = 28;
			}
			for (int x = 1; x <= size * 2 - 1; x++)
				for (int y = 1; y <= size * 2 - 1; y++) {
					mapCA[x][y] = CAAverage5(x, y, mapCA);
				}

		}

		// place
		for (int x = 0; x < size * 2 + 2; x++)
			for (int y = 0; y < size * 2 + 2; y++) {
				tmpLoc.set(x + offsetX - size, y + offsetY - 1 - size);
				Block b = getBlock(tmpLoc);
				if (mapCA[x][y] > thresh && b.blockID == 0) {
					// changeBlock(x+offsetX-size, y+offsetY-1-size, leafID, 0);
					// Block b = getBlock(tmpLoc);
					// b.resetDayBits();
					qChange(tmpLoc.x, tmpLoc.y, leafID, 15, plane);
					// b.set(leafID, 15);
					// updater.updateBlock(tmpLoc, b, this, false);
					// addUpdate(x+offsetX-size, y+offsetY-1-size);
					// updater.doLightUpdateDayLight(x+offsetX-size,
					// y+offsetY-1-size);
				}
			}
		/*
		 * for (int x = 0; x < size*2+2; x++) for (int y = 0; y < size*2+2;
		 * y++){ tmpLoc.set(x+offsetX-size, y+offsetY-1-size); Block b =
		 * getBlock(tmpLoc); if (mapCA[x][y] > thresh){
		 * //changeBlock(x+offsetX-size, y+offsetY-1-size, leafID, 0);
		 * 
		 * //b.resetDayBits(); b.dayLight = 15; //b.set(leafID, 15);
		 * updater.updateBlock(tmpLoc, b, this, false);
		 * //addUpdate(x+offsetX-size, y+offsetY-1-size);
		 * //updater.doLightUpdateDayLight(x+offsetX-size, y+offsetY-1-size); }
		 * }
		 */

		// boolean[][] CA = new boolean[size*2][size*2];
		// boolean[][] tmpCA = new boolean[size*2][size*2];
		// for (int x = 0; x<size*2; x++)//initialize
		// {
		// for (int y = 0; y<size*2; y++)
		// {
		// CA[x][y] = false;
		// tmpCA[x][y] = false;
		// }
		// }
		// //place leaves
		// for (int x = 0; x<size*2; x++)
		// {
		// for (int y = 0; y<size*2; y++)
		// {
		// //if (Math.random() > 0.6f ) CA[x][y] = true;
		// if (XRand.getByte( (offsetX+offsetY+y+x))>60) CA[x][y] = true;
		// //Gdx.app.log("map", "random"+XRand.getByte(7) );
		// }
		//
		// //CA[size][size] = true;
		//
		// }
		//
		// for (int x = size-2; x<size+2; x++)
		// for (int y = size-2; y<size+2; y++)
		// {
		// CA[x][y] = true;
		// //System.out.print("x:"+x+" y:"+y+" ");
		// }
		//
		//
		// //CA
		// for (int repeats = 0; repeats<3; repeats++)
		// {
		// for (int x = 1; x<size*2-1; x++)
		// {
		// for (int y = 1; y<size*2-1; y++)
		// {//look at surrounding blocks
		// int total =0;
		// for (int lookx=-1; lookx<=1; lookx++)
		// for (int looky = -1; looky<=1; looky++)
		// {
		// if (CA[x+lookx][y+looky]) total++;
		// }
		// //then print block
		// if (total>3) tmpCA[x][y] = true; else tmpCA[x][y] = false;
		// }
		// }
		//
		// //print
		// for (int x = 0; x<size*2; x++)
		// {
		// for (int y = 0; y<size*2; y++)
		// {
		// CA[x][y] = tmpCA[x][y];
		// }
		// }
		//
		// }//CA
		//
		// for (int x = 0; x<size*2; x++)
		// {
		// for (int y = 0; y<size*2; y++)
		// {
		// if (CA[x][y] && getBlock(offsetX+x-size,
		// offsetY+y-(int)(size*1.5f)).getID() == 0) {
		// Block tmpB = getBlock(offsetX+x-size, offsetY+y-(int)(size*1.5f));
		// tmpB.set(leafID,0);
		// //tmpB.dayLight = (byte)0;
		// tmpB.light = (byte)0;
		// addUpdate(offsetX+x-size, offsetY+y-(int)(size*1.5f));
		// //System.out.println("writing leaf x:"+(offsetX+x-size));
		// }
		// //System.out.println(""+)
		// }
		// }
		//
		//
		//
		//
		//
		// //System.out.println("leaves done");
	}

	BlockLoc tmpLoc = new BlockLoc();

	private void makePineNeedles(int offsetX, int offsetY, int height, int p) {
		int width = Math.max(height / 2, 8);
		// if (width%2 == 0)
		// width++;

		// true = leaves
		/*
		 * for (int y = 0; y < height+5; y+=1) for (int x = -(y%7)+(4-y/4); x <
		 * (y%7)-(4-y/4)+1; x++){ CA[16+x][y] = true; }
		 */

		int n = MathUtils.random(2, width);
		int y = 0;
		boolean done = false;
		// Gdx.app.log("map, pineneedles", "starting "+n);
		while (!done) {

			// n-= MathUtils.random(2)+1;
			n--;

			/*
			 * if (n >=0){ Block bc = getBlock(offsetX, y+offsetY); if
			 * (bc.blockID == 0){ bc.set(14, 0); bc.light = 0; bc.dayLight = 0;
			 * addUpdate(offsetX, y+offsetY); } }
			 */

			if (n > 1)
				// Gdx.app.log("map, pineneedles", "looping "+n);
				for (int x = 0; x < n; x++) {
					// Gdx.app.log("map, pineneedles", "looping x "+x);
					Block br = getBlock(x + offsetX, y + offsetY);
					if (br.blockID == 0) {
						qChange(x + offsetX, y + offsetY, 14, 15, p);

					}

					Block bl = getBlock(offsetX - x, y + offsetY);
					if (bl.blockID == 0) {
						// /bl.set(14, 15);
						// bl.light = 0;
						// bl.dayLight = 0;
						// addUpdate(offsetX-x, y+offsetY);
						qChange(offsetX - x, y + offsetY, 14, 15, p);

					}

				}
			Block bl = getBlock(offsetX, y + offsetY);
			if (bl.blockID == 0) {
				qChange(offsetX, y + offsetY, 14, 15, p);

			}

			y += 1;
			if (y > height - 3 && n <= 2)
				done = true;
			if (n <= 1) {
				// if (y < height-4)
				n = MathUtils.random(2, width-3);
				// else n = MathUtils.random(width/2)+2;
			}
		}

		// /CA
		// Chunk.makeRandom(CA, true, 0.2f);
		// Chunk.printCA(CA);
		// Chunk.doCA(CA, 1, 3);
		// Chunk.doCA(CA, 1, 4);

		// write leaves
		/*
		 * for (int x = 0; x<32; x++) { for (int y = 0; y<height; y++) { Block
		 * tmpB = getBlock(offsetX-16+x, offsetY +height - y); if (CA[x][y] &&
		 * tmpB.blockID == 0) { tmpB.set(14, 0); tmpB.dayLight = (byte)0;
		 * tmpB.light = (byte)0; addUpdate(offsetX-16+x, offsetY +height - y);
		 * 
		 * }
		 * 
		 * //System.out.println(""+) } }
		 */

	}

	public boolean updateTrees() {
		// if (allChunksPostFetched && allChunksLoaded &&
		// updater.dayLightUpdateList.size() == 0)
		return growTrees();
	}

	private int leafY;

	/*
	 * public void updateGrass21(){//TODO this wastes resources, needs to remove
	 * non-grass blocks
	 * 
	 * updater.addLoc(Chunk.grassList.getNext());
	 * updater.addLoc(chunkT.grassList.getNext());
	 * updater.addLoc(chunkB.grassList.getNext());
	 * updater.addLoc(chunkL.grassList.getNext());
	 * updater.addLoc(chunkR.grassList.getNext());
	 * updater.addLoc(chunkTL.grassList.getNext());
	 * updater.addLoc(chunkTR.grassList.getNext());
	 * updater.addLoc(chunkBR.grassList.getNext());
	 * updater.addLoc(chunkBL.grassList.getNext());
	 * 
	 * }
	 */

	public void generateAppleTree(int mapX, int mapY, int p) {
		Chunk.seededRandom.setSeed(l_player.gameInfo.gameSeed + mapX * 21);
		int growSize = Chunk.seededRandom.nextInt(10) + 6;
		boolean isBig = false;// (growSize > 12);
		{// small
			makeLeaves(mapX, mapY + growSize, growSize / 2 + 4, 18,
					Chunk.seededRandom, p);
			qChange(mapX, mapY, 17, 0, p);
			for (mapTreeI = 0; mapTreeI < growSize - 1; mapTreeI++) {
				// Block tmpB = getBlock(mapX,mapY+mapTreeI);
				qChange(mapX, mapY + mapTreeI, 17, 1, p);
			}
			qChange(mapX, mapY + mapTreeI, 17, 2, p);
			// leaves

		}
		qChange(mapX, mapY, 17, 0, p);
	}

	//public ConcurrentLinkedQueue<PlacePacket> queuedChanges = new ConcurrentLinkedQueue<PlacePacket>();

	private void qChange(int x, int y, int id, int meta, int p) {
		PlacePacket pack = PlacePacket.pool.obtain();
		pack.set(x, y, id, meta, p);
		chunkPool.updateStream.add(pack);
	}

	public void flushChanges() {

		/*if (queuedChanges.isEmpty())
			return;
		PlacePacket p = queuedChanges.poll();
		int x0 = p.x, y0 = p.y, x1 = p.x, y1 = p.y;
		while (!queuedChanges.isEmpty()) {
			PlacePacket pack = queuedChanges.poll();
			if (pack.x < x0)
				x0 = pack.x;
			if (pack.y < y0)
				y0 = pack.y;
			if (pack.x > x1)
				x1 = pack.x;
			if (pack.y > y1)
				y1 = pack.y;
			
			changeBlock(pack.x, pack.y, pack.id, pack.meta, true);//(pack.x+pack.y % 8 == 0?true:false));
			pack.free();
		}
		removeLight(x0, y0, x1, y1);*/
	}

	private void removeLight(int x0, int y0, int x1, int y1) {
		for (int x = x0; x <= x1; x++)
			for (int y = y0; y <= y1; y++) {
				Block b = getBlock(x, y);
				b.removeLight();
			}
		for (int x = x0; x <= x1; x++) {
			addLightUpdate(x, y0);
			addLightUpdate(x, y1);
		}
		for (int y = y0 + 1; y < y1; y++) {
			addLightUpdate(x0, y);
			addLightUpdate(x1, y);
		}
	}

	public void generatePalmTree(int mapX, int mapY, int p) {
		Chunk.seededRandom.setSeed(l_player.gameInfo.gameSeed + mapX);
		int growSize = Math.max(12, Chunk.seededRandom.nextInt(32));
		
		makeLeaves(mapX, mapY + growSize + growSize / 2 - 4, growSize / 2, 13,
				Chunk.seededRandom, p);
		for (mapTreeI = 0; mapTreeI < growSize; mapTreeI++) {

			qChange(mapX, mapY + mapTreeI, 15, 0, p);

		}
		
		// Gdx.app.log("map", "made tree @ "+mapX+","+mapY);

	}

	public void generatePineTree(int mapX, int mapY, int p) {
		Chunk.seededRandom.setSeed(l_player.gameInfo.gameSeed + mapX);
		int growSize = Math.max(6, Chunk.seededRandom.nextInt(24));

		leafY = 3;
		// Gdx.app.log("map", "made tree @ "+mapX+","+mapY);

		// while (leafY < growSize){
		
		makePineNeedles(mapX, mapY + leafY, growSize, p);
		for (mapTreeI = 0; mapTreeI < growSize; mapTreeI++) {

			qChange(mapX, mapY + mapTreeI, 13, 0, p);
		}
		qChange(mapX, mapY, 13, 0, p);
		
		
	}

	public void generateCacaoTree(int mapX, int mapY, int p) {
		Chunk.seededRandom.setSeed(l_player.gameInfo.gameSeed + mapX);
		int size = Chunk.seededRandom.nextInt(16);
		int growSize;
		if (size > 12)
			growSize = (Chunk.seededRandom.nextInt(16) + 4) << 2;
		else
			growSize = Chunk.seededRandom.nextInt(32) + 8;
		qChange(mapX, mapY, 19, 0, p);
		

		leafY = growSize - MathUtils.random(4) - 3;
		boolean branchLeft = MathUtils.randomBoolean();

		while (leafY > 8) {
			int branchLength = (Chunk.seededRandom.nextInt(4) + 1);
			

			for (int i = 1; i < branchLength; i++) {
				qChange(mapX + (branchLeft ? -i : i), mapY + leafY, 19, 8, p);
			}
			// join
			qChange(mapX, mapY + leafY, 19, branchLeft ? 3 : 4, p);
			// end
			qChange(mapX + (branchLeft ? -branchLength : branchLength), mapY
					+ leafY, 19, branchLeft ? 6 : 7, p);
			
			makeLeaves(mapX + (branchLeft ? -branchLength : branchLength), mapY
					+ leafY + 1, 5, 20, Chunk.seededRandom, p);
			
			leafY -= MathUtils.random(4) + 2;
			branchLeft = !branchLeft;
		}
		//
		for (mapTreeI = 0; mapTreeI < growSize; mapTreeI++) {
			qChange(mapX, mapY + mapTreeI, 19, 1, p);

		}
		makeLeaves(mapX, mapY + growSize, MathUtils.random(12) + 8, 20,
				Chunk.seededRandom, p);
		//qChange(mapX, mapY, 1, 0, p);

	}
	
	public void generateSkyTree(int mapX, int mapY, int p) {
		Chunk.seededRandom.setSeed(l_player.gameInfo.gameSeed + mapX);
		int growSize = Chunk.seededRandom.nextInt(32)+10;

		leafY = 3;
		// Gdx.app.log("map", "sky tree"+mapX+","+mapY);

		// while (leafY < growSize){
		//makePineNeedles(mapX, mapY + leafY, growSize, p);

		int count = 0, runLength = 2;
		int gx = mapX, gy = mapY, dx = 0, dy = 1;
		while (count < growSize){
			qChange(gx, gy, 38, 0, p);
			runLength--;
			if (runLength == 0)
			{
				if (dx != 0){
					dy=1;
					dx = 0;
				} else {
					if (MathUtils.randomBoolean()){
						dx--;
						
					}else {
						dx++;
						
					}
					dy = 0;
				}
				runLength = MathUtils.random(2,3);
			}
			dx = MathUtils.clamp(dx, -1, 1);
			gx += dx;
			gy += dy;
			count++;
			//Gdx.app.log("map", "step"+count+","+dx+" "+dy);
		}
		
		makeLeaves(gx, gy, MathUtils.random(12) + 8, 39,
				Chunk.seededRandom, p);
		qChange(mapX, mapY, 38, 0, p);
		/*for (mapTreeI = 0; mapTreeI < growSize; mapTreeI++) {

			
		}*/
	}

	int[] tmpLandInfo;

	/*
	 * public void updateGrass21(){//TODO this wastes resources, needs to remove
	 * non-grass blocks
	 * 
	 * updater.addLoc(Chunk.grassList.getNext());
	 * updater.addLoc(chunkT.grassList.getNext());
	 * updater.addLoc(chunkB.grassList.getNext());
	 * updater.addLoc(chunkL.grassList.getNext());
	 * updater.addLoc(chunkR.grassList.getNext());
	 * updater.addLoc(chunkTL.grassList.getNext());
	 * updater.addLoc(chunkTR.grassList.getNext());
	 * updater.addLoc(chunkBR.grassList.getNext());
	 * updater.addLoc(chunkBL.grassList.getNext());
	 * 
	 * }
	 */
	
	public void generateTree(int x, int y, int p, int type) {
		//int[] inf = Chunk.getLandInfo(Chunk.getTemp(x));
		//flushChanges();
		//Gdx.app.log(TAG, "gentree "+type);
		
		switch (type){
		case 0:generatePineTree(x,y,p);break;
		case 1:generatePalmTree(x,y, p);break;
		case 2:generateAppleTree(x,y,p);break;
		case 3:generateCacaoTree(x,y,p);break;
		default:
		case 4:generateSkyTree(x,y,p);break;
		}
		//flushChanges();
		
	}

	/*public void makeFlareTable() {
		for (int x = 0; x < 9; x++)
			for (int y = 0; y < 9; y++) {
				// flareTable[x][y] = (byte)Math.max((16-tmpV.set(x-16,
				// y-16).len()+0.7f), 0);
				// System.out.println("torch:" + torchTable[x][y]);
				flareTable[x][y] = (byte) Math
						.max(0, (4 - tmpV.set(x - 4, y - 4).len() + MathUtils
								.random(3)));
			}
	}*/
	public boolean growTrees() {
		return true;
	}/*
	public boolean growTreesold() {// returns true if all trees done

		int growSize = 10;

		Iterator<BlockLoc> iter = updater.treeSourceList.list.iterator();
		int size = Math.min(1, updater.treeSourceList.size());

		while (size > 0) {
			BlockLoc treeLoc = updater.treeSourceList.removeFirst();
			// Gdx.app.log("trees",
			// "processing "+treeLoc.x+","+treeLoc.y+" id:"+getBlock(treeLoc).blockID);

			mapX = treeLoc.x;
			mapY = treeLoc.y;
			// int surfaceBlock =
			// chunkC.getLandInfo(Chunk.getTemp(treeLoc.x))[0];
			// surfaceBlock = 50;
			Block treeB = getBlock(mapX, mapY);

			tmpLandInfo = Chunk.getLandInfo(Chunk.getTemp(treeLoc.x));
			int landBlock = tmpLandInfo[1];
			int surfaceBlock = tmpLandInfo[0];
			int treeBlock = tmpLandInfo[2];
			treeB.set(treeBlock, getTreeSeed(treeLoc));

			
			boolean freeOnTop = true;// getBlock(treeLoc.x,treeLoc.y+3).dayLight
										// > 8;//true;
			int i = 1;
			while (i < 7) {
				Block b = getBlock(treeLoc.x, treeLoc.y + i);
				if (b.blockID != 0)
					freeOnTop = false;

				i++;
			}
			if (!freeOnTop) {
				// treeB.set(7, 0);
				size--;
				// Gdx.app.log("trees",
				// "not free on top "+treeLoc.x+","+treeLoc.y);

				continue;
			}
			// Gdx.app.log("trees",
			// "growing "+mapX+","+mapY+" id "+treeB.blockID);

			flushChanges();
			switch (treeB.blockID) {// tree source metas are all 1-16
									// case 0: treeB.set(50,0);
									// Gdx.app.log("trees",
									// "off "+getBlock(mapX,mapY-1).blockID);

			case 6:
				generateAppleTree(mapX, mapY, currentPlane);
				break;
			case 4:// palm
				generatePalmTree(mapX, mapY, currentPlane);
				break;
			case 5:// pine

				generatePineTree(mapX, mapY, currentPlane);

				break;
			case 53:// cacao

				generateCacaoTree(mapX, mapY, currentPlane);
				break;

			default:
				Gdx.app.log(TAG, "tree source error" + mapX + "," + mapY
						+ "  id" + treeB.blockID);
			}// switch

			size--;
			flushChanges();
		}
		if (updater.treeSourceList.size() == 0) {
			allTreesGrown = true;
			return true;
			// updateBlocks(-1, l_world, l_mi);
		}
		return false;
		// updateSky();
	}*/

	private int getTreeSeed(BlockLoc treeLoc) {
		long x = treeLoc.x + (treeLoc.y >> 32);
		x ^= (x << 21);
		x ^= (x >>> 35);
		x ^= (x << 4);
		return (int) (x & 15);
	}

	/*public void makeTorchTable() {
		for (int x = 0; x < 33; x++)
			for (int y = 0; y < 33; y++) {
				torchTable[x][y] = (byte) Math.max(
						(16 - tmpV.set(x - 16, y - 16).len() + 0.7f), 0);
				// System.out.println("torch:" + torchTable[x][y]);
			}
	}

	public void makeFireTable() {
		for (int x = 0; x < 25; x++)
			for (int y = 0; y < 25; y++) {
				fireTable[x][y] = (byte) Math.max((13 - tmpV
						.set(x - 13, y - 13).len() + 0.7f), (int) (Math
						.random() * 8));

				// System.out.println("fire:" + fireTable[x][y]);
			}
	}*/

	private Vector2 shootV = new Vector2(0, 0);
	private Vector2 shootAddV = new Vector2(0, 0);

	public Vector2 getShootVector(Vector2 touch, int strength) {

		shootV.set(touch);
		shootV.mul(-1);// reverse?
		shootV.mul(1f / touch.len());// make 1 unit long
		shootAddV.set(shootV);
		shootAddV.mul(10f);
		shootV.mul(strength);
		shootV.add(shootAddV);
		return shootV;
	}

	public Vector2 getPlayerShootVector(float angle, float strength) {
		// Gdx.app.log("map", "shootstrength:"+strength);
		shootV.set(1, 0);
		shootV.rotate(angle + 180);// reverse?

		// shootV.nor();
		shootV.mul(12 * Math.max(.10425f, strength)).add(
				shootV.tmp().nor().mul(5));
		// shootV.mul(strength*18);
		shootV.add(l_player.body.linVelWorld);

		return shootV;
	}

	

	public boolean shootWand(Vector2 touch, Player player, long time,
			World world, PunkBodies monsterIndex, GenericMob srcMob) {

		boolean isLeft = (l_player.throwAngle > 90 && l_player.throwAngle < 270);
		targetSrc.set(l_player.hand.position);
		GrenadeInfo info = l_mi.playerWands[player.activeWand];
		Grenade aBullet = (Grenade) chunkActors.getGrenade();

		aBullet.createBody(28, world, monsterIndex, targetSrc);
		if (aBullet.info.flying)
			aBullet.body.setType(BodyType.KinematicBody);
		aBullet.createBBs(world, monsterIndex);
		aBullet.set(info, 0, srcMob);
		aBullet.info.collidesWithFriends = false;
		aBullet.info.collidesWithEnemies = true;
		aBullet.animTimer = Punk.gTime + 20;
		aBullet.body.setFixedRotation(false);
		// aBullet.body.setAngularVelocity(MathUtils.random(8f,
		// 32f)*(MathUtils.randomBoolean()?1f:-1f));

		if (aBullet.info.particleIndex != -1) {

			ParticleEffect p = Player.particles.grenadeParticle(aBullet.x,
					aBullet.y, info.particleIndex);
			// if (p != null)Gdx.app.log("map", "has particles!!!!!!!!!!!!");
			aBullet.info.setParticle(p);
		}
		monsterIndex.playSpellSound(aBullet.info);
		Gdx.app.getInput().vibrate(300);
		// Gdx.app.log("map", "particles on:"+aBullet.info.hasParticle);
		aBullet.body.setLinearVelocity(tmpV.set(info.maxVelocity, 0).rotate(
				Punk.adjustedTouchAngle + 180));
		// l_player.setHeadTarget(aBullet.body);
		player.shootTimer = Punk.gTime + info.minVelocity;// denotes the
															// interval in this
															// case
		return l_player.inv.reduceDurability(l_player.activeQuickSlot);
		// return true;

	}

	

	// public boolean shootGrenadeFromPlayerold(float angle, Player player, long
	// time, World world, PunkBodies monsterIndex){
	// //return true if weapon is broke i.e need to changeBeltSlot in main
	// if (player.hasValidAmmo())
	// {
	// int type = player.inventory.getGrenadeInfo(player.activeAmmoSlot);
	//
	// switch (type){
	// case 0:
	// case 1:
	// case 2:
	// case 3:
	// case 4:
	// case 5:
	// case 7:
	// case 8:
	// case 10:
	// case 11:
	// PhysicsActor aBullet = chunkActors.getGrenade(type);
	// ((Grenade)aBullet).info.collidesWithPlayer = false;
	// aBullet.createBody(28, world, monsterIndex,
	// tmpV.set(player.position.x,player.position.y+1f));
	// if (!grenadeBodyTypeIsDynamic(type))
	// aBullet.body.setType(BodyType.KinematicBody);
	//
	//
	// aBullet.createBBs(world, monsterIndex);
	// aBullet.resetValues(28, type);
	// aBullet.animTimer = time + 250;
	// aBullet.body.setFixedRotation(false);
	// aBullet.body.setAngularVelocity(MathUtils.random(8f,
	// 32f)*(player.isLeft?1f:-1f));
	//
	// /*tmpV.set(touch);
	// tmpV.mul(-2);
	// if (!player.isLeft) tmpV.x += 2;
	// tmpV.add(player.position);*/
	// tmpV.set(player.position);
	// //shooting particlefx
	// //explosionPool.createExplosion(ExplosionType.LAUNCHER, world, this,
	// monsterIndex, tmpV, time, player);
	//
	// aBullet.actorMeta = type;
	// aBullet.body.setLinearVelocity(getPlayerShootVector(angle,
	// player.throwLength));
	//
	// player.setHeadTarget(aBullet.body);
	//
	// player.shootTimer = time + player.getShootTime();
	//
	// break;
	//
	// case 6:
	//
	//
	// }
	//
	//
	// if (player.getGameMode() == 1)
	// monsterIndex.playThrowSound(player.inventory.getItemID(player.activeQuickSlot));
	// else monsterIndex.playLauncherSound(player.getActiveID());
	// //return (player.useAmmo());
	// player.useAmmo();
	// if (player.inventory.getItemAmount(player.activeAmmoSlot)<=0) return
	// true;
	// if (player.inventory.getItemAmount(player.activeQuickSlot)<=0) return
	// true;
	//
	// }
	// return false;
	//
	//
	// }

	public boolean grenadeBodyTypeIsDynamic(int type) {
		switch (type) {
		case 11:
		case 10:
			return false;

		default:
			return true;
		}
	}

	public boolean monsterShootold(int strength, int type, Vector2 touch,
			PhysicsActor source, long time, World world, PunkBodies monsterIndex) {

		PhysicsActor aBullet = null;// chunkActors.getGrenade(type);
		((Grenade) aBullet).info.collidesWithFriends = true;
		aBullet.createBody(28, world, monsterIndex, tmpV.set(source.position.x
				+ (touch.x > .1f ? -1 : 1), source.position.y + 1f));
		if (!grenadeBodyTypeIsDynamic(type))
			aBullet.body.setType(BodyType.KinematicBody);

		aBullet.createBBs(world, monsterIndex);
		aBullet.resetValues(28, type);
		aBullet.animTimer = time + 20;
		aBullet.body.setFixedRotation(false);
		aBullet.body.setAngularVelocity(MathUtils.random(8f, 32f)
				* (source.isLeft ? 1f : -1f));

		tmpV.set(touch);
		tmpV.mul(-2);
		if (!source.isLeft)
			tmpV.x += 2;
		tmpV.add(source.position);
		// shooting particlefx
		// explosionPool.createExplosion(ExplosionType.LAUNCHER, world, this,
		// monsterIndex, tmpV, time, player);

		aBullet.body.setLinearVelocity(getShootVector(touch, strength));

		// source.animTimer = time + 300;

		return true;
	}

	Vector2 targetSrc = new Vector2();

//	public boolean shootPlayerGrenade(World world, PunkBodies monsterIndex) {
//		boolean isLeft = (l_player.throwAngle > 90 && l_player.throwAngle < 270);
//		targetSrc.set(l_player.position).add(l_player.handTarget);
//		GrenadeInfo info = l_player.activeGrenade;
//		Grenade aBullet = (Grenade) chunkActors.getGrenade();
//
//		aBullet.createBody(info.bodyDefID, world, monsterIndex, targetSrc);
//		if (aBullet.info.flying)
//			aBullet.body.setType(BodyType.KinematicBody);
//		else if (aBullet.info.bounces)
//			aBullet.body.getFixtureList().get(0).setRestitution(1);
//		aBullet.createBBs(world, monsterIndex);
//		aBullet.set(info, 1);
//		//aBullet.factionID = 0;
//		//aBullet.info.collidesWithFriends = false;
//		//aBullet.info.collidesWithEnemies = true;
//		aBullet.animTimer = Punk.gTime + 20;
//		aBullet.body.setFixedRotation(false);
//		aBullet.body.setAngularVelocity(0);// MathUtils.random(8f,
//											// 32f)*(MathUtils.randomBoolean()?1f:-1f));
//
//		aBullet.body.setLinearVelocity(getPlayerShootVector(
//				l_player.throwAngle, l_player.throwLength));
//		if (info.hasCamera)l_player.setHeadTarget(aBullet);
//		l_player.inv.useUpItem(l_player.activeQuickSlot);
//		monsterIndex.playThrowSound();
//		return true;
//	}

	public boolean shootTargetedGrenade(int faction, Vector2 source,
			Vector2 dest, GrenadeInfo info, World world, PunkBodies monsterIndex, GenericMob srcMob) {

		Grenade aBullet = (Grenade) chunkActors.getGrenade();
		//aBullet.info.collidesWithFriends = true;
		aBullet.createBody(28, world,
				monsterIndex, source);
		if (aBullet.info.flying)
			aBullet.body.setType(BodyType.KinematicBody);
		aBullet.createBBs(world, monsterIndex);
		aBullet.set(info, faction, srcMob);
		aBullet.animTimer = Punk.gTime;
		aBullet.body.setFixedRotation(false);
		aBullet.body.setAngularVelocity(MathUtils.random(8f, 32f)
				* (MathUtils.randomBoolean() ? 1f : -1f));
		if (!info.flying)
			aBullet.body.setLinearVelocity(PhysicsActor.getTargetPt(source,
					dest, aBullet.info.throwStrength));
		else
			aBullet.body.setLinearVelocity(dest.tmp().sub(source).nor()
					.mul(info.throwStrength));

		return true;
	}

	public boolean shootGrenade(int faction, Vector2 source,
			float angle, GrenadeInfo info, World world, PunkBodies monsterIndex, GenericMob srcMob) {

		Grenade aBullet = (Grenade) chunkActors.getGrenade();
		//aBullet.info.collidesWithFriends = true;
		aBullet.createBody(28, world,
				monsterIndex, source);
		if (aBullet.info.flying)
			aBullet.body.setType(BodyType.KinematicBody);
		aBullet.createBBs(world, monsterIndex);
		aBullet.set(info, faction, srcMob);
		aBullet.animTimer = Punk.gTime;
		aBullet.body.setFixedRotation(false);
		aBullet.body.setAngularVelocity(MathUtils.random(8f, 32f)
				* (MathUtils.randomBoolean() ? 1f : -1f));
		//if (!info.flying)
			//aBullet.body.setLinearVelocity(PhysicsActor.getTargetPt(source,
					//dest, aBullet.info.throwStrength));
		//else
			aBullet.body.setLinearVelocity(tmpV.set(-info.throwStrength,0).rotate(angle));

		return true;
	}
	
	public class DestroyBlockQuery implements QueryCallback {

		@Override
		public boolean reportFixture(Fixture fixture) {
			PhysicsActor act = (PhysicsActor) fixture.getBody().getUserData();
			// Gdx.app.log("map", "fixture reported"+act.actorID);
			if (act.actorID == 26 || act.actorID == 33)
				act.deactivate();
			if (act.actorID == 7) {
				// Gdx.app.log("map", "anchor found");
				// could be at start or end of rope. replace with end actor
				ArrayList<JointEdge> joints = act.body.getJointList();
				while (joints.size() > 0) {

					PhysicsActor otherPA = (PhysicsActor) joints.remove(0).other
							.getUserData();
					/*if (otherPA instanceof RopeLink) {
						RopeLink otherRope = (RopeLink) otherPA;
						PhysicsActor ropeStub = chunkActors.add(34, 0, l_world,
								l_mi, act.body.getPosition());

						// aRope.links[i-1].linkBack = aRope.links[i];
						jd.initialize(otherRope.body, ropeStub.body,
								ropeStub.body.getPosition());
						// aRope.joints[i] =
						// (RevoluteJoint)world.createJoint(jd);

						otherRope.parentRope.joints[otherRope.actorMeta] = (RevoluteJoint) l_world
								.createJoint(jd);
						// Gdx.app.log("map", "making rope stub");
					}*/

				}
				act.deactivate();
			}

			return true;
		}

	}

	public void onExplosion() {

	}

	

	int maxUpd, maxLight, maxDay;

	public void addlightUpdate(BlockLoc currentBlock) {
		addLightUpdate(currentBlock.x, currentBlock.y);

	}

	public void addLightUpdate(int x, int y) {
		// maxLight = Math.max(updater.lightUpdateList.size(), maxLight);
		// if (updater.fucked) throw new GdxRuntimeException(" light"+x+" "+y);;
		chunkPool.addLightUpdate(x, y);
	}

	public void addDayLightUpdate(BlockLoc currentBlock) {
		addDayLightUpdate(currentBlock.x, currentBlock.y);
		// maxDay = Math.max(updater.lightUpdateList.size(), maxDay);
	}

	public void addDayLightUpdate(int mapX2, int mapY2) {
		chunkPool.addLightUpdate(mapX2, mapY2);
	}

	public void addLightUpdatesOnJoins() {
		for (int i = 0; i < Punk.CHUNKSIZE * 3; i++) {
			int gh = Chunk.getGroundHeight(LEFTMOSTBLOCK + i);
			addDayLightUpdate(LEFTMOSTBLOCK + i, currentChunkHeightID
					* Punk.CHUNKSIZE + 1);
			addDayLightUpdate(LEFTMOSTBLOCK + i, currentChunkHeightID
					* Punk.CHUNKSIZE);
			addDayLightUpdate(LEFTMOSTBLOCK + i, currentChunkHeightID
					* Punk.CHUNKSIZE - 1);

			addDayLightUpdate(LEFTMOSTBLOCK + i, (currentChunkHeightID + 1)
					* Punk.CHUNKSIZE + 1);
			addDayLightUpdate(LEFTMOSTBLOCK + i, (currentChunkHeightID + 1)
					* Punk.CHUNKSIZE);
			addDayLightUpdate(LEFTMOSTBLOCK + i, (currentChunkHeightID + 1)
					* Punk.CHUNKSIZE - 1);

			addDayLightUpdate((currentChunk) * Punk.CHUNKSIZE, BOTTOMBLOCK + i);
			addDayLightUpdate((currentChunk) * Punk.CHUNKSIZE - 1, BOTTOMBLOCK
					+ i);
			addDayLightUpdate((currentChunk) * Punk.CHUNKSIZE + 1, BOTTOMBLOCK
					+ i);

			addDayLightUpdate((currentChunk + 1) * Punk.CHUNKSIZE - 1,
					BOTTOMBLOCK + i);
			addDayLightUpdate((currentChunk + 1) * Punk.CHUNKSIZE, BOTTOMBLOCK
					+ i);
			addDayLightUpdate((currentChunk + 1) * Punk.CHUNKSIZE + 1,
					BOTTOMBLOCK + i);

			// getBlock(LEFTMOSTBLOCK+i,
			// currentChunkHeightID*Punk.CHUNKSIZE+1).dayLight = 15;
			// getBlock(LEFTMOSTBLOCK+i,
			// currentChunkHeightID*Punk.CHUNKSIZE).dayLight = 15;
			// getBlock(LEFTMOSTBLOCK+i,
			// currentChunkHeightID*Punk.CHUNKSIZE-1).dayLight = 15;

			// getBlock(LEFTMOSTBLOCK+i,
			// (currentChunkHeightID+1)*Punk.CHUNKSIZE+1).dayLight = 15;
			// getBlock(LEFTMOSTBLOCK+i,
			// (currentChunkHeightID+1)*Punk.CHUNKSIZE).dayLight = 15;
			// getBlock(LEFTMOSTBLOCK+i,
			// (currentChunkHeightID+1)*Punk.CHUNKSIZE-1).dayLight = 15;

			// getBlock((currentChunk)*Punk.CHUNKSIZE, BOTTOMBLOCK+i).dayLight =
			// 15;
			// getBlock((currentChunk)*Punk.CHUNKSIZE+1, BOTTOMBLOCK+i).dayLight
			// = 15;
			// getBlock((currentChunk+1)*Punk.CHUNKSIZE, BOTTOMBLOCK+i).dayLight
			// = 15;
			// getBlock((currentChunk+1)*Punk.CHUNKSIZE+1,
			// BOTTOMBLOCK+i).dayLight = 15;

			addLightUpdate(LEFTMOSTBLOCK + i, currentChunkHeightID
					* Punk.CHUNKSIZE);
			addLightUpdate(LEFTMOSTBLOCK + i, currentChunkHeightID
					* Punk.CHUNKSIZE - 1);

			addLightUpdate(LEFTMOSTBLOCK + i, (currentChunkHeightID + 1)
					* Punk.CHUNKSIZE);
			addLightUpdate(LEFTMOSTBLOCK + i, (currentChunkHeightID + 1)
					* Punk.CHUNKSIZE - 1);

			addLightUpdate((currentChunk) * Punk.CHUNKSIZE, BOTTOMBLOCK + i);
			addLightUpdate((currentChunk) * Punk.CHUNKSIZE + 1, BOTTOMBLOCK + i);

			addLightUpdate((currentChunk + 1) * Punk.CHUNKSIZE, BOTTOMBLOCK + i);
			addLightUpdate((currentChunk + 1) * Punk.CHUNKSIZE + 1, BOTTOMBLOCK
					+ i);

			// top
			addDayLightUpdate(LEFTMOSTBLOCK + i, (currentChunkHeightID + 2)
					* Punk.CHUNKSIZE + 1);

			addDayLightUpdate(LEFTMOSTBLOCK + i, (currentChunkHeightID + 2)
					* Punk.CHUNKSIZE);
			addDayLightUpdate(LEFTMOSTBLOCK + i, (currentChunkHeightID + 2)
					* Punk.CHUNKSIZE - 1);
			// sunlight

			/*
			 * boolean needsSunTop = (TOPBLOCK > gh-10); if (needsSunTop){ Block
			 * b = getBlock(LEFTMOSTBLOCK+i,
			 * (currentChunkHeightID+2)*Punk.CHUNKSIZE); if (b.blockID ==
			 * 0)b.dayLight = 15; } boolean needsSunM = (TOPBLOCK >
			 * gh-10-Punk.CHUNKSIZE); if (needsSunM){ Block b =
			 * getBlock(LEFTMOSTBLOCK+i,
			 * (currentChunkHeightID+1)*Punk.CHUNKSIZE); if (b.blockID ==
			 * 0)b.dayLight = 15; }
			 */

		}
	}

	public int[][][] platformData = new int[24][24][24];// x y platformLength
	public int[] platformRhythmX = new int[8], platformRhythmY = new int[8],
			platLengths = new int[8];
	public int[] maxJumps = { 7, 8, 8, 9, 10, 10, 10, 10, 11, 11, 11, 12, 12,
			12, 12, 12 };
	public Random dungeonR = new Random();

	public void makeDungeon(long seed) {
		int genCX, genCY;// coarse
		int seedOffset = 0;
		// dungeonR.setSeed(seed);
		// jump rhythm

		int rowHeight, rowWidth, rows;
		int difficulty = 1;// make jumps shorter by
		int upOff = 0, downOff = 0;
		int jumps;
		int yOffset;
		// simulate rows
		boolean rowWorks = false;
		do {
			Gdx.app.log("map", "generating a rhythm" + seedOffset);
			dungeonR.setSeed(seed + seedOffset);
			jumps = dungeonR.nextInt(4) + 4;
			yOffset = 0;
			rowWidth = 0;
			upOff = 0;
			downOff = 0;

			for (int i = 0; i < jumps; i++) {
				int jumpY = dungeonR.nextInt(9);
				int platLength = dungeonR.nextBoolean() ? dungeonR.nextInt(16)
						: dungeonR.nextInt(3);
				platformRhythmX[i] = maxJumps[jumpY];
				platformRhythmY[i] = 5 - jumpY;
				platLengths[i] = platLength;
				Gdx.app.log("map", "generating jump " + platformRhythmX[i]
						+ " " + platformRhythmY[i]);
				yOffset += platformRhythmY[i];
				rowWidth += platformRhythmX[i];
				rowWidth += platLength;
				if (yOffset > upOff)
					upOff = yOffset;
				else if (yOffset < downOff)
					downOff = yOffset;
			}
			if (yOffset > 1)
				rowWorks = true;
			else {
				Gdx.app.log("map", "discarding" + yOffset);
				seedOffset++;
			}
		} while (!rowWorks);

		// now we have a rhythm that goes up
		int rhythms = (Punk.CHUNKSIZE * 3 - 30) / rowWidth;
		rows = (Punk.CHUNKSIZE * 3 - 30) / (upOff - downOff + 2);
		int xOffset = rowWidth * rhythms;
		int headRoom = 4;
		rowHeight = rhythms * yOffset + headRoom + upOff + downOff;
		boolean rowLeft = true;
		int yProgress = 15;

		// Gdx.app.log("map", "writing stair xO "+xOffset + " yoff "+yOffset);
		// jump row
		makePlatformFloor(yProgress - 6, xOffset);
		makePlatformRow(yProgress, xOffset, yOffset, upOff, downOff, rhythms,
				rowHeight, jumps, difficulty, 0, rowLeft);
		makeStair(yProgress - downOff + (yOffset * rhythms), xOffset, 4, upOff
				- downOff + floorThickness + 8, rowLeft, false);

		// jump row
		yProgress += rowHeight + 16;
		rowLeft = !rowLeft;
		makePlatformFloor(yProgress - 4, xOffset);
		makePlatformRow(yProgress, xOffset, yOffset, upOff, downOff, rhythms,
				rowHeight, jumps, difficulty, 0, rowLeft);

		// special
		// makePlatformFloor(yProgress-4, xOffset);
		int specialHeight = 64;// rowHeight;//dungeonR.nextInt(64)+64;
		makeStair(yProgress - downOff + (yOffset * rhythms), xOffset, 2, upOff
				- downOff + floorThickness + 8 + specialHeight, rowLeft, false);

		yProgress += rowHeight + 16;
		rowLeft = !rowLeft;
		makePlatformFloor(yProgress - 4, xOffset);
		// long stair
		int wallThickness = 5;
		// makeStair(yProgress-downOff+(yOffset* rhythms), xOffset, 2,
		// upOff+specialHeight+floorThickness+8, !rowLeft, true);
		// stair walls inside
		makeInsideStairWall(yProgress - 8, xOffset, 2, specialHeight + 16,
				!rowLeft, true);
		// outside
		makeInsideStairWall(yProgress - 8, xOffset, 2, specialHeight + 16,
				!rowLeft, false);
		// entrance at inside corner

		// shaft

		// hazard at bottom

		// tunnel

		// exit

		// exit other end

		yProgress += specialHeight + 16;
		rowLeft = !rowLeft;

		makePlatformFloor(yProgress - 4, xOffset);
		// makePlatformRow(yProgress, xOffset, yOffset, upOff, downOff, rhythms,
		// rowHeight, jumps, difficulty, 0, true);
		// makeStair(yProgress-downOff+(yOffset* rhythms), xOffset,
		// upOff-downOff+floorThickness+8, true);

		getBlock(-1000000000, 0).set(1, 0);
		loadEverything();
	}

	int floorBlock = 82, floorThickness = 8, wallThickness = 4, wallBlock = 50;;

	private void makePlatformFloor(int y, int width) {
		for (int j = 0; j < floorThickness; j++)
			for (int i = 0; i < width; i++) {
				changeBlock(i + 15, y - j, floorBlock, 0, true);// wallBlock,
																// 0);
			}

	}

	private void makeWall(int x, int y, int width, int height) {
		for (int j = y; j < y + height; j++)
			for (int i = x; i < x + width; i++) {
				changeBlock(i, j, wallBlock, 0, true);// wallBlock, 0);
			}

	}

	private void makeInsideStairWall(int y, int rowWidth, int width,
			int height, boolean left, boolean cont) {
		int x = left ? 15 - width : rowWidth + 16 + width, count = 0;
		Gdx.app.log("stair", "x " + x);
		int stepY = 4;
		boolean stepL = cont ? !left : left;
		int continueVal = cont ? 1 : 0;
		while (stepY < height + 5 || count % 2 == continueVal) {
			if (stepL) {
				changeBlock(x - 2 - width, y + stepY, wallBlock, 0, true);
				changeBlock(x - 3 - width, y + stepY, wallBlock, 0, true);

			} else {
				changeBlock(x + 2 + width, y + stepY, wallBlock, 0, true);
				changeBlock(x + 3 + width, y + stepY, wallBlock, 0, true);
			}
			// stepL = !stepL;
			count++;
			stepY += 1;
		}
	}

	private void makePlatformRow(int yProgress, int xOffset, int yOffset,
			int upOff, int downOff, int rhythms, int rowHeight, int jumps,
			int difficulty, int hazard, boolean isLeft) {
		// Gdx.app.log("map", "done "+ "rhy:"+rhythms + " h:"+rowHeight +
		// " j: "+jumps + " r:"+ rhythms);
		int x = 0;
		int y = yProgress - downOff;
		for (int i = 0; i < rhythms; i++) {
			for (int r = 0; r < jumps; r++) {
				// write platform
				// Gdx.app.log("map", "writing platform "+y);
				writePlatform(x - difficulty, y, platLengths[r] + difficulty,
						xOffset, isLeft);
				x += platLengths[r];
				x += platformRhythmX[r];
				y += platformRhythmY[r];

			}
		}
		writePlatform(x - difficulty, y, 16, xOffset, isLeft);

		Gdx.app.log("map", "done writing platforms, level 1");
	}

	private void makeStair(int y, int rowWidth, int width, int height,
			boolean left, boolean cont) {
		// x and y are starting positions, will already be valid
		int x = left ? 15 - width : rowWidth + 16 + width, count = 0;
		Gdx.app.log("stair", "x " + x);
		int stepY = 4;
		boolean stepL = cont ? !left : left;
		int continueVal = cont ? 1 : 0;
		while (stepY < height + 5 || count % 2 == continueVal) {
			if (stepL) {
				changeBlock(x - 0 - width, y + stepY, floorBlock, 0, true);
				changeBlock(x - 1 - width, y + stepY, floorBlock, 0, true);
				changeBlock(x - 1 - width, y + stepY + 1, floorBlock, 0, true);
				changeBlock(x - 1 - width, y + stepY + 2, floorBlock, 0, true);
				changeBlock(x - 1 - width, y + stepY + 3, 50, 0, true);
			} else {
				changeBlock(x + 0 + width, y + stepY, floorBlock, 0, true);
				changeBlock(x + 1 + width, y + stepY, floorBlock, 0, true);
				changeBlock(x + 1 + width, y + stepY + 1, floorBlock, 0, true);
				changeBlock(x + 1 + width, y + stepY + 2, floorBlock, 0, true);
				changeBlock(x + 1 + width, y + stepY + 3, 50, 0, true);
			}
			stepL = !stepL;
			count++;
			stepY += 4;
		}

	}

	private void writePlatform(int xpos, int y, int length, int xOffset,
			boolean left) {
		for (int x = xpos; x <= xpos + length; x++)
			changeBlock((left ? xOffset - x : x) + 15, y, floorBlock, 0, true);
		changeBlock((left ? xOffset - xpos - length : xpos + length) + 15,
				y + 1, 50, 0, true);
	}

	public void openDungeon() {
		// starts dungeon mode and generates first dungeon based on
		// player.activeDoor
		enterDungeonMode();
		makeDungeon((l_player.y << 16) + l_player.x);
	}

	public BlockLoc worldDoorLoc = new BlockLoc();

	public void enterDungeonMode() {
		dungeonMode = true;
		openWorld = false;
		worldDoorLoc.set(l_player.activeDoor.x, l_player.activeDoor.y);
		saveAllChunks();
		clearAllChunks();
		// CHUNKOFFSET = -Punk.CHUNKSIZE;
		currentChunk = 1;
		currentChunkHeightID = 1;
		CHUNKOFFSET = currentChunk * (Punk.CHUNKSIZE);
		CHUNKOFFSETY = currentChunkHeightID * Punk.CHUNKSIZE;
		CHUNKTOPY = CHUNKOFFSETY + Punk.CHUNKSIZE;
		RIGHTMOSTBLOCK = CHUNKOFFSET + Punk.CHUNKSIZE * 2;
		LEFTMOSTBLOCK = CHUNKOFFSET - Punk.CHUNKSIZE;
		TOPBLOCK = CHUNKOFFSETY + Punk.CHUNKSIZE * 2;
		BOTTOMBLOCK = CHUNKOFFSETY - Punk.CHUNKSIZE;
		l_player.body.setTransform(20, 20, 0);
		// border

	}

	public void exitDungeonMode(Player player) {
		dungeonMode = false;
		openWorld = true;
		recalculatePositionConstants(player);
	}

	public void timedSpawnUpdate() {

	}

	public void clearAllChunks() {
		Iterator<Chunk> iter = chunkPool.entries().iterator();
		boolean done = false;
		while (!done && iter.hasNext()) {
			// Entry<Chunk> e = iter.next();
			Chunk c = iter.next();// e.value;
			c.clear();

		}
		;
	}

	public boolean isInValidArea(int x, int y) {
		return (x >= LEFTMOSTBLOCK && x <= RIGHTMOSTBLOCK && y >= BOTTOMBLOCK && y <= TOPBLOCK);
	}

	public void discardOutOfRange(PGenericMob pool) {
		Iterator<GenericMob> i = pool.monsterList.iterator();
		while (i.hasNext()) {
			GenericMob m = i.next();
			if (!isInValidArea(m.x, m.y))
				m.deactivate();
		}
	}

	private void discardOutOfRange() {
		// TODO Auto-generated method stub
		discardOutOfRange(chunkActors.mobPool[3]);
	}

	public static int updateState = 0;
	public long[] updateTimes = new long[11], updateData = new long[11],
			updateLast = new long[11];
	// public long statetimer1, statetimer2;
	public int updateCount = 0, updateCountCoarse;
	public boolean saturated = false;
	/*
	public void update(Player player) {

		//finishLoadingMap();
		//if (true) return;
		
		// Gdx.app.log(TAG, "map state: "+updateState);
		// statetimer1 = System.nanoTime();
		if (doTimedUpdates())
			return;
		updateCount++;

		switch (updateState) {
		case 0:// fetch

			if (updateFetch(l_player)) {
				updateState++;
				// update(player);
				// completeLogState(updateCount);

			}
			break;
		case 1:// postfetch
			if (checkPostFetch(true)) {
				// if (allChunksLoaded)
				updateState++;
				// update(player);
				// else updateState = 0;
				// completeLogState(updateCount);
			}
			break;
		case 20://
			if (updateTrees()) {
				updateState++;
				// update(player);
				// completeLogState(updateCount);
			} // else updateState = 0;

			break;
		case 2:
			if (checkInsideLight()) {
				updateState++;
				// update(player);
			}
			break;
		case 40:// blocks
			updateBlocks(BLOCKUPDATEREPS, l_world, l_mi);
			if (updater.blockUpdateList.list.isEmpty()) {
				updateState++;
				// update(player);
			}
			// completeLogState(updateCount);

			break;

		case 3://
			updater.updateSky();

			if (updater.lightFinished()) {
				// if (allChunksLoaded){

				updateState++;
				update(player);
				// }
				// else updateState ++;//= 0;
				// updateState++;
				// completeLogState(updateCount);
			}
			break;

		case 4:
			if (!chunkPool.updateSave(false)) {
				updateState++;
				// update(player);
			}
			// updateState = 0;

			break;

		case 5:
			if (!chunkPool.updateFetchQueue(player)) {

				saturated = true;
			} else
				saturated = false;
			updateState++;

			break;
		case 6:

			if (miniMap.checkZoom(l_player.zoomLevel)) {
				// Gdx.app.log(TAG, "mesh");
				miniMap.makeMesh(currentChunk, l_player.gameInfo.gameSeed);

			}// else update(player);
			updateState++;
			break;
		case 7:
			updateSpawns();

		default:
			updateState = 0;
		}
		// statetimer1 -= System.nanoTime();
		// logState(-statetimer1);
	}

	

	/*
	 * case 9999://save if (chunkPool.updateSave(false)){ if
	 * (allChunksLoaded)updateState++; else updateState= 0;
	 * completeLogState(updateCount); } break;
	 */

	public void logState(long nano) {

		updateData[updateState] += nano;
	}

	private void completeLogState(int count) {
		int u;
		if (updateState != 0) {
			u = updateState - 1;
		} else
			u = 6;
		if (count > 0) {
			updateLast[u] = updateData[u] / count;
			if (updateTimes[u] < updateData[u])
				updateTimes[u] = updateData[u];
			updateCountCoarse++;
			if (updateCountCoarse > 300) {
				clearLogs();
				updateCountCoarse = 0;
			}
		}
		updateCount = 0;
		updateData[u] = 0;
	}

	private void clearLogs() {
		for (int i = 0; i < 8; i++) {
			updateTimes[i] = 0;
		}

	}

	public void sortLists2() {
		//updater.removeDupes();

	}

	/*public void printStats() {
		Gdx.app.log(TAG, "day:" + updater.dayLightUpdateList.list.size()
				+ "light:" + updater.lightUpdateList.list.size() + "block:"
				+ updater.blockUpdateList.list.size()

		);

	}

	public void printLists2() {
		Gdx.app.log(TAG, "" + "  l:: " + updater.lightUpdateList.list.size()
				+ "  d: " + updater.dayLightUpdateList.list.size() + "  u: "
				+ updater.blockUpdateList.list.size() + " t: "
				+ updater.timedUpdateList.list.size() + "/" + timedUpdatesLeft
				+ "   max Light   " + updater.maxDUpdates);
	}
*/
	

	public void rasterCircle(int x0, int y0, int radius, int outerID,
			int innerID) {
		int f = 1 - radius;
		int ddF_x = 1;
		int ddF_y = -2 * radius;
		int x = 0;
		int y = radius;

		// // setPixel(x0, y0 + radius);
		// setPixel(x0, y0 - radius);
		// setPixel(x0 + radius, y0);
		// setPixel(x0 - radius, y0);
		// horizLine(x0, y0-radius, x0, y0-radius, outerID, innerID);
		// , outerID, innerID);
		// dot at top and bottom here
		horizLine(x0 - radius, y0, x0 + radius, outerID, innerID);

		while (x < y) {
			// ddF_x == 2 * x + 1;
			// ddF_y == -2 * y;
			// f == x*x + y*y - radius*radius + 2*x - y + 1;
			if (f >= 0) {
				y--;
				ddF_y += 2;
				f += ddF_y;
			}
			x++;
			ddF_x += 2;
			f += ddF_x;
			horizLine(x0 - x, y0 + y, x0 + x, outerID, innerID);
			horizLine(x0 - x, y0 - y, x0 + x, outerID, innerID);
			horizLine(x0 - y, y0 + x, x0 + y, outerID, innerID);
			horizLine(x0 - y, y0 - x, x0 + y, outerID, innerID);
			// setPixel(x0 + x, y0 + y);
			// setPixel(x0 - x, y0 + y);
			// setPixel(x0 + x, y0 - y);
			// setPixel(x0 - x, y0 - y);
			// setPixel(x0 + y, y0 + x);
			// setPixel(x0 - y, y0 + x);
			// setPixel(x0 + y, y0 - x);
			// setPixel(x0 - y, y0 - x);
		}
	}

	public void horizLine(int x0, int y0, int x1, int outID, int inID) {
		qChange(x0, y0, outID, 0, currentPlane);
		for (int x = x0 + 1; x < x1; x++)
			qChange(x, y0, inID, 0, currentPlane);
		qChange(x1, y0, outID, 0, currentPlane);
	}

	public void vertLine(int x0, int y0, int y1, int outID, int inID) {
		qChange(x0, y0, outID, 0, currentPlane);
		if (y0 > y1) {
			int t = y0;
			y0 = y1;
			y1 = t;
		}
		for (int y = y0 + 1; y < y1; y++)
			qChange(x0, y, inID, 0, currentPlane);
		qChange(x0, y1, outID, 0, currentPlane);
	}

	public void rect(int x0, int y0, int x1, int y1, int outID, int outM,
			int inID, int inM, boolean n, boolean e, boolean s, boolean w) {

		if (y0 > y1) {
			int t = y0;
			y0 = y1;
			y1 = t;
		}
		if (x0 > x1) {
			int t = x0;
			x0 = x1;
			x1 = t;
		}
		for (int x = x0 + 1; x < x1; x++) {

			qChange(x, y0, s ? outID : inID, s ? outM : inM, currentPlane);
			for (int y = y0 + 1; y < y1; y++)
				qChange(x, y, inID, inM, currentPlane);
			qChange(x, y1, n ? outID : inID, n ? outM : inM, currentPlane);
		}

		qChange(x0, y0, w || s ? outID : inID, w || s ? outM : inM, currentPlane);
		for (int y = y0 + 1; y < y1; y++)
			qChange(x0, y, w ? outID : inID, w ? outM : inM, currentPlane);
		qChange(x0, y1, w || n ? outID : inID, w || n ? outM : inM, currentPlane);

		qChange(x1, y0, e || s ? outID : inID, e || s ? outM : inM, currentPlane);
		for (int y = y0 + 1; y < y1; y++)
			qChange(x1, y, e ? outID : inID, e ? outM : inM, currentPlane);
		qChange(x1, y1, e || n ? outID : inID, e || n ? outM : inM, currentPlane);
	}

	public void makeHumanCity(int x, int y, int seed) {
		// //x,y are at centre
		// doors are 0-8+id*8
		// assume flat ground
		Random ran = Chunk.seededRandom;
		ran.setSeed(seed);
		int radius = ran.nextInt(3) + 8 + 28;
		boolean left = ran.nextBoolean();
		// towers/walls at each end, walkway at top
		int towWidth = ran.nextInt(2) + 11, towHeight = ran.nextInt(8) + 9;
		/*
		 * vertLine(x-radius-towWidth, y, y+towHeight, towID, towID);
		 * vertLine(x-radius, y, y+towHeight+houseHeight*3, towID, towID);
		 * 
		 * vertLine(x+radius+towWidth, y, y+towHeight, towID, towID);
		 * vertLine(x+radius, y, y+towHeight+houseHeight*3, towID, towID);
		 * 
		 * //walkway horizLine(x-radius-towWidth-2, y+towHeight,
		 * x+radius+towWidth+2, towID, towID);
		 * 
		 * horizLine(x-radius, y+towHeight+houseHeight, x+radius, towID, towID);
		 * 
		 * horizLine(x-radius, y+towHeight+houseHeight*2, x+radius, towID,
		 * towID);
		 * 
		 * horizLine(x-radius, y+towHeight+houseHeight*3, x+radius, towID,
		 * towID);
		 */
		// makeDoor(x-radius+towWidth+2, y, 0, x-radius+towWidth+2,
		// y+towHeight+4, 0);
		// makeDoor(x-radius+towWidth+2, y+towHeight+4, 0, x-radius+towWidth+2,
		// y, 0);

		// makeDoor(x+radius-towWidth-2, y, 0, x+radius-towWidth-2,
		// y+towHeight+4, 0);
		// makeDoor(x+radius-towWidth-2, y+towHeight+4, 0, x+radius-towWidth-2,
		// y, 0);

		// floor-to-floor stairs
		int floors = ran.nextInt(6) + 2;

		// rooms under walkway
		int roomHeight = 10;

		// outline
		rect(x - radius, y + towHeight, x + radius, y + towHeight + roomHeight
				* floors + 3, 82, 25, 0, 1, false, true, false, true);

		rect(x - radius, y - 1, x + radius, y + towHeight, 82, 25, 0, 1, false,
				false, true, false);

		rect(x - radius + towWidth, y + towHeight, x + radius - towWidth, y
				+ towHeight + 3, 82, 25, 0, 1, true, true, true, true);

		for (int i = 0; i < floors - 1; i++) {// floors
			rect(x - radius + towWidth, y + towHeight + roomHeight * (i + 1), x
					+ radius - towWidth, y + towHeight + roomHeight * (i + 1)
					+ 3, 82, 25, 0, 1, true, true, true, true);

		}

		// rect(x+radius, y+towHeight, x+radius-towWidth, y+towHeight*3+3, 82,
		// 25, 0, 1, false, true, true, false);

		// steps
		for (int iy = y + towHeight; iy < y + towHeight + roomHeight * floors; iy += (roomHeight)) {
			rect(x - radius + towWidth - 2, iy + 1, x - radius + towWidth,
					iy + 3, 82, 25, 0, 1, true, true, true, true);
			changeBlock(x - radius + towWidth - 1, iy + 2, 50, 3, false);
			rect(x - radius, iy + (roomHeight) / 2 + 1, x - radius + 2, iy
					+ (roomHeight) / 2 + 3, 82, 25, 0, 1, true, true, true,
					true);
			changeBlock(x - radius + 1, iy + (roomHeight) / 2 + 2, 50, 3, false);
			rect(x + radius - towWidth + 3, iy, x + radius - towWidth, iy + 3,
					82, 25, 0, 1, true, true, true, true);
			changeBlock(x + radius - towWidth + 1, iy + 2, 50, 3, false);
			rect(x + radius, iy + (roomHeight) / 2 + 1, x + radius - 2, iy
					+ (roomHeight) / 2 + 3, 82, 25, 0, 1, true, true, true,
					true);
			changeBlock(x + radius - 1, iy + (roomHeight) / 2 + 2, 50, 3, false);
		}

		boolean stairsLeft = left;
		int sx = x + (radius - towWidth - 2) * (stairsLeft ? -1 : 1), sx2 = x
				+ (radius - towWidth - 2) * (stairsLeft ? 1 : -1), sxm = x;

		makeDoor(sxm, y, 2, sxm, y + towHeight + 4, 0);
		// makeDoor(sxm , y+towHeight+4, 3, sxm, y, 0);
		changeBlock(sxm, y + towHeight + 4 + 3, 50, 3, true);

		for (int sy = y + towHeight + 4; sy < y + towHeight + roomHeight
				* floors + 1; sy += roomHeight) {
			changeBlock(sx, sy + 3, 50, 3, true);
			makeDoor(sx, sy, 2, sx, sy + roomHeight, 0);
			// makeDoor(sx, sy+roomHeight, 3, sx, sy, 0);

			changeBlock(sx2, sy + 3, 50, 3, true);
			makeDoor(sx2, sy, 2, sx2, sy + roomHeight, 0);
			// makeDoor(sx2, sy+roomHeight, 3, sx2, sy, 0);

			// makeDoor(sxm, sy, 2, sx, sy+roomHeight, 0);
			changeBlock(sxm, sy + roomHeight + 3, 50, 3, true);
			makeDoor(sxm, sy + roomHeight, 3, sxm, sy, 0);

			// random torches
			for (int tx = sx, ty = sy + roomHeight; tx <= sx2; tx += MathUtils
					.random(16) + 3)
				changeBlock(tx, ty, 50, 3, true);

			stairsLeft = !stairsLeft;
		}

		// balcony/top part
		int balcR = x + radius + ran.nextInt(20), balcL = x - radius
				- ran.nextInt(20), balcY = y + towHeight + roomHeight
				* (floors), balcRepeats = ran.nextInt(2) + 2, balcHeight = 0;

		for (int i = 0; i < balcRepeats; i++) {

			int newbalcR = balcR + ran.nextInt(30) + 1, newbalcL = balcL
					- ran.nextInt(30) - 1;

			rect(balcL, balcY + balcHeight, balcR, balcY + balcHeight + 3, 82,
					25, 0, 1, true, false, true, false);

			// grate
			rect(newbalcL, balcY + balcHeight, balcL, balcY + balcHeight + 3,
					83, 0, 0, 0, true, false, true, true);// l

			rect(newbalcR, balcY + balcHeight, balcR, balcY + balcHeight + 3,
					83, 0, 0, 0, true, true, true, false);// r

			// rooms

			int balcDiff = ran.nextInt(8) + 8;

			rect(newbalcL, balcY + balcHeight + 4, newbalcR, balcY + balcHeight
					+ balcDiff, 83, 0, 0, 1, false, true, false, true);// rooms

			changeBlock((balcL + balcR) / 2 - 6, balcY + balcHeight + 6, 50, 3,
					true);

			changeBlock(sx2, balcY + balcHeight + 6, 50, 3, true);
			makeDoor(sx2, balcY + balcHeight + 4, 2, sx2, balcY + balcHeight
					+ balcDiff + 4, 0);

			changeBlock(sx, balcY + balcHeight + 6, 50, 3, true);
			makeDoor(sx, balcY + balcHeight + 4, 2, sx, balcY + balcHeight
					+ balcDiff + 4, 0);

			changeBlock(sxm, balcY + balcHeight + 6, 50, 3, true);
			makeDoor(sxm, balcY + balcHeight + balcDiff + 4, 3, sxm, balcY
					+ balcHeight + 4, 0);

			// random torches
			for (int tx = balcL, ty = balcY + balcHeight + 5; tx <= balcR; tx += ran
					.nextInt(16) + 3)
				changeBlock(tx, ty, 50, 3, true);

			balcHeight += balcDiff;
			balcR = newbalcR;
			balcL = newbalcL;

		}

		int topHeight = balcY + balcHeight + ran.nextInt(8) + 3;
		// top
		rect(balcL, balcY + balcHeight, balcR, balcY + balcHeight + 3, 82, 25,
				0, 1, true, true, true, true);// rooms

		changeBlock((balcL + balcR) / 2 - 6, balcY + balcHeight + 6, 50, 3,
				true);

		changeBlock(sx2, balcY + balcHeight + 6, 50, 3, true);
		// makeDoor(sx2, balcY+balcHeight+4, 2, sx2,
		// balcY+balcHeight+balcDiff+4, 0);

		// throne room

		// info to save?
		// bounds, throne room(target for invasions)

		//
		flushChanges();
		//finishLoadingMap();

	}

	public void makeDoor(int x, int y, int id, int destX, int destY,
			int destPlane) {
		chunkPool.getChunkWorld(x, y).createDoor(x, y, id, destX, destY,
				destPlane);
	}

	public void makeElfCity(int x, int y, int seed) {

	}
//	public void buildFeatures(){
//		for (int i = 0; i < Chunk.ways.size; i++){
//			Waypoint w = Chunk.ways.get(0);
//			if (!w.built){
//				switch (w.id){
//				case 28://human city
//						makeHumanCity(w.loc.x, w.loc.y, w.seed);
//					break;
//					
//				case 29://elf city
//					
//					break;
//				}
//			}
//		}
//	}

	Vector2 globalTouchedV = new Vector2();
	private Vector2 rayA = new Vector2();
	private Vector2 rayB = new Vector2();
	private Vector2 hmod = new Vector2();
	private Vector2 vmod = new Vector2();
	private Vector2 tmpBV = new Vector2(), D = new Vector2(), A = new Vector2();;
	public BlockLoc lastDigTargetBlock = new BlockLoc();
	public boolean checkSurroundingBlocks(int x, int y, boolean blockFlag){
		return (checkBlock(x+1,y, blockFlag) || checkBlock(x-1,y, blockFlag) ||
				checkBlock(x,y+1, blockFlag) || checkBlock(x,y-1, blockFlag));
	}
	public boolean checkBlock(int x, int y, boolean blockFlag){
		if (blockFlag){//looking for solids
			int bt = getBlock(x,y).blockType();
			if (bt>1) return true;
			lastDigTargetBlock.set(x,y);
			return false;
		}
		//digging
		if (getBlock(x,y).blockType()>1) return true;
		lastDigTargetBlock.set(x,y);
		return false;
	}
	public Vector2 rayCastForPlace(Vector2 P, float angle, RayType type, int range, float minRange){
		D.set(1,0);
		D.rotate(angle);
		D.add(P);
		float m = D.tmp().sub(P).y;
		//m = -1f/m;
		m /= D.tmp().sub(P).x;
		float c = -m*P.x+P.y;
		if ( m == 1f/0f) m = 1000000;
		else if (m == -1f/0f) m = -1000000;
		if (m == 0f) m = .0000001f;
		else if (m > 1000000) m = 1000000;
		tmpBV.set(-1, -1);
		boolean done;
		
		boolean isUp = D.y > P.y, isLeft = D.x < P.x, hdone = false, vdone = false;

		float Xa = 1f/m, Ya = m;

		Xa *= (isUp?1:-1);
		Ya *= (isLeft?-1:1);
		
		
		
		//horizontal (x-wise)
		A.x = MathUtils.floor(P.x)+ (isLeft?0:1);
		A.y = m * A.x + c;//MathUtils.floor(P.y) + Ya ;
		//Gdx.app.log("punk", "A0 "+A + " m "+ m);
		done =false;
		
		rayA.set(A);
		while (!done && !hdone){
			float dst = Math.max(Math.abs(A.x-P.x), Math.abs(A.y - P.y));
			if (dst > range)done = true;
			else if ( checkBlock(MathUtils.floor(A.x+(isLeft?-.01f:.01f)), MathUtils.floor(A.y+.001f), true)){
				rayA.set(A.x+(isLeft?.011f:-.011f), A.y);
				hdone = true;									
		
			}		
			//Gdx.app.log("rayaaaa", "A "+A + " Ya " + Ya + " m "+m);
			A.add(isLeft?-1:1, Ya);	
		}
		
		//y-wise
		A.y =  MathUtils.floor(P.y)+ (isUp?1:0)  ;
		A.x = (A.y - c)/m;
		rayB.set(A);
		//Gdx.app.log("punk", "A1 "+A + " c " + c);
		done = false;
		
		while (!done && !vdone){
			float dst = Math.max(Math.abs(A.x-P.x), Math.abs(A.y - P.y));
			if (dst > range)done = true; 
			else{
				if (checkBlock(MathUtils.floor(A.x+.00f), MathUtils.floor(A.y+(isUp?+.01f:-0.01f)), true)){		
					rayB.set(A.x, A.y+(isUp?-.011f:.011f));//.x, A.y+(isUp?0:1 ));															
					vdone = true;		
				}
			}		
			
			//Gdx.app.log("raybb", "A "+A + Xa + " m " + m);
			A.add(Xa, isUp?1.00000000f:-1.0000000f);
		
		}
		
		
		//compare 2 blocks, find closest
			if (!hdone && !vdone){return tmpBV.set(0,-1000000);}
			else
			if (!hdone){
				//if (rayA.dst2(P) < range){
				//Gdx.app.log("rayc", "cpmpuiuoituio"); 
					tmpBV.set(rayB);//.add(0, isUp?-1.001f:0);//.sub(Xa, 0);
					globalTouchedV.set(rayB);
				//}
			} else
				if (!vdone){
					//if (rayB.dst2(P) < range){
						tmpBV.set(rayA);//.add(isLeft?0:-.99f, -.001f);
						globalTouchedV.set(rayA);
					//}
			} else {
				if (rayA.dst2(P) < rayB.dst2(P)){
						//Gdx.app.log("rayc", "cpmpuiuoituio"); 
						tmpBV.set(rayA);//.add(isLeft?0:-.99f,-.001f );;//.sub(0,Ya);
						globalTouchedV.set(rayA);
				}
				else {
					tmpBV.set(rayB);//.add(0, isUp?-.001f:0.001f);//.sub(Xa, 0);
					globalTouchedV.set(rayB);
					
				}
			}
			
			//Gdx.app.log("rayc", "returning "+tmpBV + "h " + hdone + " v "+vdone);
			return tmpBV;//.set(MathUtils.floor(tmpBV.x), MathUtils.floor(tmpBV.y));//tmpBV;
		
	}
	public Vector2 rayCastForDig(Vector2 P, float angle, RayType type, int range, float minRange){
		D.set(1,0);
		D.rotate(angle);
		D.add(P);
		float m = D.tmp().sub(P).y;
		//m = -1f/m;
		m /= D.tmp().sub(P).x;
		float c = -m*P.x+P.y;
		if ( m == 1f/0f) m = 1000000;
		else if (m == -1f/0f) m = -1000000;
		if (m == 0f) m = .0000001f;
		else if (m > 1000000) m = 1000000;
		tmpBV.set(-1, -1);
		boolean done;
		
		boolean isUp = D.y > P.y, isLeft = D.x < P.x, hdone = false, vdone = false;

		float Xa = 1f/m, Ya = m;

		Xa *= (isUp?1:-1);
		Ya *= (isLeft?-1:1);
		
		
		
		//horizontal (x-wise)
		A.x = MathUtils.floor(P.x)+ (isLeft?0:1);
		A.y = m * A.x + c;//MathUtils.floor(P.y) + Ya ;
		//Gdx.app.log("punk", "A0 "+A + " m "+ m);
		done =false;
		
		rayA.set(A);
		while (!done && !hdone){
			float dst = Math.max(Math.abs(A.x-P.x), Math.abs(A.y - P.y));
			if (dst > range)done = true;
			else if ( checkBlock(MathUtils.floor(A.x+(isLeft?-.01f:.01f)), MathUtils.floor(A.y+.001f), true)){
				rayA.set(A.x+(isLeft?-.011f:.011f), A.y);
				hdone = true;									
		
			}		
			//Gdx.app.log("rayaaaa", "A "+A + " Ya " + Ya + " m "+m);
			A.add(isLeft?-1:1, Ya);	
		}
		
		//y-wise
		A.y =  MathUtils.floor(P.y)+ (isUp?1:0)  ;
		A.x = (A.y - c)/m;
		rayB.set(A);
		//Gdx.app.log("punk", "A1 "+A + " c " + c);
		done = false;
		
		while (!done && !vdone){
			float dst = Math.max(Math.abs(A.x-P.x), Math.abs(A.y - P.y));
			if (dst > range)done = true; 
			else{
				if (checkBlock(MathUtils.floor(A.x+.00f), MathUtils.floor(A.y+(isUp?+.01f:-0.01f)), true)){		
					rayB.set(A.x, A.y+(isUp?.011f:-.011f));//.x, A.y+(isUp?0:1 ));															
					vdone = true;		
				}
			}		
			
			//Gdx.app.log("raybb", "A "+A + Xa + " m " + m);
			A.add(Xa, isUp?1.0f:-1.0f);
		
		}
		
		
		//compare 2 blocks, find closest
			if (!hdone && !vdone){return tmpBV.set(0,-1000000);}
			else
			if (!hdone){
				//if (rayA.dst2(P) < range){
				//Gdx.app.log("rayc", "cpmpuiuoituio"); 
					tmpBV.set(rayB);//.add(0, isUp?-1.001f:0);//.sub(Xa, 0);
					globalTouchedV.set(rayB);
				//}
			} else
				if (!vdone){
					//if (rayB.dst2(P) < range){
						tmpBV.set(rayA);//.add(isLeft?0:-.99f, -.001f);
						globalTouchedV.set(rayA);
					//}
			} else {
				if (rayA.dst2(P) < rayB.dst2(P)){
						//Gdx.app.log("rayc", "cpmpuiuoituio"); 
						tmpBV.set(rayA);//.add(isLeft?0:-.99f,-.001f );;//.sub(0,Ya);
						globalTouchedV.set(rayA);
				}
				else {
					tmpBV.set(rayB);//.add(0, isUp?-.001f:0.001f);//.sub(Xa, 0);
					globalTouchedV.set(rayB);
					
				}
			}
			
			//Gdx.app.log("rayc", "returning "+tmpBV + "h " + hdone + " v "+vdone);
			return tmpBV;//.set(MathUtils.floor(tmpBV.x), MathUtils.floor(tmpBV.y));//tmpBV;
		
	}

	public void createItem(Item it, int x, int y) {
		createItem(it.id, it.amount, it.meta, it.durability, x, y);
		
	}

	public void createItem(int iID, int iAm, int meta, long durability, int mapX, int mapY) {
		itemPool.createItem(iID, iAm, meta, durability, l_world, l_mi, this, tmpV.set(mapX+.5f, mapY+.5f));
		
	}

	public void clear() {
		itemPool.clear();
		
	}
	BlockLoc bLoc = PunkBlockList.pool.obtain();
	public void updateSlow() {
		Iterator<Chunk> iter = chunkPool.entries().iterator();
		
		
		
		while (iter.hasNext()) {

			Chunk c = iter.next();
			
			if (c.isPostFetched) {
				for (int i = 0; i < 12; i++){
					int rx = MathUtils.random(Punk.CHUNKSIZE-1), ry = MathUtils.random(Punk.CHUNKSIZE-1);;
					Block b = c.showingBlock[((rx)<<Punk.CHUNKBITS)+(ry)];
					bLoc.set(c.xOffset+rx, c.yOffset+ry);
					getBlockDef(b.blockID).updateSlow(bLoc, b, this);
				}
				
				// queueLightInside(c);
			}

		}
		//PunkBlockList.pool.free(loc);
		
		
		
		
	}

	
	

	public BlockMoverPool blockMoverPool;// = new BlockMoverPool();
	public void createBlockMover(int x, int y, Block b, int type) {
		blockMoverPool.add(x, y, type, b);
		
	}

	public byte getBackgroundBlock(int mx, int my) {
		return chunkPool.getBackgroundBlock(mx, my);
		
	}

	public static boolean getFlammable(int blockID) {
		if (blockID < 0) return ChunkPool.sBlockDefs[0].flammable;
		return ChunkPool.sBlockDefs[blockID].flammable;
	}
	public static BlockDef getBlockDef(int id) {
		if (id < 0) return ChunkPool.defaultBlockDef;
		return ChunkPool.sBlockDefs[id];
	}

	public static byte getBlockType(int blockID) {
		if (blockID < 0) return ChunkPool.sBlockDefs[0].blockType;
		return ChunkPool.sBlockDefs[blockID].blockType;
	}

	public void addTimedUpdate(BlockLoc tmpB) {
		addTimedUpdate(tmpB.x, tmpB.y);
		
	}

	public void addLightUpdatesSurrounding(BlockLoc loc) {
		addLightUpdate(loc.x-1, loc.y);
		addLightUpdate(loc.x+1, loc.y);
		addLightUpdate(loc.x, loc.y+1);
		addLightUpdate(loc.x, loc.y-1);
		//Gdx.app.log(TAG, "surrounding");
	}

	public void completeFetch() {
		fetchDone = false;
		threadRuns = 0;
		while (!fetchDone) updateThreadedFetch(l_player);
		Gdx.app.log(TAG, "complete fetch "+threadRuns);
	}

	public void beam(int strength, BlockDamageType type, int x0, int y0, int x1, int y1) {
		int count = 0, length = 256;
		
		switch (type){
		case CHARGE:
		{
				IntArray blocks = getLightningLine(x0, y0, x1, y1);
				//Gdx.app.log(TAG, "beam "+blocks.size);
				int x = blocks.get(count*2), y = blocks.get(count*2+1);
				
				count = 1;
				while (strength > 0 && count < length && count*2 < blocks.size-1){
					
					//add by maxTrans, dump into air/automata
					int maxTrans = 4;
					x = blocks.get(count*2);
					y = blocks.get(count*2+1);
					int
							dx = blocks.get(count*2-2)-x, dy = blocks.get(count*2-1)-y 
							,dx2 = x - blocks.get(count*2-2), dy2 = y - blocks.get(count*2-1)
							, bitmask = 0;
					bitmask += chargeBM(dx,dy);
					bitmask += chargeBM(dx2,dy2);
					int meta = 0;
					switch (bitmask){
					case 3:meta = 3;break;
					case 9:meta = 4;break;
					case 12:meta = 5;break;
					case 6:meta = 6;break;
					case 5:meta = 1;break;
					case 10:meta = 2;break;
					default:meta = 0;break;
					
					}
					//Gdx.app.log(TAG, "beam "+bitmask+" meta"+meta + " @ "+x+","+y);
					Block b = getBlock(x,y);
					if (b.blockType() == 0 || b.blockID == 44 || b.blockID == 45){
						//damageBlock(type, x, y, strength);
						changeBlock(x,y,44,meta, true);
						//break;
					}else if (b.isAutomata()){
						damageBlock(type, x, y, strength);
						break;
						//strength -= maxTrans;
						
					}
					count++;
				}
				
				break;
			}
			
		case FROST:
			IntArray blocks = getStraightLine(x0, y0, x1, y1);
			int x = blocks.get(count*2), y = blocks.get(count*2+1);
			
			while (strength > 0 && count < length){
				//add by maxTrans, dump into air/automata
				int maxTrans = 4;
				//BlockLoc loc = blocks.removeFirst();
				Block b = getBlock(x,y);
				if (b.blockType() == 0){
					damageBlock(BlockDamageType.FROST, x, y, strength);
					break;
				}else if (b.isAutomata()){
					damageBlock(BlockDamageType.FROST, x, y, maxTrans);
					strength -= maxTrans;
				}
				count++;
			}
			
			break;
		}
		
	}
	
	private int chargeBM(int dx, int dy) {
		int bm = 0;
		if (dx == dy || dx == -dy) return 0;
		if (dx == -1) bm += 8;
		else if (bm == 1) bm += 2;
		if (dy == -1) bm += 4;
		else if (dy == 1) bm += 1; 
		return bm;
	}

	private IntArray lineBlocks = new IntArray();
	public IntArray getStraightLine(int x0, int y0, int x1, int y1){
		int dx = Math.abs(x1-x0), sx = x0<x1 ? 1 : -1;
		int dy = Math.abs(y1-y0), sy = y0<y1 ? 1 : -1; 
		int err = (dx>dy ? dx : -dy)/2, e2;
		//if (x0 == x1 && y0 == y1) lin;// tmpV.set(-1,-1);
		lastDigTargetBlock.set(-1,-1000000);
		lineBlocks.clear();
		int count = 0;
		for(int reps = 0;reps != -1; reps++){
			
			if (x0==x1 && y0==y1){
				lineBlocks.add(x0);
				lineBlocks.add(y0);
				break;
			}
			//if (count > range) break;
			e2 = err;
			if (e2 >-dx) { //extend
				err -= dy; 
				x0 += sx; 
				count++;
				lineBlocks.add(x0);
				lineBlocks.add(y0);
			}
			if (e2 < dy) { //extend
				err += dx; 
				y0 += sy; 
				count++;
				lineBlocks.add(x0);
				lineBlocks.add(y0);
				
			}
		
		}	
		
		return lineBlocks;
	}
	BlockLoc tmpB = new BlockLoc();
	public IntArray getLightningLine(int x0, int y0, int x1, int y1){
		lineBlocks.clear();
		lineBlocks.add(x0);
		lineBlocks.add(y0);
		int direction = 0;//nesw
		for(int reps = 0;reps != -1; reps++){
			int dx = Math.abs(x1-x0), sx = x0<x1 ? 1 : -1;
			int dy = Math.abs(y1-y0), sy = y0<y1 ? 1 : -1; 
			if (x0==x1 && y0==y1){
				
				break;
			}
			
			//they can't both be going away
			//int x = MathUtils.random(-1,1), y = MathUtils.random(-1,1);
			
			switch (MathUtils.random(3)){
			
			case 0:tmpB.set(-1, 0);break;
			case 1:tmpB.set(+1, 0);break;
			case 2:tmpB.set(0, -1);break;
			case 3:tmpB.set(0, +1);break;
			//case 4:tmpB.set(loc.x, loc.y-1);break;
			
			}
			if (tmpB.x == -sx || tmpB.y == -sy)continue;
			
			x0 += tmpB.x;
			y0 += tmpB.y;
			lineBlocks.add(x0);
			lineBlocks.add(y0);
			
		}	
		
		return lineBlocks;
	}
	
	protected Vector2 findDigTargetbresold(PunkMap map, int x0, int y0, int x1, int y1, boolean blockFlag, int range){
	//int x0 = player.x;
	//int y0 = player.y+1;
	//tmpV.set(direction).mul(player.DIGRANGE);//.mul(-1);
	//tmpV.add(player.position);
	//int x1 = (int)(target.x);;
	//int y1 = (int)(target.y);
	//Gdx.app.log("punk", "dig target. "+x1+", "+y1);
	int dx = Math.abs(x1-x0), sx = x0<x1 ? 1 : -1;
	int dy = Math.abs(y1-y0), sy = y0<y1 ? 1 : -1; 
	int err = (dx>dy ? dx : -dy)/2, e2;
	
	//Gdx.app.log("punk", "touch angle:"+touchLoc.angle());
	if (x0 == x1 && y0 == y1) return tmpV.set(-1,-1);
	lastDigTargetBlock.set(-1,-1);
	checkBlock(x0,y0, blockFlag);
	int count = 0;
	for(int reps = 0;reps != -1; reps++){
		/*if (checkBlock(x0,y0, blockFlag)){
			return (blockFlag?tmpV.set(lastDigTargetBlock.x, lastDigTargetBlock.y):tmpV.set(x0,y0));}*/
		//if (x0==x1 && y0==y1) break;
		if (count > range) break;
		e2 = err;
		if (e2 >-dx) { //extend
			err -= dy; 
			x0 += sx; 
			count++;
			if (checkBlock(x0,y0, blockFlag)){
				//Gdx.app.log("punk", "line, add sx");
				if (reps > 2) return (blockFlag?tmpBV.set(lastDigTargetBlock.x, lastDigTargetBlock.y):tmpBV.set(x0,y0+sy));

				//return (tmpBV.set(lastDigTargetBlock.x, lastDigTargetBlock.y));
				}
		}
		if (e2 < dy) { //extend
			err += dx; 
			y0 += sy; 
			count++;
			if (checkBlock(x0,y0, blockFlag)){
				if (reps > 2) return (blockFlag?tmpBV.set(lastDigTargetBlock.x, lastDigTargetBlock.y):tmpBV.set(x0+sx,y0));
				//return (tmpBV.set(lastDigTargetBlock.x, lastDigTargetBlock.y));
	
			}
		}
	}
		
	
	
	return tmpBV.set(-1,-1);
}


	/*			plagues
	 * The Mithrans - vanished mysteriously long ago. maybe zombies. Curable? Intelligenet? Evolution? overpopulation=evoloution
	 * 
	 * widespread / quarantined / night only
	 * 
	 * items for cure/control, that's it. maybe dragons have them, maybe wizards, maybe lost. 
	 * 
	 * 
	 * 
	 * Map Corruption(destruction)
	 * Winter (order)
	 * weird day/night cycle - (chaos)
	 * Nature creep/overpopulation (regeneration)
	 * 
	 * 
	 * 
	 * Night (moon)
	 * day (sun)
	 * 
	 * one god - just need to get the other god to balance things out
	 * one god missing - same deal, resurrect missing one to restore balance(or become a god yourself)
	 * all gods - void is plotting to overthrow them via necromancers
	 * no gods - graviticus and void in balance. rescue other deities.
	 * 
	 * 
	 * 
	 * 
	 * dragons - peaceful, in sky
	 * 		warlike, control some towns
	 * 	`	keep to themselves. civil war
	 * 		vanished long ago
	 * 
	 * war - elves, dwarves?, humans, ogres, darklings, wizards, churches
	 * 
	 * power grid. refugess. the resistance. mirror of the god situation.
	 * 
	 * 
	 * terrain types 
	 * half/half separated by ocean
	 * all winter (order)
	 * all summer(chaos)+overpop
	 * 
	 * 
	 * underground stuff
	 * cities
	 * doors
	 * lava / water pools
	 * traps!
	 * 
	 * 
	 * 		end of tunnnels:
	 * hidden cities
	 * abandoned cities
	 * infested - zombies / darklings
	 */

	
}

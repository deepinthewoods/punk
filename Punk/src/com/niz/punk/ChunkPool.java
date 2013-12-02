package com.niz.punk;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntMap.Entry;
import com.badlogic.gdx.utils.IntMap.Values;
import com.badlogic.gdx.utils.Pool;

public class ChunkPool extends Pool<Chunk> {
	private static int CB = Punk.CHUNKBITS;
	private String TAG = "chunk Pool";
	private IntMap<Chunk> chunks = new IntMap<Chunk>(2000,.6f);
	private Array<Chunk> saveChunks = new Array<Chunk>();
	public ConcurrentLinkedQueue<Chunk> unFetchedChunks = new ConcurrentLinkedQueue<Chunk>(); 
	public 	ConcurrentLinkedQueue<Signal>	updateStream = new ConcurrentLinkedQueue<Signal>();
	//public ConcurrentLinkedQueue<Chunk> unFetchedChunks = new ConcurrentLinkedQueue<Chunk>();
	PunkBlockList timed = new PunkBlockList();
	public static final int MAXBEFORESAVING = 180;
	static final Object SUFF_CHEST = ".chest";
	private StringBuilder saveLoc = new StringBuilder(), infoLoc = new StringBuilder(), actorsLoc = new StringBuilder();//tmp
	private FileHandle saveHandle, monHandle, infoHandle, actorsHandle;
	private Player player;
	private PunkMap map;
	public BufferedOutputStream sos;
	private final int subIntervals = 16;
	private int saveProgressFine, saveProgressSub;
	private Array<Object> garbage = new Array<Object>();
	//private StringBuilder
	public int loadDistance = 6;
	public int maxChunks = 28;
	public int updatesTot, lightTot, fetchTot;
	private final String SUFF_ACTORS = ".act", SUFF_INFO = ".inf", SUFF_MAP = ".dat";
	@Override
	protected Chunk newObject() {
		
		if (Start.mi.getAvailMegs() < 16)Punk.saveAllQueued = true;
		return new Chunk(0);
	}
	
	public ChunkPool(Player player, PunkMap map){
		super(50);
		initBlockDefs();
		this.player = player;
		this.map = map;
		saveChunks.ordered = true;
		for (int i = 0; i < 2*MAXBEFORESAVING/3; i++)
			free(obtain());
	}
	public Chunk getChunk(int id, int heightID){
		Chunk c = chunks.get(getKey(id, heightID), null);
		return c;
	}
	
	public Chunk getChunkWorld(int x, int y) {
		
		return getChunk(x>>Punk.CHUNKBITS, y>>Punk.CHUNKBITS);
	}
	public Chunk getChunkWorldRender(int x, int y) {
		Chunk c = getChunk(x>>Punk.CHUNKBITS, y>>Punk.CHUNKBITS);;
		if (c == null) return PunkMap.nullChunk;
		return c;
	}

	public boolean updateFetchQueue(Player player){
		//iterate through, fetch closest
		//true if found none
		int length = 0;
		boolean done = false;
		if (tryFetch(PunkMap.currentChunk, PunkMap.currentChunkHeightID, 
				PunkMap.currentPlane, player)) return false;
		while (!done && length < PunkMap.minChunkRange){
			for (int i = 0; i < length; i++){
				int j = length - i;
				//	Gdx.app.log(TAG, "length:"+length+"  i"+i+" j"+j);
					if (tryFetch(PunkMap.currentChunk+i, PunkMap.currentChunkHeightID+j, 
							PunkMap.currentPlane, player)) return false;
					if (tryFetch(PunkMap.currentChunk-j, PunkMap.currentChunkHeightID+i,
							PunkMap.currentPlane, player)) return false;
					if (tryFetch(PunkMap.currentChunk-i, PunkMap.currentChunkHeightID-j, 
							PunkMap.currentPlane, player)) return false;
					if (tryFetch(PunkMap.currentChunk+j, PunkMap.currentChunkHeightID-i, 
							PunkMap.currentPlane, player)) return false;
				}
				
					
				
			
				
			length++;
		}
		//extra at sides
		if (tryFetch(PunkMap.currentChunk+length, PunkMap.currentChunkHeightID, 
				PunkMap.currentPlane, player)) return false;
		if (tryFetch(PunkMap.currentChunk-length, PunkMap.currentChunkHeightID,
				PunkMap.currentPlane, player)) return false;
		return true;
	}
	private Chunk getChunk(BlockLoc loc) {
		return getChunkWorld(loc.x, loc.y);
	}
	//public Chunk lastC;
	public Block getBlock(int x, int y){
		//Gdx.app.log(TAG, "size "+chunks.size);
		
		Chunk lastC = getChunkWorld(x, y);
		if (lastC == null){
			//Gdx.app.log(TAG, "returning generic block");
			return Punk.genericAirBlock;
		}
		//if (c.isPostFetched)return c.block[x&Punk.CHUNKSIZEMASK][y&Punk.CHUNKSIZEMASK]; 
		synchronized (lastC){
		return lastC.
				showingBlock[((x&Punk.CHUNKSIZEMASK)<<CB)+(y&Punk.CHUNKSIZEMASK)];
		}
	}
	
	public Block getBlockActual(int x, int y){
		//Gdx.app.log(TAG, "size "+chunks.size);
		
		Chunk c = getChunkWorld(x, y);
		if (c == null){
			Gdx.app.log(TAG, "!!!!!!!!!!!!!!");
			return Punk.genericAirBlock;
			
		}
		return c.
				block[((x&Punk.CHUNKSIZEMASK)<<CB)+(y&Punk.CHUNKSIZEMASK)];
	}
	
	public void setModified(int x, int y){
		Chunk c = getChunkWorld(x,y);
		c.modified = true;
		//c.pixProgress = 0;
	}
	public void setModified(BlockLoc loc){
		setModified(loc.x, loc.y);
	}
	public Values<Chunk> entries() {
		// TODO Auto-generated method stub
		//chunks.
		return chunks.values();
	}

	public void createChunk() {
		//chunks.put(-10000000, obtain());
		free(obtain());
	}

	public void saveChunk(Chunk c, boolean remove){
		
	
		saveChunks.add(c);
		while (c.modified)updateSave(); 
		//c.saving = true;
		//c.freeAfterSave = remove;
	}
	
	
	public void saveAll(){
		Gdx.app.log(TAG, "save all");
		allSaved = false;
		while (!allSaved)updateSave();
		
	}
	
	
	private int saveCount = 0, writeProgress;
	public boolean allSaved;
	static com.niz.punk.ChunkEncoder enc = new ChunkEncoder();

	public static ChunkBGEncoder encBG = new ChunkBGEncoder();

	public boolean updateSave(){//returns true if it's done anything
		//m,ake sure fetched completely
		
		//returns true if it's done something
		
			if (saveChunks.size == 0) {
				allSaved = false;
				Chunk c = null;// = saveChunks.get(0);//e.value;
				Chunk farC = null;
				int farDist = -1;
				Iterator<Entry<Chunk> > it = chunks.entries().iterator();
				boolean done = false;
				while (it.hasNext()){
					Entry<Chunk> e = it.next();
					c = e.value;
					if ((c.distanceFromPlayer() > farDist) && c.modified){
						farDist = c.distanceFromPlayer();
						farC = c;
						done = true;
					}
				}
				
				if ( done){
					saveChunks.add(farC);
					//Gdx.app.log(TAG, "recalc save chunk"+farDist + "  "+farChunk.chunkID + ", "+farChunk.heightID);
					// (all) 
						//return true;
					return true;
				} 
				//Gdx.app.log(TAG, "no save chunks");
				allSaved = true;
				return false;
				
				
			} //else Gdx.app.log(TAG, "update save");
			//Entry<Chunk> e = iter.next();
			
			Chunk c = saveChunks.get(0);
			if (c == null){
				//Gdx.app.log(TAG, "null c");
				return false;
			}
		
			int cID;
			
			switch (saveProgressFine){
	
			case 0:
				
				cID = c.chunkID;
				if (saveProgressSub == 0){
					enc.clear();
					encBG.clear();
				}
				//Gdx.app.log("map", "saveing Chunk:"+cID+","+chunk.heightID);
				
					for (int x = saveProgressSub*(Punk.CHUNKSIZE/subIntervals); x < (saveProgressSub+1)*(Punk.CHUNKSIZE/subIntervals); x++)
						for (int i = 0; i < Punk.CHUNKSIZE; i++){
							enc.add(c.block[((x)<<CB)+(i)]);
							encBG.add(c.blockBG[((x)<<CB)+(i)]);
						}
		
			    saveProgressSub++;
			    if (saveProgressSub < subIntervals)saveProgressFine--;
			    else {
			    	saveProgressSub = 0;
			    	writeProgress = 0;
			    }
			    break;
			case 1:
				cID = c.chunkID;
			
				if (saveProgressSub == 0){
					
					
					getChunkLoc(saveLoc,
					cID,
					c.heightID,
					c.plane);
					saveLoc.append(SUFF_MAP);
					saveHandle = Gdx.files.external(saveLoc.toString());
					garbage.add(saveHandle);
					
						sos = new BufferedOutputStream(saveHandle.write(false));
						garbage.add(sos);
						//sos.setFinishBlockOnFlush(true);
						enc.finish();
						encBG.finish();
					
				}
				
			    try{
			    	
			    	int count = 0;
			    	if (enc.hasNext())
			    		while (enc.hasNext() && count < 100){
			    			sos.write(enc.next());
			    			count++;
			    		
			    		}
			    	else if (encBG.hasNext){
			    		count = 0;
			    		while (encBG.hasNext && count < 100){
					    	sos.write(encBG.next());
					    	count++;
					    }
			    		
			    	}
			    	
			    	

				} catch (IOException ex)
				{
					Gdx.app.log("map, save stage 1", "Error saving chunk!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
					
				}
			    
			    saveProgressSub++;
			    if (enc.hasNext() || encBG.hasNext)saveProgressFine--;
			    else{
			    	saveProgressSub = 0;
			    	try {
			    		c.writeDoors(sos, map);
			    		sos.close();
			    	} catch (IOException ex){
			    		
			    	}
			    }
			    
			    break;
			
			case 2:
				/*getChunkLoc(infoLoc,
				c.chunkID,
				c.heightID,
				c.plane);*/
				//infoLoc.append(SUFF_INFO);
				//infoHandle = Gdx.files.external(infoLoc.toString());
				//garbage.add(infoHandle);
				//c.info.writeToFile(infoHandle);
				saveProgressFine++;
			    break;
			case 3:
			case 5:
				
			case 6:
				
				saveProgressFine = 6;
				
				break;
			default:	
				
			}
			
			saveProgressFine++;
			if (saveProgressFine >= 4){
				saveProgressFine = 0;
				saveChunks.removeIndex(0);
				//Gdx.app.log(TAG, "done saving "  +  " "+c.chunkID + ", "+c.heightID);
				c.modified = false;
				saveCount++;
				if (saveCount % 512 == 0){
					gc();
					Chunk.gc();
					System.gc();
				}
				
				if (c.distanceFromPlayer() > PunkMap.maxChunkRange) {
					
				}
				return true;
			}
			
			
		
			
		
		//if (!allSaved) return true;
			
		
		return true;
		
		
		
	
	}
	public void fetchWorld(int x, int y, int plane, Player player) {
		fetch (x>>Punk.CHUNKBITS, y>>Punk.CHUNKBITS, plane, player);
		
	}
	
	private static String sCreativeFolder = "creative/";
	public static String  activeCreativeFolder = "standard";
	public static void getChunkLoc(StringBuilder loc, int chunkID, int chunkHeightID, int plane) {
		Punk.getSaveLoc(loc);
		if (plane == 2){
			loc.append(sCreativeFolder);
			loc.append(activeCreativeFolder);
		}
		loc.append('p');
		//loc.append(Chunk.planes.get(plane).name);
		loc.append(plane);
		
		loc.append('/');
		loc.append(chunkID);
		loc.append('h');
		loc.append(chunkHeightID);
		
	}
	
	private boolean tryFetch(int chunkID, int chunkHeightID, int plane, Player player){
		if (chunks.containsKey(getKey(chunkID, chunkHeightID)) || unFetchedContains(chunkID, chunkHeightID)){//if it's already loaded/being loaded
			//Gdx.app.log(TAG, "chunk skip"+chunkID+","+chunkHeightID + "hash "+(chunkID+(chunkHeightID<<8)));
			//chunks.get(chunkID+(chunkHeightID<<8)).freeAfterSave = false;;
				return false;
		}
		fetch (chunkID, chunkHeightID, plane, player);
		return true;
	}

	public synchronized Chunk fetch(int chunkID, int chunkHeightID, int plane, Player player){
		//Gdx.app.log(TAG, "fetch chunk"+chunks.size);
		Chunk chunk;
		if (chunks.containsKey(getKey(chunkID, chunkHeightID)) || unFetchedContains(chunkID, chunkHeightID)){//if it's already loaded/being loaded
			//Gdx.app.log(TAG, "chunk skip"+chunkID+","+chunkHeightID + "hash "+(chunkID+(chunkHeightID<<8)));
			//chunks.get(chunkID+(chunkHeightID<<8)).freeAfterSave = false;;
				return null;
		}
		allSaved = false;
		
		
		if (chunks.size >= maxChunks && !super.has()){
			//Gdx.app.log(TAG, "furthest");
			chunk = getFurthestChunk();
			
		} else{
			//if (!has() )saveAll();
			chunk = obtain();
			
		}
		unFetchedChunks.add(chunk);
		
		//infoLoc = saveDir+PunkMap.current_gameType+"/" +"game"+ PunkMap.gameID +  "/p"+plane+"c"+ chunkID+"h"+chunkHeightID + ".ino";
		//getChunkLoc(infoLoc, chunkID, chunkHeightID, plane);
		//infoLoc.append(SUFF_INFO);
		//garbage.add(infoLoc);
		
	

	//	if (tmpChunk.block[0][0] == null) Gdx.app.log("punkMap", "fetch chunk: chunk null!");
		//saveLoc = saveLoc + "chunk" + chunkID + ".map";
	    //System.out.println("fetchChunk");
		
		
		//infoHandle = Gdx.files.external(infoLoc.toString());
		//garbage.add(infoHandle);
		
		getChunkLoc(saveLoc, chunkID, chunkHeightID, plane);
		saveLoc.append(SUFF_MAP);
		//saveLoc = saveDir+PunkMap.current_gameType+"/" +"game"+ PunkMap.gameID + "/p"+plane+"c"+ chunkID+"h"+chunkHeightID + ".map";
		saveHandle = Gdx.files.external(saveLoc.toString());
		//unScrubChunk(tmpChunk, chunkID);
		garbage.add(saveHandle);
		//CHUNK INFO
		//chunk.info.readFromFile(infoHandle);
		
		
		
		chunk.startFetching(saveLoc.toString(), chunkID, chunkHeightID, plane);
		//chunk.distanceFromPlayer();

		//Gdx.app.log("map", "RESETTING FLAGS");
		//PunkMap.allChunksLoaded = false;
		//PunkMap.allTreesGrown = false;
		//PunkMap.allChunksPostFetched = false;
		//return tmpChunk;
		return chunk;
	}
	
	
	

	private boolean unFetchedContains(int chunkID, int chunkHeightID) {
		Iterator<Chunk> iter = unFetchedChunks.iterator();
		boolean has = false;
		while (iter.hasNext() && !has){
			Chunk c = iter.next();
			if (c.chunkID == chunkID && c.heightID == chunkHeightID) has = true;
		}
			
		return has;
	}

	private Chunk getFurthestChunk() {
		
		Entry<Chunk> e = null;// = saveChunks.get(0);//e.value;
		Chunk farC = null;
		
		//Gdx.app.log(TAG, "get ferthest chunk");
		int farDist = -1;
		int farKey = 0;
		Iterator<Entry<Chunk> > it = chunks.entries().iterator();
		boolean done = false;
		while (it.hasNext()){
			e = it.next();
			Chunk c = e.value;
			//Gdx.app.log(TAG, "dist:"+c.distanceFromPlayer()+"  p "+PunkMap.currentChunk + ", "+PunkMap.currentChunkHeightID);
			if (c.distanceFromPlayer() > farDist){
				farDist = c.distanceFromPlayer();
				farC = c;
				farKey = e.key;
				done = true;
				//Gdx.app.log(TAG, "found "+farDist+"  p "+PunkMap.currentChunk + ", "+PunkMap.currentChunkHeightID + " c "+c.chunkID+","+c.heightID);
			}
		}
		if (!done){
			//Gdx.app.log(TAG, "furthest chunk failed");
			return obtain();
		}
		//Chunk c = farEntry.value;
		if (farC.modified){
			saveChunks.add(farC);
			saveAll();
		}
		
		chunks.remove(farKey);
		
		return farC;
		
	}
	
	 
	
	
	public void doPostFetch(Chunk chunk, PunkMap map){
		//MONUMENT INFO
		//Gdx.app.log("map", "postfetch"+chunk.chunkID+":"+chunk.heightID);

		
		//String actorsLoc = saveDir+PunkMap.current_gameType+"/" +"game"+ PunkMap.gameID + 
		//		"/chunk"+ chunk.chunkID+"h"+chunk.heightID + ".act";
		//garbage.add(actorsLoc);
		getChunkLoc(actorsLoc, chunk.chunkID, chunk.heightID, chunk.plane);
		actorsLoc.append(SUFF_ACTORS);
		
		actorsHandle = Gdx.files.external(actorsLoc.toString());
		garbage.add(actorsHandle);
		
		//ACTORS
		if (actorsHandle.exists()){
			//Gdx.app.log(TAG, "loading from disk:"+chunkID+"file length:"+monHandle.length()+"path:" + monHandle.path());
		//	Gdx.app.log("map", "unscrub actors");

		try{
			BufferedInputStream is = new BufferedInputStream(actorsHandle.read());
			garbage.add(is);
			map.unScrubActors(is);
			is.close();
		} catch (IOException ex)
		{
			ex.printStackTrace();
			System.out.println("Error loading actors");
		} 
		}else {//generate new(default)info
			//tmpChunk.monument = new MonumentInfo();
		}
		chunk.doPostFetch(map);
		//Gdx.app.log("map", "dopostfetch done in chunk:"+chunk.isPostFetched);
		//chunk.makeDeposits(this);
		//chunk.makeCaveFluids();
		//chunk.makeSpawners();
		///if (chunk.chunkID == chunkC.chunkID && chunk.heightID == chunkC.heightID)
		//unScrubChunk(chunk);
		//unScrubActors(chunk);
		//check for all loaded
		/*if (chunkC.isPostFetched && chunkR.isPostFetched && chunkR.isPostFetched && 
				chunkT.isPostFetched && chunkTR.isPostFetched && chunkTL.isPostFetched &&
				chunkB.isPostFetched && chunkBR.isPostFetched && chunkBL.isPostFetched )
		{
			allChunksLoaded = true;
		}*/
		//map.unScrubOres(chunk);		
	}
	
	
	
	public void scrubChunk2(Chunk chunk, int prog, int subIntervals){
		for (int x = prog*(Punk.CHUNKSIZE/subIntervals); x < (prog+1)*(Punk.CHUNKSIZE/subIntervals); x++)
			for (int i = 0; i < Punk.CHUNKSIZE; i++){
				//Gdx.app.log("map", "scrub "+x+"  "+i);
				Block oneBlock = chunk.block[((x)<<CB)+(i)];
				//if (oneBlock.blockType() == 1 && oneBlock.meta != 0)
				//	oneBlock.set(0,0);
				//if (oneBlock.blockID == 7)
				//	oneBlock.set(0,0);
				//else if (oneBlock.blockID == 51)
				//	oneBlock.set(0,0);
			}
	}
	public void saveChest(PunkInventory chestInfo){
		//add all items to the gameInfo inventory and save
	
		//String saveLoc = saveDir+PunkMap.current_gameType+"/" +"game"+ PunkMap.gameID + "/chunk"+ PunkMap.currentChunk+"h"+PunkMap.currentChunkHeightID + "chest"+chestInfo.chestID+".ino";
		//garbage.add(saveLoc);
		getChunkLoc(saveLoc, loadDistance, loadDistance, loadDistance);
		saveLoc.append(chestInfo.chestID);
		saveLoc.append(SUFF_CHEST);
		//TODO chestInfo.writeToFile(Gdx.files.external(saveLoc.toString()));
	}

	public int getSize() {
		return chunks.size;
	}
	public boolean hasLeft(BlockLoc loc) {
		Chunk c = getChunk(loc);
		if (c == null) return false;
		Chunk ch = getChunk(c.chunkID-1, c.heightID);
		return (ch != null && ch.isPostFetched);
	}
	public boolean hasRight(BlockLoc loc) {
		Chunk c = getChunk(loc);
		if (c == null) return false;
		Chunk ch = getChunk(c.chunkID+1, c.heightID);
		return (ch != null && ch.isPostFetched);
	}
	public boolean hasDown(BlockLoc loc) {
		Chunk c = getChunk(loc);
		if (c == null) return false;
		Chunk ch = getChunk(c.chunkID, c.heightID-1);
		return (ch != null && ch.isPostFetched);
	}
	public boolean hasUp(BlockLoc loc) {
		Chunk ch = getChunkWorld(loc.x, loc.y);
		if (ch == null) return false;
		Chunk c = getChunk(ch.chunkID, ch.heightID+1);
		return (c != null && c.isPostFetched);
	}
	public boolean hasLeft(Chunk ch) {
		Chunk c = getChunk(ch.chunkID-1, ch.heightID);
		return (c != null && c.isPostFetched);
	}
	public boolean hasRight(Chunk ch) {
		Chunk c = getChunk(ch.chunkID+1, ch.heightID);
		return (c != null && c.isPostFetched);
	}
	public boolean hasChunk(BlockLoc loc) {
		Chunk c = getChunkWorld(loc.x, loc.y);
	
		return (c != null && c.isPostFetched);
	}

	public boolean hasDown(Chunk ch) {
		Chunk c = getChunk(ch.chunkID, ch.heightID-1);
		return (c != null && c.isPostFetched);
	}
	public boolean hasUp(Chunk ch) {
		Chunk c = getChunk(ch.chunkID, ch.heightID+1);
		return (c != null && c.isPostFetched);
	}
	public boolean hasChunk(Chunk ch) {
		Chunk c = getChunk(ch.chunkID, ch.heightID);
		return (c.unScrubFinished);
	}
	public boolean hasChunk(int x, int y) {
		Chunk c = getChunkWorld(x, y);
		return (c != null && c.isPostFetched);
	}
	public boolean allSaved() {
		
		return (saveChunks.size == 0);
		
	}
	private String garbStr = "gc";
	public void gc (){
		garbage.clear();
		
		Gdx.app.log(TAG, garbStr);
	}

	public void fetchIfMissing(int chunkID, int chunkHeightID, int plane, Player player) {
		getChunkLoc(saveLoc, chunkID, chunkHeightID, plane);
		saveLoc.append(SUFF_MAP);
		//saveLoc = saveDir+PunkMap.current_gameType+"/" +"game"+ PunkMap.gameID + "/p"+plane+"c"+ chunkID+"h"+chunkHeightID + ".map";
		saveHandle = Gdx.files.external(saveLoc.toString());
		if (!saveHandle.exists())         fetch(chunkID, chunkHeightID, plane, player);
	}

	public void freeAll() {
		//frees all chunks
	
		Entry<Chunk> e = null;// = saveChunks.get(0);//e.value;
		Iterator<Entry<Chunk> > it = chunks.entries().iterator();
	
		while (it.hasNext()){
			e = it.next();
			Chunk c = e.value;
			//Gdx.app.log(TAG, "key:"+e.key);
			chunks.remove(e.key);
			free(c);
		}
		
		
		
	}

	public void removeOtherPlanes() {
		Entry<Chunk> e = null;// = saveChunks.get(0);//e.value;
		Iterator<Entry<Chunk> > it = chunks.entries().iterator();
	
		while (it.hasNext()){
			e = it.next();
			Chunk c = e.value;
			//Gdx.app.log(TAG, "key:"+e.key);
			if (c.plane != PunkMap.currentPlane){
				chunks.remove(e.key);
				while (c.doors.size > 0){
					Door d = c.doors.pop();
					Chunk.allDoors.removeValue(d, true);
					Chunk.doorPool.free(d);
				}
				free(c);
			}
			
		}
		
		
	}
	BlockBG genericBGBlock = new BlockBG();
	public byte getBackgroundBlock(int x, int y) {
		
		//Gdx.app.log(TAG, "size "+chunks.size);
			Chunk lastC = getChunkWorld(x, y);
			//genericBGBlock.set(0,0);
		if (lastC == null){
			//Gdx.app.log(TAG, "generic bg");
			return 0;
			
		}
			//if (c.isPostFetched)return c.block[x&Punk.CHUNKSIZEMASK][y&Punk.CHUNKSIZEMASK]; 
		return lastC.blockBG
					[((x&Punk.CHUNKSIZEMASK)<<CB)+(y&Punk.CHUNKSIZEMASK)];
	}

	public void completedFetch() {//moves 1 unfetched chunk to chunks
		Gdx.app.log(TAG, "fetch complete");
		Chunk c = unFetchedChunks.poll();
		chunks.put(getKey(c.chunkID, c.heightID), c);
		
	}

	public boolean doFetch() {
		//Gdx.app.log(TAG,  "fetch");
		if (unFetchedChunks.isEmpty()) return false;
		Chunk c = unFetchedChunks.peek();
		if (c.fetch(map)){//move to chunks
			unFetchedChunks.poll();
			chunks.put(getKey(c.chunkID, c.heightID), c);
			map.checkInsideLight(c);
			//Gdx.app.log(TAG, "finished chunk fetch");
			
		}
		fetchTot++;
		//Gdx.app.log(TAG, "chunk fetch");
		return true;
		
	}
		
	private int UPDATECOUNT = 10;
	public boolean doModifications() {
		//Gdx.app.log(TAG,  "mods");
		if (updateStream.isEmpty())return false;
		//Gdx.app.log(TAG, "updates:"+updateStream.size());
		int count = 0;
		while (count < UPDATECOUNT && !updateStream.isEmpty()){
			Signal sig = updateStream.poll();
			if (sig instanceof PlacePacket){
				PlacePacket p = (PlacePacket) sig;
				map.changeBlock(p.x, p.y, p.id, p.meta, true);
				PlacePacket.pool.free(p);
				
			}else if (sig instanceof BlockLocDamage){
				BlockLocDamage loc = (BlockLocDamage) sig;
				map.destroyBlock(loc.type, loc.x, loc.y, loc.p, 0);
			}else if (sig instanceof BlockLoc){//updates
				BlockLoc loc = (BlockLoc) sig;
				
				Block b = getBlock(loc.x, loc.y);
				
				if (b == null || b.blockID == -1){
					Gdx.app.log(TAG, "update"+b+loc);
					//return true;
				}
				BlockDef bd = blockDefs[b.blockID];
				bd.update(loc, b, map, true);
				//map.addlightUpdate(loc);
				PunkBlockList.pool.free(loc);
			}
			count++;
		}
		updatesTot++;
		return true;
	}
	public ConcurrentLinkedQueue<Chunk> lightChunks = new ConcurrentLinkedQueue<Chunk>();
	public boolean doLight() {
		//Gdx.app.log(TAG,  "light");
		//lock chunks
		if (lightChunks.isEmpty()){
			synchronized (chunks){
				Iterator<Chunk> iter = chunks.values().iterator();
				while (iter.hasNext()){
					Chunk c = iter.next();
					if (c == null){
						iter.remove();
					}else {
						map.checkInsideLight(c);
						if (!c.lightUpdates.list.isEmpty())
							lightChunks.add(c);
						
					
					}
				}
			}
			if (lightChunks.isEmpty()){
				//Gdx.app.log(TAG,  "no light");
				return false;
				
			}
			return false;
		}
		//actual updates
		
		Chunk c = lightChunks.peek();
		if (c.updateLight(map)) lightChunks.poll();
		lightTot++;
		
		return true;
	}

	public static int getKey(int chunkID, int chunkHeightID){
		return chunkID+(chunkHeightID<<16);
		
	}

	public void addToUpdateList(int x, int y) {
		
		updateStream.add(PunkBlockList.obtain(x,y));
	}

	public void addToTimedUpdateList(int x, int y) {
		timed.addBlock(x,y);
	
		
	}

	public void addLightUpdate(int x, int y) {
		
		Chunk c = getChunkWorld(x,y);
		if (c == null) {
			//Gdx.app.log(TAG, "null chunk");
			return;
		}
		//Gdx.app.log(TAG, "add light "+x+","+y);
		c.lightUpdates.addBlock(x, y);
		//throw new GdxRuntimeException("add");
	}

	

	public void addPlaceQueue(int x, int y, int iID, int metaData,
			int plane) {
		//look in chunks first and add to updatestream
		if (plane != PunkMap.currentPlane){
			
			return;
		}
		if (hasChunk(x,y)){
			updateStream.add(PlacePacket.pool.obtain().set(x, y, iID, metaData, plane));
			return;
		}
		//look in unfetchedchunks, fetch if absent
		Iterator<Chunk> iter = unFetchedChunks.iterator();
		int chunkID = x >> Punk.CHUNKBITS, chunkHeightID = y >> Punk.CHUNKBITS;
		boolean has = false;
		Chunk c= null;
		while (iter.hasNext() && !has){
			c = iter.next();
			if (c.chunkID == chunkID && c.heightID == chunkHeightID){
				has = true;
			}
		}
		if (has)c.addPlaceQueue(x&Punk.CHUNKSIZEMASK, y&Punk.CHUNKSIZEMASK, iID, metaData, plane);
		else {
			this.fetch(chunkID, chunkHeightID, plane, player).
			addPlaceQueue(x&Punk.CHUNKSIZEMASK, y&Punk.CHUNKSIZEMASK, iID, metaData, plane);
			//Gdx.app.log(TAG, "error place q");
		}
		
	}

	public BlockDef getInitBlockDef(int id){
		switch (id){
		
		case 0: return new com.niz.punk.blocks.Air();
		default:
		case 1: return new com.niz.punk.blocks.Stone();
		
		case 2: return new com.niz.punk.blocks.Dirt();
		case 3: return new com.niz.punk.blocks.Grass();
		case 4: return new com.niz.punk.blocks.Gravel();
		case 5: return new com.niz.punk.blocks.Sand();
		case 6: return new com.niz.punk.blocks.Snow();
		case 7: return new com.niz.punk.blocks.Stone();
		case 8: return new com.niz.punk.blocks.Chest();
		case 9: return new com.niz.punk.blocks.PineSapling();
		case 10: return new com.niz.punk.blocks.SkyBlock();
		case 11: return new com.niz.punk.blocks.Lava();
		case 12: return new com.niz.punk.blocks.Water();
	
		//case 19: return new com.niz.punk.blocks.Stone();
		//case 20: return new com.niz.punk.blocks.Stone();
		case 21: return new com.niz.punk.blocks.Sign();
		case 22: return new com.niz.punk.blocks.CobbleStone();
		case 23: return new com.niz.punk.blocks.Bricks();
		case 24: return new com.niz.punk.blocks.SandStone();
		case 25: return new com.niz.punk.blocks.CobbleStone();//castle tiles
		case 26: return new com.niz.punk.blocks.CobbleStone();//castle tiles
		case 27: return new com.niz.punk.blocks.Stone();
		case 28: 
		case 29:
		case 30: case 31:case 32:return new com.niz.punk.blocks.Ore();
		case 33:case 89:return new com.niz.punk.blocks.Mushroom();
		case 38:return new com.niz.punk.blocks.SkyWood();
		case 39:return new com.niz.punk.blocks.SkyLeaf();
		case 41:return new com.niz.punk.blocks.Bed();
		case 42: return new com.niz.punk.blocks.Fire();
		case 43: return new com.niz.punk.blocks.Frost();
		case 44:return new com.niz.punk.blocks.ChargeSource();
		case 45:return new com.niz.punk.blocks.Charge();
		case 50: return new com.niz.punk.blocks.Torch();
		case 14:return new com.niz.punk.blocks.PineLeaf();
		case 16:case 18:case 20:return new com.niz.punk.blocks.Leaf();
		case 13:case 15:case 17:case 19:return new com.niz.punk.blocks.Wood();
		case 51:return new com.niz.punk.blocks.PlayerHeldTorch();
		case 52:return new com.niz.punk.blocks.OreSource();
		case 53:return new com.niz.punk.blocks.TrollBlock();

		case 61:return new com.niz.punk.blocks.Bedrock();
		case 71:return new com.niz.punk.blocks.SkyGlowBlock();
//*/

		}
	}
	
	
	private void initBlockDefs() {
		for (int i = 0; i < 128; i++){
			blockDefs[i] = getInitBlockDef(i);
			sBlockDefs[i] = blockDefs[i];
		}
		defaultBlockDef = getInitBlockDef(0);
	}
	public BlockDef[] blockDefs = new BlockDef[128];
	public static BlockDef defaultBlockDef;
	public static BlockDef[] sBlockDefs = new BlockDef[128];
	
	public boolean isOnEdge(BlockLoc loc) {
		boolean valid = true;
		int ax = loc.x & Punk.CHUNKSIZEMASK, ay = loc.y & Punk.CHUNKSIZEMASK;
		if (!hasChunk(loc)) return true;
		if (ax == 0){//left
			if (!hasLeft(loc))
				valid = false;
		} else if (ax == Punk.CHUNKSIZE-1){//right
			if (!map.chunkPool.hasRight(loc))
				valid = false;
		}
		if (ay == 0){//bottom
			if (!hasDown(loc))
				valid = false;
		} else if (ay == Punk.CHUNKSIZE-1){//top
			if (!hasUp(loc))
				valid = false;
		}
		return !valid;
	}

	@Override
	public void clear(){
		//if (true)throw new GdxRuntimeException("herp");
		//assume everything's been saved
		Iterator<Entry<Chunk> > it = chunks.entries().iterator();
		boolean done = false;
		while (it.hasNext()){
			Entry<Chunk> e = it.next();
			Chunk c = e.value;
			free(chunks.remove(e.key));
		}
		while (!unFetchedChunks.isEmpty())
			free(unFetchedChunks.poll());
		
		
		
		
		timedBlocksLeft = 0;
		timedUpdatesQueued = 0;
	}

	public PathfindingNode getPathfindingNodeFromHash(int x, int y) {
		
		Chunk c = this.getChunkWorld(x, y);
		if (c == null) return null;
		//look through chunk and find one with same hash
		/*for (int i = 0; i < c.columnedNodes.length; i++){
			if (c.nodes.get(i).x == x && c.nodes.get(i).y == y){
				return c.nodes.get(i);
			}
		}*/
		for (int i = 0; i < c.columnedNodes[x&Punk.CHUNKSIZEMASK].size; i++)
			if (c.columnedNodes[x&Punk.CHUNKSIZEMASK].get(i).y == y){
				return c.columnedNodes[x&Punk.CHUNKSIZEMASK].get(i);
			}
		
		return null;
		
	}
	public int timedBlocksLeft = 0, timedUpdatesQueued = 0;
	public boolean updateTimed() {
		if (timedBlocksLeft <=0) {
			if (timedUpdatesQueued > 0){
				timedUpdatesQueued--;
				timedBlocksLeft = timed.list.size();
				//Gdx.app.log(TAG, "timed update reset"+timedUpdatesQueued);
				return false;
			}
			
			return true;
		}
		//Gdx.app.log(TAG, "timed update"+timedBlocksLeft);
		BlockLoc loc = timed.removeOrdered();
		timedBlocksLeft--;
		if (loc == null) return false;
		Block b = getBlock(loc.x, loc.y);
		if (b == null || b.blockID == -1) throw new GdxRuntimeException("whoa!"+loc);
		//synchronized (blockDefs){
			BlockDef def = this.blockDefs[b.blockID];
			def.timedUpdate(loc, b, map);
		//}
		
		
		PunkBlockList.pool.free(loc);
		return false;
		
	}
}

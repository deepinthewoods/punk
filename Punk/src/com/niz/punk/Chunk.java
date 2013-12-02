package com.niz.punk;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntMap;
import com.niz.punk.planes.Causeway;
import com.niz.punk.planes.Creative;
import com.niz.punk.planes.PrimeMaterial;





public class Chunk implements Disposable{
	private static String TAG = "Chunk";
	public static int seed;
	public Array<ParticleEffect> particles = new Array<ParticleEffect>();
	public Block[] block = new Block[Punk.CHUNKSIZE*Punk.CHUNKSIZE];
	public byte[] blockBG = new byte[Punk.CHUNKSIZE*Punk.CHUNKSIZE];
	public boolean modifiedBG = false;
	//public byte[][] lightMap = new byte[Punk.CHUNKSIZE][Punk.CHUNKSIZE];
	public int distanceFromSpawn;
	public int chunkID;
	public int heightID = 0, yOffset = 0;
	public int xOffset;
	public boolean nullChunk = false;
	public Array<ParticleEffect> clouds = new Array<ParticleEffect>();
	//static PunkBlockList lightSourceList2 = new PunkBlockList();
	PunkBlockList oreSourceList = new PunkBlockList();
	PunkBlockList grassList = new PunkBlockList();
	PunkBlockList actorSourceBlockList = new PunkBlockList();
	public int[] heightMap = new int[Punk.CHUNKSIZE+1];
	//public ChunkInfo info;

	
	public boolean IDsFinished = true, metasFinished = true, lightFinished = true, BGFinished = true, fileOpened = true, isFirstChunk = false,
	isPostFetched = true, modified = false, unScrubFinished = true;;
	public boolean spawnersChecked;
	public static SimplexNoise noise = new SimplexNoise();
	private static int CHUNKSIZE2 = Punk.CHUNKSIZE*2;
	//PunkBlockList blockUpdateList = new PunkBlockList();
	//public IntArray genQueue = new IntArray(256);
	int loadIndex = 0;
	//boolean loadFromLeft;
	private FileHandle saveHandle;
	//public int tempLeft=0, tempRight=0, smoothnessL=0, smoothnessR=0;
	//////
	int landHeight = 100;
	static Random seededRandom = new Random();
	private static Array<Object> garbage = new Array<Object>();
	//public PunkInventory[] chests = new PunkInventory[16];
	public IntMap<PunkInventory> chests = new IntMap<PunkInventory>();
	//

	
	public Array<Door> doors = new Array<Door>();
	public static PDoor doorPool = new PDoor();
	public static Array<Door> allDoors = new Array<Door>();
	public static Array<PlaneDef> planes = new Array<PlaneDef>();

	public PlaneDef activePlane = primeMaterialPlane;
	public static PrimeMaterial primeMaterialPlane = new PrimeMaterial();;
	
	public int[] weights = new int[Punk.CHUNKSIZE*Punk.CHUNKSIZE];
	public void reset(){
		//lightSourceList.clear();
		oreSourceList.clear();
		grassList.clear();
		actorSourceBlockList.clear();
		
	}
	private static int CB = Punk.CHUNKBITS, MIN_NODE_COUNT = 16;
	public Chunk(int gameType)
	{
		for (int x = 0; x < Punk.CHUNKSIZE; x++){
			columnedNodes[x] = new Array<PathfindingNode>(MIN_NODE_COUNT);
			for (int y = 0; y < Punk.CHUNKSIZE; y++){
				block[(x<<CB)+y] = new Block(0,0);
				//blockBG[x][y] = new BlockBG();
			}
			
		}
		
		//info = new ChunkInfo();
		//generate(id, seed);	
		packets.ordered = true;
			
		
		
	}
	static Block[] nullBlock = new Block[Punk.CHUNKSIZE*Punk.CHUNKSIZE];
	public Block[] showingBlock = block;
	private static Block airBlock = new BlockNoEdit(0,0);
	public Chunk(boolean b) {
		for (int x = 0; x < Punk.CHUNKSIZE; x++)
			for (int y = 0; y < Punk.CHUNKSIZE; y++){
				nullBlock[(x<<CB)+y] = airBlock;
				block[(x<<CB)+y] = airBlock;
				//blockBG[x][y] = new BlockBG();
			}
		
		//info = new ChunkInfo();
		lightFinished = false;
		nullChunk = true;
		unScrubFinished = false;
	}
	public static int[] blockPixelColors = new int[256];
	
	public void drawClouds(SpriteBatch batch, float delta, int n){
		//for (int i = 0, n = clouds.size; i < n; i++){
		if (clouds.size >n)
			clouds.get(n).draw(batch, delta);
		//}
	}
	
	/*public static void setUpMiniGameFeatures(){
		elevX.clear();
		elevY.clear();
		elevX.clear();
		elevY.clear();
		tempX.clear();
		tempY.clear();
		smoothX.clear();
		smoothY.clear();
		
		elevX.add(-1000);
		elevY.add(-200);
		elevX.add(1000);
		elevY.add(-200);
		tempX.add(-1000);
		tempY.add(0);
		smoothX.add(-1000);
		smoothY.add(2);
		
	}*/
	/*public static void setFeaturesForCreative(){
		elevX.clear();
		elevY.clear();
		tempX.clear();
		tempY.clear();
		smoothX.clear();
		smoothY.clear();
		
		elevX.add(-wWidth);
		elevY.add(32);
		tempX.add(-wWidth);
		tempY.add(0);
		smoothX.add(-wWidth);
		smoothY.add(4);
		
		
		
		
		elevationSpline = new Spline(elevX.items,elevY.items);
		smoothnessSpline = new Spline(smoothX.items, smoothY.items);
		temperatureSpline = new Spline(tempX.items, tempY.items);
		
	}*/
	
	
	
	
	public static String saveDir = "PocketMiner/saves/";
	//public InflaterInputStream inputStream;
	public BufferedInputStream inputStream;
	//public byte[] columnBlockInfo = new byte[Punk.CHUNKSIZE];
	
	
	public void setAsArena(PunkMap map){}	
	
	public void setAsMiniGame(PunkMap map, int id){
		if (id == 0)setAsArena(map);
		else setAsCreative(map);
	}

	public void clear(){
		for (int x = 0; x < Punk.CHUNKSIZE; x++)
			for (int y = 0; y < Punk.CHUNKSIZE; y++){
				block[(x<<CB)+y].set(0,0);
				block[(x<<CB)+y].setLight(0);
				block[(x<<CB)+y].setLightBits(0);
				block[(x<<CB)+y].setDayLight(0);
			}
	}
	
	public void setAsCreative(PunkMap map) {
		for (int x = 0; x < Punk.CHUNKSIZE; x++)
			for (int y = 0; y < Punk.CHUNKSIZE; y++){
				if (x == 0 || y == 0 || x ==Punk.CHUNKSIZE-1)
					block[(x<<CB)+y].set(61, 0);
				else if (y == Punk.CHUNKSIZE-1){
					block[(x<<CB)+y].set(71, 0);
					
					//map.dayLightUpdateList.addBlock(x+chunkOffset, y+yOffset-1);
				}
				else if (y < 8)
					block[(x<<CB)+y].set(2,0);
				
				else block[(x<<CB)+y].set(0,0);
				
				block[(x<<CB)+y].setDayLight(0);
				block[(x<<CB)+y].setLight(0);
				unScrubBlock(map, x, y);
			}
	}
	public boolean fetch(PunkMap map){//returns true if everything is done
		//Gdx.app.log(TAG, "fetch");
		if (!fetchAColumn(map)) return false;
		if (!doPostFetch(map)) return false;
		//Gdx.app.log(TAG, "fetch done");
		return true;
	}
	
	static ChunkDecoder dec = new ChunkDecoder();
	static ChunkBGDecoder decBG = new ChunkBGDecoder();
	public static PunkBlockList unfoundDoors = new PunkBlockList();
	//public PunkBlockList spaces = new PunkBlockList();
	public boolean startFetching(String saveLoc, int chunkID, int heightID, int p){
		//Gdx.app.log("chunk","start chinkf:"+distanceTo(PunkMap.currentChunk, PunkMap.currentChunkHeightID));
		
		l = false;r = false;u = false;d = false;
		this.chunkID = chunkID;
		this.heightID = heightID;
		showingBlock = nullBlock;
		modified = false;
		saving = false;
		freeAfterSave = false;
		xOffset = chunkID*Punk.CHUNKSIZE;
		yOffset = heightID*Punk.CHUNKSIZE;
		fileOpened = false;
		distanceFromSpawn = Math.abs(Math.max(chunkID, heightID));
		saveHandle = Gdx.files.external(saveLoc);
		unScrubFinished = false;
		spawnersChecked = false;
		actorSourceBlockList.clear();
		isPostFetched = false;
		loadIndex = 0;
		//spaces.clear();
		//Gdx.app.log(TAG, "fetchong "+p+" "+planes.size);
		activePlane = planes.get(p);
		activePlane.start(null);
		plane = p;
		//doors
		while (doors.size > 0){
			Door d = doors.pop();
			allDoors.removeValue(d, true);
			doorPool.free(d);
		}
		//nodesModified = false;
		//lightSourceList.clear();
		//grassList.clear();
		//oreSourceList.clear();
		//actorSourceBlockList.clear();
		//pixHelper.sprite.setPosition(chunkID*Punk.CHUNKSIZE, heightID * Punk.CHUNKSIZE);
		//pixHelper.sprite.setSize(Punk.CHUNKSIZE, Punk.CHUNKSIZE);
		//pixProgress = 0;
		PunkMap.allChunksLoaded = false;
		//if (!saveHandle.exists())Gdx.app.log("chunk", "not persistent!!!!!!!!!!!!!!!!!!!!!!");
		//clearLightMap();
		fileOpened = false;
		hasFile = false;
		if (saveHandle.exists()){
			//Gdx.app.log(TAG, "loading from disk:"+chunkID+"file length:"+saveHandle.length()+"path:" + saveHandle.path());
			//inputStream = new InflaterInputStream( new BufferedInputStream(saveHandle.read()));
			hasFile = true;
			//if (!fileOpened)Gdx.app.log("chunk", "error opening map file !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			IDsFinished = false;
			lightFinished = false;
			BGFinished = false;
			metasFinished = false;
			
		} else {
			
			//tmpChunk.generate(chunkID, player.gameInfo.gameSeed, player.gameInfo.gameSeed);
			//modified = true;
			freeAfterSave = false;
			IDsFinished = false;
			lightFinished = false;
			BGFinished = false;
			metasFinished = false;
			
			//Gdx.app.log(TAG,"chunk generated:" + chunkID);
		}
		//isPostFetched = false;
		loadIndex = 0;
		

		return true;
//		thickRight = 
	}
	public synchronized boolean fetchAColumn(PunkMap map){
		//if (info.isSpawnChunk) Gdx.app.log("chunk", "is spawn!");
		//while (!finished)
		//Gdx.app.log("chunk","fetch:"+chunkID+" "+loadIndex);
		
		if (loadIndex < Punk.CHUNKSIZE){
			int x = loadIndex;
			//if (chunkID == 0 && heightID == 0)Gdx.app.log("chunk", "column:"+x);
			if (hasFile){

						if (!IDsFinished){
							if (x == 0){
								
									inputStream =  new BufferedInputStream(saveHandle.read());
									garbage.add(inputStream);
									dec.set(inputStream);
									//inputStream.setUseFullReads(false);
									fileOpened = true;
								
							}
							for (int y = 0; y < Punk.CHUNKSIZE; y++){
								int id = dec.get();
								if (id == -1)Gdx.app.log("chunk", "read block ");
								block[(x<<CB)+y].set(id, 0);
								//block[x][i].dayLight = 0;
								//block[x][i].light = 0;
							}
						}
						else if (!metasFinished)
							for (int y = 0; y < Punk.CHUNKSIZE; y++){
								block[(x<<CB)+y].setMeta(dec.get());
								
							}
						else if (!lightFinished) 
							for (int y = 0; y < Punk.CHUNKSIZE; y++){
									int l = dec.get();
									//Gdx.app.log("chunk", "read light:"+(l&15)+" day:"+(l>>4));
									block[(x<<CB)+y].setLight((byte)(l & 15));
									block[(x<<CB)+y].setDayLight((byte)((l >>> 4)));
							} else if (!BGFinished){
								if (x == 0){
									decBG.set(inputStream);
								
							}
							for (int y = 0; y < Punk.CHUNKSIZE; y++){
								blockBG[(x<<CB)+y]=(byte) decBG.get();
							
								//Gdx.app.log(TAG, "bg "+blockBG[x][i]);
							}
								
							}
						else if (!unScrubFinished) 
							for (int y = 0; y < Punk.CHUNKSIZE; y++){
							//int l = inputStream.read();
						
								unScrubBlock(map, x, y);
								doNode(x,y);
							}
						
				
			} else{
				if (!lightFinished) generateColumn(x, map);
				else
					for (int y = 0; y < Punk.CHUNKSIZE; y++){
							unScrubBlock(map, x, y);
				
					}
			}
			loadIndex++;
			return false;
		} 
		
		
		
			if (!IDsFinished) {
				IDsFinished = true;
				if (!fileOpened){
					//generateDoors(map);
					if (!activePlane.finish(map, this)){
						loadIndex = 0;
						lightFinished = true;
						metasFinished = true;
					} else IDsFinished = false;
					
					//showingBlock = block;
					//unScrubFinished = true;
					
				}
				loadIndex = 0;
			}
			else if (!metasFinished){
				
				metasFinished = true;
				loadIndex = 0;
			} else if (!lightFinished){
				lightFinished = true;
				
				loadIndex = 0;
			} else if (!BGFinished){
				BGFinished = true;
				loadIndex = 0;
			}
			else if (!unScrubFinished){
				unScrubFinished = true;
				return true;
			}
	
			
		
		return unScrubFinished;
	}

	
	public synchronized boolean doPostFetch(PunkMap map){
		//Gdx.app.log("chunk", "postfetch");
		if (fileOpened){
			try {
				//readDoors(inputStream, map);
				inputStream.close();
				fileOpened = false;
			}catch (IOException ex){
				Gdx.app.log("chunk", "error closing input stream");
			}
		} else {
			activePlane.finish(map, this);
		}
		
		
		
		showingBlock = block;
		isPostFetched = true;
		flushPlaceQueue(map);
		if (packets.size > 0){
			showingBlock = nullBlock;
			isPostFetched = false;
			return false;
		}
		return true;
		
	}
	private void readDoors(BufferedInputStream inputStream2, PunkMap map) {//uses recursion
		int dx=0;
		try {
			dx = inputStream2.read();
			if (dx == -1) return;
			
			createDoor(dx, inputStream2.read(), inputStream2.read(), inputStream2.read(), inputStream2.read(), inputStream2.read() );
			readDoors(inputStream2, map);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void writeDoors(BufferedOutputStream sos, PunkMap map){
		for (int i = 0; i < doors.size; i++){
			Door d = doors.get(i);
			try {
				sos.write(d.x);
				sos.write(d.y);
				sos.write(d.anim);
				sos.write(d.dest.x);
				sos.write(d.dest.y);
				sos.write(d.destPlane);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void setForMiniGame(boolean blank){}
	public int putChest(int x, int y){
		int meta = 0;
		//step through files and find first one, then make file.
		ChunkPool.getChunkLoc(Punk.path, chunkID, heightID, plane);
		Punk.path.append(meta);
		Punk.path.append(ChunkPool.SUFF_CHEST);
		FileHandle chestFile =  Gdx.files.external(Punk.path.toString());
		while (chestFile.exists()){
			meta ++;
			ChunkPool.getChunkLoc(Punk.path, chunkID, heightID, plane);
			Punk.path.append(meta);
			Punk.path.append(ChunkPool.SUFF_CHEST);
			chestFile =  Gdx.files.external(Punk.path.toString());
		}
		PunkInventory aChest = new PunkInventory(meta);
		//TODO aChest.writeToFile(chestFile);
		chests.put(meta, aChest);
		
		return meta;
	}
	
	public boolean containsPosition(int x, int y){
		return (x >= xOffset && x < xOffset+Punk.CHUNKSIZE && y >= yOffset && y < yOffset+Punk.CHUNKSIZE);
	}
	public BlockLoc tmpLoc = new BlockLoc();
	public Block nLastBlock;
	public void unScrubBlock(PunkMap map, int x, int y){
		
		tmpLoc.set(xOffset+x, y+yOffset);
		Block b = block[(x<<CB)+y];
		//if (b.meta < -1) throw new GdxRuntimeException("meta wrong");
		if (b.blockID < 0)Gdx.app.log("chunk", "unscrub "+x+","+y+ "  "+b+xOffset+","+yOffset+this.fileOpened);
		
		PunkMap.getBlockDef(b.blockID).unscrub(map, x, y, b, this, false);
		
		if (x >0 &&  y >0 && y < Punk.CHUNKSIZE-1 && x < Punk.CHUNKSIZE-1){
			//map.updater.doLightUpdateDayLight(x-1+chunkOffset, y+yOffset);
			//map.updater.doLightUpdateNormal(x-1+chunkOffset, y+yOffset);
			//map.updater.getHighestLight(x-1+chunkOffset, y+yOffset, true);
			getLightMatrix(x, y);
			block[(x<<CB)+y].setLightBitsNew(lightMatrix, dayLightMatrix);
		} else {
			getLightMatrixSelf(x, y);
			block[(x<<CB)+y].setLightBitsNew(lightMatrix, dayLightMatrix);
		}
		
		
		
	}
	private void doNode(int x, int y){
		int nodeType = -1;
		Block b = block[(x<<CB)+y];
		if (y == 0){
			nodeRun = 0;
			nLastBlock = b;
			if (nLastBlock.blockType() >= 64 && b.blockType() == 0) nodeType = 0;
			else if (b.blockType() == 0) nodeType = 1;
		}
		else {
			
			if (nLastBlock.blockType() >= 64 && b.blockType() == 0) nodeType = 0;
			else if (b.blockType() == 0) nodeType = 1;
			
			nLastBlock = b;
		}
		if (nodeType != -1){
			nodeRun++;
			columnedNodes[x].add(PathfindingNode.obtain(x,y, nodeType));
		}else nodeRun = 0;
	}
	public Array<PathfindingNode>[] columnedNodes = new Array[Punk.CHUNKSIZE];
	public Array<PathfindingNode> nodes1 = new Array<PathfindingNode>();
	private int nodeRun, nodeProgress, nodeProgressFine;;
	private boolean nodesSaved, nodesModified;
	public void updatePaths(){
		
		switch (nodeProgress){
		case 0:
			//open file
			
			nodeProgress++;
			nodeProgressFine = 0;
			break;
		
		case 1:
			if (nodeProgressFine >Punk.CHUNKSIZE-1){
				nodeProgressFine= 0;
				nodeProgress++;
				break;
			}
			int x = nodeProgressFine;
			Array<PathfindingNode> al = columnedNodes[x];
			Array<PathfindingNode> ar = columnedNodes[x+1];
			int left = 0, right = 0;
			while (left < al.size && right < ar.size){
				PathfindingNode nl = al.get(left);
				PathfindingNode nr = ar.get(right);
				if (nl.x == nr.x){
					nr.type += 8;
					nl.type += 2;
				}else {//move
					if (nl.y > nr.y)
						right++;
					else left++;
				}
				
			}
			
			nodeProgressFine++;
				
			break;
		case 2:
			
			Array<PathfindingNode> list = columnedNodes[nodeProgressFine];
			for (int i = list.size-1; i >= 0;i--){
				PathfindingNode n = list.get(i);
				PathfindingNode nextN = list.get(i>0?i-1:i);
				if (nextN.y == n.y-1)
					n.type += 4;
				
				if (n.type == 15){//if its just hanging in the air
					list.removeIndex(i);
				}
			}
			
			nodeProgressFine++;
		break;
		
		case 3:
			
			//process/move to nodes. they're still in chunk space
			//set finished flag to true when they're fully processed
			
			break;
			
		}
		
		
	}
	
	private Block getBlock(int x, int y, PunkMap map){
		if (x < 0 || y < 0 || x > Punk.CHUNKSIZE || y >= Punk.CHUNKSIZE) return map.getBlock(x+xOffset, y+yOffset);
		return block[(x<<CB)+y];
	}

	public void setGenQueue(int i){
		loadIndex = 0;
	}
	
	private static byte[][] lightMatrix = new byte[3][3], dayLightMatrix = new byte[3][3];
	
	private void getLightMatrix(int x, int y){
		for (int i = -1; i <=1; i++)
			for (int j = -1; j <= 1; j++){
				Block b = block[((i+x)<<CB)+(y+j)];//.light
				lightMatrix[1+i][1+j] = b.getLight();
				dayLightMatrix[1+i][1+j] = b.getDayLight();
			}
	}
	private void getLightMatrixSelf(int x, int y){
		for (int i = -1; i <=1; i++)
			for (int j = -1; j <= 1; j++){
				Block b = block[(x<<CB)+y];//.light
				lightMatrix[1+i][1+j] = b.getLight();
				dayLightMatrix[1+i][1+j] = b.getDayLight();
			}
	}
	public boolean hasFile;
	public void set(Chunk achunk){
		Block tmpBlock ;
		for (int x = 0; x<Punk.CHUNKSIZE; x++)
		{
			for (int y = 0; y<Punk.CHUNKSIZE; y++)
			{
				tmpBlock = new Block(block[(x<<CB)+y].blockID, block[(x<<CB)+y].meta);
				block[(x<<CB)+y] = new Block(tmpBlock.blockID, tmpBlock.meta);
			}
		}
	}
	//public void resetLightMap(byte min){//TODO remove this it gets called loads
		/*for (int x = 0; x < Punk.CHUNKSIZE; x++)
			for (int y = Punk.CHUNKSIZE-1; y >=0; y--){
				lightMap[x][y]` = min;
			}//*/
	//}
	
	public static void doStaticNoise(boolean[][] input, int sizeX, int sizeY, int type){
		switch (type){
		case 0: 
			
				for (int x = 1; x<sizeX; x++)
				{
					for (int y = 1; y<sizeY; y++)
					{
						input[x][y] = (noise.noise(x,y) > 0.7);
					}
				}
			break;
		}
	}
	public void encroachCA(boolean[][] input, int type){
		
	}
	public boolean equals(Chunk c){
		return (chunkID == c.chunkID && heightID == c.heightID);
	}
	public boolean[][]doNoise(boolean[][] input, int NType){
		switch (NType)
		{
		case 12://make trues random
			for (int x = 0; x<Punk.CHUNKSIZE; x++)
			{
				for (int y = 0; y <Punk.CHUNKSIZE; y++)
				{
					if (input[x][y])
					input[x][y] =
						(Math.random() > 0.54);
					else input[x][y] = true;
				}
			}
			break;
		case 13://for new CA
				//making the falses random
			for (int x = 0; x<Punk.CHUNKSIZE; x++)
			{
				for (int y = 0; y <Punk.CHUNKSIZE; y++)
				{
					if (!input[x][y])
					input[x][y] =
						(Math.random() > 0.54);
					else input[x][y] = true;
				}
			}
				
			
			break;
		case 14:// false is air
			//true is valid for caves. false is always solid
			for (int x = 0; x<Punk.CHUNKSIZE; x++)
			{
				for (int y = 0; y <Punk.CHUNKSIZE; y++)
				{
					if (!input[x][y])input[x][y] = false; else
					input[x][y] =
						(Math.random() > 0.58);
					//else input[x][y] = true;
				}
			}
			//printCA(input);
			break;
		case 15:
			for (int x = 0; x<Punk.CHUNKSIZE; x++)
			{
				for (int y = 0; y <Punk.CHUNKSIZE; y++)
				{
					if (input[x][y])
						input[x][y] = (noise.noise(x,y) > 0.2);

					else input[x][y] = true;
				}
			}
			break;
		case 16://flips
			for (int x = 0; x<Punk.CHUNKSIZE; x++)
			{
				for (int y = 0; y <Punk.CHUNKSIZE; y++)
				{
					if (!input[x][y])input[x][y] = false; else
					input[x][y] =
						(Math.random() > 0.5);
					//else input[x][y] = true;
				}
			}
			
			break;
		case 4:
			for (int x = 1; x<Punk.CHUNKSIZE-1; x++)
			{
				for (int y = 1; y<landHeight-1; y++)
				{
					input[x][y] = (noise.noise(x,y) > 0.4);
				}
			}
		break;
		case 5:
			for (int x = 1; x<Punk.CHUNKSIZE-1; x++)
			{
				for (int y = 1; y<landHeight-1; y++)
				{
					input[x][y] = (noise.noise(x,y) > 0.2);
				}
			}
			break;
		case 6:
			for (int x = 1; x<Punk.CHUNKSIZE-1; x++)
			{
				for (int y = 1; y<landHeight-1; y++)
				{
					input[x][y] = Math.random() > 0.85f;
				}
			}
			break;
		case 7:
			for (int x = 1; x<Punk.CHUNKSIZE-1; x++)
			{
				for (int y = 1; y<landHeight-1; y++)
				{
					input[x][y] = Math.random() > 0.65f;
				}
			}	
			break;
		case 8:
			for (int x = 1; x<Punk.CHUNKSIZE-1; x++)
			{
				for (int y = 1; y<landHeight-1; y++)
				{
					input[x][y] = false;
				}
			}	
			
			
			break;
			
		case 9:
			for (int x = 1; x<Punk.CHUNKSIZE-1; x++)
			{
				for (int y = 1; y<landHeight-1; y++)
				{
					input[x][y] = Math.random() > 0.995f;
				}
			}	
			break;
			
		}//switch
		
		return input;	
		
	}
	public void make3Lines (boolean[][] input, int x, int y, int x2, int y2){
		makeLine(input, x, y, x2, y2);
		makeLine(input, x+1, y+1, x2+1, y2+1);
		makeLine(input, x+1, y-1, x2+1, y2-1);
		
	}
	public void makeLine (boolean[][] input, int x, int y, int x2, int y2){
		
		int w = x2 - x ;
	    int h = y2 - y ;
	    int dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0 ;
	    if (w<0) dx1 = -1 ; else if (w>0) dx1 = 1 ;
	    if (h<0) dy1 = -1 ; else if (h>0) dy1 = 1 ;
	    if (w<0) dx2 = -1 ; else if (w>0) dx2 = 1 ;
	    int longest = Math.abs(w) ;
	    int shortest = Math.abs(h) ;
	    if (!(longest>shortest)) {
	        longest = Math.abs(h) ;
	        shortest = Math.abs(w) ;
	        if (h<0) dy2 = -1 ; else if (h>0) dy2 = 1 ;
	        dx2 = 0 ;            
	    }
	    int numerator = longest >> 1 ;
	    for (int i=0;i<=longest;i++) {
	        //putpixel(x,y,color) ;
	    	//System.out.println(" line" + x + "," + y);
	        input[x][y] = false;
	        numerator += shortest ;
	        if (!(numerator<longest)) {
	            numerator -= longest ;
	            x += dx1 ;
	            y += dy1 ;
	        } else {
	            x += dx2 ;
	            y += dy2 ;
	        }
	    }
	}
	
public void makenLine (boolean[][] input, int x, int y, int x2, int y2){
		
		int w = x2 - x ;
	    int h = y2 - y ;
	    int dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0 ;
	    if (w<0) dx1 = -1 ; else if (w>0) dx1 = 1 ;
	    if (h<0) dy1 = -1 ; else if (h>0) dy1 = 1 ;
	    if (w<0) dx2 = -1 ; else if (w>0) dx2 = 1 ;
	    int longest = Math.abs(w) ;
	    int shortest = Math.abs(h) ;
	    if (!(longest>shortest)) {
	        longest = Math.abs(h) ;
	        shortest = Math.abs(w) ;
	        if (h<0) dy2 = -1 ; else if (h>0) dy2 = 1 ;
	        dx2 = 0 ;            
	    }
	    int numerator = longest >> 1 ;
	    for (int i=0;i<=longest;i++) {
	        //putpixel(x,y,color) ;
	    	//System.out.println(" line" + x + "," + y);
	        input[x][y] = true;
	        numerator += shortest ;
	        if (!(numerator<longest)) {
	            numerator -= longest ;
	            x += dx1 ;
	            y += dy1 ;
	        } else {
	            x += dx2 ;
	            y += dy2 ;
	        }
	    }
	}
	
	
	
	public boolean[][] doPlusCA(boolean[][] input, int repeats, int threshold){
		boolean[][] tmp = new boolean[input.length][input[0].length];

		for (int CAIter = 0; CAIter<repeats; CAIter++)
		{
			for (int x = 1; x<input.length-1; x++)
			{
				for (int y = 1; y<input[0].length-1; y++)
				{//look at sideways blocks
					int total =0;
					for (int lookx=-1; lookx<=1; lookx++)
						for (int looky = 0; looky<=0; looky++)
						{
							if (input[x+lookx][y+looky]) total++;
						}
					//then print block
					if (total>=threshold) tmp[x][y] = true; else tmp[x][y] = false; 
				}
			}
			
			for (int x = 0; x<input.length; x++)
			{
				for (int y = 0; y<input[0].length; y++)
				{
					input[x][y] = tmp[x][y];
				}
			}
			
			for (int x = 1; x<input.length-1; x++)
			{
				for (int y = 1; y<input[0].length-1; y++)
				{//look at vertical blocks
					int total =0;
					for (int lookx=0; lookx<=0; lookx++)
						for (int looky = 01; looky<=1; looky++)
						{
							if (input[x+lookx][y+looky]) total++;
						}
					//then print block
					if (total>=threshold) tmp[x][y] = true; else tmp[x][y] = false; 
				}
			}
			for (int x = 0; x<input.length; x++)
			{
				for (int y = 0; y<input[0].length; y++)
				{
					input[x][y] = Math.random() > 0.65f;
				}
			}

			
		}
		
		return tmp;
	}
	
	public static void printCA(boolean[][] input){
		/*for (int i = 1; i < input.length-1; i++)
		{
			System.out.print(""+i+":");
			for (int j = 1; j < input[0].length-1; j++)
			{
				System.out.print(input[i][j]?"0":" ");
				
			}
			System.out.println();
		}*/
	}
	
	public static boolean[][] doCA(boolean[][] input, int repeats, int threshold){
		boolean[][] tmp = new boolean[input.length][input[0].length];
		for (int CAIter = 0; CAIter<repeats; CAIter++)
		{
			for (int x = 1; x<input.length-1; x++)
			{
				for (int y = 1; y<input[0].length-1; y++)
				{//look at surrounding blocks
					int total =0;
					for (int lookx=-1; lookx<=1; lookx++)
						for (int looky = -1; looky<=1; looky++)
						{
							if (input[x+lookx][y+looky]) total++;
						}
					//then print block
					if (total>=threshold) tmp[x][y] = true; else tmp[x][y] = false; 
				}
			}
			for (int x = 0; x<input.length; x++)
			{
				for (int y = 0; y<input[0].length; y++)
				{
					input[x][y] = tmp[x][y];
				}
			}
			
		}
		
		return tmp;
	}
	public void flipBits(boolean[][] input){
		for (int x = 0; x<Punk.CHUNKSIZE; x++)
			for (int y = 0; y <Punk.CHUNKSIZE; y++)
				input[x][y] = !input[x][y];
	}
	public static boolean[][] doOppCA(boolean[][] input, int repeats, int threshold){
		boolean[][] tmp = new boolean[input.length][input[0].length];
		for (int CAIter = 0; CAIter<repeats; CAIter++)
		{
			for (int x = 1; x<input.length-1; x++)
			{
				for (int y = 1; y<input[0].length-1; y++)
				{//look at surrounding blocks
					int total =0;
					for (int lookx=-1; lookx<=1; lookx++)
						for (int looky = -1; looky<=1; looky++)
						{
							if (!input[x+lookx][y+looky]) total++;
						}
					//then print block
					if (total>=threshold) tmp[x][y] = true; else tmp[x][y] = false; 
				}
			}
			for (int x = 0; x<input.length; x++)
			{
				for (int y = 0; y<input[0].length; y++)
				{
					input[x][y] = !tmp[x][y];
				}
			}
			
		}
		
		return tmp;
	}
	
	public void makeCaves(boolean[][] input, int amount, int thickness){
		int startX, startY, endX, endY;
		
		//topLeft
		startX = (int)(Math.random()*40);
		startY = (int)(Math.random()*40);
		endX = (int)(Math.random()*40);
		endY = (int)(Math.random()*40);
		
		for (int i = 0; i < amount; i++)
		{
			makeLine(input, startX, startY, endX, endY);
			makeLine(input, startX+1, startY, endX+1, endY);
			makeLine(input, startX, startY+1, endX, endY+1);
			startX = endX;
			startY = endY;
			endX+= (int)(Math.random()*20)-10;
			endY+= (int)(Math.random()*20)-10;
			
			endX = Math.min(input.length-2, endX);
			endX = Math.max(0 , endX);
			
			endY = Math.min(input[0].length-5, endY);
			endY = Math.max(0 , endY);
		}
		
		//bottomR
		endX = (int)(Math.random()*40)+input.length-30;
		endY = (int)(Math.random()*40);
		
		endX = Math.min(input.length-2, endX);
		endX = Math.max(0 , endX);
		endY = Math.min(input[0].length-2, endY);
		endY = Math.max(0 , endY);
		
		startX = endX;
		startY = endY;
		
		for (int i = 0; i < amount; i++)
		{
			makeLine(input, startX, startY, endX, endY);
			//makeLine(input, startX-1, startY, endX-1, endY);
			makeLine(input, startX, startY+1, endX, endY+1);
			startX = endX;
			startY = endY;
			endX+= (int)(Math.random()*20)-10;
			endY+= (int)(Math.random()*20)-10;
			
			endX = Math.min(input.length-2, endX);
			endX = Math.max(0 , endX);
			
			endY = Math.min(input[0].length-5, endY);
			endY = Math.max(0 , endY);
		}
		
		//topR
		endX = (int)(Math.random()*40)+input.length-30;
		endY = (int)(Math.random()*40)+input[0].length-30;
		
		endX = Math.min(input.length-2, endX);
		endX = Math.max(0 , endX);
		endY = Math.min(input[0].length-5, endY);
		endY = Math.max(0 , endY);
		startX = endX;
		startY = endY;
		for (int i = 0; i < amount; i++)
		{
			makeLine(input, startX, startY, endX, endY);
			makeLine(input, startX-1, startY, endX-1, endY);
			makeLine(input, startX, startY+1, endX, endY+1);
			startX = endX;
			startY = endY;
			endX+= (int)(Math.random()*20)-10;
			endY+= (int)(Math.random()*20)-10;
			
			endX = Math.min(input.length-2, endX);
			endX = Math.max(0 , endX);
			
			endY = Math.min(input[0].length-5, endY);
			endY = Math.max(0 , endY);
		}
		
		//topL
		endX = (int)(Math.random()*40);
		endY = (int)(Math.random()*40)+input[0].length;
		
		endX = Math.min(input.length-3, endX);
		endX = Math.max(2 , endX);
		endY = Math.min(input[0].length-3, endY);
		endY = Math.max(2 , endY);
		startX = endX;
		startY = endY;
		for (int i = 0; i < amount; i++)
		{
			makeLine(input, startX, startY, endX, endY);
			makeLine(input, startX+1, startY, endX+1, endY);
			//makeLine(input, startX, startY-1, endX, endY-1);
			startX = endX+1;
			startY = endY+1;
			endX+= (int)(Math.random()*20)-10;
			endY+= (int)(Math.random()*20)-10;
			
			endX = Math.min(input.length-2, endX);
			endX = Math.max(0 , endX);
			
			endY = Math.min(input[0].length-2, endY);
			endY = Math.max(0 , endY);
		}
		
		
		//middle
		endX = (int)(Math.random()*40) + input.length/2 - 20;
		endY = (int)(Math.random()*40)+input[0].length/2-20;
		
		endX = Math.min(input.length-3, endX);
		endX = Math.max(2 , endX);
		endY = Math.min(input[0].length-3, endY);
		endY = Math.max(2 , endY);
		startX = endX;
		startY = endY;
		for (int i = 0; i < amount*2; i++)
		{
			makeLine(input, startX, startY, endX, endY);
			makeLine(input, startX+1, startY, endX+1, endY);
			//makeLine(input, startX, startY-1, endX, endY-1);
			startX = endX+1;
			startY = endY+1;
			endX+= (int)(Math.random()*20)-10;
			endY+= (int)(Math.random()*20)-10;
			
			endX = Math.min(input.length-20, endX);
			endX = Math.max(2 , endX);
			
			endY = Math.min(input[0].length-20, endY);
			endY = Math.max(2 , endY);
		}
		
		for (int i = 0; i < 0; i++)
		makeLine(input, (int)(Math.random()*input.length-2), (int)(Math.random()*input[0].length-2)+1, 
				(int)(Math.random()*input.length-2), (int)(Math.random()*input[0].length-2));
		
		
		
		//makeLine(input,25,15,20,80);
		doCA(input, 1, 3);
	}
	
	public boolean[][] metaBall(int sizeX, int sizeY){
		boolean[][] tmpData = new boolean[sizeX][sizeY];
		byte total = 0;
		
		for (int x = 0; x < sizeX; x++)
			for (int y = 0; y < sizeY; y++)
				tmpData[x][y] = false;
		
		for (int x = 1; x < sizeX-1; x++)
			for (int y = 1; y < sizeY-1; y++)
				tmpData[x][y] = (Math.random() > 0.35f);
		
		//             repeats
		for (int c = 0; c < 3; c++)
		for (int x = 1; x < sizeX-1; x++)
			for (int y = 1; y < sizeY-1; y++)
			{
				total = 0;
				for (int a = -1; a < 2; a++)
					for (int b = -1; b < 2; b++)
						if (tmpData[x-a][y-b]) total++;
				tmpData[x][y] = (total >= 5);
			}
		
		return tmpData;
	}
	

		
	
	
	
	public void makeASpace(boolean[][] input, int x, int y, int type){
	int width;
	int height;
	switch (type){
	case 0: width = 50;
			height = 30;
		break;
		
	default: width = 5;
			height = 5;
		break;
		
	}
		
		for (int sx = 0; sx < width; sx++)
			for (int sy = 0; sy < height; sy++)
				input[x+sx][y+sy] = true;
	
	
	}
	
	public void makeHeightMap(int[] height, int[]dirt, int smoothness, int seed){
		
		
		int xOffset = chunkID*Punk.CHUNKSIZE;
		for (int x=0;x<=Punk.CHUNKSIZE; x+=1)
		{
			//offsetmap[x] = 0;//(int)(Math.random()*20);
			height[x] =(int)( noise.noise( (x+xOffset)/24f, 0 ) * smoothness +landHeight )+3;
			dirt[x] =(int)( noise.noise( (x+xOffset)/24f, 0.25f ) * smoothness + landHeight)-1;
			//System.out.println("noisex:" + x + " height "+heightmap[x]);
		}
		
		
	}
	
	
	public void makeBigFeatures(int[] height, int[] dirt, int type){
//		offsetmap[0] = 1;
//		for (int x=0;x<=3; x+=1)
//		{
//			for (int y=1; y<Punk.CHUNKSIZE; y++){
//				offsetmap[y] = (offsetmap[y-1]+offsetmap[y+1])/2;
//			}		
//		}
		
//		for (int x=0;x<Punk.CHUNKSIZE; x++)
//		{
//			heightmap[x] += offsetmap[x];
//			//System.out.println("offset"+offsetmap[x]);
//		}
	}
	
	public void makeTunnels(boolean[][] input, int type){
		//make a hole
		
		
		//make tunnels coming from the hole
		boolean tunnelLeft = false;
		boolean tunnelUp = true;
		//type -1=left, 0=middle, 1=right
		int startX;
		int startY;
		int tunnelX;
		int tunnelY;
		int length;
		startX = input.length/2 + ((input.length/2-20) * type);int slope;
		
		
		
		for (int r = 0; r < 6; r++){
			startX = input.length/2 + ((input.length/2-20) * type);
			startY = 10;
			tunnelX = startX ;
			tunnelY = startY;
			length = 0;
			tunnelLeft = ((Math.random() > 0.5f)?true:false);
			slope = (int)(Math.random()*5+3);
			//tunnelUp = ((Math.random() > 0.5f)?true:false);
			//while (tunnelY > 10 && tunnelY < input[0].length && tunnelX)
			//System.out.println("r : " +r);
			while (length < 80 ){//&& tunnelX > 6 && tunnelX < input.length && tunnelY > 6 && tunnelY < input[0].length){
				
				makeLine(input, startX, startY, tunnelX, tunnelY);
				//makeLine(input, startX+1, startY, tunnelX, tunnelY+1);
				//makeLine(input, startX, startY+1, tunnelX+1, tunnelY);

				//System.out.println("start: " + startX + "," + startY);
				startX = tunnelX;
				startY = tunnelY;
				
				tunnelY += ((int)(Math.random()*8) * (tunnelUp?1:-1));
				tunnelX += ((int)(Math.random()*slope+3) * (tunnelLeft?-1:1));
				if (tunnelX > input.length-10 ) {
					tunnelLeft = true;
					slope = (int)(Math.random()*5+3);
				}
				if (tunnelX < 10){
					tunnelLeft = false;
					slope = (int)(Math.random()*5+3);
				}
				if (tunnelY > input[0].length-35 ) tunnelUp = false;
				if (tunnelY < 10) tunnelUp = true;
				length ++;
				
			}
		}
		
		makeASpace(input, Punk.CHUNKSIZE/2 + type*(Punk.CHUNKSIZE/2-30), 10, 0);
		doCA(input, 3, 3);
		
	}
	
	public int interp(int num1, int num2, float delta){
		return (int)(num2 * delta + num1 * (1-delta));
	}
	
	public void makeMountain(int[] height, int[] dirt){
		int influence;
		for (int x = 0; x < Punk.CHUNKSIZE; x++)
		{
			//influence[x] = 
		}
		
		int[] mountainmap = new int[Punk.CHUNKSIZE];
		
		//int plats = 0;//(int)(Math.random()*3)+1;
		//int[] platWidth = new int[plats];
		//int[] platHeight = new int[plats];
		//int[] platX = new int[plats];
		//number of plateaux
		
		//slopes and plats
		//maybe define plats first
	/*	for (int p = 0; p < plats; p++){
			platWidth[p] = (int)(Math.random()*10);
			platHeight[p] = Punk.CHUNKSIZE - 20 - (int)(Math.random()*80);
			platX[p] = (Punk.CHUNKSIZE/2) + (int)(Math.random()*80)-40;
		}
		*/
		
		//then make slopes join them up
		//left to right
		
		int x = 0;
		int startX = 0;
		int endX =0;
		
		int width ;//= endX - startX;
		float delta ;//= 0;
		int platHeight;
		int platWidth = 0;
		platHeight = Punk.CHUNKSIZE - 50 - (int)(Math.random()*80);
		endX +=  (int)(Math.random()*40)+20;
		
		while (endX < Punk.CHUNKSIZE - 50){
			width = endX - startX;
			delta = 0;
			while (x < endX)
			{
				delta = 1f/width * (x-startX);
				//mountainmap[x] =//= (height[x]*x + platHeight[0])/(x+1);
				//interpolate
				height[x] = interp(height[startX], platHeight, delta);
				dirt[x] = interp(dirt[startX], platHeight, delta);

				
				x++;
			}
			platWidth = (int)(Math.random()*10);
			//need to output the plat
			while (x < endX + platWidth+1){
				height[x] = platHeight;
				dirt[x] = platHeight-3;
				
				x++;
			}
			startX = endX + platWidth;
			platHeight = Punk.CHUNKSIZE - 50 - (int)(Math.random()*80);
			endX +=  (int)(Math.random()*70);
		}
		//last slope
		//startX = endX + platWidth;
		endX = Punk.CHUNKSIZE;
		while (x < Punk.CHUNKSIZE){
			width = endX - startX;
			delta = 1f/width * (x-startX);
			height[x] = interp(height[startX-1], height[endX], delta);
			dirt[x] = interp(dirt[startX-1], dirt[endX], delta);
			x++;
		}
		
		
		/*for (int i = 0; i < Punk.CHUNKSIZE; i++)
			System.out.println("heightm" +i+":"+ height[i]);*/
		//return height;
	}
	/*public void makeNewHeight(int seed, float[] smoothnessChart, float[]tempChart){
		//new method using density
		int xOffset = chunkID*Punk.CHUNKSIZE;
		int waterLevel = 120;
		float density, adjustedDensity;
		int landBlock, surfaceBlock;
		float adjustedSmoothness ;
		
		for (int x = 0; x <Punk.CHUNKSIZE; x+=1)
		{
			adjustedSmoothness = (smoothnessChart[x]+1)*8f;//should be between 1-16 now
			//adjustedSmoothness = 16;
			landBlock = getLandInfo(tempChart[x])[1];
			surfaceBlock = getLandInfo(tempChart[x])[0];
			for (int y = 0; y < Punk.CHUNKSIZE; y++){
				
				density = noise.noise(chunkID*8+x/32f, y/32f, seed);
				adjustedDensity = density*adjustedSmoothness+y;
				if ( adjustedDensity <100)
					block[(x<<CB)+y] = new Block(1,0);
				else if ( adjustedDensity <104)
					block[(x<<CB)+y] = new Block(landBlock,0);
				else block[(x<<CB)+y] = new Block(0,0);
				//Gdx.app.log("chunkgen", ""+x+y);
			}
		}
	}*/
	public static void makeRandom(boolean[][] input, boolean inFlag, float randomnessfactor){
		for (int x = 0; x < input.length; x++)
			for (int y = 0; y < input[0].length; y++){
				if (inFlag == input[x][y])
					input[x][y] = (Math.random() > randomnessfactor);
				
			}
	}
	public void makeSimplexRandom(boolean[][] input, boolean inFlag, float randomnessfactor, float sampleSize){
		float randomseed = MathUtils.random();
		for (int x = 0; x < input.length; x++)
			for (int y = 0; y < input[0].length; y++){
				if (inFlag == input[x][y])
					input[x][y] = (noise.noise((x+chunkID)/sampleSize+randomseed, (y+chunkID)/sampleSize)+randomseed > randomnessfactor);
				
			}
	}
	public void makeSquigglyLine(boolean[][] input, int x1, int y1, 
			int x2, int y2, int samples, int wobblyness){
		int lastx = x1, lasty = y1;
		int nextx, nexty;
		//divide line into smaller lines
		for (int i = 1; i <= samples; i++){
			//interpolate point, draw line, save point
			nextx = interp(x1,x2,1f/samples*i)+ (int)(Math.random()*wobblyness);
			nexty = interp(y1,y2,1f/samples*i)+(int)(Math.random()*wobblyness);
			nexty += (int)(Math.random()*wobblyness);
			//Gdx.app.log("chunkgen","line("+i+"):"+nextx+","+nexty+"last:"+lastx+","+lasty);

			makeLine(input, lastx, lasty, nextx, nexty);
			makeLine(input, lastx+1, lasty+1, nextx+1, nexty+1);
			makeLine(input, lastx+1, lasty, nextx+1, nexty);
			makeLine(input, lastx+2, lasty+2, nextx+2, nexty+2);
			makeLine(input, lastx+2, lasty+1, nextx+2, nexty+1);

			lastx = nextx;
			lasty = nexty;
		}
		
	}
	public void makeNewCA(boolean[][] input, int type, int seed){
		//init. true is solid on the CA
		//making them true if they're valid 
		for (int x = 0; x < 256; x++)
			for (int y = 0; y < 256; y++){
				if (block[(x<<CB)+y].blockID == 0)input[x][y] = false;
				//else if (y < 104) input[x][y] = true;
				else input[x][y] = true;
				 if(y > 95) input[x][y] = false;
			}
		
		
		//noise?
		//doOppCA(input, 4, 5);
		switch (type){
		case 1:
			//makeRandom(input, true, 0.43f);
						//doNoise(input, 16);
			
			//make some random lines
			flipBits(input);
			makeRandom(input, false, 0.9f);
			//printCA(input);
			doCA(input,2,3);
			
		//	printCA(input);
			makeSimplexRandom(input, false, -.2f, 16);
			int y1,y2;
			int repeats = (int)(Math.random()*5)+4;
			for (int i = 0; i < repeats; i++){
				y1 =(int)(Math.random()*80);
				y2 =(int)(Math.random()*80);
				makeSquigglyLine(input, 2, y1, 235, y2, 32, 16);

			}
			doCA(input, 1, 4);

			//doCA(input, 3, 4);
			
			/*
			for (int x = 0; x < 256; x++)
				for (int y = 0; y < 150; y++){
					input[x][y] = input[x][y+13];
				}
				*/
			break;
		}
	}
	public boolean[][] makeADeposit(int sizex, int sizey){
		boolean[][] tmpdeposit = metaBall(sizex, sizey);
		makeRandom(tmpdeposit, true, 0.7655f);
		doCA(tmpdeposit, 1, 4);
		makeRandom(tmpdeposit, true, 0.5f);
		return tmpdeposit;
	}
	/*public void writeDeposit(boolean[][] input, int posx, int posy, int depositID, PunkMap map){
		for (int x = 0; x < input.length; x++)
			for (int y = 0; y < input[0].length; y++){
				if (input[x][y] && block[x+posx][y+posy].blockID == 1){
					block[x+posx][y+posy].set(depositID, 0);
					//Gdx.app.log("map", "making deposit"+posx+","+posy);
				}
					
			}
	}
	public void writeCrystals(boolean[][] input, int posx, int posy, int depositID){
		for (int x = 0; x < input.length; x++)
			for (int y = 0; y < input[0].length; y++){
				if (input[x][y] && block[x+posx][y+posy].blockID == 0 && block[x+posx][y+posy+1].blockID == 1)
					block[x+posx][y+posy].set(depositID, 0);
			}
		//printCA(input);
	}*/
	public boolean[][] makeCrystalPlaces(int sizex, int sizey){
		boolean[][] tmpdeposit = metaBall(8, 120);
		makeRandom(tmpdeposit, true, 0);
		makeRandom(tmpdeposit, false, 0);
		
		return tmpdeposit;
	}
	public static float lerp(float a, float b, float delta){
		return (1-delta)*a+delta*b;
	}
	
	public int lerpChunkSize(int b, int a, int delta){
	    return ((a*delta)>>(Punk.CHUNKBITS))+(b*(Punk.CHUNKSIZE-delta)>>(Punk.CHUNKBITS));

	}
	
	
	public boolean saving;

	

	
	public boolean freeAfterSave;
	
	//fade 16 blocks from top
	/*
	
	  	8x8 block
	
	
	
	
	
	
	
	 */
	//public int plane;
	public void generateColumn(int x, PunkMap map){//local values
		/*
		if (yOffset < effectiveDepth-seaGap || x+chunkOffset < -wWidth-Punk.CHUNKSIZE || x+chunkOffset > wWidth + Punk.CHUNKSIZE){
			generateWaterColumn(x, map);
		}else 
		generateElevatedColumn(x,map);
		*/
		activePlane.generateColumn(x, map, this);
		
		
	}//*/
	
	
	
	public void generate(int chunkID, int seed, int startoffset)
	{
		/*chunkID += startoffset;
		//for the automata
		boolean[][] CA = new boolean[Punk.CHUNKSIZE][Punk.CHUNKSIZE];
		
		int chunkType= (int)(noise.noise(chunkID/14f, seed) * 6.49f);
		//for (int i = 0; i < 100; i++)
		//System.out.println("noise:" + (int)(SimplexNoise.noise(i/14f, seed) * 5.99f));
		System.out.println("chunk type:" + chunkType);
		//chunkType = -5;
		//if (chunkType != -2 && chunkType != 2 && chunkType !=1 && chunkType != 3) chunkType = 2;
		//init values
		
		//int xOffset = chunkID*Punk.CHUNKSIZE;
		Gdx.app.log("chunkgen", "making cmoothness chart for chunk:"+chunkID);

		float[] smoothnessChart = new float[Punk.CHUNKSIZE];
		for (int x = 0; x < Punk.CHUNKSIZE; x++){
			smoothnessChart[x]=
				noise.noise(chunkID/16f+x/4096f, seed);
			//Gdx.app.log("chunkgen", "smoothness"+x+":"+smoothnessChart[x]);
		}
		
		float[] temperatureChart = new float[Punk.CHUNKSIZE];
		for (int x = 0; x < Punk.CHUNKSIZE; x++){
			temperatureChart[x]=
				noise.noise(chunkID/8f+x/2048f, seed);
			//Gdx.app.log("chunkgen", "smoothness"+x+":"+smoothnessChart[x]);
		}
		
		
		boolean mountain = false;
		int treeType = 6;
		//blockID for saplings
		float treeDensity = 1f;
		int treeMinSpacing = 2;
		//density: chance in 100 of tree generating on surface block
		
		//TODO mushrooms/.flowers etc
		
		chunkType = (int)(temperatureChart[0]*6.5f);
		
		makeNewHeight(seed, smoothnessChart, temperatureChart);
		//makeWaterLevel();//doesn't work :(
		
		makeNewCA(CA, 1, seed);
		//printCA(CA);
		
		
		
		//quite nice normal ones
		//doNoise(CA, 4);
		//doCA(CA, 2, 3);
		
		//big ones
		//doNoise(CA, 5);
		//doCA(CA, 4, 4);
		
		//TODO make plusCA not shit
		//doNoise(CA, 6);
		//doPlusCA(CA, 3, 1);
		
		//nice smallish ones
		//doNoise(CA, 7);
		//doCA(CA, 2,4);
		
		//niceish tunnel caves
		//doNoise(CA, 9);//8=solid, 9 = slight noise
		//makeCaves(CA, 100, 2);
		
		//doNoise(CA, 9);
		//makeTunnels(CA, 0);
		
		//-1left0middle1right
		
		//printCA(CA);
		
		
		
		
		
		
		
	
		//write the caves
		//System.out.println("writing caves");
		for (int x=1; x<Punk.CHUNKSIZE-1; x++)
		{
			for (int y=0; y<256; y++)
			{	
				
				if (!CA[x][y])
					block[(x<<CB)+y].set(0,0);
				
			}
			
		}
		
		
		makeCaveFluids();
		//makeWaterLevel();
		makeDeposits();
		makeSkyIslands(1);
		makeFlowers();
		
		//makeDwarfCastle(0);
		
		//trees
		
		int treeSpaceCount = 0, treeProgress = 0, y;
		boolean treeDone = false;
		for (int x=1; x<Punk.CHUNKSIZE-1; x++)
		{
			y = 200;//200 down to 50
			treeDone = false;
			while (!treeDone && y > 50 ){
				if (block[(x<<CB)+y].blockID == 0 && block[x][y-1].blockID == getLandInfo(temperatureChart[x])[1])
					treeDone = true;
				y--;
			}
			
			treeType = getLandInfo(temperatureChart[x])[2];
			treeDensity = Math.abs(temperatureChart[x])/4f +.1f;
			if (Math.abs(temperatureChart[x])>.8) treeMinSpacing = 16;
			else treeMinSpacing = 4;
			if (treeDone && Math.random() < treeDensity && treeSpaceCount >= treeMinSpacing)
			{
				block[(x<<CB)+y].set(treeType,(int)(Math.random()*16));
				treeSpaceCount = 0;
				//Gdx.app.log("chunkGen", "PLACED TREE!�+!!!"+treeType+"@"+x+","+y);
			} else {
				treeSpaceCount++;
				block[(x<<CB)+y].set(getLandInfo(temperatureChart[x])[0], 0);
			}
		}
			
		//other plants?
		
		//bed. -3
		
		*/
		/*for (int i = 0; i < Punk.CHUNKSIZE; i++){
			generateColumn(i, seed, map);
		}*/

		
	}
	
	

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	public int distanceTo(int x, int y) {
		//return Math.max(Math.abs(chunkID - x), Math.abs(heightID - y));
		return Math.abs(chunkID - x)+ Math.abs(heightID - y);
	}
	
	private Array<PlacePacket> packets = new Array<PlacePacket>();
	public int plane;
	public boolean l, r, u, d;
	public final int placeLimit = 5;
	public PunkBlockList lightUpdates = new PunkBlockList();

	public void flushPlaceQueue(PunkMap map){
		int count = 0;
		int size = Math.min(placeLimit, packets.size);
		//if (size > 0)Gdx.app.log(TAG, "place packets:"+size);
		while (count < size){
			PlacePacket p = packets.removeIndex(0);
			Block b = block[((p.x&Punk.CHUNKSIZEMASK)<<CB)+(p.y&Punk.CHUNKSIZEMASK)];
			map.change(b,this, p.x+xOffset, p.y+yOffset, p.id, p.meta, false, false);
			//if (map.getBlock(p.x, p.y).blockID == p.id)Gdx.app.log(TAG, "placed successfully");
			//else Gdx.app.log(TAG, "placed unsuccessfully");
			p.free();
			count++;
		}
		
	}
	public void addPlaceQueue(int x, int y, int iID, int metaData, int p) {
		//Gdx.app.log(TAG, "add packet"+x+","+y+" id"+iID);
		packets.add(PlacePacket.pool.obtain().set(x,y,iID, metaData, p));
	}
	public static  void gc(){
		garbage.clear();
	}

	public int distanceFromPlayer() {
		return distanceTo(PunkMap.currentChunk, PunkMap.currentChunkHeightID);
	}
	public void createDoor(int x, int y, int id, int destX, int destY, int destPlane){
		Door d = doorPool.obtain();
		d.set(x,y,id, destX, destY, destPlane, -.5f, 0);
		doors.add(d);
		allDoors.add(d);
		modified = false;
		Gdx.app.log("chunk","create door"+allDoors.size);
	}
	public static void updateDoors(Player player, PunkMap map){
		
		Iterator<Door> it = allDoors.iterator();
		while (it.hasNext()){
			Door d = it.next();
			d.update(player, map);
			//Gdx.app.log("chunk","update doors");
		}
	}
	public static void drawDoors(SpriteBatch batch){
		Iterator<Door> it = allDoors.iterator();
		while (it.hasNext()){
			Door d = it.next();
			d.draw(batch);
		}
	}
	public static boolean checkPlayerDoorCollisions(Player player){//true if found door
		Iterator<Door> it = allDoors.iterator();
		
		while (it.hasNext()){
			Door d = it.next();
			if (player.distanceToPlayer(d.x, d.y)< 2) {
				player.activeDoor = d;
				return true;
			}
		}
		//player.activeDoor = null;
		return false;
	}

	public static int getTemp(int x) {
		return planes.get(PunkMap.currentPlane).getTemp(x);
	}

	public static int[] getLandInfo(int temp) {
		return planes.get(PunkMap.currentPlane).getLandInfo(temp);
	}

	public static int getGroundHeight(int i) {
		return planes.get(PunkMap.currentPlane).getGroundHeight(i);
	}

	public static int getSmoothness(int x) {
		return planes.get(PunkMap.currentPlane).getSmoothness(x);
	}

	public static void createNewFeatures(int gameSeed, PunkBodies monsterIndex, PunkMap map, int size, String[] features) {
		
		primeMaterialPlane.size = size;
		
		//primeMaterialPlane.setUp(monsterIndex, map);
		planes.add(primeMaterialPlane);
		planes.add(new Causeway(gameSeed));
		planes.add(new Creative(gameSeed));
		//save
		for (int i = 0; i < planes.size; i++)
			planes.get(i).setUp(monsterIndex, map);
		
		saveFeatures(gameSeed, monsterIndex);
		
	}
	private static StringBuilder tmpStr = new StringBuilder();
	public static void loadFeatures(int gameSeed, PunkBodies monsterIndex) {
		Punk.getSaveLoc(tmpStr);
		//primeMaterialPlane.loadFromDisk(tmpStr);
		planes.add(primeMaterialPlane.loadFromDisk(tmpStr));
		planes.add(new Causeway(gameSeed).loadFromDisk(tmpStr));
		planes.add(new Creative(gameSeed).loadFromDisk(tmpStr));
		
	}
	public static void saveFeatures(int gameSeed, PunkBodies monsterIndex) {
		Punk.getSaveLoc(tmpStr);
		primeMaterialPlane.saveToDisk(tmpStr);
		Gdx.app.log(TAG, "saving features");
	}

	private static final int LIGHTUPDATES = 10;
	private byte[][] lm = new byte[3][3], dm = new byte[3][3];
	public IntMap<String> signs = new IntMap<String>();
	public boolean updateLight(PunkMap map) {
		
		int count = 0;
		while (count < LIGHTUPDATES && !lightUpdates.list.isEmpty()){
			
			BlockLoc loc = lightUpdates.removeFirst();
			int x = loc.x & Punk.CHUNKSIZEMASK, y = loc.y & Punk.CHUNKSIZEMASK;
			boolean chunkEdge = (x == 0 || y == 0 || x == Punk.CHUNKSIZE-1 || y == Punk.CHUNKSIZE-1);
			boolean edge = map.chunkPool.isOnEdge(loc);
			if (chunkEdge){//special case
				if (edge)
					continue;
				for (int i = -1; i <= 1; i++)
					for (int j = -1; j <= 1; j++){
						Block b = map.getBlock(loc.x+i,loc.y+j);
						lm[i+1][j+1] = b.getLight();
						dm[i+1][j+1] = b.getDayLight();
					}
			} else {
				for (int i = -1; i <= 1; i++)
					for (int j = -1; j <= 1; j++){
						Block b = block[((x+i)<<CB)+(y+j)];
						lm[i+1][j+1] = b.getLight();
						dm[i+1][j+1] = b.getDayLight();
					}
						
			}
			
			Block tmpB = block[(x<<CB)+y];
			BlockDef bd = map.getBlockDef(tmpB.blockID);
			//Gdx.app.log(TAG, "light size "+lightUpdates.list.size()+"  "+loc+"    " +tmpB);
			boolean updated = false;
			if (bd.updateDayLight(loc.x, loc.y, tmpB, this, lm, dm)) {
				//if (!edge){
					map.addLightUpdatesSurrounding(loc);;
					//Gdx.app.log(TAG, "prop "+tmpB+"  @ "+loc.x+","+loc.y);
				//}
				
				updated = true;
			}
			if (bd.updateLight(loc.x, loc.y, tmpB, this, true, lm, dm)) {
				//if (!edge){
					if (!updated)map.addLightUpdatesSurrounding(loc);;
					//Gdx.app.log(TAG, "prop "+tmpB+"  @ "+loc.x+","+loc.y);
				//}
				
				updated = true;
			}
			//Gdx.app.log(TAG, "after"+tmpB+"@"+x+","+y);
			//if (bd.updateLight(loc.x, loc.y, tmpB, this, edge, lm, dm)){
			//	if (!updated) map.addLightUpdatesSurrounding(loc);;
				//TODO possibly being left out?
			//}
		}

		if (lightUpdates.list.isEmpty()) return true;
		return false;
	}
	/*public int pixProgress;
	public Pixmap pix = new Pixmap(Punk.CHUNKSIZE, Punk.CHUNKSIZE, Format.RGB888);
	public PixmapHelper pixHelper = new PixmapHelper(pix);
	
	public boolean updatePixmap(){//return false if finished
		if (pixProgress >= Punk.CHUNKSIZE) return false;
		for (int i = 0; i < Punk.CHUNKSIZE; i++)
			pixHelper.setPixel(pixProgress, i, blockPixelColors[block[pixProgress][i].blockID]);
		pixProgress++;
		pixHelper.update();
		return true;
	}

	public void draw(SpriteBatch batch) {
		Sprite s = pixHelper.sprite;
		s.draw(batch);
		
	}
	
	public static void setupPixels(Pixmap map){
		for (int i = 0; i < 256; i++){
			blockPixelColors[i] = map.getPixel(i%16, i/16);
			Gdx.app.log(TAG, "pixels "+blockPixelColors[i]);
		}
	}*/

	public void draw(SpriteBatch batch, int mx, int my, int lastMX, int lastMY) {
		
		
		
		int x = mx - xOffset, y = my - yOffset, x2 = lastMX - xOffset, y2 = lastMY - yOffset;
		
		x = Math.max(0, x);
		y = Math.max(0, y);
		int sy = y;
		x2 = Math.min(Punk.CHUNKSIZE-1, x2);
		y2 = Math.min(Punk.CHUNKSIZE-1, y2);
		//x+= xOffset;
		//y += yOffset;
		//x2 += xOffset;
		//y2 += yOffset;
		while (x <= x2){
			while (y <= y2){
				Block tmpBlock = block[(x<<CB)+y]; 
				int renderTmp = tmpBlock.blockID;

				
				
				

				if (renderTmp != 0){
					CorneredSprite s =  PunkBodies.getBlockSprites(tmpBlock.blockID, tmpBlock.meta);;
						s.setBounds(x+xOffset, y+yOffset, 1, 1);
						//tmpBlock.sprite[1].setCorners(tmpBlock.lightBits, tmpBlock.dayBits);
						s.setCornersSimple(tmpBlock.getLight(), tmpBlock.getDayLight());
						s.draw(batch);
						//Gdx.app.log(TAG, "x"+x+" y"+y+" x2 "+(x+xOffset)+" y2 "+(y+yOffset));
				} 
				else {//bg
					byte bg = blockBG[(x<<CB)+y];
					if (bg != 0){
						CorneredSprite s =  PunkBodies.getBlockBGSprites(bg);;

						s.setBounds(x+xOffset, y+yOffset, 1, 1);
						s.setBackgroundCorners(tmpBlock.getLightBits(), tmpBlock.getDayBits());
						s.draw(batch);
					}
					
					
				}
				
				y++;
			}
			y = sy;
			x++;
		}
		
		
		
	}

	public Block block(int x, int y) {
		return block[(x<<Punk.CHUNKBITS)+y];
	}
}

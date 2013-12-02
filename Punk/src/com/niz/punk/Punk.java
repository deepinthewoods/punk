package com.niz.punk;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.IntMap.Entry;
import com.badlogic.gdx.utils.Pool;
import com.niz.punk.PunkMap.BlockDamageType;
import com.niz.punk.PunkMap.DayTime;
import com.niz.punk.planes.PrimeMaterial;


public class Punk extends Punkesque {
	
	public static final float FLASHINTERVAL = .18f;
	public static final float PIXELSIZE = 1f/16f;
	private int mx,  ox, oy;
	private int my;
	private int digLength = 0;
	public static int DIGMAX = 16;
	private Vector2 digV = new Vector2(0,0);
	private int renderTmp;
	private Vector2 tmpV = new Vector2(0,0);
	private Vector2 tmpW = new Vector2(0,0);
	private Vector3 tmpV3 = new Vector3(0,0,0);
	private int viewX = 0, viewY = 0;
	private boolean findDone;
	public static String[] numberStrings = new String[4024];
	private float findRatio = 0;
	private int findIter = 0;
	private float lastLen;
	private String blurb1 = new String("Guns. Touch opposite from where you want to shoot. Guns that use ammo will show the active ammo in green");
	private String blurb2 = new String("Projectiles. Touch and drag to aim");
	private String blurb3 = new String("Blocks. Touch and drag to change placement, release to place.");
	private String blurb4 = new String("Pickaxes. Touch and hold in the direction you want to dig");
	private String blurb5 = new String("You'll figure it out.");
	public static boolean pendingBeltChange = false;
	public StringBuilder[] altS = new StringBuilder[4];
	public String randoms = "qwertyuiopasdfghjklzxcvbnmQWERRTYUIOPASDFGHJKLZXCVBNM";
	String eol = System.getProperty("line.separator");
	//private int[] tmpFloatyInfo = new int[2];
	//private Block tmpBlock;
	//stores itemID
	public static Door bedDoor = new Door();
	//private ArrayList<float[]> farBackgroundVerts = new ArrayList<float[]>(farChunkCount);
	public String[] helpStrings = {

"1/6\nPlace blocks to build things. Place torches to shed light."
,
"2/6\nBreak blocks with your fist or with Flails or Axes."
,
"3/6\nHostile Mobs will try to kill you. They spawn in dark areas and at night. Eat food to restore lost health."
,
"4/6\nThrow missiles or use a weapon to damage mobs."
,
"5/6\nBuild out by dragging onto the run button while placing a block."
,
"6/6\nPress the Back Button for Inventory and Crafting. Recipes appear on the Crafting Screen if you have the right ingredients."
	};
	
	
	//private ArrayList<float[]> nearBackgroundVerts = new ArrayList<float[]>(nearChunkCount);
	
	
	
	
	public static String TAG = "debug";
	public static AdWhirlViewHandler myRequestHandler;
	public Punk(AdWhirlViewHandler handler){
		myRequestHandler = handler;
		bedDoor.set(0, 0, 0, 0, 0, 2, 0, 0);
	}
	//240x320
	//480x320
	//480x800
	//480x854
	//1280x800
	//1024x600
	public void setTooltips(){
		tooltips[0] = new String();
		tooltips[1] = new String("Touch OPPOSITE side,\n hold to aim,\n release to throw.");
		tooltips[2] = new String("Touch and drag to change placement,\n release to place block.");
		tooltips[3] = new String("Touch and hold to dig.");
		tooltips[4] = new String();
		tooltips[5] = new String();
		tooltips[6] = new String("Touch OPPOSITE side, hold to shoot.");
		tooltips[7] = new String("Touch and drag to change placement,\n release to use item");
		tooltips[8] = new String("Touch and drag to change placement,\n release to plant. Must be planted on dirt.");
		tooltips[9] = new String("Touch and hold \n OPPOSITE side to shoot.");
		tooltips[10] = new String("Touch screen to eat.");
		tooltips[11] = new String();
		tooltips[12] = new String();
		tooltips[13] = new String();
		tooltips[14] = new String();
		tooltips[15] = new String();
		//resetTooltips();
		for (int i = 0; i < 4; i++)
			altS[i] = new StringBuilder();
	}
	//public MobInfo mobInfo = new MobInfo();
	
	@Override protected void createPhysicsWorld (World world, boolean isFirstSave) {
		//bulletPool = new BulletPool(5,world,  monsterIndex, tmpV.set(0,0));
		bHandler = new BossHandler(world, monsterIndex);
		gMap = new PunkMap( player, bHandler, world, monsterIndex, camera, isFirstSave);
		camProj.setToOrtho2D(0f,0f,RESX,RESY);
		
		
		listener = new PunkContactListener(player, monsterIndex, world, gMap, this);
		world.setContactListener(listener);
		//Gdx.app.log("punk", "listener done");

		bHandler.clearBosses(gMap, world);
		//gMap.spawnBosses(bHandler, world, monsterIndex);
		myRequestHandler.hide();
		monsterIndex.beltHideButton.setPosition(BELTOFFSETX + BELTBUFFERX+BELTBUFFERX/2,RESY-MAPTILESIZE);
		monsterIndex.beltHideButtonBottom.setPosition(BELTOFFSETX + BELTBUFFERX+BELTBUFFERX/2,RESY-MAPTILESIZE-MAPTILESIZE/2);
		
		/*
		 * if (beltHidden)return 	y < PBELTBUFFERY/2 && 
					x > PBELTOFFSETX-PBELTBUFFERX/2 &&
					x < PBELTOFFSETX+PBELTBUFFERX/2;
				
			return 	y < PBELTBUFFERY+PBELTBUFFERY/2 && 
					y > PBELTBUFFERY &&
					x > PBELTOFFSETX-PBELTBUFFERX/2 &&
					x < PBELTOFFSETX+PBELTBUFFERX/2;
		 */
		//Gdx.app.log("punk", "create physics world done");
		
		//if (player.stats.stats[0] == 0){
		//	openMessageWindow(popupMessages[0]);
		//	player.stats.stats[0] = 1;
		//}
		
		makeTouchHighlightVerts();
		
	}
	
	
	//boolean hasTool, hasTorch;
	
	
	
	
	
	protected void drawItemPicker(){
		
	}
	

	protected void startItemPicker(){
		//gameMode = 19;//
		//gMap.unScrubChunk(gMap.chunkC);
		//player.globalMinute = -1;
		
	}
	
	 String farVShader = "uniform mat4 u_mvpMatrix;                   \n" + "attribute vec4 a_position;                  \n"
             + "void main()                                 \n" + "{                                           \n"
             + "   gl_Position = u_mvpMatrix * a_position;  \n" + "}                \n";
     String farFShader = "uniform mat4 u_mvpMatrix;                   \n" + "attribute vec4 a_position;                  \n"
             + "void main()                                 \n" + "{                                           \n"
             + "   gl_Position = u_mvpMatrix * a_position;  \n" + "}  " ;
     ShaderProgram farShader;
	
	@Override protected void createTextureWorld (World nworld){
		/*if (!Gdx.app.getGraphics().isGL20Available()) {
			throw new GdxRuntimeException("GLES2 Not Available!");
		}*/
		font = new BitmapFont(Gdx.files.internal("data/font"+MAPTILESIZE+".fnt"), Gdx.files.internal("data/font"+MAPTILESIZE+".png"),false);
		setTooltips();
		//backgrounds
		/*farBackgroundVerts.ensureCapacity(farChunkCount);
		for (int i = 0; i < farChunkCount; i++){
			farBackgroundVerts.add(new float[20]);

		}
		//nearBackgroundIndices.ensureCapacity(nearChunkCount);
		for (int i = 0; i < nearChunkCount; i++){
			nearBackgroundVerts.add(new float[36]);

		}*/
		/*farBackM = new Mesh(false,5,5,
				new VertexAttribute(Usage.Position, 3, "a_position"),
				new VertexAttribute(Usage.ColorPacked, 4, "a_color"));
		nearBackM = new Mesh(false,9,11,
				new VertexAttribute(Usage.Position, 3, "a_pos"),
				new VertexAttribute(Usage.ColorPacked, 4, "a_color"));*/
		
	     //farShader = new ShaderProgram(farVShader, farFShader);
		for (int i = 0; i < 4024; i++)
			numberStrings[i] = ""+i;
		
		
		//setPlayerLamp();
	}
	private void setPlayerLamp() {
		tmpV.set(8,8);
		for (int x = 0; x <= 16; x++)
			for (int y = 0; y <= 16; y++)
				lamp[x][y] = (int) Math.max(0, 8-tmpV.dst(x,y));
		
	}
	private float[] tmpFar = new float[5];
	private float[] tmpNear = new float[9];
	
	float[] tmpFVerts = new float[20];
	
	
	
	
	
	
	
	
	public void doBackgroundMesh(){
		
		//vertexCacheN = makeNearMesh(gMap.currentChunk, player.gameInfo.gameSeed, vertexCacheN);
		//vertexCacheF = makeFarMesh(gMap.currentChunk, player.gameInfo.gameSeed, vertexCacheF);
		Chunk.planes.get(PunkMap.currentPlane).miniMap.makeMesh(PunkMap.currentChunk, Player.gameInfo.gameSeed);
		adjustBackgroundLighting(skyColor);
		//adjustBackgroundLighting(skyColor);
	}
	//short[] farInd = new short[]{0,1,2,3,4,5};
	short[] farFanInd = new short[]{4,3,1,0,2};
	
	@Override
	protected void renderFarBackground(){

	
		
	}
	short[] nearInd = new short[]{0,1,2,3,4,5};
	short[] nearFanInd = new short[]{8,7,5,4,6};
	@Override
	protected void renderNearBackground(){
		//miniMap.render(batch, camera, player, gMap, gl);
		/*camera.zoom = player.zoomLevel;
		camera.position.set(player.head.position.x*parallaxNearRatio, player.head.position.y, 0);
		
		camera.update();
		
		camera.apply(gl);
		//find left and right
		int leftX = MathUtils.floor((
				(player.head.position.x)
						));//);
		
		leftX -= gMap.CHUNKOFFSET;
		leftX+= Punk.CHUNKSIZE;

		leftX = (int)(leftX/parallaxNearRatio);
		
		leftX =(leftX/(nearChunkLength));

		int cRadius = (int)(7*player.zoomLevel*parallaxNearRatio);
		
		leftX -= cRadius;
		leftX = Math.max(leftX,  0);
		
		
		//render appropriate mesh
		
		backgroundM.setVertices(vertexCacheN);
		//backgroundM.setIndices(indexCache);
		backgroundM.render(GL10.GL_TRIANGLE_STRIP, leftX*9, cRadius*20+1);//0, nearChunkCount*9-10);// 18*(int)(player.zoomLevel*4));
		//backgroundM.unbind();
			
			/*nearBackM.setIndices(nearInd);
			nearBackM.setVertices(tmpBackVerts);
			nearBackM.render(GL10.GL_TRIANGLE_STRIP);
			nearBackM.setIndices(nearFanInd);
			nearBackM.render(GL10.GL_TRIANGLE_FAN);*/
		
	}
	
protected void renderMiniMap(){
		
	//miniMap.render(batch, camera, player, gMap, gl);
		
	}
	
	Vector3 topR = new Vector3(0,0,0), bottomL = new Vector3(0,0,0);
	float endX;
	int  endY, startY;
    /*protected void renderMaprealoold(World world, SpriteBatch batch, Vector3 origin, Vector3 camPos, PunkBodies monsterIndex, Camera cam)
	{
    	bottomL.set(camPos.x-BRADIUSX, camPos.y-BRADIUSY, 0);
    	startY =MathUtils.floor( bottomL.y);
    	topR.set(camPos.x+BRADIUSX, camPos.y+BRADIUSY, 0);
    	
    	
		//camera.unproject(topR);
	
		//camera.unproject(bottomL);
		endX = MathUtils.floor(topR.x);
		endY = MathUtils.floor(topR.y);
		mx = MathUtils.round(bottomL.x);
		my = MathUtils.round(bottomL.y);
		startY = my;
		//Gdx.app.log("renderMap", "start render:"+mx+","+my);
		batch.disableBlending();
		while (mx < endX){
			my = startY;
			while (my < endY){
				tmpBlock = gMap.getBlock(mx,my);
				renderTmp = tmpBlock.blockID;
				if (renderTmp > 0){
					if (gMap.getLightLevel(mx, my) > 7)
						
					
						//batch.draw(monsterIndex.terrainTex[renderTmp], mx, my);
						////Gdx.app.log("rendermap", "mx:"+mx+"my:"+my);
					batch.draw(monsterIndex.terrainTex[renderTmp], 
							  mx - .5f, my);//, // the bottom left corner of the box, unrotated
							//  .5f, .5f, // the rotation center relative to the bottom left corner of the box
							//  1, 1, // the width and height of the box
							//  1, 1, // the scale on the x- and y-axis
						//	  0);
				} 
				//air block
				else if (gMap.getLightLevel(mx, my) > 0)
					 	batch.draw(monsterIndex.getAirFrame(gMap.getLightLevel(mx, my)),mx, my);
						
				
						
				my += 1;	
				}
			mx += 1;
			}
		//Gdx.app.log("renderMap", "end locs:"+mx+","+my);
		batch.enableBlending();
	}
	protected void renderMapscreen(World world, SpriteBatch batch, Vector3 origin, Vector3 camPos, PunkBodies monsterIndex, Camera cam)
	{
		mx = MathUtils.floor(camPos.x)-BRADIUSX;
		my = MathUtils.floor(camPos.y)-BRADIUSY;
		//mx -=1;
		//if (mx < 0) mx +=2;
		////Gdx.app.log("renderMap", "mx:"+mx);
		
		float px = origin.x;//x-coord for start ptpx
		float py = origin.y;
				
		int firstBY = my;//bottom block
		/*if (firstBY < 0) 
			{
				py = py -(firstBY*TILESIZE);
				firstBY = 0;
			}*/
		
		/*int lastBY ;//= my + BREPEATSY;
		
		float pStartY = py;//y-coord for resetting each line px
		
		lastBY = 0;
		//if (lastBY > CHUNKSIZE) lastBY = CHUNKSIZE;
		
		//batch.disableBlending();
		while (px<RESX)
		{
			my = firstBY;
			py = pStartY;
				while (my<lastBY)
				{
				//System.out.println("row:x" + px + "y" + py);
				//System.out.println("my"+my+"py"+py);
					tmpBlock = gMap.getBlock(mx,my);
					renderTmp = tmpBlock.blockID;
					if (renderTmp > 0){
						if (gMap.getLightLevel(mx, my) > 0)
							//batch.setColor(.2f,.2f,.2f,.2f);
							//batch.draw(monsterIndex.terrainTex[renderTmp], px, py);
							monsterIndex.terrainS[renderTmp][gMap.getLightLevel(mx, my)].setPosition(px, py);
							monsterIndex.terrainS[renderTmp][gMap.getLightLevel(mx, my)].draw(batch);

					} 
					//air block
					else if (renderTmp < 0 && gMap.getLightLevel(mx, my) > 0){
						switch (renderTmp){
						case 0: 	batch.draw(monsterIndex.getAirFrame(gMap.getLightLevel(mx, my)),px, py);
							break;
						case -1:monsterIndex.terrainS[218+Math.abs(tmpBlock.meta)][gMap.getLightLevel(mx, my)].setPosition(px, py);
								monsterIndex.terrainS[218+Math.abs(tmpBlock.meta)][gMap.getLightLevel(mx, my)].draw(batch);
							break;
						case -2:
						case -3:
							batch.draw(monsterIndex.getSpecialBlockTex(renderTmp, tmpBlock.meta, gMap.getLightLevel(mx, my)), px, py);
							break;
						case -4:
						case -5:
						case -6:
						case -7:
						case -8:
							monsterIndex.terrainS[1][gMap.getLightLevel(mx, my)].setPosition(px, py);
							monsterIndex.terrainS[1][gMap.getLightLevel(mx, my)].draw(batch);
								////Gdx.app.log("renderer", "water, "+renderTmp+","+tmpBlock.meta);
							break;
						
						}
					}
					py += TILESIZE;
					my+=1;
				}
			px = Math.round(px + TILESIZE-.01f);
			mx+=1;
		}
		//batch.enableBlending();
	}*/
	//float py, px;
	protected void renderMap(World world, SpriteBatch batch, Vector3 origin, Vector3 endPos, PunkBodies monsterIndex, Camera cam){
		//if (PunkMap.timeOfDay == DayTime.DAY || PunkMap.timeOfDay == DayTime.SUNRISE) 
		
			renderMapDay(world, batch, origin, endPos, monsterIndex, cam);
		//else renderMapNight(world, batch, origin, endPos, monsterIndex, cam);
	}
	public int[][] lamp = new int[][]{
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,1,2,2,1,0,0,0,0,0,0,0},
			{0,0,0,0,0,1,2,3,3,2,1,0,0,0,0,0,0},
			{0,0,0,0,0,1,3,5,5,3,1,0,0,0,0,0,0},
			{0,0,0,0,0,1,2,3,3,2,1,0,0,0,0,0,0},
			{0,0,0,0,0,0,1,2,2,1,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}
	};
	public static final int PORTAL_MASK_SIZE = 150;
	private static final Color SEMIWHITE = new Color(1f,1f,1f,.5f);
	
	private boolean[][] pMask = new boolean[PORTAL_MASK_SIZE][PORTAL_MASK_SIZE], pFillMask = new boolean[PORTAL_MASK_SIZE][PORTAL_MASK_SIZE];
	private boolean portalsOn;
	
	protected void setPortalMask(int x0, int y0, int x1, int y1){
		portalsOn = false;
		int w = x1-x0, h = y1-y0;
		w = MathUtils.clamp(w, 0, PORTAL_MASK_SIZE-1);
		h = MathUtils.clamp(h, 0, PORTAL_MASK_SIZE-1);
		for (int x = 0; x < w+1; x++)
			for (int y = 0; y < h+1; y++){
				pMask[x][y] = true;
			}
		
		//iterate through portals
		Iterator<Portal> it = portalList.iterator();
		while (it.hasNext()){
			Portal port = it.next();
			//Gdx.app.log(TAG, "x1:"+x1+" y1:"+y1 +  "x0:"+x0+" y0:"+y0 +" pos"+port.position+port.isVisible(x0, y0, x1, y1));

			if (port.isVisible(x0, y0, x1, y1, player.x, player.y)){
				Gdx.app.log(TAG, "vis");
				portalsOn = true;
				for (int x = 0; x < w+1; x++)
					for (int y = 0; y < h+1; y++){
						pFillMask[x][y] = true;
					}
				int x00 = player.x - x0;
				int y00 = player.y - y0;
				int x11 = port.position.x-x0;
				int y11 = port.position.y-y0;
				
				int dx0 = 0, dy0=00, dx1=0, dy1=0;//0 is left
				switch (port.type){
				case 0:
					dx0 = -1;
					dx1 = 1;
					break;
				
				case 1:
					dx0 = 0;
					dy0 = -1;
					dx1 = 0;
					dy1 = 1;
					break;
				
				case 2:
					dx0 = 1;
					dy0 = 0;
					dx1 = -1;
					dy1 = 0;
					break;
				
				case 3:
					dx0 = 1;
					dy0 = 0;
					dx1 = -1;
					dy1 = 0;
					break;
				
				
				}
				queuePortalBlock(x11, y11, port, x0, y0);
				BlockLoc endPt = makePortalMaskLine(x00, y00, x11+dx0, y11+dy0, w, h, port, x0, y0);
				int endX0 = endPt.x, endY0 = endPt.y;
				endPt = makePortalMaskLine(x00, y00, x11+dx1, y11+dy1, w, h, port, x0, y0);
				int endX1 = endPt.x, endY1 = endPt.y;
				//fill
				//Gdx.app.log(TAG, "x1:"+x1+" y1:"+y1 + " x11:"+x11+" y11:"+y11  +  "x0:"+x0+" y0:"+y0 + " x00:"+x00+" y00:"+y00);
				fillPortalQueue((endX1+endX0)/2+1, (endY1+endY0)/2+1, port, w, h, x0, y0);
				activePortals.add(port);
				/*for (int i = 0; i < w; i++){
					String str = "";
					for (int j = 0; j < h; j++)
						//if (i == )
							str += pFillMask[i][j]?"O":" ";
						//else 
					Gdx.app.log(TAG, str);
				}
				Gdx.app.log(TAG, "");*/
			}
			
			
		}
		//check for portals on screen
		
			//on portal
			
			//side lines
			
		
			//fill(stay in bounds!)  midway from 2 end pts
	}
	
	private PunkBlockList q = new PunkBlockList();
	private BlockLoc e = new BlockLoc(), w = new BlockLoc();
	private void fillPortalQueue(int x, int y, Portal port, int width, int height,
			int xo, int yo) {
		boolean target = true;
		boolean repl = false;
//		1. Set Q to the empty queue.
		q.clear();
//		 2. If the color of node is not equal to target-color, return.
		if (pFillMask[x][y] != target) return;
//		 3. Add node to Q.
		q.addBlock(x,y);
//		 4. For each element n of Q:
		while (!q.list.isEmpty()){
			BlockLoc n = q.list.poll();
			
//		 5.     If the color of n is equal to target-color:
			if (pFillMask[n.x][n.y] == target){
//		 6.					Set w and e equal to n.
				w.set(n);
				e.set(n);
				while (w.x >= 0 && pFillMask[w.x][w.y] == target) w.set(w.x-1, w.y);
				while (w.x <= width && pFillMask[e.x][e.y] == target) e.set(e.x+1, e.y);
//				 7.         Move w to the west until the color of the node to the west of w no longer matches target-color.
//				 8.         Move e to the east until the color of the node to the east of e no longer matches target-color.
//				 9.         Set the color of nodes between w and e to replacement-color.
				for (int i = w.x+1; i < e.x; i++) {
					//set color
					queuePortalBlock(i, w.y, port, xo, yo);
				}
				
				if (w.y < height) for (int i = w.x+1; i < e.x; i++) {
					//queue north-south
					if (pFillMask[i][w.y+1] == target)q.addBlock(i, w.y+1);
				}
				
				if (w.y >0) for (int i = w.x+1; i < e.x; i++) {
					if (pFillMask[i][w.y-1] == target)q.addBlock(i, w.y-1);
					
				}
			}
		}
		

		
		
	}
	private void queuePortalBlock(int x, int y, Portal portal, int xOff, int yOff){
		//main mask, subMask
		pMask[x][y] = false;
		pFillMask[x][y] = false;
		
		//queue in portal
		portal.addBlock(x,y, xOff, yOff);
	}
	public BlockLoc tmpLocForPortal = new BlockLoc();
	private BlockLoc makePortalMaskLine(int x0, int y0, int x1, int y1, int width, int height, Portal portal, int xOff, int yOff){
		
		int dx = Math.abs(x1-x0), sx = x0<x1 ? 1 : -1;
		int dy = Math.abs(y1-y0), sy = y0<y1 ? 1 : -1; 
		int err = (dx>dy ? dx : -dy)/2, e2;
		boolean valid = false;
		if (x0 == x1 && y0 == y1) valid = true;
		if (valid) queuePortalBlock(x0, y0, portal, xOff, yOff);
		int count = 0;
		if (dx == 0 && dy == 0) {
			tmpLocForPortal.set(x0, y0);
			
		} else
		for(;;){
			
			if (x0 > width || x0 <0 || y0 > height || y0 <0) {
				tmpLocForPortal.set(x0, y0);
				break;
			}
			if (x0 == x1 && y0 == y1) valid = true;
			if (valid) {
				//Gdx.app.log(TAG, "q:"+x0+","+y0+"  w"+width+"  h"+height + "  dx"+dx+"  dy"+dy);
				queuePortalBlock(x0, y0, portal, xOff, yOff);
			}

			e2 = err;
			if (e2 >-dx) { //extend
				err -= dy; 
				x0 += sx; 
				count++;
				
			}
			if (e2 < dy) { //extend
				err += dx; 
				y0 += sy; 
				count++;
				//if (x0 == x1 && y0 == y1) valid = true;
			}
		}
		
		
		return tmpLocForPortal;
	}
	//private BlockLoc mapRenderBottomLeft
	public Array<Portal> activePortals = new Array<Portal>();
	protected void renderMapDaySmooth(World world, SpriteBatch batch, Vector3 origin, Vector3 endPos, PunkBodies monsterIndex, Camera cam)
	{
		mx = MathUtils.floor(origin.x);
		int startx = mx;
		//px = mx;
		my = MathUtils.floor(origin.y);
		int lastMX = MathUtils.floor(endPos.x)+1;
		int lastMY = MathUtils.floor(endPos.y)-1;
		int firstMY = my;
		//Gdx.app.log("renderer", "start:"+mx+"y:"+my+"lastx:"+lastMX+"lasty:"+lastMY);
		//batch.disableBlending();
		;
		setPortalMask(mx, lastMY, lastMX, my);
		if (portalsOn){
	
				int ix = 0, iy = 0;
				while (mx<lastMX)
				{
					my = firstMY;
					iy = 0;
						while (my>lastMY)
						{
							if (pMask[MathUtils.clamp(mx - startx, 0, PORTAL_MASK_SIZE)][MathUtils.clamp(my - lastMY, 0, PORTAL_MASK_SIZE)]){
								tmpBlock = gMap.getBlock(mx,my); 
								renderTmp = tmpBlock.blockID;
								
	
								if (renderTmp != 0){
									CorneredSprite s =  PunkBodies.getBlockSprites(tmpBlock.blockID, tmpBlock.meta);;
									/*if (s.hasTransparency){
										BlockBG tmpBB = gMap.getBackgroundBlock(mx, my);
										CorneredSprite bs =  PunkBodies.getBlockSprites(tmpBB.blockID, tmpBB.meta);;

										bs.setBounds(mx, my, 1, 1);
										bs.setCorners(tmpBlock.lightBits, tmpBlock.dayBits);
										bs.draw(batch);
									}*/
										s.setBounds(mx, my, 1, 1);
										s.setCorners(tmpBlock.getLightBits(), tmpBlock.getDayBits());
										s.draw(batch);
								} else {//bg
									byte bg = gMap.getBackgroundBlock(mx, my);
									if (bg != 0){
										CorneredSprite s =  PunkBodies.getBlockBGSprites(bg);;
								
										s.setBounds(mx, my, 1, 1);
										s.setCorners(tmpBlock.getLightBits(), tmpBlock.getDayBits());
										s.draw(batch);
									}
								}
							}
							
							my-=1;
							iy+=1;
							
						}
					mx+=1;
					ix += 1;
				}
			
			//draw portals
			while (activePortals.size > 0){
				Portal port = activePortals.pop();
				port.draw(camera, batch, gMap, gl, player);
			}
			
		} else {
		
			int ix = 0, iy = 0;
			while (mx<lastMX)
			{
				my = firstMY;
				iy = 0;
				//py = my;
					while (my>lastMY)
					{
						tmpBlock = gMap.getBlock(mx,my); 
						renderTmp = tmpBlock.blockID;

						
						int lampX = MathUtils.clamp(player.x - mx+8, 0, 16);
						int lampY = MathUtils.clamp(player.y - my+8, 0, 16);
						

						if (renderTmp != 0){
							CorneredSprite s =  PunkBodies.getBlockSprites(tmpBlock.blockID, tmpBlock.meta);;
								s.setBounds(mx, my, 1, 1);
								//tmpBlock.sprite[1].setCorners(tmpBlock.lightBits, tmpBlock.dayBits);
								s.setCorners(tmpBlock.getLightBits(), tmpBlock.getDayBits());
								s.draw(batch);
						} 
						else {//bg
							byte bg = gMap.getBackgroundBlock(mx, my);
							if (bg != 0){
								CorneredSprite s =  PunkBodies.getBlockBGSprites(bg);;
								//Gdx.app.log(TAG, "bg "+bg.blockID+","+bg.meta);
								s.setBounds(mx, my, 1, 1);
								s.setBackgroundCorners(tmpBlock.getLightBits(), tmpBlock.getDayBits());
								s.draw(batch);
							}
							
							
						}
						
						my-=1;
						//lampY--;
						iy+=1;
						
					}
				mx+=1;
				ix += 1;
				//lampX++;
			
			}
		}
		gMap.blockMoverPool.draw(batch, monsterIndex);
		//batch.enableBlending();
	}
	
	Array<Chunk> pixChunks = new Array<Chunk>(true, 16);
	protected void renderMapDay(World world, SpriteBatch batch, Vector3 origin, Vector3 endPos, PunkBodies monsterIndex, Camera cam)
	{
		mx = MathUtils.floor(origin.x);
		int startx = mx;
		//px = mx;
		my = MathUtils.floor(origin.y);
		int lastMX = MathUtils.floor(endPos.x)+1;
		int lastMY = MathUtils.floor(endPos.y)-1;
		int firstMY = my;
		
		int width = lastMX - mx;
		//Gdx.app.log(TAG, "width "+width);
		/*if (width > 50){//
			//get relevant chunks
			pixChunks.clear();
			Chunk c0 = gMap.chunkPool.getChunkWorld(mx, my);
			Chunk c1 = gMap.chunkPool.getChunkWorld(lastMX, my);
			Chunk c2 = gMap.chunkPool.getChunkWorld(mx, lastMY);
			Chunk c3 = gMap.chunkPool.getChunkWorld(lastMX, lastMY);
			c0.draw(batch);
			if (!c1.equals(c2) && !c1.equals(c3)) c1.draw(batch);
			if (!c2.equals(c1) && !c2.equals(c3)) c2.draw(batch);
			if (!c3.equals(c2) && !c3.equals(c1)) c3.draw(batch);
			
			c0.updatePixmap();
			c1.updatePixmap();
			c2.updatePixmap();
			c3.updatePixmap();
			return;
		}*/
		
		//Gdx.app.log("renderer", "start:"+mx+"y:"+my+"lastx:"+lastMX+"lasty:"+lastMY);
		//batch.disableBlending();
		;
		setPortalMask(mx, lastMY, lastMX, my);
//		if (portalsOn){
//			
//		} else {
//		
//			while (mx<lastMX)
//			{
//				my = firstMY;
//				iy = 0;
//				//py = my;
//					while (my>lastMY)
//					{
//						tmpBlock = gMap.getBlock(mx,my); 
//						renderTmp = tmpBlock.blockID;
//
//						
//						
//						
//
//						if (renderTmp != 0){
//							CorneredSprite s =  PunkBodies.getBlockSprites(tmpBlock.blockID, tmpBlock.meta);;
//								s.setBounds(mx, my, 1, 1);
//								//tmpBlock.sprite[1].setCorners(tmpBlock.lightBits, tmpBlock.dayBits);
//								s.setCornersSimple(tmpBlock.getLight(), tmpBlock.getDayLight());
//								s.draw(batch);
//						} 
//						else {//bg
//							byte bg = gMap.getBackgroundBlock(mx, my);
//							if (bg != 0){
//								CorneredSprite s =  PunkBodies.getBlockBGSprites(bg);;
//
//								s.setBounds(mx, my, 1, 1);
//								s.setBackgroundCorners(tmpBlock.getLightBits(), tmpBlock.getDayBits());
//								s.draw(batch);
//							}
//							
//							
//						}
//						
//						my-=1;
//						//lampY--;
//						iy+=1;
//						
//					}
//				mx+=1;
//				ix += 1;
//				//lampX++;
//			
//			}
//		}
		
		if (mx > lastMX){
			int t = mx;
			mx = lastMX;
			lastMX = t;
		}
		if (my > lastMY){
			int t = my;
			my = lastMY;
			lastMY = t;
		}
		/*Chunk c0 = gMap.chunkPool.getChunkWorldRender(mx, my);
		Chunk c1 = gMap.chunkPool.getChunkWorldRender(lastMX, my);
		Chunk c2 = gMap.chunkPool.getChunkWorldRender(mx, lastMY);
		Chunk c3 = gMap.chunkPool.getChunkWorldRender(lastMX, lastMY);*/
		
		int x1 = mx>>CHUNKBITS, x2 = lastMX>>CHUNKBITS, y1 = my>>CHUNKBITS, y2 =lastMY>>CHUNKBITS;
		for (int i = x1; i<=x2; i++)
			for (int j = y1; j <= y2; j++){
				Chunk c = gMap.chunkPool.getChunk(i, j);
				if (c != null)
					c.draw(batch, mx, my, lastMX, lastMY );
			}
		
		gMap.blockMoverPool.draw(batch, monsterIndex);
		//batch.enableBlending();
	}
	
	
	private int darken(int t) {
		int d = (t& 0xF - 6) + ((t>>4& 0xF - 6)<<4) + ((t>>8 & 0xF - 6)<<8) + ((t>>12 & 0xF - 6)<<12);
		t -= 2;
		
		return d;
		
	}
	protected void renderMapNight(World world, SpriteBatch batch, Vector3 origin, Vector3 endPos, PunkBodies monsterIndex, Camera cam)
	{
		mx = MathUtils.floor(origin.x);
		//px = mx;
		my = MathUtils.floor(origin.y);
		long lastMX = MathUtils.floor(endPos.x)+1;
		int lastMY = MathUtils.floor(endPos.y)-1;
		int firstBY = my;
		//Gdx.app.log("renderer", "start:"+mx+"y:"+my+"lastx:"+lastMX+"lasty:"+lastMY);
		//batch.disableBlending();
		while (mx<lastMX)
		{
			my = firstBY;
			//py = my;
				while (my>lastMY)
				{
					tmpBlock = gMap.getBlock(mx,my); 
					renderTmp = tmpBlock.blockID;
					//int light = Math.max(tmpBlock.dayLight-10,tmpBlock.light);
					if (renderTmp != 0){
						CorneredSprite s =  PunkBodies.getBlockSprites(tmpBlock.blockID, tmpBlock.meta);;

							s.setBounds(mx, my, 1, 1);
							s.setCorners(tmpBlock.getLightBits());
							s.draw(batch);
					} 
					
					my-=1;
					
				}
			mx+=1;
		
		}
		//batch.enableBlending();
	}
	
@Override protected void renderTextures(World world, SpriteBatch batch, Vector3 offsetVector) {
		//player.update(Gdx.graphics.getDeltaTime());
		
		//draw player
		//batch.getProjectionMatrix().setToOrtho2D(0, 0, 25, 15);
		
	}


private Block tmpBlock = new Block(0,0);
private Block tmpLookBlock = new Block(0,0);




protected boolean damageBlock(World world, PunkMap map, Vector2 dig,int damage, ItemPool itemPool){
	map.damageBlock(BlockDamageType.FLAIL, dig, damage);//only used for digging?
	//Gdx.app.log("punk", "damaged Block "+player.position);
	blockHP -= damage;
	//System.out.println(" block damaged, hp now:" + blockHP);
	if (blockHP > 0)return true; else {
		//player.x = 0;//so the bbs update after destroying a block
		return false;
	}
}




/*public boolean checkBucketBlock(int x, int y){
	if (gMap.getBlock(x,y).blockType()>1) return true;
		lastDigTargetBlock.set(x,y);
		return false;
	
	//digging
	//if (gMap.getBlock(x,y).blockType()>1) return true;
	//lastDigTargetBlock.set(x,y);
	//retu*rn false;
}*/
/*
Vector2 tmpBV = new Vector2();
protected Vector2 findDigTargetbres(World world, PunkMap map, Vector2 target, boolean blockFlag){
	int x0 = player.x;
	int y0 = player.y+1;
	//tmpV.set(direction).mul(player.DIGRANGE);//.mul(-1);
	//tmpV.add(player.position);
	int x1 = MathUtils.floor(target.x);;
	int y1 = MathUtils.floor(target.y);
		 
	int dx = Math.abs(x1-x0), sx = x0<x1 ? 1 : -1;
	int dy = Math.abs(y1-y0), sy = y0<y1 ? 1 : -1; 
	int err = (dx>dy ? dx : -dy)/2, e2;
	
	//Gdx.app.log("punk", "touch angle:"+touchLoc.angle());
	
	checkBlock(x0,y0, blockFlag);
	int count = 0;
	for(;;){
		
		if (count > player.DIGRANGE) break;
		e2 = err;
		if (e2 >-dx) { //extend
			err -= dy; 
			x0 += sx; 
			count++;
			if (checkBlock(x0,y0, blockFlag)){
				//Gdx.app.log("punk", "line, add sx");
				return (blockFlag?tmpBV.set(lastDigTargetBlock.x, lastDigTargetBlock.y):tmpBV.set(x0,y0+sy));}
		}
		if (e2 < dy) { //extend
			err += dx; 
			y0 += sy; 
			count++;
			if (checkBlock(x0,y0, blockFlag)){
				//Gdx.app.log("punk", "line, add sy");

				return (blockFlag?tmpBV.set(lastDigTargetBlock.x, lastDigTargetBlock.y):tmpBV.set(x0+sx,y0));}
		}
	}
		
	
	
	return tmpBV.set(-1,-1);
}

*/

public enum RayType{SOLID};






Vector2 baseBlock = new Vector2(0,0);
protected int lineLength(int x2, int y2){
	int length = 0;
		int x=0;
		int y=0;
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
	        length+=1;
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
	return length;
}
private Vector2 tmpV2 = new Vector2();
@Override
protected Vector2 findDigTarget(){
	return gMap.rayCastForDig(tmpV.set(player.position).add(0,Player.EYEHEIGHT), (adjustedTouchAngle+180)%360, RayType.SOLID, 36, 2);
			
}
@Override
protected Vector2 findBlockTarget(World world, PunkMap map, Vector2 touch, int range){
	return gMap.rayCastForPlace(tmpV2.set(player.position).add(0,Player.EYEHEIGHT), (adjustedTouchAngle+0)%360, RayType.SOLID, 48, 2);
	//return findDigTarget(world, map, touch, true, range);
}


@Override
protected void updateControls(){
	//JUMPBUTTONX1 = (int)player.screenPosition.x-JUMPBUTTONSIZE;
	//JUMPBUTTONX2 = (int)player.screenPosition.x+JUMPBUTTONSIZE;
	//JUMPBUTTONY1 = RESY-(int)player.screenPosition.y-JUMPBUTTONSIZE;
	//JUMPBUTTONY2 = RESY-(int)player.screenPosition.y+JUMPBUTTONSIZE;

	
}
int drawI;

private String[] systemS = {"","","Save Game","Save&Quit", "", ""};
private String difficultyS;

static float invButtonSize;// = Math.min((PRESY - h - adBuffer - modesH)/5f, (PRESX*.666666666f)/5f);		
public int fingerHighlight = -1;
public void drawBelt(){
	float x = (RESX / BELTSLOTCOUNT) * beltXa, y = getBeltHeight(), w = RESX/BELTSLOTCOUNT, h = RESY * beltYa;
	for (int i = 0; i < BELTSLOTCOUNT; i++){
		
		
		Item item = player.controllingMob.inv.getItem(i);
		float c = RESX/2;
		float x2 = c+(-3+i)*x,  h2 = Math.min(w, h), y2 = RESY-h2;
		if (i == player.controllingMob.activeInvSlot)monsterIndex.beltSelected9.draw(batch, x2,y2,h2,h2);//Gdx.app.log(TAG, "belt"+x2+y2+h2);
				if (item != null){
					CorneredSprite s = PunkBodies.getItemFrame(item.id, item.meta);
					
					
					batch.draw(s, x2, y2,h2, h2);
				}
		
	}
	
}
String strLoading = "loading....", strKills = "Kills: ", strSaved = "s";

@Override
protected void drawUI(){
	camera.zoom = 1f;
	camera.update();
	//int beltY = beltOnTop?RESY-BELTBUFFERY:0;
	font.setColor(1,1,1,.7f);
	//if (gameMode < 16 || gameMode >= 80){
		//beltGroup.draw(batch, 1);
		//TODO
		//Gdx.app.log(TAG, "belt");
		
		//drawBelt();
	//}
	font.setColor(1,1,1,1f);
	
	if (drawFinger){
		drawFingerHightlightFilled(screenSpaceTouch.x, screenSpaceTouch.y, 1f);
		drawFinger = false;
	}
	
	
	
	if (debugTextOn)font.drawMultiLine(batch, 
	//		Gdx.app.log(TAG, 
		"x:"+player.x + "y:"+player.y + "  pos:"+player.controllingMob.position + "  j"+player.controllingMob.angle
		//+ " p:"+PunkMap.currentPlane + " l:"+gMap.getBlock(player.x, player.y)
		+"\n fr"+player.controllingMob.frame + " ang:"+player.controllingMob.angle + " stateTime:"+player.controllingMob.stateTime
		+"\nfall:"+player.controllingMob.blocksFallen
		+"c:"+PunkMap.currentChunk+","+PunkMap.currentChunkHeightID
		+" block"+player.blockC + " tot l"+gMap.chunkPool.lightTot+",u"+gMap.chunkPool.updatesTot+",f"+gMap.chunkPool.fetchTot+","
		//+(player.isSwimmingUp?" swUp":"")
		//+ (player.isHoldingBreath?"holdBr":"")
		//+ (player.isSwimming?"sw":"")
		+ "\n state: "+ (player.state) + "  "+ player.gravityState + "  "+player.frictionState + "  |  "+player.controllingMob.stopped + "  |||"+player.controllingMob.body.getLinearVelocity()
		+"   "+player.controllingMob.hasHitGround + "   "+player.jumpStamina + "   grav"+player.body.getGravityScale()
		+ "\n o:"+player.originOffset +" l:"+player.handLOffset + " r:"+player.handROffset 
		//+"\nwall:"+player.isWallSliding
		//+"\nsmooth:"+Chunk.getSmoothness(player.x)
		//+"  height:"+Chunk.getGroundHeight(player.x)
		//+"chunk "+gMap.currentChunk+","+gMap.currentChunkHeightID
	//	+(PunkMap.allChunksLoaded?"pfet":"")
		//+" gh:"+Chunk.getGroundHeight(player.x)
		//+"\nmin: "+player.globalMinute + " s "+player.globalTime + " \ninfo min "+ player.gameInfo.minutes
		//+"\nfall"+player.blocksFallen
		//+ touchBlockV
		//+"mode"+gameMode
		//+"iID"+player.getActiveID()
		+(player.isAimingPoi?"aim":"")
		//+(player.isGliding?"gli":"")
		//+(player.isThrusting?"thr":"")
		//+(player.isWallSliding?"slide":"")
		//+"h:"+player.health
		//+"spawn: "+player.gameInfo.spawnPosition.x + ", "+player.gameInfo.spawnPosition.y+" "
		//+ "p:"+player.poisonDamage 
		//+ "h:"+player.health
		//+ " f:"+player.isOnFire
		//+" s:"+player.isSwimming + "j:"+(player.isJumping?1:0) + "b:"+player.isHoldingBreath
		//+" l:"+player.light
		//+"time:"+player.globalTime 
		//+"act:"+getActionButton()
		//+"c:"+player.isFallingThroughBridge	
		//+"x:"+player.x%256
		//+"aim:"+player.aimStrength
		//+"state:"+player.state
		//+(player.isGrappling?"grap":"")
			//+ (player.isAiming?"aim":"")
			//+(player.viewingSignID!= -1?"sign":"")
			//+(player.isClimbing?"climb":"")
			//+(player.isFalling?"fall":"")
			//+(climbDownPressed?"clD":"")
			//+(climbUpPressed?"clUp":"")
			
			//+(!gMap.allTreesGrown?"TreesW":"")
			//+(!gMap.allChunksPostFetched?"PFw":"")
			//+(gMap.allChunksLoaded?"loaded":"")
			//+"z:"+player.zoomLevel + "\n"
			
			//+"/"+gMap.threadRuns
			//+"\nZombies:"+gMap.chunkActors.zombiePool.monsterList.size
		//	+"heightID:"+gMap.currentChunkHeightID
		//	+"chunkHeight:"+gMap.chunkC.heightID
		//+"gm:"+gameMode
		, 50, 80);//*/
	gMap.threadRuns = 0;
	//font.draw(batch, numberStrings, x, y)
	int intFPS = (int)fps;
	if (intFPS < 0 || intFPS > 1024) intFPS = 0;
	font.draw(batch, numberStrings[intFPS], Punk.MAPTILESIZE*2, Punk.MAPTILESIZE);
	//font.draw(batch, numberStrings[PunkMap.updateState], Punk.MAPTILESIZE*3, Punk.MAPTILESIZE);
	//font.draw(batch, numberStrings[Start.mi.getMB()], Punk.MAPTILESIZE*4, Punk.MAPTILESIZE);
	/*for (int i = 0; i < 0; i++){
		if (gMap.updateTimes[i]/1000000 < 4000)font.draw(batch, numberStrings[(int) (gMap.updateTimes[i]/1000000)], (MAPTILESIZE*2*i), Punk.MAPTILESIZE*2);
		else font.draw(batch, numberStrings[990+i], (MAPTILESIZE*2*i), Punk.MAPTILESIZE*2);
	}*/
	if (gMap.chunkPool.allSaved)font.draw(batch, strSaved, 50, 50);
	 font.draw(batch, numberStrings[availableRAM], 50, 30);
//	font.draw(batch, 
//			""+gMap.timeOfDay+" "+player.globalTime, 50, TILESIZE);
	if (gTime < queuedTip.timer ){//&& queuedTip.s != null){
		font.drawMultiLine(batch, 
				queuedTip.s, 
				0, (RESY/8)*6+TILESIZE2, RESX, HAlignment.CENTER);
		////Gdx.app.log("punk", "drawing tooltip"+queuedTip);
	} else if (gTime < queuedTip.timer + 3000 && (gameMode < 16 || gameMode >= 80)){
		font.drawMultiLine(batch, 
				altS[(int)(gTime %2000)/500], 
				0, (RESY/8)*6+TILESIZE2, RESX, HAlignment.CENTER);
	}
	//if (!gMap.allChunksLoaded) monsterIndex.loadingBtn.draw(batch);
	//if (gMap.updater.lightUpdateList.list.size > 0 || gMap.updater.dayLightUpdateList.list.size > 0)
	//	monsterIndex.lightBtn.draw(batch);
		//font.draw(batch, strLoading, 32, 76);
	/*if (!PunkMap.openWorld){
		font.draw(batch, strKills, RESX-64, RESY-48); 
		font.draw(batch, numberStrings[player.stats_kills], RESX-64, RESY-64);
	}*/
	/*if (player.viewingSignID != -1){
		font.drawMultiLine(batch, gMap.chunkPool.getChunkWorld(player.x, player.y).signs.get(player.viewingSignID), 0, RESY-Punk.TILESIZE*2, RESX, HAlignment.CENTER);

	}*/
	//batch.setColor(1f,1,1,.2f);
	float b = .77f;
	if (prefs_buttons.value == 1 && gameMode != 16 && gameMode != 17){
		//batch.draw(monsterIndex.arrowSprite, 0, 0);
		//batch.draw(monsterIndex.arrowSpriteR, RESX-TILESIZE*2, 0);	
		if (player.draggedL) monsterIndex.arrowSprite.setColor(.3f,1, .3f, 1);
		else monsterIndex.arrowSprite.setColor(1f,1, 1f, 1);
			
		if (player.draggedR) monsterIndex.arrowSpriteR.setColor(.3f,1, .3f, 1);
			else monsterIndex.arrowSpriteR.setColor(1f,1, 1f, 1);
		monsterIndex.arrowSprite.draw(batch, b);
		
		monsterIndex.arrowSpriteR.draw(batch, b);
		
		monsterIndex.jumpSprite.setPosition(RESX-32, 64);
		monsterIndex.jumpSprite.draw(batch,b);//(RESX-32, 64);
		
		monsterIndex.jumpSprite.setPosition(0, 64);//, 32, 64);
		monsterIndex.jumpSprite.draw(batch,b);
		
		batch.setColor(1f,1,1,1f);
	}
	
	
	
	//button2's
	if (player.climbButtonUpValid){
		monsterIndex.climbUpSprite.draw(batch,b);
	}

	{	
		

	}

	if (player.climbButtonDownValid){
		monsterIndex.climbDownSprite.draw(batch,b);
	} 
	
	{
		
	}
	
	drawActionButton();
	drawItemUI();
	
	
	
	//monsterIndex.invBtn.draw(batch);
	b = .9f;
	int healthProgress = 0;
	Sprite healthS = monsterIndex.healthSprite;
	Sprite fullHealthS = monsterIndex.fullHealthSprite;
	healthS.setColor(1,0,0,b);
	fullHealthS.setColor(1,0,0,b);
	//health
	/*while (healthProgress < player.health && healthProgress < player.maxHealth()){
		int yOffset = RESY-Punk.MAPTILESIZE-Punk.MAPTILESIZE-Punk.MAPTILESIZE/2-4;//start
		yOffset -= (healthProgress)* (Punk.MAPTILESIZE/4) ;
		healthS.setPosition (0,yOffset);
		healthS.setScale(1,1);
		
		healthProgress++;
		//healthS.setScale(-1,1);
		fullHealthS.setPosition(0,yOffset);
		if (healthProgress < player.health)
			fullHealthS.draw(batch);
		else healthS.draw(batch);
		healthProgress++;
	}
	//poison
	healthS.setColor(0,1,0,b);
	fullHealthS.setColor(0,1,0,b);
	healthProgress = 0;
	while (healthProgress < player.poisonDamage){
		int yOffset = RESY-Punk.MAPTILESIZE-Punk.MAPTILESIZE-Punk.MAPTILESIZE/2-4;//start
		yOffset -= (healthProgress)*( Punk.MAPTILESIZE/4) ;
		healthS.setPosition (Punk.MAPTILESIZE/2,yOffset);
		healthS.setScale(1,1);
		//healthS.draw(batch);
		healthProgress++;
		//healthS.setScale(-1,1);
		fullHealthS.setPosition(Punk.MAPTILESIZE/2,yOffset);
		if (healthProgress < player.poisonDamage)
			fullHealthS.draw(batch);
		else healthS.draw(batch);
		healthProgress++;
	}*/
	monsterIndex.healthBack9.draw(batch, 0, RESY-getBeltHeight()-4, RESX/3, 4);
	float health = player.controllingMob.health/player.controllingMob.maxHealth();
	monsterIndex.health9.draw(batch, 0, RESY-getBeltHeight()-4, health*(RESX/3), 4);
	
	
}

public void drawDirectionArrow(float x, float y, float a) {
	shapeR.setProjectionMatrix(camera.combined);;;
	//a+=180;
	shapeR.begin(ShapeType.Line);
	shapeR.setColor(SEMIWHITE);
	tmpV.set(-1.5f*player.zoomLevel,0);
	tmpW.set(tmpV);
	tmpW.add(tmpV);
	tmpV.rotate(a);
	tmpV.add(x,y);
	tmpW.rotate(a);
	tmpW.add(x,y);
	shapeR.line(x,y,tmpV.x, tmpV.y);
	
	tmpV2.set(-1f*player.zoomLevel,0);
	tmpV2.rotate(a-3);
	tmpV2.add(x,y);
	shapeR.line(tmpV2.x,tmpV2.y,tmpV.x, tmpV.y);
	
	tmpV2.set(-1f*player.zoomLevel,0);
	tmpV2.rotate(a+3);
	tmpV2.add(x,y);
	shapeR.line(tmpV2.x,tmpV2.y,tmpV.x, tmpV.y);
	
	shapeR.end();	
	
}

protected void drawAimingLine(Vector2 s, Vector2 d) {
	shapeR.setProjectionMatrix(camera.combined);;;
	
	shapeR.begin(ShapeType.Line);
	
	shapeR.line(s.x, s.y, d.x, d.y);
	shapeR.end();	
	
}
protected void drawBlockHighlight(BlockLoc p) {
	shapeR.setProjectionMatrix(camera.combined);;;
	shapeR.begin(ShapeType.Rectangle);
	shapeR.rect(p.x, p.y, 1, 1);
	shapeR.end();	
}
protected void drawInvalidBlockHighlight(BlockLoc p) {
	shapeR.setProjectionMatrix(camera.combined);;;
	shapeR.begin(ShapeType.Line);
	shapeR.line(p.x, p.y, p.x+1, p.y+1);
	shapeR.line(p.x+1, p.y, p.x, p.y+1);
	shapeR.end();	
}
@Override
protected void drawCraftingConfirm(){/*
	Player.inventory.getValidRecipes();
	//drawing recipeitems
	Recipe tmpRec = Player.inventory.rb.validRecipes.get(selectedRecipe);
	for (int i = 0; i < tmpRec.size(); i++){
		//frame
		batch.draw(PunkBodies.getItemFrame(tmpRec.list.get(i).id, 0), 
				BUTTONBUFFER, 
				(4-i)*MAPTILESIZE*2+MAPTILESIZE*2
				, 0, 0, MAPTILESIZE, MAPTILESIZE, 2f, 2f, 0);
		//count
		font.draw(batch, ""+tmpRec.list.get(i).q, 
				BUTTONBUFFER+MAPTILESIZE*2, 
				(5-i)*MAPTILESIZE*2+MAPTILESIZE);
		
	}
	//name
	font.draw(batch, ""+tmpRec.name, RESX/2, 6*MAPTILESIZE*2+MAPTILESIZE*2);
	//icon
	batch.draw(PunkBodies.getItemFrame(tmpRec.out, 0),(RESX/4)*3, 5*MAPTILESIZE*2+MAPTILESIZE*2 , MAPTILESIZE*2, MAPTILESIZE*2);
	font.draw(batch, "Uses:", BUTTONBUFFER +MAPTILESIZE/2, 6*MAPTILESIZE*2+MAPTILESIZE*1.5f);

	//draw buy buttons
	font.drawMultiLine(batch,"Craft 1", (RESX/2)*(9%2), (9/2+1)*BELTBUFFERY+MAPTILESIZE*1.5f, RESX/2, HAlignment.CENTER);
	if (craftingRecipeMax > 1){
		font.drawMultiLine(batch,"Craft "+craftingRecipeMax/2, (RESX/2)*(7%2), (7/2+1)*BELTBUFFERY+MAPTILESIZE*1.5f, RESX/2, HAlignment.CENTER);
		font.drawMultiLine(batch,"Craft "+craftingRecipeMax, (RESX/2)*(5%2), (5/2+1)*BELTBUFFERY+MAPTILESIZE*1.5f, RESX/2, HAlignment.CENTER);
	}
	//font.draw(batch, "BACK", BUTTONBUFFER + 2*BELTBUFFERX+TILESIZE/2, TILESIZE/2+TILESIZE);
	font.drawMultiLine(batch,"BACK", 
	RESX/2, (1)*BELTBUFFERY+MAPTILESIZE*1.5f, RESX/2, HAlignment.CENTER);
	
	//batch.draw(player.getFrame(monsterIndex), MAPTILESIZE, RESY-MAPTILESIZE*2-MAPTILESIZE/2, 16,32);
	drawActionButton();
	//batch.draw(monsterIndex.craftBtn, RESX-MAPTILESIZE*2, RESY - MAPTILESIZE*2, MAPTILESIZE*2, MAPTILESIZE*2);
*/
}
@Override
protected void drawCrafting(){
	/*player.inventory.getValidRecipes();
	for (int i = 0; i<3; i++){
		//batch.draw(monsterIndex.getItemFrame(player.inventory.getItemID(i)), BUTTONBUFFER + i*BELTBUFFERX+16, 0, 0, 0, TILESIZE, TILESIZE, 2f, 2f, 0);
	//	batch.draw(monsterIndex.uiLineV,(float)(BUTTONBUFFER + i*BELTBUFFERX*2), 0f, 0f, 0f, 128f, 2f, RESX/128f, 1f, 90f );
	}
	
	batch.draw(monsterIndex.selectSprite, BUTTONBUFFER+BELTBUFFERX*player.activeQuickSlot, 0);
	//for (int i = 0; i<UILINECOUNTX+1; i++)
	//	batch.draw(monsterIndex.uiLine, i*128, BELTBUFFERY);
	
	//for (int i = BELTBUFFERY; i<RESY-TILESIZE; i+=TILESIZE*2)//y
	//	for (int j = BUTTONBUFFER; j<BUTTONBUFFER+BELTBUFFERX*4; j+=128)batch.draw(monsterIndex.uiLine, j, i);
	
	drawI = player.inventory.rb.getValidSize(player.inventory);
	batch.draw(monsterIndex.uiLine,0 , MAPTILESIZE*2);

	////Gdx.app.log("crafting", "valid recs:" + drawI);
	for (int i = 2; i< Math.min(12,drawI+2); i++)
		{

			font.drawMultiLine(batch, player.inventory.rb.validRecipes.get(i-2).name, (RESX/2)*(i%2), (i/2)*BELTBUFFERY+MAPTILESIZE*1.5f, RESX/2, HAlignment.CENTER);
			
		}
	//top buttons
	//font.drawMultiLine(batch, "Craft", 0, RESY-(RESY/8)*(1*2+1)+16, RESX/2, HAlignment.CENTER);
	font.draw(batch, "Craft", BUTTONBUFFER + 3*BELTBUFFERX, 6*MAPTILESIZE*2+MAPTILESIZE*1.5f);
	batch.draw(player.getFrame(monsterIndex), MAPTILESIZE, RESY-MAPTILESIZE*2-MAPTILESIZE/2, 16,32);
	drawActionButton();
	batch.draw(monsterIndex.invBtn, RESX-MAPTILESIZE*2, RESY - MAPTILESIZE*2, MAPTILESIZE*2, MAPTILESIZE*2);
*/

}
protected  void drawSystem(){
//	
//	for (int i = 0; i<6; i++)
//		{
//			font.drawMultiLine(batch, (i<systemS.length)?systemS[i]:"" +i, (RESX/2)*(i%2), ((i+2)/2)*BELTBUFFERY+MAPTILESIZE*1.5f, RESX/2, HAlignment.CENTER);
//		}
//	font.drawMultiLine(batch, "Sound "+ 
//			(prefs.getBoolean("soundOn")?"on":"off")
//			, 0, 
//			4*BELTBUFFERY+MAPTILESIZE*1.5f, RESX/2, HAlignment.CENTER);
//	font.drawMultiLine(batch, "Run Buttons "+
//			(prefs_buttons?"on":"off")
//			, (RESX/2), 
//			4*BELTBUFFERY+MAPTILESIZE*1.5f, RESX/2, HAlignment.CENTER);
//	int jumpSize = prefs_jumpBtn;
//
//	font.drawMultiLine(batch, "Jump Button Size:"+
//			prefs_jumpBtn
//			, (RESX/2), 
//			3*BELTBUFFERY+MAPTILESIZE*1.5f, RESX/2, HAlignment.CENTER);
//	font.drawMultiLine(batch, "Music "+ 
//			(prefs_music?"on":"off")
//			, 0, 
//			3*BELTBUFFERY+MAPTILESIZE*1.5f, RESX/2, HAlignment.CENTER);
//	font.drawMultiLine(batch, "Zoom Button "+
//			(prefs_zoomBtn?"on":"off")
//			, (RESX/2), 
//			5*BELTBUFFERY+MAPTILESIZE*1.5f, RESX/2, HAlignment.CENTER);
//	
//	
//	switch (Player.gameInfo.difficulty){
//	case 0: difficultyS = "Peaceful";
//	break;
//	case 1: difficultyS = "Easy";
//	break;
//	case 2: difficultyS = "Normal";
//	break;
//	
//	case 3: difficultyS = "Hard";
//	break;
//	default: difficultyS = "error!";
//		break;
//	
//	}
//	font.drawMultiLine(batch, "Difficulty:"+ 
//			difficultyS
//			, 0, 
//			5*BELTBUFFERY+MAPTILESIZE*1.5f, RESX/2, HAlignment.CENTER);
///*	font.drawMultiLine(batch, " "+
//			(prefs.getBoolean("buttonsOn")?"on":"off")
//			, (RESX/2), 
//			5*BELTBUFFERY+TILESIZE*1.5f, RESX/2, HAlignment.CENTER);
//	*/
//	//top buttons
//	//font.drawMultiLine(batch, "Craft", 0, RESY-(RESY/8)*(1*2+1)+16, RESX/2, HAlignment.CENTER);
//	//font.draw(batch, "SYSTEM", BUTTONBUFFER + 2*BELTBUFFERX, 6*TILESIZE*2+TILESIZE*2);
//	batch.draw(player.getFrame(monsterIndex), MAPTILESIZE, RESY-MAPTILESIZE*2-MAPTILESIZE/2
//			, 0, 0, MAPTILESIZE, MAPTILESIZE*2, 1f, 1f, 0);
//	drawActionButton();
}
@Override
protected void drawInv(Player player){
	
	/*for (int i = 0; i<5; i++){
		//batch.draw(monsterIndex.getItemFrame(player.inventory.getItemID(i)), BUTTONBUFFER + i*BELTBUFFERX+16, 0, 0, 0, TILESIZE, TILESIZE, 2f, 2f, 0);
		//batch.draw(monsterIndex.uiLineV,(float)(BUTTONBUFFER + i*BELTBUFFERX), 0f, 0f, 0f, 128f, 2f, RESX/128f, 1f, 90f );
	}
	//batch.draw(monsterIndex.invBack, EXTRASPACEX, 0, RESX-EXTRASPACEX*2, RESY);
	batch.draw(monsterIndex.selectSprite, BUTTONBUFFER+BELTBUFFERX*player.activeQuickSlot, 0);
	
	
	
	for (int i = 0; i<24; i++)
		{
			if (player.inventory.getItemAmount(i) > 0){
				if (touchDragging = false || touchSlotDown != i) 
					
					//if (player.inventory.getItemID(i) > 0)
						batch.draw(monsterIndex.getItemFrame(player.inventory.getItemID(i), player.inventory.getItemMeta(i)),
								BELTOFFSETX+(i%4)*BELTBUFFERX, 
								RESY-(BELTBUFFERY* (i/4+1))
								, 0, 0, MAPTILESIZE, MAPTILESIZE, 2f, 2f, 0);
				else {
					batch.draw(monsterIndex.getItemFrame(player.inventory.getItemID(touchSlotDown),player.inventory.getItemMeta(touchSlotDown)),
						RESX/2+(touchLoc.x*RESX)/2-MAPTILESIZE, 
						RESY/2+(touchLoc.y*RESY)/2-MAPTILESIZE, 
						0, 0, MAPTILESIZE, MAPTILESIZE, 2f, 2f, 0);
							
				}
						

				font.draw(batch, ""+(player.inventory.getItemAmount(i)), 
						BELTOFFSETX+(i%4)*BELTBUFFERX, 
						RESY-(BELTBUFFERY* (i/4+1))+MAPTILESIZE);
			}
		}
	//top buttons
	//font.drawMultiLine(batch, "Craft", 0, RESY-(RESY/8)*(1*2+1)+16, RESX/2, HAlignment.CENTER);
	//font.draw(batch, "inventory", BUTTONBUFFER + 2*BELTBUFFERX, 6*TILESIZE*2+TILESIZE*2);
	drawActionButton();
	batch.draw(player.getFrame(monsterIndex), MAPTILESIZE/2, RESY-MAPTILESIZE*2
			, 0, 0, MAPTILESIZE, MAPTILESIZE*2, 1f, 1f, 0);
	//if (touchDragging && touchSlotDown >= 0) batch.draw(monsterIndex.getItemFrame(player.inventory.getItemID(touchSlotDown)),touchX-16, touchY-16, 0, 0, TILESIZE, TILESIZE, 2f, 2f, 0);
	batch.draw(monsterIndex.craftBtn, RESX-MAPTILESIZE*2, RESY - MAPTILESIZE*2, MAPTILESIZE*2, MAPTILESIZE*2);
*/
}

@Override
protected void drawHelp(){
	
	
	font.drawMultiLine(batch, blurb1, (RESX/2)*(1%2), ((1+2)/2)*BELTBUFFERY+TILESIZE*1.5f, RESX/2, HAlignment.CENTER);
	font.drawMultiLine(batch, blurb2, (RESX/2)*(2%2), ((2+2)/2)*BELTBUFFERY+TILESIZE*1.5f, RESX/2, HAlignment.CENTER);
	font.drawMultiLine(batch, blurb3, (RESX/2)*(3%2), ((3+2)/2)*BELTBUFFERY+TILESIZE*1.5f, RESX/2, HAlignment.CENTER);
	font.drawMultiLine(batch, blurb4, (RESX/2)*(4%2), ((4+2)/2)*BELTBUFFERY+TILESIZE*1.5f, RESX/2, HAlignment.CENTER);
	font.drawMultiLine(batch, blurb5, (RESX/2)*(5%2), ((5+2)/2)*BELTBUFFERY+TILESIZE*1.5f, RESX/2, HAlignment.CENTER);

	
/*font.drawMultiLine(batch, "sound "+ 
		(prefs.getBoolean("soundOn")?"on":"off")
		, 0, 
		4*BELTBUFFERY+TILESIZE*1.5f, RESX/2, HAlignment.CENTER);*/
}
protected void drawActionButton(){
	//System.out.println("action:" + getActionButton());
	//batch.draw(monsterIndex.getButtonSprite(getActionButton()), RESX-TILESIZE*2, RESY - TILESIZE*2);
	//if (getActionButton() > 0 && getActionButton() <=4)
	//	batch.draw(monsterIndex.uiButtons[getActionButton()-1], RESX-TILESIZE*2, RESY - TILESIZE*2);
	//else if (getActionButton() > 0)
		//batch.draw(monsterIndex.uiButtons[0], RESX-TILESIZE*2, RESY - TILESIZE*2);
	//if (getActionButton() == 30) batch.draw(monsterIndex.uiButtons[1], RESX-TILESIZE*2, RESY - TILESIZE*2);
}
@Override
protected void drawPlayer(){
	//player.draw(batch);
}
@Override
protected void drawMonsters(){

	//zombiePool.drawMonsters(camera, batch, monsterIndex);
	//slimePool.drawMonsters(camera, batch, monsterIndex);
	//snakePool.drawMonsters(camera, batch, monsterIndex);
	//pigPool.drawMonsters(camera, batch, monsterIndex);
	//spiderPool.drawMonsters(camera, batch, monsterIndex);
	//dwarfPool.drawMonsters(camera, batch, monsterIndex);
	gMap.chunkActors.draw(camera, batch, font, monsterIndex);

	gMap.explosionPool.draw(camera, batch, monsterIndex, deltaTime);
	bHandler.draw(camera, batch, font, monsterIndex);
}
protected void drawLoadingScreen(){
	//font.drawMultiLine(batch, "Crossing the void...", 0, (RESY/8)*4, RESX, HAlignment.CENTER);
}
@Override
protected void drawPlacingBlockLine(){
	Sprite s = monsterIndex.dashedLineAnimation.getKeyFrame(player.stateTime, true);
	//tmpV.set(player.hand.position.x, player.hand.position.y);
	tmpV.set(player.position.x, player.position.y+Player.EYEHEIGHT);
	aimerLineVerts[0] = tmpV.x;
	aimerLineVerts[1] = tmpV.y;
	
	aimerLineVerts[2] = dashedLineColor ;
	aimerLineVerts[3] = s.getU();
	aimerLineVerts[4] = s.getV();
	//tmpV.add(tmpV.tmp().set(1,0).rotate(adjustedTouchAngle));
	
	tmpV.set(gMap.globalTouchedV.x, gMap.globalTouchedV.y);
	aimerLineVerts[5] = tmpV.x;
	aimerLineVerts[6] = tmpV.y;
	
	aimerLineVerts[7] = dashedLineColor;
	aimerLineVerts[8] = s.getU2();
	aimerLineVerts[9] = s.getV2();
	
	gl.glLineWidth(1);
	s.getTexture().bind();
	gl.glEnable(GL10.GL_TEXTURE_2D);
	gl.glEnable(GL10.GL_BLEND);
	gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
	aimerLineMesh.setIndices(aimerLineIndices);
	aimerLineMesh.setVertices(aimerLineVerts);
	aimerLineMesh.render(GL10.GL_LINES, 0, 2);

}
float dashedLineColor = Color.toFloatBits(1,1,1,.5f);
@Override
protected void drawAimingLine(boolean fromHand){
	Sprite s;
	if (!fromHand){
		s = monsterIndex.dashedLineAnimation.getKeyFrame(player.stateTime, true);
		float aimerColor = Color.toFloatBits(100, 100, 100, 255);;
		tmpV.set(player.position.x, player.position.y+Player.EYEHEIGHT);
		aimerLineVerts[0] = tmpV.x;
		aimerLineVerts[1] = tmpV.y;
		
		aimerLineVerts[2] = dashedLineColor;
		aimerLineVerts[3] = s.getU();
		aimerLineVerts[4] = s.getV();
		//tmpV.add(tmpV.tmp().set(1,0).rotate(adjustedTouchAngle));
		tmpV.set(player.poi.position.x, player.poi.position.y);
		aimerLineVerts[5] = tmpV.x;
		aimerLineVerts[6] = tmpV.y;
		
		aimerLineVerts[7] = dashedLineColor;
		aimerLineVerts[8] = s.getU2();
		aimerLineVerts[9] = s.getV2();
		
		
		} else {
			s = monsterIndex.dashedLineAnimation.getKeyFrame(player.stateTime, true);
			float aimerColor = Color.toFloatBits(100, 100, 100, 255);;
			tmpV.set(player.hand.position.x, player.hand.position.y);
			aimerLineVerts[0] = tmpV.x;
			aimerLineVerts[1] = tmpV.y;
			
			aimerLineVerts[2] = dashedLineColor;
			aimerLineVerts[3] = s.getU();
			aimerLineVerts[4] = s.getV();
			//tmpV.add(tmpV.tmp().set(1,0).rotate(adjustedTouchAngle));
			tmpV.set(player.position.x, player.position.y+1);
			aimerLineVerts[5] = tmpV.x;
			aimerLineVerts[6] = tmpV.y;
			
			aimerLineVerts[7] = dashedLineColor;
			aimerLineVerts[8] = s.getU2();
			aimerLineVerts[9] = s.getV2();
		}
	
	//arrows
	//middle
	float aimerColor = Color.toFloatBits(100, 100, 100, 255);;
	aimerArrowVerts[4] = tmpV.x;
	aimerArrowVerts[5] = tmpV.y;
	aimerArrowVerts[6] = 0;
	aimerArrowVerts[7] = aimerColor;
	
	
	tmpV.add(mapTmp.set(1,0).rotate(adjustedTouchAngle-30));
	aimerArrowVerts[0] = tmpV.x;
	aimerArrowVerts[1] = tmpV.y;
	aimerArrowVerts[2] = 0;
	aimerArrowVerts[3] = aimerColor;
	tmpV.sub(mapTmp);
	tmpV.add(mapTmp.set(1,0).rotate(adjustedTouchAngle+30));
	aimerArrowVerts[8] = tmpV.x;
	aimerArrowVerts[9] = tmpV.y;
	aimerArrowVerts[10] = 0;
	aimerArrowVerts[11] = aimerColor;//Color.toFloatBits(90, 90, 90, 255);;*/
	
	gl.glLineWidth(1);
	s.getTexture().bind();
	gl.glEnable(GL10.GL_TEXTURE_2D);
	gl.glEnable(GL10.GL_BLEND);
	gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
	aimerLineMesh.setIndices(aimerLineIndices);
	aimerLineMesh.setVertices(aimerLineVerts);
	aimerLineMesh.render(shader, GL10.GL_LINES, 0, 2);
	
	aimerArrowMesh.setIndices(aimerArrowIndices);
	aimerArrowMesh.setVertices(aimerArrowVerts);
}

//protected void drawWeaponSlot(){
	

public void drawFingerArrow(float x, float y, boolean male){
	//batch.draw(monsterIndex.circleSprite, x, y);

	Sprite s;
	{
		s = monsterIndex.solidLine;//dashedLineAnimation.getKeyFrame(player.stateTime, true);
		float aimerColor = Color.toFloatBits(100, 100, 100, 255);;
		tmpV.set(player.position.x, player.position.y+Player.EYEHEIGHT);
		aimerLineVerts[0] = tmpV.x;
		aimerLineVerts[1] = tmpV.y;
		
		aimerLineVerts[2] = dashedLineColor;
		aimerLineVerts[3] = s.getU();
		aimerLineVerts[4] = s.getV();
		//tmpV.add(tmpV.tmp().set(1,0).rotate(adjustedTouchAngle));
		tmpV.set(x,y);//player.poi.position.x, player.poi.position.y);
		aimerLineVerts[5] = tmpV.x;
		aimerLineVerts[6] = tmpV.y;
		
		aimerLineVerts[7] = dashedLineColor;
		aimerLineVerts[8] = s.getU2();
		aimerLineVerts[9] = s.getV2();
		
		
		}
	
	//arrows
	//middle
	/*aimerArrowVerts[4] = tmpV.x;
	aimerArrowVerts[5] = tmpV.y;
	aimerArrowVerts[6] = 0;
	aimerArrowVerts[7] = aimerColor;
	
	
	tmpV.add(mapTmp.set(1,0).rotate(adjustedTouchAngle-30));
	aimerArrowVerts[0] = tmpV.x;
	aimerArrowVerts[1] = tmpV.y;
	aimerArrowVerts[2] = 0;
	aimerArrowVerts[3] = aimerColor;
	tmpV.sub(mapTmp);
	tmpV.add(mapTmp.set(1,0).rotate(adjustedTouchAngle+30));
	aimerArrowVerts[8] = tmpV.x;
	aimerArrowVerts[9] = tmpV.y;
	aimerArrowVerts[10] = 0;
	aimerArrowVerts[11] = aimerColor;//Color.toFloatBits(90, 90, 90, 255);;*/
	
	gl.glLineWidth(1);
	s.getTexture().bind();
	gl.glEnable(GL10.GL_TEXTURE_2D);
	gl.glEnable(GL10.GL_BLEND);
	gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
	aimerLineMesh.setIndices(aimerLineIndices);
	aimerLineMesh.setVertices(aimerLineVerts);
	aimerLineMesh.render(GL10.GL_LINES, 0, 2);
	
	//aimerArrowMesh.setIndices(aimerArrowIndices);
	//aimerArrowMesh.setVertices(aimerArrowVerts);

}

float[] touchHighlightCache = new float[touchHighlightSides*2+4];
private void makeTouchHighlightVerts(){
	Sprite s = monsterIndex.solidLine;;
	for (int i = 0; i < touchHighlightSides+1; i++)
	{
		
		int start = i * 2;
		//s =//dashedLineAnimation.getKeyFrame(player.stateTime, true);
		float aimerColor = Color.toFloatBits(100, 100, 100, 255);;
		tmpV.set(0, 0);
		tmpV.add(tmpV.tmp().set(0,20).rotate((360/(touchHighlightSides))*i));
		touchHighlightCache[start+0] = tmpV.x;
		touchHighlightCache[start+1] = tmpV.y;
		
		fingerIndices[i*2] = (short) i;
		fingerIndices[i*2+1] = (short) (i+1);
		
		filledIndices[i] = (short) i;
	}
	filledIndices[0] = touchHighlightSides+2;
	
}
public void drawFingerHightlight(float x, float y){
	/*Sprite s = monsterIndex.solidLine;;
	for (int i = 0; i < touchHighlightSides+1; i++)
	{
		
		int start = i * 5;
		//s =//dashedLineAnimation.getKeyFrame(player.stateTime, true);
		float aimerColor = Color.toFloatBits(100, 100, 100, 255);;
		tmpV.set(touchHighlightCache[i*2], touchHighlightCache[i*2+1]);
		fingerVerts[start+0] = tmpV.x+x;
		fingerVerts[start+1] = tmpV.y+y;
		
		fingerVerts[start+2] = dashedLineColor;
		fingerVerts[start+3] = s.getU();
		fingerVerts[start+4] = s.getV();
	}
	gl.glLineWidth(1);
	s.getTexture().bind();
	gl.glEnable(GL10.GL_TEXTURE_2D);
	gl.glEnable(GL10.GL_BLEND);
	gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
	fingerMesh.setIndices(fingerIndices);
	fingerMesh.setVertices(fingerVerts);
	fingerMesh.render(shader, GL10.GL_LINES, 0, touchHighlightSides*2+2);*/

	
	shapeR.setProjectionMatrix(camProj);;;
	
	shapeR.begin(ShapeType.Circle);
	
	shapeR.circle(x, y, 10);
	shapeR.end();
}

ShapeRenderer shapeR = new ShapeRenderer();
Matrix4 camProj = new Matrix4();
public void drawFingerHightlightFilled(float x, float y, float pc){
	Sprite s = monsterIndex.solidLine;;
	for (int i = 0; i < touchHighlightSides+1; i++)
	{
		
		int start = i * 5;
		//s =//dashedLineAnimation.getKeyFrame(player.stateTime, true);
		float aimerColor = Color.toFloatBits(100, 100, 100, 255);;
		tmpV.set(touchHighlightCache[i*2], touchHighlightCache[i*2+1]);
		fingerVerts[start+0] = tmpV.x+x*pc;
		fingerVerts[start+1] = tmpV.y+y*pc;
		
		fingerVerts[start+2] = dashedLineColor;
		fingerVerts[start+3] = s.getU();
		fingerVerts[start+4] = s.getV();
	}
	gl.glLineWidth(1);
	s.getTexture().bind();
	gl.glEnable(GL20.GL_TEXTURE_2D);
	gl.glEnable(GL20.GL_BLEND);
	gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	fingerMesh.setIndices(fingerIndices);
	fingerMesh.setVertices(fingerVerts);
	batch.end();
	//shader.begin();
	//fingerMesh.render(shader, GL20.GL_TRIANGLE_FAN, 0, touchHighlightSides*2);
	//shader.end();
	
	shapeR.setProjectionMatrix(camProj);;;
	
	shapeR.begin(ShapeType.FilledCircle);
	
	shapeR.filledCircle(x, y, 10);
	shapeR.end();
	batch.begin();
}


private void highlightBlock(Vector2 v){
	batch.draw(monsterIndex.blockSelSprite, 
					MathUtils.floor(v.x), 
					MathUtils.floor(v.y)
					, 1, 1, 1, 1,1,1,0);
}
protected void doInvButton(){
	switch (gameMode)
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
	case 12:
	case 13:
	case 14:
	//case 15:
	case 26:	
		
		gameMode = 16;
		break;
	case 18:
	case 17:
	case 16://changeBeltSlot(player.activeQuickSlot, false);
	break;
	
	}
	if (gameMode < 16 || gameMode >= 80) myRequestHandler.hide();
	else myRequestHandler.show();
}


protected void doActionButton(){
	switch(getActionButton())
	{

	case 1:player.isClimbing = true;
		break;
	case 2:gameMode = 17;
			craftingRecipeCount = player.inv.rb.getValidSize(player.inv);
		break;
	case 3:gameMode = 16;
		break;
	case 5://swim down
			player.body.applyLinearImpulse(tmpV.set(0,-16), player.position);
			break;
	case 6:
			if (player.isClimbingRope && player.isHoldingRope){
				player.destroyClimbJoint(world, gTime);
			}
		break;
	case 7: player.isFallingThroughBridge = true;
		break;
	case 30:
			gMap.addSign(activeSignLoc, signText);
			//Gdx.app.log("punk", "exit sign writing");
			//changeBeltSlot(player.activeQuickSlot, false);
		break;
	case 28:

		break;
	}
	if (gameMode < 16 || gameMode >= 80) myRequestHandler.hide();
	else myRequestHandler.show();
	
}
protected void doMenuButton(){
	//if (prefs_menuBtn.value == 0) beltHidden = !beltHidden;
}

protected void doPlayerTouch(){
	player.jump(1, gMap, world, monsterIndex, gameMode, gTime);
	/*if (player.isClimbingRope){
		player.climbTimer = gTime + 200;
		player.climbRopeUp(world, gTime);
	}*/
	
}

@Override
protected int getTouchSlot(int x, int y)
{
	int xoff;
	if (x < PBELTOFFSETX  || x > PBELTOFFSETX+PBELTBUFFERX*4||
		y > PBELTBUFFERY*6	
	)return -1;
	xoff = x - PBELTOFFSETX;
	xoff = xoff / PBELTBUFFERX;//0-3
	int yoff = y / PBELTBUFFERY;//0-4, 5 now
	if (yoff == 5) yoff = 4;
	//Gdx.app.log("getTouchSlot", "SLOT:"+(xoff+yoff*4));
	return xoff+yoff*4;
	
	
	//if (slot > 31) return 27;
	//else if (slot > 27) slot-=4;
	
	//return slot;
}

@Override
protected void updateMonsters(){
	
	gMap.explosionPool.update(gMap, world, deltaTime, player, gTime, monsterIndex);
	gMap.chunkActors.update(gMap, world, deltaTime, player, gTime, monsterIndex, null);
	bHandler.update(gMap, world, deltaTime, player, gTime, monsterIndex);
	
}
@Override
protected void updateSpawns(){


}



@Override
protected void updateMonsterRemovals(){
	//zombiePool.updateRemovals(world, player);
	//slimePool.updateRemovals(world, player);
	//snakePool.updateRemovals(world, player);
	//pigPool.updateRemovals(world, player);
	//spiderPool.updateRemovals(world, player);
	//dwarfPool.updateRemovals(world, player);
	//gMap.chunkActors.updateRemovals(world, player);
	gMap.explosionPool.updateRemovals(world, player);

}
@Override
protected void updateActionButton(){
	
}
@Override
protected int getActionButton(){
	
	switch (gameMode)
	{
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
	case 12: return 28;
	/*
		if (player.isClimbingRope) return 6; 
		if (player.isSwimming) return 5;
		if (player.bridgeTimer > gTime) return 7;
		if (player.climbButtonValid) return 1;
		
	return 0;*/
		
	case 16:return 2;
	case 18:
	case 17:return 3;
	case 30: return 30;
	//case 26: return 3;
	
	}
	
	return 0;
}
@Override
protected void doChunkShift (int direction){
	//Gdx.app.log ("punk", "SHIFT"+direction);
	gMap.shiftChunk(direction, player);
	
	//player.saveToDisk(false);
	
	//updateSpawns();
	//changeBeltSlot(player.activeQuickSlot, false);
	
	
	//bHandler.clearBosses(gMap, world);
	//scan for special blocks
	//gMap.spawnBosses(bHandler, world, monsterIndex);
	

	
	doBackgroundMesh();
	
	//chunkQueue = 0;
}
@Override
protected void saveGame(boolean saveP){
	//Gdx.app.log ("punk", "SAVEGAME");
	//if (PunkMap.openWorld){
		if (saveP)player.saveToDisk(true);
		gMap.chunkPool.saveAll();//saveAllChunks();
	//} else if (PunkMap.creativeGameName != null){
		//gMap.saveCreativeChunk();
		//String saveLoc = "Thwacker/saves/creative/"+PunkMap.creativeGameName+".inf";
		//Gdx.app.log("main", "save. loc "+saveLoc);
		//player.gameInfo.writeToFile(Gdx.files.external(saveLoc));
	//}
}

private int lastGM;
@Override
protected void changeBeltSlot(int slotID, boolean doStats){
	lastGM = gameMode;
	player.calculatePerks();
	if (slotID < 0){
		if (slotID == -1) gameMode = 11;
		else if (slotID == -2) gameMode = 12;
				
				
		
	} else
	if (slotID > 6 ){//|| gameMode == 19){
		
	}else {
		
		
		player.controllingMob.activeInvSlot = slotID;
		
		
		//tips
		/*if ( gameMode < 16 && !player.gameInfo.tooltipDone[gameMode] ){
			queuedTip.s = tooltips[gameMode];
			queuedTip.timer = gTime + monsterIndex.TIPTIMEOUT;
			player.gameInfo.tooltipDone[gameMode] = true;
		} */
		
		
	}
	Object data = PunkBodies.getItemData(player.getActiveID(), player.getActiveMeta());
	player.activeItem = PunkBodies.getItemInfo(player.getActiveID(), player.getActiveMeta());
	//Gdx.app.log("main", "game mode:"+gameMode);
	if (gameMode == 12){//axes
		player.activeAxe = player.getActiveID()-350;
	}
	
	if (player.isAxeing){
		
		player.destroyAxe(world);
	}
	
	if (gameMode == 11){
		{
			//if (!player.ironFist)
			//player.activeTool = (ToolInfo) data;
			//else player.activeFlail = monsterIndex.playerTools[6];
		}
		//se player.activeFlail = monsterIndex.playerTools[player.getActiveID()-399];
		//if (player.activeFlail.s == null) Gdx.app.log("main", "null poi!!!!!");
		
	}
	if (gameMode == 9){//wand
		player.activeWand = player.getActiveID()-270;
		
	}
	//player.isGliding = false;
	if (gameMode == 80 && doStats){//gliding
		if (!player.isOnSolidGround && !player.isGliding){
			player.startGliding(world);
			Punk.touchLoc.set((player.isLeft?1:-1), -.33f);
			flyTouchLoc.set(player.isLeft?Punk.PRESX*3/4:Punk.PRESX/4 , Punk.PRESY*2/3);
			//flyTouchLoc.set(actualTouchLoc);
			setFlyTouchAngle();
		}else if (player.isGliding && lastGM == 80){
			
		}
		else monsterIndex.playFailedSound();
		player.activeFlight = (FlightInfo) data;
	}else if (gameMode != 80){
		
	}
	
	if (gameMode == 81){//melee
		player.activeTool = (WeaponInfo) data;
		//Gdx.app.log(TAG, "flail");
	}
	
	if (gameMode == 1 || gameMode == 15){
		player.activeGrenade = (GrenadeInfo) data;
	}
	
	//check ammo slot for throwing
	
	if (gameMode > 15 && gameMode < 80) {
		if (lastGM != gameMode) myRequestHandler.show();}
	else {
		if (lastGM != gameMode)
			myRequestHandler.hide(); 
	}
	
	
	//stats
	int statID = -1, iID;
	switch (gameMode){
	case 1:
			iID = player.getActiveID();
			if (iID < 303 && iID >= 300){//grenades
				statID = 10;
			}else if (iID == 307){//bridge
				statID = 11;
			} else {// throw
				statID = 9;
			}
		break;
	case 2:
		iID = player.getActiveID();
		if (iID == 50 || iID == 51)
			statID = 6;
		else statID = 2;
		break;
	case 11:
		iID = player.getActiveID();
		if (iID == 0) statID = 4;
		else statID = 3;
		break;
	case 12:
		statID = 5;
		break;
		
	}
	if (doStats && statID != -1 && player.stats.stats[statID] < 1){
		//openMessageWindow(Punk.popupMessages[statID]);
		player.stats.stats[statID]++;
	}
	
	
	//beltHidden = true;
}

public void setStarter(Start st){
	starter = st;
}



public static float STAGEDURATION = .35f;
public static int visibleDistanceFromPlayer;
/*public ButtonForInvSlots[] slotBtns;
public ButtonForCraft btnCraft;
public ButtonForCraftRecipe[] btnCraftRecipe ;
public ButtonForCraftRecipe btnCraftIcon;
public ButtonForCraftAmountControls btnCraftAmountPlus, btnCraftAmountMinus;
public ButtonForCraftConfirm btnCraftYes, btnCraftNo;
public ButtonForCraftUses[] btnCraftUses;

public Label labelCraftAmount, labelCraftInfo, labelCraftUses;
public ButtonForCraftAction btnCraftAction;*/
public Recipe activeRecipe;
public int craftAmount = 1;
public void onStartNewGame(){
	skillPage = 0;
}
/*public ButtonForMessageClose btnMessageClose;
public ButtonForMessageText btnMessageText;
public ButtonForPrefs[] btnPrefs = new ButtonForPrefs[8];
public String craftDefaultDescription = "Select item to craft.\n";
public String craftDescription = craftDefaultDescription;*/
//public ChestToInvListener[] chestListeners;// = new ChestToInvListener(slotBtns);
//public static ButtonForOnScreenButton[] btnAction = new ButtonForOnScreenButton[3];
//public static ButtonForHelp btnHelp;
//public ButtonForCraftPage btnCraftL, btnCraftR;
//public ButtonForPerk[] perkBtns = new ButtonForPerk[16];
//public ButtonForPerkAction perkActionBtn;
//public static ButtonForPerkScreen btnPerk;

public ButtonForBeltSlots[] btnBelt = new ButtonForBeltSlots[7];


protected static void getGraphics(){
	//System.out.println("width:::::" + Gdx.graphics.getWidth());
	PRESX = Gdx.graphics.getWidth();
	PRESY = Gdx.graphics.getHeight();
	/*switch (PRESX){
	
	case 480: //480x320 = 1.5
		
		break;
	case 640:
	case 320://1.333
		RESX = 320;
		RESY = 240;
		BWIDTH=20;
		BHEIGHT=15;

		break;
	case 800://1.6666
		RESX = 400;
		RESY = 240;
		BWIDTH=25;
		BHEIGHT=15;
		
		break;
	case 854://1.777
		RESX = 854;
		RESY = 480;
		BWIDTH=27;
		BHEIGHT=15;
	
		break;
		
	}*/
	aspectRatio = (float)PRESX/(float)PRESY;
	RESX = MathUtils.round(aspectRatio*240);
	RESY = 240;
	BWIDTH = MathUtils.round(15 * aspectRatio);
	BHEIGHT = 15;
	//Gdx.app.log("punk", "AR = "+aspectRatio + "width = "+BWIDTH+ "resx:"+RESX);
	float xScaleFrom320 = (float)PRESX / (float)RESX;
	float yScaleFrom240 = (float)PRESY/(float)RESY;
	JUMPBUTTONSIZE = (int)(16*yScaleFrom240);
	TILESIZE = 32;
	MAPTILESIZE = 16;
	ITEMTILESIZE = 32;
	PRADIUSX = PRESX/2;
	//Gdx.app.log("INIT RESOLUTIONS", "PRADUISX:"+PRADIUSX);
	PRADIUSY = PRESY/2;
	
	UILINECOUNTX = RESX / 128+1;
	//JUMPBUTTONX1 = PRADIUSX-JUMPBUTTONSIZE;
	//JUMPBUTTONX2 = PRADIUSX+JUMPBUTTONSIZE;
	//JUMPBUTTONY1= PRADIUSY-JUMPBUTTONSIZE;
	//JUMPBUTTONY2 = PRADIUSY+JUMPBUTTONSIZE;
	BUTTONBUFFERL = BUTTONBUFFER;
	BUTTONBUFFERR = RESX - BUTTONBUFFER;
	TILESIZE2 = (byte)(TILESIZE / 2);
	
	
	BUTTONBUFFER = 40;//side buttons
	BELTBUFFERX = 32;
	BELTBUFFERY = 32;
	BELTOFFSETX = (RESX/2)-BELTBUFFERX*2;

	PBUTTONBUFFER = (int)(BUTTONBUFFER*xScaleFrom320);//side buttons
	PBELTBUFFERX = (int)(BELTBUFFERX*xScaleFrom320);//size of belt buttons in px for touch
	PBELTBUFFERY = (int)(BELTBUFFERY*yScaleFrom240);//need to be proportional to the window:actual resolutions
	
	PBELTOFFSETX = PRESX/2-PBELTBUFFERX*2;
	
	
	//Gdx.graphics.setDisplayMode(PRESX, PRESY, false);
	
		

}
class InvButton extends Button{
	int id;
	PunkInventory inv;
	public InvButton(int id, ButtonStyle style) {
		super(new Actor(), style);	
		this.id = id;
	}
	@Override
	public void draw(SpriteBatch batch, float a){
		if (player.controllingMob.activeInvSlot != id)super.draw(batch, a);
		else getStyle().down.draw(batch, getX(), getY(), getWidth(), getHeight());
		int iid = inv.getItemID(id);
		if (iid == 0) return;
		//get.down.draw(batch, getX(), getY(), getWidth(), getHeight());
	//if (id == main.player.activeQuickSlot)mi.beltSelected9.draw(batch, getX()+picOffset-2, getY()-4, picWidth+4, picHeight+4, getColor());
		float  ox = (getWidth() - uiItemX)/2, oy = ( getHeight()- uiItemY)/2;;
		Sprite s = monsterIndex.getItemFrame(iid, inv.getItemMeta(id));
		batch.draw(s, getX()+ox, getY()+oy, uiItemX, uiItemY);
		
		int itemID = inv.getItemID(id), itemMeta = inv.getItemMeta(id);
		//Gdx.app.log(TAG, "draw inv");
		if (PunkInventory.hasDurability(itemID, itemMeta)){//durability meter
			int newWidth;
			newWidth = (int) ((  inv.getItemMeta(id)*(getWidth()-4))/PunkInventory.getMaxDurability(itemID, itemMeta));
			batch.draw(monsterIndex.durability, getX(), getY(), newWidth, getHeight()/8);
		} else{
			font.drawMultiLine(batch, Punk.numberStrings[Math.max(0, inv.getItemAmount(id))], 
						getX(), getY()+oy, getWidth(), HAlignment.CENTER);
		}
		
	}
	
}

class BeltButton extends Button{
	int id;
	PunkInventory inv;
	public BeltButton(int id, ButtonStyle style) {
		super(new Actor(), style);	
		this.id = id;
	}
	@Override
	public void draw(SpriteBatch batch, float a){
		int beltID = player.controllingMob.belt[id];
		if (player.controllingMob.activeInvSlot != beltID)super.draw(batch, a);
		else getStyle().down.draw(batch, getX(), getY(), getWidth(), getHeight());
		
		if (id < 0){//power
			
		} else {//item
			
			int iid = inv.getItemID(beltID);
			if (iid == 0) return;
			float  ox = (getWidth() - uiItemX)/2, oy = ( getHeight()- uiItemY)/2;;
			Sprite s = PunkBodies.getItemFrame(iid, inv.getItemMeta(beltID));
			batch.draw(s, getX()+ox, getY()+oy, uiItemX, uiItemY);
			int itemID = inv.getItemID(beltID), itemMeta = inv.getItemMeta(beltID);
			
			if (PunkInventory.hasDurability(itemID, itemMeta)){//durability meter
				int newWidth;
				newWidth = (int) ((  inv.getItemMeta(beltID)*(getWidth()-4))/PunkInventory.getMaxDurability(itemID, itemMeta));
				batch.draw(monsterIndex.durability, getX(), getY(), newWidth, getHeight()/8);
			} else{
				font.drawMultiLine(batch, Punk.numberStrings[Math.max(0, inv.getItemAmount(beltID))], 
							getX(), getY()+oy, getWidth(), HAlignment.CENTER);
			}
			
		}
		
		
	}
	
}

int skillPage = 0;
/*class SkillButton extends Button{
	int id;
	public SkillButton(int id, ButtonStyle style) {
		super(new Actor(), style);	
		this.id = id;
	}
	@Override
	public void draw(SpriteBatch batch, float a){
		if (player.controllingMob.activeInvSlot != id)super.draw(batch, a);
		else getStyle().down.draw(batch, getX(), getY(), getWidth(), getHeight());	
		float  ox = (getWidth() - uiItemX)/2, oy = ( getHeight()- uiItemY)/2;;
		//Sprite s = monsterIndex.getItemFrame(iid, inv.getItemMeta(id));
		Sprite s = monsterIndex.getSkillSprite(id);
		batch.draw(s, getX()+ox, getY()+oy, uiItemX, uiItemY);	
		//int itemID = inv.getItemID(id), itemMeta = inv.getItemMeta(id);
		//Gdx.app.log(TAG, "draw inv");
		
			font.drawMultiLine(batch, Punk.numberStrings[Math.max(0, inv.getItemAmount(id))], 
						getX(), getY()+oy, getWidth(), HAlignment.CENTER);
	}
	
}
SkillButton[] uiSkillButtons = new SkillButton[16];*/
float uiItemX = 32, uiItemY = 32;
Table uiRoot, uiInv, uiDebug, uiBelt, uiSkills;
static Table uiModes;
Table uiChar;
Table uiItemInfo;
Table uiStats, uiSettings;

InvButton[] invButtons, altInvButtons;
BeltButton[] beltButtons;

static EventListener skillClicker;
Label invL1, invL2, itemInfoL, itemDescL, itemTouchDescL, itemInfoTitleL, pNameL, pClassL;
Label[] statVals;
ButtonStyle style;
Button[] statIncrementer;
Window itemInfoW;
static float adBuffer = 0;//52;
public boolean stretchBelt = true;

public void makeMenuActors(){
	//Gdx.app.log(TAG, "menu actors");
	Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"));
	skin = starter.newG.skin;
	Table beltTab = new Table();
	style = new ButtonStyle();
	NinePatchDrawable inv9 = new NinePatchDrawable(monsterIndex.invBack9), invs9 = new NinePatchDrawable(monsterIndex.invSelected9);
	style.down = invs9;
	style.up = inv9;
	style.over = inv9;
	
	invL1 = new Label("Inventory", skin);
	invL2 = new Label("Skills", skin);
	//float h = getBeltHeight();
	float modesH = getModesHeight();//uiModes.getHeight();
	invButtonSize = getInvButtonSize();	
	
	uiRoot = new Table(skin);
	uiRoot.setFillParent(true);
	pNameL = new Label("", skin);
	pClassL = new Label("", skin);
	SkillButton.back9 = ((NinePatchDrawable) style.down).getPatch();
	Button bInv = new Button(skin);
	bInv.addListener(new ClickListener(){
		@Override
		public void clicked(InputEvent event, float x, float y){
			openInv();
		}
	});
	bInv.add("Inv");//.pad(invButtonSize/2, 10, invButtonSize/2+3, 10);
	
	Button bStats = new Button(skin);
	bStats.addListener(new ClickListener(){
		@Override
		public void clicked(InputEvent event, float x, float y){
			openChar();
		}
	});
	bStats.add("Char");//.size(invButtonSize);//pad(invButtonSize/2, 10, invButtonSize/2+3, 10);
	
	Button bSettings = new Button(skin);
	bSettings.addListener(new ClickListener(){
		@Override
		public void clicked(InputEvent event, float x, float y){
			openSettings();
		}
	});
	bSettings.add("Conf");
	uiModes = new Table(skin);
	uiModes.add(bInv).expandX().left().size(invButtonSize);
	uiModes.row();
	uiModes.add(bStats).expandX().left().size(invButtonSize);
	uiModes.row();
	uiModes.add(bSettings).expandX().left().size(invButtonSize);
	uiModes.size(getInvButtonSize());
	uiModes.pack();
	
	
	uiSettings = new Table(skin);
	uiSettings.bottom().padBottom(adBuffer).size((getInvScreenWidth()), getInvScreenHeight());
	
	
	SettingLabel lPlaceMode = new SettingLabel(prefs_place_mode, skin),
			lMusicOn  = new SettingLabel(prefs_music, skin)
	,lSoundOn  = new SettingLabel(prefs_sound, skin)
	,lButtonsOn = new SettingLabel(prefs_buttons, skin)
	,lBackground = new SettingLabel(prefs_background, skin)
	,lDebugButtons = new SettingLabel(prefs_debug, skin);;

	
	uiSettings.add(lMusicOn);
	uiSettings.row();
	uiSettings.add(lSoundOn);
	uiSettings.row();
	uiSettings.add(lButtonsOn);
	uiSettings.row();
	uiSettings.add(lBackground);
	uiSettings.row();
	uiSettings.add(lPlaceMode);
	uiSettings.row();
	uiSettings.add(lDebugButtons);
	uiSettings.row();
	uiSettings.bottom().padBottom(adBuffer).size((getInvScreenWidth()), getInvScreenHeight());
	uiSettings.pack();
	
	uiInv = new Table(skin);
	
	uiInv.bottom().padBottom(adBuffer).size((getInvScreenWidth()), getInvScreenHeight());
	invButtons = new InvButton[PunkInventory.INVENTORYSIZE];
	
	for (int i = 0; i < PunkInventory.INVENTORYSIZE; i++){
		
		invButtons[i] = new InvButton(i, style);
		invButtons[i].inv = player.inv;
		invButtons[i].addListener(new ActorGestureListener(){
			public boolean longPress(Actor act, float x, float y){
				openItemInfoWindow((InvButton)act);
				return true;
			}
			
			public void tap(InputEvent event,
	                float x,
	                float y,
	                int count,
	                int button){
				
				InvButton but = (InvButton) event.getListenerActor();
				//if (but.id >= BELTSLOTCOUNT)but.inv.moveToBelt(but.id);
				player.controllingMob.putInBelt(but.id);
				
				
			}
			
		});
		invButtons[i].addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				
			}
		});
		
		//if (i >= BELTSLOTCOUNT){
			uiInv.add(invButtons[i]).size(invButtonSize).expand();
			if (i % BELTSLOTCOUNT == BELTSLOTCOUNT-1) uiInv.row();
		//}
	}
	uiInv.pack();
	
	uiStats = new Table(skin);
	uiStats.bottom().padBottom(adBuffer).size((PRESX/3), getInvScreenHeight());
	
	
	uiBelt = new Table(skin);
	beltButtons = new BeltButton[BELTSLOTCOUNT];
	
	for (int i = 0; i < BELTSLOTCOUNT; i++){
		beltButtons[i] = new BeltButton(i, style);
		beltButtons[i].inv = player.inv;
		beltButtons[i].addListener(new ClickListener(){
			
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				BeltButton but = (BeltButton) event.getListenerActor();
				checkForButtonOverridePress(7);
				player.controllingMob.touchBelt(but.id);
				return true;
			}
			
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button){
				checkForButtonOverrideUnPress(7);
				
			}
			
			
		});
		float invButtonSize = getInvButtonSize();		
		
			
		uiBelt.add(beltButtons[i]).size(invButtonSize).expandX();;
	}
	uiBelt.top().size(PRESX, getBeltHeight());
	//uiBelt.setFillParent(true);
	uiBelt.pack();
	
	uiSkills = new Table(skin);
	uiSkills.bottom().padBottom(adBuffer).size(PRESX/2-getInvModeButtonWidth()-20, getInvScreenHeight());
	
	
	uiChar = new Table(skin);
	statVals = new Label[GenericMob.STATS_COUNT];
	statIncrementer = new Button[GenericMob.STATS_COUNT];
	for (int i = 0; i < GenericMob.STATS_COUNT; i++){
		uiChar.add(GenericMob.STAT_NAMES[i]);
		statVals[i] = new Label("0", skin);
		uiChar.add(statVals[i]);
		statIncrementer[i] = new Button(skin);
		statIncrementer[i].add("+");
		statIncrementer[i].addListener(new ClickListener(){
			int index;
			
			@Override
			public void clicked(InputEvent event, float x, float y){
				GenericMob mob = player.controllingMob;
				if (mob.getMaxStat(index) > mob.baseStats[index]){
					mob.baseStats[index]++;
					populateCharacterScreen();
				}
			}
		});
		uiChar.add(statIncrementer[i]);
		uiChar.row();
	}
	
	uiItemInfo = new Table(skin);
	uiItemInfo.setFillParent(true);
	uiItemInfo.addListener(new ClickListener(){
		@Override
		public void clicked(InputEvent event, float x, float y){
			uiRoot.removeActor(uiItemInfo);
			};
	});
	itemInfoL = new Label("info", skin);
	itemDescL = new Label("description", skin);
	itemTouchDescL = new Label("description of how to use the item", skin);

	itemInfoL.setWrap(true);
	itemDescL.setWrap(true);
	itemTouchDescL.setWrap(true);
	itemTouchDescL.setAlignment(Align.left+Align.top);
	itemInfoL.setAlignment(Align.left+Align.top);
	itemDescL.setAlignment(Align.left+Align.top);
	itemInfoW = new Window("", skin);
	Table itemInfoLabels = new Table(skin);
	itemInfoLabels.add(itemInfoL).size(PRESX/4-20, PRESY/4).padRight(10).top();
	itemInfoLabels.add(itemTouchDescL).size(PRESX/4-20, PRESY/4).padLeft(10).expandX().top();
	
	itemInfoTitleL = new Label("item name", skin);
	
	itemInfoW.add(itemInfoTitleL);//.padTop(8);
	itemInfoW.row();
	itemInfoW.add(itemInfoLabels).left().expandX().top().expandY()
	.pad(5).padTop(20);
	itemInfoW.row();
	itemInfoW.add(itemDescL).pad(5).padTop(20).expandX().size(PRESX/2-20, PRESY/4-10);
	itemInfoW.setSize(PRESX,  PRESY);
	itemInfoW.setTitleAlignment(Align.top);
	uiItemInfo.add(itemInfoW).size(PRESX/2, PRESY/2);
	
	uiItemInfo.pack();
	
	

	stage.addActor(uiRoot);
	closeInv();
}
	


private float getModesHeight() {
	// TODO Auto-generated method stub
	return 0;
}
private float getInvScreenHeight(){
	return PRESY - getBeltHeight() - adBuffer - getModesHeight();
}
private float getInvButtonSize() {
	return Math.min((getInvScreenHeight())/6f, (getInvScreenWidth())/6f);
}
private float getInvScreenWidth(){
	return PRESX*.5f;
}
private float getPowerScreenWidth(){
	return PRESX - getInvScreenWidth() - getInvModeButtonWidth();
}
private float getInvModeButtonWidth() {
	
	return getInvButtonSize();
}
private float getBeltHeight() {
	return RESY * beltYa;
}
protected void openItemInfoWindow(InvButton act) {
	ItemDef info = player.controllingMob.inv.getItemInfo(act.id);
	itemInfoL.setText(info.getInfoText());
	itemInfoL.pack();
	itemTouchDescL.setText(info.getTouchDescText(player.info.classID));
	itemTouchDescL.pack();
	itemDescL.setText(info.desc);
	itemDescL.pack();
	//itemInfoW.setTitle(info.getName());
	itemInfoTitleL.setText(info.getName());
	itemInfoW.pack();
	//uiRoot.clear();
	uiItemInfo.pack();
	uiRoot.addActor(uiItemInfo);
	
	uiRoot.pack();
	
}
protected void openChar() {
	//populateSkills();
	charOpen = true;
	populateCharacterScreen();
	populateSkills();
	uiRoot.clear();
	uiStats.clear();
	uiRoot.add(uiBelt);
	uiRoot.row();
	pNameL.setText(player.controllingMob.name+" "+player.classID);
	pNameL.pack();
	uiStats.add(pNameL);
	uiStats.row();
	pClassL.setText(GenericMob.classInfos[player.controllingMob.classID].name);
	pClassL.pack();
	uiStats.add(pClassL);
	uiStats.row();
	uiStats.add(uiChar).expand().top().padTop(10);
	uiStats.pack();
	uiSkills.pack();
	uiRoot.add(uiStats).size(PRESX/3, PRESY/2).left().top();
	uiRoot.add(uiSkills).right().top().expand().padRight(10);
	uiRoot.add(uiModes);
	uiRoot.pack();
}
public void openInv(){
	charOpen = false;
	gameMode = 16;
	uiRoot.clear();
	populateSkills();
	uiSkills.pack();
	uiRoot.add(uiBelt).expandX();
	uiRoot.row();
	uiRoot.add(uiInv).left().bottom();//.expand();
	//uiRoot.add(uiSkills).top();//.expand();
	uiRoot.add(uiModes);
	uiRoot.row();
	uiRoot.pack();
}

protected void openSettings() {
	uiRoot.clear();
	uiSettings.pack();
	uiRoot.add(uiBelt);
	uiRoot.row();
	uiRoot.add(uiSettings).expandY();
	for (int i = 0; i < uiSettings.getChildren().size; i++){
		SettingLabel l = (SettingLabel)(uiSettings.getChildren().get(i));
		l.setting.getVal();
		l.setName();
	}
	uiRoot.pack();
	
}
public void closeInv(){
	uiRoot.clear();
	populateBelt();
	uiRoot.add(uiBelt).top().expand();
	uiBelt.pack();
	uiRoot.pack();
	gameMode = 0;
	
	//Gdx.app.log(TAG, "belt"+uiBelt.getChildren().get(1).getX());
}
public void populateBelt(){
	float h = PRESY * beltYa, width = stretchBelt?PRESX/BELTSLOTCOUNT:h;
	uiBelt.clear();
	for (int i = 0; i < BELTSLOTCOUNT; i++){
		beltButtons[i].inv = player.inv;
		uiBelt.add(beltButtons[i]).expand().size(width,h);
	}
	uiBelt.pack();
}
public boolean charOpen = false;
private void populateSkills() {
	Iterator<Actor> contentIter = uiSkills.getChildren().iterator();
	while (contentIter.hasNext()){
		Actor a = contentIter.next();
		if (a instanceof SkillButton){
			skillButtonPool.free((SkillButton) a);
		}
		
	}
	uiSkills.clear();
	//skillButtonPool.freeAll();
	for (int i = 0, n = player.skills.size;i<n;i++){
		//Entry<Object> e = iter.next();
		int v = player.skills.get(i);
		switch (GenericMob.skillInfos[v].type ){
		default:if (!charOpen) break;
		case 2:
			SkillButton but = skillButtonPool.obtain();
			but.set(v);
			uiSkills.add(but).size(invButtonSize).expand();
			Gdx.app.log(TAG, "adding skill button");
			if (i%2 == 0)
				uiSkills.row();
			break;
			
		}
	}
	
}

private void populateCharacterScreen(){
	//uiRoot.clear();
	
	for (int i = 0; i < GenericMob.STATS_COUNT; i++){
		if (player.controllingMob.hasLevelUp()){
			if (player.controllingMob.getMaxStat(i) > player.controllingMob.baseStats[i]){
				statIncrementer[i].setDisabled(false);
				continue;
			}
		}
		statIncrementer[i].setDisabled(true);
	}
	//uiRoot.add(uiChar);
}

public static Pool<SkillButton> skillButtonPool  = new Pool<SkillButton>(){
	@Override
	protected SkillButton newObject() {
		SkillButton s = new SkillButton();
		//contents.add(s);
		return s;		
	}
};
public void hideMenu(){
}
public void closeMenu(){
}
public void openBelt(){
}
public void closeBelt(boolean buttonToo){
}


public void prepareCraftingUses(){

}

public void openCrafting(){

}
public void closeCrafting(){
	/*for (Actor act:craftGroup.getActors()){
		act.action(MoveTo.$(act.x, act.y-RESY, STAGEDURATION)
				.setInterpolator(OvershootInterpolator.$(STAGEDURATION)));
	}*/
	
	
}
public void openCraftingConfirm(){
	
}
public void closeConfirm(){
	//btnCraftYes.action(MoveTo.$(RESX+50, RESY/2, STAGEDURATION)
	//		.setInterpolator(OvershootInterpolator.$(STAGEDURATION)));
	//btnCraftNo.action(MoveTo.$(-200, RESY/2, STAGEDURATION)
	//		.setInterpolator(OvershootInterpolator.$(STAGEDURATION)));
	//openCrafting();
}

public void openPerks(){
	
}

protected void closePerks(){
	
}


public void openMessageWindow(String message){
	
}
public void closeMessageWindow(){
	
}
public void swapChest(boolean toChest){
	
	
	
}
@Override
protected void openChestButton() {}

public static void openAction(int type){}

public static void openDoorButtons(Door d) {
	
	
}
private static boolean bedButtonsOpen;
public static BlockBG genericBGBlock = new BlockBG();
public static void openBedButtons() {

	openDoorButtons(bedDoor);
	bedButtonsOpen = true;
}
public static void closeBedButtons() {
	//closeDoorButtons();
	if (bedButtonsOpen)
		closeAction();
}


protected static void closeAction(){}
protected void openDieDialog(){
	
}
public void resetButtons(){
	//closeInv();
	
	//closeConfirm();
	//closeCrafting();
	//closeMenu();
	//
	//if (!beltHidden) 
	//	openBelt();
	//else closeBelt(false);
	//if (gameMode != 19)
		//changeBeltSlot(player.activeQuickSlot);
	//backToGameTimer = Punk.STAGEDURATION;
	//backToGamePending = true;
	//Punk.myRequestHandler.hide();
	//stage.act(STAGEDURATION);
}
private static final float WEEK_LENGTH_SECONDS = 60*16;
private static final float DAY_LENGTH = 16, NIGHT_START = 3;
public static final int BUTTONOVERRIDECOUNT = 8;


public void adjustSkyColor(){
	int groundHeight = Chunk.getGroundHeight(player.x)-20;
	float 
	factor=.66f; //change the background in renderplayer if this chhhanges
	
	if (//ground chunk, above ground
			true ||
			 ( player.y > groundHeight) || player.y < PrimeMaterial.effectiveDepth
			//|| gMap.currentChunkHeightID > 0
					){
		
		//skyColor = gMap.skyTarget;
		skyColor = 0;
		CorneredSprite.l = 0;
		
		int time = (int)(player.globalTime % (DAY_LENGTH*60)) / 60;
		//Gdx.app.log(TAG, "adjust sky "+time);
		if (time == 0){//morning
			skyColor = (player.globalTime % 60)/60f;
			//Gdx.app.log("main", "moiirning~"+skyColor);
			if (skyColor > .5f)
				CorneredSprite.l =(int)Math.min(15.9f, (skyColor-.5f)*2*16);
			PunkMap.timeOfDay = DayTime.SUNRISE;
		}
		else if (time < NIGHT_START){//day
			skyColor = 1;
			PunkMap.timeOfDay = DayTime.DAY;
			CorneredSprite.l = 15;
		}
		else if (time < NIGHT_START+1){//sunset
			skyColor = (player.globalTime % 120)/120f;
			//Gdx.app.log("main", "sunset~"+skyColor);
			CorneredSprite.l = 15;
			if (skyColor > .5f)
				CorneredSprite.l =15-(int)Math.min(15.9f, (skyColor-.5f)*16*2);//(skyColor - .5f )* 2*16);
			skyColor = 1-skyColor;
			PunkMap.timeOfDay = DayTime.SUNSET;
		} else {//night
			skyColor = 0f;
			//Gdx.app.log("main", "night~"+skyColor);
			CorneredSprite.l = 0;
			if (skyColor > .5f)
				CorneredSprite.l =15-(int)((skyColor - .5f )* 2*16);
			PunkMap.timeOfDay = DayTime.NIGHT;
		}
		
		if (PunkMap.dungeonMode)skyColor = 0;
		gl.glClearColor(skyColor*(factor/2),skyColor*factor,skyColor,1);
			//adjustBackgroundLighting(skyColor);

			//Gdx.app.log("main", "changing sky"+skyColor+"target:"+skyTarget);
		
		
	} else if (player.y > groundHeight-10){//ground chunk, transition
		float adjustedSkyColor =  1f/((groundHeight-player.y)+1);
		gl.glClearColor(adjustedSkyColor*(factor/2),adjustedSkyColor*factor,adjustedSkyColor,1);
		//adjustBackgroundLighting(skyColor);
		//Gdx.app.log("main", "changing sky, transition"+adjustedSkyColor+"target:"+skyTarget);

	} else {//below ground
		//skyColor = 0;
		gl.glClearColor(0,0,0,0);
		//adjustBackgroundLighting(skyColor);
		//Gdx.app.log("main", "changing sky, below ground"+skyColor+"target:"+skyTarget);

	}

}
@Override
public void adjustBackgroundLighting(float l){
	//Gdx.app.log("main", "adjust back lighting"+l);
	MiniMap miniMap = Chunk.planes.get(gMap.currentPlane).miniMap;
	miniMap.adjustNearLighting(l);
	//adjustFarLighting(l, vertexCacheF);
}
public void openLoadInCreative(){
	
}
public void openHelp() {

}
public void openDungeonDoor() {
	gMap.openDungeon();
	
}
public String[] levelupStr = {"", "", "Wall Jump!", "Double Jump!", "", "", "", "", "", "", "", "", ""};
public void showLevelupMessage(int level) {
	openMessageWindow(levelupStr[level]);
	
}
//int genTimeLeft;
@Override
protected void drawChunkMap() {

		//genString.
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		batch.getProjectionMatrix().setToOrtho2D(0, 0, RESX, RESY);
		batch.begin();
		font.draw(batch, genString.toString(), 0, 100);
		int pc = (genProgress*100) / genTotal;
		pc = MathUtils.clamp(pc,  0, 1000);
		font.draw(batch, numberStrings[pc], 0, 70);
		
		int timeLeft = (int)(( (genTime - genTimeLast)* (100-((genProgress * 100) / genTotal)) )/1000);
		timeLeft = MathUtils.clamp(timeLeft, 0, 1000);
		timeLeft /= 60;
		font.draw(batch, numberStrings[timeLeft], 0, 40);
		batch.end();
		
	
	
}
public void useDoor(Player player, Door d) {
	if (d.destPlane != PunkMap.currentPlane){
		PunkMap.currentPlane = d.destPlane;
		processing = true;
		processingInc = 0;
		gameMode = 66;
		gMap.chunkPool.fetchWorld(d.dest.x, d.dest.y, d.destPlane, player);
		//Chunk.activePlane = Chunk.planes.get(PunkMap.currentPlane);
		int itemLimit = Chunk.planes.get(d.destPlane).itemLimit;
		if (itemLimit != -1){
			//player.storeInventory(itemLimit);
			
		}
		/*if (Chunk.planes.get(d.destPlane).creativeItems){
			slotBtns[PunkInventory.INVENTORYSIZE].id = -4;
			slotBtns[PunkInventory.INVENTORYSIZE+1].id = -5;
			//Gdx.app.log(TAG, "creative items");
			slotBtns[PunkInventory.INVENTORYSIZE].populateCreativeItems();
			
		}*/
	}
	player.body.setTransform(d.dest.x+.5f, d.dest.y+.5f, 0);
	player.activeDoor = null;
}

//protected void drawPlayer2(){
//	
//	float angle = MathUtils.radiansToDegrees *player.body.getAngle(); // the rotation angle around the center
//	CorneredSprite playerS;
//	if (!player.isFlashing || (int)(player.stateTime / FLASHINTERVAL)%2 == 1)
//		playerS= player.getFrame(monsterIndex, 16);
//	else playerS= player.getFrame(monsterIndex, 16);
//	//batch.draw(playerS, player.position.x-1, player.position.y-.25f, 2, 2);
//	
//		playerS.setPosition(player.position.x-1, player.position.y-.25f);
//		playerS.setRotation(angle);
//		playerS.setCorners(player.lightBits, player.dayBits);
//		playerS.draw(batch);
//	
//	
//	
//	
//	if (player.isOnFire){
//		//Sprite fs = monsterIndex.getFireFrame(player.stateTime);
//		//fs.setPosition(player.position.x-1, player.position.y);
//		//fs.draw(batch);
//		batch.draw(monsterIndex.getFireFrame(player.stateTime), player.position.x-1f, player.position.y, 1, 1, 2, 2, 1, 1, 0);
//	}
//
//	if (false && player.isDigging)
//	{
//		//transTmp.set(targetBlock.x, targetBlock.y, 0);
//		//camera.project(transTmp, 0, 0, RESX, RESY);
//		//batch.draw(monsterIndex.digAnim.getKeyFrame(player.stateTime, true), targetBlock.x, targetBlock.y);
//		int blockX = (int)targetBlock.x;
//		int blockY =(int)targetBlock.y;
//		Block b = gMap.getBlock(blockX, blockY );
//		
//		//Sprite airBlock = PunkBodies.terrainS[0][b.light];
//		//airBlock.setColor(skyColor*(.66f/2),skyColor*.66f,skyColor,1);
//		//airBlock.setPosition(blockX,  blockY);
//		//airBlock.draw(batch);
//		batch.draw(PunkBodies.getItemFrame(b.blockID,15), 
//				(long)targetBlock.x, (int)targetBlock.y, // the bottom left corner of the box, unrotated
//				  .5f, .5f, // the rotation center relative to the bottom left corner of the box
//				  1, 1, // the width and height of the box
//				  1, 1, // the scale on the x- and y-axis
//				  player.stateTime*80f);
//				 
//		if (player.getActiveID() != 0)batch.draw(PunkBodies.getItemFrame(player.getActiveID(),Player.inventory.getItemMeta(player.activeQuickSlot)), 
//				player.position.x-touchLoc.x-.5f,//-(player.isLeft?0:1), 
//				player.position.y+.5f-touchLoc.y, 
//				.5f, .5f, 1, 1, 1, 1, touchLoc.angle()+(touchLoc.angle() > 90 && touchLoc.angle() < 270?180:-270));
//	}
//	if (gameMode == 2 || gameMode == 7){
//		if (player.isPlacingBlock)
//		{
//			//batch.setColor(1f, 1f, 1f, .5f);
//			
//			CorneredSprite hs = PunkBodies.getItemFrame(Player.inventory.getItemID(player.activeQuickSlot),Player.inventory.getItemMeta(player.activeQuickSlot));
//			hs.setPosition(player.hand.position.x-.5f, 
//					player.hand.position.y-.3f);
//			//hs.setOrigin(.5f, .5f);
//			hs.setRotation( +(player.isLeft?touchLoc.angle()+180:touchLoc.angle()));
//			//hs.setScale(1, player.isLeft?1:1);
//			hs.setCorners(player.lightBits, player.dayBits);
//			hs.draw(batch);hs.setRotation(0);hs.setScale(1,1);
//			
//			batch.setColor(1f, 1f, 1f, 1);
//			highlightBlock(placeTargetBlock);
//			
//			
//			/*batch.draw(PunkBodies.getItemFrame(player.inventory.getItemID(player.activeQuickSlot),player.inventory.getItemMeta(player.activeQuickSlot))[15], 
//					MathUtils.floor(targetBlock.x), 
//					MathUtils.floor(targetBlock.y)
//					, 1, 1, 1, 1,1,1,0);*/
//			//batch.draw(monsterIndex.crossS, rayA.x, rayA.y, .1f, .1f);
//			//batch.draw(monsterIndex.crossS, rayB.x, rayB.y, .1f, .1f);
//			//batch.draw(monsterIndex.crossS, globalTouchedV.x, globalTouchedV.y, .1f, .1f);
//			
//			//batch.draw(monsterIndex.crossS, touchBlockV.x, touchBlockV.y, .1f, .1f);
//			//batch.draw(monsterIndex.crossS, player.position.x, player.position.y + Player.EYEHEIGHT, .1f, .1f);
//		} else {
//			//batch.setColor(1f, 1f, 1f, .5f);
//			
//			CorneredSprite hs = PunkBodies.getItemFrame(Player.inventory.getItemID(player.activeQuickSlot),Player.inventory.getItemMeta(player.activeQuickSlot));
//			hs.setPosition(player.hand.position.x-.5f, 
//					player.hand.position.y-.3f);
//			//hs.setOrigin(.5f, .5f);
//			hs.setRotation( +(player.isLeft?touchLoc.angle()+180:touchLoc.angle()));
//			hs.setScale(1, player.isLeft?-1:1);
//			hs.setCorners(player.lightBits, player.dayBits);
//			hs.draw(batch);hs.setRotation(0);hs.setScale(1,1);
//			//batch.draw
//		}
//	}
//	
//	if (player.isShootingWand){
//		player.wandParticle.setPosition(player.hand.position.x, player.hand.position.y);
//		player.wandParticle.draw(batch);
//		Gdx.app.getInput().vibrate(MathUtils.random(2));
//	}
//	
//	if (player.isWallSliding && player.wallJumpsTot>0){
//		player.slideParticle.setPosition(player.x+(player.isLeft?0:1), player.position.y-.3f);
//		player.slideParticle.draw(batch);
//		Gdx.app.getInput().vibrate(MathUtils.random(2));
//	}
//	
//	if (player.isAimingAxe){
//		Sprite axeS = adjustedTouchAngle < 270 && adjustedTouchAngle > 90?monsterIndex.axeS[player.activeAxe]:monsterIndex.axeSL[player.activeAxe];
//		axeS.setRotation(adjustedTouchAngle);
//		axeS.setPosition(player.position.x, player.position.y+1);
//		float br = (float)(gTime-player.shootTimer)/(float)Player.PREDELAY;
//		br = Math.min(1,br);
//		axeS.setColor(br,br,br,1);
//		axeS.draw(batch);
//	} else if (player.isAxeing){
//		Sprite axeS = player.axeIsLeft?monsterIndex.axeS[player.activeAxe]:monsterIndex.axeSL[player.activeAxe];
//		axeS.setRotation(player.axe.body.getAngle()*MathUtils.radiansToDegrees);
//		axeS.setPosition(player.position.x, player.position.y+1);
//		axeS.setColor(1,1,1,1);
//		//monsterIndex.axeS.setScale(player.axeIsLeft?1:-1,1);
//		axeS.draw(batch);
//	}
//	/*Vector2 position = player.position; // that's the box's center position
//	float angle = MathUtils.radiansToDegrees *player.body.getAngle(); // the rotation angle around the center
//	batch.draw(player.getFrame(monsterIndex), 
//				  position.x - .5f, position.y, // the bottom left corner of the box, unrotated
//				  1f, 1f, // the rotation center relative to the bottom left corner of the box
//				  1, 2, // the width and height of the box
//				  1, 1, // the scale on the x- and y-axis
//				  angle); // the rotation angle*/
//	
//	
//	
//	//transTmp.set(player.position.x+(player.isLeft?-.5f:-.5f), player.position.y-.125f, 0);
//	//camera.project(transTmp, 0, 0, RESX, RESY);
//	//player.screenPosition.set(transTmp.x, transTmp.y);
//	updateControls();
//	//batch.draw(player.getFrame(monsterIndex), transTmp.x, transTmp.y);
//	
//
//	////Gdx.app.log("drawPlayer, Punk", "player stateTime:"+player.stateTime);
//	
//	if (gameMode == 5)
//		{
//			
//		} 
//			else if (gameMode == 6 || gameMode == 9){
//				
//					//transTmp.set(player.position.x-touchLoc.x-.5f, 
//					//		player.position.y+.5f, 0);
//					//camera.project(transTmp, 0, 0, RESX, RESY);
//				
//				CorneredSprite hs = PunkBodies.getItemFrame(Player.inventory.getItemID(player.activeQuickSlot),Player.inventory.getItemMeta(player.activeQuickSlot));
//						
//				hs.setPosition(player.hand.position.x-.5f, 
//						player.hand.position.y-.5f);
//				hs.setRotation(adjustedTouchAngle+45+(player.isLeft?0:90));
//				hs.setScale((player.isLeft?1:-1), 1);
//				hs.setCorners(player.lightBits, player.dayBits);
//				hs.draw(batch);hs.setRotation(0);hs.setScale(1,1);
//						
//			}//GM69
//			else if (gameMode == 1){//throwing
//				//float offset = MathUtils.sin(player.position.x*4);
//				if (!player.isDoneThrowing){
//					float br = (float)(screenTime-player.shootTimer)/(float)Player.PREDELAY;
//					br = Math.min(1,br);
//					batch.setColor(br,br,br,1);
//					
//					CorneredSprite hs = PunkBodies.getItemFrame(Player.inventory.getItemID(player.activeQuickSlot),Player.inventory.getItemMeta(player.activeQuickSlot)); 
//							;
//					hs.setPosition(player.hand.position.x-.5f, 
//							player.hand.position.y-.5f);
//					hs.setRotation(adjustedTouchAngle+45+(player.isLeft?0:90));
//					hs.setScale((player.isLeft?1:-1), 1);
//					hs.setCorners(player.lightBits, player.dayBits);
//					hs.draw(batch);hs.setRotation(0);hs.setScale(1,1);
//					batch.setColor(1,1,1,1f);
//
//				}
//			}
//			
//	
//	
//	boolean skipHand = false;
//	
//	if ((player.isPoiing|| player.isAimingPoi)){
//		tmpV.set(player.poi.position);
//		int playerid = player.getActiveID();
//		if (!player.activeTool.hasChain) 
//			skipHand = true;
//		CorneredSprite s = PunkBodies.getItemFrame(Player.inventory.getItemID(player.activeQuickSlot),Player.inventory.getItemMeta(player.activeQuickSlot)); 
//		;
//		
//		s.setPosition(tmpV.x-1.5f, tmpV.y-1.5f);
//		float poiD = player.position.dst(player.poi.position);
//		float poiRot = adjustedTouchAngle + (player.isLeft?-45:45);//+ ((poiDistance*30 - 30) * (player.isLeft?1:-1))+(player.isLeft?-90:90);// + ((poiDistance / 6f)*180)-90;
//		//if (player.activeFlail.hasChain)poiRot = 0; 
//		float br = 1;//(float)(gTime-player.shootTimer)/(float)Player.PREDELAY;
//		br = Math.min(1,br);
//		if (!player.isPostPoi)
//			batch.setColor(br,br,br,1);
//		tmp.set(.32f,-.32f);
//		tmp.rotate(
//				player.isLeft?
//				poiRot+90+player.activeTool.angle
//				:poiRot-player.activeTool.angle
//				);
//	//monsterIndex.itemS[158].setRotation(25f);
//	//monsterIndex.itemS[158].draw(batch);
//		
//		s.setPosition(player.poi.position.x-.5f, player.poi.position.y-.5f);
//		s.setOrigin(.5f, .5f);
//		s.setSize(1,1);
//		s.setScale(player.isLeft?1:1,player.isLeft?1:-1);
//		s.setRotation(player.isLeft?poiRot+90+player.activeTool.angle:poiRot-90-player.activeTool.angle);
//		s.setCorners(player.lightBits, player.dayBits);
//	
//		s.draw(batch);
//		batch.setColor(1,1,1,1);
//
//		if (gameMode == 11 && (player.isAimingPoi || player.isPrePoi || player.isPoiing && !player.isPostPoi))highlightBlock(digTargetBlock); 
//		
//		/*tmp.set(.17f,-.15f);
//		if (player.isLeft)
//			tmp.rotate(poiRot);
//		else {
//			tmp.x *= -1;
//			tmp.rotate(180+poiRot);
//		}*/
//		if (gameMode == 11 || (gameMode == 81 && !player.activeTool.hasChain)){
//			
//		
//				 
//				Sprite hs = player.getHandSprite(monsterIndex); 
//		hs.setPosition(player.poi.position.x-.5f+tmp.x//+tmp.x
//				,player.poi.position.y-.5f+tmp.y
//				); 
//		//.5f, .5f, 1, 1, 
//		hs.setScale(1,1);//(player.isLeft?1:-1), (player.isLeft?-1:1));
//		hs.setRotation(0);//player.isLeft?poiRot+90+player.activeTool.angle:poiRot-90-player.activeTool.angle);//tmpV.angle()+(player.isLeft?180:0));
//		hs.draw(batch);hs.setRotation(0);hs.setScale(1,1);	
//		}	
//		//Gdx.app.log(TAG, "draw hand, tool");
//		//batch.draw(monsterIndex.getBlockSprites(9,1)[15], touchBlockV.x, touchBlockV.y, 1, 1);
//	}
//	
//	//hand
//	tmpV.set(player.hand.position.x-.5f, player.hand.position.y-.5f);
//	tmpV.sub(player.position.x, player.position.y+1);
//	if (!skipHand){
//		Sprite s = player.getHandSprite(monsterIndex); 
//			s.setPosition(player.hand.position.x-.5f, 
//			player.hand.position.y-.5f); 
//			//.5f, .5f, 1, 1, 
//			s.setScale((player.isLeft?1:-1), 1);
//			s.setRotation(tmpV.angle()+(player.isLeft?180:0));
//			s.draw(batch);
//	}
//	
//	if (player.isGliding){
//		tmpV.set(10*Player.zoomLevel, 0);
//		tmpV.rotate(adjustedTouchAngle);
//		Sprite s = player.getHandSprite(monsterIndex); 
//		s.setPosition(tmpV.x, tmpV.y); 
//		//.5f, .5f, 1, 1, 
//		s.setScale((player.isLeft?1:-1), 1);
//		s.setRotation(0);
//		
//				
//	}
//	/*Sprite s = player.getHandSprite(monsterIndex); 
//	s.setPosition(touchBlockV.x, touchBlockV.y); 
//	//.5f, .5f, 1, 1, 
//	s.setScale((player.isLeft?1:-1), 1);
//	s.setRotation(tmpV.angle()+(player.isLeft?180:0));
//	s.draw(batch);*/
//	if (player.isGliding){
//		player.activeFlight.anim.draw(player, batch);
//		tmpV.set(3*Player.zoomLevel, 0);
//		tmpV.rotate(Player.flyTouchAngle);
//		tmpV.add(player.position).add(0,Player.EYEHEIGHT);
//		Sprite s2 = player.getFlyArrowSprite(monsterIndex); 
//		s2.setPosition(tmpV.x, tmpV.y); 
//		//.5f, .5f, 1, 1, 
//		//s2.setScale((player.isLeft?1:-1), 1);
//		s2.setRotation(Player.flyTouchAngle);
//		s2.setScale(Player.zoomLevel*(player.isThrusting?1.5f:1));
//		s2.draw(batch);
//	}
//	if (gameMode == 2)
//	{
//		//ItemInfo inf = PunkBodies.getItemInfo(player.getActiveID(), player.getActiveMeta());
//		//if (inf.data instanceof TouchAction){
//			//TouchAction a = (TouchAction)(inf.data);
//		placeBlockAction.playerDraw(this, player, gMap, world, monsterIndex, batch);
//			
//		//}
//		//
//	}
//}
public static String saveDir = "mithrilminer/saves/";
public static String gameName;
public static StringBuilder path = new StringBuilder();

public static void getSaveLoc(StringBuilder saveLoc){
	saveLoc.setLength(0);
	saveLoc.append(saveDir);
	
	saveLoc.append(gameName);
	saveLoc.append('/');
}

public static void queueLine(Vector2 src, Vector2 dst) {
	//angleMale = b;
	lineSrc.set(src);
	lineDest.set(dst);
	drawAngle = true;
	
}



private static boolean angleMale;
public static int versionNumber = 1;
public static int prefs_CloudsPerChunk = 2;;


public static void qFingerHighlight() {
	drawFinger = true;
	
}
@Override
protected void openMenu() {
	// TODO Auto-generated method stub
	
}

public static void queueBlockHighlight(BlockLoc targetB) {
	blockHighlightQ.add(targetB);
}
//static boolean drawDirection = false;
//static float directionArrowAngle = 0;
public static void queueDirectionArrow(Vector2 src, float angle) {
	directionArrowQ.add(src.x);
	directionArrowQ.add(src.y);
	directionArrowQ.add(angle);
	//directionArrowSrc.set(src);
	//directionArrowAngle = angle;
	//drawDirection = true;
}
public static void queueInvalidBlockHighlight(BlockLoc targetB) {
	invalidBlockHighlightQ.add(targetB);
	
}















}


/*
 * //reflected is whether it's the solid block, or the surface block that gets returned
	float Xa0, Ya0, Xa1, Ya1;
	  //Xa = 1/tan(s)
	  //where s is angle of passed ray
	  
	 // A is first intersection pt
	  
	 // P is source
	  
	   
	  //horizontal intersections(will always be on top or underneath)
	tmpBV.set(-1, -1);
	float s = dest.tmp().sub(P).angle() % 360;
	boolean isUp = dest.y > P.y, isLeft = dest.x < P.x, hdone = false, vdone = false;
	s *=  MathUtils.degreesToRadians;
	Gdx.app.log("raya   ", "s = "+s);
	Ya0 = isUp?1:-1;
	Xa0 = -(float) (1f/Math.tan(s));// * (isUp?1:-1);
	A.y = MathUtils.floor(P.y)+(isUp?-1:0);
	A.x =(float) (P.x + (P.y-A.y)/Math.tan(s));
	A.add(.2f, .2f);
	rayA.set(-1,-1);
	rayB.set(-1,-1);
	Gdx.app.log("rayc", "Xa "+Xa0+"  Ya "+Ya0);
	boolean done = false;
	//if (Math.abs(P.x-dest.x) > 3 )
	while (!hdone && !done){//horiz
		float dst = A.dst2(P);
		if ( checkBlock(MathUtils.floor(A.x), MathUtils.floor(A.y), true)){
				Gdx.app.log("punk", "done "+A);
			
			if (dst > minRange) {
				if (reflected)
					rayA.set(A.x, A.y);
					
				else rayA.set(A);
				hdone = true;
			}
			
		
		
		}
		if (dst > range)done = true;
		//Gdx.app.log("rayaaaa", "A "+A+Xa+" "+Ya);
		A.add(Xa0, Ya0);
		
	}
	
	Ya1 = (float) Math.tan(s);// * (isLeft?-1:1);
	Xa1 = isLeft?-1:1;
	A.x = MathUtils.floor(P.x)+(isLeft?0:-1);
	A.y = P.y + (P.x-A.x)*(float)-Math.tan(s);
	A.add(.2f, .2f);
	Gdx.app.log("rayc 2", "Xa "+Xa1+"  Ya "+Ya1);
	done = false;
	//if (Math.abs(P.y-dest.y) > 3 )
	while (!vdone && !done){//vertical
		
		float dst = A.dst2(P);
		if (checkBlock(MathUtils.floor(A.x), MathUtils.floor(A.y), true)){
				//Gdx.app.log("punk", "line, add sx");
			//float dst = A.dst2(P);
			if (dst > minRange) {
				if (reflected)
					rayB.set(A.x, A.y);
					
				else 
					rayB.set(A);
				vdone = true;
				
			}
			
		
		
		}
		if (dst > range)done = true;
		//Gdx.app.log("rayc", "A "+A+Xa+" "+Ya);
		A.add(Xa1, Ya1);
	}
	
	//compare 2 blocks, find closest
	if (!hdone && !vdone){}
	else
	if (!hdone){
		if (rayA.dst2(P) < range)
			tmpBV.set(rayB).sub(Xa1, 0);
	} else
		if (!vdone){
			if (rayB.dst2(P) < range)
				tmpBV.set(rayA).sub(0, Ya0);
	} else {
		if (rayA.dst2(P) < rayB.dst2(P)){
				Gdx.app.log("rayc", "cpmpuiuoituio"); 
				tmpBV.set(rayA).sub(0,Ya0);
		}
		else tmpBV.set(rayB).sub(Xa1, 0);
	}
	
	//Gdx.app.log("rayc", "returning "+tmpBV + "dest " + dest);
	return tmpBV;//.set(MathUtils.floor(tmpBV.x), MathUtils.floor(tmpBV.y));//tmpBV;
	//return rayB;
	
	
	
	
	
	/88888888
	
	
	
	//reflected is whether it's the solid block, or the surface block that gets returned
	float Xa0, Ya0, Xa1, Ya1;
	  //Xa = 1/tan(s)
	  //where s is angle of passed ray
	  
	 // A is first intersection pt
	  
	 // P is source
	  
	   
	  //horizontal intersections(will always be on top or underneath)
	//Vector2 v = dest.tmp().sub(P);
	float m = dest.tmp().sub(P).y / dest.tmp().sub(P).x;
	float c = m*P.x+P.y;
	
	tmpBV.set(-1, -1);
	//float s = dest.tmp().sub(P).angle() % 360;
	
	boolean isUp = dest.y > P.y, isLeft = dest.x < P.x, hdone = false, vdone = false;
	
	//s *=  MathUtils.degreesToRadians;
	//Gdx.app.log("raya   ", "s = "+s);
	Ya0 = isUp?1:-1;
	//Xa0 = -(float) (1f/Math.tan(s));// * (isUp?1:-1);
	A.y = MathUtils.floor(P.y)+(isUp?-1:0);
	A.x = - (A.y - c)/m  ;//(float) (P.x + (P.y-A.y)/Math.tan(s));
	Gdx.app.log("rayCCCCC " , "initial A "+A);
	hmod.set((Ya0)/m, Ya0);
	//A.add(.2f, .2f);
	rayA.set(-1,-1);
	rayB.set(-1,-1);
	Gdx.app.log("rayc   ", "m"+m+" c"+c);
	boolean done = false;
	//if (Math.abs(P.x-dest.x) > 3 )
	while (!hdone && !done && m != 0f && Math.abs(m) < 1000){//horiz
		float dst = A.dst2(P);
		if ( checkBlock(MathUtils.floor(A.x), MathUtils.floor(A.y), true)){
				//Gdx.app.log("punk", "done "+A);
			
			if (dst > minRange) {
				if (reflected)
					rayA.set(A.x, A.y);
					
				else rayA.set(A);
				hdone = true;
			}
		
		}
		if (dst > range)done = true;
		Gdx.app.log("rayaaaa", "A "+A);
		A.add(hmod);
		
	}
	
	//Ya1 = (float) Math.tan(s);// * (isLeft?-1:1);
	Xa1 = isLeft?-1:1;
	A.x = MathUtils.floor(P.x)+(isLeft?0:-1);
	A.y = -(m * A.x + c);//P.y + (P.x-A.x)*(float)-Math.tan(s);
	A.add(.2f, .2f);
	vmod.set(Xa1, m*Xa1);
	//Gdx.app.log("rayc 2", "Xa "+Xa1+"  Ya "+Ya1);
	done = false;
	//if (Math.abs(P.y-dest.y) > 3 )
	while (!vdone && !done && m != 0f && Math.abs(m) < 1000){//vertical
		
		float dst = A.dst2(P);
		if (checkBlock(MathUtils.floor(A.x), MathUtils.floor(A.y), true)){
				//Gdx.app.log("punk", "line, add sx");
			//float dst = A.dst2(P);
			if (dst > minRange) {
				if (reflected)
					rayB.set(A.x, A.y);
					
				else 
					rayB.set(A);
				vdone = true;
				
			}
			
		
		
		}
		if (dst > range)done = true;
		Gdx.app.log("rayc", "A "+A);
		A.add(vmod);
	}
	
	//compare 2 blocks, find closest
	if (!hdone && !vdone){}
	else
	if (!hdone){
		if (rayA.dst2(P) < range)
			tmpBV.set(rayB).sub(Xa1, 0);
	} else
		if (!vdone){
			if (rayB.dst2(P) < range)
				tmpBV.set(rayA).sub(0, Ya0);
	} else {
		if (rayA.dst2(P) < rayB.dst2(P)){
				Gdx.app.log("rayc", "cpmpuiuoituio"); 
				tmpBV.set(rayA).sub(0,Ya0);
		}
		else tmpBV.set(rayB).sub(Xa1, 0);
	}
	
	//Gdx.app.log("rayc", "returning "+tmpBV + "dest " + dest);
	return tmpBV;//.set(MathUtils.floor(tmpBV.x), MathUtils.floor(tmpBV.y));//tmpBV;
	//return rayB;
	

	
	
	
	
	
	
*/

//protected Vector2 findDigTargetbresold(World world, PunkMap map, Vector2 target, boolean blockFlag, int range){
//	int x0 = player.x;
//	int y0 = player.y+1;
//	//tmpV.set(direction).mul(player.DIGRANGE);//.mul(-1);
//	//tmpV.add(player.position);
//	int x1 = (int)(target.x);;
//	int y1 = (int)(target.y);
//	//Gdx.app.log("punk", "dig target. "+x1+", "+y1);
//	int dx = Math.abs(x1-x0), sx = x0<x1 ? 1 : -1;
//	int dy = Math.abs(y1-y0), sy = y0<y1 ? 1 : -1; 
//	int err = (dx>dy ? dx : -dy)/2, e2;
//	
//	//Gdx.app.log("punk", "touch angle:"+touchLoc.angle());
//	if (x0 == x1 && y0 == y1) return tmpV.set(-1,-1);
//	lastDigTargetBlock.set(-1,-1);
//	checkBlock(x0,y0, blockFlag);
//	int count = 0;
//	for(int reps = 0;reps != -1; reps++){
//		/*if (checkBlock(x0,y0, blockFlag)){
//			return (blockFlag?tmpV.set(lastDigTargetBlock.x, lastDigTargetBlock.y):tmpV.set(x0,y0));}*/
//		//if (x0==x1 && y0==y1) break;
//		if (count > range) break;
//		e2 = err;
//		if (e2 >-dx) { //extend
//			err -= dy; 
//			x0 += sx; 
//			count++;
//			if (checkBlock(x0,y0, blockFlag)){
//				//Gdx.app.log("punk", "line, add sx");
//				if (reps > 2) return (blockFlag?tmpBV.set(lastDigTargetBlock.x, lastDigTargetBlock.y):tmpBV.set(x0,y0+sy));
//
//				//return (tmpBV.set(lastDigTargetBlock.x, lastDigTargetBlock.y));
//				}
//		}
//		if (e2 < dy) { //extend
//			err += dx; 
//			y0 += sy; 
//			count++;
//			if (checkBlock(x0,y0, blockFlag)){
//				if (reps > 2) return (blockFlag?tmpBV.set(lastDigTargetBlock.x, lastDigTargetBlock.y):tmpBV.set(x0+sx,y0));
//				//return (tmpBV.set(lastDigTargetBlock.x, lastDigTargetBlock.y));
//	
//			}
//		}
//	}
//		
//	
//	
//	return tmpBV.set(-1,-1);
//}

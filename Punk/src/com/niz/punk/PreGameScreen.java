package com.niz.punk;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GLCommon;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;

public class PreGameScreen implements Screen, InputProcessor {

	//public BitmapFont menuFont;
	public SpriteBatch menuBatch;
	private int yres, xres;
	public Start starter;
	public Punk engine;
	public Preferences prefs;
	public boolean[] validGames = new boolean[6];
	public FileHandle[] handles = new FileHandle[6];
	private static String saveDir;// = "BlockHead/saves/";
	private FileHandle playerHandle;
	//private int selectedSlot, otherSelectedSlot, selectedPick, selectedTorches = 0;
	public int  dialog, loadCount;
	private AdWhirlViewHandler myRequestHandler;
	public Array<int[]> availableItems= new Array<int[]>(512);
	public IntArray selectedSlots = new IntArray();
	private int verticalTiles, horizontalTiles, horizSize, vertSize;
	private Vector3 origin = new Vector3(0,0,0), camPos = new Vector3(0,0,0);
	private long fadeTimer;
	private float alpha = 1f;
	private Camera cam;
	private GameInfo gi;
	private int gid;//game id
	public PreGameScreen(AdWhirlViewHandler handler){
		myRequestHandler = handler;		
		
	}
	
	public void create(SpriteBatch batch, Punk mainloop, Preferences pref){
		//Gdx.app.log("preg", "pregame initiated");
		Gdx.input.setInputProcessor(this);
		//menuFont =  starter.menuFont;
		engine = mainloop;
		
		prefs = pref;
		saveDir = "MithrilMiner/saves/";
		
		for (int i = 0; i < 6; i++){
			//handles[i]  new FileHandle();
			handles[i] = Gdx.files.external(saveDir + "game" + i);
			validGames[i] = handles[i].exists();
		}
		//availableItems = new Array(512);
		dialog = 0;//1=newgame confirm 2=load game confirm
		batch.getProjectionMatrix().setToOrtho2D(0f,0f,engine.RESX,engine.RESY);
		menuBatch = batch;
		//loadGame();
	}
	
	public void processValidItems(){
		availableItems.clear();
		/*for (int i = 0; i < 512; i++)
			if (gi.inv[i] > 0)
			{
				int[] newItem = {i, gi.inv[i]};
				availableItems.add(newItem);
			}*/
		
		//Gdx.app.log("preg", "process valid");

		
	}
	
	

	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {
		// TODO Auto-generated method stub
		//Gdx.app.log("preg", "touchdown, al:"+alpha);
		if (true == true){
			dialog = 1;
			
		}
		if (availableItems.size <= 4) dialog = 2;
		int sSlot = x/(xres/horizontalTiles)
				+ y/(yres/verticalTiles)*horizontalTiles;
		if (selectedSlots.contains(sSlot)) selectedSlots.removeValue(sSlot);
		else selectedSlots.add(sSlot);
		if (selectedSlots.size >= 4 || availableItems.size <= 4) {
			dialog = 1;
			//put stuff into the player's inventory
			if (selectedSlots.size <4){//defaults
				
			}else{//load from selections
				for (int i = 0; i < 4; i++){
					engine.player.inv.addItem(
							availableItems.get(selectedSlots.get(i))[0],
							availableItems.get(selectedSlots.get(i))[1]
					);
					//gi.inv[availableItems.get(selectedSlots.get(i))[0]]=availableItems.get(selectedSlots.get(i))[1];
				}
				//then write to info file
				String saveLoc = "MithrilMiner/saves/" +"game"+ gi.gameID + "/player.inf";
				
				try{
					ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(Gdx.files.external(saveLoc).write(false)));
					out.writeObject(gi);
					out.close();
				}catch (IOException ex){//Gdx.app.log("preg", "error saving player info, phase 2");}
				}
			}
			
		}
		
		return false;
	}
	

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}
	public void setStarter(Start thegame){
		starter = thegame;
		menuBatch = starter.spriteBatch;
	}
	public void setInfo(GameInfo info){
		gi = info;
		gid = info.gameID;
		//gid = gameIDFromNewG;
		processValidItems();
		//menuBatch = new SpriteBatch();
		//Gdx.app.log("preg", "new batch");

	}
	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int x, int y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void render(float delta) {
//Gdx.app.log("preg", "render"+dialog);
		GLCommon gl = Gdx.gl;
		gl.glClearColor(0, 0, 0, 1);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		yres = Gdx.graphics.getHeight();
		xres = Gdx.graphics.getWidth();
		menuBatch.setColor(1,1,1,alpha);
		menuBatch.begin();
	
		////Gdx.app.log("preg", "dialog:"+dialog);

		switch (dialog)//0 = select, 1= fade out, 2= fade in
		{
		case 1://fade out
			if (System.currentTimeMillis() > fadeTimer){
				fadeTimer = System.currentTimeMillis()+50;
				alpha -= .01;
				//Gdx.app.log("preg", "alpha:"+alpha);

				if (alpha <= 0) {
					dialog = 2;
					//Gdx.app.log("preg", "alpha reached 0");
				}
			}
		case 0:	
			
			//if (dialog == 0)
			//	menuFont.drawMultiLine(menuBatch, "Select 4 Items", 0, yres-(yres/8), xres, HAlignment.CENTER);
			//draw squares under selected stuff
			
			for (int i = 0; i < availableItems.size; i++){
				int[] anI = (int[])availableItems.get(i);
				menuBatch.draw(engine.monsterIndex.getItemFrame(anI[0], 0), 
						i/(verticalTiles), i&(verticalTiles), 
						0, 0, 
						engine.TILESIZE, engine.TILESIZE, 
						2, 2, 
						(selectedSlots.contains(i)?0:45));
				}
			//menuFont.drawMultiLine(menuBatch, "BACK", 0, (yres/8)*1+16, xres, HAlignment.CENTER);
			break;
		
		case 2://fade in
			cam.position.set(engine.player.head.getPos().x, engine.player.head.getPos().y, 0);

			//origin.set((int)(cam.position.x-1)-engine.BRADIUSX,(int)(cam.position.y)-engine.BRADIUSY,0); 
			cam.project(origin);
			//menuBatch.end();
			//menuBatch.setColor(.31f,.31f,.31f,.2f);
			//menuBatch.begin();
			engine.renderMap(engine.world, menuBatch, origin, camPos, engine.monsterIndex, cam);
			if (System.currentTimeMillis() > fadeTimer){
				fadeTimer = System.currentTimeMillis()+50;
				alpha += .01;
				//Gdx.app.log("preg", "alpha incremented");

				if (alpha >= 1) starter.setScreen(engine);
			}
			break;
		}
		menuBatch.end();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

}

package com.niz.punk;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GLCommon;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class MainMenu implements Screen, InputProcessor {
	private Game game;
	public SpriteBatch menuBatch;
	public Sprite creativeS, survivalS, psychonautS;
	public BitmapFont menuFont;
	private int yres, xres, selectedGameType = 1, validGameTypes;
	public Start starter;
	public boolean hasMultipleSaves = false, newGameSelected = false, beenTouched = false;
	public long pointerTimer = 0;
	
	
	public Group g;
	public MainMenu (){
		
	}
	
	public void create(SpriteBatch batch){
		yres = Gdx.graphics.getHeight();
		xres = Gdx.graphics.getWidth();
		//GL10 gl = Gdx.graphics.getGL10();
		hasMultipleSaves = false;
		newGameSelected = false;
		beenTouched = false;
		menuBatch = batch;
		float aR = (float)xres/(float)yres;
		menuBatch.getProjectionMatrix().setToOrtho2D(0f,0f,4,4f/aR);
		Gdx.input.setInputProcessor(this);
		menuFont =  starter.menuFont;//new BitmapFont(Gdx.files.internal("data/font.fnt"), Gdx.files.internal("data/font_0.png"),false);
		//menuFont.HAlignment = CENTER;
		//menuBatch.getProjectionMatrix().setToOrtho2D(10,10, 10, 10);
		//gl.glClearColor(1f,1f,1f,1);
		validGameTypes = starter.prefs.getInteger("validGameTypes");
		
		creativeS = new Sprite(new Texture(Gdx.files.internal("data/startcreative.png")));
		survivalS= new Sprite(new Texture(Gdx.files.internal("data/startsurvival.png")));
		psychonautS= new Sprite(new Texture(Gdx.files.internal("data/startpsychonaut.png")));
		creativeS.setSize(1,1);
		survivalS.setSize(1,1);
		psychonautS.setSize(1,1);
		creativeS.setPosition(0, 1.5f/aR);
		survivalS.setPosition(1, 1.5f/aR);
		psychonautS.setPosition(2, 1.5f/aR);
		
		if (validGameTypes == 0){
			creativeS.setColor(1,1,1,1);
			survivalS.setColor(.5f,.5f,.5f,.5f);
			psychonautS.setColor(.5f,.5f,.5f,.5f);
		} else if (validGameTypes == 1){
			creativeS.setColor(1,1,1,1);
			survivalS.setColor(1,1,1,1);
			psychonautS.setColor(.5f,.5f,.5f,.5f);
		} else{
			creativeS.setColor(1,1,1,1);
			survivalS.setColor(1,1,1,1);
			psychonautS.setColor(1,1,1,1);
		}
			
		pointerTimer = System.currentTimeMillis()+100000;
		
		
		//////////////////////////
		

	}
	public void setStarter(Start thegame){
		starter = thegame;
	}
	
	@Override
	public void dispose() {
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
	public void render(float deltaTime) {
		// TODO Auto-generated method stub
		GLCommon gl = Gdx.gl;
		gl.glClearColor(0, 0, 0, 1);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		/*menuBatch.begin();
		//menuBatch.draw(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)
		
		creativeS.draw(menuBatch);
		survivalS.draw(menuBatch);
		psychonautS.draw(menuBatch);
		
		//menuFont.drawMultiLine(menuBatch, "NEW", 0, (yres/8)*7+16, xres, HAlignment.CENTER);
		//menuFont.drawMultiLine(menuBatch, "PLAY", 0, (yres/8)*4+16, xres, HAlignment.CENTER);
		//menuFont.drawMultiLine(menuBatch, "SETTINGS", 0, (yres/8)*3+16, xres, HAlignment.CENTER);
		//menuFont.drawMultiLine(menuBatch, "EXIT", 0, (yres/8)*1+16, xres, HAlignment.CENTER);

		menuBatch.end();
		if (Gdx.input.isButtonPressed(0) && System.currentTimeMillis() > pointerTimer+2000){
			newGameSelected = true;
			Gdx.app.log("mainmenu", "new game selected"+selectedGameType);
			//8/9
			if (selectedGameType == 0)starter.mode = 2;
			else if (selectedGameType == 1) starter.mode = 8;
			else starter.mode = 9;
		}*/
		
		//startStage.act(deltaTime);
		
		//startStage.draw();
		
	}

	@Override
	public void resize(int arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		//menuBatch.getProjectionMatrix().setToOrtho2D(0f,0f,512,288);

	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}
	@Override
	public boolean keyDown(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean keyTyped(char arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean keyUp(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean scrolled(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean touchDown(int x, int y, int ptr, int btn) {
		/*if (y > (yres/4)*3)
		{//bottom
			//exit!
			System.out.println("exit");			
		} else
			if (y > yres/2)
			{//third
				starter.mode = 0;
				System.out.println("mode = " + starter.mode);				
			} else
				if (y <(yres/4))
				{//second
					//starter.mode = 3;
					
					System.out.println("mode = " + starter.mode);					
				} else
				{//top
					System.out.println("mode = " + starter.mode);
					starter.mode =3;					
				}
		/*
		if (x <Gdx.graphics.getWidth()/8 && y > Gdx.graphics.getHeight() - Gdx.graphics.getHeight()/8) {
			starter.mode = 2;
		}
		
		else*/ 
		/*beenTouched = true;
		selectedGameType = 2;
		if (validGameTypes ==0) selectedGameType = 0;
		if (validGameTypes == 1) selectedGameType = 1;
		if (x < xres/2 && validGameTypes >=1) selectedGameType = 1;
		if (x < xres/4) selectedGameType = 0;
		pointerTimer = System.currentTimeMillis();*/
		return false;
	}
	@Override
	public boolean touchDragged(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean mouseMoved(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean touchUp(int x, int y, int arg2, int arg3) {
		// TODO Auto-generated method stub
		if (!beenTouched) return true;
		if (System.currentTimeMillis() < pointerTimer + 2000) newGameSelected = false;
		if (x < xres/4)starter.mode = 3;//new creative
		else if (x < xres/2 && validGameTypes >=1){
			starter.mode = 6;
			Gdx.app.log("mainmenu", "survival pressed");
		}
		else if (validGameTypes >=2)starter.mode = 7;
		Gdx.app.log("mainmenu", "MODE = "+starter.mode);
		return false;
	}

}

package com.niz.punk;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class LoadScreen implements Screen {
	private Game game;
	public LoadScreen (Game thegame){
		game = thegame;
	}
	BitmapFont font;
	String[] nums = new String[101];
	SpriteBatch batch;
	Sprite dot;
	public float prog;
	public void set(BitmapFont menuFont, SpriteBatch batch, Sprite dot){
		this.dot = dot;
		this.batch = batch;
		this.font = menuFont;
		for (int i = 0; i < 101; i++){
			nums[i] = ""+i;
		}
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
	public void render(float arg0) {
		
		batch.begin();
		//font.draw(batch, nums[MathUtils.clamp(prog, 0, 100)], 50, 50);
		dot.setSize(prog, .2f);
		dot.draw(batch);
		batch.end();

	}

	@Override
	public void resize(int arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

}

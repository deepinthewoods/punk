package com.niz.punk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

public class SkillButton extends Table{
	public int id;
	static NinePatch back9;
	SpriteDrawable draw = new SpriteDrawable(){
		
		public void draw(SpriteBatch batch, float x, float y, float width, float height){
			super.draw(batch, x, y, width, height);
			back9.draw(batch, x, y, width, height);
		}
		@Override
		public void setSprite(Sprite s){
			s.setSize(getWidth(), getHeight());
			super.setSprite(s);
		}
	};
	public void set(int i){
		id = i;
		Gdx.app.log("skillbtn", "i = "+i);
		draw.setSprite(PunkBodies.getSkillSprite(id));
		
	}
	public SkillButton(){
		this.setBackground(draw);
		addListener(Punk.skillClicker);
	}
}

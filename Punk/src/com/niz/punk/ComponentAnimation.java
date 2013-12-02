package com.niz.punk;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.IntArray;

public class ComponentAnimation implements Component {
	
	public boolean isLooping, isComposite;
	public float sizeX, sizeY, offX=1f, offY=.5f;
	@Override
	public void act(GenericMob mob, PunkMap map, Player player,
			PunkBodies monsterIndex, World world) {
		// TODO Auto-generated method stub
		
	}
	public void dispose(){
		
	}
	public void draw(GenericMob mob, SpriteBatch batch){
		CorneredSprite s;
		//mob.light = 15;
		//Gdx.app.log("anim", ""+name);
		//if (mob.isLeft){
		//	s = anim.getKeyFrame(mob.stateTime, isLooping);
		//} else s = animR.getKeyFrame(mob.stateTime, isLooping);
		//s.setColor(PunkBodies.colors[mob.light]);
		//s.setBounds(mob.position.x-offX-mob.xSize, mob.position.y-offY, sizeX, sizeY);
		//s.setCorners(mob.lightBits, mob.dayBits);
		//if (mob.isFrozen)s.setColor(Color.BLUE);
		//else if (mob.isSlowed)s.setColor(Color.GRAY);
		//else if (mob.isHeld)s.setColor(Color.GREEN); 
		//s.draw(batch); 
	}
	
	public void drawItem(GenericMob mob, SpriteBatch batch) {

	}
	CorneredSprite[] tmpSpriteFrames = new CorneredSprite[17];
	public String name;
	public void set(float delta, boolean isLooping, TextureAtlas atlas, String name, float sizeX, float sizeY){
		///this.anim = anim;
		/*this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.isLooping = isLooping;
		this.name = name;
		
		
			int frame = 0;
			boolean done = false;
			do{
				tmpSpriteFrames[frame] =  atlas.createCorneredSprite(name+frame+"f");
				if (tmpSpriteFrames[frame] == null) done = true;
				else {
					tmpSpriteFrames[frame].setSize(sizeX,  sizeY);
				}
				frame++;
			} while (!done);
			frame--;//last frame
			
			animR = new CorneredSpriteAnimation(delta, tmpSpriteFrames, frame, 0);
			//animR[i].setDarkness(i/(float)PunkBodies.DARKFRAMES);
			
			for (int f = 0; f < frame; f++){
				tmpSpriteFrames[f].setSize(sizeX,  sizeY);
				tmpSpriteFrames[f].flip(true, false);
			}
			anim = new CorneredSpriteAnimation(delta, tmpSpriteFrames, frame, 0);
			//anim[i].setDarkness(i/(float)PunkBodies.DARKFRAMES);
		
		*/
	}
	public void queueDrawLayers(GenericMob mob, IntArray drawQ, Item[] i) {
		
		
	}
	
	protected Vector2 returnV = new Vector2();
	
	public Vector2 getHandROffset(int s){
		return returnV.set(0,0);
	}
	public Vector2 getHandLOffset(int s){
		return returnV.set(0,0);
	}
	public Vector2 getOriginOffset(int s) {
		return returnV.set(0,0);
	}
	
	
	
}

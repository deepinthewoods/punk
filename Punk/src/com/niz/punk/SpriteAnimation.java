package com.niz.punk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class SpriteAnimation {     
	   final Sprite[] keyFrames;
	   public float frameDuration;
	   
	   /**
	    * Constructor, storing the frame duration and key frames. 
	    * 
	    * @param frameDuration the time between frames in seconds.
	    * @param keyFrames the {@link TextureRegion}s representing the frames.
	    */
	   public SpriteAnimation(boolean flipx, float frameDuration, TextureRegion ... keyFrames){
		   this.frameDuration = frameDuration;
		   Sprite[] tmpKeyFrames = new Sprite[keyFrames.length];
		   for (int i = 0; i < keyFrames.length; i++){
			   Gdx.app.log("sspriteanim", "i:"+i);
			   tmpKeyFrames[i] = new Sprite(keyFrames[i]);
			   if (flipx) tmpKeyFrames[i].flip(true, false);
		   }
	       this.keyFrames = tmpKeyFrames;
	   }
	   public SpriteAnimation(boolean flipx, float frameDuration, Sprite s){
		   this.frameDuration = frameDuration;
		   Sprite[] tmpKeyFrames = new Sprite[1];
		   tmpKeyFrames[0] = s;
			   
	       this.keyFrames = tmpKeyFrames;
	   }
	  // public SpriteAnimation(float frameDuration, Sprite ... keyFrames) {
	   //    this.frameDuration = frameDuration;
	   //    this.keyFrames = keyFrames;
	   //}
	   
	   public SpriteAnimation(float frameDuration, AtlasRegion region, int frameSizeX, int frameSizeY){
		   this.frameDuration = frameDuration;
		   TextureRegion tmpR = region;
		   TextureRegion[] keyFrames = tmpR.split(frameSizeX, frameSizeY)[0];
		   Sprite[] tmpKeyFrames = new Sprite[keyFrames.length];
		   for (int i = 0; i < keyFrames.length; i++){
			   tmpKeyFrames[i] = new Sprite(keyFrames[i]);
			   //if (flipx) tmpKeyFrames[i].flip(true, false);
		   }
	       this.keyFrames = tmpKeyFrames;
		   
	   }
	   public SpriteAnimation(float frameDuration, Sprite[] keyFrames) {
		   this.frameDuration = frameDuration;
		   Sprite[] tmpKeyFrames = new Sprite[keyFrames.length];
		   for (int i = 0; i < keyFrames.length; i++){
			   tmpKeyFrames[i] = new Sprite(keyFrames[i]);
			   //if (flipx) tmpKeyFrames[i].flip(true, false);
		   }
		   this.keyFrames = tmpKeyFrames;
	   }
	   public SpriteAnimation(float frameDuration, TextureRegion[] keyFrames, int frameSizeX,
			int frameSizeY) {
		   this.frameDuration = frameDuration;

		   Sprite[] tmpKeyFrames = new Sprite[keyFrames.length];
		   for (int i = 0; i < keyFrames.length; i++){
			   tmpKeyFrames[i] = new Sprite(keyFrames[i]);
			   //if (flipx) tmpKeyFrames[i].flip(true, false);
		   }
	       this.keyFrames = tmpKeyFrames;
	}
	public SpriteAnimation(float frameDuration, Sprite[] keyFrames, int frameCount, int darkness, int extra) {
		this.frameDuration = frameDuration;
		Sprite[] tmpKeyFrames = new Sprite[frameCount];
		   for (int i = 0; i < frameCount; i++){
			   tmpKeyFrames[i] = new Sprite(keyFrames[i]);
		   }
		   this.keyFrames = tmpKeyFrames;
		
	}
	public void setOrigin(float x, float y){
		   for (Sprite spr: keyFrames)
			   spr.setOrigin(x,y);
	   }
	   
	   public void flip(boolean x, boolean y){
		   for (Sprite spr: keyFrames)
			   spr.flip(x,y);
	   }
	   /**
	    * Returns a {@link TextureRegion} based on the so called state time. 
	    * This is the amount of seconds an object has spent in the state this
	    * Animation instance represents, e.g. running, jumping and so on. The
	    * mode specifies whether the animation is looping or not. 
	    * @param stateTime the time spent in the state represented by this animation.
	    * @param looping whether the animation is looping or not.
	    * @return the TextureRegion representing the frame of animation for the given state time.
	    */
	   public Sprite getKeyFrame(float stateTime, boolean looping, float rotation, float x, float y){
		   Sprite s = getKeyFrame(stateTime, looping);
		   s.setRotation(rotation);
		   return s;
	   }
	   public Sprite getKeyFrame(float stateTime, boolean looping) {
	       int frameNumber = (int)(stateTime / frameDuration);
	       
	       if(!looping) {
	           frameNumber = Math.min(keyFrames.length-1, frameNumber);            
	       } else {
	           frameNumber = frameNumber % keyFrames.length;
	       }        
	       return keyFrames[frameNumber];
	   }  
	   public void setDarkness(float dark){
		  // for (int i = 0; i < keyFrames.length; i++)
		   for (Sprite spr: keyFrames)
			   spr.setColor(dark, dark, dark, 1);
	   }
	   public void setColor(Color col){
		   for (Sprite spr: keyFrames)
			   
			   spr.setColor(col);
	   }
	   public void flip(boolean f){
		   for (Sprite spr:keyFrames)
			   spr.flip(f, false);
	   }
	   public void setSize(float x, float y){
		   for (Sprite spr: keyFrames)
			   spr.setSize(x,y);
	   }
	   public void setRotation(float r){
		   for (Sprite spr: keyFrames)
			   spr.setRotation(r);
	   }
	public void setColor(float r, float g, float b, float a) {
		// TODO Auto-generated method stub
		for (int i = 0; i < keyFrames.length; i++)
			   keyFrames[i].setColor(r,g,b,a);
	}
	}
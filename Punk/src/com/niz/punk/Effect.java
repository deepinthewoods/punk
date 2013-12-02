package com.niz.punk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Pool;

public class Effect {
	public byte[] tint = new byte[4];
	public byte copy = 0;
	public byte particle = 0;
	public float time = 0f, maxTime;
	public int id = 0;
	//public boolean cumulative = false;
	public boolean draw(GenericMob mob, IntArray array){//returns true if it has queued it's own color/layer
		
		return false;
	}
	public void update(GenericMob mob){
		
		
	}
	public void onStats(GenericMob mob){
		
		
	}
	public void onEnd(GenericMob mob) {
		
		
	}
	public void applyTo(GenericMob mob, GenericMob src){
		if (time != 0f){
			mob.addTimedEffect(this);
			
				//Gdx.app.log("effect", "TIMED EFFECT FXFXFXFXFXFXFXFX");
			
		} else if (time == -1f){
			mob.addPermanentEffect(this);
		}
	}
	
	public Effect time(float t, float max){
		time = t;
		maxTime = max;
		return this;
	}
	
}

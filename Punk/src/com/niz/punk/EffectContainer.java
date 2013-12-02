package com.niz.punk;

import com.badlogic.gdx.utils.IntArray;

public class EffectContainer {
public Effect e;
private float time;

public boolean update(GenericMob mob, float delta){//returns true if it's time is up. 
	time -= delta;
	if (time < 0f){
		e.onEnd(mob);
		return true;
	}
	return false;
}
public void set(Effect e, float t){
	this.e = e;
	time = t;
	
}
}

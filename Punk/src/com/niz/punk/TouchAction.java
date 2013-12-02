package com.niz.punk;

import com.badlogic.gdx.physics.box2d.World;

///		//for items that do custom stuff


public abstract class TouchAction extends ButtonOverride{
public TouchAction(PunkMap map, PunkBodies mi, World world){
	this.map = map;
	this.world = world;
	this.mi = mi;
}
public static String[] infoDescs = {"place", "tool", "Tap screen while facing an enemy to attack.", "wearable", "ring", "throwable", "wand"};
public static final int PLACE = 0, TOOL = 1, MELEE = 2, WEARABLE = 3, RING = 4, THROWABLE = 5, WAND = 6;
int type=0;
public abstract boolean touchDown(GenericMob mob);
public abstract void touchUp(GenericMob mob);
public abstract void cancelTouch(GenericMob mob);
public abstract void beltSelected(GenericMob mob);
public abstract void onBeltUnselect(GenericMob mob);
public abstract void stats(GenericMob mob);

public boolean selectable  = true;
public int aiNeuronIndex = 1;
protected PunkMap map;
protected PunkBodies mi;
protected World world;
public ButtonOverride button;

//public String touchDesc = "touch description. ";
public abstract String getFinalDescription();

public String getTouchDesc(int playerClass) {
	return infoDescs[type];
}


}

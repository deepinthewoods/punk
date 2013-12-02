package com.niz.punk;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;

public abstract class ButtonOverride {
public boolean pressed = false;
public ButtonOverride sibling;
public boolean buttonLeft;

public abstract void unPress(PunkMap map, GenericMob mob, PunkBodies mi, boolean twoPresses);
public abstract void pressed(GenericMob mob, PunkMap gMap, World world,
		PunkBodies mi, boolean pressed, float deltaTime) ;
//public abstract void draw(float x, float y, float w, float h, SpriteBatch batch);//on paperdoll?

}

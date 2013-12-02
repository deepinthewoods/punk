package com.niz.punk;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class JugglingParticle {
Vector2 pos = new Vector2(), v = new Vector2();
public int data = 0;
static Vector2 g = new Vector2(0,-10);
CorneredSprite s;
//float g = -10;
float time;
int height, throwType;
public JugglingParticleBehavior behavior;
public JugglingComponentAnimation anim;
public PhysicsActor target;
public void setUp(){
	
		
}
public void step(float dt, PunkMap map){
	//float gx = 0;
	time += dt;
	behavior.act(this, map);
	v.add(g.tmp().mul(dt));
	pos.add(v.tmp().mul(dt));
}

public void draw(SpriteBatch batch){
	anim.draw(this, batch);
}
}

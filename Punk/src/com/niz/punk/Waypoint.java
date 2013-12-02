package com.niz.punk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class Waypoint {
public BlockLoc loc = new BlockLoc();
public int id, type, plane;
public String name = "wayPt";
public Sprite s;
public boolean built = false, discovered = false;
public int seed;
public static Array<Waypoint> list = new Array<Waypoint>();
	public Waypoint(int id, int type, String name, int x, int y, int plane){
		this.id = id;
		this.type = type;
		this.plane = plane;
		this.name = name;
		this.loc.set(x,y);
		this.s = PunkBodies.getWaypointSprite(x,y,type, plane);
		list.add(this);
		this.seed = MathUtils.random(0xffffffff, 0x0ffffffff);
	}
	public void draw(OrthographicCamera cam, SpriteBatch batch){
		if (!discovered) return;
		float scale = Player.zoomLevel;
		s.setScale(scale);
		s.draw(batch);
		//Gdx.app.log("waypt:", "draw"+s.getY()+"@"+Chunk.getGroundHeight((int) s.getX()));
	}



}

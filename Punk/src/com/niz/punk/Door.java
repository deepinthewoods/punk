package com.niz.punk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Door {
public int x,y;
public int anim = 0;
public float xOff, yOff;
public BlockLoc dest = new BlockLoc();
public int destPlane;
public int distanceFromPlayer;
public boolean open, revealed = true;
private int lightBits, dayBits;


	public void update(Player player, PunkMap map){
		distanceFromPlayer = player.distanceToPlayerHead(x, y);
		Block b = map.getBlock(x,y);
		lightBits = b.getLightBits();
		dayBits = b.getDayBits();
		//Gdx.app.log("door", "update"+x+","+y);
	}
	
	public void draw(SpriteBatch batch){
		//Gdx.app.log("door", "ANIM"+x+","+y);
		if (distanceFromPlayer > Punk.visibleDistanceFromPlayer) return;
		
		CorneredSprite s = PunkBodies.doorSpritesClosed.get(anim);
		s.setPosition(x+xOff,y+yOff);
		s.setCorners(lightBits, dayBits);
		s.draw(batch);
	}

	public void set(int x, int y, int id, int destX, int destY,
			int destPlane, float xOff, float yOff) {
		this.x = x;
		this.y = y;
		this.anim = id;
		//Gdx.app.log("door", "init"+anim);
		dest.set(destX, destY);
		this.destPlane = destPlane;
		this.xOff = xOff;
		this.yOff = yOff;
		
	}
	
}
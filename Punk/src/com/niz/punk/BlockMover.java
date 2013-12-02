package com.niz.punk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class BlockMover {
private int srcx, srcy;
private int id, meta;
private float timer;
private static final float DURATION = .5f;
private int lightBits, dayBits;
private int dx, dy;

public boolean update(PunkMap map, float delta){
	//returns true if complete
	//Gdx.app.log("mover", "update");
	timer -= delta;
	if (timer < 0){
		map.changeBlock(srcx+dx, srcy+dy, id, meta, true);
		
		Gdx.app.log("mover", "done");
		return true;
	}
	return false;
}

public void start(int srcx, int srcy, int dx, int dy, Block b){
	timer = DURATION;
	id = b.blockID;
	meta = b.meta;
	lightBits = b.getLightBits();
	dayBits = b.getDayBits();
	this.srcx = srcx;
	this.srcy = srcy;
	this.dx = dx;
	this.dy = dy;
	//this.dstx = dstx;
	//this.dsty = dsty;
	//Gdx.app.log("mover", "starting "+id+"  meta "+meta + "bits "+lightBits+dayBits);
}

public void draw(SpriteBatch batch, PunkBodies monsterIndex){
	float d = timer/DURATION;
	CorneredSprite s = monsterIndex.getBlockSprites(id, meta);
	//s.setBounds(srcx/d+dstx/(1-d)
	//		, srcy/d+dsty/(1-d)
	//		,
	//		1, 1);
	s.setBounds(srcx+(1-d)*dx
			, srcy+(1-d)*dy
			,
			1, 1);
	s.setCorners(lightBits, dayBits);
	s.draw(batch);
}
	
	
}

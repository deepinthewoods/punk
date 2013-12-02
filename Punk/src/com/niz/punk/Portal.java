package com.niz.punk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;



public class Portal {

public BlockLoc position = new BlockLoc();
public int plane, type;
public Portal target;
public PunkBlockList q = new PunkBlockList();
public static BlockLoc tmpL = new BlockLoc();
public void addBlock(int x, int y, int xOff, int yOff) {
	int rot = target.type - type;
	if (rot < 0) rot += 4;
	switch (rot){
	case 0:
		q.addBlock(xOff+position.x-x, yOff+position.y-y);
		break;
	case 1:
		q.addBlock(xOff+position.x+y, yOff+position.y-x);
		break;
	case 2:
		q.addBlock(xOff+position.x-x, yOff+position.y+y);
		break;
	case 3:
		q.addBlock(xOff+position.x+y, yOff+position.y+x);
		break;
	}
	//q.addBlock(xOff+x, yOff+y);
}

Vector3 tmpV = new Vector3();
Vector2 tmpV2 = new Vector2();
public void draw(OrthographicCamera camera, SpriteBatch batch, PunkMap map, GL20 gl, Player player){
	//set cam first
	batch.end();
	int rot = target.type - type;
	//rotation
	tmpV2.set(0,1);
	tmpV2.rotate(rot*90);
	camera.up.set(tmpV2.x, tmpV2.y, 0);
	
	//position
	
	tmpV2.set(target.position.x, target.position.y);
	tmpV2.sub(player.head.position);
	tmpV2.rotate(rot);
	tmpV2.add(player.head.position);

	camera.position.set(tmpV2.x, tmpV2.y, 0);
	
	camera.update();
	//camera.apply(gl);
	batch.begin();
	while (!q.list.isEmpty()){
		BlockLoc m = q.list.poll();
		Block tmpBlock = map.getBlock(m.x,m.y); 
		int renderTmp = tmpBlock.blockID;
		//int light = Math.max(tmpBlock.dayLight,tmpBlock.light);
		
		if (renderTmp != 0 || tmpBlock.meta != 0){
			CorneredSprite s =  PunkBodies.getBlockSprites(tmpBlock.blockID, tmpBlock.meta);;

				s.setBounds(m.x, m.y, 1, 1);
				//tmpBlock.sprite[1].setCorners(tmpBlock.lightBits, tmpBlock.dayBits);
				s.setCorners(tmpBlock.getLightBits(), tmpBlock.getDayBits());
				s.draw(batch);
		} 
	}
	
}

public boolean isVisible(Player player) {
	return (target != null && player.distanceToPlayer(position.x, position.y) < Punk.visibleDistanceFromPlayer);
}

public boolean isVisible(int x0, int y0, int x1, int y1, int px, int py) {
	//if (true) return true;
	boolean vis = target != null && position.x >=x0 && position.x <= x1 && position.y >=y0 && position.y <= y1;
	
	if (vis) 
		switch (type){
		case 0:
			if (py < position.y) vis = false;
			break;
		case 1:
			if (px > position.x) vis = false;
			break;
		case 2:
			if (py >= position.y)vis = false; 
			break;
		case 3:
			if (px < position.x)vis = false;
			break;
		}
		
	
	//if (!vis)Gdx.app.log("", target +"!= null && "+position.x +">="+x0 +" &&"+ position.x +"<= "+x1 +"& "+position.y+ " >="+y0 +"&&"+ position.y +" <= "+y1);
	return (vis);
	
}






}

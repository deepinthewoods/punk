package com.niz.punk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntArray;
public class LayeredAnimation extends ComponentAnimation{
	//[0=stocky][0=male][frames]
	public int NUM_FRAMES, NUM_LAYERS;
	public int packID;
	public CorneredSprite[][] layers;
	public float[] handLAngles, handRAngles;
	
	public LayeredAnimation(int frames, int layerc, int packID) {
		NUM_FRAMES = frames;
		NUM_LAYERS = layerc;
		this.layers = new CorneredSprite[NUM_LAYERS][NUM_FRAMES];
		handLAngles = new float[NUM_FRAMES];
		handRAngles = new float[NUM_FRAMES];
		this.packID = packID;
		
	}
	protected Vector2 tmpV = new Vector2();
	private int layerIndex = 0;
	@Override
	public void draw(GenericMob mob, SpriteBatch batch){
		CorneredSprite s;
		int f = mob.frame;
		boolean left = mob.isLeft;
		float x = mob.position.x, y = mob.position.y;
		//x = MathUtils.floor(mob.position.x * 16)*Punk.PIXELSIZE; y = MathUtils.floor(mob.position.y * 16)*Punk.PIXELSIZE;

		int copy = mob.copy;
		layerIndex = 0;
		
		for (int i = 0, tot = mob.drawQ.size/2; i < tot; i++){
			int layerID = mob.drawQ.get(i*2), colorID = mob.drawQ.get(i*2+1);
		
			
			
			s = layers[layerID][f];
			byte light = mob.blockC.getLight(), dayLight = mob.blockC.getDayLight();
			
			
			if (mob.rotateLayers.contains(layerID)){
				if (copy != 0) drawCopyAngled(batch, s, colorID, light, dayLight, x, y, left, mob.angle, mob.originOffset, copy);
				drawLayerAngled(batch, s, colorID, light, dayLight, x, y, left, mob.angle, mob.originOffset);
				
			} 
			else {
				if (copy != 0) drawCopy(batch, s, colorID, light, dayLight, x, y, left, 0f, copy);
				drawLayer(batch, s, colorID, light, dayLight, x, y, left, 0f);
				
			}		
		}
		
		
	}
	
	private void drawCopy(SpriteBatch batch, CorneredSprite s, int colorID,
			byte light, byte dayLight, float x, float y, boolean left, float f,
			int copy) {
		
		switch (copy){
		
		}
		
	}

	private void drawCopyAngled(SpriteBatch batch, CorneredSprite s,
			int colorID, byte light, byte dayLight, float x, float y,
			boolean left, float angle, Vector2 originOffset, int copy) {
		
		switch (copy){
		
		}
		
	}


	public void drawItem(GenericMob mob, SpriteBatch batch) {
		if (!mob.drawItem) return; 
		
		Item item = mob.inv.getItem(mob.activeInvSlot);
		if (item != null){
			CorneredSprite is = PunkBodies.getItemFrame(item.id, item.meta);
			CorneredSprite s = layers[34][mob.frame];
			ItemDef idef = PunkBodies.getItemInfo(item.id, item.meta);
			float angle = mob.angle, x = mob.position.x-idef.xOff, y = mob.position.y-idef.yOff;;
			if (mob.anim.states[mob.state].rotateAll || mob.anim.states[mob.state].rotateArm){
				
			} else angle = mob.isLeft?0:180;
			//angle = Punk.gTime*1000f;
			//angle %= 360;
			//angle += 180;
		//	angle = 0;
				Vector2 origin = mob.originOffset;
				if (mob.isLeft){
					is.setScale(idef.scale,idef.scale);
					//is.setSize(.1f, .1f);
					//s.setColorID(colorID, light, dayLight);
					//returnV.set(+(s.xOff), s.yOff).sub(origin).rotate(angle).add(origin);
					returnV.set(mob.handROffset);
					//mob.handROffset.set(returnV);
					returnV.add(x,y);
					is.setPosition(returnV.x, returnV.y);
					//Gdx.app.log("hum", "pos "+returnV);
					
					is.setOrigin(idef.xOff, idef.yOff);
					is.setRotation( angle + idef.angle + handRAngles[mob.frame]);
					
					is.draw(batch);
				} else {
					is.setScale(-idef.scale,idef.scale);					
					//s.setColorID(colorID, light, dayLight);
					//is.setSize(.1f, .1f);
					//angle -=180;
					if (angle < 0) angle += 360;
					//returnV.set(-(s.xOff), s.yOff).sub(origin).rotate(angle+180).add(origin);
					//mob.handROffset.set(returnV);
					returnV.set(mob.handROffset);
					returnV.x *= 1;
					returnV.add(x,y);
					is.setPosition(returnV.x, returnV.y);
					is.setOrigin(idef.xOff, idef.yOff);
					is.setRotation(180+(angle-idef.angle   - handRAngles[mob.frame]));

					is.draw(batch);
				}
				
				return;
			
			
		}
	}
	private void drawLayer(SpriteBatch batch, CorneredSprite s, int colorID, byte light, byte dayLight, float x, float y, boolean left, float jitter){
		s.setRotation(0);
		s.setOrigin(0,0);
		
		if (left){
			s.setScale(1,1);
			s.setPosition(x+(s.xOff)+jitter, y+s.yOff + jitter);
			returnV.set(0,0).sub ((s.xOff), s.yOff);
			s.setColorID(colorID, light, dayLight, layerIndex);
			s.draw(batch);
		} else {
			s.setScale(-1,1);
			s.setPosition(x-(s.xOff)+jitter, y+s.yOff+jitter);
			
			returnV.set(0,0).sub(-(s.xOff), s.yOff);
			s.setColorID(colorID, light, dayLight, layerIndex);
			s.draw(batch);
			//Gdx.app.log("humanim", "draw");
		}
		layerIndex++;
	}
	
	private void drawLayerAngled(SpriteBatch batch, CorneredSprite s, int colorID, byte light, byte dayLight, float x, float y, boolean left, float angle, Vector2 origin){
		
		if (left){
			s.setScale(1,1);
			s.setColorID(colorID, light, dayLight, layerIndex);
			//Gdx.app.log("layeredanim", "origin "+origin);
			returnV.set(+(s.xOff), s.yOff).sub(origin).rotate(angle).add(x,y).add(origin);
			s.setPosition(returnV.x, returnV.y);
			s.setOrigin(0,0);
			s.setRotation(angle);
			
			s.draw(batch);
		} else {
			s.setScale(-1,1);
			
			s.setColorID(colorID, light, dayLight, layerIndex);
			
			angle =angle-180;
			if (angle < 0) angle += 360;
			returnV.set(-(s.xOff), s.yOff).sub(origin).rotate(angle).add(x,y).add(origin);
			s.setPosition(returnV.x, returnV.y);

			s.setRotation(angle);

			s.draw(batch);
		}
		layerIndex++;
	}
	
	
	
	CorneredSprite[] tmpSpriteFrames = new CorneredSprite[17];
	public String name;
	
	//public abstract void queueDrawLayers(GenericMob mob, IntArray q, Item[] i);

	

	protected void queueTinted(GenericMob mob, Item[] i, IntArray q, int slot,
			int layer, int tint) {
		queue(mob, i, q, slot, layer, tint);
		
	}

	protected void queue(GenericMob mob, Item[] i, IntArray q, int slot, int layer) {
		int skin = mob.skin;
		if (mob.tint[0] != 0) skin = mob.tint[0];
		queue(mob, i, q, slot, layer, skin);
	}
	
	protected void queue(GenericMob mob, Item[] i, IntArray q, int slot, int layer, int skin) {
		if (slot < 0){
			q.add(layer);
			q.add(slot);
			return;
		}
		Item it = i[slot];
		
		
		if (it == null){
			if (layer == -1) return;
			q.add(layer);
			q.add(skin);
		} else try{
			ItemDef def = PunkBodies.getItemInfo(it.id, it.meta);
			Effect e =  ((Wearable)def).e;;
			if (e.draw(mob, q));
			else {
				if (layer == -1) return;
				q.add(layer);
				q.add(skin);
			}
		} catch (ClassCastException ex){
			Gdx.app.log("humanoidan", "invalid wearable");
		}
		
	}
	@Override
	public Vector2 getHandROffset(int f){
		return returnV.set(
				layers[34][f].xOff,
				layers[34][f].yOff
				
				);
	}
	@Override
	public Vector2 getHandLOffset(int f){
		returnV.set(
				layers[33][f].xOff,
				layers[33][f].yOff
				
				);
		//Gdx.app.log("humanoidan", "handL"+f+returnV);
		return returnV;
	}
	@Override
	public Vector2 getOriginOffset(int f) {
		return returnV.set(
				layers[38][f].xOff,
				layers[38][f].yOff
				
				);
	}
	
}

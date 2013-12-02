package com.niz.punk;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Pool.Poolable;

public abstract class StateFrameInfo {
	
	/*private static Pool<StateFrameInfo> pool = new Pool<StateFrameInfo>(){

		@Override
		protected StateFrameInfo newObject() {
			return new StateFrameInfo();
		}
		
	};*/
	
	public static String[] stateNames = {
		"stand", "walk", "run"
	};
	public Actor anim = new Actor(){
		
		@Override
		public void draw(SpriteBatch batch, float a){
			
		}
		
		
	};
	public class FrameActor extends Actor implements Poolable{//draws one frame, touching selects
		public int frame;
		@Override
		public void draw(SpriteBatch batch, float a){
			
		}
		@Override
		public void reset() {
			frame = 0;			
		}
		
		
	};
	Table table = new Table();
	
	;
	private static final int STATETOTAL = 30;
	private static final String TAG = null;

	//public IntArray[] stateFrames = new IntArray[STATETOTAL];
	//public float[] stateDeltas = new float[STATETOTAL];
	//public int[] stateAnimationType = new int[STATETOTAL];
	public StateFrameInfo(int start, int len, boolean loop, float delta){
		this.delta = delta;
		this.len = len;
		this.start = start;
		this.loop = loop;
		
		for (int i = 0; i < len; i++){
			layers.add(start+i);
		}
		
		
	}
	public StateFrameInfo() {
		this(18, 3, true, .2f);
	}
	public StateFrameInfo(int i, int j, boolean b, float runDelta2, boolean c) {
		this(i,j,b,runDelta2);
		resetStateTime = false;
	}
	public float delta;
	private int start, len;
	private boolean loop = false;
	boolean resetStateTime = true;;;
	boolean rotateAll, rotateArm;
	private IntArray layers = new IntArray(true, 4);
	public void addToTable(Table tab, int state){
		Array<Actor> actors = table.getChildren();
		Iterator<Actor> i = actors.iterator();
		while (i.hasNext()){
			//i.next().
		}
		table.clear();
		
		
	}
	
	public int getFrameIndex(float time){
		int f = MathUtils.floor((Math.abs(time)/delta));
		//Gdx.app.log("state frame info", "f"+f+"   time "+time + "     delta"+delta);
		//if (reverse) f = len-f-1;
		if (loop)
			return layers.get((f % len));
		else		
			return layers.get(Math.min(f,len-1));
		
	}

	
	public void addRotateLayer(int... i){
		rotateLayers.addAll(i);
	}
	private IntArray rotateLayers = new IntArray();
	public abstract void newFrame(GenericMob genericMob, PunkMap map, World world) ;
	public void addRotateLayers(IntArray l) {
		l.addAll(rotateLayers);
		
	}
	
	private Vector2 o = new Vector2(), hl = new Vector2(), hr = new Vector2();
	public boolean drawItem = true;
	public void setOffsets(GenericMob mob) {
		if (rotateAll || rotateArm){
			ComponentAnimation a = mob.anim;
			int fr = mob.frame;
			o.set(a.getOriginOffset(fr));
			
			hr.set(a.getHandROffset(fr));
			hl.set(a.getHandLOffset(fr));
			float angle;
			if (mob.isLeft){
				angle = mob.angle;
			}else {
				angle = 180 - mob.angle;
				if (angle < 0) angle += 360;
			}
			//o.rotate(angle);
			hr.sub(o).rotate(angle).add(o);
			hl.sub(o).rotate(angle).add(o);
			
			if (!mob.isLeft){
				hr.x *= -1;
				hl.x *= -1;
			}
			if (!mob.isLeft)o.x *=-1; 
			mob.originOffset.set(o);
			mob.handROffset.set(hr);
			mob.handLOffset.set(hl);
			//Gdx.app.log("sfinfo", "origin "+o+"  "+hr);
		} else {
			ComponentAnimation a = mob.anim;
			int st = mob.frame;
			o.set(a.getOriginOffset(st));
			hr.set(a.getHandROffset(st));
			hl.set(a.getHandLOffset(st));
			if (!mob.isLeft){
				o.x *=-1; 
				//hl.x -= o.x;
				hl.x *=-1;
				//hl.x += o.x;
				hr.x *=-1;
			}
			//o.set(0,1.125f);
			
			
			mob.originOffset.set(o);
			mob.handROffset.set(hr);
			mob.handLOffset.set(hl);
			//Gdx.app.log(TAG, "set oriogin"+o+hr);
		}
		
		
	}
	public float getMaxTime() {
		return delta * (len+1);
	}
	public StateFrameInfo reverseLoop() {
		for (int i = 0; i < len; i++)
			layers.add(layers.get(len-i-1));
		len *= 2;
		return this;
	}
	public StateFrameInfo loop(){
		loop = true;
		return this;
	}
	public StateFrameInfo reverse(){
		IntArray newL = new IntArray(layers);
		layers.clear();
		for (int i = 0; i < len; i++)
			layers.add(newL.get(len-i-1));//.add(layers.get(len-i-1));
		
		
		return this;
	}
	public StateFrameInfo repeat(int index, int len, int reps){
		for (int r = 0; r < reps; r++)
			for (int i = index; i < index+len; i++)
				layers.add(layers.get(i));
		return this;
	}
	public StateFrameInfo gggg(){
		return this;
	}
	public StateFrameInfo add(int i) {
		layers.add(i);
		len++;
		return this;
	}
	
}

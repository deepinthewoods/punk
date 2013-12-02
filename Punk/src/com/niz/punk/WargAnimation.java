package com.niz.punk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.IntArray;

public class WargAnimation extends LayeredAnimation {
	static int framecount = 71;
	static int layercount = 40;
	public WargAnimation(TextureAtlas atlasses[], int ind) {
		
		super(framecount, layercount,  ind);
		
		TextureAtlas atlas = atlasses[ind];
		for (int i = 0; i < framecount; i++){
			
			float xx=4f, yy = 4.5f;
			layers[0][i] = new CorneredSprite(atlas.findRegion("warg", i), xx,yy);
			
			layers[33][i] = new CorneredSprite(atlas.findRegion("warghgl", i), xx,yy);
			layers[34][i] = new CorneredSprite(atlas.findRegion("warghgr", i), xx,yy);
			layers[35][i] = new CorneredSprite(atlas.findRegion("wargwtr", i), xx,yy);
			//layers[36][i] = new CorneredSprite(atlas.findRegion("upperarmr", i), xx,yy);
			//layers[37][i] = new CorneredSprite(atlas.findRegion("upperarml", i), xx,yy);
			
			//layers[38][i] = new CorneredSprite(atlas.findRegion("wargorigin", i), xx,yy);
			layers[39][i] = new CorneredSprite(atlas.findRegion("wargwtl", i), xx,yy);
			handLAngles[i] = tmpV.set(layers[39][i].xOff, layers[39][i].yOff).sub(layers[33][i].xOff, layers[33][i].yOff).angle()-180;
			handRAngles[i] = tmpV.set(layers[35][i].xOff, layers[35][i].yOff).sub(layers[34][i].xOff, layers[34][i].yOff).angle()-180;
			//Gdx.app.log("han", "ss:"+i+":"+handRAngles[i]);
		
		}
		for (int i = 0; i < framecount; i++){
			for (int j = 0; j < layercount; j++){
				if (layers[j][i] == null)
					layers[j][i] = layers[0][i];
			}
		}
	
		initStateAnims();
	}
	
	private StateFrameInfo[] initStateAnims() {
		states = new StateFrameInfo[100];

		
		states[0] = new StateFrameInfo(13,7, true, 1f){
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {	} };
			
		states[31] = new StateFrameInfo(52,6, false, 3f){//die
				public void newFrame(GenericMob genericMob, PunkMap map, World world) {	} };
			
		states[1] = new StateFrameInfo(0,11, true, .11f){//walk
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {	} };;
			
		states[2] = new StateFrameInfo(0,11, true,  .06f){//run
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {	} };;
			
		states[3] = new StateFrameInfo(12,11, true,  .1f){//
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {	} };;
			
		states[4] = new StateFrameInfo(10,1, true,  .2f){//jump1
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {	} };;
		states[5] = new StateFrameInfo(10,1,false,  .2f){//jump 2
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {	} };;
		states[10] = new StateFrameInfo(10,1,true,  .2f){//fall
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {	} };;
			
			
		states[6] = new StateFrameInfo(){
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {	} };;
			
		states[7] = new StateFrameInfo(111,4, false,  .07f){
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {	} };;
		states[7].addRotateLayer(0,1,2,3,4, 24, 25, 26, 27, 28, 36, 37);
		states[7].rotateArm = true;
		states[8] = new StateFrameInfo(111, 9, false,  .07f){
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {	} };;
		states[8].addRotateLayer(0,1,2,3,4, 24, 25, 26, 27, 28, 36, 37);
		states[8].rotateArm = true;
		states[29] = new StateFrameInfo(110, 12, false,  .07f){
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {	} };;
		states[29].addRotateLayer(0,1,2,3,4, 24, 25, 26, 27, 28, 36, 37);
		states[29].rotateArm = true;
		states[29].drawItem = false;
		
		states[30] = new StateFrameInfo(49, 6, true,  .7f){
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {	} };;
			states[30].reverseLoop();
		
		states[9] = new StateFrameInfo(7,1,false,  .2f){
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {	} };;
			
		
			
		states[11] = new StateFrameInfo(170,3,true,  .4f){
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {	} };;
		states[11].rotateAll = true;
		states[11].addRotateLayer(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40);
		
		states[22] = new StateFrameInfo(170,3,true,  .4f){
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {	} 
		};;
		states[22].rotateAll = true;
		states[22].addRotateLayer(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40);

			
		states[12] = new StateFrameInfo(){
				public void newFrame(GenericMob genericMob, PunkMap map, World world) {	} 
				};;
		states[13] = new StateFrameInfo(){
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {	} 
			};;
		states[14] = new StateFrameInfo(){
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {	} 
		};;
		states[15] = new StateFrameInfo(){
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {	} 
		};;
		states[16] = new StateFrameInfo(){
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {	} 
		};;
		states[17] = new StateFrameInfo(){
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {	} 
		};;
		states[18] = new StateFrameInfo(){
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {	} 
		};;
		states[19] = new StateFrameInfo(){
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {	} 
		};;
		states[20] = new StateFrameInfo(){
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {	} 
		};;
		states[21] = new StateFrameInfo(){
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {	} 
		};;
		
		states[23] = new StateFrameInfo(){
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {	} 
		};;
		states[24] = new StateFrameInfo(){
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {	} 
		};;
		states[25] = new StateFrameInfo(){
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {	} 
		};;
		states[26] = new StateFrameInfo(){
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {	} 
		};;
										
							
				
		return states;
	}
	@Override
	public void queueDrawLayers(GenericMob mob, IntArray q, Item[] i) {
		//layer then color
		q.clear();
	
		//hand
		if (mob.tint[2] != 0)queueTinted(mob, i, q, 8, 0  ,mob.tint[2]);else

		queue(mob, i, q, 8, 0);
		
		//arm r
		if (mob.tint[3] != 0)queueTinted(mob, i, q, 1, 2, mob.tint[3]);else
		queue(mob, i, q, 1, 2);
		
		//HEAD
		
		//legs
		//if (mob.tint[3] != 0)queueTinted(mob, i, q, 3, 19  ,mob.tint[3]);else
		queue(mob, i, q, 3, 19);
		
		//feet
		if (mob.tint[1] != 0)queueTinted( mob, i, q, 4, 23 ,mob.tint[1]);else
		queue(mob, i, q, 4, 23);
		
		//body
		Item it = i[2];
		boolean male = mob.genderID == 0;
		if (it == null){
			if (male){
				if (mob.tint[0] != 0){
					queueTinted(mob, i, q, 1, 7  ,mob.tint[0]);
					queueTinted(mob, i, q, 1, 8  ,mob.tint[0]);
			}else{
					queue(mob, i, q, 1, 7, mob.cloth1);
					queue(mob, i, q, 1, 8, mob.cloth2);
			}
			}else {
				if (mob.tint[0] != 0){
					queueTinted(mob, i, q, 1, 5  ,mob.tint[0]);
					queueTinted(mob, i, q, 1, 6  ,mob.tint[0]);
				}else{
					queue(mob, i, q, 1, 5, mob.cloth1);
					queue(mob, i, q, 1, 6, mob.cloth2);
				}
			}
		
		} else try{
			ItemDef def = PunkBodies.getItemInfo(it.id, it.meta);
			
			Effect e = ((Wearable)def).e;
			if (e.draw(mob, q));
			else {
				
				if (male){
					if (mob.tint[0] != 0){
						queueTinted(mob, i, q, 1, 7  ,mob.tint[0]);
						queueTinted(mob, i, q, 1, 8  ,mob.tint[0]);
				}else{
						queue(mob, i, q, 1, 7, mob.cloth1);
						queue(mob, i, q, 1, 8, mob.cloth2);
				}
				}else {
					if (mob.tint[0] != 0){
						queueTinted(mob, i, q, 1, 5  ,mob.tint[0]);
						queueTinted(mob, i, q, 1, 6  ,mob.tint[0]);
					}else{
						queue(mob, i, q, 1, 5, mob.cloth1);
						queue(mob, i, q, 1, 6, mob.cloth2);
					}
				}
			
			
			}
		} catch (ClassCastException ex){
			Gdx.app.log("humanoidan", "invalid wearable");
		}
		
		
		//arm l
		if (mob.tint[3] != 0)queueTinted(mob, i, q, 1, 24  ,mob.tint[3]);else
		queue(mob, i, q, 1, 24);
		//hand l
		if (mob.tint[2] != 0)queueTinted( mob, i, q, 8, 27 ,mob.tint[2]);else
		queue(mob, i, q, 8, 27);
		
		//shield
		if (mob.tint[3] != 0)queueTinted( mob, i, q, 9, -1 ,mob.tint[3]);else
		queue(mob, i, q, 9, -1);
	
		
	}
	
	

}

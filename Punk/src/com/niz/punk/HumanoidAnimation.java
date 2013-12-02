package com.niz.punk;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.IntArray;
import com.niz.punk.PunkMap.BlockDamageType;

public class HumanoidAnimation extends LayeredAnimation {
	static int framecount = 240;
	static int layercount = 47;
	
	public HumanoidAnimation(TextureAtlas[] atlasses, int ind, float foot) {
		super(framecount, layercount, ind);
		create(atlasses, ind, foot);
	}
	
	public HumanoidAnimation(FileHandle file, Texture tex, int ind){
		super(framecount, layercount, ind);
		load(file, tex);
	}
	
	public void create(TextureAtlas[] atlasses, int ind, float footpx){
		
		
		
		TextureAtlas atlas = atlasses[ind];
		float xx=8f, yy = 3.295f-footpx/16f;
		for (int i = 0; i < framecount; i++){
			
			
			layers[0][i] = new CorneredSprite(atlas.findRegion("handr", i), xx,yy);
			layers[1][i] = new CorneredSprite(atlas.findRegion("glover", i), xx,yy);
			layers[2][i] = new CorneredSprite(atlas.findRegion("armr", i), xx,yy);
			layers[3][i] = new CorneredSprite(atlas.findRegion("robearmr", i), xx,yy);
			layers[4][i] = new CorneredSprite(atlas.findRegion("gauntletr", i), xx,yy);
			layers[5][i] = new CorneredSprite(atlas.findRegion("body1f", i), xx,yy);
			layers[6][i] = new CorneredSprite(atlas.findRegion("body2f", i), xx,yy);
			layers[7][i] = new CorneredSprite(atlas.findRegion("body1m", i), xx,yy);
			layers[8][i] = new CorneredSprite(atlas.findRegion("body2m", i), xx,yy);
			layers[9][i] = new CorneredSprite(atlas.findRegion("robes1f", i), xx,yy);
			layers[10][i] = new CorneredSprite(atlas.findRegion("robes2f", i), xx,yy);
			layers[12][i] = new CorneredSprite(atlas.findRegion("robes1m", i), xx,yy);
			layers[13][i] = new CorneredSprite(atlas.findRegion("robes2m", i), xx,yy);
			layers[19][i] = new CorneredSprite(atlas.findRegion("legs", i), xx,yy);
			layers[20][i] = new CorneredSprite(atlas.findRegion("grieves", i), xx,yy);
			layers[21][i] = new CorneredSprite(atlas.findRegion("feet", i), xx,yy);
			layers[22][i] = new CorneredSprite(atlas.findRegion("shoes", i), xx,yy);
			layers[23][i] = new CorneredSprite(atlas.findRegion("boots", i), xx,yy);
			layers[24][i] = new CorneredSprite(atlas.findRegion("arml", i), xx,yy);
			layers[25][i] = new CorneredSprite(atlas.findRegion("robearml", i), xx,yy);
			layers[26][i] = new CorneredSprite(atlas.findRegion("gauntletl", i), xx,yy);
			layers[27][i] = new CorneredSprite(atlas.findRegion("handl", i), xx,yy);
			layers[28][i] = new CorneredSprite(atlas.findRegion("glovel", i), xx,yy);
			//layers[29][i] = new CorneredSprite(atlas.findRegion("shieldbuckler", i), xx,yy);
			//layers[30][i] = new CorneredSprite(atlas.findRegion("shieldmedium", i), xx,yy);
			//layers[31][i] = new CorneredSprite(atlas.findRegion("shieldtower", i), xx,yy);
			layers[32][i] = new CorneredSprite(atlas.findRegion("whip", i), xx,yy);
			layers[33][i] = new CorneredSprite(atlas.findRegion("handguidel", i), xx,yy);
			layers[34][i] = new CorneredSprite(atlas.findRegion("handguider", i), xx,yy);
			layers[35][i] = new CorneredSprite(atlas.findRegion("weapontipr", i), xx,yy);
			layers[36][i] = new CorneredSprite(atlas.findRegion("upperarmr", i), xx,yy);
			layers[37][i] = new CorneredSprite(atlas.findRegion("upperarml", i), xx,yy);
			layers[38][i] = new CorneredSprite(atlas.findRegion("origin", i), xx,yy);
			layers[39][i] = new CorneredSprite(atlas.findRegion("weapontipl", i), xx,yy);
			layers[40][i] = new CorneredSprite(atlas.findRegion("head", i), xx,yy);
			layers[41][i] = new CorneredSprite(atlas.findRegion("hairflong", i), xx,yy);
			layers[42][i] = new CorneredSprite(atlas.findRegion("hairfshort", i), xx,yy);
			layers[43][i] = new CorneredSprite(atlas.findRegion("helmeta", i), xx,yy);
			layers[44][i] = new CorneredSprite(atlas.findRegion("helmetb", i), xx,yy);
			layers[45][i] = new CorneredSprite(atlas.findRegion("helmetc", i), xx,yy);
			layers[46][i] = new CorneredSprite(atlas.findRegion("helmetd", i), xx,yy);
			handLAngles[i] = tmpV.set(layers[39][i].xOff, layers[39][i].yOff).sub(layers[33][i].xOff, layers[33][i].yOff).angle()-180;
			handRAngles[i] = tmpV.set(layers[35][i].xOff, layers[35][i].yOff).sub(layers[34][i].xOff, layers[34][i].yOff).angle()-180;
			//Gdx.app.log("han", "ss:"+i+":"+handRAngles[i]);
		
		}
	
		initStateAnims();
	}
	public void save(FileHandle file){
		DataOutputStream os = new DataOutputStream(file.write(false));
		
		try {
			
			for (int i = 0; i < layercount; i++)
			for (int j = 0; j < framecount; j++){
				//Gdx.app.log("huiman anim", "write"+j+" "+i);
					if (layers[i][j] == null){os.writeFloat(0f);os.writeFloat(0f);os.writeFloat(0f);os.writeFloat(0f);
					os.writeFloat(0f);os.writeFloat(0f);os.writeFloat(0f);os.writeFloat(0f);
					} else {
						//os.writeFloat(0f);
						//os.writeFloat(0f);os.writeFloat(0f);os.writeFloat(0f);
						//os.writeFloat(0f);os.writeFloat(0f);os.writeFloat(0f);os.writeFloat(0f);
						
						os.writeFloat(layers[i][j].sizeX);
						os.writeFloat(layers[i][j].sizeY);
						os.writeFloat(layers[i][j].xOff);
						os.writeFloat(layers[i][j].yOff);
						os.writeFloat(layers[i][j].getU());
						os.writeFloat(layers[i][j].getV());
						os.writeFloat(layers[i][j].getU2());
						os.writeFloat(layers[i][j].getV2());
						os.flush();
					}
				
			}
			os.close();
				} catch (IOException e) {
					Gdx.app.log("huiman anim", "error writing"+e);
					e.printStackTrace();
				}
		
		
	}
	public void load(FileHandle file, Texture tex){
		DataInputStream is = new DataInputStream (new BufferedInputStream(file.read()));
		try {
			for (int i = 0; i < layercount; i++)
			for (int j = 0; j < framecount; j++){
			
				float sizeX = 
						is.readFloat(), 
						sizeY =is.readFloat(), 
						xOff = is.readFloat(), 
						yOff = is.readFloat(), 
						u = is.readFloat(), 
						v = is.readFloat(), 
						u2 = is.readFloat(), 
						v2 = is.readFloat();
				layers[i][j] = new CorneredSprite(tex, sizeX, sizeY, xOff, yOff, u, v, u2, v2);
								
				
				}
			is.close();
			} catch (IOException ex){
					Gdx.app.log("huiman anim", "error reading"+ex);
				}

	}
	public static float JUMPWALK = 4f;
	private StateFrameInfo[] initStateAnims() {
		states = new StateFrameInfo[100];
		StateFrameInfo defaultState = new StateFrameInfo(0,8,true, .1f){
			@Override
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {	}};
		for (int i = 0; i < 100; i++)
			states[i] = defaultState;
		
		
		
		
		states[0] = new StateFrameInfo(18,5, true, 3f, false){
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {	} };
			
		states[1] = new StateFrameInfo(7,11, true, .11f){
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {	} };;
			float runDelta = 0.07f, runDelta2 = runDelta * JUMPWALK;
		states[2] = new StateFrameInfo(7,11, true,  runDelta, false){
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {	} };;
			
		states[9] = new StateFrameInfo(7,11,false,  runDelta2, false){
			public void newFrame(GenericMob mob, PunkMap map, World world) {	} };;
				
		states[10] = new StateFrameInfo(7,11,true,  runDelta2, false){
			public void newFrame(GenericMob mob, PunkMap map, World world) {	} };;
				
		states[3] = new StateFrameInfo(12,11, true,  runDelta2, false){
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {	} };;
			
		states[4] = new StateFrameInfo(7,11, true,  runDelta2, false){
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {	} };;
		
		states[5] = new StateFrameInfo(7,11,false,  runDelta2, false){
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {	} };;
			
			
		states[6] = new StateFrameInfo(){
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {	} };;
			
		states[7] = new StateFrameInfo(127,4, false,  .06f){
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {	} };;
		states[7].addRotateLayer(0,1,2,3,4, 24, 25, 26, 27, 28, 36, 37);
		states[7].rotateArm = true;
		states[8] = new StateFrameInfo(127, 9, false,  .06f){
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {	} };;
		states[8].addRotateLayer(0,1,2,3,4, 24, 25, 26, 27, 28, 36, 37);
		states[8].rotateArm = true;
		states[29] = new StateFrameInfo(127, 11, false,  .06f){
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {	} };;
		states[29].addRotateLayer(0,1,2,3,4, 24, 25, 26, 27, 28, 36, 37);
		states[29].rotateArm = true;
		states[29].drawItem = false;
		
		//place
		states[18] = new StateFrameInfo(127, 4, false,  .04f){
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {	} };;
		states[18].addRotateLayer(0,1,2,3,4, 24, 25, 26, 27, 28, 36, 37);
		states[18].rotateArm = true;
		
		states[48] = new StateFrameInfo(127, 4, false,  .04f){
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {	} };;
		states[48].addRotateLayer(0,1,2,3,4, 24, 25, 26, 27, 28, 36, 37);
		states[48].rotateArm = true;
		
		states[49] = new StateFrameInfo(127, 4, false,  .04f){
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {	} };;
		states[49].addRotateLayer(0,1,2,3,4, 24, 25, 26, 27, 28, 36, 37);
		states[49].rotateArm = true;
		
		states[39] = new StateFrameInfo(127, 9, false,  .04f){
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {	} };;
		states[39].addRotateLayer(0,1,2,3,4, 24, 25, 26, 27, 28, 36, 37);
		states[39].rotateArm = true;
		

		states[50] = new StateFrameInfo(127, 9, false,  .04f){
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {	} };;
		states[50].addRotateLayer(0,1,2,3,4, 24, 25, 26, 27, 28, 36, 37);
		states[50].rotateArm = true;
		
		states[20] = new StateFrameInfo(127, 11, false,  .04f){
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {	} };;
		states[20].addRotateLayer(0,1,2,3,4, 24, 25, 26, 27, 28, 36, 37);
		states[20].rotateArm = true;
		//states[20].drawItem = false;
		
		states[41] = new StateFrameInfo(127, 4, false,  .04f){
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {	} };;
		states[41].addRotateLayer(0,1,2,3,4, 24, 25, 26, 27, 28, 36, 37);
		states[41].rotateArm = true;
		states[42] = new StateFrameInfo(127, 9, false,  .04f){
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {	} };;
		states[42].addRotateLayer(0,1,2,3,4, 24, 25, 26, 27, 28, 36, 37);
		states[42].rotateArm = true;
		states[43] = new StateFrameInfo(127, 11, false,  .04f){
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {	} };;
		states[43].addRotateLayer(0,1,2,3,4, 24, 25, 26, 27, 28, 36, 37);
		states[43].rotateArm = true;
		
		states[30] = new StateFrameInfo(157, 7, true,  .2f){
			public void newFrame(GenericMob mob, PunkMap map, World world) {	
					
				mob.prayForBlessings();
				
			} };;
			states[30].reverseLoop();
		
			float flyDelta = .075f;
		states[32] = new StateFrameInfo(5, 2, true,  flyDelta){

			@Override
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {
				// TODO Auto-generated method stub
				
			}
				
			};;
			states[32].loop();
		states[33] = new StateFrameInfo(0, 6, false,  flyDelta){

			@Override
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {
				// TODO Auto-generated method stub
				
			}
			
		};;
		states[33].reverse();
		
		
		states[34] = new StateFrameInfo(0, 7, false,  flyDelta){

			@Override
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {
				// TODO Auto-generated method stub
				
			}
			
		};;
		//states[34].reverse();
		
		
		states[35] = new StateFrameInfo(0, 2, true,  flyDelta){

			@Override
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {
				// TODO Auto-generated method stub
				
			}
			
		};;
		states[36] = new StateFrameInfo(5, 2, true,  .3f){
			public void newFrame(GenericMob genericMob, PunkMap map, World world) {	} 
		};;
		
		
			
		states[11] = new StateFrameInfo(196,3,true,  .2f){
			public void newFrame(GenericMob mob, PunkMap map, World world) {	} };;
		states[11].rotateAll = true;
		states[11].addRotateLayer(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40);
		
		states[22] = new StateFrameInfo(196,3,true,  .2f){
			public void newFrame(GenericMob mob, PunkMap map, World world) {	} 
		};;
		states[22].rotateAll = true;
		states[22].addRotateLayer(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40);

			
		
		
		
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
		
		states[12] = new StateFrameInfo(23, 14, false, .05f){
			public void newFrame(GenericMob m, PunkMap map, World world) {
				if (m.state >3)m.attackRaycast();
			} 
		};;
		states[13] = new StateFrameInfo(37, 14, false, .05f){
			public void newFrame(GenericMob m, PunkMap map, World world) {
				if (true)m.attackRaycast();
			} 
		};;
		states[14] = new StateFrameInfo(50,14,false,.05f){
			public void newFrame(GenericMob m, PunkMap map, World world) {	
				if (true)m.attackRaycast();
			} 
		};;
		states[15] = new StateFrameInfo(64, 21, false, .05f){
			public void newFrame(GenericMob m, PunkMap map, World world) {	
				if (true)m.attackRaycast();
			} 
		};;
		states[16] = new StateFrameInfo(85, 21, false, .05f){
			public void newFrame(GenericMob m, PunkMap map, World world) {	
				if (true)m.attackRaycast();
			} 
		};;
		states[17] = new StateFrameInfo(106, 21, false, .05f){
			public void newFrame(GenericMob m, PunkMap map, World world) {	
				if (true)m.attackRaycast();
			} 
		};;		
		states[36] = new StateFrameInfo(200, 2, false, .05f){
			public void newFrame(GenericMob m, PunkMap map, World world) {	
				//if (true)m.attackRaycast();
			} 
		};;		
		states[37] = new StateFrameInfo(203, 2, false, .015f){
			public void newFrame(GenericMob m, PunkMap map, World world) {	
				if (m.frame >4 && m.stopped){//do stomp
					if (m.targetB.x == m.x && m.targetB.y == m.y-1){
						map.damageBlock(BlockDamageType.HAND, m.targetB, 1000);
						Gdx.app.log("humanoidan", "saadssdstomp block"+m.targetB);
						m.targetB.set(m.x,m.y-2);
					} else {
						
						m.targetB.set(m.x,m.y-1);
					}
					m.state = 44;
					m.lastState = 44;
				}
			} 
		};;		
		states[37].add(204).add(204).add(204).add(204).add(204)
		.add(204).add(204).add(204).add(204).add(204)
		.add(204).add(204).add(204).add(204).add(204)
		.add(204).add(204).add(204).add(204).add(204)
		.add(205).add(205).add(205).add(205);
		
		states[44] = new StateFrameInfo(203, 2, false, .015f){
			public void newFrame(GenericMob m, PunkMap map, World world) {
				
			} 
		};;		
		states[44].add(204).add(204).add(204).add(204).add(204)
		.add(204).add(204).add(204).add(204).add(204)
		.add(204).add(204).add(204).add(204).add(204)
		.add(204).add(204).add(204).add(204).add(204)
		.add(205).add(205).add(205).add(205);
		
		states[38] = new StateFrameInfo(194, 1, false, .015f){
			public void newFrame(GenericMob m, PunkMap map, World world) {	
				//if (true)m.attackRaycast();
			} 
		};;		
		
		states[40] = new StateFrameInfo(194, 1, false, .015f){
			public void newFrame(GenericMob m, PunkMap map, World world) {	
				//if (true)m.attackRaycast();
			} 
		};;		
		
		states[45] = new StateFrameInfo(164, 8, false, .025f){
			public void newFrame(GenericMob m, PunkMap map, World world) {	
				//if (true)m.attackRaycast();
			} 
		};;		
		
		states[46] = new StateFrameInfo(171, 1, false, .025f){
			public void newFrame(GenericMob m, PunkMap map, World world) {	
				//if (true)m.attackRaycast();
			} 
		};;		
		states[46].rotateAll = true;
		states[46].addRotateLayer(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40);

		
		states[47] = new StateFrameInfo(164, 8, false, .025f){
			public void newFrame(GenericMob m, PunkMap map, World world) {	
				//if (true)m.attackRaycast();
			} 
		};;		
		states[47].reverse();
		states[47].rotateAll = true;
		states[47].addRotateLayer(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40);

				
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
		if (mob.tint[5] != 0)queueTinted(mob, i, q, 0, 40, mob.tint[5]);else
			queue(mob, i, q, 0, 40);
		//legs
		//if (mob.tint[3] != 0)queueTinted(mob, i, q, 3, 19  ,mob.tint[3]);else
		queue(mob, i, q, 3, 19);
		
		//feet
		if (mob.tint[1] != 0)queueTinted( mob, i, q, 4, 21 ,mob.tint[1]);else
		queue(mob, i, q, 4, 21);
		
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
		queue(mob, i, q, 9, -1);//*/
	
		//origin marker
		queueTinted(mob, i, q, 4, 34, 3);
		
	}
	
	public Vector2 getOriginO2ffset(int f) {
		return returnV.set(
				0,
				0f
				
				);
	}

}

package com.niz.punk;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntArray;

public class ChunkEncoder {
public IntArray[] buffers = new IntArray[3];// ids = new IntArray(), metas = new IntArray(), light = new IntArray();
public int idR, metaR, lightR, idL, metaL, lightL;
public void init(){
	//ids = BufferUtils.newByteBuffer(Punk.CHUNKSIZE*Punk.CHUNKSIZE*2);
	//metas = BufferUtils.newByteBuffer(Punk.CHUNKSIZE*Punk.CHUNKSIZE*2);
	//light = BufferUtils.newByteBuffer(Punk.CHUNKSIZE*Punk.CHUNKSIZE*2);
	for (int i = 0; i < 3; i++){
		buffers[i] = new IntArray(10000);
		buffers[i].ordered = true;
	}
}
public void add(Block b){
	if (b.meta == -7) throw new GdxRuntimeException("meta wrong");
	if (idR == 0){
		idL = //(byte) 
				b.blockID;
		idR = 1;
	}
	else if (b.blockID == idL && idR < 100){
		idR++;
	} else {
		buffers[0].add(idR);
		buffers[0].add(idL);
		idL = //(byte) 
				b.blockID;
		idR = 1;
	}
	//assert (b.meta != -7);
	if (metaR == 0){
		//Gdx.app.log("encoder", "meta"+b.meta + " = "+(byte)250);
		metaL = //(byte) 
				b.meta;
		metaR = 1;
	}
	else if (b.meta == metaL && metaR < 100){
		metaR++;
	} else {
		buffers[1].add(metaR);
		buffers[1].add(metaL);
		metaL = //(byte) 
				b.meta;
		metaR = 1;
	}
	int bl = //(byte) 
			((b.getLight()&15) + (b.getDayLight()<<4));
	if (lightR == 0){
		//lightL = b.blockID;
		lightL  = bl;;
		//sos.write(l);
		lightR = 1;
	}
	else if (bl == lightL && lightR < 100){
		lightR++;
	} else {
		buffers[2].add(lightR);
		buffers[2].add(lightL);
		lightL  = bl;;
		lightR = 1;
	}
	
}

public void clear(){
	buffers[0].clear();
	buffers[1].clear();
	buffers[2].clear();
	idR = 0;
	metaR = 0;
	lightR = 0;
	//prog = 0;
	//progState = 0;
}

public void finish() {
	buffers[0].add(idR);
	buffers[0].add(idL);
	
	buffers[1].add(metaR);
	buffers[1].add(metaL);
	
	buffers[2].add(lightR);
	buffers[2].add(lightL);
	prog = 0;
	progState = 0;
	//hasNext = true;
	//Gdx.app.log("encoder ", " buffers, 0:"+buffers[0].size + "  1:"+buffers[1].size + "  2:"+buffers[2].size);
}
int prog, progState;
public boolean hasNext(){
	return (progState <= 2 && prog <= buffers[progState].size);
}
public int next(){
	
	int i =  buffers[progState].get(prog);
	prog++;
	
	if (buffers[progState].size <= prog){
		progState++;
		prog = 0;
		//if (progState == 3) hasNext = false;
	}
	
	//if (progState == 2 && prog >= buffers[2].size) hasNext = false;
	return i;
}


}

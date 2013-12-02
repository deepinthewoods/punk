package com.niz.punk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.IntArray;

public class ChunkBGEncoder {
public IntArray[] buffers = new IntArray[1];// ids = new IntArray(), metas = new IntArray(), light = new IntArray();
public byte idR,  idL;
public void init(){
	//ids = BufferUtils.newByteBuffer(Punk.CHUNKSIZE*Punk.CHUNKSIZE*2);
	//metas = BufferUtils.newByteBuffer(Punk.CHUNKSIZE*Punk.CHUNKSIZE*2);
	//light = BufferUtils.newByteBuffer(Punk.CHUNKSIZE*Punk.CHUNKSIZE*2);
	for (int i = 0; i < 1; i++){
		buffers[i] = new IntArray();
		buffers[i].ordered = true;
	}
}
public void add(byte b){
	if (idR == 0){
		idL = b;
		idR = 1;
	}
	else if (b == idL && idR < 100){
		idR++;
	} else {
		buffers[0].add(idR);
		buffers[0].add(idL);
		idL = b;
		idR = 1;
	}
	
	
	
}

public void clear(){
	buffers[0].clear();
	
	idR = 0;

}

public void finish() {
	buffers[0].add(idR);
	buffers[0].add(idL);
	
	
	prog = 0;
	progState = 0;
	hasNext = true;
	//Gdx.app.log("encoder ", " buffers, 0:"+buffers[0].size + "  1:"+buffers[1].size + "  2:"+buffers[2].size);
}
int prog, progState;
public boolean hasNext;
public int next(){
	if (buffers[progState].size <= prog){
		progState++;
		prog = 0;
		
	}
	int i =  buffers[progState].get(prog);
	prog++;
	if (prog >= buffers[0].size) hasNext = false;
	return i;
}


}

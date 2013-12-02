package com.niz.punk;

import java.io.BufferedInputStream;
import java.io.IOException;

import com.badlogic.gdx.utils.GdxRuntimeException;

public class ChunkDecoder {
public int r,v;
public BufferedInputStream stream;

public void set(BufferedInputStream inputStream) {
	stream = inputStream;
	r = 0;
	v = 0;
}

public int get() {
	// returns int as if from outputStream
	if (r == 0){
		try {
			r = stream.read();
			v = stream.read();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	r--;
	//if (stream.read()) throw new GdxRuntimeException("empty");
	if (v < 0) throw new GdxRuntimeException("meta wrong");
	return v;
}

}

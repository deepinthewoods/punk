package com.niz.punk;

import java.io.BufferedInputStream;
import java.io.IOException;

public class ChunkBGDecoder {
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	r--;
	return 0;//v;
}


}

package com.niz.punk;

public class XRand {
	protected static int get(int nbits, int x) {
	    // N.B. Not thread-safe!
		//byte x = 5;//seed
	    x ^= (x << 21);
	    x ^= (x >>> 35);
	    x ^= (x << 4);
	    //this.seed = x;
	    x &= ((1L << nbits) -1);
	    return  x;
	  }
	
	protected static int get(int nbits, float seed) {
	    // N.B. Not thread-safe!
		//byte x = 5;//seed
		int x = (int)(seed*200);
	    x ^= (x << 21);
	    x ^= (x >>> 35);
	    x ^= (x << 4);
	    //this.seed = x;
	    x &= ((1L << nbits) -1);
	    return  x;
	  }
	protected static int getSeeded(int nbits, int seed) {
	    // N.B. Not thread-safe!
		//byte x = 5;//seed
		int x = (seed*200);
	    x ^= (x << 21);
	    x ^= (x >>> 35);
	    x ^= (x << 4);
	    //this.seed = x;
	    x &= ((1L << nbits) -1);
	    return  x;
	  }
	
	protected static boolean getBoolean( int seed) {
	    // N.B. Not thread-safe!
		//byte x = 5;//seed
		
		int x = (seed*200);
	    x ^= (x << 21);
	    x ^= (x >>> 35);
	    x ^= (x << 4);
	    //this.seed = x;
	    x &= ((1L << 1) -1);
	    return  x>0;
	  }
	
	protected static byte getByte(int seed) {
//		int x = (int)(seed*200);
//		x ^= (x << 21);
//	    x ^= (x >>> 35);
//	    x ^= (x << 4);
//	    //this.seed = x;
//	    //x &= ((1L << nbits) -1);
	    return  (byte)getSeeded(8, seed);
	  }
}

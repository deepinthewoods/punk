package com.niz.punk;

public class MurmurHash3 {
	private static byte[] tmpBytes = new byte[8];
	public static int get(int x, int y, int seed){
		tmpBytes[0] = (byte) (x & 255);
		tmpBytes[1] = (byte) ((x>>>4) & 255);
		tmpBytes[2] = (byte) ((x>>>8) & 255);
		tmpBytes[3] = (byte) ((x>>>12) & 255);
		
		tmpBytes[4] = (byte) ((y>>>0) & 255);
		tmpBytes[5] = (byte) ((y>>>4) & 255);
		tmpBytes[6] = (byte) ((y>>>8) & 255);
		tmpBytes[7] = (byte) ((y>>>12) & 255);
		
		return murmurhash3_x86_32(tmpBytes, 0, 8, seed);
	}
	  /** Returns the MurmurHash3_x86_32 hash. */
	  public static int murmurhash3_x86_32(byte[] data, int offset, int len, int seed) {

	    final int c1 = 0xcc9e2d51;
	    final int c2 = 0x1b873593; 

	    int h1 = seed;
	    int roundedEnd = offset + (len & 0xfffffffc);  // round down to 4 byte block

	    for (int i=offset; i<roundedEnd; i+=4) {
	      // little endian load order
	      int k1 = (data[i] & 0xff) | ((data[i+1] & 0xff) << 8) | ((data[i+2] & 0xff) << 16) | (data[i+3] << 24);
	      k1 *= c1;
	      k1 = (k1 << 15) | (k1 >>> 17);  // ROTL32(k1,15);
	      k1 *= c2;

	      h1 ^= k1;
	      h1 = (h1 << 13) | (h1 >>> 19);  // ROTL32(h1,13);
	      h1 = h1*5+0xe6546b64;
	    }

	    // tail
	    int k1 = 0;

	    switch(len & 0x03) {
	      case 3:
	        k1 = (data[roundedEnd + 2] & 0xff) << 16;
	        // fallthrough
	      case 2:
	        k1 |= (data[roundedEnd + 1] & 0xff) << 8;
	        // fallthrough
	      case 1:
	        k1 |= (data[roundedEnd] & 0xff);
	        k1 *= c1;
	        k1 = (k1 << 15) | (k1 >>> 17);  // ROTL32(k1,15);
	        k1 *= c2;
	        h1 ^= k1;
	    }

	    // finalization
	    h1 ^= len;

	    // fmix(h1);
	    h1 ^= h1 >>> 16;
	    h1 *= 0x85ebca6b;
	    h1 ^= h1 >>> 13;
	    h1 *= 0xc2b2ae35;
	    h1 ^= h1 >>> 16;

	    return h1;
	  }

}

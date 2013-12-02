package com.niz.punk;

import java.io.Serializable;

public class MonumentInfo implements Serializable{
public int[] x = new int[10];
public int[] y = new int[10];
public byte[] type = new byte[10];
public MonumentInfo(int mx, int my, byte mi){
	x[0] = mx;
	y[0] = my;
	type[0] = mi;
}
public MonumentInfo(){
	x[0] = 0;
	y[0] = 0;
	type[0] = 0;
}
}

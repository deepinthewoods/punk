package com.niz.punk;

public class MemInfoDesktop implements MemInfo {
	public static int returnMegs = 71;
	@Override
	public int getAvailMegs() {
		return returnMegs;
	}
	@Override
	public int getMB() {
		// TODO Auto-generated method stub
		return 0;
	}

}

package com.niz.punk;

public class BlockCornered extends Block {
	public BlockCornered(int id, int met) {
		super(id, met);
	}

	private int lightBits, dayBits;
	
	public int getLightBits() {
		return lightBits;
	}
	public void setLightBits(int lightBits) {
		this.lightBits = lightBits;
	}
	public int getDayBits() {
		return dayBits;
	}
	public void setDayBits(int dayBits) {
		this.dayBits = dayBits;
	}
	public boolean setLightBits(byte[][] m, byte[][] dm) {
		return setLightBitsNew(m, dm);
	}
}

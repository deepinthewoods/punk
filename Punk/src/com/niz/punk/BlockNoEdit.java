package com.niz.punk;

public class BlockNoEdit extends Block{

	public BlockNoEdit(int id, int met) {
		super(id, met);
		// TODO Auto-generated constructor stub
	}
	@Override
	public void set(int id, int meta){
		super.set(0,0);
		setLight(0);
		setDayLight(0);
	}
}

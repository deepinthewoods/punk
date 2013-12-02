package com.niz.punk;

public abstract class ComponentUpdate implements Component {
	public int dropID, dropID2, dropAmount, dropMeta, dropAmount2, dropMeta2;
	public void setDrop(int id, int id2, int am, int am2, int meta, int meta2){
		dropID = id;
		dropID2 = id2;
		dropAmount = am;
		dropAmount2 = am2;
		dropMeta = meta;
		dropMeta2 = meta2;
	}
	
}

package com.niz.punk;

import com.badlogic.gdx.utils.Pool;

public class PlacePacket implements Signal {
	public int x,y, id, meta, plane;
	public static Pool<PlacePacket> pool = new Pool<PlacePacket>(){
		@Override
		protected PlacePacket newObject() {
			return new PlacePacket();
		
		}
		
	};
	public void free(){
		pool.free(this);
	}
	public PlacePacket set(int x2, int y2, int iID, int metaData, int plane) {
		x = x2;
		y = y2;
		id = iID;
		meta = metaData;
		this.plane = plane;
		return this;
	}
}

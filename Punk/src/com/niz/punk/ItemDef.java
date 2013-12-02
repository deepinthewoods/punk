package com.niz.punk;

import com.badlogic.gdx.physics.box2d.World;

public class ItemDef {
	public int gameMode, amount=256, meta;
	public boolean droppable = true;
	public long durability=0;
	public ButtonOverride[] buttonOverrides = new ButtonOverride[Punk.BELTSLOTCOUNT];
	public TouchAction data;
	public String name = "\nname", desc = "description", infoDesc = "info";
	public float xOff= .75f-.0625f*1, yOff= .25f-.0625f, scale = 1f;
	public float angle = 45f;
	public int soundID;
	

	public static ButtonOverride nonBtn = new ButtonOverride(){

		@Override
		public void unPress(PunkMap map, GenericMob mob, PunkBodies mi,
				boolean twoPresses) {
		}

		@Override
		public void pressed(GenericMob mob, PunkMap gMap, World world,
				PunkBodies mi, boolean pressed, float deltaTime) {
		}
		
	};
	public ItemDef(int mode) {
		gameMode = mode;
	}

	public void setData(ItemDef i){
		this.meta = i.meta;
		this.gameMode = i.gameMode;
		this.durability = i.durability;
		this.data = i.data;
	}

	public CharSequence getInfoText() {
		return infoDesc;
	}

	public String getName() {
		return name;
	}
	
	public void finalizeDescription(){
		//String d = desc;
		if (data != null)infoDesc = data.getFinalDescription();
	}

	public String getTouchDescText(int pcl) {
		if (data == null) return "null data";
		return data.getTouchDesc(pcl);
	}
	
}

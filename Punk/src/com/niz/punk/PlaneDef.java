package com.niz.punk;

import com.badlogic.gdx.utils.Array;

public abstract class PlaneDef {
public String name;
public int seed;
public boolean cached = false;
public static String suffix = ".pln";
public abstract void generateColumn(int x, PunkMap map, Chunk c);
public int itemLimit = -1;
public boolean creativeItems = false;
public abstract boolean setUp(PunkBodies monsterIndex, PunkMap map);
public Array<BuildPlan> buildPlans = new Array<BuildPlan>();
public abstract void start(Chunk c);
public abstract boolean finish(PunkMap map, Chunk c);
public abstract PlaneDef loadFromDisk(StringBuilder s);
public abstract void saveToDisk(StringBuilder s);
public MiniMap miniMap = new MiniMap();
public Array<Waypoint> ways = new Array<Waypoint>();

public int getTemp(int x) {
	return 0;// primeMaterialPlane.getTemp(x);
}

public int[] getLandInfo(int temp) {
	return null;//primeMaterialPlane.getLandInfo(temp);
}

public int getGroundHeight(int i) {
	return 0;// primeMaterialPlane.getGroundHeight(i);
}

public int getSmoothness(int x) {
	return 0;//primeMaterialPlane.getSmoothness(x);
}

public void spawn(Player player, PunkMap map){};




}
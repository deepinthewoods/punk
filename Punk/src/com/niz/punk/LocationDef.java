package com.niz.punk;



public class LocationDef {
	//plane or 
	public static enum LocationType {RANDOM, CAMPSITE_JUNGLE, CAMPSITE_SNOW, CAMPSITE_HILLS, CAMPSITE_ANY, CAMPSITE_MOUNTAIN, PLANE};
	public LocationType type;
	public int seed;
	public BuildPlan plan;
}

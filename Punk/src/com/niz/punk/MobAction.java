package com.niz.punk;

public interface MobAction {
	
	public boolean checkPreConditions(GenericMob mob, PunkMap map);
	public void act(GenericMob mob, PunkMap map);
	
}

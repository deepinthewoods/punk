package com.niz.punk;

public class QuestGoalMove extends QuestGoal {
	int x,y,plane;
	
	public void set(int x, int y, int p){
		desc = "move to "+x+","+y + " on "+Chunk.planes.get(p).name;
		this.x = x;
		this.y = y;
		this.plane = p;
		repeating = false;
	}
	@Override
	public boolean checkConditions(GenericMob owner) {
		if (owner.x == x && owner.y == y && owner.p == plane) return true;
		return false;
	}
	@Override
	public void onComplete(GenericMob mob, PunkMap map) {
		// TODO Auto-generated method stub
		
	}

	
	
}

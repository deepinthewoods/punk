package com.niz.punk;

public class Goal {
public int type = 0, //0=go : always a flag

// 1=kill  2=stun  3=idle 
faction = 0;
public PhysicsActor actor;
public BlockLoc loc = new BlockLoc();
//public Quest successQuest;

public void setKill(PhysicsActor act, int faction){
	type = 0;
	actor = act;
}

public void setMove(int x, int y, int faction){
	loc.set(x,y);
	type = 0;
	actor = null;
}
public void setMove(BlockLoc loc, int faction){
	
}

public void update(){
	switch (type){
	case 0:
		
		break;
	}
}


}

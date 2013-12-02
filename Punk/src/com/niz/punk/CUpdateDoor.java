package com.niz.punk;

import com.badlogic.gdx.physics.box2d.World;

public class CUpdateDoor extends ComponentUpdate {
	//danger! variables are the same for all of this type
	int type;
	public CUpdateDoor(int type){
		this.type = type;
		switch (type){
		
		}
	}
	@Override
	public void act(GenericMob mob, PunkMap map, Player player,
			PunkBodies monsterIndex, World world) {
		
		
	}

}

package com.niz.punk.blocks;

import com.badlogic.gdx.physics.box2d.World;
import com.niz.punk.Block;
import com.niz.punk.BlockLoc;
import com.niz.punk.PhysicsActor;
import com.niz.punk.Punk;
import com.niz.punk.PunkMap;

public class Water extends Liquid {

	@Override
	public void mobFeet(PunkMap map, PhysicsActor act, Block b, World world) {
		//act.startSwimming();
		act.isOnFire = false;

	}

	@Override
	public void mobHead(PunkMap map, PhysicsActor act, Block b) {
		if (!act.isHoldingBreath){
			act.isHoldingBreath = true;
			act.breathTimeout = Punk.gTime + 10000;
		}

	}

	@Override
	public void mobFeetExit(PunkMap map, PhysicsActor act, Block b) {
		
		
	}

	

}

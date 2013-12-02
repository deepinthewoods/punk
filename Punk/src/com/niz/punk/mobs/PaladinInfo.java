package com.niz.punk.mobs;

import com.badlogic.gdx.Gdx;
import com.niz.punk.AIPFighter;
import com.niz.punk.AIProcessor;
import com.niz.punk.CMovePressButtons;
import com.niz.punk.CUpdateImp;
import com.niz.punk.CUpdateStates;
import com.niz.punk.ComponentMove;
import com.niz.punk.ComponentUpdate;
import com.niz.punk.GenericMob;
import com.niz.punk.MobInfo;
import com.niz.punk.PunkBodies;
import com.niz.punk.PunkMap;

public class PaladinInfo extends MobInfo {

	public PaladinInfo(){
		super();
		
		
		ComponentUpdate update = new CUpdateImp();		
		ComponentMove move = new CMovePressButtons();
		AIProcessor ai = new AIPFighter();
		set(2, new CUpdateStates(), move, update, true, true, 300, 
				
				true, false, ai);
		name = "Paladin";
		description = "Holy Warriors Devoted to a church, Paladins call upone their Deity's power to aid them in battle.";
		defaultSkills(0);
		
		//startBlessings(0);
		startDeities = new byte[]{0,1,2,3};
		minStats = new int[]{10,10,10,10,10,10};
		classID = 1;
	}
	
	
	@Override
	public void onSpawn(GenericMob mob, PunkMap map) {
		

	}

}

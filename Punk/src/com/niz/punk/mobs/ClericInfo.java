package com.niz.punk.mobs;

import com.niz.punk.AIPFighter;
import com.niz.punk.AIProcessor;
import com.niz.punk.ButtonOverride;
import com.niz.punk.CMovePressButtons;
import com.niz.punk.CUpdateImp;
import com.niz.punk.CUpdateStates;
import com.niz.punk.ComponentMove;
import com.niz.punk.ComponentUpdate;
import com.niz.punk.GenericMob;
import com.niz.punk.MobInfo;
import com.niz.punk.PunkBodies;
import com.niz.punk.PunkMap;
import com.niz.punk.buttons.BOJMiner;
import com.niz.punk.buttons.BOJPaladin;
import com.niz.punk.buttons.BONone;
import com.niz.punk.buttons.BORun;

public class ClericInfo extends MobInfo {

	public ClericInfo(){
		super();
		ComponentUpdate update = new CUpdateImp();		
		ComponentMove move = new CMovePressButtons();
		AIProcessor ai = new AIPFighter();
		set(2, new CUpdateStates(), move, update, true, true, 300, 
				
				true, false, ai);
		name = "Cleric";
		description = "Priest devoted to the gods. ";
		classID = 5;
	}
	@Override
	public void onStart(GenericMob mob){
		//mob.addBlessing(PunkBodies.blessings[1]);
	}
	
	@Override
	public void onSpawn(GenericMob mob, PunkMap map) {
		

	}

}

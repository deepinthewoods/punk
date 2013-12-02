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
import com.niz.punk.PunkMap;
import com.niz.punk.buttons.BOJMiner;
import com.niz.punk.buttons.BOJPaladin;
import com.niz.punk.buttons.BONone;
import com.niz.punk.buttons.BORun;

public class WildMageInfo extends MobInfo {

	public WildMageInfo(){
		super();
		ComponentUpdate update = new CUpdateImp();		
		ComponentMove move = new CMovePressButtons();
		AIProcessor ai = new AIPFighter();
		set(2, new CUpdateStates(), move, update, true, true, 300, 
				
				true, false, ai);
		
		name = "Wild Mage";
		description = "Wild Mages have a natural ability for magic, but have no schooling. They can channel magical forces through their bodies, though they lack the Wizard's ability to fly.";
		classID = 9;
	}
	@Override
	public void onStart(GenericMob mob){
		
	}
	
	@Override
	public void onSpawn(GenericMob mob, PunkMap map) {
		

	}

}

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
import com.niz.punk.buttons.BOJPaladin;
import com.niz.punk.buttons.BONone;
import com.niz.punk.buttons.BORun;

public class SmithInfo extends MobInfo {

	public SmithInfo(){
		super();
		
		
		ComponentUpdate update = new CUpdateImp();		
		ComponentMove move = new CMovePressButtons();
		AIProcessor ai = new AIPFighter();
		set(2, new CUpdateStates(), move, update, true, true, 300, 
				
				true, false, ai);
		name = "Smith";
		description = "Smiths can craft all weapons and armor.";
		defaultSkills(0);
		//Gdx.app.log("smith");
		classID = 13;
		rangePlace = 12;
	}
	@Override
	public void onStart(GenericMob mob){
		
	}
	
	@Override
	public void onSpawn(GenericMob mob, PunkMap map) {
		

	}

}

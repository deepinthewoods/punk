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

public class BuilderInfo extends MobInfo {

	public BuilderInfo(){
		super();
		
		
		ComponentUpdate update = new CUpdateImp();		
		ComponentMove move = new CMovePressButtons();
		AIProcessor ai = new AIPFighter();
		set(2, new CUpdateStates(), move, update, true, true, 300, 
				
				true, false, ai);
		name = "Paladin";
		description = "Holy Warriors fanatically devoted to Lumiera, the Lady of Light. " +//Lumiera
				"Paladins call upon Lumiera's power to strengthen them in battle, making them formidable warriors. " +
				"They are bound by their Oath to act honorably, and breaking that oath can bring serious consequences from their deity.";
		defaultSkills(0,1);
		classID = 12;
	}
	@Override
	public void onStart(GenericMob mob){
		//mob.addBlessing(1);
	}
	
	@Override
	public void onSpawn(GenericMob mob, PunkMap map) {
		

	}

}

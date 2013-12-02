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
import com.niz.punk.PunkMap;

public class WizardInfo extends MobInfo {

	public WizardInfo(){
		super();
		ComponentUpdate update = new CUpdateImp();		
		ComponentMove move = new CMovePressButtons();
		AIProcessor ai = new AIPFighter();
		set(2, new CUpdateStates(), move, update, true, true, 300, 
				true, false, ai);
		name = "Wizard";
		description = "Much has been lost of the Old Magicks. What knowledge remains is jealously guarded by the Wizard Guilds, and shared only with their own.";
		defaultSkills(7);
		//defaultSkillSelection();
		classID = 8;
		baseStamina = 1f;
	}
	@Override
	public void onStart(GenericMob mob){
		//mob.addBlessing(1);
		
	}
	
	@Override
	public void onSpawn(GenericMob mob, PunkMap map) {
		Gdx.app.log("Wiazrd", "start"+defaultSkills);

	}

}

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
import com.niz.punk.buttons.BONone;
import com.niz.punk.buttons.BORun;

public class BarbarianInfo extends MobInfo {

	public BarbarianInfo(){
		ButtonOverride[] classButtons = new ButtonOverride[8];
		classButtons[0] = new BORun(true);
		classButtons[3] = new BORun(false);
		classButtons[0].sibling = classButtons[3];
		classButtons[3].sibling = classButtons[0];
		
		classButtons[1] = new BONone();
		classButtons[2] = new BONone();
		classButtons[4] = new BONone();
		classButtons[5] = new BONone();
		classButtons[6] = new BONone();
		classButtons[7] = new BONone();
		
		this.defaultButtons = classButtons;
		ComponentUpdate update = new CUpdateImp();		
		ComponentMove move = new CMovePressButtons();
		AIProcessor ai = new AIPFighter();
		
		set(2, new CUpdateStates(), move, update, true, true, 300, 
				
				true, false, ai);
		classID = 0;
		name = "Barbarian";
		description = "Savage warriors who stun enemies with their mighty Stomp attack.";
		defaultSkills(1);
		baseStamina = .15f;
		jumpSpeed = 16.5f;
	}
	
	@Override
	public void onStart(GenericMob mob){
		//mob.addBlessing(PunkBodies.blessings[1]);
	}
	
	@Override
	public void onSpawn(GenericMob mob, PunkMap map) {
		

	}

}

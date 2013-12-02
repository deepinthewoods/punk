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

public class RogueInfo extends MobInfo {

	public RogueInfo(){
		super();
		ComponentUpdate update = new CUpdateImp();		
		ComponentMove move = new CMovePressButtons();
		AIProcessor ai = new AIPFighter();
		set(2, new CUpdateStates(), move, update, true, true, 300, 
				
				true, false, ai);
		name = "Rogue";
		description = "Rogues excel at acrobatics and are masters at moving around dungeons.They can bounce off walls and floors to cross large distances.";
		classID = 3;
		defaultSkills(2);
		jumpSpeed = 16f;
		baseStamina = .1f;
		
		this.minStats = new int[]{7,7,7,7,7,7};
	}
	@Override
	public void onStart(GenericMob mob){
		//mob.addBlessing(1);
	}
	
	@Override
	public void onSpawn(GenericMob mob, PunkMap map) {
		

	}

}

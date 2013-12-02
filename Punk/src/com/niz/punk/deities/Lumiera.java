package com.niz.punk.deities;

import com.badlogic.gdx.Gdx;
import com.niz.punk.Deity;
import com.niz.punk.GenericMob;
import com.niz.punk.PunkBodies;

public class Lumiera extends Deity {
public Lumiera(){
	
	name = "Lumiera";
	nickName = "The Lady of LIght";
	desc = "God of Regeneration and Light";
	factionID = 2;
}

public void grantBlessings(GenericMob mob){
	
	if (PunkBodies.factions[factionID].opinion.get(mob.faction.id) > 500){
		mob.blessings.add(0);
		Gdx.app.log("stats", "bless"+mob.faction.id);

	}
}



}

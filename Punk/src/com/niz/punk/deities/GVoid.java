package com.niz.punk.deities;

import com.niz.punk.Deity;
import com.niz.punk.GenericMob;
import com.niz.punk.PunkBodies;

public class GVoid extends Deity {
public GVoid(){
	name = "Void";
	nickName = "He Who Sleeps";
	desc = "God of Nothingness and Death, Void has domain over all that which is out of sight.";
	factionID = 6;
	}

	public void grantBlessings(GenericMob mob){
		if (PunkBodies.factions[factionID].opinion.get(mob.faction.id) > 500){
			mob.blessings.add(4);
		}
	}



	}

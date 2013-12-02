package com.niz.punk.deities;

import com.niz.punk.Deity;
import com.niz.punk.GenericMob;
import com.niz.punk.PunkBodies;

public class Knossos extends Deity {
public Knossos(){
	name = "Knossos";
	nickName = "The Count of Chaos";
	desc = "God of Chaos, Confusion and Electricity.";
	factionID = 5;
	}

	public void grantBlessings(GenericMob mob){
		if (PunkBodies.factions[factionID].opinion.get(mob.faction.id) > 500){
			mob.blessings.add(3);
		}
	}



	}

package com.niz.punk.deities;

import com.niz.punk.Deity;
import com.niz.punk.GenericMob;
import com.niz.punk.PunkBodies;

public class Graviticus extends Deity {
	public Graviticus(){
		name = "Graviticus";
		nickName = "The Great Balance";
		desc = "God of Balance, Nature and Physics.";
		factionID = 7;
	}

	public void grantBlessings(GenericMob mob){
		if (PunkBodies.factions[factionID].opinion.get(mob.faction.id) > 500){
			mob.blessings.add(5);
		}
	}



	}

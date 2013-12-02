package com.niz.punk.deities;

import com.niz.punk.Deity;
import com.niz.punk.GenericMob;
import com.niz.punk.PunkBodies;

public class Ignis extends Deity {
public Ignis(){
	name = "Ignis";
	nickName = "Lord of Destruction";
	desc = "God of Destruction and Fire.";
	factionID = 4;
	}

public void grantBlessings(GenericMob mob){
		if (PunkBodies.factions[factionID].opinion.get(mob.faction.id) > 500){
			mob.blessings.add(2);
		}
	}



	}


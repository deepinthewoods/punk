package com.niz.punk.deities;

import com.niz.punk.Deity;
import com.niz.punk.GenericMob;
import com.niz.punk.PunkBodies;

public class Vakava extends Deity {
public Vakava(){
	name = "Vakava";
	nickName = "Order's Fist";
	desc = "God of Order, Cold";
	factionID = 3;
	}

public void grantBlessings(GenericMob mob){
		if (PunkBodies.factions[factionID].opinion.get(mob.faction.id) > 500){
			mob.blessings.add(1);
		}
	}



	}

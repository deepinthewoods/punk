package com.niz.punk;

import com.badlogic.gdx.physics.box2d.World;

public abstract class Spell{
public String name = "default spell";
public int school, prefix, suffix;
public static final String[] Prefixes = {"Fire", "Frost", "Charge"};
public Spell(String nam, int sch){
	name = nam;
	school = sch;
}
	public abstract void act(GenericMob mob, PunkMap map,
			PunkBodies monsterIndex, World world); 
		
	

}

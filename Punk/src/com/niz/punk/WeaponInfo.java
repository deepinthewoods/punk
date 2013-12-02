package com.niz.punk;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.World;

public class WeaponInfo extends TouchAction{
	public float speed, range;;
	int damage, damageDistribution;
	public Effect[] effects = new Effect[EFFECTS_COUNT];
	public static final int EFFECTS_COUNT = 4;
	public int damageType;
	//boolean hasChain;
	public int prof = 0, attacks = 1;
	public int[] modes = {0};
	public boolean twoHanded = false;
	public static final int  PROF_DAGGER=0, PROF_SWORD=1, PROF_LONGSWORD=2, PROF_AXE=3, 
			PROF_SPEAR=4, PROF_POLEAXE=5, PROF_STAFF =6,
			PROF_CLUB=7, PROF_HAMMER=8,
			PROF_WHIP=9, PROF_FLAIL=10, PROF_PICKAXE=11,
			PROF_BOW=12, PROF_CROSSBOW=13, PROF_SLING=14
			,TH = 12, OH = 13, OH2 = 15, TH2 = 14;
	public static final int DT_PIERCING = 1, DT_SLASHING = 0;
	public WeaponInfo(PunkMap map, PunkBodies mi, World world, int damage, int damageDistr, float speed, float range, int type, int prof){
		super(map, mi, world);
		this.speed = speed;
		this.damage = damage;
		this.damageDistribution = damageDistr;
		this.range = range;
		this.damageType = type;
		aiNeuronIndex = 0;
		//touchDesc =  "tap screen while facing an enemy to attack.";
		super.type = TouchAction.MELEE;
		
		//this.hasChain = hasChain;
	}
	
	public static final String[] damageTypeNames = {"slashing", "piercing"}, 
			proficiencyNames = {"Dagger", "Short Sword", "Long Sword", "Curved Sword", "Spear", "Axe", "Staff", "Club"
		,"Hammer", "Whip", "Flail", "Hand Tool", "Bow", "Crossbow", "Sling", "2-Handed Sword", "2-Handed Axe", "2-handed Hammer"
		, "Tool"};
	
	@Override
	public boolean touchDown(GenericMob mob) {
		if (mob.state == 0 || mob.state == 10 || mob.state == 4 || mob.state == 5 || mob.state == 9){
			mob.attacksRemaining = attacks;
			mob.state = modes[MathUtils.random(modes.length-1)]+12
					;
			//mob.state = TH2;
			//Gdx.app.log("weapon", "click");
			return true;
		}
		return false;
	}
	@Override
	public void touchUp(GenericMob mob) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void cancelTouch(GenericMob mob) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void beltSelected(GenericMob mob) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onBeltUnselect(GenericMob mob) {
		mob.state = 10;
		
	}
	@Override
	public void stats(GenericMob mob) {
		if (mob.state >= 12 && mob.state <= 17)mob.speedMultiplier *= speed;
		
	}
	@Override
	public void unPress(PunkMap map, GenericMob mob, PunkBodies mi,
			boolean twoPresses) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void pressed(GenericMob mob, PunkMap gMap, World world,
			PunkBodies mi, boolean pressed, float deltaTime) {
		
		
	}
	public String getFinalDescription() {
		return ""+proficiencyNames[prof]+"\n"+damage + " "+damageTypeNames[damageType] + " damage.";
		
	}
	
	public int getDamage() {
		switch (damageDistribution){
		default:
		case 0:
			return MathUtils.random(1,damage*2);
		case 1:
		{
			int a = MathUtils.random(1,damage*2), b = MathUtils.random(1,damage*2);;
			return Math.max(a, b);
		}
		case 2:
		{
			int a = MathUtils.random(1,damage), b = MathUtils.random(1,damage);
			return a+b;
		}
		
		case 3:
		{
			int a = MathUtils.random(1,damage), b = MathUtils.random(1,damage), 
					c = MathUtils.random(1,damage), d = MathUtils.random(1,damage);
			return Math.max(a,b)+Math.max(c, d);
		}
			
		}
	}
	
}

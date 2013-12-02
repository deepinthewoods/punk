package com.niz.punk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.niz.punk.buttons.BONone;
import com.niz.punk.buttons.BORun;

public abstract class MobInfo {
	
	int bodyID; 
	Component states; 
	ComponentMove move; 
	Component update;
	boolean hasBBs; 
	boolean isRandomUpdate;
	int updateInterval; 
	IntArray validBlocks = new IntArray(3);
	public int[] immunity;
	int sizeX; int sizeY;
	public boolean isImmuneToFire = false, burnsInDaylight = false;
	public int minRange, idealRange = 3;
	boolean needsDark = false;
	public int timeFlags = 0;
	public int zone;//0=groundheight, -1,-2 undergdound, 1 air
	public int level = 1;
	//public Faction faction;
	public int visualRange = 16, rangePlace = 8;
	public int rangeVariance = 0;
	public boolean climbsTrees = false;
	public int defaultFaction = 0, defaultDeity = 0;;
	public boolean pushable = false;
	public int animID = 0, classID;
	public int[] minStats = {0,0,0,0,0,0,0,0};
	public int[] statsDistribution = {0,1,2,3};
	public byte[] startDeities = new byte[0];
	public int startStatPointTotal = 30;
	public int[] defaultSkills = {}, skillTree = {}, startBlessings = {};
	public String name = "class  |", description = " No Description ";
	public float runSpeed = 6f, runAccel = .1f, jumpSpeed = 9.75f;
	//public ButtonOverride jumpButton, runButton;
	public ButtonOverride[] defaultButtons = new ButtonOverride[Punk.BUTTONOVERRIDECOUNT];
	public String toString(){return name;};
	public int[] quests = new int[1];
	public static final int 
	Q_ATTACK_ENEMY_MELEE = 1,
	Q_ATTACK_ENEMY_THROWN = 2
	,Q_ATTACK_ENEMY_SPELL = 3
	,Q_ATTACK_ENEMY_WAND = 4
	,Q_ATTACK_ENEMY_BOW = 5
	
	
	
			;
	public MobInfo defaultSkills(int... a){
		defaultSkills = a;
		for (int i = 0,n=defaultSkills.length;i<n;i++){
			//Gdx.app.log("mobinfo", "v "+defaultSkills[i]);
		}
		return this;
	}
	
	public MobInfo defaultQuests(int... a){
		quests = a;
		return this;
	}
	
	public MobInfo startBlessings(int... a){
		startBlessings = a;
		return this;
	}
	public MobInfo skillTree(int... a){
		skillTree = a;
		return this;
	}
	public MobInfo(){
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
	}
	public void setImmunity(boolean fire){
		isImmuneToFire = fire;
	}
	
	public Array<Item> inv = new Array<Item>();
	public int[] weaponProficiencies = new int[GenericMob.WEAPON_PROFICIENCIES_COUNT];
	public int hp = 10;
	public float baseStamina = .32f;
	public int maxHealth = 10;
	public AIProcessor ai;
	
	
	public void set(int bodyID, Component states, ComponentMove move, Component update,
			 boolean hasBBs, boolean isRandomUpdate, int updateInterval, 
			 
			 boolean burns, boolean climbs, AIProcessor ai){
		this.bodyID = bodyID;
		this.states = states;
		this.move = move;
		this.update = update;
		this.hasBBs = hasBBs;
		this.updateInterval = updateInterval;
		this.ai = ai;
		burnsInDaylight = burns;
		//this.faction = faction;
		sizeX = 0;
		sizeY = 1;
	}
	
	public void setLevel(int l){
		level = l;
	}
	
	public void setSpawnBlockIDs(boolean needsDark, int zone, int...blocks){
		this.needsDark = needsDark;
		validBlocks.clear();
		for (int i = 0; i < blocks.length; i++)
			validBlocks.add(blocks[i]);
		this.zone = zone;
	}
	
	public void setTimeFlags(int min, int max){
		timeFlags = 0;
		for (int i = min; i <= max; i++){
			timeFlags += 1<<(i-1);
		}
	}
	
	public boolean canSpawn(Block b){
		//Gdx.app.log("mobinfo", "checking spawn, b:"+b+" self:"+validBlocks.get(0)+"effectiveLight"+b.effectiveLight());
		//Vboolean lowLight = (effectiveLight < PunkBodies.SPAWNLIGHTLEVEL);
		if (needsDark && b.effectiveLight() > PunkBodies.SPAWNLIGHTLEVEL) return false;
		//
		//Gdx.app.log("mobinfo","zone:"+this.zone+" map zone:"+zone + "contains?"+validBlocks.contains(b.blockID));
		if ((zone == this.zone && validBlocks.contains(b.blockID))){
			//Gdx.app.log("mobinfo", "got through");
			return true;
		}
		return false;
	}
	public abstract void onSpawn(GenericMob mob, PunkMap map);

	public void onStart(GenericMob mob) {
		// TODO Auto-generated method stub
		
	}
	
}

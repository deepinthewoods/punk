package com.niz.punk;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.IntArray;

public class Faction {
	public class Reward{
		public int size;
	}
	public int
	size
	,wealth
	,chaos// helps random/helps friends. Attacks random/attacks weakest
	,proselytism//spreads word/internal conflicts
	,community //work together/separate families
	,evil //helps / attacks
	,underdogAdvantage
	,leaderType//different strategies
	;
	public static final int FACTIONCOUNT = 100;
	public IntArray opinion = new IntArray(FACTIONCOUNT);
	public int id;
	public int minionID, minionOffset;
	public Faction(int id){
		this.id = id;
		for (int i = 0; i < FACTIONCOUNT; i++){
			opinion.add(-2);
		}
	}
	public void getQuest(){
		
	}
	public static enum MissionType {CAPTURE, DAMAGE, DEFEND, ESCORT, GATHER, TRADE, GIVE, KILL, SPY, RESCUE};
	
	public void runMission(MissionType type, Faction target, Reward reward){
		
				//roll for success
				float success = MathUtils.random();
				//size vs size
				int targetRoll = MathUtils.random(target.size);
				boolean adv = false;
				if (size-target.size > target.size) {
					targetRoll += underdogAdvantage;
					adv = true;
				}
				if (MathUtils.random(size) > targetRoll){
					win(type, target, reward);
					
				} else if (adv){underdogAdvantage++;}
				
				
			
	}

	private void win(MissionType type, Faction target, Reward reward) {
		
		
	}
	
}

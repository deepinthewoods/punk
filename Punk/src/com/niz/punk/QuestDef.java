package com.niz.punk;

import com.badlogic.gdx.utils.Array;

public class QuestDef {
	//spawnConditions conditions. needs all of these to start. 
	Array<QuestCondition> conditions = new Array<QuestCondition>();
	/*
	 * ie 
	 * finished x quest
	 * player level x
	 * city power level x
	 * 
	 * 
	 * also needs
	 */
	
	
	
	/*
	 *  goals
	 * 		move to x,y   
	 * 		kill name /count of type
	 * 		destroy block
	 * 		
	 */
	Array<QuestGoal> goals = new Array<QuestGoal>();
	
	//description
	String description;
	//responses
	Array<QuestResponse> responses = new Array<QuestResponse>();
	
	
	
}

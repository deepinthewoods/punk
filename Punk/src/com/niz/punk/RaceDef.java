package com.niz.punk;

public class RaceDef {
public int[] statBonusses = {0,0,0,0,0,0,0,0,0};
public int[] maxStats = {5,5,5,5,5,5};
public String name = " ";
public int[] startSkills, startLearnedSkills;
	public RaceDef(String string, int[] skills, int[] learnedSkills) {
		name = string;
		this.startSkills = skills;
		this.startLearnedSkills = learnedSkills;
	}
	public String toString(){return name;};
	
}

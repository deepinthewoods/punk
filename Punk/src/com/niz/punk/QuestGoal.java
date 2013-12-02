package com.niz.punk;

public abstract class QuestGoal {
public abstract boolean checkConditions(GenericMob owner);//returns true if goal is reached
String desc;
public abstract void onComplete(GenericMob mob, PunkMap map);
public boolean repeating = false;
public int weight;
}
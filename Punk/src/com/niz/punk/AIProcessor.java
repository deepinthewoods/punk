package com.niz.punk;

public abstract class AIProcessor {
	AINeuron[] neurons;
	
	public void act(GenericMob mob, PunkMap map){
		//check valid ais and equip stuff on belt
		/*for (int i = 0; i < Punk.BELTSLOTCOUNT; i++){
			mob.aiValid[i] = false;
		}
		for (int i = 0; i < PunkInventory.INVENTORYSIZE; i++){
			ItemDef def = mob.inv.getItemInfo(i);
			TouchAction action = def.data;
			if (!mob.aiValid[action.aiNeuronIndex]){//first
				mob.aiValid[action.aiNeuronIndex] = true;
				mob.belt[action.aiNeuronIndex]= i; 
			} else {//compare
				if (def.better(mob.inv.getAmountByID(mob.belt[action.aiNeuronIndex]))){
					mob.belt[action.aiNeuronIndex] = i;
				}
			}
		}*/
		
		
		
		
		int active = 0, high = 0;
		for (int i = 0; i < neurons.length; i++){
			if (!mob.aiValid[i]) continue;
			int result = neurons[i].assess(mob);
			if (result > high){
				active = i;
				high = result;
			}
		}
		mob.aiActiveNeuron = active;
		
	}

	public void bump(GenericMob mob, GenericMob target){
		TouchAction data = mob.inv.getItemInfo(mob.belt[mob.activeInvSlot]).data;
		if (data instanceof WeaponInfo){
			mob.isLeft = (mob.x > target.x);
			WeaponInfo w = (WeaponInfo) data;
			w.touchDown(mob);
		}
	}
}

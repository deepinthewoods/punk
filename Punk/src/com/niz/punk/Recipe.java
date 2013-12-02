package com.niz.punk;

import com.badlogic.gdx.utils.Array;

public class Recipe {
	public class RecipeItem{
		public int id;
		public int q;
		public boolean needsMeta = false;
		public int meta;
		public RecipeItem(int iID, int quant){
			id = iID;
			q = quant;
		}
		public RecipeItem(int iID, int quant, int meta){
			id = iID;
			q = quant;
			needsMeta = true;
			this.meta = meta;
		}
	}
	public Array<RecipeItem> list;
	public int out, outQ, outM = 0;
	public int cat;
	public String name;// = new String("");
	//This stores one recipe
	public Recipe(int iID, int quant, String s, int o, int oq){
		list = new Array<RecipeItem>();
		list.add(new RecipeItem(iID, quant));
		out = o;
		outQ = oq;
		name = new String(s);
	//	name = "HHHHHHHHHHHHHHHHHH";
	}
	public Recipe(int iID, int quant, String s, int o, int oq, int oM){
		list = new Array<RecipeItem>();
		list.add(new RecipeItem(iID, quant));
		out = o;
		outQ = oq;
		name = new String(s);
		//name = "HHHHHHHHHHHHHHHHHH";
		outM = oM;
	}
	public Recipe(int iID, int quant, int meta, String s, int o, int oq, int oM){
		list = new Array<RecipeItem>();
		list.add(new RecipeItem(iID, quant, meta));
		out = o;
		outQ = oq;
		name = new String(s);
		//name = "HHHHHHHHHHHHHHHHHH";
		outM = oM;
	}
	public boolean isValid(PunkInventory inv){
		boolean isValid = true;
		if (list.size == 0) isValid = false;
		for (int i = 0; i < list.size; i++){
			if (list.get(i).needsMeta){
				if (!inv.contains(list.get(i).id, list.get(i).q, list.get(i).meta)) isValid = false;
			}else {
				if (!inv.contains(list.get(i).id, list.get(i).q)) isValid = false;
			}
			
			
		}
		
		return isValid;//works
	}
	public int size(){
		//number of recipeitems
		return list.size;
	}
	public int maxCraftable(PunkInventory inv){
		//sees how many you can make
		int count = 64;
		for (RecipeItem recI:list){
			if (inv.idCount(recI.id)/recI.q < count) 
				count = inv.idCount(recI.id)/recI.q;
		}
		return count;
	}
	
	//public void set(int inID, int quant, int outID){
	//	out = outID;
		//add(inID, quant);
	//}
	public void clear(){
		list.clear();
		out = 0;
		
	}
	public void setOutMeta(int m){
		outM = m;
	}
	public void add(int iID, int quant){
		list.add(new RecipeItem(iID, quant));
		
		
	}
}

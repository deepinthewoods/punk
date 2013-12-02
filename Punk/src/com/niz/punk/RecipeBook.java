package com.niz.punk;

import com.badlogic.gdx.utils.Array;

public class RecipeBook {
	public Array<Recipe> recipes;;
	public int[] tmpArr;
	public Array<Recipe> validRecipes;// = new Recipe[6];
	
	public RecipeBook(){
		//initialize all recipes here
		recipes = new Array<Recipe>();
		validRecipes = new Array<Recipe>();
		
		
	}
	
	public void addCrafts(){
		recipes.add( new Recipe(37,1,"Torch", 50, 4));
	
		
		Recipe 	r = new Recipe(15, 1, "Wood Planks", 37, 4);
		recipes.add(r);
		
		r = new Recipe(16, 1, "Wood Planks", 37, 4);
		recipes.add(r);
		r = new Recipe(54, 1, "Wood Planks", 37, 4);
		recipes.add(r);
		r = new Recipe(17, 1, "Wood Planks", 37, 4);
		recipes.add(r);
		
	 	r = new Recipe(37, 4, "Chest", 8, 1);
		recipes.add(r);
		
		/*r = new Recipe(37, 6, "Sign", 43, 1);
		recipes.add(r);*/
		
		/*r = new Recipe(260, 4, "Bridge", 307, 1);
		r.add(37, 2);
		recipes.add(r);*/
		r = (new Recipe(1,3, "Stone Pickaxe", 414, 1, 50));
		r.add(37,1);//wood
		recipes.add(r);
		
		r = (new Recipe(25,3, "Gold Pickaxe", 415, 1, 100));
		r.add(37,1);//wood
		recipes.add(r);
		
		r = (new Recipe(26,3, "Copper Pickaxe", 416, 1, 100));
		r.add(37,1);//wood
		recipes.add(r);
		
		
		r = (new Recipe(27,3, "Iron Pickaxe", 417, 1, 200));
		r.add(37,1);//wood
		recipes.add(r);
		
		
		r = (new Recipe(28,3, "Diamond Pickaxe", 418, 1, 500));
		r.add(37,1);//wood
		recipes.add(r);
		
		r = new Recipe(1, 4, "Stone Flail", 419, 1, 128);
		r.add(260,1);		
		recipes.add(r);
		
		r = new Recipe(25, 4, "Gold Flail", 420, 1, 128);
		r.add(260,1);		
		recipes.add(r);
		
		r = new Recipe(26, 4, "Copper Flail", 421, 1, 200);
		r.add(260,1);		
		recipes.add(r);
		
		r = new Recipe(27, 4, "Iron Flail", 422, 1, 500);
		r.add(260,1);
		recipes.add(r);
		
		r = new Recipe(28, 4, "Diamond Flail", 423, 1, 500);
		r.add(37,1);
		recipes.add(r);
		
		
		r = new Recipe(1, 4, "Stone Shovel", 424, 1, 128);
		r.add(37,1);		
		recipes.add(r);
		
		r = new Recipe(25, 4, "Gold Shovel", 425, 1, 128);
		r.add(37,1);		
		recipes.add(r);
		
		r = new Recipe(26, 4, "Copper Shovel", 426, 1, 200);
		r.add(37,1);		
		recipes.add(r);
		
		r = new Recipe(27, 4, "Iron Shovel", 427, 1, 500);
		r.add(37,1);
		recipes.add(r);
		
		r = new Recipe(28, 4, "Diamond Shovel", 428, 1, 500);
		r.add(37,1);
		recipes.add(r);
		
		
		r = new Recipe(26, 3, "Bucket", 310, 1);
		recipes.add(r);
		
		r = new Recipe(2,1, "Dirt Clod", 430, 4);
		recipes.add(r);
		
		r= new Recipe(1, 1, "Rock", 431, 4);
		recipes.add(r);
		
		r= new Recipe(2,2, "Bouncy Dirt Clod", 439, 8);
		r.add(260, 1);
		recipes.add(r);
		
		r= new Recipe(1, 2, "Bouncy Rock", 440, 8);
		r.add(260, 1);
		recipes.add(r);
		
		r = (new Recipe(49,1, "White Powder", 259, 1));
		r.add(260,1);
		r.add(30,1);
		recipes.add(r);
		
		r = (new Recipe(32,1, "Black Powder", 258, 1));
		r.add(30,1);
		recipes.add(r);
		
		
		r = new Recipe(28, 1, "Fire Wand 1", 270, 1, 256);
		r.add(37, 2);
		recipes.add(r);
		
		r = new Recipe(28, 2, "Fire Wand 2", 271, 1, 64);
		r.add(37, 2);
		recipes.add(r);
		
		r = (new Recipe(1,3, "Stone Axe", 350, 1, 64));
		r.add(37,1);//wood
		recipes.add(r);
		
		r = (new Recipe(25,3, "Gold Axe", 351, 1, 80));
		r.add(37,1);//wood
		recipes.add(r);
		
		r = (new Recipe(26,3, "Copper Axe", 352, 1, 150));
		r.add(37,1);//wood
		recipes.add(r);
		
		r = (new Recipe(27,3, "Iron Axe", 353, 1, 300));
		r.add(37,1);//wood
		recipes.add(r);
		
		/*r = (new Recipe(450,1, "Nitroglycerin", 258, 1));
		r.add(30,1);
		recipes.add(r);*/
		
		//sticky blocks
		/*r = (new Recipe(2,1, "Sticky:Dirt", 303, 1));
		r.add(260,1);//goo
		r.setOutMeta(2);
		recipes.add(r);
		
		r = (new Recipe(23,1, "Sticky:Cobblestone", 303, 1));
		r.add(260,1);//goo
		r.setOutMeta(23);
		recipes.add(r);
		*/
		//pickaxes
	
		
		
		r = (new Recipe(1,3, "Stone HandAxe", 400, 1, 50));
		r.add(37,1);//wood
		recipes.add(r);
		
		r = (new Recipe(25,3, "Gold HandAxe", 401, 1, 100));
		r.add(37,1);//wood
		recipes.add(r);
		
		r = (new Recipe(26,3, "Copper HandAxe", 402, 1, 100));
		r.add(37,1);//wood
		recipes.add(r);
		
		
		r = (new Recipe(27,3, "Iron HandAxe", 403, 1, 200));
		r.add(37,1);//wood
		recipes.add(r);
		
		
		r = (new Recipe(28,3, "Diamond HandAxe", 404, 1, 500));
		r.add(37,1);//wood
		recipes.add(r);
		
		/*
		
		//launchers
		r = new Recipe(25, 8, "Launcher(Copper)", 320, 1, 32);
		r.add(259, 16);//white powder
		recipes.add(r);
		
		r = new Recipe(26, 8, "Launcher(Iron)", 321, 1, 64);
		r.add(259, 16);//white powder
		recipes.add(r);
		
		r = new Recipe(27, 8, "Launcher(Mithril)", 322, 1, 256);
		r.add(259, 16);//white powder
		recipes.add(r);*/
		
		//ammo
		r=  new Recipe(25, 1, "Grenade(Gold)", 433, 1);
		r.add(258, 1);//black powder
		recipes.add(r);
		
		r=  new Recipe(26, 1, "Grenade(Bronze)", 434, 1);
		r.add(258, 2);//black powder
		recipes.add(r);
		
		r=  new Recipe(27, 1, "Grenade(Iron)", 435, 1);
		r.add(258, 4);//black powder
		recipes.add(r);
		
		r=  new Recipe(25, 1, "Pipe Bomb(Gold)", 436, 1);
		r.add(259, 1);//white powder
		recipes.add(r);
		
		r=  new Recipe(26, 1, "Pipe Bomb(Bronze)", 437, 1);
		r.add(259, 2);//white powder
		recipes.add(r);
		
		r=  new Recipe(27, 1, "Pipe Bomb(Iron)", 438, 4);
		r.add(259, 4);//white powder
		recipes.add(r);
		
		/*r = new Recipe(260, 1, "Sticky Dirt", 303, 2);
		r.add(2, 1);
		recipes.add(r);
		
		r = new Recipe(260, 1, "Sticky Stone", 303, 1);
		r.add(1, 1);
		recipes.add(r);
		
		r = new Recipe(260, 1, "Sticky Cobblestone", 303, 1);
		r.add(23, 1);
		recipes.add(r);
		
		r = new Recipe(25, 12, "TommGun(Copper)", 290, 1, 256);
		r.add(259, 12);//white powder
		recipes.add(r);
		
		r = new Recipe(26, 12, "TommGun(Iron)", 291, 1, 512);
		r.add(259, 12);//white powder
		recipes.add(r);
		
		r = new Recipe(27, 12, "TommGun(Mithril)", 292, 1, 1024);
		r.add(259, 12);//white powder
		recipes.add(r);*/
		
		
	}
	public void addMonuments(){
		Recipe r;
		r = new Recipe(10, 6, "Bed Monument built. Changes made to this chunk will now be saved", 1, 0);
		r.add(11, 8);//skygrass
		recipes.add(r);
	}
	public String getCatName(int cat){
		switch (cat){
		case 0: return "Misc";
		case 1: return "blah";
		default: return "default";
		}
	}
	public int getValidSize(PunkInventory inv){
		//only needs to be between 0 and 11
		int count = 0;
		int rTotal = 0;
		validRecipes.clear();
		boolean isDone = false, dupeFlag = false;
		while (count < recipes.size){
			//recipes[]? 
			//iterate through recipes[], if one comes back valid count++
			if (recipes.get(count).isValid(inv)){
				////Gdx.app.log("recipeBook", "found valid recipe, checking dupes");
				//need to check for dupes
				dupeFlag = false;
				for (Recipe oneOut:validRecipes)
					if (oneOut.out == recipes.get(count).out)
						dupeFlag = true;
				////Gdx.app.log("recipeBook", "dupe" + dupeFlag);
				if (!dupeFlag){
					rTotal++;
					validRecipes.add(recipes.get(count));
					
					//catOuts[rTotal] = ;
				}
				
			}
			count++;
		}
		return rTotal;
	}

	
}

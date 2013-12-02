package com.niz.punk;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

import com.badlogic.gdx.Gdx;

public class PunkInventory implements Serializable{
	private static final String TAG = "inv";
	public static int INVENTORYSIZE = 36;
	public static int[] axeDamages = {0,1,2,3,4};
	private Item[] items = new Item[INVENTORYSIZE];
	private Player l_player = null;
	public transient RecipeBook rb;// = new RecipeBook();
	public int chestID = -1;
	
	public PunkInventory(Player player){
		for (int i = 0; i < INVENTORYSIZE; i++) 
		{
			items[i] = new Item();
		}
		rb = new RecipeBook();
		l_player = player;

	}
	public PunkInventory(int id){
		for (int i = 0; i < INVENTORYSIZE; i++) 
		{
			items[i] = new Item();
		}
		chestID = id;
	}
	
	public PunkInventory() {
		for (int i = 0; i < INVENTORYSIZE; i++) 
		{
			items[i] = new Item();
		}
		//Gdx.app.log(TAG, "no constructor");
	}
	public boolean writeToFile(DataOutputStream out){
		try {
			for (int i = 0; i < PunkInventory.INVENTORYSIZE; i++){
				out.writeInt(items[i].id);
				out.writeInt(items[i].amount);
				out.writeInt(items[i].meta);
				out.writeLong(items[i].durability);
			}
			out.close();
		} catch(IOException ex){
			Gdx.app.log("player", "error saving chest");
			return false;
		}
		return true;
	}
	
	public boolean readFromFile(DataInputStream in){
	try {

			for (int i = 0; i < PunkInventory.INVENTORYSIZE; i++){
				items[i].set(
						in.readInt(),
						in.readInt(),
						in.readInt(),
						in.readLong());
				
			}
			in.close();
		} catch(IOException ex){
			Gdx.app.log("player", "error reading chest");
			return false;
		}
		return true;
	}
	
	
	
	public void clear(){
		for (int i = 0; i < INVENTORYSIZE; i++)
			destroyFromSlot(i);
	}

	public void set(Stats stats){
		clear();
		for (int i = 0; i < INVENTORYSIZE; i++){
			setItem(i,
					(int)stats.inv[i*4],
					(int)stats.inv[i*4+1],
					(int)stats.inv[i*4+2], 
					stats.inv[i*4+3]);
		}
	}
	public void destroyFromSlot(int slot){
		//Gdx.app.log(TAG, "destroy"+slot+"  /"+INVENTORYSIZE + (items[slot] == null));
		items[slot].destroy();
		
	}
	public int avail;
	public void arrangeInventory(){
		for (int i = 0; i < INVENTORYSIZE; i++)
		items[i].checkValidity();
	}
	
	
	
	
	public int idCount(int iID){
		int count = 0;
		for (int i = 0; i < INVENTORYSIZE; i++){
			if (items[i].id == iID) count += items[i].amount;
		}
		return count;
	}
	
	public boolean hasFreeSlot(){
		for (int i = 0; i < INVENTORYSIZE; i++)
			if (items[i].amount == 0 && (l_player == null || i != l_player.activeQuickSlot)) return true;
		return false;
	}
	public int getFreeSlot(){
		for (int i = 0; i < INVENTORYSIZE; i++)
			if (items[i].amount == 0 &&(l_player == null ||  i != l_player.activeQuickSlot)) return i;
		return -2;
	}
	
	public boolean contains(int iID, int count){
		boolean found = false;
		for (int i = 0; i < INVENTORYSIZE; i++){
			if (items[i].id == iID && items[i].amount >= count) return true;
		}
		return false;
	}
	
	public boolean contains(int iID, int count, int meta){
		boolean found = false;
		for (int i = 0; i < INVENTORYSIZE; i++){
			if (items[i].id == iID && items[i].amount >= count && items[i].meta == meta) return true;
		}
		return false;
	}

	
	public void useItem(int itemID, int amount){
		//jsut subtracts amount of that item
		int total = amount;
		int count = 0;
		while (count < INVENTORYSIZE && total >0){
			if (items[count].id == itemID){
				if (items[count].amount > amount) {//if there's enough
					items[count].amount -=amount;
					total -= amount;
				} else{
					total -= items[count].amount;
					items[count].amount = 0;
				}
			}
			count++;
		}
	}
	
	public void useItem(int itemID, int amount, int meta){
		//jsut subtracts amount of that item
		int total = amount;
		int count = 0;
		while (count < INVENTORYSIZE && total >0){
			if (items[count].id == itemID && items[count].meta == meta){
				if (items[count].amount > amount) {//if there's enough
					items[count].amount -=amount;
					total -= amount;
				} else{
					total -= items[count].amount;
					items[count].amount = 0;
				}
			}
			count++;
		}
	}
	

	public boolean hasSpaceFor(int id, int am){
		for (int i = 0; i < INVENTORYSIZE; i++)
			if (((items[i].id == id  && items[i].amount < getStackSize(id)) || items[i].amount == 0) && (l_player == null || i != l_player.activeQuickSlot))
				am -= (getStackSize(id)-items[i].amount);
		return am < 1;
	}
	
	public void addItem(Item item){
		addItem(item.id, item.amount, item.meta, item.durability);
		PunkBodies.playItemSound();	
	}
	

	public boolean addItem(int itemID, int amount, int meta, long durability){//this isn't right
		//needs to look through inventory and merge if it can, else add to a new pile
		//Gdx.app.log("inventory", "Item added:"+itemID+"x"+amount+" meta:"+meta);
		int index = 0, total = 0;
		while (amount > 0 && index < INVENTORYSIZE){//dole out
			if (items[index].id == itemID && items[index].meta == meta && items[index].amount < getStackSize(items[index].id) &&(l_player == null ||  index != l_player.activeQuickSlot))
			{
				total = Math.min(amount,getStackSize(items[index].id) - items[index].amount);
				items[index].amount+= total;
				amount -= total;
			}
			index++;
		}
		//then just place
		index = 0;
		while (amount > 0 && index < INVENTORYSIZE){
			if (items[index].id == 0 && amount > 0 && (l_player == null || index != l_player.activeQuickSlot))
			{
				items[index].id = itemID;
				total = Math.min(amount, getStackSize(items[index].id));
				items[index].amount = total;
				items[index].meta = meta;
				items[index].durability = durability;
				amount -= total;
				amount = 0;
			}
			index++;
		}
		arrangeInventory();
		if (amount > 0) return false;
		return true;

	}
	public void addItem(int iID, int am){
		addItem(iID, am, 0, 0);
	}
	public int getAmountByID(int iID){
		int total = 0;
		for (int i = 0; i < INVENTORYSIZE; i++){
			if (items[i].id == iID)
				total += items[i].amount;
		}
		return total;
	}
	public int getItemAmount(int index){
		return items[index].amount;
	}
	public int getItemID(int index){
		if (index >=0)return items[index].id;
		else return 0;
	}
	public int getItemMeta(int index){
		return items[index].meta;
	}
	
	public void setItemMeta(int index, int met){
		items[index].meta = met;
	}
	public void setItem(int index, int iID, int amount, int meta, long dur){
		items[index].meta = meta;
		items[index].id = iID;
		items[index].amount = amount;
		items[index].durability = dur;
	}
	public boolean useUpItem(int index)
	{
		if (!Player.permissions.hasInfiniteBlocks)
			items[index].amount -=1;
		
		arrangeInventory();
		if (items[index].amount <= 0) return true;
		return false;
	}
	
	public boolean reduceDurability(int qs){//return true if the item's used up
		return items[qs].reduceDurability();
	}
	public boolean reduceDurability(int qs, long val){//return true if the item's used up
		return items[qs].reduceDurability(val);
	}

	
	public void craftItem(Recipe rec, int amount){
		//Gdx.app.log("inventory", "crafting:"+amount);
		int count = 0;
		while (count < amount){
			for (int i = 0; i < rec.size(); i++){
				if (rec.list.get(i).needsMeta){
					useItem(rec.list.get(i).id, rec.list.get(i).q, rec.list.get(i).meta);
				}
				else useItem(rec.list.get(i).id, rec.list.get(i).q);
			}
			addItem(rec.out, rec.outQ, rec.outM, PunkBodies.getItemInfo(rec.out, rec.outM).durability);
			count++;
		}
	}

	
	public static long getMaxDurability(int iID, int meta){
		return PunkBodies.getItemInfo(iID, meta).durability;
	}
	
	public static int getStackSize(int iID){
		return PunkBodies.getItemInfo(iID, 0).amount;
	}
	
	public void dispose(){
		//rb.free();
	}
	public boolean hasNumber(int iid, int meta){
		return PunkBodies.getItemInfo(iid, meta).durability == 0;
	}
	public static boolean hasDurability(int iid, int meta){
		return PunkBodies.getItemInfo(iid, meta).durability != 0;
	}
	
	
	public long getItemDurability(int index) {
		return items[index].durability;
		
	}
	private Item tmpItem = new Item();
	
	
	public Item getItem(int index) {
		
		return items[index];
	}
	
	
	public void moveToBelt(int ind) {
		//try add
		int merge = -1;
		for (int i = 0; i < Punk.BELTSLOTCOUNT; i++){
			if (getItemID(ind) == getItemID(i) && getItemMeta(ind) == getItemMeta(i) && getItemAmount(i) < getStackSize(getItemID(i))) merge = i;
		}
		if (merge != -1){
			useUpItem(ind);
			incrementTotal(merge);
			return;
		}
		int space = -1;
		for (int i = 0; i < INVENTORYSIZE; i++){
			if (getItemID(i) == 0){
				space = i;
				break;
			}
		}
		if (space != -1){
			swap(Punk.BELTSLOTCOUNT-1, space);
			if (getItemID(space) <0) setItem(space, 0, 0, 0, 0);
			for (int i = Punk.BELTSLOTCOUNT-1; i >0; i--){
				swap(i,i-1);
			}
			tmpItem.set(getItem(ind));
			setItem(0, tmpItem.id, 1, tmpItem.meta, tmpItem.durability);
			useUpItem(ind);
		}
	
		
	
	}
	public void swap(int c, int d){
		Item a = items[c];
		Item b = items[d];
		tmpItem.set(a);
		a.set(b);;
		b.set(tmpItem);
	}
	private boolean incrementTotal(int i) {
		return items[i].increaseAmount();
		
	}
	public ItemDef getItemInfo(int index) {
		return PunkBodies.getItemInfo(getItemID(index), getItemMeta(index));
	}
	
	
	
	
}

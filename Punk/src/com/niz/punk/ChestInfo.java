package com.niz.punk;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class ChestInfo implements Serializable{
	int chestID = 0, hash;
	int[] inv = new int[PunkInventory.INVENTORYSIZE*3];//0=itemID, 1=amount, 2=meta
	private static String TAG = "chestInfo";
	public boolean writeToFile(FileHandle file){
		try {
			
			ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(file.write(false)));
			//out.writeObject(gameInfo);
			out.write(chestID);
			for (int i = 0; i < PunkInventory.INVENTORYSIZE*3; i++){
				out.writeInt(inv[i]);
			}
			out.close();
			//gameInfo.clear();
		} catch(IOException ex){
			Gdx.app.log(TAG, "error saving chest");
			return false;
		}
		return true;
	}
	
	public int getItemID(int i){
		return inv[i*3];
	}
	
	public boolean readFromFile(FileHandle file){
	try {
			
			ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(file.read()));
			//out.writeObject(gameInfo);
			chestID = in.read();
			for (int i = 0; i < PunkInventory.INVENTORYSIZE*3; i++)
				inv[i]=in.read();
			in.close();
			//gameInfo.clear();
		} catch(IOException ex){
			Gdx.app.log(TAG, "error saving chest");
			return false;
		}
		return true;
	}
	public ChestInfo(){
	}
	public ChestInfo(int id, int gameType){
		chestID = id;
		
		for (int i = 0; i < PunkInventory.INVENTORYSIZE*3; i++){
			inv[i] = 0;
		}
		if (gameType == 6){//creative mode. add all blocks. disabled
			
			inv[1] = 99999;
			inv[2] = 99999;
			inv[8] = 99999;
			inv[15] = 99999;
			inv[16] = 99999;
			inv[17] = 99999;
			inv[19] = 99999;
			inv[20] = 99999;
			inv[21] = 99999;
			inv[23] = 99999;
			inv[29] = 99999;
			inv[37] = 99999;
			inv[37] = 99999;
			inv[38] = 99999;
			inv[50] = 99999;
			inv[54] = 99999;
			inv[300] = 99999;
			inv[305] = 99999;
			inv[307] = 99999;
			inv[400] = 99999;
			inv[321] = 99999;
			inv[322] = 99999;
			inv[321] = 99999;
			inv[292] = 99999;
			inv[291] = 99999;
			inv[301] = 99999;
			inv[302] = 99999;
			
		}
		resetTooltips();
	}
	public void clearInv(){
		for (int i = 0; i < PunkInventory.INVENTORYSIZE*3; i++){
			inv[i] = 0;
		}
	}
	public void clear(){
		Gdx.app.log("gameInfo", "clear gameinfo");
		for (int i = 0; i < PunkInventory.INVENTORYSIZE*3; i++){
			inv[i] = 0;
		}		
		
	
		
	}
	public void resetTooltips(){
		//for (boolean b:tooltipDone) b = false;
		/*tooltipDone[4] = true;
		tooltipDone[0] = true;
		tooltipDone[5] = true;
		tooltipDone[11] = true;
		tooltipDone[12] = true;
		tooltipDone[13] = true;
		tooltipDone[14] = true;
		tooltipDone[15] = true;*/

	}
	public void disableTooltips(){
		//for (boolean b:tooltipDone) b = true;
	}
	public String toString(){
		String s = new String();
		//s += 
		/*"gameSeed = "+gameSeed +"\n"
		+"gameID = "+gameID+"\n"
		+"spawnPosition = "+spawnPosition+"\n"
		
		+"seconds ="+ seconds+"\n"
		+"kills ="+ kills+"\n"
		+"pigsSaved ="+ pigsSaved+"\n"
		+"blocksMined ="+ blocksMined+"\n"
		+"blocksDestroyed ="+ blocksDestroyed+"\n"
		+"startOffset ="+ startOffset+"\n"
		+"isFirstSave = "+isFirstSave+"\n"*/;
		//inv = new IntArray(512);
		for (int i = 0; i < PunkInventory.INVENTORYSIZE*3; i++){
			if (inv[i] > 0)s += "\ninv["+i+"] = "+inv[i];
		}
		//for (int i = 0; i < 16; i++)
		//s += "\ntip"+i+":"+tooltipDone[i];
		;
		
		return s;
	}
	public void addToInv(int id, int am, int met){
		inv[id*3] = id;//WARNING DOES NOTHING!
	}
	public void overrideInv(int index, int met){
		inv[index] = met;
	}
	public void set(ChestInfo in){
		/*gameSeed = in.gameSeed;
		gameID = in.gameID;
		difficulty = in.difficulty;
		spawnPosition = new BlockLoc(in.spawnPosition.x,in.spawnPosition.y);
		
		seconds = in.seconds;
		
		
		gameType = in.gameType;*/
		//inv = new IntArray(512);
		for (int i = 0; i < PunkInventory.INVENTORYSIZE*3; i++){
			inv[i] = in.inv[i];
		}
		
		//isFirstSave = in.isFirstSave;
	
		
		//for (int i = 0; i < tooltipDone.length; i++)
		//	tooltipDone[i] = in.tooltipDone[i];
	}
	
}

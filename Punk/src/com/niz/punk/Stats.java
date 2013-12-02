package com.niz.punk;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class Stats {
long[] inv = new long[PunkInventory.INVENTORYSIZE*4];
int[] stats = new int[128];
public Stats(int gameType){
	clear();
}
public boolean writeToFile(FileHandle file){
	try {
		
		DataOutputStream out = new DataOutputStream(new BufferedOutputStream(file.write(false)));
		//out.writeObject(gameInfo);
		for (int i = 0; i < PunkInventory.INVENTORYSIZE*3; i++){
			out.writeLong(inv[i]);
			//if (inv[i] > 0) Gdx.app.log("stats", "wrote value!"+inv[i]);
		}
		for (int i = 0; i < 128; i++)
			out.writeInt(stats[i]);
		out.close();
		//gameInfo.clear();
	} catch(IOException ex){
		Gdx.app.log("player", "error saving chest");
		return false;
	}
	//Gdx.app.log("stats", "saved");
	////for (int i = 0; i < 63; i++)
	//	Gdx.app.log("stats", inv[i]>0?""+(inv[i]):"");
	
	return true;
}
public String toString(){
	String s = new String();
	for (int i = 0; i < PunkInventory.INVENTORYSIZE; i++){
		if (inv[i*4+1] > 0)s += "\ninv["+i*4+"] = "+inv[i*4+1];
	}
	//for (int i = 0; i < 16; i++)
	//s += "\ntip"+i+":"+tooltipDone[i];
	;
	
	return s;
}
public void set(PunkInventory playerInv){
	for (int i = 0; i < PunkInventory.INVENTORYSIZE; i++){
		inv[i*4] = playerInv.getItemID(i);
		inv[i*4+1] = playerInv.getItemAmount(i);
		inv[i*4+2] = playerInv.getItemMeta(i);
	}
}
public void setItem(int i, int id, int amount, int meta){
	inv[i*4] = id;
	inv[i*4+1] = amount;
	inv[i*4+2] = meta;
	inv[i*4+3] = PunkBodies.getItemInfo(id, meta).durability;
}
public void setForCreative(){
	for (int i = 0; i < PunkInventory.INVENTORYSIZE; i++){
		inv[i*4] = 0;
		inv[i*4+1] = 1;
	}
	inv[0*4] = 2;
	inv[1*4] = 1;
	inv[2*4] = 50;
	inv[3*4] = 23;
	inv[4*4] = 22;
	inv[5*4] = 0;
	inv[6*4] = 20;
	inv[7*4] = 21;
	inv[8*4] = 37;
	inv[9*4] = 19;
	inv[10*4] = 55;
	inv[11*4] = 54;
	inv[12*4] = 3;
	inv[13*4] = 440;
	inv[13*4+1] = 64;
	setItem(15,310,1, 2);
	setItem(16,310,1, 1);
		
}
public boolean readFromFile(FileHandle file){
try {
		
		DataInputStream in = new DataInputStream(new BufferedInputStream(file.read()));
		//out.writeObject(gameInfo);
	
		for (int i = 0; i < PunkInventory.INVENTORYSIZE*4; i++){
			inv[i] = in.readLong();
			//if (inv[i] > 0) Gdx.app.log("stats", "read value!"+inv[i]);
		}
		for (int i = 0; i < 128; i++)
			stats[i] = in.readInt();
		in.close();
		//gameInfo.clear();
	} catch(IOException ex){
		Gdx.app.log("player", "error loading chest");
		return false;
	}
//Gdx.app.log("stats", "reading");
//for (int i = 0; i < 63; i++)
//	Gdx.app.log("stats", inv[i]>0?""+(inv[i]):"");
	return true;
}
public void clearInv(){
	for (int i = 0; i < PunkInventory.INVENTORYSIZE*3; i++)
		inv[i] = 0;
}
public void clear(){
	for (int i = 0; i < PunkInventory.INVENTORYSIZE*3; i++)
		inv[i] = 0;
	
	for (int i = 0; i < 128; i++)stats[i] = 0;
}
public void setForCampaign() {
	/*setItem(0,430,32, 0);
	setItem(1,50,32, 0);
	setItem(2,414,1, 1280);
	setItem(3,313,1, 100000);
	setItem(4,450,64, 0);
	
	
	setItem(5,86,64, 0);
	setItem(6,310,1, 1);
	setItem(7, 271, 1, 128);
	setItem(8, 291, 1, 128);
	
	setItem(9, 258, 128, 128);
	setItem(10, 259, 381, 128);
	setItem(11, 25, 138, 128);
	setItem(12, 26, 138, 128);
	setItem(13, 27, 138, 128);
	setItem(14, 291, 1, 0);
	setItem(15, 377, 1, 90);
	setItem(16, 383, 48, 90);*/
	//setItem(0,256+19,1, 3);
	//setItem(2,290,1, 2220);
	//setItem(3,100+256,1, 4);
	//setItem(5,290,3, 0);
	//setItem(1,316,1, 0);
	//setItem(4,317,1, 0);
	//setItem(1, 256+102, 64, 0);
	//setItem(4, 256+103, 100, 4);
	
	setItem(1, 50, 1000, 0);
	setItem(0, 1, 1000, 1);
	setItem(2, 60+256, 1000, 0);
	setItem(3, 60+256, 1000, 1);
	
	setItem(22, 313, 1, 2);
	setItem(23, 313, 1, 3);
	setItem(10, 275, 1, 4);
	setItem(11, 8, 10, 1);
	setItem(12, 50, 1000, 0);
	setItem(13, 313, 1, 4);
	
	setItem(4, 60+256, 1000, 2);
	
	setItem(5, 103+256, 800, 0);
	
	
	setItem(15, 0+256, 1, 1110);
	setItem(16, 0+256, 1, 1);
	setItem(17, 103+256, 122, 2);
	setItem(18, 103+256, 122, 3);
	setItem(19, 103+256, 133, 4);
	setItem(20, 103+256, 444, 5);//*/
	setItem(1, 19+256, 1, 0);
	
	/*for (int i = 0; i < 20; i++){
		setItem(i, i+413, 1, 128);
	}*/
	/*inv[1*3] = 50;
	inv[1*3+1] = 32;
	
	inv[2*3] = 414;
	inv[2*3+1] = 1;
	inv[2*3+2] = 128;
	
	inv[3*3] = 440;
	inv[3*3+1] = 8;
	
	inv[4*3] = 450;
	inv[4*3+1] = 4;*/
	
	
}

}

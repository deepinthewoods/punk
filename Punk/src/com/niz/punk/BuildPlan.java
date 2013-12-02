package com.niz.punk;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Rectangle;

public class BuildPlan{
	private static final int FLAG_COUNT = 10;
	public int x,y,width, height;
	public final String SUFFIX = ".bui";
	public String name;
	private int progress = 0;
	public boolean[] flags = new boolean[FLAG_COUNT];
	public static String[] flagNames = {""};

public void loadInfo(String name){
	this.name = name;
	Punk.getSaveLoc(Punk.path);
	Punk.path.append(name);
	Punk.path.append(SUFFIX);
	FileHandle file = Gdx.files.external(Punk.path.toString());
	DataInputStream is = new DataInputStream(new BufferedInputStream(file.read()));
	try {
		//minWidth = is.readInt();
		width = is.readInt();
		height = is.readInt();
		for (int i = 0; i < FLAG_COUNT; i++){
			flags[i] = is.readBoolean();
		}
		//maxHeight = is.readInt();
		is.close();
	} catch (IOException e) {
		
		e.printStackTrace();
	}
}
FileHandle file;
DataInputStream is;
PunkMap map;
public void printToMap(int x, int y){
	//this.name = name;
	Punk.getSaveLoc(Punk.path);
	Punk.path.append(name);
	Punk.path.append(SUFFIX);
	file = Gdx.files.external(Punk.path.toString());
	is = new DataInputStream(new BufferedInputStream(file.read()));
	try {
		width = is.readInt();

		height = is.readInt();
		for (int i = 0; i < FLAG_COUNT; i++){
			flags[i] = is.readBoolean();
		}
		
		for (int i = 0; i < width; i++)
		for (int j = 0; j < height; j++){
			
			//changeblock
			map.changeBlock(x+i, y+j, is.readByte(), is.readByte(), true);
		}
		
		is.close();
	} catch (IOException e) {
		
		e.printStackTrace();
	}
	
}




public void save(PunkMap map, int x1, int y1, int x2, int y2){
	
	Punk.getSaveLoc(Punk.path);
	Punk.path.append(name);
	Punk.path.append(SUFFIX);
	FileHandle file = Gdx.files.external(Punk.path.toString());
	DataOutputStream os = new DataOutputStream(new BufferedOutputStream(file.write(false)));
	try {
		os.writeInt(x2-x1);
		os.writeInt(y2-y1);
		for (int i = 0; i < FLAG_COUNT; i++){
			os.writeBoolean(flags[i]);
		}
		
		for (int x = x1;x <= x2; x++)
			for (int y = y1; y <= y2; y++){
				Block b = map.getBlock(x,y);
				
				os.writeByte(b.blockID);
				os.writeByte(b.meta);
				//os.writeInt(b.getLightBits());
			}
		
		os.close();
	} catch (IOException e) {
		
		e.printStackTrace();
	}
}

/*
public boolean overlaps (BuildPlan rectangle) {
	return !(x > rectangle.x + rectangle.width || x + width < rectangle.x || y > rectangle.y + rectangle.maxHeight || y + maxHeight < rectangle.y);
}*/





}

package com.niz.punk;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class GameInfo {
	public int gameID = 6;
	public int gameSeed = 0, difficulty = 2;
	//public int playerClass = 1;
	//public int health = Player.MAXHEALTH;
	long seconds;
	int  minutes;
	public int gameType;
	BlockLoc spawnPosition = new BlockLoc(64, 16);
	BlockLoc savedPosition = new BlockLoc(64,12);
	
	//public boolean[][] skills = new boolean[4][16];
	//public int xp = 0;
	//public int skillPoints = 0;
	//public int level = 1;
	public int savedPlane = 0;
	//public int playerRace;
	//public int playerGender;
	//public long subdivisions;
	public GameInfo(){
	
	}
	public GameInfo(int id, int seed, int gameType){
		gameSeed = seed;
		gameID = id;
		//spawnPosition = new BlockLoc(20000,150);
		
		seconds = 60;
		minutes = 1;
		this.gameType = gameType;
	}
	public void writeToFile(FileHandle file){
		try{
			DataOutputStream out = new DataOutputStream(new BufferedOutputStream(file.write(false)));
			out.writeInt(gameID);
			out.writeInt(gameSeed);
			out.writeInt(difficulty);
//			out.writeInt(playerClass);
//			out.writeInt(playerRace);
//			out.writeInt(playerGender);
//			out.writeInt(health);
			
			out.writeLong(seconds);
			out.writeInt(minutes);
			out.writeInt(gameType);
			out.writeLong(5);//extras
			//out.writeBoolean(isFirstSave);
			
			out.writeInt(spawnPosition.x);
			out.writeInt(spawnPosition.y);
			out.writeInt(savedPosition.x);
			out.writeInt(savedPosition.y);
			out.writeInt(savedPlane);
//			for (int c = 0; c < 4; c++)
//				for (int d = 0; d < 16; d++)
//					out.writeBoolean(skills[c][d]);
//			out.writeInt(xp);
//			out.writeInt(skillPoints);
//			out.writeInt(level);
			out.close();
			//Gdx.app.log("gameinfo", "done writing  s "+spawnPosition.y);
		} catch (IOException ex){
			Gdx.app.log("gameinfo", "error writing");
		}
	}
	public void readFromFile(FileHandle file){
		if (file.exists())
			try{
				
				DataInputStream in = new DataInputStream(new BufferedInputStream(file.read()));
				gameID=in.readInt();
				gameSeed=in.readInt();
				difficulty=in.readInt();
//				playerClass = in.readInt();
//				playerRace = in.readInt();
//				playerGender = in.readInt();
//				health=in.readInt();
				//Gdx.app.log("gameinfo", "read from file "+file.path()+" s "+spawnPosition.y+" health "+health);
				seconds=in.readLong();
				minutes = in.readInt();
				gameType = in.readInt();
				long blank =in.readLong();
				//extras
				//isFirstSave=in.readBoolean();
				
				spawnPosition.x=in.readInt();
				spawnPosition.y=in.readInt();
				savedPosition.x=in.readInt();
				savedPosition.y=in.readInt();
				savedPlane = in.readInt();
				
//				for (int c = 0; c < 4; c++)
//					for (int d = 0; d < 16; d++)
//						skills[c][d] = in.readBoolean();
//				xp = in.readInt();
//				skillPoints = in.readInt();
//				level = in.readInt();
				in.close();
			} catch (IOException ex){
				Gdx.app.log("gameinfo", "error reading");
	
			}
		else {Gdx.app.log("gameinfo", "doesn't exist!!!!!!!!!!!!!!!!!!!!!!!");}//new GameInfo().writeToFile(file);}
			
		
	}
	public void set(GameInfo in){
		gameSeed = in.gameSeed;
		gameID = in.gameID;
		difficulty = in.difficulty;
		//playerClass = in.playerClass;
		spawnPosition = new BlockLoc(in.spawnPosition.x,in.spawnPosition.y);
		savedPosition.set(in.savedPosition);
		savedPlane = in.savedPlane;
		seconds = in.seconds;
		minutes = in.minutes;
		//health = in.health;
//		gameType = in.gameType;
//		for (int c = 0; c < 4; c++)
//			for (int d = 0; d < 16; d++)
//				skills[c][d] = in.skills[c][d];
//		xp = in.xp;
//		skillPoints = in.skillPoints;
		//level = in.level;
		//inv = new IntArray(512);
//		playerClass = in.playerClass;
//		playerGender = in.playerGender;
//		playerRace = in.playerRace;
		
		
		//isFirstSave = in.isFirstSave;
	
		
		//for (int i = 0; i < tooltipDone.length; i++)
		//	tooltipDone[i] = in.tooltipDone[i];
	}
}

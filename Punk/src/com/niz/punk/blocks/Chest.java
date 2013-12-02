package com.niz.punk.blocks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.physics.box2d.World;
import com.niz.punk.Block;
import com.niz.punk.BlockDef;
import com.niz.punk.BlockLoc;
import com.niz.punk.Chunk;
import com.niz.punk.PhysicsActor;
import com.niz.punk.Player;
import com.niz.punk.Punk;
import com.niz.punk.PunkInventory;
import com.niz.punk.PunkMap;
import com.niz.punk.PunkMap.BlockDamageType;

public class Chest extends BlockDef {

	@Override
	public void destroy(BlockDamageType dType, int mapX, int mapY, int p,
			float angle, PunkMap map, int id, int meta) {
		map.createItem(8, 1, 0, 0, mapX, mapY);
		//TODO spill items
	}

	@Override
	public void update(BlockLoc currentBlock, Block block, PunkMap map,
			boolean propagate) {
		
		
	}

	@Override
	public void timedUpdate(BlockLoc currentBlock, Block block, PunkMap map) {
		
		
	}
	public static final String PATH_CHEST = "chest", PATH_SUFFIX = ".ino";
	@Override
	public void unscrub(PunkMap map, int x, int y, Block b, Chunk c, boolean placing) {
		int chestID = c.block[((x)<<Punk.CHUNKBITS)+(y)].meta;
		//Punk.getSaveLoc(Punk.path);
		map.chunkPool.getChunkLoc(Punk.path, c.chunkID, c.heightID, PunkMap.currentPlane);
		//String chestLoc = c.saveDir+map.current_gameType+"/" +"game"+ map.gameID + "/chunk"+ c.chunkID+"h"+c.heightID + "chest"+chestID+".ino";
		Punk.path.append(PATH_CHEST);
		Punk.path.append(chestID);
		Punk.path.append(PATH_SUFFIX);
		FileHandle chestHandle = Gdx.files.external(Punk.path.toString());
		
		if (chestHandle.exists()){
			//Gdx.app.log(TAG, "loading from disk:"+chunkID+"file length:"+saveHandle.length()+"path:" + saveHandle.path());
			if (c.chests.get(chestID) == null) c.chests.put(chestID, new PunkInventory(chestID));
			//TODO c.chests.get(chestID).readFromFile(chestHandle);
			//Gdx.app.log("map", "read chest");
		} else {
			c.chests.put(chestID, new PunkInventory(chestID));
			Gdx.app.log("map", "error, chest doesn't exist, making new");
		}
		
	}

	@Override
	public void place(PunkMap map, int x, int y, int beforeID, int beforeMeta,
			boolean propagate, Block b, boolean left) {
		b.setMeta(map.putChest(x, y));
		
	}

	@Override
	public void mobFeet(PunkMap map, PhysicsActor act, Block b, World world) {
		if (act instanceof Player){
			Player player = (Player) act;
			player.isOnChestBlock = true;
		}
		
	}

	@Override
	public void mobHead(PunkMap map, PhysicsActor act, Block b) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mobFeetExit(PunkMap map, PhysicsActor act, Block b) {
		if (act instanceof Player){
			Player player = (Player) act;
			player.isOnChestBlock = false;
		}
		
	}

	

}

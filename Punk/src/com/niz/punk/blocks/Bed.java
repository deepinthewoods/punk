package com.niz.punk.blocks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.World;
import com.niz.punk.Block;
import com.niz.punk.BlockDef;
import com.niz.punk.BlockLoc;
import com.niz.punk.Chunk;
import com.niz.punk.PhysicsActor;
import com.niz.punk.Player;
import com.niz.punk.Punk;
import com.niz.punk.PunkMap;

public class Bed extends BlockDef{
	private static final String TAG = "bed blockdef";
	public Bed(){
		super(9, 1, 1, 100);
	}
	private BlockLoc tmpB = new BlockLoc(0,0);
	//private BlockLoc lookBlock = new BlockLoc();
	
	@Override
	public void update(BlockLoc loc, Block b, PunkMap map, boolean propagate) {
		int currentMeta = b.meta;
		Gdx.app.log(TAG, "bed update");
		boolean left = currentMeta%2 == 0;
		tmpB.set(loc.x+(left?1:-1), loc.y);
		Block tmpBlock = map.getBlock(tmpB);
		if (tmpBlock.blockID != 41 || map.getBlock(loc.x, loc.y-1).blockType() == 0) {
			b.set(0,0);
			Gdx.app.log(TAG, "bed invalid!!!!!!!!!!!!!!!");
			if (left) map.createItem(41, 1, 0, 0, loc.x, loc.y);
			if (propagate){
				map.addUpdate(loc.x, loc.y+1);
				map.addUpdate(loc.x, loc.y-1);
				map.addUpdate(loc.x+1, loc.y);
				map.addUpdate(loc.x-1, loc.y);
			}
		}
	}

	@Override
	public void timedUpdate(BlockLoc currentBlock, Block block, PunkMap map) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unscrub(PunkMap map, int x, int y, Block b, Chunk c,
			boolean placing) {
		// TODO Auto-generated method stub
		//Gdx.app.log(TAG, "bed update");
	}

	@Override
	public void place(PunkMap map, int x, int y, int beforeID, int beforeMeta,
			boolean propagate, Block b, boolean left) {
		Gdx.app.log(TAG, "bed place");
		if (left){
			b.meta = 2;
			Block b2 = map.getBlock(x+1, y);
			if (b2.blockType() == 0)
				b2.set(41, 3);
		}else {
			b.meta = 1;
			Block b2 = map.getBlock(x-1, y);
			if (b2.blockType() == 0)
				b2.set(41, 0);
		}
		
	}

	@Override
	public void mobFeet(PunkMap map, PhysicsActor act, Block b, World world) {
		// TODO Auto-generated method stub
		if (act instanceof Player){
			Player p = (Player) act;
			p.activeDoor = Punk.bedDoor;
			Punk.openBedButtons();
		}
		
	}

	@Override
	public void mobHead(PunkMap map, PhysicsActor act, Block b) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mobFeetExit(PunkMap map, PhysicsActor act, Block b) {
		if (act instanceof Player){
			Player p = (Player) act;
			if (p.activeDoor == Punk.bedDoor)p.activeDoor = null;
		}
		
	}

	
}

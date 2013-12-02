package com.niz.punk.blocks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.World;
import com.niz.punk.Block;
import com.niz.punk.BlockDef;
import com.niz.punk.BlockLoc;
import com.niz.punk.Chunk;
import com.niz.punk.PhysicsActor;
import com.niz.punk.Punk;
import com.niz.punk.PunkMap;

public class OreSource extends BlockDef {

	@Override
	public void update(BlockLoc loc, Block block, PunkMap map, boolean propagate) {
		// TODO Auto-generated method stub

	}

	@Override
	public void timedUpdate(BlockLoc currentBlock, Block block, PunkMap map) {
		// TODO Auto-generated method stub

	}
	public int[] crystals = {34, 35, 36};
	public int[] ores = {29, 30, 31, 32, 28};
	@Override
	public void unscrub(PunkMap map, int bx, int by, Block b, Chunk c,
			boolean placing) {
		int mx = bx + c.xOffset, my = by += c.yOffset;
		
		//BlockLoc loc = chunk.oreSourceList.removeFirst();
		// make a deposit around source block.
		int oreTypeID = b.meta;
		if (oreTypeID < 3) {
			int crystID = crystals[oreTypeID];
			//map.changeBlock(mx, my, 1, 0, true);
			map.changeBlock(mx, my, crystID, 0, true);
			for (int x = -3; x <= 3; x++)
				for (int y = -3; y <= 3; y++) {
					//Block tmpB = map.getBlock(mx + x, my + y);
					int tx = mx+x, ty = my+y;
					Chunk cc = map.chunkPool.getChunkWorld(tx,ty);
					
					if (cc == null){
						return;
					}
					
					Block tmpB = cc.block[((tx&Punk.CHUNKSIZEMASK)<<Punk.CHUNKBITS)+(ty&Punk.CHUNKSIZEMASK)];
					if (tmpB.blockID == 0
							&& map.getBlock(mx + x, my + y + 1).blockID == 1) {
						map.changeBlock(mx + x, my + y, crystID, 0, true);
						//addUpdate(loc.x + x, loc.y + y);
						//updateTrollBlock(loc.x + x, loc.y + y);
						//Gdx.app.log("map", "made crystal"+crystID);
					}

				}
		} else {
			int oreID = ores[oreTypeID - 3];
			//map.changeBlock(mx, my, oreID, 0, true);
			map.changeBlock(mx, my, oreID, 0,
					true);
			for (int x = -2; x <= 2; x++)
				for (int y = -2; y <= 2; y++) {
					Block tmpB = map.getBlock(mx + x, my + y);
					if (tmpB.blockType() >= 64) {
						if (MathUtils.random(2) == 0) {
							map.changeBlock(mx + x, my + y, oreID, 0,
									true);
							//Gdx.app.log("map", "made ore!!!!!!!!!!!!!!"+oreID);
							//addUpdate(loc.x + x, loc.y + y);
							//updateTrollBlock(loc.x + x, loc.y + y);
						}

					}

				}
		}
	}

	@Override
	public void place(PunkMap map, int x, int y, int beforeID, int beforeMeta,
			boolean propagate, Block b, boolean left) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mobFeet(PunkMap map, PhysicsActor act, Block b, World world) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mobHead(PunkMap map, PhysicsActor act, Block b) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mobFeetExit(PunkMap map, PhysicsActor act, Block b) {
		// TODO Auto-generated method stub

	}

	

}

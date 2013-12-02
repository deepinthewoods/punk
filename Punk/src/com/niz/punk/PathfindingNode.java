package com.niz.punk;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class PathfindingNode {
	private static Pool<PathfindingNode> pool = new Pool<PathfindingNode>(){
		@Override
		protected PathfindingNode newObject() {
			return new PathfindingNode();
		}
	};
	
	int x,y 
	,headroom//empty spaces above
	,type //type is a bitmask of nearby air/nodes
	,hash
	;
	boolean finished = false;
	static //just the jumps is different
	int MOVETYPESTOTAL = 6
	,MOVE_RUN = 0
	,MOVE_DROP = 1
	,MOVE_PALADIN = 2
	,MOVE_ROGUE = 3
	,MOVE_BARBARIAN = 4
	,MOVE_WIZARD = 5
	
	
	;
	Array<PathfindingNode>[] neighbors = new Array[MOVETYPESTOTAL];
	
	public static PathfindingNode obtain(int x, int y, int nodeType) {
		PathfindingNode n = pool.obtain();
		for (int i = 0; i < MOVETYPESTOTAL; i++)
			n.neighbors[i].clear();
		n.x = x;
		n.y = y;
		n.headroom = 0;
		n.type = nodeType;
		//n.progress = 1+2+4+8+16;
		n.hash = n.getHash();
		n.finished = false;
		return n;
	}

	

	public PathfindingNode(){
		for (int i = 0; i < MOVETYPESTOTAL; i++)
			neighbors[i] = new Array<PathfindingNode>();
	}
	
	public void save(DataOutputStream os) throws IOException{
		for (int i = 0; i < MOVETYPESTOTAL; i++){
			os.writeInt(neighbors[i].size);
			for (int j = 0; j < neighbors[i].size;j++){
				os.writeInt(neighbors[i].get(j).x);
				os.writeInt(neighbors[i].get(j).y);
			}
		}
	}
	
	public void load(DataInputStream is, ChunkPool chunkPool) throws IOException{
		for (int i = 0; i < MOVETYPESTOTAL; i++){
			int size = is.readInt();
			for (int j = 0; j < size;j++){
				fromDisk(is.readInt(), is.readInt(), chunkPool);
			}
		}
	}

	private void fromDisk(int x, int y, ChunkPool chunkPool) {//TODO this is not doing anything useful
		
		
		PathfindingNode n = chunkPool.getPathfindingNodeFromHash(x,y);
		if (n != null)
			neighbors[x].add(n);
		else {
			
		}

	}

	int getHash() {
		return MurmurHash3.get(x, type, 0);
	}
	int hash(){
		return hash;
	}
}


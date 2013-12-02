package com.niz.punk;

import java.util.Comparator;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pool.Poolable;


public class PunkBlockList {
	public static String tag = "blockList";
	private int countBeforeSort = 5000, sortMin = 5000;
	public static class BlockPool extends Pool<BlockLoc>{
		
		protected BlockLoc newObject()
		{
			return new BlockLoc();
		}
	};
	private int counter = 0;
	public static BlockPool pool = new BlockPool();
	//public List<BlockLoc> list;
	public ConcurrentLinkedQueue<BlockLoc> list ;//= new Array<BlockLoc>(true, 64);
	//public LongMap<BlockLoc> map;
	private BlockLoc tmpLoc = new BlockLoc();
	//private ListIterator iter;
	private int tmpi;
	
	public class BlockComparator implements Comparator<BlockLoc>{

		@Override
		public int compare(BlockLoc a0, BlockLoc a1) {
			return (int) (getHash(a0) - getHash(a1));
			//return 0;
		}
		
	}
	
	
	public PunkBlockList(){
		//map = new LongMap<BlockLoc>(64);
		list =  new ConcurrentLinkedQueue<BlockLoc>();
		//list.
		counter = 0;
	}
	public PunkBlockList(int capacity) {
		//map = new LongMap<BlockLoc>(capacity);
		list =  new ConcurrentLinkedQueue<BlockLoc>();
		counter = 0;
		
	}
	
	
	public void free(BlockLoc l){
		if (l != null)pool.free(l);
	}
	
	public int prog;
	/*public BlockLoc getNext(){
		prog--;
		if (prog <0)
			prog =  list.size-1;
		else if (prog >= list.size) prog = list.size-1;
		return list.get(prog);
	}*/
	
	public void addBlock(int x, int y){
		//if (map.containsKey(getHash(x,y))) {
		//	//Gdx.app.log(tag, "not adding "+list.get(getHash(x,y))+ "  "+x+","+y);
		//	return;
		//}
		BlockLoc j = pool.obtain();
		while (j == null){
			Gdx.app.log("bl", "null");
			j = pool.obtain();
		}
		if (j == null) throw new IllegalArgumentException("object cannot be null.");

		j.set(x, y);
		//map.put(getHash(x,y), l);
		list.add(j);
		counter++;
		max = Math.max(list.size(), max);
		
	}
	public int max = 0;
	private long getHash(int x, int y) {
		
		return x+(y<<15);
	}
	private long getHash(BlockLoc l){
		return getHash(l.x, l.y);
	}
	public void clear(){
	
		Iterator<BlockLoc> i = list.iterator();
		while (i.hasNext()){
			BlockLoc bl = i.next();
			if (bl != null)pool.free(bl);
			
		}
		list.clear();
		//map.clear();
	}
	public void addBlock(BlockLoc loc) {
		addBlock(loc.x, loc.y);
		
	}
	public BlockLoc removeFirst() {
	
		return list.poll();
	}
	private BlockComparator blockComp = new BlockComparator();
	/*public void removeDupes() {
		if (list.size < sortMin || counter < countBeforeSort)
			return;
		list.sort(blockComp);
		//traverse list backwards, comparing adjacent values
		int i = list.size-1;
		while (i > 0){
			if (list.get(i).equals(list.get(i-1))) {
				pool.free(list.removeIndex(i));
			}
			i--;
		}
		counter = 0;
		Gdx.app.log(tag, "sort");
	}*/
	public BlockLoc removeOrdered() {
		return list.poll();
	}
	public static BlockLoc obtain(int x, int y) {
		BlockLoc l = pool.obtain();
		while (l == null) l = pool.obtain();
		l.set(x,y);
		return l;
	}
	
	
	
}

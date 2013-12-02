package com.niz.punk;

import java.util.Comparator;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.niz.punk.PunkMap.BlockDamageType;

public class BlockListDamage {
	public static String tag = "blockList";
	private int countBeforeSort = 5000, sortMin = 5000;
	public static class BlockPool extends Pool<BlockLocDamage>{
		protected BlockLocDamage newObject()
		{
			return new BlockLocDamage();
		}
	};
	private int counter = 0;
	public static class DBlockPool extends Pool<BlockLocDamage>{

		@Override
		protected BlockLocDamage newObject() {
			return new BlockLocDamage();
		}
		
	}
	public static DBlockPool pool = new DBlockPool();
	//public List<BlockLoc> list;
	public Array<BlockLocDamage> list ;//= new Array<BlockLoc>(true, 64);
	//public LongMap<BlockLoc> map;
	private BlockLoc tmpLoc = new BlockLoc();
	//private ListIterator iter;
	private int tmpi;
	
	public class BlockComparator implements Comparator<BlockLocDamage>{

		@Override
		public int compare(BlockLocDamage a0, BlockLocDamage a1) {
			return (int) (getHash(a0) - getHash(a1));
			//return 0;
		}
		
	}
	
	public BlockListDamage(){
		//map = new LongMap<BlockLoc>(64);
		list =  new Array<BlockLocDamage>(true, 64);
		//list.
		counter = 0;
	}
	public BlockListDamage(int capacity) {
		//map = new LongMap<BlockLoc>(capacity);
		list =  new Array<BlockLocDamage>(true, capacity);
		counter = 0;
		
	}
	
	public void removeLast(){
		//BlockLoc inList = list.removeIndex(progress-1);
		//tmpLoc.set(inList);
		//progress--;
		//free(inList);
		BlockLocDamage lastVal = list.removeIndex(prog);
		//map.remove(getHash(lastVal));
		pool.free(lastVal);
		//free(lastVal);
		//return tmpLoc;
	}
	
	/*public void free(BlockLoc loc) {
		pool.free(loc);
		list.removeValue(loc, true);
		//map.remove(getHash(loc));
		//list.removeValue())
	}*/
	public void free(BlockLocDamage l){
		pool.free(l);
	}
	
	public int prog;
	public BlockLocDamage getNext(){
		prog--;
		if (prog <0)
			prog =  list.size-1;
		else if (prog >= list.size) prog = list.size-1;
		return list.get(prog);
	}
	
	public void addBlock(int x, int y, int damage, BlockDamageType  type){
		//if (map.containsKey(getHash(x,y))) {
		//	//Gdx.app.log(tag, "not adding "+list.get(getHash(x,y))+ "  "+x+","+y);
		//	return;
		//}
		BlockLocDamage l = pool.obtain();
		l.set(x, y, damage, type);
		//map.put(getHash(x,y), l);
		list.add(l);
		counter++;
		max = Math.max(list.size, max);
		
	}
	public int max = 0;
	private long getHash(int x, int y) {
		
		return x+(y<<15);
	}
	private long getHash(BlockLocDamage l){
		return getHash(l.x, l.y);
	}
	public void clear(){
	
		Iterator<BlockLocDamage> i = list.iterator();
		while (i.hasNext()){
			pool.free(i.next());
			
		}
		list.clear();
		//map.clear();
	}
	
	public BlockLocDamage removeFirst() {
	
		return list.pop();
	}
	private BlockComparator blockComp = new BlockComparator();
	public void removeDupes() {
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
	}
	public BlockLocDamage removeOrdered() {
		return list.removeIndex(0);
	}
	
	
	
}

package com.niz.punk;

import com.badlogic.gdx.utils.Pool;

public class PathPoint extends IntMappedHeap.Node{
	
	
	public PathPoint() {
		super(0);
		// TODO Auto-generated constructor stub
	}
	public static class PathPointPool extends Pool<PathPoint>{
		
		protected PathPoint newObject()
		{
			return new PathPoint();
		}
	};
	public static PathPointPool pool = new PathPointPool();

	public PathfindingNode node;
	public PathfindingNode parent;
	
	public static PathPoint obtain(float cost, PathfindingNode node, PathfindingNode parent){
		PathPoint p = pool.obtain();
		p.parent = parent;
		p.node = node;
		p.key = node.hash();
		p.value = cost;
		return p;
	}
	
}

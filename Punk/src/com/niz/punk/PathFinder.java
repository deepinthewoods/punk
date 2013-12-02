package com.niz.punk;

import com.badlogic.gdx.utils.Array;


public class PathFinder {
public IntMappedHeap<PathPoint> open = new IntMappedHeap<PathPoint>(), closed = new IntMappedHeap<PathPoint>();
public Array<PathfindingNode> neighbors = new Array<PathfindingNode>();
public void findPath(PathfindingNode goal, GenericMob source){
	
	//OPEN = priority queue containing START
	open.clear();
	//		CLOSED = empty set
	closed.clear();
	//		while lowest rank in OPEN is not the GOAL:
	PathPoint low = open.peek();
	int goalHash = goal.hash();
	while (low.key != goalHash){
	//		  current = remove lowest rank item from OPEN
		PathPoint current = open.pop();
	//		  add current to CLOSED
		closed.add(current);
	//		  for neighbors of current:
		//populate neighbors
		
	//		    cost = g(current) + movementcost(current, neighbor)
	//		    if neighbor in OPEN and cost less than g(neighbor):
	//		      remove neighbor from OPEN, because new path is better
	//		    if neighbor in CLOSED and cost less than g(neighbor): **
	//		      remove neighbor from CLOSED
	//		    if neighbor not in OPEN and neighbor not in CLOSED:
	//		      set g(neighbor) to cost
	//		      add neighbor to OPEN
	//		      set priority queue rank to g(neighbor) + h(neighbor)
	//		      set neighbor's parent to current
	}
	//		reconstruct reverse path from goal to start
	//		by following parent pointers
	
	
}
}

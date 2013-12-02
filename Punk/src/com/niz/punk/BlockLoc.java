package com.niz.punk;



import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;




	public class BlockLoc implements Signal{
		public int x;
		public int y;
		//public int y;
		public BlockLoc(){
			x=0;y=0;
		}
		
		public BlockLoc(int bx, int by){
			x = bx;
			y = by;
		}
		public BlockLoc(BlockLoc bl){
			x = bl.x;
			y = bl.y;
		}
		public BlockLoc set(int newx, int newy)
		{
			x = newx;
			y = newy;
			return this;
		}	
		public void set(BlockLoc bl){
			x = bl.x;
			y = bl.y;
		}
		public void set(Vector2 v){
			x = MathUtils.floor(v.x);
			y = MathUtils.floor(v.y);
		}
		public String toString(){
			return ("x: "+x+" y: "+y+"  ");
		}
		public boolean equals(BlockLoc loc){
			return (x == loc.x && y == loc.y);
		}
		public void add(int x, int y) {
			this.x += x;
			this.y += y;
			
		}
		public void add(BlockLoc loc) {
			x += loc.x;
			y += loc.y;
			
		}
		public int manhattanLen() {
			
			return Math.abs(x)+Math.abs(y);
		}

		public int manhattanDst(int x2, int y2) {
			int dx = Math.abs(x - x2), dy = Math.abs(y - y2);
			return Math.max(dx, dy);
		}
	}


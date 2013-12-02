package com.niz.punk;



import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.niz.punk.PunkMap.BlockDamageType;




	public class BlockLocDamage{
		public int x;
		public int y;
		public BlockDamageType type = BlockDamageType.HAND;
		public int damage = 0;
		public int p;
		//public int y;
		public BlockLocDamage(){
			x=0;y=0;
		}
		public BlockLocDamage(int bx, int by){
			x = bx;
			y = by;
		}
		public BlockLocDamage(BlockLocDamage bl){
			x = bl.x;
			y = bl.y;
		}
		public BlockLocDamage set(int newx, int newy, int damage, BlockDamageType type)
		{
			this.type = type;
			this.damage = damage;
			x = newx;
			y = newy;
			p = PunkMap.currentPlane;
			return this;
		}	
		public void set(BlockLocDamage bl){
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
		public boolean equals(BlockLocDamage loc){
			return (x == loc.x && y == loc.y);
		}
	}


package com.niz.punk;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.World;

public class CTrailArrow implements GrenadeComponent {
	int id;
	public CTrailArrow(int id){
		this.id = id;
	}
	public void set(int id){
		this.id = id;
	}
	@Override
	public void act(Grenade mob, PunkMap map, Player player,
			PunkBodies monsterIndex, World world) {
		int dx = mob.x - mob.lastx;
		int dy = mob.y - mob.lasty;
		int dx2 = mob.target.x+dx, dy2 = mob.target.y+dy;
		int newMeta;
		if (dy2 > 0 ){
			if ( dx2 == 0)
				newMeta = 0;
			else if (dx >0)
				newMeta = 1;
			else newMeta = 7;
		} else if (dy2 < 0){
			if (dx2 == 0)
				newMeta = 4;
			else if (dx2 > 0)
				newMeta = 3;
			else newMeta = 5;
		} else {//dy2 == 0
			if (dx2 > 0)
				newMeta = 2;
			else newMeta = 6;
		}
		int oM0 = (mob.actorMeta) & 7 ;
		int oM1 = (mob.actorMeta >> 3) & 7 ;
		int oM2 = (mob.actorMeta >> 6) & 7 ;
		int oM3 = (mob.actorMeta >> 9) & 7 ;
		
		mob.actorMeta &= 65528;
		mob.actorMeta += newMeta;
		mob.actorMeta = mob.actorMeta << 3;
		if (newMeta == oM1 || mob.state < 1)
			mob.actorMeta += newMeta;
		else mob.actorMeta += oM0;
		mob.target.set(dx,dy);
		
		//map.changeBlock(tx, ty, id, MathUtils.random(8,16));
	}

}

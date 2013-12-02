package com.niz.punk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.niz.punk.GenericMob;
import com.niz.punk.Item;
import com.niz.punk.Player;
import com.niz.punk.Punk;
import com.niz.punk.Punk.RayType;
import com.niz.punk.PunkBodies;
import com.niz.punk.PunkMap;
import com.niz.punk.TouchAction;

public class PlaceBlock extends TouchAction{
	private static final String TAG = "place";
	private Vector2 tmpV = new Vector2();
	public PlaceBlock(PunkMap map, PunkBodies mi, World world) {
		super(map, mi, world);
		selectable = true;
	}
	

	@Override
	public void touchUp(GenericMob mob) {
		if (mob.state == 18 && mob.targetB.y != -1000000){
			mob.clipAnimTimer();
			mob.state = 39;
			mob.lastState = 39;
		} else if (mob.state == 48){
			moved = !b.equals(mob.targetV);
			if (!moved){
				if (!valid) return;
				mob.clipAnimTimer();
				mob.state = 50;
				mob.lastState = 50;
				return;
			}
			mob.clipAnimTimer();
			mob.state = 49;
			mob.lastState = 49;
		}
	}


	@Override
	public boolean touchDown(GenericMob mob) {
		if (mob.state == 0 || mob.state == 4 || mob.state == 5 || mob.state == 9 || mob.state == 10){
			if (Punk.prefs_place_mode.value == 0){
				mob.state = 18;
				mob.stateTime = 0f;
				mob.angle = mob.touchLoc.angle()+180;
			} else if (Punk.prefs_place_mode.value == 1){
				mob.state = 48;
				lastB.set(0, -1000000);
				mob.targetV.set(mob.position).add(0,1f);;
				v.set(Punk.targetBlockV);
				//time = Punk.screenTime;
				mob.targetB.set(mob.targetV);
				moved = true;
				b.set(mob.targetV);
			}
			
			mob.faceAngle(!false);
			return true;
		}
		if (mob.state == 18){
			Punk.queueBlockHighlight(mob.targetB);
			Punk.queueLine(mob.position.tmp().add(0,mob.originOffset.y), mob.targetV);
			//Punk.drawAngleHighlight(true);
			mob.faceAngle(!false);
			mob.angle = mob.touchLoc.angle()+180;
			mob.targetV.set(map.rayCastForPlace(tmpV.set(mob.position).add(0,mob.originOffset.y), (mob.angle+180)%360, RayType.SOLID, mob.info.rangePlace, 1));
			mob.targetB.set(mob.targetV);

		} else if (mob.state == 48){
			
			float scalar = 1;
			tmpV.set(v);
			
			tmpV.sub(Punk.targetBlockV).mul(-scalar);
			mob.targetV.add(tmpV);
			mob.targetB.set(mob.targetV);
			if (mob.targetB.manhattanDst(mob.x, mob.y) > mob.info.rangePlace){
				mob.targetV.sub(tmpV);
				mob.targetB.set(mob.targetV);
				
			}
			v.set(Punk.targetBlockV);
			
			
		} else if (mob.state == 49){
			mob.clipAnimTimer();
			mob.state = 48;
			mob.lastState = 48;
			v.set(Punk.targetBlockV);
			b.set(mob.targetV);
			
			//time = Punk.screenTime;
		}
		return false;
		
	}
	private Vector2 v = new Vector2();
	//private long time;
	private Vector2 b = new Vector2();
	private BlockLoc lastB = new BlockLoc();
	boolean moved, valid;;
	@Override
	public void cancelTouch(GenericMob mob) {
		
		
	}

	@Override
	public void onBeltUnselect(GenericMob mob) {
		mob.state = 10;
		
	}

	@Override
	public void beltSelected(GenericMob mob) {	
		if (mob.state == 48 || mob.state == 49){
			if (lastB != mob.targetB){
				valid = map.checkSurroundingBlocks(mob.targetB.x, mob.targetB.y, true);
				lastB.set(mob.targetB);
			}
			if (valid)
				Punk.queueBlockHighlight(mob.targetB);
			else Punk.queueInvalidBlockHighlight(mob.targetB);
		}
	}


	@Override
	public void stats(GenericMob mob) {
		
	}

	@Override
	public void unPress(PunkMap map, GenericMob mob, PunkBodies mi,
			boolean twoPresses) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pressed(GenericMob mob, PunkMap gMap, World world,
			PunkBodies mi, boolean pressed, float deltaTime) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public String getFinalDescription() {
		// TODO Auto-generated method stub
		return null;
	}



}

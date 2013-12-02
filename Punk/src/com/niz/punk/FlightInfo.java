package com.niz.punk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class FlightInfo extends TouchAction{
	private static final String TAG = "FLIGHTINFO";
	public float //thrust=00f, turn=45, lift = 19, drag = .12f;//
	//thrust=00f, turn=45, lift = 19, drag = .12f;//glider
	 thrust=350f, turn=45, lift = 21, drag = .3f;//broomstick
	
	public boolean gliding=true, flapping, rocket;
	public long durability;
	public int airSpeedLimit = 30// 20
			, airSpeedLimit2 = airSpeedLimit * airSpeedLimit;
	
	public FlightInfo(PunkMap map, World world, PunkBodies mi, TextureAtlas atlas, int id){
		super(map, mi, world);
		selectable = true;
		//anim = new CPAnimFly(atlas, id);
		switch (id){
		default:
		case 0:
			thrust=0f;
			
			lift = 9.8f;
			drag = .08f;//broomstick
			durability=1000000;
			airSpeedLimit = 40// 20
					;
			break;
		case 1:
			thrust=250f;
			lift = 8;
			drag = .30f;//gold
			durability=16000;
			airSpeedLimit = 14// 20
					; 
			break;
		case 2:
			thrust=75f;
			lift = 8;
			drag = .08f;//cop
			durability=6000000;
			airSpeedLimit = 30// 20
					; 
			
			break;
		case 3:
			thrust=320f;
			lift = 6;
			drag = .07f;//ir
			durability=600000;
			airSpeedLimit = 18// 20
					; 
			break;
		case 4:
			thrust=00f;
			lift = 46.7f;
			drag = .01f;//di
			durability=1000000;
			airSpeedLimit = 20// 20
			; 
			break;
		}
		 airSpeedLimit2 = airSpeedLimit * airSpeedLimit;
	}

	@Override
	public boolean touchDown(GenericMob mob) {
		// thrust/flap if needed
		mob.angle = mob.touchLoc.angle();
		if (mob.state == 10){
			mob.state = 11;
			mob.touchLoc.set(mob.isLeft?-1:1,0);
		}
		else if (mob.state == 11) mob.state = 22;
		else return false;
		
		return true;
	}

	@Override
	public void touchUp(GenericMob mob) {
		if (mob.state == 22) mob.state = 11;
		
	}

	@Override
	public void cancelTouch(GenericMob mob) {
		// TODO Auto-generated method stub
		
	}
	final float TWOPI = 2f * MathUtils.PI;
	private Vector2 tmpV2 = new Vector2(), tmpV = new Vector2(), tmpD = new Vector2();;
	@Override
	public void beltSelected(GenericMob mob) {
		//Gdx.app.log(TAG, "selectged"+mob.state);
		if (mob.state == 11 || mob.state == 22){
			if (mob.hasHitGround){
				mob.state = 10;
				return;
			}

			mob.inv.reduceDurability(mob.activeInvSlot, Punk.deltaMilli);
			tmpV.set(mob.body.getLinearVelocity());
			mob.direction.set(tmpV);
			float dA = mob.angle;//+180)%360;//desired angle
			float a = tmpV.angle();
			
			float lastAngle = a;
			
			//float liftA = dA
			
			
			float le = tmpV.len2();
			if (le > airSpeedLimit2){
				le = airSpeedLimit2;
				mob.body.setLinearVelocity(tmpV.tmp().nor().mul(airSpeedLimit));
			}
			
			/*float le = Math.abs(tmpV.x);
			if (le > airSpeedLimit){
				float scalar = le/(le-airSpeedLimit);
				body.setLinearVelocity(tmpV.tmp().nor().mul(scalar));
			}*/
			boolean left = (tmpV.x < 0);
			if (mob.isLeft != left){
				mob.isLeft = left;
			}
			dA = a+180+(left?-35:35);
			
			float aoa = dA-180 - a;
			//if (aoa < 0) aoa+= 360;
			aoa %= 360;
			aoa = Math.min(113,aoa);
			aoa = Math.max(-113,aoa);
			//aoa = 0;//
			float liftCoeff = TWOPI * aoa*MathUtils.degreesToRadians;
			liftCoeff = Math.abs(liftCoeff);
			if (liftCoeff > 6)liftCoeff -= 12;
			tmpV2.set(le, 0);
			tmpV2.mul(1f/liftCoeff);
			tmpV2.mul(lift);
			float lastLiftAngle = a+(left?270:90);
			tmpV2.rotate(lastLiftAngle);
			//if (Math.abs(a-180) < 20 || Math.abs(a-180) > 160)tmpV2.set(0,0);
			//Gdx.app.log(TAG, "flight"+liftCoeff+" aoa"+aoa + "  LIFT "+tmpV2);//tmpV2:"+tmpV2 + "  " + tmpV + a + "  da" + dA + "le "+le);
			
			float attack = dA -(left?180:0) - a; // angle of attack
			if (attack < -180) attack += 360;
			float lift = -1 * le * ( this.lift  ) * MathUtils.sin(attack*MathUtils.degreesToRadians);;
		
			tmpV2.set(lift,0);
			tmpV2.rotate(lastLiftAngle);
			//tmpV2.rotate(mob.direction.angle());
			
			
			//Gdx.app.log(TAG, "aoa: "+attack + "  lc "+liftCoeff);
			
			
			
			
			//lift
			mob.body.applyForce( tmpV2, mob.position);
			
			tmpD.set(tmpV2);
			tmpD.mul(-drag);
			
			//dersag
			mob.body.applyForce(tmpD, mob.position);
			
			if (mob.state == 22){
				//thrust
				mob.body.applyForce(tmpV.tmp().nor().mul(thrust), mob.position);
				
			}
			/*if (le < .05f){
				startRunning(-1, time, deltaTime, map, world);
				run(-1, time, deltaTime, map, world);
				stopRunning(time, map, world);
			}*/
			Punk.queueDirectionArrow(mob.position.tmp().add(0, Player.EYEHEIGHT), mob.angle);
			Punk.qFingerHighlight();
		} else if (mob.state == 9){
			mob.state = 11;
		}
	}

	public void beltSelectednew(GenericMob mob) {
		//Gdx.app.log(TAG, "selectged"+mob.state);
		if (mob.state == 11 || mob.state == 22){
			if (mob.hasHitGround){
				mob.state = 10;
				return;
			}

			mob.inv.reduceDurability(mob.activeInvSlot, Punk.deltaMilli);
			tmpV.set(mob.body.getLinearVelocity());
			mob.direction.set(tmpV);
			float dA = (mob.angle+180)%360;//desired angle
			float a = tmpV.angle();
			float lastAngle = a;
			
			//float liftA = dA
			
			
			float speed = tmpV.len2();
			if (speed > airSpeedLimit2){
				speed = airSpeedLimit2;
				//mob.body.setLinearVelocity(tmpV.tmp().nor().mul(airSpeedLimit));
			}
			
			boolean left = dA < 90 || dA > 270, travelLeft = (tmpV.x < 0);
			
			if (mob.isLeft != travelLeft){
				mob.isLeft = travelLeft;
			}
			
			float le = tmpV.len();//Math.abs(tmpV.x);
			/*
		
			
			float aoa = dA-180 - a;
			//if (aoa < 0) aoa+= 360;
			aoa %= 360;
			aoa = Math.min(113,aoa);
			aoa = Math.max(-113,aoa);
			aoa = 0;//
			float liftCoeff = TWOPI * aoa*MathUtils.degreesToRadians;
			liftCoeff = Math.abs(liftCoeff);
			if (liftCoeff > 6)liftCoeff -= 12;
			tmpV2.set(le, 0);
			tmpV2.mul(1f/liftCoeff);
			tmpV2.mul(lift);
			
			tmpV2.rotate(lastLiftAngle);
			//if (Math.abs(a-180) < 20 || Math.abs(a-180) > 160)tmpV2.set(0,0);
			//Gdx.app.log(TAG, "flight"+liftCoeff+" aoa"+aoa + "  LIFT "+tmpV2);//tmpV2:"+tmpV2 + "  " + tmpV + a + "  da" + dA + "le "+le);
			
			float attack = dA -(left?180:0) - a; // angle of attack
			if (attack < -180) attack += 360;
			
			
			all above code is useless
			 
			 
			 
			*/
			float lastLiftAngle = dA+(travelLeft?270:90);
			lastLiftAngle %= 360;
			float attack = a - dA;
			attack = (attack+720)%360;
			if (attack > 180) attack -= 360;
			//steer
			float attackAdj = Math.min(Math.max(attack, -20f), 20f)* .01f;
			//lastLiftAngle += attackAdj * Punk.deltaTime;
			
			float coeff = 1;//Math.max(.2f, MathUtils.sin((90+attack)*MathUtils.degreesToRadians));
			float lift = ( this.lift ) * coeff;;
			lift = Math.abs(lift);
			tmpV2.set(lift,0);
			
			//Gdx.app.log(TAG, "lift" + lift + "  attack "+attack + "   a"+a+"  da"+dA + "  le"+le + "  sin"+coeff);
			Gdx.app.log(TAG, "lift" + lift + "  angle" + lastLiftAngle);
			//Gdx.app.log(TAG, "lift" + lift+"coeff"+coeff);
			tmpV2.rotate(lastLiftAngle);
			//tmpV2.rotate(mob.direction.angle());
			
			
			//Gdx.app.log(TAG, "aoa: "+aoa + "  lc "+liftCoeff);
			
			
			
			
			//lift
			mob.body.applyForce( tmpV2, mob.position);
			
			
			
			float dragCoeff = 
					//CD(1/2)ρV2A
//where: FD is the drag force in lb, ρ is the fluid density in slugs/ft3,
			drag * le;
			
			tmpD.set(tmpV);
			tmpD.mul(-dragCoeff);
			
			
			
			
			//tmpD.rotate();
			
			
			
			
			
			//drag
			//mob.body.applyForce(tmpD, mob.position);
			
			if (mob.state == 22){
				//thrust
				mob.body.applyForce(tmpV.tmp().nor().mul(thrust), mob.position);
				
			}
			
			Punk.queueDirectionArrow(mob.position.tmp().add(0, Player.EYEHEIGHT), mob.angle);
			Punk.qFingerHighlight();
		} else if (mob.state == 9){
			mob.state = 11;
		}
	}

	@Override
	public void onBeltUnselect(GenericMob mob) {
		if (mob.state == 22 || mob.state == 11) mob.state = 10;
		
	}

	@Override
	public void stats(GenericMob mob) {
		// TODO Auto-generated method stub
		
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

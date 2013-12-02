package com.niz.punk.buttons;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.niz.punk.ButtonOverride;
import com.niz.punk.GenericMob;
import com.niz.punk.Player;
import com.niz.punk.Punk;
import com.niz.punk.PunkBodies;
import com.niz.punk.PunkMap;

public class Jetpack extends ButtonOverride {
	//private boolean on = false;
	private float pulseTimer;
	private float pulseMin =.02f , pulseMax = .35f;
	private float thrustRise = 300f, thrustHover = 200f, thrustRunAngle = 35;
	private float sl = 10f, sl2 = sl * sl;//speeed limit
	private int hoverLevel;
	private Vector2 tmpV = new Vector2();
	
	

	@Override
	public void unPress(PunkMap map, GenericMob mob, PunkBodies mi, boolean twoPresses) {
		//on = false;
		//Gdx.app.log("jetp", "unpr");
		//return false;
	}
	
	@Override
	public void pressed(GenericMob mob, PunkMap gMap, World world,
			PunkBodies monsterIndex, boolean on, float deltaTime) {
	//	Gdx.app.log("jetp", "upd");
		if (on){
			Player player = (Player) mob;
			pulseTimer -= Punk.deltaTime;
			if (pulseTimer < 0){
				pulseTimer = MathUtils.random(pulseMin, pulseMax);
				//if (player.isJumping || player.y < hoverLevel){
				tmpV.set(0,thrustRise);
				//if (player.isRunning )tmpV.rotate((player.isLeft?1:-1)*thrustRunAngle);
				player.body.applyLinearImpulse(tmpV, player.position);
				tmpV.set(player.body.getLinearVelocity());
				if (tmpV.len2() > sl2){
					player.body.setLinearVelocity(tmpV.nor().mul(sl));
				}
					
					
				//} else {//hover
				//	player.body.applyLinearImpulse(0,thrustHover, player.x, player.y);
				//}
				Player.particles.blood(player.x, player.y);
				//Gdx.app.log("jetp", "upd inside");
			}
			
		}

	}

	

}

package com.niz.punk;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.World;

public class GrenadeInfo extends TouchAction{
	public GrenadeInfo(PunkMap map, PunkBodies mi, World world) {
		super(map, mi, world);
		
	}
	public int id = 0, meta = 0, bodyDefID = 28;//only set for inv items
	public int lifeTicks;
	public boolean 
			explodesOnGroundContact = false, 
			explodesOnMobContact = false, 
			explodes = false,
			//explodesOnEnemyContact = false,
			collidesWithFriends = false, 
			collidesWithEnemies = true, 
			flying = false,
			hasParticle = false,
			hasCamera;
	public int particleIndex;
	public float throwStrength = 10;
	public int damage = 0;
	public int damageType = DamageType.BLUNT;
	public float srcHeight;
	
	public int 
		release, 
		maxVelocity, minVelocity;
	public CorneredSprite s;
	public ComponentExplosion explosion;
	public boolean bounces = false;
	public boolean hasDirection = false;
	public ParticleEffect particle;
	public GrenadeComponent trail;
	public GrenadeInfo set(
			int id,
			int lifeTicks,
			//boolean explodesOnGroundContact, 
			//boolean explodesOnMobContact,
			//boolean explodes ,
			
			//boolean collidesWithFriends, 
			//boolean collidesWithEnemies,
			//boolean flying,
			//boolean bounces,
			//boolean hasCamera,
			int particleIndex, 
			//int particleIndex,
			int throwStrength,
			int damage,
			int damageType,
			ComponentExplosion explosion,//maximum velocity needed for damage. On flying objects, denotes speed << 2
			float srcHeight,//for wands denotes interval between shots
			int release, int maxVelocity, int minVelocity,
			CorneredSprite s, GrenadeComponent trail, boolean hasDirection){
		this.id = id;
		this.lifeTicks = lifeTicks;
		this.throwStrength = throwStrength;
		this.explodesOnGroundContact= explodesOnGroundContact;
		this.explodesOnMobContact= explodesOnMobContact;
		this.explodes= explodes;
		//this.explodesOnEnemyContact= explodesOnMobContact;
		this.collidesWithFriends= collidesWithFriends;
		this.collidesWithEnemies= collidesWithEnemies;
		this.flying= flying;
		this.bounces = bounces;
		this.trail = trail;
		
		//this.particleIndex = particleIndex;
		this.damage= damage;
		this.damageType= damageType;
		this.srcHeight = srcHeight;
		
		this.srcHeight = srcHeight;
		this.release = release;
		this.maxVelocity = maxVelocity;
		this.minVelocity = minVelocity;
		this.explosion = explosion;
		this.s = s;
		this.hasDirection = hasDirection;
		this.hasParticle = false;
		particle = null;
		this.hasCamera = hasCamera;
		
		return this;
	}
	public GrenadeInfo bounces(){
		bounces = true;
		return this;
	}
	public GrenadeInfo explodesMobs(){
		explodesOnMobContact = true;
		return this;
	}
	public GrenadeInfo explodesGround(){
		explodesOnGroundContact = true;
		return this;
	}
	public GrenadeInfo explodesTimer(){
		explodes = true;
		return this;
	}
	public void setInfo(GrenadeInfo info){
		this.id = info.id;
		this.lifeTicks = info.lifeTicks;
	
		this.explodesOnGroundContact= info.explodesOnGroundContact;
		this.explodesOnMobContact= info.explodesOnMobContact;
		this.explodes= info.explodes;
		//this.explodesOnEnemyContact= info.explodesOnEnemyContact;
		this.collidesWithFriends= info.collidesWithFriends;
		this.collidesWithEnemies= info.collidesWithEnemies;
		this.flying= info.flying;
		this.bounces = info.bounces;

		this.hasParticle = info.hasParticle;
		this.particle = info.particle;
		this.particleIndex = info.particleIndex;
		
		this.throwStrength = info.throwStrength;
		this.damage= info.damage;
		this.damageType= info.damageType;
		
		this.srcHeight = info.srcHeight;
		this.s = info.s;
		this.release = info.release;
		this.maxVelocity = info.maxVelocity;
		this.minVelocity = info.minVelocity;
		this.explosion = info.explosion;
		this.hasDirection = info.hasDirection;
		
		trail = info.trail;
	}
	public void setParticle(ParticleEffect p){
		particle = p;
		hasParticle = true;
	}

	
	
	@Override
	public boolean touchDown(GenericMob mob) {
		if (mob.state == 0){
			mob.state = 7;
			mob.stateTime = 0f;
			mob.angle = mob.touchLoc.angle();
			return true;
		}
		mob.angle = mob.touchLoc.angle();
		mob.faceAngle(true);
		return false;
	}

	@Override
	public void touchUp(GenericMob mob) {
		if (mob.state == 7){
			mob.clipAnimTimer();
			mob.state = 8;
			mob.lastState = 8;
		
		}
		
	}

	@Override
	public void cancelTouch(GenericMob mob) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beltSelected(GenericMob mob) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onBeltUnselect(GenericMob mob) {
		if (mob.state == 7 || mob.state == 8) mob.state = 0;
		
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
	public GrenadeInfo setThrown(ComponentExplosion expl, GrenadeComponent trail, CorneredSprite s) {
		set(//dirt
				0,//id, 
				
				32,//, lifeticks
			
				-1,//damageType
				12,
				1,//
				DamageType.BLUNT,//
				expl,//  srcHeight
				2f,//   (release)
				2000, //maxV
				400, //minV
				150,
				s, trail, false
				);
		return this;
	}
	
	
	
}

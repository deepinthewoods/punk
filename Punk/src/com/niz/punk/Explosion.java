package com.niz.punk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;

public class Explosion extends PhysicsActor {
	private Vector3 tmpV3 = new Vector3(0,0, 0);
	public int gear = 0;//TODO use this for something
	public long updateTimer = 0;
	//public ParticleEmitter emitter;
	//public Vector2 xPosition = new Vector2(0,0);
	private Vector2 tmpV = new Vector2(0,0);
	public ExplosionType xType;
	//public int size = 0;
	//public int strength = 0;
	public float accum = 0;
	private static Player play;
	private ComponentExplosion explodeComponent;
	//public int size = 5;//radius
	public QueryCallback caller = new QueryCallback(){
	
		@Override
		public boolean reportFixture(Fixture fixture) {
			PhysicsActor actor = (PhysicsActor)fixture.getBody().getUserData();
			if (actor == null) return true;
			//Gdx.app.log("explosion", "actor ID:"+actor.actorID);
			tmpV.set(position);
			tmpV.sub(actor.position);
			actor.doExplosionDamage(tmpV, explodeComponent, accum, play);
			//actor.x = actor.x-1;
			actor.updateBBsQueued = true;
			actor.body.setActive(true);
			//Gdx.app.log("explosion", "fixture reported:"+actor.actorID);
			return true;
		}
	};
public Explosion(){
	
}
public void doCallback(World world){
	world.QueryAABB(caller, 
			position.x-explodeComponent.size*1.5f*accum, 
			position.y-explodeComponent.size*1.5f*accum, 
			position.x+explodeComponent.size*1.5f*accum, 
			position.y+explodeComponent.size*1.5f*accum);
}

public void setSprite(Sprite sprite){
	//emitter.setSprite(sprite);
} 
public void set(ComponentExplosion explodeComponent, float ac, Vector2 pos, Player player){
	this.explodeComponent = explodeComponent;
	accum = ac;
	play = player;
}
/*public void set(ParticleEmitter p, Vector2 pos, ExplosionType type, float ac, Player player){
	//emitter = new ParticleEmitter(p);
	xType = type;
	position.set(pos);
	accum = ac;
	play = player;
	//accum should be 0f...1f
	switch (type){
	case GRENADE: size = 5;
			strength = 130;
			actorMeta = 1;
		break;
	case DWARFGRENADE:
			size = 3;
			strength = 80;
			actorMeta = 2;
		break;
	case LAUNCHER: size = 0;
					strength = 0;
					actorMeta = 3;
		break;
	case GOLD_GRENADE:
			 size = 3;
			 strength = 80;
			 actorMeta = 1;
		break;
	case MITHRIL_GRENADE:
			 size = 9;
			 strength = 180;
			 actorMeta = 1;
		break;
	case COPPER_GRENADE: 
			size = 5;
			strength = 130;
			actorMeta = 1;
		break;
	case MOLOTOV:
			size = 1;
			strength = 10;
			actorMeta = 1;
		break;
					
	}
}
*/
public void explode(World world, long time){
	doCallback(world);
	//emitter.reset();
	//emitter.start();
	

	
}


public void reset(PunkBodies monsterIndex){
	health = monsterIndex.ZOMBIEHEALTH;
	markedForRemoval = false;
	
}

public void die(Player player, PunkMap map){
	//emitter
	//dispose the emitter?
	if (player.headTarget == this) player.resetHead();
	map.onExplosion();
	this.deactivate();
	
}

public void updateMove(PunkMap map, World world, float deltaTime, Player player, long time, PunkBodies monsterIndex)
{
	if (animTimer < time) die(player, map);
	checkPosition(player, deltaTime, map, 0f);

}
}

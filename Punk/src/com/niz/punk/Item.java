package com.niz.punk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class Item extends PhysicsActor{
//public int itemID = 0; now represented with actorMeta in PhysicsActor
private Block tmpBlock = new Block(0,0);
private Vector2 tmp = new Vector2(0,0);
public boolean hasHitPlayer;
public boolean isFalling = false;
public float lifeTimer;
//public PhysicsActor boundBoxB, boundBoxS;
public int id, amount,  meta;

public long durability;
public Item(){
		actorID = 1;
	}

public void set(int id, int amount, int meta, long durability){
	this.id = id;
	this.amount = amount;
	this.durability = durability;
	this.meta = meta;
}


public Sprite getFrame(PunkBodies monsterIndex){
	return monsterIndex.getItemFrame(this.actorMeta, 0);
}
public void setItemMeta(int met){
	meta = met;
}
public void deactivate(){
	super.deactivate();
	//boundBoxB.deactivate();
	//boundBoxS.deactivate();
}


public void update(PunkMap map, World world, float deltaTime, Player player, PunkBodies monsterIndex, long time)
{
	
	stateTime += deltaTime;

	lifeTimer -= deltaTime;
	
	if (animTimer < time){
		animTimer = time+200;
		isFalling = !(map.getBlock(x,y-1).blockType() >=64); 
	}
	if (isFalling) position.y -= deltaTime*5;
	//else y += MathUtils.sin(stateTime)/5;
	x = MathUtils.floor(position.x);
	y = MathUtils.floor(position.y);
	isLeft = (player.position.x<position.x);
	boolean goingUp = (player.position.y > position.y);
	checkPositionNoBody(player, deltaTime);
	if (distanceFromPlayer <= 2 && (player.inv.hasSpaceFor(actorMeta, state)) && player.health > 0){
		//move towards player
		tmpV.set(position).sub(player.position.x, player.position.y+1);
		if (tmpV.len2() < 1 && player.health >0){
			player.inv.addItem(this);
			deactivate();
			return;
		}
		tmpV.nor().mul(-deltaTime*12);
		position.add(tmpV);
		
	}
	
	if (stateTime > 300){
		deactivate();
	}
	
}

public void destroy() {
	id = 0;
	amount = 0;
	meta = 0;
	durability = 0;
	
}

public void checkValidity() {
	if (amount == 0 || (durability < 0 && PunkBodies.getItemInfo(id, meta).durability > 0)){
		id = 0;
		
		meta = 0;
		durability = 0;
	}
	
}
public boolean reduceDurability(float d){
	if (PunkBodies.getItemInfo(id, meta).durability == 0) return false;
	durability -= d*1000;
	return (durability < 0);
}
public String toString(){
	return "id"+id+"  amount"+amount+"  meta"+meta;
}

public boolean reduceDurability() {
	durability -= 1;
	return (durability <= 0);
}

public void set(Item a) {
	id = a.id;
	amount = a.amount;
	meta = a.meta;
	durability = a.durability;
	
}

public boolean increaseAmount() {
	amount++;
	if (amount > PunkBodies.getItemInfo(id, meta).amount){
		amount--;
		return false;
	}
	return true;
}



}

package com.niz.punk;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class Bullet extends PhysicsActor {

public float stateTime = (float)Math.random();
//public static int bulletType;
private Vector2 bulletTmp = new Vector2(0,0);
private Vector2 bulletAddTmp = new Vector2(0,0);
	
	public Bullet(int type, World world, PunkBodies monsterIndex, Vector2 spawnPos){
		super(5, world, monsterIndex, spawnPos);
		actorMeta = type;
		//System.out.println("Bullet created");
		//bulletType = type;
		
	}
	public Bullet(){
		
	}

	public TextureRegion getFrame(PunkBodies monsterIndex){
		
		//return null;
		//return monsterIndex.rockAnim.getKeyFrame(stateTime, true);
		return null;
	}
	Vector2 startPosition = new Vector2(0,0);
	public void shoot(Vector2 touch, int strength, int bulletT, PhysicsActor source, World world){
		//world.createbody(monsterIndex.)
		startPosition.set(source.position.x+(source.isLeft?-0.8f:0.8f), source.position.y + 1);
		////Gdx.app.log("bullet", "left:"+source.isLeft);
		
		body.setTransform(startPosition, 0);
		body.setActive(true);
		bulletTmp.set(touch);
		bulletTmp.mul(-1);//reverse?
		bulletTmp.mul(1f/touch.len());//make 1 unit long
		bulletAddTmp.set(bulletTmp);
		bulletAddTmp.mul(3f);
		bulletTmp.mul(strength);
		bulletTmp.add(bulletAddTmp);
		//bulletType = bulletT;
		actorMeta = bulletT;
		//TODO MODIFY ALL THE VELOCITIES HERE
		/*switch (actorMeta)
			{
			case -1:
				this.deactivate();
			
			break;
				case 1: body.setLinearVelocity(bulletTmp);
						System.out.println ("typefdfa: "+actorMeta);
						if (source instanceof Player)source.setHeadTarget(body);
				break;
				case 2: body.setLinearVelocity(bulletTmp);
						System.out.println("hook fired");
				break;
				case 3: body.setLinearVelocity(bulletTmp);
						if (source instanceof Player)source.setHeadTarget(body);
				//Log.d("deb", "grenade fired");
				
				break;
				case 6:
				case 5:
				case 4:
					body.setLinearVelocity(touch);
					//Gdx.app.log("bullet", "bullet fired from trajectory");
			}*/
	}
	
	
	
	public void updateBullet(PunkMap map, World world, float deltaTime, Player player, PunkBodies monsterIndex, long time)
	{
		/*if (body.isActive())
		{
			//System.out.println("body active! bullet!");
			
			if (x != lastx || y!= lasty)
			{
				//System.out.println("position changed., type:"+bulletType);
				switch(actorMeta)
				{
				case 5:
				case 1: if (map.getBlock(x,y).blockType >3)
					{
						deactivate();
						if (player.headTarget == body)player.resetHead();
					}
					break;		
				case 3: 
					
					if (isFlashing || map.getBlock(x,y).blockType >2) 
					{
						////////map.explosionPool.createExplosion(ExplosionType.GRENADE, health, world, map, monsterIndex, position, time, player);
						deactivate();
						if (player.getHeadTarget() != player.body) player.setHeadTarget(map.explosionPool.getLast().body);
						map.makeAFire(x, y);
					}
				break;
				case 6:if (map.getBlock(x,y).blockType >2) 
				{
					
					////////////*map.explosionPool.createExplosion(ExplosionType.DWARFGRENADE, health, world, map, monsterIndex, position, time, player);
					deactivate();
					if (player.getHeadTarget() != player.body) player.setHeadTarget(map.explosionPool.getLast().body);
					map.makeAFire(x, y);
				}
				break;
				}
				lastx = x;
				lasty = y;
			}//if changed
			
		}//if active*/
	}
	
}

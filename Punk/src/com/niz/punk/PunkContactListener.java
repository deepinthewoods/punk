package com.niz.punk;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

public class PunkContactListener implements ContactListener {
	private static final String TAG = "contact listener";
	private PhysicsActor contactActorA = new PhysicsActor();
	private PhysicsActor contactActorB = new PhysicsActor();
	Player player;
	PunkBodies monsterIndex;
	World world;
	PunkMap map;
	long time;
	Punk main;
	
	//boolean playerUnderRope = false;
	
	public PunkContactListener(Player player, PunkBodies mi, World w, PunkMap m, Punk main){
		this.player = player;
		monsterIndex = mi;
		world = w;
		map = m;
		this.main = main;
	}
	
	public void processContacts(List<Contact> list, Player player, long time, PunkMap map, World world, PunkBodies monsterIndex){
		for (int i = 0; i<list.size(); i++)
		{
			//list.get(i).getFixtureA().getBody();
			Contact contact = list.get(i);
			if (contact == null) Gdx.app.log("contactlistener", "null contact!");
			Fixture fixA = contact.getFixtureA();
			Fixture fixB = contact.getFixtureB();
			if (fixA != null && fixB != null){//TODO fix the nulls! ergh
				contactActorA = (PhysicsActor)(contact.getFixtureA().getBody().getUserData());
				contactActorB = (PhysicsActor)(contact.getFixtureB().getBody().getUserData());
				
				
			}
			if (contactActorA != null && contactActorB != null){
				contactActorA.doCollision(contactActorB, time, player, map, world, monsterIndex, contact);
				contactActorB.doCollision(contactActorA, time, player, map, world, monsterIndex, contact);
			}
			//System.out.println("collision:"+contactActorA.actorID + contactActorB.actorID);
			//System.out.println("contactA: "+contactActorA.getID() + "contactB: "+contactActorB.getID());
			
		}
		
	}
	
	public void beginContact(Contact contact){
		/*contactActorA = (PhysicsActor)(contact.getFixtureA().getBody().getUserData());
		contactActorB = (PhysicsActor)(contact.getFixtureB().getBody().getUserData());
		Gdx.app.log("contactlistener", "begincontact:"+contactActorA.actorID+" with "+contactActorB.actorID);

		//contactActorB.getID();
		contactActorA.doCollision(contactActorB, time, player, map, world, monsterIndex);
		contactActorB.doCollision(contactActorA, time, player, map, world, monsterIndex);*/
	}
	public void endContact(Contact contact){
		PhysicsActor actA, actB;
		actA = (PhysicsActor)contact.getFixtureA().getBody().getUserData();;
		actB = (PhysicsActor)contact.getFixtureB().getBody().getUserData();;
		
		if (actB != null && actA != null){
			//Gdx.app.log("contactlistener", "presolve:"+act1.actorID+" with "+act2.actorID);
			if (actA.actorID == 0 && actB.actorID == 31){
				if (!player.isClimbingRope && player.climbTimer < time && actA.body.getLinearVelocity().y<-1){
					player.isClimbingRope = true;
					player.isHoldingRope = false;
					
				}
				
			}
			
			if (actB.actorID == 0 && actA.actorID == 31){
				if (!player.isClimbingRope&& player.climbTimer < time && actB.body.getLinearVelocity().y<-1){
					player.isClimbingRope = true;
					player.isHoldingRope = false;
					//player.activeRopeLink = (RopeLink)(actA);
				}
				/*if (!player.isClimbingRope && player.climbTimer < time){
					//player.isClimbingRope = true;
					//player.isHoldingRope = false;
					player.activeRopeLink = (RopeLink)(actB);
				}*/
			}
			
			if (actA.actorID == 0 || actB.actorID == 0){
				
				if (actA.actorID == 56){//door
					//contact.setEnabled(false);
					player.activeDoor = null;
					main.closeAction();
				} else if (actB.actorID == 56){
					//contact.setEnabled(false);
					player.activeDoor = null;
					main.closeAction();
				}
			}
			
			
			
		}//not null
		
	}
	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		PhysicsActor actA, actB;
		actA = (PhysicsActor)contact.getFixtureA().getBody().getUserData();;
		actB = (PhysicsActor)contact.getFixtureB().getBody().getUserData();;
		
		if (actB != null && actA != null){
			/*if (actA.actorID == 47 && (actB.actorID == 39 || actB.actorID == 28 || actB.actorID == 47)){
				MPlank plank = (MPlank)actA;
				Vector2 newPt = contact.getWorldManifold().getPoints()[0];
				plank.newLength = actA.body.getLocalPoint(newPt).x;
				
				float[] impulses = impulse.getNormalImpulses();
					//Gdx.app.log("contact", "impulses:"+impulses[0]+","+impulses[1]);
				if (Math.abs(impulses[0]) > plank.strength){// || Math.abs(impulses[1]) > plank.strength) {
						plank.breakPending = true;
						Gdx.app.log("contact", "impulses:"+impulses[0]+","+impulses[1]);
				}
								
			}*/
			 

			/*else if (actB.actorID == 47 && (actA.actorID == 39 || actA.actorID == 28 || actA.actorID == 47)){
				MPlank plank = (MPlank)actB;
				Vector2 newPt = contact.getWorldManifold().getPoints()[0];
				plank.newLength = actB.body.getLocalPoint(newPt).x;
				
				float[] impulses = impulse.getNormalImpulses();
					
				if (Math.abs(impulses[0]) > plank.strength ){//|| Math.abs(impulses[1]) > plank.strength) {
					plank.breakPending = true;
					Gdx.app.log("contact", "impulses:"+impulses[0]+","+impulses[1]);
				}
					
			}*/
			
		}
		
	}
	Vector2 playerV = new Vector2();
	Vector2 ropeV = new Vector2();
	
	
	
	
	@Override
	public void preSolve(Contact contact, Manifold man) {
		// if player collides with rope, disable contact and make player climb
		//Gdx.app.log("contactlistener", "presolve.");
		PhysicsActor actA, actB;
		actA = (PhysicsActor)contact.getFixtureA().getBody().getUserData();;
		actB = (PhysicsActor)contact.getFixtureB().getBody().getUserData();;
		
		if (actB != null && actA != null){
			//Gdx.app.log("contactlistener", "presolve:"+act1.actorID+" with "+act2.actorID);
			
			
			/*if ((actA.actorID == 38 && actB.actorID == 28) || (actB.actorID == 38 && actA.actorID == 28)){
				//ninjas vs grenade
				contact.setEnabled(false);
			}*/
			
			
			//if ((actA.actorID == 39 || actB.actorID == 39) && (player.isAimingPoi || player.isPrePoi)) contact.setEnabled(false);
			
			//slow bullets
			/*if (actA.actorID == 0 && actB.actorID == 52){
				Player p = (Player)actA;
				actA.takeDamage(actB.actorMeta+1, DamageType.ZAP, p);
				Gdx.app.log("contact","done damage222");
				actB.deactivate();
				contact.setEnabled(false);
				//Gdx.app.log("contact","done damage");
			} else if (actB.actorID == 0 && actA.actorID == 52){
				Player p = (Player)actB;
				actB.takeDamage(actB.actorMeta+1, DamageType.ZAP, p);
				Gdx.app.log("contact","done damage222");
				actA.deactivate();
				contact.setEnabled(false);
				//Gdx.app.log("contact","done damage");
			}*/
			
			/*if (actA.actorID == 55 && actB.actorID == 55){
				contact.setRestitution(1.2f);
			}*/
			if (actA instanceof GenericMob && actB instanceof GenericMob){
				GenericMob a = (GenericMob) actA, b = (GenericMob) actB;
				if (a.faction.opinion.get(b.faction.id) > 0)
					contact.setEnabled(false);
			}
			
			
			if (actA instanceof Grenade && actB instanceof GenericMob){//mob+grenade
				//Gdx.app.log(TAG, "gren col");
				Grenade g = (Grenade)actA;
				boolean friend = false;
				GenericMob gCol = (GenericMob) actB;
				if (PunkBodies.factions[g.factionID].opinion.get(gCol.faction.id) > 0) friend = true;
				//Gdx.app.log(TAG, "friemd"+friend);
				//if (friend && !info.collidesWithFriends) 
				boolean disabled = (friend && !g.info.collidesWithFriends) || (!friend && !g.info.collidesWithEnemies);
				if (disabled){
					contact.setEnabled(false);
					//Gdx.app.log(TAG, "disabled grenade");
				}
				if (g.info.flying){
					contact.setEnabled(false);
					if (!friend && g.info.collidesWithEnemies){
						actA.doCollision(actB, time, player, map, world, monsterIndex, contact);
						
					}
				}
				
			}
			if (actB instanceof Grenade && actA instanceof GenericMob){//mob+grenade
				//Gdx.app.log(TAG, "grenade col");
				Grenade g = (Grenade)actB;
				boolean friend = false;
				GenericMob gCol = (GenericMob) actA;
				if (PunkBodies.factions[g.factionID].opinion.get(gCol.faction.id) > 0) friend = true;
				//Gdx.app.log(TAG, "friemd"+friend);
				boolean disabled = (friend && !g.info.collidesWithFriends) || (!friend && !g.info.collidesWithEnemies);
				if (disabled){
					contact.setEnabled(false);
					//Gdx.app.log(TAG, "disabled grenade");
				}
				if (g.info.flying){
					contact.setEnabled(false);
					if (!friend && g.info.collidesWithEnemies){
						actA.doCollision(actB, time, player, map, world, monsterIndex, contact);
						
					}
				}
			}
			
			
			
			
			
			
		}//IF NOT NULL
		
	}
}

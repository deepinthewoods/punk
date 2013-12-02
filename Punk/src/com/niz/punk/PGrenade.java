package com.niz.punk;

import java.util.Iterator;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class PGrenade extends Pool<Grenade> {
	PunkMap map;
	World world;
	PunkBodies mi;
	public PGrenade(PunkMap map, PunkBodies mi, World world){
		this.map = map;
		this.world = world;
		this.mi = mi;
	}
	private Array<Grenade> list = new Array<Grenade>();
	private Grenade gren;
	public void add(Grenade g){
		list.add(g);
	}
	
	public void draw(SpriteBatch batch){
		
		for (int i = 0; i < list.size; i++){
			gren = list.get(i);
			//list.get(i).draw(batch);
			if (gren.isVisible){
				gren.draw(batch);
				
			}
		}
	}
	
	Iterator<Grenade> iter;
	public void updateMove(PunkMap map, World world, float deltaTime, Player player, long time, PunkBodies monsterIndex)
	{
		iter = list.iterator();
		while (iter.hasNext())
		{	gren = iter.next();
			gren.updatePA(map, world, deltaTime, player, time, monsterIndex);
		}
	}
	int tmpi;
	
	
	
	public void updateRemovals(World world, Player player){
		//handling despawning here as well as destroying the bodies
		
		int i = 0;
		while (i < list.size)
			{
				gren = list.get(i);
				
				if (gren.updateRemoval(world))
				{
					//world.destroyBody(gren.body);
					//gren.body = null;
					//gren.destroyBBs(world);
					list.removeIndex(i);	
					free(gren);
					//gren.markedForRemoval = false;
					//System.out.println("gggggggggggggggggggggg removed");
				}else 
					i++;
			}
		
	}
	
	@Override
	protected Grenade newObject() {
		
		// TODO Auto-generated method stub
		return new Grenade(map, mi, world);
	}

	public void destroyAll() {
		Iterator<Grenade> i = list.iterator();
		while (i.hasNext()){
			Grenade g = i.next();
			g.deactivate();
		}
	}

}

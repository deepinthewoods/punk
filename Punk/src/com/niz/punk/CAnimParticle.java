package com.niz.punk;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class CAnimParticle extends ComponentAnimation {
	public ParticleEffect p;
	@Override
	public void draw(GenericMob mob, SpriteBatch batch){
		//super.draw(mob, batch);
		p.setPosition(mob.x, mob.y);
		p.draw(batch);
	}
	
}

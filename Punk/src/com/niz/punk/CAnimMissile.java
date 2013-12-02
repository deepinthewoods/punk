package com.niz.punk;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class CAnimMissile extends ComponentAnimation {
	int id;
	CorneredSprite[] anim;
	public CAnimMissile(int id, CorneredSprite[] anim){
		this.id = id;
		this.anim = anim;
	}
	public ParticleEffect p;
	@Override
	public void draw(GenericMob mob, SpriteBatch batch){
		//super.draw(mob, batch);
		CorneredSprite s = anim[mob.actorMeta];
		s.setCorners(mob.lightBits, mob.dayBits);
		s.draw(batch);
		p.setPosition(mob.x, mob.y);
		p.draw(batch);
	}
}

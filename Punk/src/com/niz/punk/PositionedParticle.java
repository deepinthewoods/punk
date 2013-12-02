package com.niz.punk;

public class PositionedParticle{
public ParticleEffect e;
public int type;


public void position(GenericMob mob){
	switch (type/16){
		case 0:
		default:
			e.setPosition(mob.position.x+mob.originOffset.x*(mob.isLeft?1:-1), mob.position.y+mob.originOffset.y);
			break;
		case 1:
			e.setPosition(mob.position.x+mob.handROffset.x*(mob.isLeft?1:1), mob.position.y+mob.handROffset.y);
			break;
		case 2:
			e.setPosition(mob.position.x+mob.handLOffset.x*(mob.isLeft?1:1), mob.position.y+mob.handLOffset.y);
			break;
	}
	
}
}

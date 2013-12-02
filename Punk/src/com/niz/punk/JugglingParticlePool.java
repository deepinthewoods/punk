package com.niz.punk;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class JugglingParticlePool extends Pool<JugglingParticle> {
	public static float[] initialVelocities;//20th of a second sooner so they get caught

	private Array<JugglingParticle> list = new Array<JugglingParticle>();
	private JugglingComponentAnimation[] anims = new JugglingComponentAnimation[20];;
	private JugglingParticleBehavior[] propBehaviors = new JugglingParticleBehavior[]{
		new JugglingParticleBehavior(){
			@Override
			public void act(JugglingParticle p, PunkMap map) {//std ball
				
			}
		},
		new JugglingParticleBehavior(){
			@Override
			public void act(JugglingParticle p, PunkMap map) {
				
			}
		},
		new JugglingParticleBehavior(){
			@Override
			public void act(JugglingParticle p, PunkMap map) {
				
			}}
		
	
	};
	@Override
	protected JugglingParticle newObject() {
		return new JugglingParticle();
	}

	public JugglingParticlePool(){
		int max = 50;
		initialVelocities = new float[max];
		for (int i = 0; i < max; i++){
			float t = .5f*i, g = -10;
			initialVelocities[i] = (2*t*g)/MathUtils.sinDeg(90);
		}
	}
	public void addJugglingProp(GenericMob mob, int propType, int throwType, int throwHeight){
		JugglingParticle p = obtain();
		list.add(p);
		p.target = mob;
		p.behavior = propBehaviors[propType];
		p.anim = anims[propType];
		p.data = throwType;
		p.time = 0;
		p.v.set(0, initialVelocities[throwHeight]);
		
	}
	
	public void addPsyBoid(){
		
	}
	
	public void addLutin(GenericMob mob, int type){
		
	}
	
}

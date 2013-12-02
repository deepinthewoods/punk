package com.niz.punk;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class JugglingComponentAnimation {
	
	public void draw(JugglingParticle p, SpriteBatch batch) {
		p.s.setPosition(p.pos.x, p.pos.y);
		p.s.draw(batch);
		
	}

}

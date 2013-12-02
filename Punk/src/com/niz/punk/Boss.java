package com.niz.punk;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public interface Boss {
	
	public void update(PunkMap map, World world, float deltaTime, Player player, long time, PunkBodies monsterIndex, BulletPool bulletP);
	public void init(int ID, Vector2 position,World world, PunkBodies monsterIndex);
	public void draw(Camera camera, SpriteBatch batch, BitmapFont font, PunkBodies monsterIndex);
	public void clear(PunkMap map, World world);
	public void goHostile();
}

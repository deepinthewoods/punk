package com.niz.punk;

import com.badlogic.gdx.physics.box2d.World;

public interface Component {
public abstract void act(GenericMob mob, PunkMap map, Player player, PunkBodies monsterIndex, World world);

}

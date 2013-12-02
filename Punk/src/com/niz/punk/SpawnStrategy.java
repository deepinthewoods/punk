package com.niz.punk;

import com.badlogic.gdx.physics.box2d.World;

public interface SpawnStrategy {
public void attemptSpawn(PunkMap map, Player player, World world, PunkBodies monsterIndex);
}

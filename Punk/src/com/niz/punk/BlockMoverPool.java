package com.niz.punk;

import java.util.Iterator;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class BlockMoverPool extends Pool<BlockMover> {
	public Array<BlockMover> list = new Array<BlockMover>(true, 8);
	@Override
	protected BlockMover newObject() {
		
		return new BlockMover();
	}
	
	public void draw(SpriteBatch batch, PunkBodies monsterIndex){
		Iterator<BlockMover> it = list.iterator();
		while (it.hasNext()){
			BlockMover m = it.next();
			
			m.draw(batch, monsterIndex);
		}
	}
	
	public void update(PunkMap map, float delta){
		for (int i = list.size-1; i >=0; i--)
		{
			BlockMover m = list.get(i);
			if (m.update(map, delta)){
				free(m);
				list.removeIndex(i);
			}
		}
	}
	
	public void add(int x, int y, int type, Block b){
		BlockMover m = obtain();
		int dx=0, dy=0;
		switch (type){
		case 0:dy = 1;break;
		case 1:dx = 1;break;
		case 2:dy = -1;break;
		case 3:dx = -1;break;
		}
		m.start(x, y, dx, dy, b);
		list.add(m);
	}

}

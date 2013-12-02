package com.niz.punk;

import com.badlogic.gdx.utils.Pool;

public class PDoor extends Pool<Door> {

	@Override
	protected Door newObject() {
		
		return new Door();
	}

}

package com.niz.punk;

import com.badlogic.gdx.utils.Pool;

public class TimedEffect {
public Effect e;
public float time;
static class Tpool extends Pool<TimedEffect>{

	@Override
	protected TimedEffect newObject() {
		return new TimedEffect();
	}
	
}
public static Tpool pool = new Tpool();
public static TimedEffect get(Effect e){
	TimedEffect ef = pool.obtain();
	ef.e = e;
	ef.time = e.time;
	return ef;
}
}

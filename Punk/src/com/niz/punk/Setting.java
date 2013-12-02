package com.niz.punk;

import com.badlogic.gdx.Preferences;

public class Setting {
	public String prefName;
public int value;
public String[] names;
public Preferences prefs;
public Setting(String pn, Preferences p, String... strings){
	names = strings;
	prefs = p;
	prefName = pn;
	getVal();
}

void getVal() {
	value = prefs.getInteger(prefName);
}
private void saveVal(){
	prefs.putInteger(prefName, value);
	prefs.flush();
	
	
}

public void inc(){
	value++;
	value %= names.length;
	saveVal();
}


////////////////////////////////////////////////////////////////////////////////






}

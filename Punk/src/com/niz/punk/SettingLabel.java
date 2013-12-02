package com.niz.punk;

import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class SettingLabel extends Label{
	Setting setting;
	public SettingLabel(Setting sett, Skin skin) {
		super("", skin);
		setting = sett;
		EventListener settingIncrementer = new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				
				SettingLabel lab = (SettingLabel)event.getListenerActor();
				lab.setting.inc();
				lab.setName();
			}
		};
		addListener(settingIncrementer);
		setting.getVal();
		setName();
	}
	public void setName(){
		setText(setting.names[setting.value]);
	}
	
}

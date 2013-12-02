package com.niz.punk;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class StartStage extends Stage {
	private NewGameScreen newG;
	public StartStage(float width, float height, boolean stretch, NewGameScreen ng) {
		super(width, height, stretch);
		newG = ng;
	}
	
	@Override
	public boolean keyDown(int arg0) {
		// TODO Auto-generated method stub
		switch (arg0) {
		case Keys.S:
			//newG.showMenuButtons();
			break;
		case Keys.H:
			//newG.hideMenuButtons();
			break;
		case Keys.BACK:
		case Keys.ESCAPE:
			//if (miniGamesOn)
				//newG.showMenuButtons();
			break;
		}
		return false;
	}

}

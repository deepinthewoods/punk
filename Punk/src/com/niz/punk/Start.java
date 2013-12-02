package com.niz.punk;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Peripheral;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class Start extends Game {
	byte mode = 99, lastMode = 0;
	public Punk mainLoop;// = new Punk();
	//public MainMenu menu ;
	public NewGameScreen newG;// = new NewGameScreen(this);
	//public PreGameScreen preG;
	public LoadScreen loadScreen = new LoadScreen(this);
	public SpriteBatch spriteBatch, loadBatch;
	public BitmapFont menuFont;
	public Preferences prefs ;
	public TextureAtlas atlas;
	public NinePatch button9, buttonGrey9;
	public Sprite load, dot;
	private AdWhirlViewHandler adWhirlViewHandler;
	public static MemInfo mi;
	public void setAdWhirlViewHandler(AdWhirlViewHandler adWhirlViewHandler) {
		this.adWhirlViewHandler = adWhirlViewHandler;
	}
	public static boolean saveData = false;
	@Override
	public void create() {
		
		load = new Sprite(new Texture(Gdx.files.internal("data/loadscreen.png")));
		dot = new Sprite(new Texture(Gdx.files.internal("data/dot.png")));
		//load.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		load.setSize(1,1);
		loadBatch = new SpriteBatch();
		float width = Gdx.graphics.getWidth()/(float)Gdx.graphics.getHeight();
		loadBatch.getProjectionMatrix().setToOrtho2D(-width/4, 0, width, 1);
		if (mi == null) mi = new MemInfoDesktop();
	}
	
	public void createTextures() {
		menuFont = new BitmapFont(Gdx.files.internal("data/font16.fnt"), Gdx.files.internal("data/font16.png"),false);
		
		setUpPrefs();
		//if (!prefs.contains("savedata")){
			saveData = true;
			prefs.putBoolean("savedata", true);
		//}
		
		
		//prefs.clear();
		//prefs.
		//prefs.flush();
		//setUpPrefs();
		
		Punk.getGraphics();
		newG = new NewGameScreen(adWhirlViewHandler);
		newG.setStarter(this);
		
		loadScreen.set(menuFont, loadBatch, dot);
		setScreen(loadScreen);
		
		loader.load("data/tiles.png", Texture.class);
		loader.load(tilesLoc[0], TextureAtlas.class);
		if (saveData){
			loader.load(tilesLoc[1], TextureAtlas.class);
			for (int i = 3; i < tilesLoc.length; i++){
				loader.load(tilesLoc[i], TextureAtlas.class);
			}
		}
		loader.load(tilesLoc[2], TextureAtlas.class);
		//Gdx.app.log("start", "engine");
		
		byte b = -12;
		int i = b;
		Gdx.app.log("start", "foo"+i);

	}
	String[] tilesLoc = {"data/tiles.txt", "data/humans.txt", "data/mobs.txt"};//, "data/ogres.txt"};//, "data/wargs.txt", "data/zombies.txt"};
	public Start(){
		
	}
	AssetManager loader = new AssetManager();
	
	@Override
	public void render(){
		//setScreen(mainLoop);
		
		if (mode != lastMode)
		{
			switch (mode)
			{
	
			case 6://new survival. This is th main one!
			//	Gdx.app.log("start", "six");
				newG.gameType = 1;
				newG.populateValidGames();
				newG.dialog = 3;
				setScreen(newG);
				Gdx.input.setInputProcessor(newG.startMux);
				lastMode = mode;
			break;
		
			case 99:
				setScreen(loadScreen);
				loadBatch.begin();
				load.draw(loadBatch);
				
				loadBatch.end();
				mode = 66;
				//lastMode = mode;
				return;
			case 66:
				
				createTextures();
				
				mode = 33;
				
				return;
			case 33:
				
				loader.update();
				loadScreen.prog = loader.getProgress();
				
				//Gdx.app.log("start", "33"+loadScreen.prog);
				boolean isLoaded = true;
				for (int i = 0; i < tilesLoc.length; i++){
					if (!loader.isLoaded(tilesLoc[i]))isLoaded = false;
				}
				
				if (isLoaded){
					TextureAtlas[] ats = new TextureAtlas[tilesLoc.length];
					for (int i = 0; i < tilesLoc.length; i++){
						ats[i] = loader.get(tilesLoc[i], TextureAtlas.class);
					}
					newG.create(spriteBatch, mainLoop, prefs, ats);
					
					newG.startEngine();
					
					mode = 6;
					
				}
				break;
				//break;
			}
			
		}
		
		Screen screen = getScreen();
		screen.render(
				Gdx.graphics.getDeltaTime());
		
		
		
	}
	
	@Override
	public void dispose(){
		//TODO
		//mainLoop.dispose();
		
	}
	@Override
	public void pause(){
		if (getScreen() instanceof Punk) {
			Punk g = (Punk)getScreen();
			g.pause();
			
			//g.gameMode = 19;
		}
	}
	public void setUpPrefs(){
		//check if it exists already, otherwise create some fields
		prefs = Gdx.app.getPreferences("MMprefs");
		Punk.prefs = prefs;
		prefs.clear();
		if (!prefs.contains("version"))
			prefs.putInteger("version", 1);
		if (!prefs.contains("debug"))
			prefs.putInteger("debug", 1);
		if (!prefs.contains("buttonsOn"))
			prefs.putInteger("buttonsOn", 1);
		if (!prefs.contains("soundOn"))
			prefs.putInteger("soundOn", 0);
		if (!prefs.contains("musicOn"))
			prefs.putInteger("musicOn", 0);
		if (!prefs.contains("backgroundOn"))
			prefs.putInteger("backgroundOn", 1);
		if (!prefs.contains("menuBtn"))
			prefs.putInteger("menuBtn",0);
		if (!prefs.contains("ropeJoints"))
			prefs.putInteger("ropeJoints", 8);
		//if (!prefs.contains("validGameTypes"))
			prefs.putInteger("validGameTypes", 2);
		if (!prefs.contains("zoomBtn"))
			prefs.putInteger("zoomBtn", 
					Gdx.input.isPeripheralAvailable(Peripheral.MultitouchScreen)?
					1:0);
		
		
		/*for (int i = 0; i < 20; i++){
			if (!prefs.contains("classEnabled"+i))
				prefs.putInteger("classEnabled"+i, 0);
		}*/
		/*if (!prefs.contains("classEnabled0"))
			prefs.putInteger("classEnabled0", 0);
		if (!prefs.contains("classEnabled8"))
			prefs.putInteger("classEnabled8", 0);
		if (!prefs.contains("classEnabled3"))
			prefs.putInteger("classEnabled3", 0);
		if (!prefs.contains("classEnabled1"))
			prefs.putInteger("classEnabled1", 0);*/
		
		if (!prefs.contains("classEnabled13"))
			prefs.putInteger("classEnabled13", 0);
		
		
		if (!prefs.contains("deityEnabled0"))
			prefs.putInteger("deityEnabled0", 0);
		if (!prefs.contains("deityEnabled1"))
			prefs.putInteger("deityEnabled1", 0);
		if (!prefs.contains("deityEnabled2"))
			prefs.putInteger("deityEnabled2", 0);
		
		if (!prefs.contains("raceEnabled0"))
			prefs.putInteger("raceEnabled0", 0);
	}
	
	public void setMemInfo(MemInfo mia) {
		mi = mia;
		
	}

}

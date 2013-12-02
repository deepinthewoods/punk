package com.niz.punk;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GLCommon;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntArray;

public class NewGameScreen implements Screen, InputProcessor {
	private static final int CLASS_TOTAL = 0;
	public BitmapFont menuFont;
	// public SpriteBatch menuBatch;
	private int yres, xres;
	public Start starter;
	public Punk engine;
	public Preferences prefs;
	//public boolean[] validGames = new boolean[6];
	//private static String saveDir;// = "BlockHead/saves/";
	private FileHandle playerHandle;
	public int selectedSlot, selectedPage = 0;;
	public int dialog, loadCount, gameType = 1;
	private AdWhirlViewHandler myRequestHandler;
	int validTotal = 0;
	private boolean isNewGame = false, beenTouched = false;
	private PreGameScreen preG;
	// public Sprite selectS;
	public Sprite logo, title;
	public Stage startStage;
	//public ButtonForMenu adventureBtn, deleteBtn, loadBtn, deleteNoBtn,
	//		deleteYesBtn, creativeBtn, playBtn, creditsBtn, newNameBtn, sizeSmBtn, sizeNrmBtn, sizeLrgBtn, newConfBtn;
	//public ButtonForMenu[] classSelect = new ButtonForMenu[CLASS_TOTAL];
	private boolean newQueued, loadQueued, creditsOn, creativeQueued = false;
	private float queueTimer = 0, creditsTimer = 0;
	//public ButtonForGameSelect[] gameButtons = new ButtonForGameSelect[6];
	private PunkBodies monsterIndex;
	public SpriteBatch batch;
	public Skin skin;
	public int kills = -1;
	public int firstAvailableCreativeName, creativeViewPage = 0;;
	private static String TAG = "newG";
	public NewGameScreen(AdWhirlViewHandler handler) {
		myRequestHandler = handler;
	}
	
	
	public class ColorButton extends Button{
		int id;
		public ColorButton(int id){
			super(new SpriteDrawable(PunkBodies.colorPixels[id]));
			//super(skin);
			this.id = id;
		}
	}
	
	/////////////////////////////////////////
	///////////////////////////////////////////
	/////////////////////////////////////
	
	
	Label creditsL, infoL, deityInfoL, deityNameL;
    //Skin skin;
    Stage stage;
    Texture texture;
    Table root, table, newGameTable, resumeTable, creditsTable, newStage2, deitySelect, colorSelect, detailsMenu;
    float fadeTime = 1;
    List gamesList, classList, raceList, genderList, deityList;
    Button resumeB, creditsB, newB, colorB, deityB;
    private TextField gameNameField;
    int selectedClass=0, selectedRace=0, selectedGender=0, selectedColor1, selectedColor2, selectedSkin, selectedHair, selectedDeity;
    Deity[] deityArray;
    Button newBtn;
    Array<MobInfo> availableClasses;
    public void createButtons() {
            stage = new Stage(xres, yres, false);
           // Gdx.input.setInputProcessor(stage);
            Drawable up = new NinePatchDrawable(new NinePatch(atlas.createSprite("buttonupnine"), 4, 4, 4, 4)), 
            		down = new NinePatchDrawable(new NinePatch(atlas.createSprite("buttondownnine"), 0, 0, 0, 0)),
            		checked = new NinePatchDrawable(new NinePatch(atlas.createSprite("buttoncheckednine"), 0, 0, 0, 0));
            TextButtonStyle butStyle = new TextButtonStyle(up, down, checked);
            butStyle.font = menuFont;
            
            LabelStyle labStyle = new LabelStyle(menuFont, Color.WHITE);
            
            ListStyle listStyle = new ListStyle(menuFont, Color.WHITE, Color.GRAY, checked);
            
           // TableStyle tableStyle = new TableStyle();
            menuFont.setScale(2);
            skin = new Skin();
            skin = new Skin(Gdx.files.internal("data/uiskin.json"));
            skin.add("default", butStyle, ButtonStyle.class);
            skin.add("default", labStyle, LabelStyle.class);
            skin.add("default", listStyle, ListStyle.class);
            
            if (xres > 630){
            	//skin.getFont("default-font").setScale(2f);
            	
            }
            
             newBtn = new Button(skin){
            	public void draw(SpriteBatch batch, float parentAlpha){
            		//if (gameNameField.getText().length() > 2) 
            		super.draw(batch, parentAlpha);
            	}
            };
            newBtn.add("Start");
            newBtn.addListener(new ClickListener(){
                public void clicked (InputEvent event, float x, float y) {
                	root.clear();
                	Punk.gameName = gameNameField.getText();
                	initNewGame();
					newGame();
                }
            });
            
            Button nextBtn = new Button(skin){
            	public void draw(SpriteBatch batch, float parentAlpha){
            		//if (gameNameField.getText().length() > 2) 
            		super.draw(batch, parentAlpha);
            	}
            };
            nextBtn.add("Next");
            nextBtn.addListener(new ClickListener(){
                public void clicked (InputEvent event, float x, float y) {
                	MobInfo info = GenericMob.classInfos[selectedClass];
                	openDetailsMenu();
                	if(true)return;
                	//else open color/skills selection
                	
                	
//                	root.clear();
//                	Punk.gameName = gameNameField.getText();
//                	initNewGame();
//					newGame();
                }
            });

           // NinePatch patch = skin.getPatch("default-round");

            Label label = new Label("World Name", skin);
            
            root = new Table();
            root.setClip(true);
            //root.setWidth(10);
            //root.setHeight(10);
            //Gdx.app.log("start", "done");
            stage.addActor(root);
            gameNameField = new TextField(NameGenerator.getName(0), skin);
            int gamec = 0;
            while (gameExists(gameNameField.getText())){
            	gameNameField.setText(NameGenerator.getName(0));
            	if (gamec++ > 1000000) throw new GdxRuntimeException("no valid game names");
            }
            table = new Table();
            //root.setFillParent(true);
            
           // table.setSize(400,50);
            //table.size(300,80);
            //table.right();
            //table.right().size(200,80);
            
           // root.setSize(xres, yres);
            //Gdx.app.log("start", "done");
            
            //table.setBackground(new NinePatchDrawable(patch));
            table.setClip(false);          
            table.add(label);                     
            table.add(gameNameField);//.size(40,60);
            table.pack();
            
            
            
            colorSelect = new Table(skin);
            for (int i = 0; i < 4;i++){
            	for (int j = 0; j < 8;j++){
            		Button b = new ColorButton(i*8+j);
            		
            		if (i == 0)b.addListener(new ClickListener(){
            			public void clicked (InputEvent event, float x, float y) {
            				ColorButton b = (ColorButton) event.getRelatedActor();
            				selectedColor1 = b.id;
            			}
            		}); else if (i == 1) b.addListener(new ClickListener(){
            			public void clicked (InputEvent event, float x, float y) {
            				ColorButton b = (ColorButton) event.getRelatedActor();
            				selectedColor2 = b.id-8;
            			}
            		});
            		else if (i == 2) b.addListener(new ClickListener(){
            			public void clicked (InputEvent event, float x, float y) {
            				ColorButton b = (ColorButton) event.getRelatedActor();
            				selectedColor2 = b.id-16;
            			}
            		});
            		else if (i == 3) b.addListener(new ClickListener(){
            			public void clicked (InputEvent event, float x, float y) {
            				ColorButton b = (ColorButton) event.getRelatedActor();
            				selectedColor2 = b.id-24;
            			}
            		});
            		colorSelect.add(b).size(xres/8, (yres)/12);
            		
            	}
            	colorSelect.row();
            }
            detailsMenu = new Table(skin);
            
            
            
            deitySelect = new Table(skin);
            IntArray validD = new IntArray();
            for (int i = 0; i < 9;i++){
            	if (prefs.contains("deityEnabled"+i))
            	validD.add(i);
            }
            deityArray = new Deity[validD.size];
            for (int i = 0; i < validD.size;i++){
            	deityArray[i] = PunkBodies.deities[validD.get(i)];
            }
            deityList = new List(deityArray,skin);
            ScrollPane deityPane = new ScrollPane(deityList);
            deityPane.addListener(new ChangeListener(){

				@Override
				public void changed(ChangeEvent event, Actor actor) {
					selectedDeity = deityList.getSelectedIndex();
					deityInfoL.setText(PunkBodies.deities[selectedDeity].desc);
					//
					//deityInfoL.layout();
					//deityInfoL.pack();
					
					//deityNameL.setText("");//PunkBodies.deities[selectedDeity].name + " : "+PunkBodies.deities[selectedDeity].nickName);
					//
					//deityNameL.layout();
					//deityNameL.pack();
					deitySelect.layout();
				}
            	
            });
            
            
            Button deityDoneB = new Button(skin){
            	public void draw(SpriteBatch batch, float parentAlpha){
            		//if (gameNameField.getText().length() > 2) 
            		super.draw(batch, parentAlpha);
            	}
            };
            deityDoneB.add("Done");
            deityDoneB.addListener(new ClickListener(){
                public void clicked (InputEvent event, float x, float y) {
                	openDetailsMenu();
                }
            });
           
            
            
            
            deityInfoL = new Label("", skin);
            deityInfoL.setWrap(true);
            deityInfoL.setAlignment(Align.left);
            deityNameL = new Label("Select Deity:", skin);
            deityNameL.setWrap(true);
            deityNameL.setAlignment(Align.left);
           // infoTab.add(infoL).size(xres/2, yres/4*3).padLeft(20);
            
            Table deityInfo = new Table(skin);
           // deityInfo.add("").left().bottom().expandY();;
            deityInfo.row();
            deityInfo.add(deityInfoL).size(xres/2-20, yres/4*3).left().top();
            deityInfo.pack();
            
            
            
            deitySelect.add("").expandX().expandY();;
           	deitySelect.add(deityDoneB).right().bottom().expand();
           	deitySelect.row();
            deitySelect.add(deityNameL);
            deitySelect.row();
            deitySelect.add(deityPane).top().left().expandX().padRight(10);
            deitySelect.add(deityInfo).expand();
            deitySelect.row();
           
           
            deitySelect.pack();
           // deitySelect.expand();
            
            
            
            newGameTable = new Table(skin);
            //newGameTable.setFillParent(true);
            availableClasses = new Array<MobInfo>(true, GenericMob.classInfos);;
       
            for (int i = PunkBodies.CLASSTOTAL-1; i >=0 ; i--){
            	if (!prefs.contains("classEnabled"+i)){
            	//if (GenericMob.classInfos[i])
            		availableClasses.removeIndex(i);
            	}
            }
            availableClasses.shrink();
            
            
            
            
            
            classList = new List(availableClasses.toArray(), skin);
            //.clicked(null, 1,1);
            selectedClass = availableClasses.get(0).classID;
            classList.pack();
            
           // newGameTable.add(new Label("Select Class:", skin));
           // newGameTable.add(new Label("Select Race:", skin));
          
            //newGameTable.row();
            ScrollPane classScr = new ScrollPane(classList);
            classScr.pack();
            Table classTable = new Table(skin);
            
            classTable.add(new Label("Class:\n", skin));
            classTable.row();
            classTable.add(classScr);
            classTable.pack();
           // classList.setSelectedIndex(-1);
            Array<RaceDef> raceSelection = new Array<RaceDef>(true, GenericMob.raceInfos);;
           
            for (int i = PunkBodies.RACETOTAL-1; i >=0 ; i--){
            	if (!prefs.contains("raceEnabled"+i)){
            	//if (GenericMob.classInfos[i])
            		raceSelection.removeIndex(i);
            	}
            }
            
            raceSelection.shrink();
            raceList = new List(raceSelection.toArray(), skin);
            raceList.pack();//raceList.setFillParent(true);
            raceList.setColor(Color.CLEAR);
            classList.addListener(new ChangeListener(){

				@Override
				public void changed(ChangeEvent event, Actor actor) {
					selectedClass = availableClasses.get(classList.getSelectedIndex()).classID;
					infoL.setText(availableClasses.get(classList.getSelectedIndex()).description);
					//populate race list and fade in
					raceList.setColor(Color.CLEAR);
					raceList.addAction(Actions.fadeIn(fadeTime));
				}
            	
            });
            infoL = new Label("Class selection affects your jump ability, and which quests and skills are available to you. ", skin);
            infoL.setWrap(true);
           
            infoL.setAlignment(Align.left);
            //infoL.setSize(xres/2, yres);
            //infoL.layout();
           // infoL.pack();
            //infoL.setFillParent(true);
            //Label blankLabel = new Label("", skin), topLabel1 = new Label("Class:", skin), topLabel2 = new Label("Race:", skin), topLabel3 = new Label("Description:", skin);
            //newGameTable.add(blankLabel);
            //newGameTable.add(topLabel3);
            //newGameTable.row();
            
            Table column2 = new Table(skin);
            genderList = new List(new String[]{"Male", "Female"}, skin);
            genderList.addListener(new ChangeListener(){

				@Override
				public void changed(ChangeEvent event, Actor actor) {
					gameNameField.setText(NameGenerator.getName(genderList.getSelectedIndex()));
					selectedGender = genderList.getSelectedIndex();
					while (gameExists(gameNameField.getText())){
		            	gameNameField.setText(NameGenerator.getName(genderList.getSelectedIndex()));
		            }
					
				}
            	
            });
            column2.add(new Label("Gender:\n", skin));
            column2.row();
            column2.add(genderList);
            column2.row();
            column2.add(new Label("Race:\n", skin)).padTop(15);;
            column2.row();
            column2.add(raceList);
            column2.row();
            column2.pack();
            Table infoTab = new Table(skin);
            Table nameTable = new Table(skin);
            nameTable.add("Name: ");
            nameTable.add(gameNameField);
            nameTable.pack();
            
            //gameNameField.("Name");
            infoTab.add(nameTable);
            infoTab.row();
            
            infoTab.add(infoL).size(xres/2, yres/4*2.5f).padLeft(20);
            infoTab.pack();
            Table selectionTab = new Table(skin);
            //selectionTab.add(topLabel1).padBottom(10);
           // selectionTab.add(topLabel2).padLeft(20).padBottom(10);
            //selectionTab.row();
            selectionTab.add(classTable).left();
            selectionTab.add(column2).padLeft(20).top();
            newGameTable.add("").expandX();
            newGameTable.add(nextBtn).right();
            newGameTable.row();
            newGameTable.add(selectionTab).top();
            newGameTable.add(infoTab);
            infoL.pack();
           // newGameTable.row();
            
           // newGameTable.add(new Label(" ", skin));
            newGameTable.row();
            
            
            label.addListener(new ClickListener() {
                    public void clicked (InputEvent event, float x, float y) {
                           	fadeOutAll();  
                           	root.clear();
                            root.add(newGameTable);
                            newGameTable.addAction(Actions.fadeIn(fadeTime));
                            root.pack();
                    }
            });
            
            newGameTable.pack();
            infoL.pack();
            infoL.layout();
           
            infoL.layout();
            
            Table gamesListTable = new Table(skin);
            gamesList = new List(files, skin);
    		ScrollPane gamesPane = new ScrollPane(gamesList, skin);     
    		//gamesPane.size(300, 100);
    		gamesListTable.addActor(gamesPane);
    		gamesListTable.validate();
    		gamesList.pack();
    		gamesPane.pack();
    		gamesListTable.pack();
    		
    		
    		resumeTable = new Table(skin);
    		resumeTable.size(100,100);
    		Button resumeBtn = new Button(skin);
    		Label resLabel = new Label("Play", skin);
    		resumeBtn.add(resLabel).padLeft(10).padRight(10).padBottom(3);;
    		resumeBtn.setWidth(resLabel.getMaxWidth());
    		ClickListener listener = new ClickListener() {
                public void clicked (InputEvent event, float x, float y) {
                	fadeOutAll();
                    //root.add(newGameTable);
                    //newGameTable.addAction(Actions.fadeIn(fadeTime));
                    Punk.gameName = files[gamesList.getSelectedIndex()].nameWithoutExtension();
					initNewGame();
					newGame();
            }};
    		
    		resumeBtn.addListener(listener);
    		resumeTable.add(gamesListTable).left();
    		
    		colorB = new Button(skin);
    		deityB = new Button(skin);
    		colorB.add(new Label("Customize Colors", skin)).padLeft(10).padRight(10).padBottom(3);
    		colorB.addListener(new ClickListener(){

				@Override
				public void clicked(InputEvent event, float x, float y) {
					//fadeOutAll();
					openColorSelect();
				}

				
    			
    		});
    		deityB.add(new Label("Change Deity", skin)).padLeft(10).padRight(10).padBottom(3);
    		deityB.addListener(new ClickListener(){

				@Override
				public void clicked(InputEvent event, float x, float y) {
					//fadeOutAll();
					openDeity();
				}

				
    			
    		});
    		
    		newB = new Button(skin);
    		resumeB = new Button(skin);
    		creditsB = new Button(skin);
    		
    		newB.add(new Label("New Game", skin)).padLeft(10).padRight(10).padBottom(3);
    		newB.addListener(new ClickListener(){

				@Override
				public void clicked(InputEvent event, float x, float y) {
					//fadeOutAll();
					openNew();
				}

				
    			
    		});
    		resumeB.add(new Label("Resume", skin)).padLeft(10).padRight(10).padBottom(3);
    		resumeB.addListener(new ClickListener(){

				@Override
				public void clicked(InputEvent event, float x, float y) {
					fadeOutAll();
					root.add(resumeTable);
					resumeTable.addAction(Actions.fadeIn(fadeTime));
					resumeTable.setColor(Color.CLEAR);
				}
    			
    		});
    		
    		creditsTable = new Table(skin);
    		//creditsTable.setSize(xres, yres);
    		//creditsTable.size(xres, yres);
    		//creditsTable.size(200,800);
    		creditsL = new Label(creditsStr, skin);
    		creditsL.setAlignment(Align.center);
    		creditsL.setWrap(true);
    		creditsL.addListener(new ClickListener(){

				@Override
				public void clicked(InputEvent event, float x, float y) {
					root.clear();
					showMenuButtons();
					Gdx.app.log(TAG, "click");
				}
    			
    		});
    		creditsTable.add(creditsL).expandY();
    		creditsTable.row();
    		//creditsTable.add(creditsL).bottom();
    		//creditsTable.row()
    		;
    		//creditsTable.setFillParent(true);
    		creditsTable.pack();
    		creditsB.add(new Label("Credits", skin)).padLeft(10).padRight(10).padBottom(3);
    		creditsB.addListener(new ClickListener(){
				@Override
				public void clicked(InputEvent event, float x, float y) {
					fadeOutAll();
					
					root.add(creditsTable);
	                //creditsTable.addAction(Actions.fadeIn(fadeTime));
					creditsTable.addAction(Actions.fadeIn(fadeTime));
					creditsTable.setColor(Color.CLEAR);
				}
    			
    		});
    		
    		
    		
    		showMenuButtons();
    		//
    		root.setFillParent(true);
    		//stage.setSize(xres, yres-Punk.adBuffer);
    		//stage.s
            //root.padBottom(Punk.adBuffer);
            root.setPosition(0,0);
           // root.setSize(100,100);
            root.debug();
           
           // root.padBottom(Punk.adBuffer);//;.size(xres, yres-Punk.adBuffer);
            root.pack();
            startStage.addActor(root);
            startStage.setViewport(xres,  yres, true);
            
            
            
            initNew2();
    }
    private IntArray priestClasses = new IntArray(new int[]{1,5,10});
    private void openDetailsMenu() {
    	detailsMenu.clear();
    	detailsMenu.add(newBtn);
    	detailsMenu.row();
		if (priestClasses.contains(selectedClass))
			;//detailsMenu.add(deityB);
		detailsMenu.row();
		//detailsMenu.add(colorB);
		detailsMenu.row();
		root.clear();
		root.add(detailsMenu);
		root.pack();
	}
    
    private void openNew() {
    	root.clear();
		root.add(newGameTable).expandX();
        //newGameTable.addAction(Actions.fadeIn(fadeTime));
       // newGameTable.setColor(Color.CLEAR);
		raceList.setColor(Color.CLEAR);
		root.pack();
					
	
    }
    private void openDeity() {
    	root.clear();
		//root.row();
		root.add(deitySelect).expandX();
		root.row();
		root.pack();
	}
    
    private void openColorSelect() {
		root.clear();
		root.add(colorSelect).top();
		root.row();
		
		root.pack();
	}
    TextureAtlas atlas;
	public void create(SpriteBatch batch, Punk mainloop, Preferences pref, TextureAtlas... tatlas) {
	
		monsterIndex = new PunkBodies();
		monsterIndex.start(tatlas[0], tatlas[1], tatlas[2]);
		menuFont = starter.menuFont;
		engine = mainloop;
		prefs = pref;
		yres = Gdx.graphics.getHeight();
		xres = Gdx.graphics.getWidth();
		beenTouched = false;
		selectedSlot = prefs.getInteger("lastPlayedGame");
	
		startStage = new StartStage(xres, yres, true, this);
		startStage.getRoot();
		atlas = tatlas[0];
	
		title = atlas.createSprite("logo");
		startStage.unfocusAll();
		Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"));
		populateValidGames();
		
		//gamesPane.
		//startStage.addActor(gamesPane);
		
		createButtons();
		
		logo = atlas.createSprite("menuback");
		logo.setSize(yres, yres);
		logo.setPosition((xres-yres)/2, 0);
		// Gdx.app.log("start", "yres:"+yres);
		this.batch = startStage.getSpriteBatch();
		
		lastLoadedGame = prefs.getString("lastLoadedGame");
		selectedPage = 0;
		selectedSlot = 0;
		for (int i = 0; i < files.length; i++){
			if (files[i].name().contains(lastLoadedGame) && files[i].name().length() == lastLoadedGame.length()){
				selectedSlot = i%6;
				selectedPage = i / 6;
			}
		}
		if (files.length > 0)
		Punk.gameName = files[selectedSlot+selectedPage*6].name();
		//Gdx.app.log("start", "input");
		startMux = new InputMultiplexer();
		
		startMux.addProcessor(startStage);
		startMux.addProcessor(this);
		
		
		
		Gdx.input.setInputProcessor(startMux);
		
	}

	private boolean gameExists(String text) {
		for (int i = 0; i < files.length; i++){
			if (files[i].nameWithoutExtension().contentEquals(text)) return true;
		}
		return false;
	}




	Table deityTab, skillTab;
	String[] deityDescriptions = {};
    private void initNew2() {
		// TODO Auto-generated method stub
    	newStage2 = new Table(skin);
    	//deity selection and or skills
    	deityTab = new Table();
    	skillTab = new Table();
    	deityTab.add(new List(new String[]{"",""}, skin));
	}


	private void prepareNew2() {
		newStage2.clear();
		//skills and stat points
	}
	
	protected void fadeOutAll() {
		root.clear();
		root.pack();
		 Array<Actor> a = root.getChildren();
         Iterator<Actor> i = a.iterator();
         while (i.hasNext()){
         	Actor act = i.next();                          	
         	
			//act.addAction(Actions.sequence(Actions.fadeOut(fadeTime), Actions.removeActor()));
         }                
		
	}

	String creditsStr = "Niall Quinlan:\n    Code, Terrain&Misc Graphics, Menu Music \n\n" +
			"Enemy and some Item Graphics by \n   David Gervais and Henk Brouwer  \n" +
			"Game Music by Holmes\nBig Menu Buttons by Ravenmore\n\n\n";
	
	
	void showMenuButtons() {
		//fadeOutAll();
		
		root.add(resumeB).pad(20).right().expandX();
		root.row();
		root.add(newB).pad(20).right().expandX();
		root.row();
		root.row();
		root.add(creditsB).pad(20).right().expandX();
		root.pack();
		root.validate();
		root.addAction(Actions.fadeIn(fadeTime));
		root.setColor(Color.CLEAR);
	}




	InputMultiplexer startMux;
	String lastLoadedGame;
	FileHandle[] files;
	public void populateValidGames() {
		
		//if (selectedSlot == -1)
			selectedSlot = 0;
			selectedPage = 0;
		FileHandle folder = Gdx.files.external(Punk.saveDir);
		files = folder.list();
		//for (int i = 0, lim = Math.min(files.length-1, 6); i < lim; i++){
			//gameButtons[i].label = files[i].name();
		//}

		dialog = 0;// 1=newgame confirm 2=load game confirm
		validTotal = 0;
		

	}

	@Override
	public void render(float delta) {
		GLCommon gl = Gdx.gl;
		gl.glClearColor(0, 0, 0, 1);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		batch.begin();
		//logo.draw(batch);
		float width = xres/10;
		int yreps = (int) (yres/width)+1;
		for (int x = 0; x < 20; x++){
			for (int y = 0; y < yreps; y++){
				CorneredSprite s =  PunkBodies.getBlockSprites(2, 0);;
				s.setBounds(x*width, y*width, width, width);
				//tmpBlock.sprite[1].setCorners(tmpBlock.lightBits, tmpBlock.dayBits);
				s.setCornersSimple((byte)7,(byte)0);
				s.draw(batch);
			}
		}
		title.setPosition(xres/2-width*3, yres-width*2);
		title.setSize(width*6, width*2);
		title.draw(batch);
		
		batch.end();

		

		

		// menuBatch.end();
		startStage.act(delta);
		startStage.draw();
		//Table.drawDebug(startStage);

	}

	String strKills = "kills: ";

	public void setStarter(Start thegame) {
		starter = thegame;
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(startMux);
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		//Gdx.input.setInputProcessor(this);
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		// if (starter.getScreen() instanceof Punk )engine.saveGame();

	}

	@Override
	public boolean keyDown(int arg0) {
		// TODO Auto-generated method stub
		//Gdx.app.log(TAG, "key");
		switch (arg0) {
		case Keys.S:
			showMenuButtons();
			break;
		case Keys.H:
			hideMenuButtons();
			break;
		case Keys.BACK:
		case Keys.ESCAPE:
			//if (miniGamesOn)
			root.clear();
			showMenuButtons();
			break;
		case Keys.F6:
			starter.setScreen(engine);
			
			break;
		}
		return false;
	}

	@Override
	public boolean keyTyped(char arg0) {
		// TODO Auto-generated method stub
		//Gdx.app.log(TAG, "ket");
		return false;
	}

	@Override
	public boolean keyUp(int arg0) {
		// TODO Auto-generated method stub
		Gdx.app.log(TAG, "keyu");
		return false;
	}

	@Override
	public boolean scrolled(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public void newGame() {
		Punk.getSaveLoc(Punk.path);
		FileHandle folder = Gdx.files.external(Punk.path.toString());
		Gdx.app.log(TAG, "creating folders "+Punk.path.toString());
		folder.mkdirs();
		// save a new info
		int startoff = 0;
		int seed = MathUtils.random(-32000, 32000);//30;// -25866;//treeish 
		Gdx.app.log("newG", "seed:" + seed);
		if (gameType == 0)
			seed = 19;
		// seed = 0;
		PunkMap.openWorld = true;
		//PunkMap.dungeonMode = false;
		Chunk.seed = seed;
		//Chunk.reset();
		//Chunk.primeMaterialPlane.setUpFeatures(seed, engine.monsterIndex);
		
		//engine.worldGenIncY = Math.max(Chunk.getGroundHeight(-Chunk.primeMaterialPlane.wWidth)/Punk.CHUNKSIZE+1, Chunk.getGroundHeight(Chunk.primeMaterialPlane.wWidth)/Punk.CHUNKSIZE+1);
		
		GameInfo tmpInfo = new GameInfo(selectedSlot, seed, gameType);
		String[] featureNames = new String[]{"main"};
		Chunk.createNewFeatures(tmpInfo.gameSeed, engine.monsterIndex, engine.gMap, 0, featureNames);
		//Chunk.activePlane = Chunk.primeMaterialPlane;
		int offset = Chunk.getGroundHeight(tmpInfo.spawnPosition.x) + 22;
		//plane
		
		
		tmpInfo.spawnPosition.y = offset;
		tmpInfo.savedPosition.y = offset;
		/*
		 * teyerClass = selectedClass; tmpInfo.playerRace =
		 * selectedRace; tmpInfo.playerGender = selectedGender;
		 */
		//Gdx.app.log("newG", "spawn   "+tmpInfo.savedPosition);
		//Gdx.app.log("new", "spawn positiiiitiotititiotiottiotiotoition"+tmpInfo.spawnPosition);
		//tmpInfo
		// for (int i = 0; i < 1000; i+=10){
		// Gdx.app.log("newG", "height:"+Chunk.getGroundHeight(i));
		// }
		engine.monsterIndex.initFactions();
		
		
		Punk.getSaveLoc(Punk.path);
		//FileHandle folder = Gdx.files.external(Punk.path.toString());
		Punk.path.append(Player.PATH_GAME_INFO);
		tmpInfo.writeToFile(Gdx.files.external(Punk.path.toString()));
		Gdx.app.log(TAG, "tmpInfo "+Punk.path.toString());

		
		
		
		
		
		
		Punk.getSaveLoc(Punk.path);
		//FileHandle folder = Gdx.files.external(Punk.path.toString());
		Punk.path.append(Player.PATH_STATS);
		Stats st = new Stats(gameType);
		st.writeToFile(Gdx.files.external(Punk.path.toString()));
		Gdx.app.log(TAG, "stats "+Punk.path.toString());

		
		isNewGame = true;
		engine.setStarter(starter);
		Gdx.app.log("new", "starting campaign game");
		startCampaignGame();
		if (priestClasses.contains(selectedClass)){
			engine.monsterIndex.factions[PunkBodies.deities[selectedDeity].factionID].opinion.set(1, 1000);
			PunkBodies.factions[1].opinion.set(PunkBodies.deities[selectedDeity].factionID, 1000);
			engine.player.addAllBlessings();
			Gdx.app.log(TAG, "deity"+PunkBodies.factionString());
		}
	}

	public void loadGame() {
		// Gdx.app.log("newG", "loadGame");
		
		
		isNewGame = false;

		startCampaignGame();
		// menuBatch.end();

	}

	public void startEngine() {
		// player init

		engine = new Punk(myRequestHandler);
		engine.game = starter;

		// Gdx.app.log("newG",
		// "cache info, slot "+selectedSlot+":\n\n"+oldInfo);
		// Gdx.app.log("newG", "creating player phase 1"+st);
		engine.player = new Player(new Vector2(-10000, 0), engine);
		// Gdx.app.log("newG",
		// "creating player phase 2, seed:"+oldInfo.gameSeed+"in player:"+engine.player.gameInfo.gameSeed);
		// Gdx.app.log("newG", "player minutes = "+oldInfo.minutes);
		// Gdx.app.log("newG", "playerinit:"+engine.player.gameInfo.gameID);
		engine.create(prefs, starter, monsterIndex);
		// Gdx.app.log("newG", "createPlayer");
		// createplayer(etc)
		// Gdx.app.log("newG", "created Player");
		engine.createPhysicsWorld(engine.world, isNewGame);
		engine.monsterIndex.initPlayerTools(engine.gMap, engine.world);
		//Chunk.setupPixels(new Pixmap(Gdx.files.internal("data/blocks.png")));
	}
/*
	public void startNewCreative() {
		String name = "creative" + firstAvailableCreativeName;
		PunkMap.creativeGameName = name;
//		engine.gMap.chunkC.setAsCreative(engine.gMap);
		;
//		engine.gMap.saveCreativeChunk();
		startNewCreative(name);
	}

	public void startNewCreative(String name) {
		PunkMap.creativeGameName = name;
		initMiniGameData(1);
		// String saveLoc = saveDir+"creative/player.inf";

		// miniInfo.spawnPosition.set(64,64);
		// miniInfo.savedPosition.set(64,64);
		engine.gMap.chunkActors.destroyAll(engine.player, engine.world);
		String saveLoc = "mysticalmayhem/saves/creative/" + name + ".inf";
		GameInfo oldInfo = new GameInfo(0, 0, 0);

		Gdx.app.log("main", "loading info. loc " + saveLoc);
		miniInfo.readFromFile(Gdx.files.external(saveLoc));

		// if (!isNewGame)
		// Chunk.setUpMiniGameFeatures();

		Player.permissions.setCreative();
		PunkBodies.SPAWNMAX = 256;
		// miniStats.setItem(0,50,64,0);
		// miniStats.setItem(1,70,64,0);
		engine.player.activeQuickSlot = 6;

		// GameInfo oldInfo = new GameInfo();

		// Gdx.app.log("main", "loading info. loc "+saveLoc);
		// miniInfo.readFromFile(Gdx.files.external(saveLoc));

		miniStats.setForCreative();
		//Chunk.setFeaturesForCreative();

		engine.gMap.chunkActors.destroyAll(engine.player, engine.world);
		engine.gMap.updater.destroyAll();
		engine.gMap.clear();

		miniInfo.health = engine.player.MAXHEALTH;
		engine.player.setInfo(miniInfo, miniStats);
		engine.player.activeQuickSlot = 0;
		engine.player.deathComponent = miniDeath;
		// engine.changeBeltSlot(engine.player.activeQuickSlot, false);
		engine.player.createPlayer(engine.world, engine.monsterIndex);
		engine.resetButtons();

//		engine.gMap.chunkT.setForMiniGame(true);
//		engine.gMap.chunkTL.setForMiniGame(true);
//		engine.gMap.chunkTR.setForMiniGame(true);
//		engine.gMap.chunkL.setForMiniGame(true);
//		engine.gMap.chunkR.setForMiniGame(true);
//		engine.gMap.chunkB.setForMiniGame(true);
//		engine.gMap.chunkBL.setForMiniGame(true);
//		engine.gMap.chunkBR.setForMiniGame(true);

		engine.gMap.start(false, 1);
//		engine.gMap.chunkC.setForMiniGame(true);
//		engine.gMap.fetchCreativeChunk(engine.gMap.chunkC);

		//for (int i = 0; i < 30; i++)
		//	engine.gMap.updateTimed();
		engine.player.updateFloor(engine.gMap, engine.world, Punkesque.gTime, true);
		engine.player.updateBottom(engine.gMap);

		engine.doBackgroundMesh();
		engine.adjustSkyColor();
		prefs.putInteger("lastPlayedGame", selectedSlot);
		if (prefs.getBoolean("musicOn")) {
			engine.monsterIndex.music.play();
			engine.monsterIndex.menuMusic.stop();

		}

		starter.setScreen(engine);
		Gdx.input.setInputProcessor(engine.inputMultiplexer);

	}

	*/

	public void startCampaignGame() {
		//if (prefs.contains("lastGameName"))
		prefs.putString("lastLoadedGame", Punk.gameName);
		engine.gMap.chunkActors.destroyAll(engine.player, engine.world);
		//else prefs.
		// player
		//String saveLoc = "mysticalmayhem/saves/" + gameType + "/" + "game"
		//		+ selectedSlot + "/player.inf";
		GameInfo gameInfo = new GameInfo();
		Punk.getSaveLoc(Punk.path);
		Punk.path.append(Player.PATH_GAME_INFO);
		gameInfo.readFromFile(Gdx.files.external(Punk.path.toString()));
		
		//saveLoc = "mysticalmayhem/saves/" + gameType + "/" + "game"
		//		+ selectedSlot + "/player.stats";
		Stats st = new Stats(gameType);
		Punk.getSaveLoc(Punk.path);
		Punk.path.append(Player.PATH_STATS);
		st.readFromFile(Gdx.files.external(Punk.path.toString()));

		if (!isNewGame){
			Chunk.loadFeatures(gameInfo.gameSeed, engine.monsterIndex);
			//engine.miniMap.waypoints.addAll(Chunk.ways);
			//Player.pathBuilder.setLength(0);
			//Punk.getSaveLoc(Player.pathBuilder);
			//Player.pathBuilder.append(engine.player.PATH_STORED_ITEMS);
			//FileHandle storedInvHandle = Gdx.files.external(Player.pathBuilder.toString());
			//Player.storedInventory.readFromFile(storedInvHandle);

		}
		else {// is new game
			st.setForCampaign();
			
		}
		PunkMap.creativeGameName = null;

		Player.permissions.setCampaign();
		engine.player.deathComponent = null;
		PunkBodies.SPAWNMAX = 40;
		
		//engine.gMap.updater.destroyAll();
		engine.gMap.clear();
		
		engine.worldGenInc = -Chunk.primeMaterialPlane.wWidth;
		//worldGenInc;// = -Chunk.primeMaterialPlane.wWidth, worldGenIncY;;
		engine.genTimeLast = System.currentTimeMillis();
		
		
		
		/*
		 * st.setItem(0,50,64,0); st.setItem(1, 270, 1, 128); st.setItem(2, 434,
		 * 64, 0); st.setItem(3, 310, 1, 1); st.setItem(4, 417, 1, 128);
		 * st.setItem(5, 350, 1, 128); st.setItem(6, 437, 64, 0); st.setItem(7,
		 * 417, 1, 128); st.setItem(8, 417, 1, 128); st.setItem(9,50,64,0);
		 * st.setItem(10,50,64,0);
		 */
		//gameInfo.savedPosition.y = Chunk.getGroundHeight(gameInfo.savedPosition.x);
		engine.player.setInfo(gameInfo, st);
		//Gdx.app.log("newG", "CAMPAIGN" +gameInfo.savedPosition);
		PunkMap.currentPlane = Player.gameInfo.savedPlane;
		engine.gMap.start(true, -1);
		Chunk.planes.get(PunkMap.currentPlane)
		.miniMap.makeMeshZoomedOut(Chunk.seed);;
		// engine.changeBeltSlot(engine.player.activeQuickSlot, false);
		//Gdx.app.log("newG", "CAMPAIGN" +gameInfo.savedPosition);
		engine.player.createPlayer(engine.world, engine.monsterIndex, engine.gMap, selectedClass, selectedRace, selectedGender);
		engine.resetButtons();
		Gdx.app.log("newG", "CAMPAIGN" +engine.player.x+","+engine.player.y+"   "+engine.player.position);
		
		//Gdx.app.log(TAG, "plane "+Player.gameInfo.savedPlane);
		
		//engine.miniMap.waypoints.addAll(Chunk.ways);
		

		//for (int i = 0; i < 30; i++)
		//	engine.gMap.updateTimed();
		engine.player.updateFloor(engine.gMap, engine.world, Punkesque.gTime, true);
		engine.player.updateBottom(engine.gMap);

		engine.doBackgroundMesh();
		//engine.adjustSkyColor();
		prefs.putInteger("lastPlayedGame", selectedSlot);

		if (prefs.getBoolean("musicOn")) {
			engine.monsterIndex.music.play();
			engine.monsterIndex.menuMusic.stop();

		}

		engine.processing = false;
		//engine.gameMode = 66;
		//engine.processingInc = 0;
		//engine.backToGamePending = false;

		//Gdx.input.setInputProcessor(engine.inputMultiplexer);
		//Gdx.app.log("newG", "starting, health: " + engine.player.health);
		//Gdx.app.log("newG", "START!!!!!!!!!!!!!!!!!!!!!START");
		engine.gMap.completeFetch();
		//Gdx.app.log("newG", "DONE!!!!!!!!!!!!!!!DONE");
		if (!engine.thread.isAlive())engine.thread.start();
		if (!engine.timedThread.isAlive())engine.timedThread.start();
		
		engine.player.controllingMob.touchBelt(0);
		starter.setScreen(engine);

	}

	/*SSSize SS0 = new SSSize();
	SSizeChicken SSChicken = new SSizeChicken();

	public void initMiniGameData(int id) {
		switch (id) {
		case 0:
			SS0.set(1, 4, 70, 64);
			SS0.mob = engine.monsterIndex.enemyMobs[1];
			engine.gMap.customSpawnStrategy = SS0;
			break;

		case 1:
			SSChicken.set(1, 4, 70, 64);
			// engine.gMap.customSpawnStrategy = SSChicken;
			break;

		case 2:
			SS0.set(1, 4, 70, 64);
			SS0.mob = engine.monsterIndex.enemyMobs[0];
			// engine.gMap.customSpawnStrategy = SS0;
			break;
		}
		miniDeath = new ComponentDeathMini(engine);
	}

	Stats miniStats = new Stats(gameType);
	GameInfo miniInfo = new GameInfo();
	ComponentDeathMini miniDeath;

	*/

	public void initGame() {

	}

	

	public void deleteGame() {
		 {
			files[selectedSlot+selectedPage*6].deleteDirectory();
			populateValidGames();
		}
		populateValidGames();
	}

	public void initDeleteGameDialog() {
		// delete confirm
		hideMenuButtons();
		showDeleteConfirm();
	}

	private void showDeleteConfirm() {
		// TODO Auto-generated method stub
		
	}

	private void hideMenuButtons() {
		// TODO Auto-generated method stub
		
	}




	final float STAGEDURATION = .5f;

	/*public void hideBtn(ButtonForMenu... btns) {
		for (ButtonForMenu btn : btns) {
			// btn.x = btn.originX;
			// btn.y = btn.originY-200;
			btn.action(MoveTo.$(btn.startX, -50, STAGEDURATION)
					.setInterpolator(OvershootInterpolator.$(STAGEDURATION)));
		}
	}

	public void showBtn(ButtonForMenu... btns) {
		for (ButtonForMenu btn : btns) {

			btn.x = btn.startX;
			btn.y = btn.startY + 200;
			// Gdx.app.log("newG", "origin:"+btn.originX+","+btn.originY);
			btn.action(MoveTo.$(btn.startX, btn.startY, STAGEDURATION)
					.setInterpolator(OvershootInterpolator.$(STAGEDURATION)));
		}
	}

	public void showLoading() {
		showBtn(loadBtn);
	}

	public void hideMenuButtons() {
		hideBtn(adventureBtn, deleteBtn, creativeBtn, playBtn, creditsBtn);

	}

	public void showMenuButtons() {
		showBtn(playBtn, creditsBtn, adventureBtn);
		hideBtn(loadBtn, sizeSmBtn, sizeNrmBtn, sizeLrgBtn);
		hideBtn(deleteYesBtn, deleteNoBtn, deleteBtn, creativeBtn, newNameBtn, newConfBtn);
		Gdx.app.log(TAG, "show menu");
		for (int i = 0; i < 6; i++) {
			gameButtons[i].action(MoveTo.$(gameButtons[i].startX-320,
					gameButtons[i].startY, STAGEDURATION).setInterpolator(
					OvershootInterpolator.$(STAGEDURATION)));
			;
		}
		for (int i = 0; i < CLASS_TOTAL; i++){
			hideBtn(classSelect[i]);
		}
		textInput = false;
	}
	
	public void showWorldManagementButtons() {
		showBtn(deleteBtn, creativeBtn);
		hideBtn(loadBtn, sizeSmBtn, sizeNrmBtn, sizeLrgBtn);
		hideBtn(deleteYesBtn, deleteNoBtn, playBtn, creditsBtn, newNameBtn, newConfBtn);
		adventureBtn.action(MoveTo.$(adventureBtn.startX,
				205, STAGEDURATION).setInterpolator(
				OvershootInterpolator.$(STAGEDURATION)));
		
		for (int i = 0; i < 6; i++) {
			gameButtons[i].action(MoveTo.$(gameButtons[i].startX,
					gameButtons[i].startY, STAGEDURATION).setInterpolator(
					OvershootInterpolator.$(STAGEDURATION)));
			;
		}
		for (int i = 0; i < CLASS_TOTAL; i++){
			hideBtn(classSelect[i]);
		}
		populateValidGames();
		textInput = false;
	}

	

	

	public void showNewGameScreen() {
		hideBtn(deleteBtn, creativeBtn);
		hideBtn(loadBtn, adventureBtn);
		hideBtn(deleteYesBtn, deleteNoBtn, playBtn, creditsBtn);
		showBtn(newNameBtn, sizeSmBtn, sizeNrmBtn, sizeLrgBtn, newConfBtn);
		
		
		
		for (int i = 0; i < 6; i++) {
			gameButtons[i].action(MoveTo.$(gameButtons[i].startX-320,
					gameButtons[i].startY, STAGEDURATION).setInterpolator(
					OvershootInterpolator.$(STAGEDURATION)));
			;
		}
		
		for (int i = 0; i < CLASS_TOTAL; i++){
			showBtn(classSelect[i]);
		}
		//populateValidGames();
		Punk.gameName = engine.monsterIndex.nameGen.getWorldName();
		newNameBtn.setText(Punk.gameName);
		//textInput = true;
		//Gdx.input.getTextInput(nameListener, "New Game", "Input Name. Leave blank for random");
		
	}*/
	public class txtList implements TextInputListener{

		@Override
		public void input(String text) {
			Punk.gameName = text;
			
		}

		@Override
		public void canceled() {
			showMenuButtons();
			
		}

		private void showMenuButtons() {
			// TODO Auto-generated method stub
			
		}
		
	}
	/*private txtList nameListener = new txtList();
	public int selectedSize;
	public static boolean textInput = false;  
	
	public void showDeleteConfirm() {
		

		showBtn(deleteYesBtn, deleteNoBtn);
		hideBtn(adventureBtn, creativeBtn, playBtn, creditsBtn);
		deleteBtn.action(MoveTo.$(deleteNoBtn.startX, deleteNoBtn.startY + 50,
				STAGEDURATION).setInterpolator(
				OvershootInterpolator.$(STAGEDURATION)));

		// move save slot, hide other save slots
		for (int i = 0; i < 6; i++)
			if (i == selectedSlot) {
				gameButtons[i]
						.action(MoveTo.$(deleteYesBtn.startX,
								deleteYesBtn.startY + 50, STAGEDURATION)
								.setInterpolator(
										OvershootInterpolator.$(STAGEDURATION)));
			} else
				gameButtons[i].action(MoveTo.$(-100, gameButtons[i].startY,
						STAGEDURATION).setInterpolator(
						OvershootInterpolator.$(STAGEDURATION)));
		;

	}*/

	public void hideDeleteConfirm() {

	}

	public void initNewGame() {
		dialog = 5;
		loadCount = 0;
		int count = 0;
		boolean done = false;
		
		isNewGame = true;
		newQueued = true;
		queueTimer = 0;
		//hideMenuButtons();
		//showLoading();
	}

	private void showLoading() {
		// TODO Auto-generated method stub
		
	}

	public void initLoadGame() {
		dialog = 4;
		loadCount = 0;
		isNewGame = false;
		loadQueued = true;
		queueTimer = 0;
		// Gdx.app.log("newG", "INIT LOAD GAME");
		hideMenuButtons();
		showLoading();
	}

	@Override
	// Player(World world, PunkBodies monsterIndex, Vector2 spawnPos, int
	// gameid, int seed)
	public boolean touchDown(int x, int y, int ptr, int btn) {
	

		return false;
	}

	@Override
	public boolean touchDragged(int arg0, int arg1, int arg2) {
		return false;
	}

	

	@Override
	public boolean touchUp(int arg0, int arg1, int arg2, int arg3) {
		return false;
	}

	public void initCreative() {
		creativeQueued = true;
		queueTimer = 0;
		hideMenuButtons();
		showLoading();
	}

	public void resetValues() {
		queueTimer = -100000;
		creativeQueued = false;
		newQueued = false;
		loadQueued = false;
	}

	public void showCredits() {
		creditsOn = true;
		creditsTimer = 1;
		hideMenuButtons();
		//for (int i = 0; i < 6; i++)
			//gameButtons[i].hide();
	}

	@Override
	public boolean mouseMoved(int x, int y) {
		// TODO Auto-generated method stub
		return false;
	}
	
}

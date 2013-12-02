package com.niz.punk;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class ParticlePool {
	public static final int CRYSTALCOUNT = 16, BLOODCOUNT = 4, PLAYERBLOODCOUNT = 2, WANDFXCOUNT = 160;
	private static final String TAG = "particlePool";
	private ParticleEffect[] crystal = new ParticleEffect[CRYSTALCOUNT];
	private ParticleEffect[] enemyBlood = new ParticleEffect[BLOODCOUNT];
	private ParticleEffect[] playerBlood = new ParticleEffect[PLAYERBLOODCOUNT];
	//private ParticleEffect[] wandFX = new ParticleEffect[WANDFXCOUNT];
	private Sprite[] wandSprites =  new Sprite[10], cloudSprites = new Sprite[4];
	private boolean[] crystalOn = new boolean[CRYSTALCOUNT];
	private boolean[] enemyBloodOn = new boolean[BLOODCOUNT];
	private boolean[] playerBloodOn = new boolean[PLAYERBLOODCOUNT];
	private Array<ParticleEffect> wandList = new Array<ParticleEffect>(), fxList = new Array<ParticleEffect>();
	
	private ParticleEffect baseSlide, baseDoubleJump;
	private Pool<ParticleEffect> wandPool = new Pool<ParticleEffect>(){
		@Override
		protected ParticleEffect newObject() {
			// TODO Auto-generated method stub
			ParticleEffect fx = new ParticleEffect();
			fx.loadEmitters(Gdx.files.internal("data/wand0.txt"));
			return fx;
		}
	};
	private Array<ParticleEffect> mobFX = new Array<ParticleEffect>();
	
	
	
	
	private Pool<ParticleEffect> cloudPool = new Pool<ParticleEffect>(){
		@Override
		protected ParticleEffect newObject() {
			// TODO Auto-generated method stub
			ParticleEffect fx = new ParticleEffect();
			fx.loadEmitters(Gdx.files.internal("data/wand0.txt"));
			return fx;
		}
	}; 
	
	private Pool<ParticleEffect> fxPool = new Pool<ParticleEffect>(){
		@Override
		protected ParticleEffect newObject() {
			// TODO Auto-generated method stub
			ParticleEffect fx = new ParticleEffect();
			fx.getEmitters().add(new ParticleEmitter());
			//fx.loadEmitters(Gdx.files.internal("data/wand0.txt"));
			return fx;
		}
	};
	private Pool<PositionedParticle> positionedPool = new Pool<PositionedParticle>(){
		@Override
		protected PositionedParticle newObject() {
			// TODO Auto-generated method stub
			PositionedParticle fx = new PositionedParticle();
			//fx.getEmitters().add(new ParticleEmitter());
			//fx.loadEmitters(Gdx.files.internal("data/wand0.txt"));
			return fx;
		}
	};
	private ParticleEffect basePlayerBlood, baseBlood;
	private ParticleEffect[] baseCrystal = new ParticleEffect[4], baseLeaf = new ParticleEffect[4], baseMobParticle = new ParticleEffect[30];
	private ParticleEffect[] baseParticle = new ParticleEffect[14], baseCloud = new ParticleEffect[2];
	private ParticleEffect[] baseBlock = new ParticleEffect[256];
	private Sprite[] blockSprites = new Sprite[256];
	
	private PunkBodies mi;
	public ParticlePool(PunkBodies mi){
		this.mi = mi;
		wandList.ordered = true;
		baseCrystal[0] = new ParticleEffect();
		baseCrystal[1] = new ParticleEffect();
		baseCrystal[2] = new ParticleEffect();
		baseCrystal[3] = new ParticleEffect();
		baseBlood = new ParticleEffect();
		basePlayerBlood = new ParticleEffect();
		/*baseCrystal[0].loadEmitters(Gdx.files.internal("data/crystalsmash0.txt"));
		baseCrystal[1].loadEmitters(Gdx.files.internal("data/crystalsmash1.txt"));
		baseCrystal[2].loadEmitters(Gdx.files.internal("data/crystalsmash2.txt"));
		baseCrystal[3].loadEmitters(Gdx.files.internal("data/crystalsmash3.txt"));
		baseCrystal[0].loadEmitterImages(mi.atlas);
		baseCrystal[1].loadEmitterImages(mi.atlas);
		baseCrystal[2].loadEmitterImages(mi.atlas);
		baseCrystal[3].loadEmitterImages(mi.atlas);*/
		
		
		
		/*for (int i = 0; i < 5; i++){
			ParticleEffect p = new ParticleEffect();
			p.loadEmitters(Gdx.files.internal("data/particle"+i+"txt"));
			baseParticle[i] = p;
		}*/
		for (int c = 0; c < 10; c++){
			baseBlock[c] = new ParticleEffect();
			String path = "data/blockparticle"+c+".txt";
			if (Gdx.files.internal(path).exists())
			baseBlock[c].loadEmitters(Gdx.files.internal(path));
		}
		for (int c = 0; c < 256;c++)
				blockSprites[c] = mi.destructionParticles[c];
		
		
		for (int c = 0; c < 10;c++)
		if (Gdx.files.internal("data/particle"+c+".txt").exists()){
			baseParticle[c] = new ParticleEffect();
			baseParticle[c].loadEmitters(Gdx.files.internal("data/particle"+c+".txt"));
			wandSprites[c] = mi.atlas.createSprite("particle"+ c+"v");
			
			if (wandSprites[c] == null)Gdx.app.log("particles", "null sprite"+c);
			if (mi.atlas.findRegion("particle1v") == null)Gdx.app.log("particles", "null sprite11111111111");
		} 

		baseCloud[0] = new ParticleEffect();
		baseCloud[0].loadEmitters(Gdx.files.internal("data/cloud.txt"));
		//
		cloudSprites[0] = mi.atlas.createSprite("particlecloud");
		baseCloud[0].getEmitters().get(0).setSprite(cloudSprites[0]);
		//baseWand[0].getEmitters().get(0).setContinuous(true);
		/*baseCrystal[1].getEmitters().get(0).setSprite(mi.atlas.createSprite("particle0v"));;
		baseCrystal[1].getEmitters().get(0).setSprite(mi.atlas.createSprite("particle1v"));;
		baseCrystal[2].getEmitters().get(0).setSprite(mi.atlas.createSprite("particle2v"));
		baseCrystal[3].getEmitters().get(0).setSprite(mi.atlas.createSprite("particle3v"));*/
		baseBlood.loadEmitters(Gdx.files.internal("data/blood.txt"));
		baseBlood.loadEmitterImages(mi.atlas);
		//basePlayerBlood.loadEmitters(Gdx.files.internal("data/playerblood.txt"));
		//basePlayerBlood.loadEmitterImages(mi.atlas);
		
		for (int i = 0; i < BLOODCOUNT; i++){
			enemyBlood[i] = new ParticleEffect(baseBlood);
			enemyBloodOn[i] = false;
		}
		for (int i = 0; i < PLAYERBLOODCOUNT; i++){
			playerBlood[i] = new ParticleEffect(basePlayerBlood);
			playerBloodOn[i] = false;
		}
		for (int i = 0; i < CRYSTALCOUNT; i++){
			crystal[i] = new ParticleEffect(baseCrystal[i/4]);
			crystalOn[i] = false;
		}
		
		baseLeaf[0] = new ParticleEffect();
		baseLeaf[1] = new ParticleEffect();
		baseLeaf[2] = new ParticleEffect();
		baseLeaf[3] = new ParticleEffect();
		
		baseLeaf[0].loadEmitters(Gdx.files.internal("data/leaffall0.txt"));
		baseLeaf[1].loadEmitters(Gdx.files.internal("data/leaffall0.txt"));
		baseLeaf[2].loadEmitters(Gdx.files.internal("data/leaffall0.txt"));
		baseLeaf[3].loadEmitters(Gdx.files.internal("data/leaffall0.txt"));
		baseLeaf[0].loadEmitterImages(mi.atlas);
		baseLeaf[1].getEmitters().get(0).setSprite(mi.atlas.createSprite("particle10v"));
		baseLeaf[2].getEmitters().get(0).setSprite(mi.atlas.createSprite("particle11v"));

		baseLeaf[3].getEmitters().get(0).setSprite(mi.atlas.createSprite("particle12v"));

		//baseLeaf[1].loadEmitterImages(mi.atlas);
		//baseLeaf[2].loadEmitterImages(mi.atlas);
		//baseLeaf[3].loadEmitterImages(mi.atlas);
		baseSlide = new ParticleEffect();
		baseSlide.loadEmitters(Gdx.files.internal("data/slide.txt"));
		baseSlide.loadEmitterImages(mi.atlas);
		
		baseDoubleJump = new ParticleEffect();
		baseDoubleJump.loadEmitters(Gdx.files.internal("data/doublejump.txt"));
		baseDoubleJump.loadEmitterImages(mi.atlas);
	}
	
	public void crystal(float x, float y, int type){
		for (int i = 0; i < CRYSTALCOUNT; i++)
			if (!crystalOn[i+type*4]){
				crystalOn[i+type*4] = true;
				crystal[i+type*4].setPosition(x,y);
				//crystal[i+type*4].getEmitters().get(0).reset();
				Gdx.app.log("partpool", "size "+crystal[i+type*4].getEmitters().size);
				crystal[i+type*4].start();
				return;
			}
	}
	public void enemyBlood(float x, float y){
		for (int i = 0; i < BLOODCOUNT; i++)
			if (!enemyBloodOn[i]){
				enemyBloodOn[i] = true;
				enemyBlood[i].setPosition(x+.5f,y+.5f);
				enemyBlood[i].getEmitters().get(0);
				enemyBlood[i].start();
				return;
			}
	}
	public void blood(float x, float y){
		/*for (int i = 0; i < PLAYERBLOODCOUNT; i++)
			if (!playerBloodOn[i]){
				playerBloodOn[i] = true;
				playerBlood[i].setPosition(x,y);
				playerBlood[i].getEmitters().get(0);
				playerBlood[i].start();
				return;
			}*/
		enemyBlood(x,y);
				
	}
	public void update(float deltaTime){
		for (int i = 0; i < BLOODCOUNT; i++){
			if (enemyBloodOn[i]){
				enemyBlood[i].update( deltaTime); 
				if (enemyBlood[i].isComplete()) enemyBloodOn[i] = false;
			}
		}
		for (int i = 0; i < PLAYERBLOODCOUNT; i++){
			if (playerBloodOn[i]){
				playerBlood[i] .update( deltaTime);
				if (playerBlood[i].isComplete()) playerBloodOn[i] = false;
			}
		}
		for (int i = 0; i < CRYSTALCOUNT; i++){
			if (crystalOn[i]){
				crystal[i].update( deltaTime);
				if (crystal[i].isComplete()) crystalOn[i] = false;
			}
		}
		fxIter = wandList.iterator();
		while (fxIter.hasNext()){
			ParticleEffect f = fxIter.next();
			f.update( deltaTime);
			if (f.isComplete()){
				fxIter.remove();
				//wandList.removeValue(f, true);
				wandPool.free(f);
			}
		}
		
		fxIter = fxList.iterator();
		while (fxIter.hasNext()){
			ParticleEffect f = fxIter.next();
			//f.update(deltaTime);
			if (f.isComplete()){
				fxIter.remove();
				//wandList.removeValue(f, true);
				fxPool.free(f);
			}
		}
	}
	Iterator<ParticleEffect> fxIter;
	public void draw(SpriteBatch batch, float deltaTime){
		//Gdx.app.log(TAG, "particles");
		for (int i = 0; i < BLOODCOUNT; i++){
			if (enemyBloodOn[i]) enemyBlood[i].draw(batch, deltaTime); 
		}
		for (int i = 0; i < PLAYERBLOODCOUNT; i++){
			if (playerBloodOn[i])playerBlood[i] .draw(batch, deltaTime);
		}
		for (int i = 0; i < CRYSTALCOUNT; i++){
			if (crystalOn[i])crystal[i].draw(batch, deltaTime);
		}
		fxIter = fxList.iterator();
		while (fxIter.hasNext()){
			ParticleEffect f = fxIter.next();
			f.draw(batch, deltaTime);
		}
	}
	public ParticleEffect wallSlide(float x, float y){
		ParticleEffect fx = fxPool.obtain();
		
		fx.getEmitters().get(0).set(baseSlide.getEmitters().get(0));
		wandList.add(fx);
		fx.setPosition(x,y);
		fx.getEmitters().get(0).reset();
		fx.getEmitters().get(0).update(1);
		fx.update(2f);
		fx.start();
		
		return fx;
	}
	public void blockDestruction(float x, float y, int id){//type is blockID of leaves
		if (fxList.size > 64 || Punk.processing) {
			//Gdx.app.log("particles", "not making fx");
			return;
		}
		ParticleEffect fx = fxPool.obtain();
		BlockDef info = ChunkPool.sBlockDefs[id];
		fx.getEmitters().get(0).set(baseBlock[info.particleType].getEmitters().get(0));
		fx.getEmitters().get(0).setSprite(blockSprites[id]);
		fxList.add(fx);
		fx.setPosition(x+.5f,y+.5f);
		fx.start();
		
		//30 31 32 28 crystals
	}
	
	public ParticleEffect grenadeParticle(float x, float y, int type){
		ParticleEffect fx = wandPool.obtain();
		fx.getEmitters().get(0).setSprite(wandSprites[type]);
		wandList.add(fx);
		fx.setPosition(x,y);
		fx.start();
		return fx;
	}
	
	public void wandFX(float x, float y, int type){
		if (wandList.size > 32) {
			Gdx.app.log("particles", "not making wand fx");
			return;
		}
		ParticleEffect fx = wandPool.obtain();
		fx.getEmitters().get(0).setSprite(wandSprites[type]);
		wandList.add(fx);
		fx.setPosition(x,y);
		fx.start();
		
	}

	public void doubleJump(float x, float y) {
		ParticleEffect fx = fxPool.obtain();
		fx.getEmitters().get(0).set(baseDoubleJump.getEmitters().get(0));
		fxList.add(fx);
		fx.setPosition(x+.5f,y+.5f);
		fx.start();
		
	}
	public ParticleEffect cloud(float x, float y, int type){
		ParticleEffect fx = cloudPool.obtain();
		fx.getEmitters().get(0).set(baseCloud[type].getEmitters().get(0));
		//cloudList.add(fx);
		fx.getEmitters().get(0).setSprite(cloudSprites[type]);
		fx.setPosition(x+.5f,y+.5f);
		fx.start();
		fx.update(MathUtils.random(2f,20f));
		return fx;
	}
	public void freeCloud(ParticleEffect e){
		cloudPool.free(e);
	}

	public void addMob(Array<PositionedParticle> particles, int i) {
		particles.add(getMobFX(i));		
	}

	private PositionedParticle getMobFX(int index) {
		int i = index%16;
		ParticleEffect fx = fxPool.obtain();
		fx.getEmitters().get(0).set(baseParticle[i].getEmitters().get(0));
		fx.getEmitters().get(0).setSprite(wandSprites[i]);
		PositionedParticle pfx = positionedPool.obtain();
		pfx.type = index;
		pfx.e = fx;
		return pfx;
	}

	public void removeMobFX(Array<PositionedParticle> particles, int index) {
		for (int i = 0,n=particles.size; i<n; i++){
			if (particles.get(i).type == index){
				PositionedParticle pp = particles.removeIndex(i);
				pp.e.getEmitters().get(0).setContinuous(false);
				fxList.add(pp.e);
				positionedPool.free(pp);
				return;
			}
		}
		
	}
	
}

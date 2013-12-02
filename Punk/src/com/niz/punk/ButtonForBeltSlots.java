package com.niz.punk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;


public class ButtonForBeltSlots extends Actor{
	private Punk main;
	private PunkBodies mi;
	public float targetX = 10, targetY = 10;
	public int id;
	public float picWidth, picHeight, picOffset;
	public boolean hitsDisabled = false;
	public static Vector2 tmpV ;
	
	public ButtonForBeltSlots(Punk main, int id){
		super();
		tmpV = main.tmp;
		this.id = id;
		this.main = main;
		mi = main.monsterIndex;
		
	}
	
	public void draw(SpriteBatch batch, float parentAlpha) {
		Sprite s;
		Gdx.app.log("belt", "draw!"+getX()+","+getY()+"   w"+getWidth()+"  h"+getHeight());
		/*if (id == 6) {
			s= mi.beltHideButton;
			
			s.setSize(picWidth, picHeight);
			s.setPosition(0, getY()-2);
			s.setOrigin(picWidth/2,picHeight/2);
			if (main.beltHidden)s.setScale(1,1); else s.setScale(1,-1); 
			s.draw(batch);
		}
		else{
			if (id == main.player.activeQuickSlot)mi.beltSelected9.draw(batch, getX()+picOffset-2, getY()-4, picWidth+4, picHeight+4, getColor());
			
			s = mi.getItemFrame(main.player.inventory.getItemID(id), main.player.inventory.getItemMeta(id));
			batch.draw(s, getX()+picOffset, getY()-2, picWidth, picHeight);
			//s.setSize(picWidth, picHeight);
			
		}
		int itemID = main.player.inventory.getItemID(id), itemMeta = main.player.inventory.getItemMeta(id);
		if (id != 6){
			if (PunkInventory.hasDurability(itemID, itemMeta)){//durability meter
				int newWidth;
				newWidth = (int) ((  main.player.inventory.getItemMeta(id)*32)/PunkInventory.getMaxDurability(itemID, itemMeta));
				//Gdx.app.log("beltbtn", "new width"+newWidth);
				batch.draw(mi.durability, getX()+picOffset, getY(), newWidth, getHeight()/8);
			} else{
				main.font.draw(batch, Punk.numberStrings[Math.max(0, main.player.inventory.getItemAmount(id))], 
						getX()+8+picOffset, 
						getY()+10);
			}
		}
		*/
		if (id == main.player.activeQuickSlot);
		//Player.zoomLevel = .14f;
		
	}
	
	
	public boolean touchDown(float x, float y, int pointer) {
		return false;
	}
	
	
	public void touchUp(float x, float y, int pointer) {
		// TODO Auto-generated method stub
		
	}
	
	
	public void touchDragged(float x, float y, int pointer) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	}

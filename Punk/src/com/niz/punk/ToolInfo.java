package com.niz.punk;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.niz.punk.Punk.RayType;
import com.niz.punk.PunkMap.BlockDamageType;

public class ToolInfo extends TouchAction{
	
	public float speed;//, speedVMobs;//these are multipliers. blockHP is also used for speed
	
	
	//public boolean hasChain, isWeapon;
	
	public DamageType damageType;
	public int durability = 500;

	public BlockDamageType blockDamageType = BlockDamageType.PICKAXE;

	public int damage;
	
	public ToolInfo(PunkMap map, PunkBodies mi, World world, int damage, int durability, float speed){
		super(map, mi, world);
		
		this.speed = speed;
		this.damage = damage;
		this.durability = durability;
		//this.speedVMobs = speedVMobs;
		//this.hasChain = hasChain;
		//this.isWeapon = isWeapon;
		//this.damageVMobs = damageVMobs;
		//angle = -70;
	}
	
	/*public ToolInfo(int speed, int damage, DamageType d, boolean chain) {
		// for weapons
		damageType = d;
		//speedVMobs = speed;
		damageVMobs = damage;
		//isWeapon = true;
		//s = itemS;
		//hasChain = chain;
	}*/

	public void writeToFile(FileHandle file){
		try{
			DataOutputStream out = new DataOutputStream(new BufferedOutputStream(file.write(false)));
			//out.writeInt(gameID);
			
			out.close();
			//Gdx.app.log("gameinfo", "done writing  s "+spawnPosition.y);
		} catch (IOException ex){
			Gdx.app.log("toolinfo", "error writing");
		}
	}
	public void readFromFile(FileHandle file){
		if (file.exists())
			try{
				
				DataInputStream in = new DataInputStream(new BufferedInputStream(file.read()));
				//gameID=in.readInt();
				
				in.close();
			} catch (IOException ex){
				Gdx.app.log("toolinfo", "error reading");
	
			}
		else {Gdx.app.log("gameinfo", "doesn't exist!!!!!!!!!!!!!!!!!!!!!!!");}//new GameInfo().writeToFile(file);}
			
		
	}
	public void set(ToolInfo in){
		this.speed = in.speed;
		//this.speedVMobs = in.speedVMobs;
		//this.hasChain = in.hasChain;
		//this.isWeapon = in.isWeapon;
		//this.damageVMobs = in.damageVMobs;
	}
	
	@Override
	public void touchUp(GenericMob mob) {
		if (mob.state == 41 && mob.targetB.y != -1000000){
			mob.clipAnimTimer();
			mob.state = 42;
			mob.lastState = 42;
		
		}
	}
	private static  Vector2 tmpV = new Vector2();

	@Override
	public boolean touchDown(GenericMob mob) {
		if (mob.state == 0){
			mob.state = 41;
			mob.stateTime = 0f;
			angle(mob);
			return true;
		}
		if (mob.state == 41){
			if (mob.targetV.y != -1000000){
				Punk.queueBlockHighlight(mob.targetB);
				Punk.queueLine(mob.position.tmp().add(0,Player.EYEHEIGHT), mob.targetV);
			}
			//Punk.drawAngleHighlight(true);
			angle(mob);

		}
		return false;
		
	}

	private void angle(GenericMob mob) {
		mob.faceAngle(true);
		mob.angle = mob.touchLoc.angle()+180;
		mob.targetV.set(map.rayCastForDig(tmpV.set(mob.position).add(0,Player.EYEHEIGHT), (mob.angle+180)%360, RayType.SOLID, 93, 91));
		mob.targetB.set(mob.targetV);
		
	}

	@Override
	public void cancelTouch(GenericMob mob) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beltSelected(GenericMob mob) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onBeltUnselect(GenericMob mob) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stats(GenericMob mob) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getFinalDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unPress(PunkMap map, GenericMob mob, PunkBodies mi,
			boolean twoPresses) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pressed(GenericMob mob, PunkMap gMap, World world,
			PunkBodies mi, boolean pressed, float deltaTime) {
		// TODO Auto-generated method stub
		
	}
}

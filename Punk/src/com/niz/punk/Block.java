package com.niz.punk;

import com.badlogic.gdx.utils.GdxRuntimeException;

public class Block{
	public int blockID = 0;
	public int meta=0;
	
	//public byte blockType;
	private byte combinedLight = 0;//light then daylight
	//private byte lights;
	private int lightBits, dayBits;

	//public CorneredSprite sprite;
	public Block(int id, int met)
	{
		blockID = id;
		meta = met;
		//blockType = getBlockType(blockID);
	}
	public boolean isFlammable(){
		return PunkMap.getFlammable(blockID);
	}
	public int getBlockHP(){
		return PunkMap.getBlockDef(blockID).hp;
	}
	
	public BlockDef def(){
		return PunkMap.getBlockDef(blockID);
	}
	public String toString(){
		return " b"+blockID+":"+meta+" "+"l:"+getLight() + " d:"+getDayLight();
	}
	
	public boolean damageBlock(int damage){
		return true;
	}
	@Deprecated
	public byte blockType(){
		return PunkMap.getBlockType(blockID);
	}
	public byte getBlockType(int id){
			//if (id > 64 ) return 64;
			switch (blockID)
			{
			
			case 0 : return 0;
			
			case 81://dungeon wall
				return 0;
			case 82://dungeon floor
				return 64;
			case 83:return 69;
			case 73:case 74:case 75:case 76:case 77:case 78:case 79:
				return 0;//door
			case 30:
			case 31:
			case 32:
			
			case 22:
			case 23:
			case 24:
			//spawners
			case 45:
			 
				
			case 56:
			case 57:
			case 58:
			case 61:
			case 62:
				return 64;
			case 41:return 9;//bed
			case 20://GRAVEL
			case 21://SAND
			case 29:
				return 66;
			case 33:
			case 34:
			case 10:
			case 11:
				
			
				
				return 65;
			case 9:
			case -4:
			case -5:
			case -6:
			case -7:
			case 25://ores
			case 26:
			case 27:
			case 28:
				return 67;
			
			 
			case 19:
			case 40:
			
			case 36:
			
			case 13:
			case 14:
			case 38:
				
			case 4://roots
			case 5:
			case 6:
			case 53:
				
				return 2;
			case 15:
			case 16:
			case 17: 	
			case 54:
				return 2;
			case 100: return 100;
			
			case 18: 
			
			case 46:
			case 47:
			case 48:
			case 49:
			//case 29://snow
				
			
			
			case 55:
				
			
				return 2;
			
			case 50:
			case 70:
			
			case 88://mushroom
			case 89:
				return 6;
			
			case -2:
			case 42:
			case 51://player torch light
			 return 0;
			case 12:
			case -3:
				return 1;
			case 8:
				return 3;
			case 60:
			case 7 :
				return 4;
			case 59:
				return 5;
			case 86:
				return 7;
			case 87:
				return 8;
			case 91://portal
			default : return 0;
			case 71:return 64;
			}
			
			
		}
	
	
	public void set(int id, int met)
	{
		blockID = id;
		meta = met;
		//blockType = getBlockType(id);
		//sprite = PunkBodies.getBlockSprites(id, meta);
		//if (id == -2) light = 15;
		//if (id == -1)throw new GdxRuntimeException(" water");
		assert (meta != -7);
		//if (meta == -7) throw new GdxRuntimeException("meta wrong");
	}
	
	
	public void set(Block ablock){
		set( ablock.blockID,ablock.meta);
		//blockType = ablock.blockType;
	}
	public void setMeta(int met)
	{
		
		set(blockID, met);
		//meta = (byte)met;
	}
	
	public int getID()
	{
		return blockID;
	}
	public int getMeta()
	{
		return meta;
	}
	public int effectiveLight() {
		return Math.max(getDayLight(), getLight());
	}
	
	private static int[] corner = new int[4];
	
	
	
	//public boolean setLightBits(byte[][] m, byte[][] dm) {
//		return false;// setLightBitsNew(m, dm);
		/*corner[0] = (m[0][0] + m[0][1] + m[1][0] + m[1][1]) /4;
		corner[3] = (m[1][1] + m[2][1] + m[1][0] + m[2][0]) /4;
		corner[2] = (m[1][1] + m[2][1] + m[1][2] + m[2][2]) /4;
		corner[1] = (m[1][1] + m[0][1] + m[1][2] + m[0][2]) /4;
		
		int oldLB = lightBits;
		lightBits = corner[0] + (corner[1]<<4) + (corner[2] << 8) + (corner[3] << 12);
		
		corner[0] = (dm[0][0] + dm[0][1] + dm[1][0] + dm[1][1]) /4;
		corner[3] = (dm[1][1] + dm[2][1] + dm[1][0] + dm[2][0]) /4;
		corner[2] = (dm[1][1] + dm[2][1] + dm[1][2] + dm[2][2]) /4;
		corner[1] = (dm[1][1] + dm[0][1] + dm[1][2] + dm[0][2]) /4;
		
		int oldDB = dayBits;
		dayBits = corner[0] + (corner[1]<<4) + (corner[2] << 8) + (corner[3] << 12);
		//Gdx.app.log("block", "set corners"+lightBits + " day "+dayBits + " corners "+ corner[0] + " " + corner[1] + " " + corner[2] + " " + corner[3]);
		return (oldLB != lightBits || oldDB != dayBits);*/
		//return false;
	//}
	public boolean setLightBitsNew(byte[][] m, byte[][] dm) {
		corner[0] = (m[0][0] + m[0][1] + m[1][0] + m[1][1]) /4;
		corner[3] = (m[1][1] + m[2][1] + m[1][0] + m[2][0]) /4;
		corner[2] = (m[1][1] + m[2][1] + m[1][2] + m[2][2]) /4;
		corner[1] = (m[1][1] + m[0][1] + m[1][2] + m[0][2]) /4;
		
		int oldLB = getLightBits();
		setLightBits(corner[0] + (corner[1]<<4) + (corner[2] << 8) + (corner[3] << 12));
		//Gdx.app.log("block", "set corners"+lightBits + " day "+dayBits + " corners "+ corner[0] + " " + corner[1] + " " + corner[2] + " " + corner[3]);
		
		corner[0] = (dm[0][0] + dm[0][1] + dm[1][0] + dm[1][1]) /4;
		corner[3] = (dm[1][1] + dm[2][1] + dm[1][0] + dm[2][0]) /4;
		corner[2] = (dm[1][1] + dm[2][1] + dm[1][2] + dm[2][2]) /4;
		corner[1] = (dm[1][1] + dm[0][1] + dm[1][2] + dm[0][2]) /4;
		
		int oldDB = getDayBits();
		setDayBits(corner[0] + (corner[1]<<4) + (corner[2] << 8) + (corner[3] << 12));
		//Gdx.app.log("block", "set corners"+lightBits + " day "+dayBits + " corners "+ corner[0] + " " + corner[1] + " " + corner[2] + " " + corner[3]);
		//Gdx.app.log("block", "old"+oldDB+" newd"+getDayBits()+" old"+oldLB+" newlb"+getLightBits());
		return (oldLB != getLightBits() || oldDB != getDayBits());
	}
	public void resetDayBits() {
		setDayBits(0 + (0<<4) + (0 << 8) + (0 << 12));
		setLightBits(0 + (0<<4) + (0 << 8) + (0 << 12));
		setLight(0);
		setDayLight(0);
	}
	public void removeLight() {
		setLight(0);
		setDayLight(0);
		setLightBits(0);
		setDayBits(0);
		
	}
	public void setFullSunlight() {
		setDayBits(15 + (15<<4) + (15 << 8) + (15 << 12));
		setLightBits(0 + (0<<4) + (0 << 8) + (0 << 12));
		setLight(0);
		setDayLight(15);
		
	}
	public void setDayLight(int l) {
		if (l < 0) l = 0;

		byte t = (byte)l;
		//dayBits = t + (t<<4) + (t << 8) + (t << 12);
		//lightBits = 0 + (0<<4) + (0 << 8) + (0 << 12);
		//setLight(0);
		//dayLight = t;combine#
		combinedLight = (byte)((combinedLight & 0x0f) + (t << 4));
		
	}
	public byte getLight() {
		return (byte) (combinedLight & 15);
	}
	public void setLight(int i) {
		//this.light = (byte) i;
		if (i < 0) i = 0;

		combinedLight = (byte) ((combinedLight & 0xF0)+(i & 0x0F));
	}
	public byte getDayLight() {
		return (byte) ((combinedLight >> 4) & 0x0f);
	}
	public void setDayLightAtCreation(byte l) {
		byte t = (byte)l;
		setDayBits((int)(t + (t<<4) + (t << 8) + (t << 12)));
		setLightBits((int)(0 + (0<<4) + (0 << 8) + (0 << 12)));
		setLight(0);
		setDayLight(l);
		
	}
	/*public int getLightBits() {
		byte li = getLight();
		return  li+li<<2+li<<4+li<<6;
	}
	public void setLightBits(int lightBits) {
		
	}
	public int getDayBits() {
		byte li = getDayLight();
		return  li+(li<<4)+(li<<8)+(li<<12);
	}
	public void setDayBits(int dayBits) {
		
	}*/
	
	
	
	public int getLightBits() {
		return lightBits;
	}
	public void setLightBits(int lightBits) {
		this.lightBits = lightBits;
	}
	public int getDayBits() {
		return dayBits;
	}
	public void setDayBits(int dayBits) {
		this.dayBits = dayBits;
	}
	public boolean setLightBits(byte[][] m, byte[][] dm) {
		return setLightBitsNew(m, dm);
	}
	public boolean isAutomata() {
		return PunkMap.getBlockDef(blockID).isAutomata;
	}
	
	
	
	
}

package com.niz.punk;

import static com.badlogic.gdx.graphics.g2d.SpriteBatch.C1;
import static com.badlogic.gdx.graphics.g2d.SpriteBatch.C2;
import static com.badlogic.gdx.graphics.g2d.SpriteBatch.C3;
import static com.badlogic.gdx.graphics.g2d.SpriteBatch.C4;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.MathUtils;

public class CorneredSprite extends Sprite {
	static final int COLOR_TOTAL = 80;
public int[] cornerBits = new int[4];//start at bottom left
public float xOff, yOff, sizeY, sizeX;
private static float[] cachedColors = new float[16], cachedDayColors = new float[16], bgs = new float[16];

private static float[][][][] colorIndex;
public static void makeLookup(){
	for (int i = 0; i < 16; i++){
		//for (int j = 0; j < 16; j++){
			cachedColors[i] = Color.toFloatBits(i*16, i*16,i*16, 255);
			cachedDayColors[i] = Color.toFloatBits(i*16, i*4,i*2, 255);
			
		}
	initFades();
}

//public float[] fadeDay = new float[16], fadeNight = new float[16], fadeTorch = new float[16];
public static float[][][][] lightFades = new float[COLOR_TOTAL][16][16][16];

public static int t, l = 0;
public static final float pixelSize = 1f/16f;
public CorneredSprite(AtlasRegion reg){
	this(reg, 0, 0);
}
public CorneredSprite(AtlasRegion reg, float xx, float yy){
	super(reg);
	
	sizeY = reg.packedHeight*pixelSize;
	sizeX = reg.packedWidth*pixelSize;
	xOff = reg.offsetX*pixelSize-xx;
	yOff = yy-reg.offsetY*pixelSize-sizeY;
	setSize(sizeX, sizeY);
	//setOrigin(xOff/2f, yOff/2f);
	setOrigin(0,0);
	//Gdx.app.log("cornsor£", ""+xOff);
}

public CorneredSprite(Texture tex, float sizeX, float sizeY, float xOff, float yOff, float u, float v, float u2, float v2){
	super(tex);
	this.setU(u);
	setV(v);
	setU2(u2);
	setV2(v2);
}

public CorneredSprite(CorneredSprite s) {
	super(s);
	sizeX = s.sizeX;
	sizeY = s.sizeY;
	xOff = s.xOff;
	yOff = s.yOff;
	setSize(sizeX, sizeY);
	setOrigin(0,0);
}

static Color tmpCD = new Color(), tmpCT = new Color(), tmpCN = new Color(), tmpC = new Color(), tmpCC = new Color();
static Color[] selectableColors = new Color[COLOR_TOTAL];
private static void initFades() {
	// TODO Auto-generated method stub
	Pixmap fades = new Pixmap(Gdx.files.internal("data/fade.png"));
	/*for (int i = 0; i < 16; i++){
		Color.rgba8888ToColor(tmpC, fades.getPixel(i, 2));
		fadeTorch[i] = tmpC.toFloatBits();
		Color.rgba8888ToColor(tmpC, fades.getPixel(i, 1));
		fadeNight[i] = tmpC.toFloatBits();
		Color.rgba8888ToColor(tmpC, fades.getPixel(i, 0));
		fadeDay[i] = tmpC.toFloatBits();
	}*/
	
	for (int d = 0; d < 16; d++)
		for (int f = 0; f < 16; f++)//fire
			for (int t = 0; t < COLOR_TOTAL; t++)//time, for torch flicker
				for (int l = 0; l < 16; l++){//light, for day/night fade
					int flickeredTorch =f;
					if (f < 8) flickeredTorch -= MathUtils.random(1);
					
					Color.rgba8888ToColor(tmpCT, fades.getPixel(f, 0));//torch
					
					Color.rgba8888ToColor(tmpCD, fades.getPixel(d, 2));//Day
					
					Color.rgba8888ToColor(tmpCC, fades.getPixel(t % 16, 3+(t/16)));
					if (d == 0 && f == 0 && l == 0){
						selectableColors[t] = new Color(tmpCC);
						PunkBodies.colorPixels[t].setColor(CorneredSprite.selectableColors[t]);
					}
					//tmpCC.lerp(tmpCD, .5f);
					Color.rgba8888ToColor(tmpCN, fades.getPixel(d, 1));//Night
					
					float alpha = Math.min(l/15f, 1);
					tmpC.set(tmpCD);
					tmpC.lerp(tmpCN, alpha);//tmpC is now sunlight level
					tmpC.add(tmpCT);
					
					//colors for clothes, skin etc
					
					//t
					
					
					
					//tmpC.lerp(tmpCC,  .5f);
					
				
					//Gdx.app.log("spritecornered", "megacolorarray");
					lightFades[t][l][f][d] = blend(tmpC, tmpCC);
		}
	for (int d = 0; d < 16; d++){
		Color.rgba8888ToColor(tmpCD, fades.getPixel(d, 3));
		bgs[d] = tmpCD.toFloatBits();
	}
	specialColors[1] = getFade(fades, 4, 0, 16);//new float[32];
	specialColors[2] = getFade(fades, 4, 4, 8);
	specialColors[3] = getFade(fades, 32, 6, 64);
	specialColors[4] = getFade(fades, 8, 8, 16);
	specialColors[5] = getFade(fades, 8, 0, 64);
	specialColors[6] = getFade(fades, 8, 0, 64);
	
}

private static float[] getFade(Pixmap fades, int scale, int index, int size) {
	float[] fade = new float[size];
	int px = index % 16, py = index/16+8;
	
	for (int i = 0; i < size/scale; i++){
		float f = 0;
		Color.rgba8888ToColor(tmpCD, fades.getPixel(px+i, py));
		if (i != size/scale-1)Color.rgba8888ToColor(tmpCT, fades.getPixel(px+i+1, py));
		else Color.rgba8888ToColor(tmpCT, fades.getPixel(px+i-(size/scale)+1, py));
		for (int g = 0; g < scale; g++){
			float alpha = (g+1) / (float)scale;
			tmpC.set(tmpCD).lerp(tmpCT, alpha);
			fade[i*scale+g] = tmpC.toFloatBits();;
		}
		
	}
	return fade;
}

private static float[][] specialColors = new float[20][];
private static float blend(Color c1, Color c2) {
	float r = (c1.r * c2.r)/1f
	,g = (c1.g * c2.g)/1f
	,b = (c1.b * c2.b)/1f;
	return Color.toFloatBits(r, g, b, 1f);
}

private static float blendsmall(Color Color1, Color Color2) {
	float r = (float) (1f - Math.sqrt(((1f-Color1.r)*(1f-Color1.r) + (1f-Color2.r)*(1f-Color2.r))/2));
	float g = (float) (1f - Math.sqrt(((1f-Color1.g)*(1f-Color1.g) + (1f-Color2.g)*(1f-Color2.g))/2));;
	float b = (float) (1f - Math.sqrt(((1f-Color1.b)*(1f-Color1.g) + (1f-Color2.b)*(1f-Color2.b))/2));
	return Color.toFloatBits(r, g, b, 1f);
}

private static float blendAdd(Color tmpC, Color tmpCC) {
	float R = tmpC.r, G = tmpC.g, B = tmpC.b, A = tmpC.a, r = tmpCC.r, g = tmpCC.g, b = tmpCC.b, a = tmpCC.a;
	float ax = 1 - (1 - a) * (1 - A);
	float rx = r * a / ax + R * A * (1 - a) / ax;
	float gx = g * a / ax + G * A * (1 - a) / ax;
	float bx = b * a / ax + B * A * (1 - a) / ax;
	return Color.toFloatBits(rx, gx, bx, ax);
}

public void setCorners(int bits){
	//bits &= (-1-2-4-8-16-32-64-128);
	//Gdx.app.log("spritecornered", "set corners: "+bits);
	float[] v = getVertices();
	v[C1] = cachedColors[bits & 0x0f];
    v[C2] = cachedColors[bits >>> 4 & 0x0f];
    v[C3] = cachedColors[bits >>> 8 & 0x0f];
    v[C4] = cachedColors[bits >>> 12 & 0x0f];
}



public CorneredSprite setCorners(int bits, int daybits){
	//setCorners(daybits);
	float[] v = getVertices();
	v[C1] = lightFades[t][l][bits & 0x0f][ daybits & 0x0f];
    v[C2] = lightFades[t][l][bits >>> 4 & 0x0f][ daybits >>> 4 & 0x0f];
    v[C3] = lightFades[t][l][bits >>> 8 & 0x0f][ daybits >>> 8 & 0x0f];
    v[C4] = lightFades[t][l][bits >>> 12 & 0x0f][ daybits >>> 12 & 0x0f];
    return this;
}





public void setBackgroundCorners(int bits, int daybits) {
	float[] v = getVertices();
	v[C1] = bgs[Math.max(bits & 0x0f, daybits & 0x0f)];
    v[C2] = bgs[Math.max(bits >>> 4 & 0x0f, daybits >>> 4 & 0x0f)];
    v[C3] = bgs[Math.max(bits >>> 8 & 0x0f, daybits >>> 8 & 0x0f)];
    v[C4] = bgs[Math.max(bits >>> 12 & 0x0f, daybits >>> 12 & 0x0f)];
	
}

public void setCornersSimple(byte li, byte dl) {
	float[] v = getVertices();
	v[C1] = lightFades[0][l][li][dl];
    v[C2] = lightFades[0][l][li][dl];
    v[C3] = lightFades[0][l][li][dl];
    v[C4] = lightFades[0][l][li][dl];
	
}

public void setColorID(int color, int li, int dl, int index) {
	if (color < 0){
		setSpecialColorID(color, index);
		return;
	}
	float[] v = getVertices();
	v[C1] = lightFades[color][l][li][dl];
    v[C2] = lightFades[color][l][li][dl];
    v[C3] = lightFades[color][l][li][dl];
    v[C4] = lightFades[color][l][li][dl];
	//setColor(lightFades[color][l][li][dl]);
	//this.setColor(Color.ORANGE);
}

private void setSpecialColorID(int color, int index) {
	color = -color;
	byte li = 15, dl = 15;
	int inc = 0;
	switch (color){
	default:
	case 1:
	{
		inc = index*12+Punk.accum16;
		inc %= specialColors[color].length;
		float[] v = getVertices();
		v[C1] = specialColors[color][inc];
	    v[C2] = specialColors[color][inc];
	    v[C3] = specialColors[color][inc];
	    v[C4] = specialColors[color][inc];
	}
		break;
	case 2:
	{
			inc = index+Punk.accum30;
			inc %= specialColors[color].length;
			float[] v = getVertices();
			v[C1] = specialColors[color][inc];
		    v[C2] = specialColors[color][inc];
		    v[C3] = specialColors[color][inc];
		    v[C4] = specialColors[color][inc];
	}
		break;
	case 3:
	{
			inc = index*9+Punk.accum30;
			inc %= specialColors[color].length;
			int inc2 = (13+ inc)%specialColors[color].length,
					inc3 = (36+ inc)%specialColors[color].length,
							inc4 = (49+ inc)%specialColors[color].length;
			float[] v = getVertices();
			v[C1] = specialColors[color][inc];
		    v[C2] = specialColors[color][inc2];
		    v[C3] = specialColors[color][inc3];
		    v[C4] = specialColors[color][inc4];
	}
		break;
	}
	
	
}

}

package com.niz.punk.planes;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.FloatArray;
import com.niz.punk.Chunk;
import com.niz.punk.PlaneDef;
import com.niz.punk.Player;
import com.niz.punk.Punk;
import com.niz.punk.PunkBodies;
import com.niz.punk.PunkMap;
import com.niz.punk.SimplexNoise;
import com.niz.punk.Spline;
import com.niz.punk.Waypoint;

public class PrimeMaterial extends PlaneDef {
	String TAG = "planedef";
	private static int CB = Punk.CHUNKBITS;
	public static int wWidth = 52000;
	private static int wDepth = 250;
	public static int wLavaDepth = -300;
	public static int seaGap = 200;
	private boolean sideBlocks;
	private static int maximumHeight, minimumHeight;
	public static int effectiveDepth;
	private static int skyIslandsOffset = 60;
	private static boolean[] treeMask;//anding with this
	private static int campsiteSize = 20;
	static FloatArray elevX = new FloatArray();
	static FloatArray elevY = new FloatArray();
	static FloatArray tempX = new FloatArray();
	static FloatArray tempY = new FloatArray();
	static FloatArray smoothX = new FloatArray();
	static FloatArray smoothY = new FloatArray();
	static FloatArray thickX = new FloatArray();
	static FloatArray thickY = new FloatArray();
	static int[] elevCache, smoothCache, tempCache;
	private int oceanDepth = 100;
	static Random seededRandom = new Random();
	public static Spline elevationSpline, temperatureSpline, smoothnessSpline;//, groundThicknessSpline; 
	public int[] maxHeights = {200, -50, -300, 200, -50, -150, -250, 200};
	public int[] oreChances = {500, 500,  500, 300,400, 400, 500, 200};
	boolean skyBlocks ;//= (y+yOffset > groundHeight +40);
	static int[] tmpLandInfo = new int[6];
	public int lastSpawnedTree;
	
	public int[] mushZones = {0,1,2,3,4,5,6,7}, mushZonesDeep = {4,5,6,7,0,1,2,3};
	public int size, surfaceTowns, gnomeTowns, darkElfTowns;

	public PrimeMaterial(){
		name = "Prime Material";
	}
	
	
	private void generateDoors(PunkMap map, Chunk c) {
		//4 per chunk?
		/*Gdx.app.log(TAG, "making doors"+c.spaces.list.size);
		int doorCount = 4;
		if (c.spaces.list.size <= doorCount)
			while (c.spaces.list.size > 0){
				BlockLoc loc = c.spaces.removeFirst();
				c.unfoundDoors.addBlock(loc.x+c.chunkOffset, loc.y+c.yOffset);
				c.unfoundDoors.free(loc);
			}
		else for (int i = 0; i < doorCount; i++){
			BlockLoc loc = c.spaces.list.removeIndex(i*(c.spaces.list.size/doorCount));
			c.unfoundDoors.addBlock(loc.x+c.chunkOffset, loc.y+c.yOffset);
			c.createDoor(loc.x+c.chunkOffset, loc.y+c.yOffset, 3, 0, 0, 2);
			map.changeBlock(loc.x+c.chunkOffset, loc.y+c.yOffset, 50, 0, true);
			
		}*/
	}
	

	public static void makeClearing(int x1, int x2){
		for (int i = x1+wWidth; i < x2+wWidth; i++)
			if (i >= 0 && i < wWidth*2)treeMask[i] = false;
	}

	public static void makeForest(int x1, int x2){
		for (int i = x1+wWidth; i < x2+wWidth; i++)
			if (i >= 0 && i < wWidth*2)treeMask[i] = true;
	}
	public static void addOffsetNode(FloatArray fx, FloatArray fy, int xOff, int yOff){
		fx.add(fx.get(fx.size-1)+xOff);
		fy.add(fy.get(fy.size-1)+yOff);
	}
	public static void addAbsoluteNode(FloatArray fx, FloatArray fy, int xval, int yval){
		fx.add(xval);
		fy.add(yval);
	}

	private void setUpFeatures(long seed){
		//features
		treeMask = new boolean[wWidth*2];
		for (int i = 0; i < wWidth*2; i++)
			treeMask[i] = true;
		
		  elevX = new FloatArray();
		  elevY = new FloatArray();
		  tempX = new FloatArray();
		  tempY = new FloatArray();
		  smoothX = new FloatArray();
		  smoothY = new FloatArray();
		  thickX = new FloatArray();
		  thickY = new FloatArray();
		
		
		maximumHeight = 0;
		elevX.clear();
		elevY.clear();
		tempX.clear();
		tempY.clear();
		smoothX.clear();
		smoothY.clear();
		
		elevX.add(-wWidth);
		elevY.add(0);
		tempX.add(-wWidth);
		tempY.add(0);
		smoothX.add(-wWidth);
		smoothY.add(2+MathUtils.random(18));
		
		int wayID = 0;
		seededRandom.setSeed(seed);
		//boolean tooLow = false;
		int featureType = 0;
		while (elevX.get(elevX.size-1) < wWidth*2+Punk.CHUNKSIZE*2){
			//addOffsetNode(elevX, elevY, 100, 0);
			//addAbsoluteNode(smoothX, smoothY, (int)elevX.get(elevX.size-1), MathUtils.random(10));
			featureType = seededRandom.nextInt(8);
			int lastY = (int) elevY.get(elevY.size-1);
			boolean tooLow = (lastY < 100);
			
			//if (featureType == 0)
				//featureType = 5;
			//else 
			//	featureType =0;
			//if (tooLow) featureType = -1;
			featureType = 3;
			int prevX = MathUtils.floor(elevX.get(elevX.size-1));//x value of leftmost block
			//if (prevX > -812 && prevX > 128) featureType = 0;;
			Gdx.app.log("chunk", "feature:"+featureType+" x "+elevX.get(elevX.size-1));
			switch (featureType){//
			
				case 12://city, elf
				{
	
					//outposts before and after
					
					int fadeTemp = 1;
					int size0 = seededRandom.nextInt(202)+38;
					int size1 = seededRandom.nextInt(202)+38;
					int yOff = 0;
					//yOff = 0;
					int newSmoothness = 0;
					//newSmoothness = 0;
					int prevTemp = (int) tempY.get(tempY.size-1);
					int newTemp = 1;
					//int newThickness = seededRandom.nextInt(5)+5;
					
					//int newTemp = MathUtils.clamp(prevTemp+MathUtils.random(-1,1), -6, 6);
					
					
					//addAbsoluteNode(thickX, thickY, prevX+size1, newThickness);
					//addAbsoluteNode(tempX, tempY, prevX+size1, prevTemp);
					
					//fade
					int fadeSize = seededRandom.nextInt(16)+8;
					addAbsoluteNode(tempX, tempY, prevX+2, prevTemp);
					addOffsetNode(tempX, tempY, 1,0);
					addAbsoluteNode(tempX, tempY, prevX+2+fadeSize, fadeTemp);
					addOffsetNode(tempX, tempY, 1,0);
					addOffsetNode(tempX, tempY, 1,0);
					makeClearing(prevX, prevX+fadeSize);
					
					//node1
					prevX = (int)elevX.get(elevX.size-1);//x value of leftmost block
					Gdx.app.log("chunk", "temp@ ("+prevX+") : "+prevTemp);
					addOffsetNode(elevX, elevY, 2, 0);
					addOffsetNode(elevX, elevY, size1, yOff);
					
					addAbsoluteNode(smoothX, smoothY, prevX, (int) smoothY.get(smoothY.size-1));
					addOffsetNode(smoothX, smoothY, 1, 0);
					addAbsoluteNode(smoothX, smoothY, prevX+size1, newSmoothness);
					
					//makeClearing(prevX+size1+size0-fadeSize, prevX+size1+size0);
					//makeClearing(prevX, prevX+size1+size0-fadeSize);
					//middle. oasis
					
					{
						int mX = (int)elevX.get(elevX.size-1)+2;
						addOffsetNode(elevX, elevY, 1, 0);
						addOffsetNode(elevX, elevY, campsiteSize+4, 0);
						addOffsetNode(elevX, elevY, 1, 0);
						if (mX < wWidth)ways.add(new Waypoint(wayID, 29, "elf city", mX+campsiteSize/2, (int) elevY.get(elevY.size-1)+100, 0));
						wayID++;
						makeClearing(mX, mX+campsiteSize);
						
						addAbsoluteNode(smoothX, smoothY, mX-campsiteSize, 0);
						addAbsoluteNode(smoothX, smoothY, mX+campsiteSize, 0);
					}
					
					
					
					
					
					//node2
					addOffsetNode(elevX, elevY, size0, 0);
					addOffsetNode(elevX, elevY, 2, 0);
					addAbsoluteNode(smoothX, smoothY, prevX+size1+size0, newSmoothness);
					
					addAbsoluteNode(tempX, tempY, prevX+size1+size0-fadeSize, fadeTemp);
					addOffsetNode(tempX, tempY, 1,0);
					addOffsetNode(tempX, tempY, 1,0);
					addAbsoluteNode(tempX, tempY, prevX+size1+size0, newTemp);
					addOffsetNode(tempX, tempY, 1,0);
					
					//addAbsoluteNode(thickX, thickY, prevX+size1+size0, newThickness);
					//addAbsoluteNode(tempX, tempY, prevX+size1+size0, newTemp);
					
			
				
				}
				break;
				default:
				case 11://city, human
				{
					//campsites before and after
					
					int fadeTemp = seededRandom.nextInt(7)-6;
					int size0 = seededRandom.nextInt(102)+38;
					int size1 = seededRandom.nextInt(102)+38;
					int yOff = seededRandom.nextInt(4)-2;
					//yOff = 0;
					int newSmoothness = 0;
					//newSmoothness = 0;
					int prevTemp = (int) tempY.get(tempY.size-1);
					int newTemp = 2;//seededRandom.nextInt(13)-6;
					//int newThickness = seededRandom.nextInt(5)+5;
					
					//int newTemp = MathUtils.clamp(prevTemp+MathUtils.random(-1,1), -6, 6);
					
					
					//addAbsoluteNode(thickX, thickY, prevX+size1, newThickness);
					//addAbsoluteNode(tempX, tempY, prevX+size1, prevTemp);
					
					//fade
					int fadeSize = seededRandom.nextInt(16)+8;
					addAbsoluteNode(tempX, tempY, prevX+2, prevTemp);
					addOffsetNode(tempX, tempY, 1,0);
					addAbsoluteNode(tempX, tempY, prevX+2+fadeSize, fadeTemp);
					addOffsetNode(tempX, tempY, 1,0);
					addOffsetNode(tempX, tempY, 1,0);
					makeClearing(prevX, prevX+fadeSize);
					
					//node1
					prevX = (int)elevX.get(elevX.size-1);//x value of leftmost block
					Gdx.app.log("chunk", "temp@ ("+prevX+") : "+prevTemp);
					addOffsetNode(elevX, elevY, 2, 0);
					addOffsetNode(elevX, elevY, size1, yOff);
					
					addAbsoluteNode(smoothX, smoothY, prevX, (int) smoothY.get(smoothY.size-1));
					addOffsetNode(smoothX, smoothY, 1, 0);
					addAbsoluteNode(smoothX, smoothY, prevX+size1, newSmoothness);
					
					//makeClearing(prevX+size1+size0-fadeSize, prevX+size1+size0);
					//makeClearing(prevX, prevX+size1+size0-fadeSize);
					//middle. CITY
					
					
					{
						int mX = (int)elevX.get(elevX.size-1)+2;
						addOffsetNode(elevX, elevY, 1, 0);
						addOffsetNode(elevX, elevY, campsiteSize+4, 0);
						addOffsetNode(elevX, elevY, 1, 0);
						if (mX < wWidth)ways.add(new Waypoint(wayID, 28, "human city", mX+campsiteSize/2, (int) elevY.get(elevY.size-1)+100, 0));
						wayID++;
						makeClearing(mX-60, mX+60);
						
						addAbsoluteNode(smoothX, smoothY, mX-campsiteSize, 0);
						addAbsoluteNode(smoothX, smoothY, mX+campsiteSize, 0);
					}
					
					
					
					
					//node2
					addOffsetNode(elevX, elevY, size0, 0);
					addOffsetNode(elevX, elevY, 2, 0);
					addAbsoluteNode(smoothX, smoothY, prevX+size1+size0, newSmoothness);
					
					addAbsoluteNode(tempX, tempY, prevX+size1+size0-fadeSize, fadeTemp);
					addOffsetNode(tempX, tempY, 1,0);
					addOffsetNode(tempX, tempY, 1,0);
					addAbsoluteNode(tempX, tempY, prevX+size1+size0, newTemp);
					addOffsetNode(tempX, tempY, 1,0);
					
					//addAbsoluteNode(thickX, thickY, prevX+size1+size0, newThickness);
					//addAbsoluteNode(tempX, tempY, prevX+size1+size0, newTemp);
					
				}
				
				
				break;
				
				
				case 8:case 9:case 10:
				case 5://flat, forest random
				{
					//campsites before and after
					
					int fadeTemp = seededRandom.nextInt(7)-6;
					int size0 = seededRandom.nextInt(102)+38;
					int size1 = seededRandom.nextInt(102)+38;
					int yOff = seededRandom.nextInt(4)-2;
					//yOff = 0;
					int newSmoothness = seededRandom.nextInt(18)+4;
					//newSmoothness = 0;
					int prevTemp = (int) tempY.get(tempY.size-1);
					int newTemp = seededRandom.nextInt(13)-6;
					//int newThickness = seededRandom.nextInt(5)+5;
					
					//int newTemp = MathUtils.clamp(prevTemp+MathUtils.random(-1,1), -6, 6);
					
					
					//addAbsoluteNode(thickX, thickY, prevX+size1, newThickness);
					//addAbsoluteNode(tempX, tempY, prevX+size1, prevTemp);
					
					//fade
					int fadeSize = seededRandom.nextInt(16)+8;
					addAbsoluteNode(tempX, tempY, prevX+2, prevTemp);
					addOffsetNode(tempX, tempY, 1,0);
					addAbsoluteNode(tempX, tempY, prevX+2+fadeSize, fadeTemp);
					addOffsetNode(tempX, tempY, 1,0);
					addOffsetNode(tempX, tempY, 1,0);
					makeClearing(prevX, prevX+fadeSize);
					
					//node1
					prevX = (int)elevX.get(elevX.size-1);//x value of leftmost block
					Gdx.app.log("chunk", "temp@ ("+prevX+") : "+prevTemp);
					addOffsetNode(elevX, elevY, 2, 0);
					addOffsetNode(elevX, elevY, size1, yOff);
					
					addAbsoluteNode(smoothX, smoothY, prevX, (int) smoothY.get(smoothY.size-1));
					addOffsetNode(smoothX, smoothY, 1, 0);
					addAbsoluteNode(smoothX, smoothY, prevX+size1, newSmoothness);
					
					//makeClearing(prevX+size1+size0-fadeSize, prevX+size1+size0);
					//makeClearing(prevX, prevX+size1+size0-fadeSize);
					//middle. oasis
					
					{
						int mX = (int)elevX.get(elevX.size-1)+2;
						addOffsetNode(elevX, elevY, 1, 0);
						addOffsetNode(elevX, elevY, campsiteSize+4, 0);
						addOffsetNode(elevX, elevY, 1, 0);
						if (mX < wWidth)ways.add(new Waypoint(wayID, 27, "forest clearing", mX+campsiteSize/2, (int) elevY.get(elevY.size-1)+100, 0));
						wayID++;
						makeClearing(mX, mX+campsiteSize);
						
						addAbsoluteNode(smoothX, smoothY, mX-campsiteSize, 0);
						addAbsoluteNode(smoothX, smoothY, mX+campsiteSize, 0);
					}
					
					
					
					
					
					//node2
					addOffsetNode(elevX, elevY, size0, 0);
					addOffsetNode(elevX, elevY, 2, 0);
					addAbsoluteNode(smoothX, smoothY, prevX+size1+size0, newSmoothness);
					
					addAbsoluteNode(tempX, tempY, prevX+size1+size0-fadeSize, fadeTemp);
					addOffsetNode(tempX, tempY, 1,0);
					addOffsetNode(tempX, tempY, 1,0);
					addAbsoluteNode(tempX, tempY, prevX+size1+size0, newTemp);
					addOffsetNode(tempX, tempY, 1,0);
					
					//addAbsoluteNode(thickX, thickY, prevX+size1+size0, newThickness);
					//addAbsoluteNode(tempX, tempY, prevX+size1+size0, newTemp);
					
			
				}
				break;
				
				case 0:///flat, desert
				{//campsites before and after
					
						int fadeTemp = 5;
						int size0 = seededRandom.nextInt(142)+88;
						int size1 = seededRandom.nextInt(142)+88;
						int yOff = seededRandom.nextInt(4)-2;
						yOff = 0;
						int newSmoothness = 0;
						//newSmoothness = 0;
						int prevTemp = (int) tempY.get(tempY.size-1);
						int newTemp = seededRandom.nextInt(13)-6;
						//int newThickness = seededRandom.nextInt(5)+5;
						
						//int newTemp = MathUtils.clamp(prevTemp+MathUtils.random(-1,1), -6, 6);
						
						
						//addAbsoluteNode(thickX, thickY, prevX+size1, newThickness);
						//addAbsoluteNode(tempX, tempY, prevX+size1, prevTemp);
						
						//fade
						int fadeSize = seededRandom.nextInt(16)+8;
						addAbsoluteNode(tempX, tempY, prevX+2, prevTemp);
						addOffsetNode(tempX, tempY, 1,0);
						addAbsoluteNode(tempX, tempY, prevX+2+fadeSize, fadeTemp);
						addOffsetNode(tempX, tempY, 1,0);
						addOffsetNode(tempX, tempY, 1,0);
						makeClearing(prevX, prevX+fadeSize);
						
						//node1
						prevX = (int)elevX.get(elevX.size-1);//x value of leftmost block
						Gdx.app.log("chunk", "temp@ ("+prevX+") : "+prevTemp);
						addOffsetNode(elevX, elevY, 2, 0);
						addOffsetNode(elevX, elevY, size1, yOff);
						
						addAbsoluteNode(smoothX, smoothY, prevX, (int) smoothY.get(smoothY.size-1));
						addOffsetNode(smoothX, smoothY, 1, 0);
						addAbsoluteNode(smoothX, smoothY, prevX+size1, newSmoothness);
						
						makeClearing(prevX+size1+size0-fadeSize, prevX+size1+size0);
						makeClearing(prevX, prevX+size1+size0-fadeSize);
						//middle. oasis
						
						{
							int mX = (int)elevX.get(elevX.size-1)+2;
							addOffsetNode(elevX, elevY, 1, 0);
							addOffsetNode(elevX, elevY, campsiteSize+4, 0);
							addOffsetNode(elevX, elevY, 1, 0);
							if (mX < wWidth)ways.add(new Waypoint(wayID, 13, "oasis", mX+campsiteSize/2, (int) elevY.get(elevY.size-1)+100, 0));
							wayID++;
							makeForest(mX, mX+campsiteSize);
							
							addAbsoluteNode(smoothX, smoothY, mX-campsiteSize, 0);
							addAbsoluteNode(smoothX, smoothY, mX+campsiteSize, 0);
						}
						
						
						
						
						
						//node2
						addOffsetNode(elevX, elevY, size0, 0);
						addOffsetNode(elevX, elevY, 2, 0);
						addAbsoluteNode(smoothX, smoothY, prevX+size1+size0, newSmoothness);
						
						addAbsoluteNode(tempX, tempY, prevX+size1+size0-fadeSize, fadeTemp);
						addOffsetNode(tempX, tempY, 1,0);
						addOffsetNode(tempX, tempY, 1,0);
						addAbsoluteNode(tempX, tempY, prevX+size1+size0, newTemp);
						addOffsetNode(tempX, tempY, 1,0);
						
						//addAbsoluteNode(thickX, thickY, prevX+size1+size0, newThickness);
						//addAbsoluteNode(tempX, tempY, prevX+size1+size0, newTemp);
						
				}
						break;
				case 1://mountain
				{
					
						//random walk up, set temp, random walk down, set temp
					 prevX = MathUtils.floor(elevX.get(elevX.size-1));//x value of leftmost block
					
					int prevTemp = MathUtils.floor(tempY.get(tempY.size-1));
					int prevSmoothness = MathUtils.floor(smoothY.get(smoothY.size-1));
					int size1;// = seededRandom.nextInt(64)+20;
					int yOff;// = seededRandom.nextInt(64)-32;
					int numberOfStepsApproach = seededRandom.nextInt(16)+1;
					int numberOfStepsRetreat = numberOfStepsApproach;//seededRandom.nextInt(6)+4;
					int numberOfStepsMiddle = 5;
					int newTemp = seededRandom.nextInt(13)-6; 
					int topSmoothness = seededRandom.nextInt(16)+2;//for peak
					int lSmoothness = seededRandom.nextInt(6)+2;//for peak
					int rSmoothness = seededRandom.nextInt(6)+2;//for peak
					addAbsoluteNode(smoothX, smoothY, prevX+2, lSmoothness);
					for (int i = 0; i < numberOfStepsApproach; i++){//approach;
						if (seededRandom.nextInt(16) >8){
							int middleX = (int)elevX.get(elevX.size-1)+2;
							addOffsetNode(elevX, elevY, 1, 0);
							addOffsetNode(elevX, elevY, campsiteSize+4, 0);
							addOffsetNode(elevX, elevY, 1, 0);
							if (middleX < wWidth)ways.add(new Waypoint(wayID, 27, "mtn campsite", middleX+campsiteSize/2, (int) elevY.get(elevY.size-1)+100, 0));
							wayID++;
							makeClearing(middleX, middleX+campsiteSize);
							addAbsoluteNode(smoothX, smoothY, middleX-campsiteSize, 0);
							addAbsoluteNode(smoothX, smoothY, middleX+campsiteSize, 0);
						}
						size1 = seededRandom.nextInt(32)+16;
						yOff =  seededRandom.nextInt(108);
						addOffsetNode(elevX, elevY, size1, yOff);
					}
					
					int middleX = (int)elevX.get(elevX.size-1);
					//smoothness higher at top
					
					addAbsoluteNode(smoothX, smoothY, middleX, topSmoothness);
					
					for (int i = 0; i < numberOfStepsMiddle; i++){//peak
						size1 = seededRandom.nextInt(16)+16;
						yOff = -seededRandom.nextInt(108)+64;
						addOffsetNode(elevX, elevY, size1, yOff);
					}
					
					for (int i = 0; i < numberOfStepsRetreat; i++){//retreat
						size1 = seededRandom.nextInt(32)+16;
						yOff = -seededRandom.nextInt(108);
						addOffsetNode(elevX, elevY, size1, yOff);
						if (seededRandom.nextInt(16) >8){
							int mX = (int)elevX.get(elevX.size-1)+2;
							addOffsetNode(elevX, elevY, 1, 0);
							addOffsetNode(elevX, elevY, campsiteSize+4, 0);
							addOffsetNode(elevX, elevY, 1, 0);
							if (mX < wWidth)ways.add(new Waypoint(wayID, 27, "mtn campsite", mX+campsiteSize/2, (int) elevY.get(elevY.size-1)+100, 0));
							wayID++;
							makeClearing(mX, mX+campsiteSize);
							addAbsoluteNode(smoothX, smoothY, mX-campsiteSize, 0);
							addAbsoluteNode(smoothX, smoothY, mX+campsiteSize, 0);
						}
					}
					int lastX = (int)elevX.get(elevX.size-1);
					
					//low temp at top
					addAbsoluteNode(tempX, tempY, prevX+2, prevTemp);
					addAbsoluteNode(tempX, tempY, middleX, -6);
					addAbsoluteNode(tempX, tempY, lastX, newTemp);
					//new temp for other end
					
					
					
					addAbsoluteNode(smoothX, smoothY, lastX, rSmoothness);
					
					
					
				}break;
				
				case 2://valley
				{
					
					
					prevX = MathUtils.floor(elevX.get(elevX.size-1))+1;//x value of leftmost block
					//if (prevX < 128 && prevX > 0) break;
					int prevTemp = MathUtils.floor(tempY.get(tempY.size-1));
					int prevSmoothness = MathUtils.floor(smoothY.get(smoothY.size-1));
					int size1=0;// = seededRandom.nextInt(64)+20;
					int yOff=0;// = seededRandom.nextInt(64)-32;
					int numberOfStepsApproach = 2;//seededRandom.nextInt(8)+8;
					int numberOfStepsRetreat = 2;//seededRandom.nextInt(8)+8;
					int newTemp = seededRandom.nextInt(13)-6; 
					int newSmoothness = 0;//seededRandom.nextInt(8)+5;//for valley
					int lSmoothness = seededRandom.nextInt(3)+3;//for valley
					int rSmoothness = seededRandom.nextInt(4)+3;//for valley
					int leftX = (int)elevX.get(elevX.size-1);
					for (int i = 0; i < numberOfStepsApproach; i++){//approach;
						
						size1 = seededRandom.nextInt(52)+65;
						yOff = seededRandom.nextInt(52)-65-32;
						addOffsetNode(elevX, elevY, size1, yOff);
					}
					//valley
					
					//size1 = ;
					//yOff = 0;
					//addOffsetNode(elevX, elevY, seededRandom.nextInt(128)+2416, 0);//valley floor
					
					
					
					int middleX = (int)elevX.get(elevX.size-1)+2;
					addOffsetNode(elevX, elevY, 1, 0);
					addOffsetNode(elevX, elevY, campsiteSize+4, 0);
					addOffsetNode(elevX, elevY, 1, 0);
					if (middleX < wWidth)ways.add(new Waypoint(wayID, 27, "valley campsite", middleX+campsiteSize/2, (int) elevY.get(elevY.size-1)+100, 0));
					wayID++;
					makeClearing(middleX, middleX+campsiteSize);
					
					for (int i = 0; i < numberOfStepsRetreat; i++){//retreat
						//size1 = seededRandom.nextInt(20)+24;
						//yOff = seededRandom.nextInt(64)+128;
						size1 = seededRandom.nextInt(52)+65;
						yOff = -(seededRandom.nextInt(52)-65-32);
						addOffsetNode(elevX, elevY, size1, yOff);
					}
					int lastX = (int)elevX.get(elevX.size-1);
					
					//random temp at far side
					addAbsoluteNode(tempX, tempY, prevX+2, prevTemp);
					//addAbsoluteNode(tempX, tempY, middleX, -6);
					addAbsoluteNode(tempX, tempY, lastX, newTemp);
					//new temp for other end
					
					
					//smoother in the middle
					addAbsoluteNode(smoothX, smoothY, leftX+2, lSmoothness);
					addAbsoluteNode(smoothX, smoothY, middleX-campsiteSize, 0);
					addAbsoluteNode(smoothX, smoothY, middleX+campsiteSize, 0);
					addAbsoluteNode(smoothX, smoothY, lastX, rSmoothness);
				}break;
				case 7:
				case 3://hills
				{
					prevX = MathUtils.floor(elevX.get(elevX.size-1));//x value of leftmost block
					int prevTemp = MathUtils.floor(tempY.get(tempY.size-1));
					int prevSmoothness = MathUtils.floor(smoothY.get(smoothY.size-1));
					
					int newTemp = seededRandom.nextInt(13)-6; 
					int newSmoothness = seededRandom.nextInt(2);
					int yOff = 0;
					int size1 = 0;
					int numberOfRepeats = seededRandom.nextInt(8)+4;
					for (int i = 0; i < numberOfRepeats; i++){
						
						
						if (i % 2 == 1) yOff *= -1;
						else {
							yOff = seededRandom.nextInt(16)+4;
							size1 = seededRandom.nextInt(8)+32;
						}
						addOffsetNode(elevX, elevY, size1, yOff);
					}
					int lastX = (int)elevX.get(elevX.size-1);
					
					
					
					addAbsoluteNode(tempX, tempY, prevX+2, prevTemp);
					//addAbsoluteNode(tempX, tempY, middleX, -6);
					addAbsoluteNode(tempX, tempY, lastX, newTemp);
					//new temp for other end
					
					
					//smoother in the middle
					//addAbsoluteNode(smoothX, smoothY, middleX, newSmoothness);
					addAbsoluteNode(smoothX, smoothY, lastX, newSmoothness);
					addOffsetNode(elevX, elevY, 8,0);
					
				}break;
				case 6:
				case 4://plat
				{
					prevX = MathUtils.floor(elevX.get(elevX.size-1));//x value of leftmost block
					int prevSmoothness = MathUtils.floor(smoothY.get(smoothY.size-1));
					int size1;// = seededRandom.nextInt(64)+20;
					int yOff;// = seededRandom.nextInt(64)-32;
					int numberOfSteps = seededRandom.nextInt(1)+1;
					int newSmoothness = seededRandom.nextInt(16);//for peak
					int rough = seededRandom.nextInt(16)+2;
					int smooth = seededRandom.nextInt(3);
					//needs a less smooth approach and flat on top
					
					
					for (int i = 0; i < numberOfSteps; i++){//approach;
						
						size1 = seededRandom.nextInt(12)+4;
						yOff = seededRandom.nextInt(16)-8;
						addOffsetNode(elevX, elevY, size1, yOff);
					}
					int approachStartX = (int)elevX.get(elevX.size-1);
					
					//plat
					size1 = seededRandom.nextInt(128)+32;
					yOff = seededRandom.nextInt(64)-32;
					addOffsetNode(elevX, elevY, size1, yOff);				
					
					int retreatStartX = (int)elevX.get(elevX.size-1);
					
					for (int i = 0; i < numberOfSteps; i++){//retreat
						size1 = seededRandom.nextInt(32)+15;
						yOff = seededRandom.nextInt(32)-16;
						addOffsetNode(elevX, elevY, size1, yOff);
					}
					
					int retreatEndX = (int)elevX.get(elevX.size-1);
					
					makeForest(prevX, retreatEndX);
					//smoothness higher at top
					addAbsoluteNode(smoothX, smoothY, prevX, prevSmoothness);
					addAbsoluteNode(smoothX, smoothY, approachStartX+2, newSmoothness);
					addAbsoluteNode(smoothX, smoothY, retreatEndX, newSmoothness);
				}break;
				/*case 5://transition
				{
					
				}break;
				case 6:
				case 7:*/
				case -1:
					//random walk up, set temp, random walk down, set temp
					prevX = MathUtils.floor(elevX.get(elevX.size-1));//x value of leftmost block
					if (prevX < 512 && prevX > 10) break;
					int prevTemp = MathUtils.floor(tempY.get(tempY.size-1));
					int prevSmoothness = MathUtils.floor(smoothY.get(smoothY.size-1));
					int size1;// = seededRandom.nextInt(64)+20;
					int yOff;// = seededRandom.nextInt(64)-32;
					int numberOfSteps = seededRandom.nextInt(16)+1;
					int newTemp = seededRandom.nextInt(13)-6; 
					int newSmoothness = seededRandom.nextInt(32)+4;//for peak
					
					for (int i = 0; i < numberOfSteps; i++){//approach;
						
						size1 = seededRandom.nextInt(32)+16;
						yOff = seededRandom.nextInt(32)+16;
						addOffsetNode(elevX, elevY, size1, yOff);
					}
					
					int middleX = (int)elevX.get(elevX.size-1);
					
					
					//low temp at top
					addAbsoluteNode(tempX, tempY, prevX+2, prevTemp);
					addAbsoluteNode(tempX, tempY, middleX, newTemp);
					//addAbsoluteNode(tempX, tempY, lastX, newTemp);
					//new temp for other end
					
					
					//smoothness higher at top
					addAbsoluteNode(smoothX, smoothY, prevX+2, prevSmoothness);
					addAbsoluteNode(smoothX, smoothY, middleX, newSmoothness);
					//addAbsoluteNode(smoothX, smoothY, lastX, prevSmoothness);
					break;
			}
		}
		
		
		
		Gdx.app.log("splines:", " elevx "+elevX.get(elevX.size-1)+" smoothness "+smoothX.size+" temperature "+tempX.size);
		addOffsetNode(elevX, elevY, 20,0);
		addOffsetNode(elevX, elevY, 20,0);
		
		elevationSpline = new Spline(elevX.items,elevY.items);
		
		smoothnessSpline = new Spline(smoothX.items, smoothY.items);
		temperatureSpline = new Spline(tempX.items, tempY.items);
		cacheSplines();
		//for (int i = 0; i < 50000; i += 1)//MathUtils.random(10))
		//	if (smoothnessSpline.getValue(i) < 0) Gdx.app.log("chunkinit", "x:"+i+" = "+smoothnessSpline.getValue(i));
		//groundThicknessSpline = new Spline(thickX.items, thickY.items);
		
	}

	private static void cacheSplines() {
		

		elevCache = new int[wWidth*2];
		tempCache = new int[wWidth*2];
		smoothCache = new int[wWidth*2];
		maximumHeight = 0;
		minimumHeight = 0;
		for (int i = 0; i < wWidth*2; i++){
			elevCache[i] = MathUtils.floor(elevationSpline.getValue(i-wWidth));
			if (elevCache[i] > maximumHeight)
				maximumHeight = elevCache[i];
			if (elevCache[i] < minimumHeight)
				minimumHeight = elevCache[i];
			
			tempCache[i] = MathUtils.floor(temperatureSpline.getValue(i-wWidth));
			smoothCache[i] = Math.max(0,MathUtils.floor(smoothnessSpline.getValue(i-wWidth)));
			//Gdx.app.log("spline", "elev"+i+": "+elevCache[i]+" tmp:"+tempCache[i]+" smo"+smoothCache[i]);
		}
		//for (int i = 0; i < elevX.size; i++)
			//Gdx.app.log("chunk", "node "+elevX.get(i)+","+elevY.get(i));
		
		effectiveDepth = minimumHeight-wDepth;
		//elevCache = null;
		//tempCache = null;
		//smoothCache = null;
		elevationSpline = null;
		temperatureSpline = null;
		smoothnessSpline = null;
		elevX = null;
		elevY = null;
		smoothX = null;
		smoothY = null;
		tempX = null;
		tempY = null;
		System.gc();
	}

	/*public void makeDepos2its(PunkMap map){
		//ores
		int depx, depy, repeats;
		boolean done;
		//printCA(tmpdeposit);
		//copper deposits, whole map
		//repeats = XRand.getSeeded(2,chunkID);
		//repeats++;
		//repeats = 32;
		if (heightID == 0){
			repeats = MathUtils.random(1,5);
			for (int i = 0; i < repeats; i++){
				depx = MathUtils.random(Punk.CHUNKSIZE-44)+22;
				depy = MathUtils.random(22, Punk.CHUNKSIZE-22);
				writeDeposit(makeADeposit(20,20), depx, depy, -6, map);
			}
			//mithril deposits, bottom
			repeats = MathUtils.random(0,3);
			//repeats = XRand.getSeeded(2,chunkID);
			
			for (int i = 0; i < repeats; i++){
				depx = MathUtils.random(20, Punk.CHUNKSIZE-20);
				depy = MathUtils.random(20, Punk.CHUNKSIZE-20);
				writeDeposit(makeADeposit(10,10), depx, depy, -7, map);
			}
			
			//gold
			repeats = MathUtils.random(8)+4;
			//repeats = XRand.getSeeded(3,chunkID);
			//repeats++;
			for (int i = 0; i < repeats; i++){
				depx = MathUtils.random(50, Punk.CHUNKSIZE-50);
				depy = MathUtils.random(50, Punk.CHUNKSIZE-50);
				
				for (int y = depy; y >30; y-=MathUtils.random(10)+10){
					writeDeposit(makeADeposit((depy-y)/2+4,8), depx, y, -5, map);
				}
				
			}
			
			//crystals
			repeats = (int)(Math.random()*3+1);
			for (int i = 0; i < repeats; i++){
				depx = MathUtils.random(50, Punk.CHUNKSIZE-50);
				depy = MathUtils.random(50, Punk.CHUNKSIZE-50);
				
				writeCrystals(makeADeposit(16,100), depx, 0, 30);
			}
			repeats = (int)(Math.random()*3+1);
			for (int i = 0; i < repeats; i++){
				depx = MathUtils.random(50, Punk.CHUNKSIZE-50);
				depy = MathUtils.random(50, Punk.CHUNKSIZE-50);
				
				writeCrystals(makeADeposit(16,100), depx, 0, 31);
			}
			repeats = (int)(Math.random()*3+1);
			for (int i = 0; i < repeats; i++){
				depx = MathUtils.random(50, Punk.CHUNKSIZE-50);
				depy = MathUtils.random(50, Punk.CHUNKSIZE-50);
				
				writeCrystals(makeADeposit(16,100), depx, 0, 32);
			}
		}
		
		
		
		//fluids
		
	}
	public boolean isSoil(int id){
		switch (id){
		case 21:
		case 20:
		case 2:
		case 3: return true;
		default: return false;
		}
	}
	public void makeWaterLevel(){
		int y;
		boolean done = false;
		for (int x = 0; x < Punk.CHUNKSIZE; x++){
			y = 120;
			done = false;
			while (!done && y > 80){
				if (block[(x<<CB)+y].blockID == 0) block[(x<<CB)+y].set(-1, 0);
				else done = true;
				y--;
			}
		}
	}
	*/
	
	/*public void makeeSpawners(){
		//random positions, check around
		int spawnerCount = 50;
		if (gameType == 0) {}spawnerCount = 0;
		for (int i = 0; i < spawnerCount; i++){
			int x = MathUtils.random(5, Punk.CHUNKSIZE-5);
			x = Math.min(Math.max(10, x), 245);
			int y = MathUtils.random(5, Punk.CHUNKSIZE-5);
			y = Math.min(Math.max(10, y), 245);
			int lSpace = 0, rSpace = 0, tSpace = 0, bSpace = 0;
			
			while (lSpace < 10 && block[x-lSpace][y].blockID == 0)
				lSpace++;
			while (rSpace < 10 && block[x+rSpace][y].blockID == 0)
				rSpace++;
			while (tSpace < 10 && block[x][y+tSpace].blockID == 0)
				tSpace++;
			while (bSpace < 10 && block[x][y-bSpace].blockID == 0)
				bSpace++;
			if (rSpace != 0 && bSpace != 0){
				block[x][y-bSpace+1].set(42, MathUtils.random(3));
				Gdx.app.log("chunk", "spawner made @ "+x+","+y);
			}
		}
	}*/
	
	
	//public Vector2 findBed(int chunkID){
		/*
		BlockLoc tmpLoc = new BlockLoc(0,0);
		//
		
		
		//scan map and look for beds in one pass
		Array<BlockLoc> potentialSites = new Array<BlockLoc>();
		Block lookBlock;
		for (int x = 0; x < Punk.CHUNKSIZE; x++){
			boolean isSolid = true, isLastSolid = true, Last2Solid = true;
			for (int y = Punk.CHUNKSIZE-1; y >2; y--){
				//this goes down
				//check for spawn sites. any solid with 2 air block on top
				//Gdx.app.log("chunk, findbed", "block:"+x+","+y);
	
				
				Last2Solid = isLastSolid;
				isLastSolid = isSolid;
				lookBlock = block[(x<<CB)+y];
				if (lookBlock.blockType ==64){
					isSolid = true;
				} else isSolid = false;
				
				if (isSolid && !isLastSolid && !Last2Solid){
					potentialSites.add(new BlockLoc(x,y+2));
					//Gdx.app.log("chunk, findbed", "bed site:"+x+","+y);
				}
				//if (lookBlock.blockID == 
				
			}
		}//end of loop populating potentialSites 
		//TODO scan for beds and place here
		BlockLoc lookLoc;
		Vector2 ret;
		if (potentialSites.size > 4){
			lookLoc = potentialSites.get(potentialSites.size/2);
			ret = new Vector2(lookLoc.x, lookLoc.y);
		}else{
		ret = new Vector2(128, 160);
		}
		ret.x += chunkID*Punk.CHUNKSIZE;
		//throw new GdxRuntimeException("returning"+ret);
		ret.y = 128;
		return ret;
	}*/
	
	
	public float getThreshold2(int x, int y , int offset, int thickness, float thresh){//
		float bias;
		
		float t = 10;
		
			
			bias = 0;//noise.get2d(x, y, seed, 16)+1;
			bias *= .13f;
		
			float delta = (y-offset-100+t)/t;
			if (y-offset < 100-t){
				delta = 1;
			}else {
				bias = Chunk.noise.get2d(x, y, seed, 16)+1;
				bias *= 3f;
				delta *= 12;
				delta += 1;
			
			}
			bias *= delta;
			//Gdx.app.log(TAG, "y "+(y-offset) + "  delata "+delta +"  bias "+bias);
		
		//bias = 1.62f;
		return thresh-bias;
		//if (y > 70) return thresh-.8f;
		//return thresh-.0f;
	}

	public float getThreshold(int x, int y, int offset, int groundThickness, int smoothness){
		return .0f+getBias(x,y-offset, offset, groundThickness, smoothness);
	}

	public int[] getLandInfo(int temp){
		int  treeType=6, surfaceBlock=6, landBlock=6;
		int treeInterval = 4, treeMax = 22;
		int flowerMeta;
		switch (temp){
		//case 6:
		case 6: //sand on sand
				treeInterval = 4;
				treeMax = 16;
				//treeDensity = .1f;
				treeType = 1;
				surfaceBlock = 5;
				landBlock = 5;
				flowerMeta = MathUtils.random(3)+12;
			break;
		case 5: //sand on dirt
				//treeDensity = .05f;
			 	treeInterval = 4;
			 	treeMax = 16;
				treeType = 1;
				surfaceBlock = 5;
				landBlock = 2;
				flowerMeta = MathUtils.random(3)+12;
			break;
		case 4:
		case 3: 
				//grass on dirt
				//treeDensity = .15f;
				treeType = 1;
				treeInterval = 4;
			 	treeMax = 16;
				surfaceBlock = 3;
				landBlock = 2;
				flowerMeta = MathUtils.random(3)+(MathUtils.randomBoolean()?12:0);
			break;
		case 2: //grass on dirt
				//treeDensity = .05f;
				treeType = 3;
				treeInterval = 4;
			 	treeMax = 16;
				surfaceBlock = 3;
				landBlock = 2;
				flowerMeta = MathUtils.random(3);
			break;
		case 1: //grass
				//treeDensity = .3f;
				
				treeType = 3;
				treeInterval = 4;
			 	treeMax = 16;
				surfaceBlock = 3;
				landBlock = 2;
				flowerMeta = MathUtils.random(7);
			break;
		case 0:	
				//treeDensity = .1f;
			treeInterval = 4;
		 	treeMax = 16;
				treeType = 3;
				surfaceBlock = 3;
				landBlock = 2;
				flowerMeta = MathUtils.random(3)+4;
			break;
		case -1: 
				//treeDensity = .15f;
				treeType = 2;
				treeInterval = 4;
			 	treeMax = 16;
				surfaceBlock = 3;
				landBlock = 2;
				flowerMeta = MathUtils.random(7)+4;
			break;
		case -2: 
				//treeDensity = .15f;
				treeType = 2;
				treeInterval = 4;
			 	treeMax = 16;
				surfaceBlock = 3;
				landBlock = 2;
				flowerMeta = MathUtils.random(3)+8;
			break;
		case -3://gravel! on dirt
				
				//treeDensity = .1f;
				
				treeType = 0;
				treeInterval = 4;
			 	treeMax = 16;
				surfaceBlock = 4;
				landBlock = 2;
				flowerMeta = MathUtils.random(3)+(MathUtils.randomBoolean()?16:8);
			break;
		case -4: //snow on dirt
				//treeDensity = .05f;
				
				treeType = 0;
				treeInterval = 5;
			 	treeMax = 16;
				surfaceBlock = 4;
				landBlock = 2;
				flowerMeta = MathUtils.random(3)+16;
			break;
		default:
		case -5: 
		case -6://snow on snow
				treeInterval = 9;
				treeMax = 30;
				//treeDensity = .05f;
				treeType = 0;
				surfaceBlock = 6; 
				landBlock = 6;
				flowerMeta = MathUtils.random(3)+16;
			break;
		
		}
	
		tmpLandInfo[0] = surfaceBlock;
		tmpLandInfo[1] = landBlock;
		tmpLandInfo[2] = treeType;
		tmpLandInfo[3] = treeInterval;
		tmpLandInfo[4] = treeMax;
		tmpLandInfo[5] = flowerMeta;
		//Gdx.app.log("land info", "type:"+temp+"  treet"+treeType);
	
		return tmpLandInfo;
	}
	private boolean underBlocks;
	public float getBias(int x, int mapY, int offset, int groundThickness, int smoothness){
		skyBlocks = false;
		sideBlocks = false;
		underBlocks = false;
		int y = mapY;
		int w = 60;//fade outside world
		float fillOffset = (int)((Chunk.noise.get2d(x, y, seed, 140)*2));	
		float sinBias = Math.min(1, MathUtils.sin((y+(Chunk.noise.get2d(x,y+offset, seed, 24)*8f*MathUtils.PI))/6f)*.8f+ fillOffset);
		
		
		int dw = Math.abs(x)-wWidth;
		if (dw >= 0){
			sideBlocks = true;
			if (y+offset <= effectiveDepth-w) return -1f;
			if (y+offset <= effectiveDepth ){
				int dy = effectiveDepth+Math.abs(y+offset);
				return Chunk.lerp(sinBias, -1f, (Math.max(dy, dw)/(float)w));
			}
			int ex = (x < 0?-wWidth:wWidth-2);
			if (y+offset > getGroundHeight(ex)-dw) return -1f;
			if (dw > w)
				return -1;
			float delta = (dw)/(float)w;
			//Gdx.app.log("chunk", "delta:"+delta+"x:"+(-wWidth-x));
			return Chunk.lerp(sinBias, -1f, delta);
		}
		
		
		if (y+offset <= -wDepth && y+offset > -wDepth-10){
			float delta = (y+offset+wDepth+10)/10f;
			//underBlocks = true;
			//Gdx.app.log("chunk", "delta:"+delta+"y:"+offset);
			return Chunk.lerp(2.4f, sinBias,delta);
			
		}
		if (y+offset <= -wDepth-10 && y+offset > -wDepth-20){
			float delta = (y+offset+wDepth+20)/10f;
			underBlocks = true;
			//Gdx.app.log("chunk", "delta:"+delta+"y:"+offset);
			return Chunk.lerp( 1, 2.4f, delta);	
		} 
		if (y+offset <= -wDepth-20 && y+offset > -wDepth-40){
			float delta = (y+offset+wDepth+40)/(float)20f;
			//Gdx.app.log("chunk", "delta:"+delta+"y:"+(y+offset-wDepth+20+w));
			underBlocks = true;
			//return -1f;
			return Chunk.lerp(-1f, 1, delta);
			
		}
		else if (y+offset <= -wDepth-40){
			underBlocks = true;
			return -1f;//if (y+offset <=wDepth-20-w-w){
		}
		
		
		
		
		int h = skyIslandsOffset+100;//sky islands height
		//offset = 0;
		//h = 0;
		if (y > h+160){
			skyBlocks = true;
			//return (noise.get2d(x, mapY, seed, 1256)+1)/8f;
		}
		if (y > h+110) {
			skyBlocks = true;
			return -1f;
		}
		if (y > h+90){
			skyBlocks = true;
			return -.8f;
		}
		if (y > h+70){
			skyBlocks = true;
			return -.6f;
		}
		
		if (y > h+50){
			skyBlocks = true;
			return -.4f;
		}
		if (y > h){
			skyBlocks = true;
			//return -.2f;
		}
		//if (y > 100+smoothness+200) skyBlocks = true;
		//if (skyBlocks)Gdx.app.log("chunk", "skyblocks");
		
		
		if (y < 100-smoothness-groundThickness) return sinBias;
		if (y < 100-groundThickness) {
			float delta = 1+((y-100+smoothness)/smoothness);
			//Gdx.app.log("chunk", "delta:"+delta);
			return Chunk.lerp(sinBias,1f,delta); 
		}
		if (y < 100) return 1f;
				
					
		if (y < 100+smoothness){
			float delta = ((y-100f)/smoothness);	
			//Gdx.app.log("chunk", "delta2:"+delta+" sm "+smoothness);
			/*if (smoothness == 0){
				if (y > 100+smoothness+2) return -3f;
				return 3f;
			}*/
			return Chunk.lerp(1,-1f, delta);
		}
		/*if (y < 100+smoothness+smoothness){
			float delta = ((y-100f-smoothness)/(smoothness*1));	
			//Gdx.app.log("chunk", "delta2:"+delta);
			
			return lerp(0,-1f, delta);
		}*/
		
		
		return -1;
		
	}

	public int getGroundHeight(int x){//absolute x val
		if (!PunkMap.openWorld) return -200;
		//Gdx.app.log("chunk", "getting spawn, heightOffset:"+heightOffset+" ground:"+groundThickness+"seed"+seed);
		return 100+getElevation(x)+getSmoothness(x);
	}

	//fade 16 blocks from top
	/*
	
	  	8x8 block
	
	
	
	
	
	
	
	 */
	
	
	public static int getElevation(int x){
		if (x < -wWidth){ return -1000;}
		else if (x >= wWidth){ return -1000;}
		
		return elevCache[x+wWidth];
		//return MathUtils.floor(elevationSpline.getValue(x));
	}

	public int getSmoothness(int x){
		//float sm = smoothnessSpline.getValue(x);
		//if (sm <= -1) //throw new GdxRuntimeException("ack!"+sm+smoothY); 
			//Gdx.app.log("chunk", "sm@"+x+" :"+sm);
		//return Math.abs(MathUtils.floor(sm));
		//if (true)return 1;
		if (x < -wWidth){return 0;}
		else if (x >= wWidth){return 0;}
		
		return smoothCache[x+wWidth];
	}

	//public Block[] aColumn = new Block[Punk.CHUNKSIZE];
	/*public void generateNormalColumn(int x, PunkMap map){//local values
		if (heightID == -10){
			generateColumnLower(x,seed, map);
			return;
		}
		int heightOffset =(int)(noise.get1d(x+chunkOffset, seed, 2000)*100);
		//heightOffset = MathUtils.random(100);
		int bigBumps = (int)(noise.get1d(x+chunkOffset, seed, 200)*30);
		heightOffset += bigBumps;
		int groundThickness = (int)((noise.get1d(x+chunkOffset, seed, 64)+1)*6)+3;//TODO float?
		
		int temp = getTemp(x+chunkOffset,seed);
		tmpLandInfo = getLandInfo(temp);
		int landBlock = tmpLandInfo[1];
		int surfaceBlock = tmpLandInfo[0];
		/*if (heightID > 0){
			landBlock = 10;
			surfaceBlock = 11;
		}
		int treeBlock = tmpLandInfo[2];
		int treeInterval = tmpLandInfo[3];
		int treeThreshold = tmpLandInfo[4];
	
		int groundHeight  = 100-heightOffset-groundThickness;
		int topBlock = (heightID+1)*Punk.CHUNKSIZE;
		boolean needsGrass = (topBlock > groundHeight-30);// && topBlock < groundHeight + 180 );
		boolean needsSun = (topBlock > groundHeight + 30);
		boolean foundSurface = !needsGrass;//yOffset+Punk.CHUNKSIZE > getGroundHeight(x,seed)?false:true;
			for (int y = Punk.CHUNKSIZE-1; y >= 0 ; y--){
				boolean skyBlocks = false;//(y+yOffset+heightOffset > 300);
				float density = noise.get2d(x+chunkOffset,y+yOffset,seed, 16);
				float adjustedThreshold = getThreshold(x+chunkOffset,y+yOffset,heightOffset, groundThickness);
				//Gdx.app.log("chunk", "threshold:"+adjustedThreshold+" thresh2:"+getThreshold2(y+yOffset+heightOffset, adjustedThreshold));
				//Gdx.app.log("chunk", "");
				if (density > adjustedThreshold){
					block[(x<<CB)+y].set(0,0);
					if (skyBlocks) foundSurface = false;
				} else if (density > getThreshold2(y+yOffset,adjustedThreshold)){
					
					if (!foundSurface){
						foundSurface = true;
						//Gdx.app.log("chunk", "found surface");
						map.dayLightUpdateList.addBlock(x+chunkOffset, y+yOffset);
						/*if (x % treeInterval == 0 && XRand.getByte(x+y+chunkOffset) > treeThreshold && y > groundHeight-10){
							block[(x<<CB)+y].set(skyBlocks?11:treeBlock, XRand.get(4, x+chunkOffset));
							if (skyBlocks && y != Punk.CHUNKSIZE-1)block[x][y+1].set(MathUtils.random(33,34), 0);//mushrooms
						} 
						//else if (skyBlocks && XRand.getByte(seed+x+y+y+seed)< -120 && y != Punk.CHUNKSIZE-1) block[x][y+1].set(42,0);//spawners
						else ///*{
							//Gdx.app.log("chunk", "got to the right place");
							block[(x<<CB)+y].set(skyBlocks?11:(needsGrass?surfaceBlock:landBlock), 0);
						}
					} else {
						block[(x<<CB)+y].set(skyBlocks?10:landBlock, 0);
					}
				} else {
					if (!foundSurface){
						foundSurface = true;
						//if (needsGrass)Gdx.app.log("chunk", "found surface1 "+surfaceBlock);
						map.dayLightUpdateList.addBlock(x+chunkOffset, y+yOffset);
						block[(x<<CB)+y].set((needsGrass?surfaceBlock:landBlock),0);
					} else if (XRand.getByte(seed+x+x+y+y+y)< -127){
						block[(x<<CB)+y].set(-10, 0);
					}
					else block[(x<<CB)+y].set(1, 0);
				}
				//unscrub block
				unScrubBlock(map, x, y);
				block[(x<<CB)+y].light = 0;
				//block[(x<<CB)+y].dayLight = (needsSun && !foundSurface)?15:(byte)0;
				if (needsSun){
					if (foundSurface) block[(x<<CB)+y].dayLight = 0;
					else {
						//Gdx.app.log("chunk", "daylight:"+x+","+y);
						block[(x<<CB)+y].dayLight = 15;
					}
					//Gdx.app.log("chunk", "");
				}
				else block[(x<<CB)+y].dayLight = 0;
				
	
		}
		
			
			
		
		
		
	}*/
	/*public void generateColumnLower(int x, int seed, PunkMap map){
		int heightOffset =(int)(noise.get1d(x+chunkOffset, seed, 2000)*100);
		//heightOffset = MathUtils.random(100);
		int bigBumps = (int)(noise.get1d(x+chunkOffset, seed, 200)*30);
		heightOffset += bigBumps;
		//heightOffset += (noise.get1d(x, seed, 200)*4)*heightOffset;//small bumps
		//heightOffset = -50;
		//float temp = noise.get1d(x+chunkOffset, seed, 2048);
		int temp = getTemp(x+chunkOffset,seed);
		//aColumn = block[x];
		int groundThickness = (int)((noise.get1d(x+chunkOffset, seed, 64)+1)*6)+3;//TODO float?
		//Gdx.app.log("chunk", "halls gen£");
		//groundThickness =10;
		tmpLandInfo = getLandInfo(temp);
		int landBlock = tmpLandInfo[1];
		int surfaceBlock = tmpLandInfo[0];
		int treeBlock = tmpLandInfo[2];
		int treeInterval = tmpLandInfo[3];
		int treeThreshold = tmpLandInfo[4];
		boolean foundSurface = false;
		int groundHeight  = 100-heightOffset-groundThickness;
		int topBlock = (heightID+1)*Punk.CHUNKSIZE;
		boolean needsGrass = (topBlock > groundHeight-2 && topBlock < groundHeight + 180 );
		//if (adjustedHeightID >= 0)
			for (int y = Punk.CHUNKSIZE-1; y >= 100 ; y--){
				
				float density = noise.get2d(x+chunkOffset,y+yOffset,seed, 16);
				//////////float adjustedThreshold = getThreshold(x+chunkOffset,y+yOffset,heightOffset, groundThickness);
				adjustedThreshold -= ((Punk.CHUNKSIZE-y)/28f);
				if (density > adjustedThreshold){
					block[(x<<CB)+y].set(0,0);
					
				} else if (density > getThreshold2(y+yOffset+heightOffset,adjustedThreshold)){
					
					block[(x<<CB)+y].set(1, 0);
				} else {
					block[(x<<CB)+y].set(1, 0);
				}
				//unscrub block
				unScrubBlock(map, x, y);
		}
		for (int y = 99; y >= 0 ; y--){
		//corridors
			
			switch (y%8){
			case 0: block[(x<<CB)+y].set(56,0);
			break;
			case 1: if (XRand.getByte(x+y+yOffset*heightOffset) > 110) block[(x<<CB)+y].set(50,0); else block[(x<<CB)+y].set(0,0);
			break;
			default: block[(x<<CB)+y].set(0,0);
			break;
			}
		
		
		float roomNoise = noise.get2d(x/8, y/8, seed, 1);
		if (roomNoise > -.7f){//column
			if (x%8 == 3){//left col
				//Gdx.app.log("chunk", "making left copl");
				switch (y%8){
				
				case 1:block[(x<<CB)+y].set(-9,3);
					break;
				case 9:block[(x<<CB)+y].set(-9,5);
					break;
				case 0:break;
				default:block[(x<<CB)+y].set(-9,0);
				}
			} else if (x%8 == 4){
				switch (y%8){
				
				case 1:block[(x<<CB)+y].set(-9,2);
					break;
				case 9:block[(x<<CB)+y].set(-9,4);
					break;
				case 0:break;
				default:block[(x<<CB)+y].set(-9,1);
				}
			}
		}
		else if (roomNoise > -.85f){//ladder
			if (x % 8 == 4) {
				block[(x<<CB)+y].set(19,0);
				block[x][y+1].set(19,0);
			}
			//block[x][y+1].set(19,0);
		}
		else {//if (roomNoise > -.8f){//stairs
			if (x%8 == y % 8 && x!= 0) {
				block[x-1][y+1].set(60,0);
				block[x][y+1].set(60,0);
			}
		} 
		float density = noise.get2d(x+chunkOffset,y+yOffset,seed, 32);
		if (density > .7f) block[(x<<CB)+y].set(0,0);
		
		unScrubBlock(map, x, y);
		
		}
	
		
		
		
	}*/
	public int getTemp(int x){
		/*float temp = noise.get1d(x, seed, 2048);
		//return 4;
		temp *= 6;
		if (temp< -5)temp = -5;getele
		return (int)(temp);*/
		if (x < -wWidth){return 0;}
		else if (x >= wWidth){return 0;}
		
		return tempCache[x+wWidth];
	}

	public int getGroundThickness(int x){
		return (int)((Chunk.noise.get1d(x, seed, 52)+1.2f)*16)+0;
		//return 1;
	}
	private int getOutsideGroundHeight(int x){
		float adjustedX = x;
		adjustedX = Math.abs(adjustedX);
		if (adjustedX < wWidth) return effectiveDepth - seaGap -oceanDepth;
		else if (adjustedX > wWidth +oceanDepth*2) return effectiveDepth-seaGap + oceanDepth;
		
		adjustedX -= wWidth;
		int height = effectiveDepth-seaGap-(int)( MathUtils.sin((adjustedX/oceanDepth) *.5f* MathUtils.PI+ MathUtils.PI*.5f) * oceanDepth );
		
		return height;//effectiveDepth-seaGap-50;//height;
	}

	private boolean getTreeMask(int i) {
		if (i >= -wWidth && i < wWidth) return treeMask[wWidth+i];
		return false;
	}

	private int getTreeJit23ter(int x, int y, int treeInterval) {
		int tot = 0;
		seededRandom.setSeed(seed+x*21+y*9);
		//r (int i = 0; i < 4;i++)
			tot += seededRandom.nextInt(treeInterval*2);
			//tot += seededRandom.nextInt(treeInterval);
			//tot += XRand.getSeeded(5, seed+x<<5+y);
			//tot += XRand.getSeeded(5, seed+x<<9+y| 21);
			tot += treeInterval;
			//Gdx.app.log(TAG, "jitter"+tot);
		return tot;
	}
	private int getTreeJitter(int x, int y, int treeInterval, int max) {
		return MathUtils.random(treeInterval, max);
	}

	private float getLavaDensity(int x, int y) {
			// TODO Auto-generated method stub
			if (y >0)
				return -3;
	//		if (y > -128) return -.88f;
	//		if (y > -256) return -.97f;
	//		if (y > -384) return -.96f;
	//		if (y > -512) return -.95f;
	//		if (y > 640) return -.94f;
			if (y > -128) return -.98f;
			if (y > -256) return -.97f;
			if (y > -384) return -.96f;
			if (y > -512) return -.95f;
			if (y > 640) return -.94f;
			return -.92f;
		
		}

	@Override
	public boolean setUp(PunkBodies monsterIndex, PunkMap map) {
		switch (size){
		case 0:
			wWidth = 1000;
			surfaceTowns = 2;
			break;
		case 1:
			wWidth = 5000;
			surfaceTowns = 8;
			break;
		case 2:
			wWidth = 15000;
			surfaceTowns = 16;
			break;
		
		}
		setUpFeatures(seed);
		return false;
	}






	@Override
	public void start(Chunk c) {
		lastSpawnedTree = 1;
	}






	@Override
	public boolean finish(PunkMap map, Chunk c) {
		
		generateDoors(map, c);
		generateClouds(map, c);
		return false;
	}





	private void generateClouds(PunkMap map, Chunk c) {
		for (int i = 0, n = c.clouds.size; i<n;i++){
			
			Player.particles.freeCloud(c.clouds.removeIndex(0));
		}
		boolean valid = false;
		SimplexNoise noise = Chunk.noise;
		int y = (c.heightID)*Punk.CHUNKSIZE, x = c.chunkID * Punk.CHUNKSIZE;
		if (y+Punk.CHUNKSIZE*0 > getGroundHeight(x) || y+Punk.CHUNKSIZE*0 > getGroundHeight(x+Punk.CHUNKSIZE-1))valid = true;
		if (valid){
			for (int i = 0; i < Punk.prefs_CloudsPerChunk; i++){
				float cx = noise.get1d(i*4+c.chunkID+c.heightID<<4, c.seed, 1), cy = noise.get1d(i*4+c.chunkID+c.heightID<<4, c.seed+4, 1);
				cx ++;cy++;
				cx/=2f;cy/=2f;
				cx *= Punk.CHUNKSIZE;
				cy *= Punk.CHUNKSIZE;
				c.clouds.add(Player.particles.cloud(cx+x, cy+y, 0));
			}
		}
		
	}


	public int checkForOre(int x, int y){//uses adjusted y
		int oreID = -1;
		if (MathUtils.random(oreChances[0]) == 0 && y < maxHeights[0]) oreID = 0;
		 if (MathUtils.random(oreChances[1]) == 0 && y < maxHeights[1]) oreID = 1;
		 if (MathUtils.random(oreChances[2]) == 0 && y < maxHeights[2]) oreID = 2;
		 if (MathUtils.random(oreChances[3]) == 0 && y < maxHeights[3]) oreID = 3;
		 if (MathUtils.random(oreChances[4]) == 0 && y < maxHeights[4]) oreID = 4;
		 if (MathUtils.random(oreChances[5]) == 0 && y < maxHeights[5]) oreID = 5;
		 if (MathUtils.random(oreChances[6]) == 0 && y < maxHeights[6]) oreID = 6;
		 if (MathUtils.random(oreChances[7]) == 0 && y < maxHeights[7]) oreID = 7;
		//if (oreID != -1)Gdx.app.log("chunk", "drpodit! "+oreID+" y "+y);
		return oreID;
	}


	public void generateElevatedColumn(int x, PunkMap map, Chunk c){//local values
		int chunkOffset = c.xOffset, heightID = c.heightID, yOffset = c.yOffset, chunkID = c.chunkID;
		SimplexNoise noise = Chunk.noise;
		if (heightID == -10){
			//generateColumnLower(x,seed, map);
			//return;
		}
		int landmetas = 3;
		int groundThickness = getGroundThickness(x+chunkOffset);//(int)((noise.get1d(x+chunkOffset, seed, 64)+1)*6)+3;//TODO float?
		int heightOffset = getElevation(x+chunkOffset);
		//Gdx.app.log("chunk", "elevation@ "+x+": "+heightOffset);
		int temp = getTemp(x+chunkOffset);
		int smoothness = getSmoothness(x+chunkOffset);
		tmpLandInfo = getLandInfo(temp);
		int landBlock = tmpLandInfo[1];
		int surfaceBlock = tmpLandInfo[0];
		int treeBlock = 9, treeMeta = tmpLandInfo[2];
		int treeInterval = tmpLandInfo[3];
		int treeMax = tmpLandInfo[4];
		int flowerMeta = tmpLandInfo[5];
		int groundHeight  = getGroundHeight(x+chunkOffset);
		int topBlock = (heightID+1)*Punk.CHUNKSIZE;
		// && topBlock < groundHeight + 180 );
		
		boolean foundFirstSurface = (topBlock < groundHeight);
		boolean foundSurface = false;//!needsGrass;//yOffset+Punk.CHUNKSIZE > getGroundHeight(x,seed)?false:true;
		
		
		
			for (int y = Punk.CHUNKSIZE-1; y >= 0 ; y--){
				boolean needsMush;
				
				needsMush = (noise.get2d(x, y, seed, 3) > .1f
						&& noise.get2d(x, y, seed, 32) > .3f 
						&& y < Punk.CHUNKSIZE-1);
				int 
				mushMeta =  5;//(MathUtils.random(700) > y+yOffset-heightOffset?mushZones[Math.abs(chunkID*2)%8]:mushZonesDeep[Math.abs(chunkID*2)%8]);
				//needsMush = false;
				
				boolean needsGrass = (y+yOffset > groundHeight-10);
				int flowerBlock = 89, mushBlock = 33;
				if (x == 0 || x == Punk.CHUNKSIZE)needsGrass = false; 
				int oreID = checkForOre(x+c.xOffset,y+yOffset-heightOffset-100);
				float density = noise.get2d(x+chunkOffset,y+yOffset,seed, 16);
				float adjustedThreshold = getThreshold(x+chunkOffset,y+yOffset,heightOffset, groundThickness, smoothness);
				if (sideBlocks){
					treeBlock = 0;
					surfaceBlock = 12;
					flowerBlock = 0;
				}
				//Gdx.app.log("chunk", "thresh+9old:"+adjustedThreshold+" thresh2:"+getThreshold2(y+yOffset+heightOffset, adjustedThreshold));
				//Gdx.app.log("chunk", "");
				if (adjustedThreshold > 1){
					c.block[(x<<CB)+y].set(61,0);
					//Gdx.app.log("chunk", "bedrock");
				}
				
				else if (density > adjustedThreshold){
					if (!underBlocks &&  y+yOffset < wLavaDepth && !sideBlocks && y+yOffset < wLavaDepth - noise.get2d(x+chunkOffset, y+yOffset, seed, 30)*30 - 30)
						c.block[(x<<CB)+y].set(53,11);
					else {
						c.block[(x<<CB)+y].set(0,0);
					}
					
					if (foundSurface && sideBlocks) c.block[(x<<CB)+y].set(71, 0);
					if (y != Punk.CHUNKSIZE-1 && underBlocks && (c.block[((x)<<CB)+(y+1)].blockID == 1)) c.block[(x<<CB)+y].set(71, 0);
					foundSurface = false;
					
				} else if (density > getThreshold2(x+chunkOffset,y+yOffset,heightOffset,groundThickness, adjustedThreshold)){
					
					if (!foundSurface){
						if (!foundFirstSurface){
							foundFirstSurface = true;
							//c.spaces.addBlock(x,y);
							map.addDayLightUpdate(x+chunkOffset, y+yOffset);
							//Gdx.app.log(TAG, "space");
						}
						else{
							//c.spaces.addBlock(x,y);
							//Gdx.app.log(TAG, "space");
						}
						foundSurface = true;
						//Gdx.app.log("chunk", "found surface");
						//map.addDayLightUpdate(x+chunkOffset, y+yOffset);
						//Gdx.app.log("chunk", "found surface "+(x+(chunkID*Punk.CHUNKSIZE))+ ", "+heightID+" y "+(y+yOffset) +"  gr "+needsGrass + " 1: "+(y+yOffset) + "   2: "+( groundHeight)+ "- 40");
	
						// block[(x<<CB)+y].set(skyBlocks?11:surfaceBlock, 0);
						c.block[(x<<CB)+y].set(skyBlocks?10:
							(landBlock)
								, MathUtils.random(landmetas));
						
						
						boolean needsPlants;
						 
							needsPlants = (y != Punk.CHUNKSIZE-1 && noise.get2d(x, y, seed, 3) > .502f && noise.get2d(x, y, seed, 64) > .50f);
						//needsPlants = false;
						if (needsGrass){
							c.block[(x<<CB)+y].set(skyBlocks?10:surfaceBlock, 0);
							if (needsPlants){
								c.block[((x)<<CB)+(y+1)].set(flowerBlock, flowerMeta);
								//Gdx.app.log(TAG, "flower");
								//unScrubBlock(map, x, y+1);
							}
								
						} else {
							
							
							if (needsMush){
								c.block[((x)<<CB)+(y+1)].set(mushBlock, mushMeta);
								//Gdx.app.log(TAG, "mush");
								//unScrubBlock(map, x, y+1);
							}
							
						} 
						
						
						
						
						 //boolean needsTree = (y+yOffset-groundHeight-groundThickness < 20);
						if (!skyBlocks && needsGrass && x> lastSpawnedTree && needsGrass && y < Punk.CHUNKSIZE-1){
							
							if (getTreeMask(x+chunkOffset)){
								c.block[((x)<<CB)+(y+1)].set(treeBlock,treeMeta);;
								lastSpawnedTree = x+ getTreeJitter(x,y,treeInterval, treeMax);//MathUtils.random(treeInterval);//jitter
								//Gdx.app.log("chunk", "tree "+lastSpawnedTree+" interval offset "+treeBlock);
							}
							
							
						
						} else if (skyBlocks && y < Punk.CHUNKSIZE-1 && MathUtils.random(32) == 1){
							c.block[((x)<<CB)+(y+1)].set(treeBlock,4);;
							//Gdx.app.log("chunk", "sky tree "+treeBlock);
							
						}
						
						
			
						
						
					} else {
						c.block[(x<<CB)+y].set(skyBlocks?10:landBlock, MathUtils.random(landmetas));
					}
				} else {
					
						if (density < getLavaDensity(x+chunkOffset, y+yOffset)){//-.94f 
							//&& adjustedThreshold >.85f
							//Gdx.app.log("chunk", "lava!"+density);
							c.block[(x<<CB)+y].set(53, 11);
					}else if (oreID != -1){
						c.block[(x<<CB)+y].set(52,oreID);
						//Gdx.app.log("chunk", "deposit"+oreID);
					}else{
						{ 
							c.block[(x<<CB)+y].set(skyBlocks?10:1, 0);
							if (!foundSurface){
								foundFirstSurface = true;
							
								foundSurface = true;

								if (needsMush){
									c.block[((x)<<CB)+(y+1)].set(mushBlock, mushMeta);
									//Gdx.app.log(TAG, "mush2");
									//unScrubBlock(map, x, y+1);
								}
								
							
								
							}
							//Gdx.app.log("chunk", "density "+density+" thresh"+adjustedThreshold);
						}
							
							
							
					}
						
				}
				
				
				//c.block[(x<<CB)+y].light = 0;
				//block[(x<<CB)+y].dayLight = 0;
				//block[(x<<CB)+y].dayLight = (needsSun && !foundSurface)?15:(byte)0;
				if (y+yOffset > groundHeight-20//){
					&&//if (
							c.block[(x<<CB)+y].blockID == 0 && (!foundFirstSurface ||  sideBlocks || underBlocks)) 
						c.block[(x<<CB)+y].setFullSunlight();
					//else c.block[(x<<CB)+y].setDayLightAtCreation((byte) 0);;
					//block[(x<<CB)+y].dayBits = 0;
					//block[(x<<CB)+y].lightBits = 0;
					//else {
						//Gdx.app.log("chunk", "daylight:"+x+","+y);
						//block[(x<<CB)+y].dayLight = 15;         
					
					//Gdx.app.log("chunk", "");
				//}
				else if ((sideBlocks || underBlocks) && c.block[(x<<CB)+y].blockID == 0) 
					c.block[(x<<CB)+y].setFullSunlight(); 
				else if (skyBlocks){
					c.block[(x<<CB)+y].setFullSunlight(); 
				}else
				{
					//block[(x<<CB)+y].dayLight = 0;
					c.block[(x<<CB)+y].setDayLightAtCreation((byte) 0);
					
				}
				
				//unScrubBlock(map, x, y);
				if (!foundFirstSurface) c.blockBG[(x<<CB)+y] = 0;
				else c.blockBG[(x<<CB)+y]=0;
		}
		
		//if (topBlock > groundHeight && topBlock < groundHeight+Punk.CHUNKSIZE) map.addDayLightUpdate(x+c.chunkOffset+Punk.CHUNKSIZE, groundHeight);
		//Gdx.app.log(TAG, "");
			
	
	}






	private void generateWaterColumn(int x, PunkMap map, Chunk c) {
		//Gdx.app.log("chunk", "water column");
		//sin-based heightmap
		SimplexNoise noise = Chunk.noise;
		int rx = x+c.xOffset;
		int height = getOutsideGroundHeight(x+c.xOffset);
		int s0 = 200, s1 = 100, s2 = 50, s3 = 25;
		float roughHeight = noise.get1d(rx, seed, s0)* s0;
		roughHeight += noise.get1d(rx, seed, s1)* s1;
		roughHeight += noise.get1d(rx, seed, s2)* s2;
		roughHeight += noise.get1d(rx, seed, s3)* s3;
	
		roughHeight /= 5;
		
		int sflat = 500;
		float flatHeight = noise.get1d(rx, seed, sflat)* sflat/8;
		
		int blendScale = 300;
		float blend = (MathUtils.clamp(noise.get1d(rx, seed, blendScale)+.5f, 0,1));
		//Gdx.app.log(TAG, "blend"+blend);
		
		height += (int)Chunk.lerp(flatHeight, roughHeight, blend);
		//layers - sand, dirt, stone/ores.
		int layer0 = (int)((noise.get1d(rx, seed, 170)+.65f)*22)
				, layer1 = (int)((noise.get1d(rx, seed+2, 50)+1)*19)
				, layer2 = (int)(noise.get1d(rx, seed+5, 50)*8)
				;
		
		
		
		for (int y = 0; y < Punk.CHUNKSIZE; y++){
			byte lite = 0;
			if (y+c.yOffset > height){
				if (y+c.yOffset < effectiveDepth-seaGap){
					c.block[(x<<CB)+y].set(12, 0);
					//lite = 6;
				}
				else {
					c.block[(x<<CB)+y].set(0,0);
					lite = 15;
				}
			}else if (y+c.yOffset > height -layer0){
				c.block[(x<<CB)+y].set(4,0);
			}
			else if (y+c.yOffset > height -layer0 - layer1)
				c.block[(x<<CB)+y].set(2,0);
			else c.block[(x<<CB)+y].set(1,0);
			
			
			
			if (lite == 0)c.block[(x<<CB)+y].setDayLightAtCreation(lite);
			else c.block[(x<<CB)+y].setFullSunlight();
		}
		
	}






	@Override
	public void generateColumn(int x, PunkMap map, Chunk c) {
		if (x < -wWidth || x > wWidth
|| c.heightID < -(wDepth/Punk.CHUNKSIZE)-1
				) generateWaterColumn(x, map, c);
		else generateElevatedColumn(x, map, c);
		
	}






	@Override
	public PlaneDef loadFromDisk(StringBuilder s) {
		treeMask = new boolean[wWidth*2];

		elevCache = new int[wWidth*2];
		tempCache = new int[wWidth*2];
		smoothCache = new int[wWidth*2];
		
		s.append(name);
		s.append(PlaneDef.suffix);
		FileHandle f = Gdx.files.external(s.toString());
		DataInputStream is = new DataInputStream(new BufferedInputStream(f.read()));
		try {
			wWidth = is.readInt();
			wDepth = is.readInt();
			for (int i = 0; i < wWidth*2; i++)
				elevCache[i] = is.readInt();
			Gdx.app.log(TAG, "read elev");
			for (int i = 0; i < wWidth*2; i++)
				smoothCache[i] = is.readInt();
			Gdx.app.log(TAG, "read sm");
			for (int i = 0; i < wWidth*2; i++)
				tempCache[i] = is.readInt();
			Gdx.app.log(TAG, "read tem");
			for (int i = 0; i < wWidth*2; i++)
				treeMask[i] = is.readBoolean();
			is.close();
			
		} catch (IOException ex){
			Gdx.app.log(TAG, "failed reading caches"+wWidth);
		}
		Gdx.app.log(TAG, "read caches"+s);
		return this;
	}





	
	
	@Override
	public void saveToDisk(StringBuilder s) {
		s.append(name);
		s.append(PlaneDef.suffix);
		FileHandle f = Gdx.files.external(s.toString());
		DataOutputStream os = new DataOutputStream(new BufferedOutputStream(f.write(false)));
		Gdx.app.log(TAG, "saving"+wWidth);
		try {
			os.writeInt(wWidth);
			os.writeInt(wDepth);
			for (int i = 0; i < wWidth*2; i++)
					os.writeInt(elevCache[i]);
			
			for (int i = 0; i < wWidth*2; i++)
				os.writeInt(smoothCache[i]);
			
			for (int i = 0; i < wWidth*2; i++)
				os.writeInt(tempCache[i]);
			
			for (int i = 0; i < wWidth*2; i++)
				os.writeBoolean(treeMask[i]);
			
			os.close();
		} catch (IOException ex){
			Gdx.app.log(TAG, "error saving to disk");
		}
			
	}


	

}


/*if (!foundSurface){
if (!foundFirstSurface)
	foundFirstSurface = true;
else spaces.addBlock(x,y);
foundSurface = true;
//Gdx.app.log("chunk", "found surface1 "+(x+(chunkID*Punk.CHUNKSIZE))+ ", "+heightID+" y "+(y+yOffset) +"  gr "+needsGrass + " 1: "+(y+yOffset) + "   2: "+( groundHeight)+ "- 40");
//map.addDayLightUpdate(x+chunkOffset, y+yOffset);
boolean needsPlants;
if (!needsGrass){
	needsPlants = (y != Punk.CHUNKSIZE-1 && noise.get2d(x, y, seed, 1) > 0 && noise.get2d(x, y, seed, 256) > 0.25f);
}else 
	needsPlants = (y != Punk.CHUNKSIZE-1 && noise.get2d(x, y, seed, 3) > .02f && noise.get2d(x, y, seed, 64) > 0f);
int 
mushMeta =  (MathUtils.random(700) > y+yOffset-heightOffset?mushZones[Math.abs(chunkID*2)%8]:mushZonesDeep[Math.abs(chunkID*2)%8]);
if (needsGrass){
	block[(x<<CB)+y].set(skyBlocks?10:surfaceBlock, 0);
	if (needsPlants){
		block[x][y+1].set(flowerBlock, flowerMeta);
		//unScrubBlock(map, x, y+1);
	}
}else if (needsPlants){
	block[x][y+1].set(mushBlock, mushMeta);
	//unScrubBlock(map, x, y+1);
}
//block[(x<<CB)+y].set(skyBlocks?10:(needsGrass?surfaceBlock:landBlock),0);
block[(x<<CB)+y].set(skyBlocks?10:(needsGrass?surfaceBlock:landBlock), 0);
if (!skyBlocks && x> lastSpawnedTree && needsGrass &&  y < Punk.CHUNKSIZE-2 ){
	
	if (getTreeMask(x+chunkOffset))block[x][y+1].set(treeBlock,0);
	
	
	
	//for (int i = 0; i < 4; i++)
		lastSpawnedTree += getTreeJitter(x,y,treeInterval);//MathUtils.random(treeInterval);//jitter

	//unScrubBlock(map, x, y+1);
	//Gdx.app.log("chunk", "tree "+lastSpawnedTree+" "+x);
	//Gdx.app.log("chunk", "tree created");
} //else block[(x<<CB)+y].set(skyBlocks?11:surfaceBlock, 0);
} else if (oreID != -1){
block[(x<<CB)+y].set(-10,oreID);
}
else*/ 

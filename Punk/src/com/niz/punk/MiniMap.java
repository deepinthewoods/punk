package com.niz.punk;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class MiniMap {
	private Vector2 tmpV = new Vector2();
	SimplexNoise noise = new SimplexNoise();
	public int chunkCount = 48, width = 50 ;
	//private ArrayList<float[]> nearBackgroundVerts = new ArrayList<float[]>(nearChunkCount);
	private float[] vertexCache = new float[chunkCount*4*4];
	private short[] indexCache = new short[chunkCount*9];
	private Mesh mesh;
	public static float zoomOutLimit = 4f;
	private String TAG = "!mini map";
	public float zoom;
	public float parallaxNearRatio = .33f;	
	//int nearChunkCount = 96;
	int nearChunkLength = 16;
	int parrOff = 8, parrHeight = 28, parrScale = 32;
	public Array<Waypoint> waypoints = new Array<Waypoint>();
	public void initIndices(){
		for (int i = 0; i < chunkCount/2; i++){
			int s = i * 18;//start
			short p = (short) (i * 8);//progress
			indexCache[s+0] = (short) (p+0);
			indexCache[s+1] = (short) (p+4);
			indexCache[s+2] = (short) (p+1);
			indexCache[s+3] = (short) (p+5);
			indexCache[s+4] = (short) (p+2);
			indexCache[s+5] = (short) (p+6);
			indexCache[s+6] = (short) (p+3);
			indexCache[s+7] = (short) (p+7);
			indexCache[s+8] = (short) (p+7);
			indexCache[s+9] = (short) (p+11);
			indexCache[s+10] = (short) (p+6);
			indexCache[s+11] = (short) (p+10);
			indexCache[s+12] = (short) (p+5);
			indexCache[s+13] = (short) (p+9);
			indexCache[s+14] = (short) (p+4);
			indexCache[s+15] = (short) (p+8);
			indexCache[s+16] = (short) (p+8);
			indexCache[s+17] = (short) (p+8);
		}
		mesh.setIndices(indexCache);
		mesh.setAutoBind(true);
		//backgroundM.setAutoBind(false);
	}
	static public ShaderProgram createDefaultShader () {
		 String vertexShader = "attribute vec4 a_position;    \n"
			        + "attribute vec4 a_color;\n" //+ "attribute vec2 a_texCoords;\n"
			        + "uniform mat4 u_worldView;\n" + "varying vec4 v_color;"
			       // + "varying vec2 v_texCoords;"
			        + "void main()                  \n"
			        + "{                            \n"
			        + "   v_color = a_color; \n"
			      //  + "   v_texCoords = a_texCoords; \n"
			        + "   gl_Position =  u_worldView * a_position;  \n"
			        + "}                            \n";
			    String fragmentShader = "#ifdef GL_ES\n"
			        + "precision mediump float;\n"
			        + "#endif\n"
			        + "varying vec4 v_color;\n"
			     //   + "varying vec2 v_texCoords;\n"
			     //   + "uniform sampler2D u_texture;\n"
			        + "void main()                                  \n"
			        + "{                                            \n"
			        + "  gl_FragColor = v_color;\n"
			        + "}";

		
		shader = new ShaderProgram(vertexShader, fragmentShader);
		if (shader.isCompiled() == false) throw new IllegalArgumentException("couldn't compile shader: " + shader.getLog());
		return shader;
	}
	static ShaderProgram shader;
	public void render(SpriteBatch batch, OrthographicCamera camera){
		camera.zoom = player.zoomLevel/parallaxNearRatio;
		camera.position.set(player.head.position.x, player.head.position.y, 0);
		
		camera.update();
		
		//camera.apply(gl);
		//find left and right
		//Gdx.graphics.getGL20().glEnable(GL10.GL_BLEND);
	    //Gdx.graphics.getGL20().glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
	    
		shader.begin();
		shader.setUniformMatrix("u_worldView", camera.combined);
		mesh.setVertices(vertexCache);
		mesh.setIndices(indexCache);
		mesh.render(shader, GL20.GL_TRIANGLE_STRIP);//, 0, chunkCount*9-8);
		//mesh.r
		shader.end();
		
		batch.getProjectionMatrix().set(camera.combined);

		batch.begin();
		renderWaypoints(camera, batch);
		batch.end();
	}
	
	public MiniMap(){
		mesh = new Mesh(false, chunkCount*4, chunkCount*9,
				new VertexAttribute(Usage.Position, 3, "a_position"),
				new VertexAttribute(Usage.ColorPacked, 4, "a_color"));
		initIndices();
		
		createDefaultShader();
		mesh.getVertexAttribute(Usage.Position).alias = "a_position";
		mesh.getVertexAttribute(Usage.ColorPacked).alias = "a_color";
	}
	private Color color = new Color();
	
	
	Vector2 normalVector = new Vector2();
	Vector2 point = new Vector2();
	
	
	float s = 255;
	Color cDayL = new Color(155/s,155/s,155/s, 1);
	Color cDayM = new Color(125/s,125/s,125/s,1);
	Color cBlack = new Color(0, 0, 0, 0);
	
	Color cNightL= new Color(45,45,45, 255);
	Color cNightM = new Color(25,25,25,255);
	
	Color effectiveL = new Color(cNightL);
	Color effectiveM = new Color(cNightM);
	
	Color fDayL = new Color(190/s,190/s,190/s, 1);
	Color fDayM = new Color(90/s,90/s,90/s,1);
	Color fBlack = new Color(0.15f, 0.15f, 0.15f, 0);
	
	public void makeMesh(int chunkID, int seed){
		//Gdx.app.log(TAG, "make mesh");
		//if (zoom < zoomOutLimit)
		//	makeMeshNear(chunkID, seed);
		//else makeMeshZoomedOut(chunkID, seed);
		
	}

	public void makeMeshNear(int chunkID, int seed){
		float x = MathUtils.clamp(chunkID*Punk.CHUNKSIZE-Punk.CHUNKSIZE*4-Punk.CHUNKSIZE/2, -Chunk.primeMaterialPlane.wWidth,  Chunk.primeMaterialPlane.wWidth);
		int endX = chunkID*Punk.CHUNKSIZE+Punk.CHUNKSIZE*2;
		
		
		//float[] dest = tmpFVerts;
		
		
		
		
		float[] dest = vertexCache;
		
		
		
		//Gdx.app.log("punk", "mesh: "+x+": "+groundHeight+"offsets:"+peakOffset+", mid:"+midPeakOffset+"prev"+prevPeakOffset);
		//Gdx.app.log("punk", "near mesh width:"+width+", x-w="+(x-width)+"x-w/2="+(x-(width/2f))+"x="+x);

		//Color c = new Color(5,50,5,255);
		
		float c_light = cDayL.toFloatBits();
		float c_med = cDayM.toFloatBits();
		float c_black = Color.toFloatBits(0, 0, 0, 0);
		
		int count = 0;
		while (count < chunkCount){
			int groundHeight  = Chunk.getGroundHeight((int)x);
			normalVector.set((x-20), Chunk.getGroundHeight((int)x-20));
			tmpV.set(normalVector);
			point.set((x+20), Chunk.getGroundHeight((int)x+20));
			
			tmpV.lerp(point, .5f);
			float averageGroundHeight = (tmpV.y);
			

			normalVector.set(x, tmpV.y);
			normalVector.sub(x, Chunk.getGroundHeight((int) x));

			if (normalVector.y < 0)
				normalVector.y *= -1;
			normalVector.nor().mul(.5f);
			
			
			
			//int smallAverageGroundHeight = ((Chunk.getGroundHeight((int)x-2) + Chunk.getGroundHeight((int)x+2))/2);
			parrHeight = Chunk.getSmoothness((int) x);
			float peakOffset = (noise.get1d(x, seed, parrScale));
			peakOffset *= parrHeight;
			peakOffset += parrOff;
			
			normalVector.add(x, groundHeight+peakOffset);
			
			int c = count*16;
			dest[c+0] = x;
			dest[c+1] = Chunk.primeMaterialPlane.effectiveDepth;
			dest[c+2] = 0;
			//dest[c+3] = c_black;
			
			dest[c+4] = x;
			dest[c+5] = averageGroundHeight-32;
			dest[c+6] = 0;
			//dest[c+7] = c_black;
			
//			dest[c+8] = x*parallaxNearRatio;
//			dest[c+9] = groundHeight-1+peakOffset;
//			dest[c+10] = 0;
//			dest[c+11] =  c_med;
			

			dest[c+12] = x;
			dest[c+13] = groundHeight+peakOffset;
			dest[c+14] = 0;
			//dest[c+15] =  c_light;
			
			//second from top
			//tmpV.set(dest[c+13], dest[c+13]);
			
			dest[c+8] = x;
			dest[c+9] = groundHeight+peakOffset-1;
			dest[c+10] = 0;
			//dest[c+11] =  c_med;
			
			x += nearChunkLength;
			count++;
		}
			
		adjustNearLighting(Punk.skyColor);
			
	}
	
	
	public void adjustNearLighting(float l){
		l *= 2f;
		l = MathUtils.clamp(l, .1f, 1f);
		
		effectiveL.set(cDayL);
		effectiveL.mul(l);
		//effectiveL.add(cNightL);
		//effectiveL.mul(.5f);
		effectiveM.set(cDayM);
		effectiveM.mul(l);
		/*float r1 = effectiveM.r += cNightM.r;
		float g1 = cNightM.g;
		float b1 = cNightM.b;
		float r2 = effectiveM.r; 
		float g2 = effectiveM.g;
		float b2 = effectiveM.b;
		float rgb = effectiveM.rgb888((r1+r2)/2, (g1+g2)/2, (b1+b2)/2);*/

		//effectiveM.set((r1+r2)/2, (g1+g2)/2, (b1+b2)/2, 1);
		
		
		float c_light = effectiveL.toFloatBits();
		float c_med = effectiveM.toFloatBits();
		float c_black = cBlack.toFloatBits();
		
		//Gdx.app.log("punk", "changing color:"+l);
		int count = 0;
		while (count < chunkCount){
			int c = count*16;
			vertexCache[c+3] = c_black;
			
		
			vertexCache[c+7] = c_black;
	
			vertexCache[c+15] =  c_light;
			
	
			vertexCache[c+11] =  c_med;
			
			count++;
		}
	}
	boolean out = true;
	/*public boolean checkZoom(float zoomLevel) {
		//Gdx.app.log(TAG, "zoom"+zoomLevel+"  map:"+zoom);
		if (zoomLevel > zoomOutLimit+zoomOutLimit/2){
			if (!out){
				out = true;
				makeMeshZoomedOut(PunkMap.currentChunk, Player.gameInfo.gameSeed);
				
				zoom = Chunk.primeMaterialPlane.wWidth;
				//Gdx.app.log(TAG, "out");
			}
		
			
			
			return true;
		} else {
			if (out){
				out = false;
				zoom = zoomOutLimit-.02f;
				makeMesh(PunkMap.currentChunk, Player.gameInfo.gameSeed);
				//Gdx.app.log(TAG, "in");
				return true;
			}
			
		}
		return false;
	}*/

	public void makeMeshZoomedOut(int seed) {

		float x = -Chunk.primeMaterialPlane.wWidth;
		//int endX = chunkID*Punk.CHUNKSIZE+Punk.CHUNKSIZE*2;
		
		
		//float[] dest = tmpFVerts;
		
		
		
		float[] dest = vertexCache;
		
		
		
		//Gdx.app.log("punk", "mesh: "+x+": "+groundHeight+"offsets:"+peakOffset+", mid:"+midPeakOffset+"prev"+prevPeakOffset);
		//Gdx.app.log("punk", "near mesh width:"+width+", x-w="+(x-width)+"x-w/2="+(x-(width/2f))+"x="+x);

		//Color c = new Color(5,50,5,255);
		
		float c_light = cDayL.toFloatBits();
		float c_med = cDayM.toFloatBits();
		float c_black = Color.toFloatBits(0, 0, 0, 0);
		
		int count = 0;
		while (count < chunkCount){
			int groundHeight  = Chunk.getGroundHeight((int)x);
			normalVector.set((x-20), Chunk.getGroundHeight((int)x-20));
			tmpV.set(normalVector);
			point.set((x+20), Chunk.getGroundHeight((int)x+20));
			
			tmpV.lerp(point, .5f);
			float averageGroundHeight = (tmpV.y);
			

			normalVector.set(x, tmpV.y);
			normalVector.sub(x, Chunk.getGroundHeight((int) x));

			if (normalVector.y < 0)
				normalVector.y *= -1;
			normalVector.nor().mul(.5f);
			
			
			
			//int smallAverageGroundHeight = ((Chunk.getGroundHeight((int)x-2) + Chunk.getGroundHeight((int)x+2))/2);
			float peakOffset = 0;//(noise.get1d(x, seed, parrScale));
			peakOffset *= parrHeight;
			peakOffset += parrOff;
			
			normalVector.add(x, groundHeight+peakOffset);
			
			int c = count*16;
			dest[c+0] = x;
			dest[c+1] = Chunk.primeMaterialPlane.effectiveDepth;
			dest[c+2] = 0;
			dest[c+3] = c_black;
			
			dest[c+4] = x;
			dest[c+5] = averageGroundHeight-32;
			dest[c+6] = 0;
			dest[c+7] = c_black;
			
//			dest[c+8] = x*parallaxNearRatio;
//			dest[c+9] = groundHeight-1+peakOffset;
//			dest[c+10] = 0;
//			dest[c+11] =  c_med;
			

			dest[c+12] = x;
			dest[c+13] = groundHeight+peakOffset;
			dest[c+14] = 0;
			dest[c+15] =  c_light;
			
			//second from top
			//tmpV.set(dest[c+13], dest[c+13]);
			
			dest[c+8] = x;
			dest[c+9] = groundHeight+peakOffset-1;
			dest[c+10] = 0;
			dest[c+11] =  c_med;
			
			x += Chunk.primeMaterialPlane.wWidth*2/chunkCount;
			x = Math.min(x, Chunk.primeMaterialPlane.wWidth);
			count++;
		}
			
		
		adjustNearLighting(Punk.skyColor);

	
		
	}
	private Vector3 v = new Vector3();
	public void renderWaypoints(OrthographicCamera cam, SpriteBatch batch) {
		
		Iterator<Waypoint> i = waypoints.iterator();
		while (i.hasNext()){
			Waypoint w = i.next();
			v.set(w.loc.x, w.loc.y, 0);
			w.draw(cam, batch);
		}
		
	}
	
}

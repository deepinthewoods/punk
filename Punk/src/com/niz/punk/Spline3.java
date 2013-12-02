// Spline.java
//   Functions for setting up and evaluating a cubic interpolatory spline.
//   AUTHORS:   Lawrence Shampine, Richard Allen, Steven Pruess  for 
//              the text  Fundamentals of Numerical Computing
//   DATE:      February 27, 1996
//              minimal change convertion to Java August 11, 2003

package com.niz.punk;

import com.badlogic.gdx.math.Vector2;

public class Spline3{
	

private Vector2[] P;

/**
 * a contructor
 * @param pointsVector 4 points that are required to build the bezier curve
 */
public Spline3(float[] xx, float[] yy)
{
	P = new Vector2[xx.length];
	for (int i = 0; i < xx.length; i++)
		P[i] = new Vector2(xx[i], yy[i]);
  //this.P = pointsVector;
}


/**
 * returns the point in 3d space that corresponds to the given value of t
 * @param t curve's parameter that should be in the range [0, 1.0]
 * @return  the point in 3d space that corresponds to the given value of t
 */
public Vector2 getValue(float t)
{
  if (t > 1.0 || t < 0.0)
  {
    throw new IllegalArgumentException("The value of t is out of range: " + t + " .");
  }
  float one_minus_t = 1 - t;
  Vector2 retValue = new Vector2();
  Vector2[] terms = new Vector2[4];
  terms[0] = calcNewVector(one_minus_t * one_minus_t * one_minus_t, P[0]);
  terms[1] = calcNewVector(3 * one_minus_t * one_minus_t * t, P[1]);
  terms[2] = calcNewVector(3 * one_minus_t * t * t, P[2]);  
  terms[3] = calcNewVector(t * t * t, P[3]);
  for (int i = 0 ; i < 4; i++)
  {
    retValue.add(terms[i]);
  }
  return retValue;
}

/**
 * calculates and returns a new vector that is base * scaler
 * @param scaler
 * @param base
 * @return
 */
private Vector2 tmpV = new Vector2();
private Vector2 calcNewVector(float scaler, Vector2 base)
{
  tmpV.set(base);
  tmpV.mul((float) scaler);
  return tmpV;
}
}
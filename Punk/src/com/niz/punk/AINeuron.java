package com.niz.punk;

public abstract class AINeuron {
public abstract int assess(GenericMob mob);
public abstract boolean move(GenericMob mob);//returns true if it has moved
public abstract int act(GenericMob mob);

}

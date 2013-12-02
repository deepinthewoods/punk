package com.niz.punk;

public class PlayerPermissions {
public boolean canPlaceBlocks,
	canThrow,
	canUseBucket,
	instaHeal;
public boolean hasInfiniteBlocks;

public void setCampaign(){
	canPlaceBlocks = true;
	canThrow = true;
	canUseBucket = true;
	instaHeal = true;
	hasInfiniteBlocks = false;
}
public void setCreative(){
	canPlaceBlocks = true;
	canThrow = true;
	canUseBucket = true;
	instaHeal = true;
	hasInfiniteBlocks = true;
}

}

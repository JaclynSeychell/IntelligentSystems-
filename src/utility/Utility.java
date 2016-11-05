package utility;

import java.util.Random;

import behaviours.DelayBehaviour;

public final class Utility {
	private Utility() {}
	
	private static int cidCnt = 0;
	private static String cidBase ;
	
	public Utility getInstance() {
		return this;
	}
	
	public static String genCID(String r, int hash) { 
		if (cidBase==null) {
			cidBase = r + hash + System.currentTimeMillis() % 10000 + "_";
		}
		return  cidBase + (cidCnt++); 
	}
	
	public static Random newRandom(int hash) {	
		return new Random(hash + System.currentTimeMillis()); 
	}
}

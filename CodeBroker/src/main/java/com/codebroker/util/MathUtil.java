package com.codebroker.util;

import java.util.Random;

public class MathUtil {
	final static Random random=new Random();

	public static int random(int round){
		return random.nextInt(round);
	}
}

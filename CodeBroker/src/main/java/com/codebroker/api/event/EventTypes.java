package com.codebroker.api.event;

public class EventTypes {
	public final static String KEY="et"; 
	public final static String TOPIC="e"; 
	private static int index = 0;
	/**
	 * 格子广播
	 */
	public static final int GRID_BROAD_CAST = index++;
	/**
	 * 区域广播
	 */
	public static final int AREA_BROAD_CAST = index++;
	/**
	 * 世界广播
	 */
	public static final int WORLD_BROAD_CAST = index++;
	
	/**
	 * 区域玩家进入
	 */
	public static final String AREA_USER_ENTER_AREA = "auean";
}

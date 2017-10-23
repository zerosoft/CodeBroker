package com.avic.sever.game.handler;

import java.lang.reflect.Field;

public class CommandID {
	
	public static int index=1;
	
	public static int LOGIN=1;
	
	public static int ENTER_ZONE=2;
	
	public static int CREATE_ROOM=3;
	
	public static int REMOVE_ROOM=4;
	
	public static int GET_ALL_ROOM=5;
	
	public static int JOIN_ROOM=6;
	
	public static int LEAVE_ROOM=7;
	
	public static int WORD_CHAT_SEND=8;
	
	public static int WORD_CHAT_RECIVE=9;
	
	public static int ROOM_CHAT_SEND=10;
	
	public static int ROOM_CHAT_RECIVE=11;
	
	public static void main(String[] args) {
		Field[] declaredFields = CommandID.class.getDeclaredFields();
		for (Field field : declaredFields) {
			System.out.println(field.getName()+field.toString());
		}
	}
}

package com.codebroker.setting;

public enum SystemMessageType {

	SERVER_READY(0), USER_LOGIN(1), USER_LOGOUT(2), USER_DISCONNECT(3), USER_RECONNECTION_TRY(
			4), USER_RECONNECTION_SUCCESS(5),

	USER_JOIN_WORLD(6);

	public final int id;

	private SystemMessageType(int id) {
		this.id = id;
	}

	public static SystemMessageType get(int id) {
		SystemMessageType[] tsArr = values();
		for (SystemMessageType systemMessageType : tsArr) {
			if (systemMessageType.id == id) {
				return systemMessageType;
			}
		}
		return tsArr[id];
	}

}

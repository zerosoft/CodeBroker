package com.codebroker.setting;

public enum RoomRemoveMode {
	DEFAULT, WHEN_EMPTY, WHEN_EMPTY_AND_CREATOR_IS_GONE, NEVER_REMOVE;

	public static RoomRemoveMode fromString(String id) {
		return valueOf(id.toUpperCase());
	}
}

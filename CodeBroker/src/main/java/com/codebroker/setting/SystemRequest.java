package com.codebroker.setting;

public enum SystemRequest {
    SERVER_READY(0),
    USER_LOGIN_PB(1),
    USER_LOGOUT(2),
    USER_DISCONNECT(3),
    USER_RECONNECTION_TRY(4),
    USER_RECONNECTION_SUCCESS(5),

    USER_LOGIN_JSON(6),

    USER_JOIN_WORLD(7);

    public final int id;

    private SystemRequest(int id) {
        this.id = id;
    }

    public static SystemRequest get(int id) {
        SystemRequest[] tsArr = values();
        for (SystemRequest systemMessageType : tsArr) {
            if (systemMessageType.id == id) {
                return systemMessageType;
            }
        }
        return tsArr[id];
    }

}

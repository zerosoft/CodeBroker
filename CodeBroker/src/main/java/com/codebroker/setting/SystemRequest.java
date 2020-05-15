package com.codebroker.setting;

public enum SystemRequest {
    USER_LOGIN_OR_REGISTER(1),
    USER_LOGOUT(2);



    public final int id;

    SystemRequest(int id) {
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

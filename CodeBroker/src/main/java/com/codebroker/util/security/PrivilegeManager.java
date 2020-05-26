package com.codebroker.util.security;

public interface PrivilegeManager {
    boolean isActive();

    void setActive(boolean paramBoolean);

    void setPermissionProfile(PermissionProfile paramPermissionProfile);

    void removePermissionProfile(short paramShort);

    void removePermissionProfile(String paramString);

    boolean containsPermissionProfile(short paramShort);

    boolean containsPermissionProfile(String paramString);

    PermissionProfile getPermissionProfile(short paramShort);

    PermissionProfile getPermissionProfile(String paramString);

}

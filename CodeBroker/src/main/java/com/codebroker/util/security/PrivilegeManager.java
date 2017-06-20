package com.codebroker.util.security;

public interface PrivilegeManager {
	public boolean isActive();

	public void setActive(boolean paramBoolean);

	public void setPermissionProfile(PermissionProfile paramPermissionProfile);

	public void removePermissionProfile(short paramShort);

	public void removePermissionProfile(String paramString);

	public boolean containsPermissionProfile(short paramShort);

	public boolean containsPermissionProfile(String paramString);

	public PermissionProfile getPermissionProfile(short paramShort);

	public PermissionProfile getPermissionProfile(String paramString);

}

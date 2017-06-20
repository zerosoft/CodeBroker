package com.codebroker.api.manager;
/**
 * 用户管理器
 * @author xl
 *
 */

import java.util.List;

import com.codebroker.api.IUser;

public interface IUserManager {

	public IUser createUser(boolean npc) throws Exception;

	public void removeUser(String userId);

	public List<IUser> getPlayerUser() throws Exception;

	public List<IUser> getNPCUser() throws Exception;

	public List<IUser> getAllUser() throws Exception;
}

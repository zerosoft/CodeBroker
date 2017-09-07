package com.codebroker.api.manager;
/**
 * 用户管理器
 * @author xl
 *
 */

import java.util.List;

import com.codebroker.api.IUser;

public interface IUserManager {

	public void createUser(boolean npc) throws Exception;

	public void removeUser(String userId);

	public IUser getPlayerUser(String userId) throws Exception;

	public List<IUser> getAllUser() throws Exception;
}

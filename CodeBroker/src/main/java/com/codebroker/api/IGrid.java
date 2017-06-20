package com.codebroker.api;

import java.util.Collection;

import com.codebroker.api.event.IEventDispatcher;

/**
 * 区域中的格子
 * 
 * @author ZERO
 *
 */
public interface IGrid  extends IEventDispatcher{
	/**
	 * 获取格子ID
	 * @return
	 * @throws Exception
	 */
	public String getId() throws Exception;
	/**
	 * 加入格子
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public boolean enterGrid(IUser user) throws Exception;
	/**
	 * 离开格子
	 * @param userID
	 */
	public void leaveGrid(String userID);
	/**
	 * 对指定组的用户进行广播
	 * @param jsonString
	 * @param users
	 */
	public void broadCastUsers(String jsonString, Collection<IUser> users);
	/**
	 * 广播当前格子中的所有用户
	 * @param jsonString
	 */
	public void broadCastAllUser(String jsonString);
	/**
	 * 销毁格子
	 */
	public void destroy();
}

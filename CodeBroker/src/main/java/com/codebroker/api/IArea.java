package com.codebroker.api;

import java.util.Collection;
import java.util.List;

import com.codebroker.api.event.Event;
import com.codebroker.api.event.IEventDispatcher;

/**
 * 区域
 * 
 * @author xl
 *
 */
public interface IArea extends IEventDispatcher {
	/**
	 * 获取区域ID
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getId() throws Exception;

	/**
	 * 进入区域
	 * 
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public void enterArea(IUser user) throws Exception;

	/**
	 * 离开区域
	 * 
	 * @param userID
	 */
	public void leaveArea(String userID);

	/**
	 * 创建一个格子
	 * 
	 * @param gridId
	 * @return
	 * @throws Exception
	 */
	public void createGrid(String gridId) throws Exception;

	/**
	 * 删除一个格子
	 * 
	 * @param gridId
	 */
	public void removeGridById(String gridId);

	/**
	 * 根据id获得一个格子
	 * 
	 * @param gridId
	 * @return
	 * @throws Exception
	 */
	public IGrid getGridById(String gridId) throws Exception;

	/**
	 * 获得区域内所有格子
	 * 
	 * @return
	 * @throws Exception
	 */
	public Collection<IGrid> getAllGrid() throws Exception;

	/**
	 * 获得当前区域里的所有玩家
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<IUser> getPlayers() throws Exception;

	/**
	 * 销毁区域
	 */
	public void destroy();

	/**
	 * 广播当前区域所有用户
	 * 
	 * @param jsonString
	 */
	public void broadCastAllUser(Event object);

	/**
	 * 对指定组的用户进行广播
	 * 
	 * @param jsonString
	 * @param users
	 */
	public void broadCastUsers(Event object, Collection<IUser> users);
}

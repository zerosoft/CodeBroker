package com.codebroker.api;

import java.util.Collection;
import java.util.List;

import com.codebroker.api.event.IEventDispatcher;
import com.codebroker.core.data.IObject;

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
	 * 创建一个NPC
	 * 
	 * @return
	 * @throws Exception
	 */
	public IUser createNPC() throws Exception;

	/**
	 * 销毁一个NPC
	 * 
	 * @param npcId
	 */
	public void removeNPC(String npcId);

	/**
	 * 进入区域
	 * 
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public boolean enterArea(IUser user) throws Exception;

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
	public IGrid createGrid(String gridId) throws Exception;

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
	public void broadCastAllUser(IObject object);

	/**
	 * 对指定组的用户进行广播
	 * 
	 * @param jsonString
	 * @param users
	 */
	public void broadCastUsers(IObject object, Collection<IUser> users);
}

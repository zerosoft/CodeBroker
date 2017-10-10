package com.codebroker.api;

import com.codebroker.api.event.IEventDispatcher;
import com.codebroker.core.data.IObject;

/**
 * 用户操作接口
 * 
 * @author xl
 *
 */
public interface IUser extends IEventDispatcher {
	/**
	 * 获得用户id
	 * @return
	 * @throws Exception
	 */
	public String getUserId() throws Exception;
	
	/**
	 * 坚持这个用户是不是NPC
	 * @return
	 * @throws Exception
	 */
	public boolean isNpc() throws Exception;
	/**
	 * 发送消息给IO会话
	 * @param requestId
	 * @param message
	 */
	public void sendMessageToIoSession(int requestId, Object message);
	/**
	 * 主动断开链接
	 */
	public void disconnect();
	/**
	 * 会话是否连通网络
	 * @return
	 */
	public boolean isConnected();
	/**
	 * 获取IObject
	 * @return
	 */
	public IObject getIObject();

}

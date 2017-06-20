package com.codebroker.api;

import com.codebroker.api.event.IEventDispatcher;

/**
 * 用户操作接口
 * 
 * @author xl
 *
 */
public interface IUser extends IEventDispatcher{

	public String getName() throws Exception;

	public String getUserId() throws Exception;

	public boolean isNpc() throws Exception;

	public void sendMessage(int requestId, Object message);

	public void disconnect();

	public boolean isConnected();

}

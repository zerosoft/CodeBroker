package com.codebroker.api;


import com.codebroker.api.event.IEvent;
import com.codebroker.api.internal.IService;

import java.util.Optional;

public interface IGameWorld {

	/**
	 * 查找本地在线玩家
	 * @param id
	 * @return
	 */
	Optional<IGameUser> findIGameUserById(String id);

	/**
	 * 创建一个全局服务，如果开启集群则成为集群服务
	 * @param service
	 * @return
	 */
	boolean createGlobalService(IService service);
	/**
	 * 对所有在线玩家发送消息
	 * @param requestId
	 * @param message
	 */
	void sendAllOnlineUserMessage(int requestId, Object message);

	/**
	 * 对所有在线玩家发送玩家事件
	 * @param event
	 */
	void sendAllOnlineUserEvent(IEvent event);
}

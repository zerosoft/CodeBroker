package com.codebroker.api;


import com.codebroker.api.event.IEvent;
import com.codebroker.api.internal.IService;
import com.codebroker.core.data.IObject;

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
	boolean createGlobalService(String serviceName,IService service);

	/**
	 * 创建一个集群服务
	 * @param serviceName
	 * @param service
	 * @return
	 */
	IService getClusterService(String serviceName,IService service);
	/**
	 * 发送事件到服务
	 * @param serviceName 服务名称
	 * @param object 事件对象
	 */
	void sendMessageToService(String serviceName, IObject object);
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

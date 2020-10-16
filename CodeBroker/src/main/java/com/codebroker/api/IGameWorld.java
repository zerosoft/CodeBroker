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
	 * 创建一个全局服务，如果开启集群则成为集群服务 需要给服务类增加注解 IServerType
	 * @param serviceName 指定名称
	 * @param service
	 * @return
	 */
	boolean createService(String serviceName, IService service);
	/**
	 * 创建一个全局服务，如果开启集群则成为集群服务 需要给服务类增加注解 IServerType
	 * 使用 IService.getName() 作为 serviceName
	 * @param service
	 * @return
	 */
	boolean createService(IService service);


	Optional<IObject> sendMessageToLocalIService(String serviceName, IObject message);
	/**
	 * 发消息给本地服务
	 * @param iService
	 * @param message
	 * @return
	 */
	Optional<IObject> sendMessageToLocalIService(Class iService, IObject message);

	/**
	 * 发送消息到服务
	 * @param serviceName
	 * @param message
	 */
	void sendMessageToIService(String serviceName, IObject message);

	void sendMessageToIService(Class iService, IObject message);
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

	/**
	 * 服务重启启动
	 */
	void restart();
}

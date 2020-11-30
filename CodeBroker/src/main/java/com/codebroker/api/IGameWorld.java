package com.codebroker.api;


import com.codebroker.api.event.IEvent;
import com.codebroker.api.internal.IPacket;
import com.codebroker.api.internal.IResultStatusMessage;
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
	 * 创建一个全局服务，如果开启集群则成为集群服务 需要给服务类增加注解 IServerType
	 * @param serviceName 指定名称
	 * @param service
	 * @return
	 */
	boolean createService(String serviceName, IService  service);
	/**
	 * 创建一个全局服务，如果开启集群则成为集群服务 需要给服务类增加注解 IServerType
	 * 使用 IService.getName() 作为 serviceName
	 * @param service
	 * @return
	 */
	boolean createService(IService  service);

	/**
	 * 创建一个集群服务，如果开启集群则成为集群服务 需要给服务类增加注解 IServerType
	 * @param serviceName 指定名称
	 * @param service
	 * @return
	 */
	boolean createClusterService(String serviceName, IService  service);
	/**
	 * 创建一个集群服务，如果开启集群则成为集群服务 需要给服务类增加注解 IServerType
	 * 使用 IService.getName() 作为 serviceName
	 * @param service
	 * @return
	 */
	boolean createClusterService(IService  service);


	IResultStatusMessage sendMessageToLocalIService(String serviceName, IPacket message);
	/**
	 * 发消息给集群服务
	 * @param iService
	 * @param message
	 * @return
	 */
	IResultStatusMessage sendMessageToClusterIService(Class iService, IPacket message);

	IResultStatusMessage sendMessageToClusterIService(String serviceName, IPacket message);
	/**
	 * 发消息给本地服务
	 * @param iService
	 * @param message
	 * @return
	 */
	IResultStatusMessage sendMessageToLocalIService(Class iService, IPacket message);

	/**
	 * 发送消息到服务
	 * @param serviceName
	 * @param message
	 */
	void sendMessageToIService(String serviceName, IPacket message);

	void sendMessageToIService(Class iService, IPacket message);
	/**
	 * 对所有在线玩家发送消息
	 * @param requestId
	 * @param message
	 */
	void sendAllOnlineUserMessage(int requestId, IPacket message);

	/**
	 * 对所有在线玩家发送玩家消息
	 * @param message
	 */
	void sendAllOnlineUserIPacket(IPacket message);

	/**
	 * 服务重启启动
	 */
	void restart();
}

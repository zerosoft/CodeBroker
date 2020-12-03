package com.codebroker.util.zookeeper;

import akka.cluster.Member;
import org.apache.curator.framework.CuratorFramework;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface IClusterServiceRegister {
	/**
	 * 注册服务
	 * @param serviceFullName 类的全名class.name
	 * @param sid			  服务的唯一id
	 * @param ip			  IP地址
	 * @param port			  端口
	 */
	void registerService(String serviceFullName,String sid,String ip,int port);

	/**
	 * 获得已缓存服务列表
	 * @param serviceFullName
	 * @return
	 */
	Optional<Collection<String>> getCacheService(String serviceFullName);

	/**
	 * 注册服务
	 * @param sid			  服务的唯一id
	 * @param ip			  IP地址
	 * @param port			  端口
	 */
	void registerServer(long sid, String ip, int port, String dateCenter, Set<String> roles);
	/**
	 * 获得已缓存服务列表 ip地址加端口
	 * 127.0.0.1:1122
	 * @param dateCenter
	 * @return
	 */
	Optional<Collection<String>> getCacheServer(String dateCenter);

	/**
	 * 获得链接客户端，以备日后使用分布式锁
	 * InterProcessMutex mutex = new InterProcessMutex(client,"/path");
	 * // or any InterProcessLock
	 * try ( Locker locker = new Locker(mutex, 5, TimeUnit.MINUTES) )
	 *     {
	 * 			// do work
	 *     }
	 * @return
	 */
	CuratorFramework getCuratorFramework();
	/**
	 * 增加一个服务节点
	 * @param member
	 */
	void addNewMember(Member member);
	/**
	 * 失去一个服务节点
	 * @param member
	 */
	void lostMember(Member member);

}

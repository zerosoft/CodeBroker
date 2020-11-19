package com.codebroker.util.zookeeper;

import akka.actor.Address;
import akka.actor.typed.javadsl.Behaviors;
import akka.cluster.Member;
import com.codebroker.cluster.ClusterListenerActor;
import com.codebroker.cluster.ServerOnline;
import com.codebroker.core.actortype.ActorPathService;
import com.codebroker.util.zookeeper.curator.CuratorZookeeperClient;
import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.*;
import java.util.stream.Collectors;

public class ZookeeperClusterServiceRegister implements IClusterServiceRegister {

	private CuratorZookeeperClient curatorZookeeperClient;

	private static final String ROOT_PATH="/CodeBroker";
	private static final String SERVICE_PATH="/Service";
	private static final String SERVER_PATH="/Server";
	private Map<String, List<ServiceInfo>> serverNameMap= Maps.newConcurrentMap();
	private Set<MemberInfo> members= Sets.newConcurrentHashSet();


	DataListener serverDataListener;
	DataListener serviceDataListener;

	public ZookeeperClusterServiceRegister(CuratorZookeeperClient curatorZookeeperClient) {
		this.curatorZookeeperClient = curatorZookeeperClient;
		serverDataListener=new ServerDataListener(this);
		serviceDataListener=new ServiceDataListener(this);
	}

	@Override
	public void registerService(String serviceFullName, String sid, String ip, int port) {
		if (curatorZookeeperClient.checkExists(ROOT_PATH)){
			if (curatorZookeeperClient.checkExists(ROOT_PATH+SERVICE_PATH)){
				registerEphemeralService(serviceFullName, sid, ip, port);
			}else {
				curatorZookeeperClient.createPersistent(ROOT_PATH+SERVICE_PATH);
				registerEphemeralService(serviceFullName, sid, ip, port);
			}
		}else {
			curatorZookeeperClient.createPersistent(ROOT_PATH);
			curatorZookeeperClient.createPersistent(ROOT_PATH+SERVICE_PATH);
			registerEphemeralService(serviceFullName, sid, ip, port);
		}
	}

	private void registerEphemeralService(String serviceFullName, String sid, String ip, int port) {
		ServiceInfo serviceInfo = new ServiceInfo(serviceFullName, sid, ip, port);
		if (!curatorZookeeperClient.checkExists(ROOT_PATH + SERVICE_PATH + "/" + serviceFullName)) {
			curatorZookeeperClient.createPersistent(ROOT_PATH + SERVICE_PATH + "/" + serviceFullName);
		}
		curatorZookeeperClient.create(ROOT_PATH + SERVICE_PATH + "/" + serviceFullName+"/"+serviceInfo.toString(), serviceInfo.toString(), true);
		curatorZookeeperClient.addDataListener(ROOT_PATH + SERVICE_PATH + "/" + serviceFullName,serviceDataListener);
	}


	private void registerEphemeralServer(long sid,String dateCenter, String host, int port, Set<String> roles) {
		MemberInfo memberInfo=new MemberInfo(sid,host,port,dateCenter,roles);
		String path = ROOT_PATH + SERVER_PATH + "/" + dateCenter;
		if (!curatorZookeeperClient.checkExists(path)) {
			curatorZookeeperClient.createPersistent(path);
		}
		curatorZookeeperClient.create(ROOT_PATH + SERVER_PATH + "/" + dateCenter+"/"+memberInfo.toString(), memberInfo.toString(), true);
		curatorZookeeperClient.addDataListener(path,serverDataListener);
	}


	@Override
	public Optional<Collection<String>> getCacheService(String serviceFullName) {
		if (serverNameMap.containsKey(serviceFullName)){
			List<String> collect = serverNameMap.get(serviceFullName).stream().map(serviceInfo -> serviceInfo.ip + ":" + serviceInfo.port).collect(Collectors.toList());
			return Optional.ofNullable(collect);
		}
		return Optional.empty();
	}

	@Override
	public void registerServer(long sid, String ip, int port, String dateCenter, Set<String> roles) {
		if (curatorZookeeperClient.checkExists(ROOT_PATH)){
			if (!curatorZookeeperClient.checkExists(ROOT_PATH+SERVER_PATH)){
				curatorZookeeperClient.createPersistent(ROOT_PATH+SERVER_PATH);
			}
		}else {
			curatorZookeeperClient.createPersistent(ROOT_PATH);
			curatorZookeeperClient.createPersistent(ROOT_PATH+SERVER_PATH);
		}
		registerEphemeralServer(sid,dateCenter, ip, port,roles);
	}

	@Override
	public Optional<Collection<String>> getCacheServer(String dateCenter) {
		return Optional.empty();
	}

	@Override
	public void addNewMember(Member member) {
		long longUid = member.uniqueAddress().longUid();
		Address address = member.address();
		String host = "";
		int port = 0;
		if (address.getHost().isPresent()){
			host=address.getHost().get();
		}
		if (address.getPort().isPresent()){
			port=address.getPort().get();
		}
		String dataCenter = member.dataCenter();
		Set<String> roles = member.getRoles();

		MemberInfo memberInfo=new MemberInfo(longUid,host,port,dataCenter,roles);
		members.add(memberInfo);
	}

	@Override
	public void lostMember(Member member) {
		for (MemberInfo memberInfo : members) {
			if (memberInfo.longUid==member.uniqueAddress().longUid()){
				members.remove(memberInfo);
				break;
			}
		}
	}

	protected List<String> getChildren(String path){
		List<String> children = curatorZookeeperClient.getChildren(path.substring(0,path.lastIndexOf("/")));
		return children;
	}


	public void changeServiceData(String fullClassName, String path) {
		List<String> children = getChildren(path);
		List<ServiceInfo> collect = children.stream().map(da -> ServiceInfo.buildServiceInfo(da)).collect(Collectors.toList());
		serverNameMap.put(fullClassName,collect);
	}

	public void changeServerData(String path) {
		List<String> children = getChildren(path);
		List<MemberInfo> collect = children.stream().map(da -> MemberInfo.buildMemberInfo(da)).collect(Collectors.toList());
		members.clear();
		members.addAll(collect);
		//本地的
		Collection<Member> values = ActorPathService.clusterService.values();
		for (MemberInfo value : collect) {
			boolean has=false;
			for (Member memberInfo : values) {
				Address address = memberInfo.uniqueAddress().address();
				String host =address.getHost().get();
				int port =address.getPort().get();
				if (value.ip.equals(host)&&port==value.port){
					has=true;
					break;
				}
			}
			if (!has){
				ServerOnline serverOnline = new ServerOnline(value.ip, value.port, value.dataCenter, value.role);
				ActorPathService.clusterDomainEventActorRef.tell(serverOnline);
			}
		}
	}
}

class ServiceInfo{
	public final String serviceFullName;
	public final String sid;
	public final String ip;
	public final int port;

	public ServiceInfo(String serviceFullName, String sid, String ip, int port) {
		this.serviceFullName = serviceFullName;
		this.sid = sid;
		this.ip = ip;
		this.port = port;
	}
	//TODO maybe has bug
	@Override
	public String toString() {
		return serviceFullName+":"+sid+":"+ip+":"+port;
	}
	//TODO maybe has bug
	public static ServiceInfo buildServiceInfo(String info){
		String[] split = info.split(":");
		ServiceInfo serviceInfo=new ServiceInfo(split[0],split[1],split[2],Integer.parseInt(split[3]));
		return serviceInfo;
	}
}
class MemberInfo{
	public final long longUid;
	public final String ip;
	public final int port;
	public final String dataCenter;
	public final Set<String> role;

	public MemberInfo(long longUid, String ip, int port, String dataCenter, Set<String> role) {
		this.longUid = longUid;
		this.ip = ip;
		this.port = port;
		this.dataCenter = dataCenter;
		this.role = role;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MemberInfo that = (MemberInfo) o;
		return longUid == that.longUid &&
				port == that.port &&
				Objects.equal(ip, that.ip) &&
				Objects.equal(dataCenter, that.dataCenter);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(longUid, ip, port, dataCenter, role);
	}
	//TODO maybe has bug
	@Override
	public String toString() {
		return longUid+":"+ip+":"+port+":"+dataCenter+":"+role.stream().collect(Collectors.joining( ","));
	}

	//TODO maybe has bug
	public static MemberInfo buildMemberInfo(String info){
		String[] split = info.split(":");
		String[] split1 = split[4].split(",");
		List<String> strings = Arrays.asList(split1);
		Set<String> roles=Sets.newConcurrentHashSet();
		roles.addAll(strings);
		MemberInfo serviceInfo=new MemberInfo(Long.valueOf(split[0]),split[1],Integer.parseInt(split[2]),split[3],roles);
		return serviceInfo;
	}
}

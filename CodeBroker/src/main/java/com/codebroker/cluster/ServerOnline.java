package com.codebroker.cluster;

import akka.cluster.ClusterEvent;

import java.util.Set;

public class ServerOnline implements ClusterEvent.ClusterDomainEvent{

	public final  String host;
	public final  Integer port;
	public final  String dataCenter;
	public final Set<String> roles;

	public ServerOnline(String host, Integer port, String dataCenter, Set<String> roles) {
		this.host = host;
		this.port = port;
		this.dataCenter = dataCenter;
		this.roles = roles;
	}
}
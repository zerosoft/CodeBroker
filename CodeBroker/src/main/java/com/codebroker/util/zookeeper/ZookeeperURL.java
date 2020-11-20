package com.codebroker.util.zookeeper;


import java.net.*;
import java.util.regex.Pattern;

public class ZookeeperURL {

	private static final Pattern LOCAL_IP_PATTERN = Pattern.compile("127(\\.\\d{1,3}){3}$");
	static String LOCALHOST_KEY = "localhost";
	String ANYHOST_VALUE = "0.0.0.0";

	private final String protocol;
	private final String host;
	private final int port;

	private volatile transient String ip;

	protected ZookeeperURL() {
		this.protocol = null;
		this.host = null;
		this.port = 0;
	}

	public ZookeeperURL(String protocol, String host, int port) {
		this.protocol = protocol;
		this.host = host;
		this.port = (port < 0 ? 0 : port);
	}

	public String getProtocol() {
		return protocol;
	}

	public ZookeeperURL setProtocol(String protocol) {
		return new ZookeeperURL(protocol, host, port);
	}

	public String getHost() {
		return host;
	}

	public ZookeeperURL setHost(String host) {
		return new ZookeeperURL(protocol, host, port);
	}

	public String getIp() {
		if (ip == null) {
			ip = getIpByHost(host);
		}
		return ip;
	}

	public String getIpByHost(String hostName) {
		try {
			return InetAddress.getByName(hostName).getHostAddress();
		} catch (UnknownHostException e) {
			return hostName;
		}
	}

	public int getPort() {
		return port;
	}

	public ZookeeperURL setPort(int port) {
		return new ZookeeperURL(protocol, host, port);
	}

	public int getPort(int defaultPort) {
		return port <= 0 ? defaultPort : port;
	}

	public String getAddress() {
		return port <= 0 ? host : host + ":" + port;
	}

	public ZookeeperURL setAddress(String address) {
		int i = address.lastIndexOf(':');
		String host;
		int port = this.port;
		if (i >= 0) {
			host = address.substring(0, i);
			port = Integer.parseInt(address.substring(i + 1));
		} else {
			host = address;
		}
		return new ZookeeperURL(protocol, host, port);
	}

	public boolean isLocalHost() {
		return host != null
				&& (LOCAL_IP_PATTERN.matcher(host).matches()
				|| host.equalsIgnoreCase(LOCALHOST_KEY));
	}

	public boolean isAnyHost() {
		return ANYHOST_VALUE.equals(host);
	}

	public java.net.URL toJavaURL() {
		try {
			return new java.net.URL(toString());
		} catch (MalformedURLException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public InetSocketAddress toInetSocketAddress() {
		return new InetSocketAddress(host, port);
	}

	public String getZookeeperAddress() {
		return host + ":" + port;
	}
}

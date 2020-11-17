package com.codebroker.util.zookeeper;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URL {

	String DEFAULT_KEY_PREFIX = "default.";
	private static final Pattern LOCAL_IP_PATTERN = Pattern.compile("127(\\.\\d{1,3}){3}$");
	private static final Pattern KVP_PATTERN = Pattern.compile("([_.a-zA-Z0-9][-_.a-zA-Z0-9]*)[=](.*)");
	Pattern COMMA_SPLIT_PATTERN = Pattern.compile("\\s*[,]+\\s*");
	static String LOCALHOST_KEY = "localhost";
	String LOCALHOST_VALUE = "127.0.0.1";
	String INTERFACE_KEY = "interface";
	String VERSION_KEY = "version";
	String GROUP_KEY = "group";
	String ANYHOST_KEY = "anyhost";
	String ANYHOST_VALUE = "0.0.0.0";
	String BACKUP_KEY = "backup";

	private final String protocol;

	private final String username;

	private final String password;

	private final String host;
	private final int port;

	private final String path;

	private final Map<String, String> parameters;

	private volatile transient String ip;

	private volatile transient String full;

	private volatile transient String identity;

	private volatile transient String parameter;

	private volatile transient String string;

	protected URL() {
		this.protocol = null;
		this.username = null;
		this.password = null;
		this.host = null;
		this.port = 0;
		this.path = null;
		this.parameters = null;
	}

	public URL(String protocol, String host, int port) {
		this(protocol, null, null, host, port, null, (Map<String, String>) null);
	}

	public URL(String protocol, String host, int port, Map<String, String> parameters) {
		this(protocol, null, null, host, port, null, parameters);
	}

	public URL(String protocol, String host, int port, String path) {
		this(protocol, null, null, host, port, path, (Map<String, String>) null);
	}

	public URL(String protocol, String host, int port, String path, String... pairs) {
		this(protocol, null, null, host, port, path, toStringMap(pairs));
	}

	public URL(String protocol, String host, int port, String path, Map<String, String> parameters) {
		this(protocol, null, null, host, port, path, parameters);
	}

	public URL(String protocol, String username, String password, String host, int port, String path) {
		this(protocol, username, password, host, port, path, (Map<String, String>) null);
	}

	public URL(String protocol, String username, String password, String host, int port, String path, String... pairs) {
		this(protocol, username, password, host, port, path, toStringMap(pairs));
	}

	public URL(String protocol, String username, String password, String host, int port, String path, Map<String, String> parameters) {
		if (StringUtils.isEmpty(username)
				&& StringUtils.isNotEmpty(password)) {
			throw new IllegalArgumentException("Invalid url, password without username!");
		}
		this.protocol = protocol;
		this.username = username;
		this.password = password;
		this.host = host;
		this.port = (port < 0 ? 0 : port);
		// trim the beginning "/"
		while (path != null && path.startsWith("/")) {
			path = path.substring(1);
		}
		this.path = path;
		if (parameters == null) {
			parameters = new HashMap<>();
		} else {
			parameters = new HashMap<>(parameters);
		}
		this.parameters = Collections.unmodifiableMap(parameters);
	}


	public static Map<String, String> toStringMap(String... pairs) {
		Map<String, String> parameters = new HashMap<>();
		if (pairs.length > 0) {
			if (pairs.length % 2 != 0) {
				throw new IllegalArgumentException("pairs must be even.");
			}
			for (int i = 0; i < pairs.length; i = i + 2) {
				parameters.put(pairs[i], pairs[i + 1]);
			}
		}
		return parameters;
	}
	/**
	 * Parse url string
	 *
	 * @param url URL string
	 * @return URL instance
	 * @see URL
	 */
	public static URL valueOf(String url) {
		if (url == null || (url = url.trim()).length() == 0) {
			throw new IllegalArgumentException("url == null");
		}
		String protocol = null;
		String username = null;
		String password = null;
		String host = null;
		int port = 0;
		String path = null;
		Map<String, String> parameters = null;
		int i = url.indexOf("?"); // separator between body and parameters
		if (i >= 0) {
			String[] parts = url.substring(i + 1).split("&");
			parameters = new HashMap<>();
			for (String part : parts) {
				part = part.trim();
				if (part.length() > 0) {
					int j = part.indexOf('=');
					if (j >= 0) {
						parameters.put(part.substring(0, j), part.substring(j + 1));
					} else {
						parameters.put(part, part);
					}
				}
			}
			url = url.substring(0, i);
		}
		i = url.indexOf("://");
		if (i >= 0) {
			if (i == 0) {
				throw new IllegalStateException("url missing protocol: \"" + url + "\"");
			}
			protocol = url.substring(0, i);
			url = url.substring(i + 3);
		} else {
			// case: file:/path/to/file.txt
			i = url.indexOf(":/");
			if (i >= 0) {
				if (i == 0) {
					throw new IllegalStateException("url missing protocol: \"" + url + "\"");
				}
				protocol = url.substring(0, i);
				url = url.substring(i + 1);
			}
		}

		i = url.indexOf("/");
		if (i >= 0) {
			path = url.substring(i + 1);
			url = url.substring(0, i);
		}
		i = url.lastIndexOf("@");
		if (i >= 0) {
			username = url.substring(0, i);
			int j = username.indexOf(":");
			if (j >= 0) {
				password = username.substring(j + 1);
				username = username.substring(0, j);
			}
			url = url.substring(i + 1);
		}
		i = url.lastIndexOf(":");
		if (i >= 0 && i < url.length() - 1) {
			if (url.lastIndexOf("%") > i) {
				// ipv6 address with scope id
				// e.g. fe80:0:0:0:894:aeec:f37d:23e1%en0
				// see https://howdoesinternetwork.com/2013/ipv6-zone-id
				// ignore
			} else {
				port = Integer.parseInt(url.substring(i + 1));
				url = url.substring(0, i);
			}
		}
		if (url.length() > 0) {
			host = url;
		}
		return new URL(protocol, username, password, host, port, path, parameters);
	}

	public static URL valueOf(String url, String... reserveParams) {
		URL result = valueOf(url);
		if (reserveParams == null || reserveParams.length == 0) {
			return result;
		}
		Map<String, String> newMap = new HashMap<>(reserveParams.length);
		Map<String, String> oldMap = result.getParameters();
		for (String reserveParam : reserveParams) {
			String tmp = oldMap.get(reserveParam);
			if (StringUtils.isNotEmpty(tmp)) {
				newMap.put(reserveParam, tmp);
			}
		}
		return result.clearParameters().addParameters(newMap);
	}

	public static URL valueOf(URL url, String[] reserveParams, String[] reserveParamPrefixs) {
		Map<String, String> newMap = new HashMap<>();
		Map<String, String> oldMap = url.getParameters();
		if (reserveParamPrefixs != null && reserveParamPrefixs.length != 0) {
			for (Map.Entry<String, String> entry : oldMap.entrySet()) {
				for (String reserveParamPrefix : reserveParamPrefixs) {
					if (entry.getKey().startsWith(reserveParamPrefix) && StringUtils.isNotEmpty(entry.getValue())) {
						newMap.put(entry.getKey(), entry.getValue());
					}
				}
			}
		}

		if (reserveParams != null) {
			for (String reserveParam : reserveParams) {
				String tmp = oldMap.get(reserveParam);
				if (StringUtils.isNotEmpty(tmp)) {
					newMap.put(reserveParam, tmp);
				}
			}
		}
		return newMap.isEmpty() ? new URL(url.getProtocol(), url.getUsername(), url.getPassword(), url.getHost(), url.getPort(), url.getPath())
				: new URL(url.getProtocol(), url.getUsername(), url.getPassword(), url.getHost(), url.getPort(), url.getPath(), newMap);
	}

	public static String encode(String value) {
		if (StringUtils.isEmpty(value)) {
			return "";
		}
		try {
			return URLEncoder.encode(value, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static String decode(String value) {
		if (StringUtils.isEmpty(value)) {
			return "";
		}
		try {
			return URLDecoder.decode(value, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public String getProtocol() {
		return protocol;
	}

	public URL setProtocol(String protocol) {
		return new URL(protocol, username, password, host, port, path, getParameters());
	}

	public String getUsername() {
		return username;
	}

	public URL setUsername(String username) {
		return new URL(protocol, username, password, host, port, path, getParameters());
	}

	public String getPassword() {
		return password;
	}

	public URL setPassword(String password) {
		return new URL(protocol, username, password, host, port, path, getParameters());
	}

	public String getAuthority() {
		if (StringUtils.isEmpty(username)
				&& StringUtils.isEmpty(password)) {
			return null;
		}
		return (username == null ? "" : username)
				+ ":" + (password == null ? "" : password);
	}

	public String getHost() {
		return host;
	}

	public URL setHost(String host) {
		return new URL(protocol, username, password, host, port, path, getParameters());
	}

	/**
	 * Fetch IP address for this URL.
	 * <p>
	 * Pls. note that IP should be used instead of Host when to compare with socket's address or to search in a map
	 * which use address as its key.
	 *
	 * @return ip in string format
	 */
	public String getIp() {
		if (ip == null) {
			ip = getIpByHost(host);
		}
		return ip;
	}

	public static String getIpByHost(String hostName) {
		try {
			return InetAddress.getByName(hostName).getHostAddress();
		} catch (UnknownHostException e) {
			return hostName;
		}
	}

	public int getPort() {
		return port;
	}

	public URL setPort(int port) {
		return new URL(protocol, username, password, host, port, path, getParameters());
	}

	public int getPort(int defaultPort) {
		return port <= 0 ? defaultPort : port;
	}

	public String getAddress() {
		return port <= 0 ? host : host + ":" + port;
	}

	public URL setAddress(String address) {
		int i = address.lastIndexOf(':');
		String host;
		int port = this.port;
		if (i >= 0) {
			host = address.substring(0, i);
			port = Integer.parseInt(address.substring(i + 1));
		} else {
			host = address;
		}
		return new URL(protocol, username, password, host, port, path, getParameters());
	}


	static String appendDefaultPort(String address, int defaultPort) {
		if (address != null && address.length() > 0 && defaultPort > 0) {
			int i = address.indexOf(':');
			if (i < 0) {
				return address + ":" + defaultPort;
			} else if (Integer.parseInt(address.substring(i + 1)) == 0) {
				return address.substring(0, i + 1) + defaultPort;
			}
		}
		return address;
	}

	public String getPath() {
		return path;
	}

	public URL setPath(String path) {
		return new URL(protocol, username, password, host, port, path, getParameters());
	}

	public String getAbsolutePath() {
		if (path != null && !path.startsWith("/")) {
			return "/" + path;
		}
		return path;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public String getParameterAndDecoded(String key) {
		return getParameterAndDecoded(key, null);
	}

	public String getParameterAndDecoded(String key, String defaultValue) {
		return decode(getParameter(key, defaultValue));
	}

	public String getParameter(String key) {
		String value = parameters.get(key);
		return StringUtils.isEmpty(value) ? parameters.get(DEFAULT_KEY_PREFIX + key) : value;
	}

	public String getParameter(String key, String defaultValue) {
		String value = getParameter(key);
		return StringUtils.isEmpty(value) ? defaultValue : value;
	}

	public String[] getParameter(String key, String[] defaultValue) {
		String value = getParameter(key);
		return StringUtils.isEmpty(value) ? defaultValue : COMMA_SPLIT_PATTERN.split(value);
	}

	public List<String> getParameter(String key, List<String> defaultValue) {
		String value = getParameter(key);
		if (value == null || value.length() == 0) {
			return defaultValue;
		}
		String[] strArray = COMMA_SPLIT_PATTERN.split(value);
		return Arrays.asList(strArray);
	}

	public char getParameter(String key, char defaultValue) {
		String value = getParameter(key);
		return StringUtils.isEmpty(value) ? defaultValue : value.charAt(0);
	}

	public boolean getParameter(String key, boolean defaultValue) {
		String value = getParameter(key);
		return StringUtils.isEmpty(value) ? defaultValue : Boolean.parseBoolean(value);
	}

	public boolean hasParameter(String key) {
		String value = getParameter(key);
		return value != null && value.length() > 0;
	}

	public String getMethodParameterAndDecoded(String method, String key) {
		return URL.decode(getMethodParameter(method, key));
	}

	public String getMethodParameterAndDecoded(String method, String key, String defaultValue) {
		return URL.decode(getMethodParameter(method, key, defaultValue));
	}

	public String getMethodParameter(String method, String key) {
		String value = parameters.get(method + "." + key);
		return StringUtils.isEmpty(value) ? getParameter(key) : value;
	}

	public String getMethodParameter(String method, String key, String defaultValue) {
		String value = getMethodParameter(method, key);
		return StringUtils.isEmpty(value) ? defaultValue : value;
	}


	public char getMethodParameter(String method, String key, char defaultValue) {
		String value = getMethodParameter(method, key);
		return StringUtils.isEmpty(value) ? defaultValue : value.charAt(0);
	}

	public boolean getMethodParameter(String method, String key, boolean defaultValue) {
		String value = getMethodParameter(method, key);
		return StringUtils.isEmpty(value) ? defaultValue : Boolean.parseBoolean(value);
	}

	public boolean hasMethodParameter(String method, String key) {
		if (method == null) {
			String suffix = "." + key;
			for (String fullKey : parameters.keySet()) {
				if (fullKey.endsWith(suffix)) {
					return true;
				}
			}
			return false;
		}
		if (key == null) {
			String prefix = method + ".";
			for (String fullKey : parameters.keySet()) {
				if (fullKey.startsWith(prefix)) {
					return true;
				}
			}
			return false;
		}
		String value = getMethodParameter(method, key);
		return value != null && value.length() > 0;
	}

	public boolean isLocalHost() {
		return isLocalHost(host) || getParameter(LOCALHOST_KEY, false);
	}

	public static boolean isLocalHost(String host) {
		return host != null
				&& (LOCAL_IP_PATTERN.matcher(host).matches()
				|| host.equalsIgnoreCase(LOCALHOST_KEY));
	}


	public boolean isAnyHost() {
		return ANYHOST_VALUE.equals(host) || getParameter(ANYHOST_KEY, false);
	}

	public URL addParameterAndEncoded(String key, String value) {
		if (StringUtils.isEmpty(value)) {
			return this;
		}
		return addParameter(key, encode(value));
	}

	public URL addParameter(String key, boolean value) {
		return addParameter(key, String.valueOf(value));
	}

	public URL addParameter(String key, char value) {
		return addParameter(key, String.valueOf(value));
	}

	public URL addParameter(String key, byte value) {
		return addParameter(key, String.valueOf(value));
	}

	public URL addParameter(String key, short value) {
		return addParameter(key, String.valueOf(value));
	}

	public URL addParameter(String key, int value) {
		return addParameter(key, String.valueOf(value));
	}

	public URL addParameter(String key, long value) {
		return addParameter(key, String.valueOf(value));
	}

	public URL addParameter(String key, float value) {
		return addParameter(key, String.valueOf(value));
	}

	public URL addParameter(String key, double value) {
		return addParameter(key, String.valueOf(value));
	}

	public URL addParameter(String key, Enum<?> value) {
		if (value == null) {
			return this;
		}
		return addParameter(key, String.valueOf(value));
	}

	public URL addParameter(String key, Number value) {
		if (value == null) {
			return this;
		}
		return addParameter(key, String.valueOf(value));
	}

	public URL addParameter(String key, CharSequence value) {
		if (value == null || value.length() == 0) {
			return this;
		}
		return addParameter(key, String.valueOf(value));
	}

	public URL addParameter(String key, String value) {
		if (StringUtils.isEmpty(key)
				|| StringUtils.isEmpty(value)) {
			return this;
		}
		// if value doesn't change, return immediately
		if (value.equals(getParameters().get(key))) { // value != null
			return this;
		}

		Map<String, String> map = new HashMap<>(getParameters());
		map.put(key, value);
		return new URL(protocol, username, password, host, port, path, map);
	}

	public URL addParameterIfAbsent(String key, String value) {
		if (StringUtils.isEmpty(key)
				|| StringUtils.isEmpty(value)) {
			return this;
		}
		if (hasParameter(key)) {
			return this;
		}
		Map<String, String> map = new HashMap<>(getParameters());
		map.put(key, value);
		return new URL(protocol, username, password, host, port, path, map);
	}

	/**
	 * Add parameters to a new url.
	 *
	 * @param parameters parameters in key-value pairs
	 * @return A new URL
	 */
	public URL addParameters(Map<String, String> parameters) {
		if (isEmptyMap(parameters)) {
			return this;
		}

		boolean hasAndEqual = true;
		for (Map.Entry<String, String> entry : parameters.entrySet()) {
			String value = getParameters().get(entry.getKey());
			if (value == null) {
				if (entry.getValue() != null) {
					hasAndEqual = false;
					break;
				}
			} else {
				if (!value.equals(entry.getValue())) {
					hasAndEqual = false;
					break;
				}
			}
		}
		// return immediately if there's no change
		if (hasAndEqual) {
			return this;
		}

		Map<String, String> map = new HashMap<>(getParameters());
		map.putAll(parameters);
		return new URL(protocol, username, password, host, port, path, map);
	}

	public static boolean isEmptyMap(Map map) {
		return map == null || map.size() == 0;
	}

	public URL addParametersIfAbsent(Map<String, String> parameters) {
		if (isEmptyMap(parameters)) {
			return this;
		}
		Map<String, String> map = new HashMap<>(parameters);
		map.putAll(getParameters());
		return new URL(protocol, username, password, host, port, path, map);
	}

	public URL addParameters(String... pairs) {
		if (pairs == null || pairs.length == 0) {
			return this;
		}
		if (pairs.length % 2 != 0) {
			throw new IllegalArgumentException("Map pairs can not be odd number.");
		}
		Map<String, String> map = new HashMap<>();
		int len = pairs.length / 2;
		for (int i = 0; i < len; i++) {
			map.put(pairs[2 * i], pairs[2 * i + 1]);
		}
		return addParameters(map);
	}

	public URL addParameterString(String query) {
		if (StringUtils.isEmpty(query)) {
			return this;
		}
		return addParameters(parseQueryString(query));
	}

	public URL removeParameter(String key) {
		if (StringUtils.isEmpty(key)) {
			return this;
		}
		return removeParameters(key);
	}

	public URL removeParameters(Collection<String> keys) {
		if (isEmpty(keys)) {
			return this;
		}
		return removeParameters(keys.toArray(new String[0]));
	}

	public URL removeParameters(String... keys) {
		if (keys == null || keys.length == 0) {
			return this;
		}
		Map<String, String> map = new HashMap<>(getParameters());
		for (String key : keys) {
			map.remove(key);
		}
		if (map.size() == getParameters().size()) {
			return this;
		}
		return new URL(protocol, username, password, host, port, path, map);
	}

	public URL clearParameters() {
		return new URL(protocol, username, password, host, port, path, new HashMap<>());
	}

	public String getRawParameter(String key) {
		if (PROTOCOL_KEY.equals(key)) {
			return protocol;
		}
		if (USERNAME_KEY.equals(key)) {
			return username;
		}
		if (PASSWORD_KEY.equals(key)) {
			return password;
		}
		if (HOST_KEY.equals(key)) {
			return host;
		}
		if (PORT_KEY.equals(key)) {
			return String.valueOf(port);
		}
		if (PATH_KEY.equals(key)) {
			return path;
		}
		return getParameter(key);
	}

	String PROTOCOL_KEY = "protocol";
	String USERNAME_KEY = "username";
	String PASSWORD_KEY = "password";
	String HOST_KEY = "host";
	String PORT_KEY = "port";
	String PATH_KEY = "path";

	public Map<String, String> toMap() {
		Map<String, String> map = new HashMap<>(parameters);
		if (protocol != null) {
			map.put(PROTOCOL_KEY, protocol);
		}
		if (username != null) {
			map.put(USERNAME_KEY, username);
		}
		if (password != null) {
			map.put(PASSWORD_KEY, password);
		}
		if (host != null) {
			map.put(HOST_KEY, host);
		}
		if (port > 0) {
			map.put(PORT_KEY, String.valueOf(port));
		}
		if (path != null) {
			map.put(PATH_KEY, path);
		}
		return map;
	}

	@Override
	public String toString() {
		if (string != null) {
			return string;
		}
		return string = buildString(false, true); // no show username and password
	}

	public String toString(String... parameters) {
		return buildString(false, true, parameters); // no show username and password
	}

	public String toIdentityString() {
		if (identity != null) {
			return identity;
		}
		return identity = buildString(true, false); // only return identity message, see the method "equals" and "hashCode"
	}

	public String toIdentityString(String... parameters) {
		return buildString(true, false, parameters); // only return identity message, see the method "equals" and "hashCode"
	}

	public String toFullString() {
		if (full != null) {
			return full;
		}
		return full = buildString(true, true);
	}

	public String toFullString(String... parameters) {
		return buildString(true, true, parameters);
	}

	public String toParameterString() {
		if (parameter != null) {
			return parameter;
		}
		return parameter = toParameterString(new String[0]);
	}

	public String toParameterString(String... parameters) {
		StringBuilder buf = new StringBuilder();
		buildParameters(buf, false, parameters);
		return buf.toString();
	}

	private void buildParameters(StringBuilder buf, boolean concat, String[] parameters) {
		if (isNotEmptyMap(getParameters())) {
			List<String> includes = (ArrayUtils.isEmpty(parameters) ? null : Arrays.asList(parameters));
			boolean first = true;
			for (Map.Entry<String, String> entry : new TreeMap<>(getParameters()).entrySet()) {
				if (entry.getKey() != null && entry.getKey().length() > 0
						&& (includes == null || includes.contains(entry.getKey()))) {
					if (first) {
						if (concat) {
							buf.append("?");
						}
						first = false;
					} else {
						buf.append("&");
					}
					buf.append(entry.getKey());
					buf.append("=");
					buf.append(entry.getValue() == null ? "" : entry.getValue().trim());
				}
			}
		}
	}

	private String buildString(boolean appendUser, boolean appendParameter, String... parameters) {
		return buildString(appendUser, appendParameter, false, false, parameters);
	}

	private String buildString(boolean appendUser, boolean appendParameter, boolean useIP, boolean useService, String... parameters) {
		StringBuilder buf = new StringBuilder();
		if (StringUtils.isNotEmpty(protocol)) {
			buf.append(protocol);
			buf.append("://");
		}
		if (appendUser && StringUtils.isNotEmpty(username)) {
			buf.append(username);
			if (password != null && password.length() > 0) {
				buf.append(":");
				buf.append(password);
			}
			buf.append("@");
		}
		String host;
		if (useIP) {
			host = getIp();
		} else {
			host = getHost();
		}
		if (host != null && host.length() > 0) {
			buf.append(host);
			if (port > 0) {
				buf.append(":");
				buf.append(port);
			}
		}
		String path;
		if (useService) {
			path = getServiceKey();
		} else {
			path = getPath();
		}
		if (path != null && path.length() > 0) {
			buf.append("/");
			buf.append(path);
		}

		if (appendParameter) {
			buildParameters(buf, true, parameters);
		}
		return buf.toString();
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

	/**
	 * The format is "{interface}:[version]:[group]"
	 * @return
	 */
	public String getColonSeparatedKey() {
		StringBuilder serviceNameBuilder = new StringBuilder();
		append(serviceNameBuilder, INTERFACE_KEY, true);
		append(serviceNameBuilder, VERSION_KEY, false);
		append(serviceNameBuilder, GROUP_KEY, false);
		return serviceNameBuilder.toString();
	}


	private void append(StringBuilder target, String parameterName, boolean first) {
		String parameterValue = this.getParameter(parameterName);
		if (!StringUtils.isBlank(parameterValue)) {
			if (!first) {
				target.append(":");
			}
			target.append(parameterValue);
		} else {
			target.append(":");
		}
	}

	/**
	 * The format of return value is '{group}/{interfaceName}:{version}'
	 * @return
	 */
	public String getServiceKey() {
		String inf = getServiceInterface();
		if (inf == null) {
			return null;
		}
		return buildKey(inf, getParameter(GROUP_KEY), getParameter(VERSION_KEY));
	}

	/**
	 * The format of return value is '{group}/{path/interfaceName}:{version}'
	 * @return
	 */
	public String getPathKey() {
		String inf = StringUtils.isNotEmpty(path) ? path : getServiceInterface();
		if (inf == null) {
			return null;
		}
		return buildKey(inf, getParameter(GROUP_KEY), getParameter(VERSION_KEY));
	}

	public static String buildKey(String path, String group, String version) {
		StringBuilder buf = new StringBuilder();
		if (group != null && group.length() > 0) {
			buf.append(group).append("/");
		}
		buf.append(path);
		if (version != null && version.length() > 0) {
			buf.append(":").append(version);
		}
		return buf.toString();
	}

	public String toServiceStringWithoutResolving() {
		return buildString(true, false, false, true);
	}

	public String toServiceString() {
		return buildString(true, false, true, true);
	}


	public String getServiceInterface() {
		return getParameter(INTERFACE_KEY, path);
	}

	public URL setServiceInterface(String service) {
		return addParameter(INTERFACE_KEY, service);
	}




	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + port;
		result = prime * result + ((protocol == null) ? 0 : protocol.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		URL other = (URL) obj;
		if (host == null) {
			if (other.host != null) {
				return false;
			}
		} else if (!host.equals(other.host)) {
			return false;
		}
		if (parameters == null) {
			if (other.parameters != null) {
				return false;
			}
		} else if (!parameters.equals(other.parameters)) {
			return false;
		}
		if (password == null) {
			if (other.password != null) {
				return false;
			}
		} else if (!password.equals(other.password)) {
			return false;
		}
		if (path == null) {
			if (other.path != null) {
				return false;
			}
		} else if (!path.equals(other.path)) {
			return false;
		}
		if (port != other.port) {
			return false;
		}
		if (protocol == null) {
			if (other.protocol != null) {
				return false;
			}
		} else if (!protocol.equals(other.protocol)) {
			return false;
		}
		if (username == null) {
			if (other.username != null) {
				return false;
			}
		} else if (!username.equals(other.username)) {
			return false;
		}
		return true;
	}

	public static boolean isNotEmptyMap(Map map) {
		return !isEmptyMap(map);
	}

	public static boolean isEmpty(Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}

	public static boolean isNotEmpty(Collection<?> collection) {
		return !isEmpty(collection);
	}

	private static Map<String, String> parseKeyValuePair(String str, String itemSeparator) {
		String[] tmp = str.split(itemSeparator);
		Map<String, String> map = new HashMap<String, String>(tmp.length);
		for (int i = 0; i < tmp.length; i++) {
			Matcher matcher = KVP_PATTERN.matcher(tmp[i]);
			if (!matcher.matches()) {
				continue;
			}
			map.put(matcher.group(1), matcher.group(2));
		}
		return map;
	}

	public static boolean isEmpty(String str) {
		return str == null || str.isEmpty();
	}

	public static String getQueryStringValue(String qs, String key) {
		Map<String, String> map = parseQueryString(qs);
		return map.get(key);
	}

	public static Map<String, String> parseQueryString(String qs) {
		if (isEmpty(qs)) {
			return new HashMap<String, String>();
		}
		return parseKeyValuePair(qs, "\\&");
	}


	public String getBackupAddress() {
		return getBackupAddress(0);
	}

	public String getBackupAddress(int defaultPort) {
		StringBuilder address = new StringBuilder(appendDefaultPort(getAddress(), defaultPort));
		String[] backups = getParameter(BACKUP_KEY, new String[0]);
		if (ArrayUtils.isNotEmpty(backups)) {
			for (String backup : backups) {
				address.append(",");
				address.append(appendDefaultPort(backup, defaultPort));
			}
		}
		return address.toString();
	}

}

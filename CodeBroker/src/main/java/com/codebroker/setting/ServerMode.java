package com.codebroker.setting;

import java.util.EnumSet;

// TODO: Auto-generated Javadoc

/**
 * 服务器状态的枚举.
 *
 * @author zero
 */
public enum ServerMode {

	/** The unknow. */
	UNKNOW("UNKNOW", 0, false),

	/** 单服务器模式. */
	SERVER_TYPE_SINGLE("SINGLE", 1, true),

	/** 逻辑服务器模式. */
	SERVER_TYPE_SHARE("GAME", 2, true);

	/** The enums. */
	public static EnumSet<ServerMode> enums = EnumSet.allOf(ServerMode.class);

	/**
	 * Instantiates a new avalon server mode.
	 *
	 * @param name
	 *            the name
	 */
	private ServerMode(String name, int type, boolean hasAppListener) {
		this.modeName = name;
		this.type = type;
		this.hasAppListener = hasAppListener;
	}

	/** The mode name. */
	public final String modeName;

	public final int type;

	public final boolean hasAppListener;

	/**
	 * Gets the sever mode.
	 *
	 * @param modelName
	 *            the model name
	 * @return the sever mode
	 */
	public static ServerMode getSeverMode(String modelName) {
		for (ServerMode iterable_element : enums) {
			if (modelName.equals(iterable_element.modeName)) {
				return iterable_element;
			}
		}
		return SERVER_TYPE_SINGLE;
	}

	public static ServerMode getSeverMode(int type) {
		for (ServerMode iterable_element : enums) {
			if (type == iterable_element.type) {
				return iterable_element;
			}
		}
		return SERVER_TYPE_SINGLE;
	}

}

package com.codebroker.setting;

/**
 * 房间事件配置
 * 
 * @author xl
 *
 */
public enum RoomSettings {
	/**
	 * 房间名称的改变
	 */
	ROOM_NAME_CHANGE,
	/**
	 * 密码状态的变化
	 */
	PASSWORD_STATE_CHANGE,
	/**
	 * 公共信息
	 */
	PUBLIC_MESSAGES,
	/**
	 * 容量的变化
	 */
	CAPACITY_CHANGE,
	/**
	 * 用户输入事件
	 */
	USER_ENTER_EVENT,
	/**
	 * 用户退出事件
	 */
	USER_EXIT_EVENT,
	/**
	 * 用户计数更改事件
	 */
	USER_COUNT_CHANGE_EVENT,
	/**
	 * 用户变量更新事件
	 */
	USER_VARIABLES_UPDATE_EVENT;
}

package com.codebroker.api;

import com.codebroker.core.data.IObject;

public interface NPCControl {
	/**
	 * 初始化
	 */
	public void init();
	/**
	 * 执行
	 */
	public void execute(IObject iObject);
	/**
	 * 销毁
	 */
	public void destroy();
}

package com.codebroker.api.event;

import com.codebroker.core.data.IObject;

public interface IEventListener {
	/**
	 * 分发事件
	 * @param topic    主题
	 * @param iObject  消息体
	 */
	public void handleEvent(String topic,IObject iObject);

}




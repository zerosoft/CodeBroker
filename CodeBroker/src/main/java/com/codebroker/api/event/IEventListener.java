package com.codebroker.api.event;

import com.codebroker.core.data.IObject;

public interface IEventListener {
	/**
	 * 分发事件
	 * @param topic
	 * @param iObject
	 */
	public void handleEvent(String topic,IObject iObject);

}




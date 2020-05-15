package com.codebroker.api.internal;

import com.codebroker.api.event.IEvent;

/**
 * 提供Actor调用的事件接口
 */
public interface IEventHandler {

	void handlerEvent(IEvent event);

}

package com.codebroker.api.event;

/**
 * 提供Actor调用的事件接口
 */
public interface IEventHandler {

	void handlerEvent(IEvent event);

}

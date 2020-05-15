package com.codebroker.api.event;

/**
 * 事件调度
 */
public interface IEventDispatcher {

	void addEventListener(String topic, IGameUserEventListener iGameUserEventListener);

	boolean hasEventListener(String topic);

	void removeEventListener(String topic, IGameUserEventListener iGameUserEventListener);

	void dispatchEvent(IEvent event);

}

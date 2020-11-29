package com.codebroker.api.event;

/**
 * 事件调度
 */
public interface IEventDispatcher<T> {

	void addEventListener(T topic, IGameUserEventListener iGameUserEventListener);

	boolean hasEventListener(T topic);

	void removeEventListener(T topic, IGameUserEventListener iGameUserEventListener);

	void dispatchEvent(IEvent event);

}

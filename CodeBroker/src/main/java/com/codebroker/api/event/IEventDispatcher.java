package com.codebroker.api.event;

/**
 * 事件分发处理
 * 
 * @author zero
 *
 */
public interface IEventDispatcher {
	
	public void addEventListener(String topic, IEventListener eventListener);

	public boolean hasEventListener(String topic);

	public void removeEventListener(String topic);

	public void dispatchEvent(IEvent paramIEvent);
}

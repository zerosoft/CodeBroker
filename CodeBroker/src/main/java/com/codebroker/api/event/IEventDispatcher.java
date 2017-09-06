package com.codebroker.api.event;

import com.codebroker.core.data.IObject;

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

	public void dispatchEvent(IObject iObject);
}

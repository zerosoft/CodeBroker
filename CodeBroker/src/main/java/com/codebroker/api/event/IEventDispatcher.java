package com.codebroker.api.event;

/**
 * 事件分发处理
 * 
 * @author zero
 *
 */
public interface IEventDispatcher {
	/**
	 * 添加事件分发监听器
	 * @param topic		主题
	 * @param eventListener 事件监听
	 */
	public void addEventListener(String topic, IEventListener eventListener);
	/**
	 *  是否有主题监听器
	 * @param topic
	 * @return
	 */
	public boolean hasEventListener(String topic);
	/**
	 * 移除主题的监听
	 * @param topic
	 */
	public void removeEventListener(String topic);
	/**
	 * 分发事件
	 * @param iObject
	 */
	public void dispatchEvent(Event event);
}

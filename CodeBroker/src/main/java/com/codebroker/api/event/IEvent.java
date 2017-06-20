package com.codebroker.api.event;
/**
 * 事件传递载体
 * @author zero
 *
 */
public interface IEvent {
	
	public String getTopic();

	public void setTopic(String topic);

	public String getParameter();

	public void setParameter(String jsonString);
}

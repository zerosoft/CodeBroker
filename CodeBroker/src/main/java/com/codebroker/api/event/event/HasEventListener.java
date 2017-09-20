package com.codebroker.api.event.event;
/**
 * 是否有个这个主题的监听器
 * @author zero
 *
 */
public class HasEventListener {

	public final String topic;

	public HasEventListener(String topic) {
		super();
		this.topic = topic;
	}

}

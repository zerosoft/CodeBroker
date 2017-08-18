package com.codebroker.core.entities;

import com.codebroker.api.event.IEvent;

public class CodeEvent implements IEvent {

	private String topic;
	private String jsonString;

	@Override
	public String getParameter() {
		return this.jsonString;
	}

	@Override
	public void setParameter(String jsonString) {
		this.jsonString = jsonString;
	}

	@Override
	public String getTopic() {
		return topic;
	}

	@Override
	public void setTopic(String topic) {
		this.topic = topic;
	}

}

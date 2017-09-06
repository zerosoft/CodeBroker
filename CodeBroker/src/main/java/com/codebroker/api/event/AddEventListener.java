package com.codebroker.api.event;

public class AddEventListener {
	public final String topic;
	public final IEventListener paramIEventListener;

	public AddEventListener(String topic, IEventListener paramIEventListener) {
		super();
		this.topic = topic;
		this.paramIEventListener = paramIEventListener;
	}
}

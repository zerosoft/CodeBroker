package com.codebroker.util.zookeeper;

import org.apache.zookeeper.Watcher;

public enum EventType {
	None(-1),
	NodeCreated(1),
	NodeDeleted(2),
	NodeDataChanged(3),
	NodeChildrenChanged(4),
	CONNECTION_SUSPENDED(11),
	CONNECTION_RECONNECTED(12),
	CONNECTION_LOST(12),
	INITIALIZED(10);



	private final int intValue;     // Integer representation of value
	// for sending over wire

	EventType(int intValue) {
		this.intValue = intValue;
	}

	public int getIntValue() {
		return intValue;
	}

	public static Watcher.Event.EventType fromInt(int intValue) {
		switch (intValue) {
			case -1:
				return Watcher.Event.EventType.None;
			case 1:
				return Watcher.Event.EventType.NodeCreated;
			case 2:
				return Watcher.Event.EventType.NodeDeleted;
			case 3:
				return Watcher.Event.EventType.NodeDataChanged;
			case 4:
				return Watcher.Event.EventType.NodeChildrenChanged;

			default:
				throw new RuntimeException("Invalid integer value for conversion to EventType");
		}
	}
}
package com.codebroker.api.internal.net;

import com.codebroker.api.internal.IPacket;

/**
 * 消息队列
 */
public interface IPacketQueue {

	IPacket peek();

	IPacket take();

	boolean isEmpty();

	boolean isFull();

	int getSize();

	int getMaxSize();

	void setMaxSize(int paramInt);

	float getPercentageUsed();

	void clear();

	void put(IPacket paramIPacket);
}

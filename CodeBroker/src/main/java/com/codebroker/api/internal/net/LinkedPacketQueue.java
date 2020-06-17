package com.codebroker.api.internal.net;

import com.codebroker.api.internal.IPacket;
import com.google.common.collect.Lists;

import java.util.LinkedList;

public class LinkedPacketQueue implements IPacketQueue {

	private int maxSize;
	LinkedList<IPacket> list=Lists.newLinkedList();

	@Override
	public IPacket peek() {
		IPacket packet = null;
		synchronized (list) {
			if (!isEmpty()){
				packet = list.get(0);
			}
		}
		return packet;
	}

	@Override
	public IPacket take() {
		IPacket packet;
		synchronized (list) {
			packet =list.removeFirst();
		}
		return packet;
	}

	@Override
	public boolean isEmpty() {
		return list.size()==0;
	}

	@Override
	public boolean isFull() {
		return list.size() >= maxSize;
	}

	@Override
	public int getSize() {
		return list.size();
	}

	@Override
	public int getMaxSize() {
		return maxSize;
	}

	@Override
	public void setMaxSize(int paramInt) {
		this.maxSize=paramInt;
	}

	@Override
	public float getPercentageUsed() {
		if (maxSize == 0)
			return 0.0F;
		return (list.size() * 100) / maxSize;
	}

	@Override
	public void clear() {
		synchronized (list) {
			list.clear();
		}
	}

	@Override
	public void put(IPacket paramIPacket) {
		if (isFull()){
			throw new NullPointerException();
		}
		synchronized (list) {
			list.addLast(paramIPacket);
		}
	}
}

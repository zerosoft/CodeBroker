package com.avic.sever.game.entity;

import java.util.ArrayList;
import java.util.List;

import com.codebroker.api.IUser;

public class Room {
	/**
	 *房间id
	 */
	private int id;
	/**
	 *房间类型
	 */
	private int type;
	/**
	 *	房主id
	 */
	private String onwerId;
	/**
	 *  玩家列表
	 */
	private List<IUser> list=new ArrayList<>();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getOnwerId() {
		return onwerId;
	}

	public void setOnwerId(String onwerId) {
		this.onwerId = onwerId;
	}

	public List<IUser> getList() {
		return list;
	}

	public void setList(List<IUser> list) {
		this.list = list;
	}
}

package com.huahang.entity;

import org.jongo.marshall.jackson.oid.MongoId;

public class UserEntity {
	//账号id
	@MongoId
	private String userId;
	//账号id
	private String accountId;
	//用户名
	private String userName;
	//密码
	private int level;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	
	
}

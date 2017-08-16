package com.huahang.entity;

import org.jongo.marshall.jackson.oid.MongoId;
import org.jongo.marshall.jackson.oid.MongoObjectId;

public class AccountEntity {
	
	//账号名
	@MongoId
	private String accountName;
	//密码
	private String accountPassWord;
	
	private String userId;

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getAccountPassWord() {
		return accountPassWord;
	}

	public void setAccountPassWord(String accountPassWord) {
		this.accountPassWord = accountPassWord;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}

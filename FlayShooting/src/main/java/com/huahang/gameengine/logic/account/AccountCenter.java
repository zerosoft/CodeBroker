package com.huahang.gameengine.logic.account;


public class AccountCenter {
	
	static class inner {
		public static AccountCenter inner = new AccountCenter();
	}

	public AccountCenter getInstance() {
		return inner.inner;
	}
	
	
}

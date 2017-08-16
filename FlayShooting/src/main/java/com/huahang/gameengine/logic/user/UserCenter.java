package com.huahang.gameengine.logic.user;


public class UserCenter {
	
	static class inner {
		public static UserCenter inner = new UserCenter();
	}

	public UserCenter getInstance() {
		return inner.inner;
	}
}

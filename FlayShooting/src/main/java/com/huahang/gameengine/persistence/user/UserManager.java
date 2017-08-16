package com.huahang.gameengine.persistence.user;

import org.jongo.Jongo;
import org.jongo.MongoCollection;

import com.codebroker.api.AppContext;
import com.codebroker.database.JongoDBService;
import com.huahang.entity.UserEntity;

public class UserManager {
	static class inner {
		public static UserManager inner = new UserManager();
	}

	public UserManager getInstance() {
		return inner.inner;
	}

	public UserEntity createUser(String accountId, String username) {
		JongoDBService manager = AppContext.getManager(JongoDBService.class);
		Jongo jongo = manager.getJongo();

		MongoCollection collection = jongo.getCollection("USER");

		UserEntity userEntity = new UserEntity();
		userEntity.setUserId(accountId);
		userEntity.setAccountId(accountId);
		userEntity.setLevel(1);
		userEntity.setUserName(username);

		try {
			collection.insert(userEntity);
			return userEntity;
		} catch (com.mongodb.DuplicateKeyException e) {
		}
		return null;
	}

	public UserEntity getUser(String userId) {
		JongoDBService manager = AppContext.getManager(JongoDBService.class);
		Jongo jongo = manager.getJongo();

		MongoCollection collection = jongo.getCollection("USER");
		try {
			UserEntity as = collection.findOne("{_id:#}", userId).as(UserEntity.class);
			return as;
		} catch (com.mongodb.DuplicateKeyException e) {
		}
		return null;
	}
}

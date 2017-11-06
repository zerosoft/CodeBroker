package com.avic.sever.game.model;

import com.mongodb.WriteResult;
import org.bson.types.ObjectId;
import org.jongo.Jongo;
import org.jongo.MongoCollection;


public class UserManager {
	static class inner{
		public static UserManager inner=new UserManager();
	}
	
	public static UserManager getInstance(){
		return inner.inner;
	}
	
	public UserEntity selectUserByUserId(Jongo jongo, String userId){
		MongoCollection collection = jongo.getCollection("UserEntity");
		try {
			UserEntity as = collection.findOne("{_id:#}",userId).as(UserEntity.class);
			return  as;
		} catch (com.mongodb.DuplicateKeyException e) {
			return null;
		}
	}

	public UserEntity selectUserByAccountId(Jongo jongo, String accountId){
		MongoCollection collection = jongo.getCollection("UserEntity");
		try {
			UserEntity as = collection.findOne("{accountId:#}",accountId).as(UserEntity.class);
			return  as;
		} catch (com.mongodb.DuplicateKeyException e) {
			return null;
		}
	}
	public boolean createUser(Jongo jongo ,UserEntity userEntity){
		MongoCollection collection = jongo.getCollection("UserEntity");
		try {
			WriteResult insert = collection.insert(userEntity);
			Object upsertedId = insert.getUpsertedId();
			return true;
		} catch (com.mongodb.DuplicateKeyException e) {
			return false;
		}
	}

	public UserEntity createUser(Jongo jongo ,String accountId,String userId){
		UserEntity entity=new UserEntity();
		entity.setUserId(userId);
		entity.setAccountId(accountId);
		entity.setLevel(1);
		entity.getPlanes().add("U-34");
		entity.getPlanes().add("U-37");
		MongoCollection collection = jongo.getCollection("UserEntity");
		try {
			collection.insert(entity);
			return entity;
		} catch (com.mongodb.DuplicateKeyException e) {
			return null;
		}
	}
	
	public void update(Jongo jongo,UserEntity userEntity){
		MongoCollection collection = jongo.getCollection("UserEntity");
		collection.update("{_id:#}", userEntity.getUserId()).with(userEntity);

	}
}

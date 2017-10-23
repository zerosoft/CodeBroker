package com.avic.sever.game.model;

import org.jongo.Jongo;
import org.jongo.MongoCollection;


public class PlaneManager {
	static class inner{
		public static PlaneManager inner=new PlaneManager();
	}
	
	public static PlaneManager getInstance(){
		return inner.inner;
	}
	
	public PlaneEntity selectUserByUserId(Jongo jongo, String userId){
		MongoCollection collection = jongo.getCollection("PlaneEntity");
		try {
			PlaneEntity as = collection.findOne("{_id:#}",userId).as(PlaneEntity.class);
			return  as;
		} catch (com.mongodb.DuplicateKeyException e) {
			return null;
		}
	}


	public PlaneEntity createPlaneEntity(Jongo jongo ,String accountId,String userId){
		PlaneEntity entity=new PlaneEntity();
		MongoCollection collection = jongo.getCollection("PlaneEntity");
		try {
			collection.insert(entity);
			return entity;
		} catch (com.mongodb.DuplicateKeyException e) {
			return null;
		}
	}
	
	public void update(Jongo jongo,PlaneEntity userEntity){
		MongoCollection collection = jongo.getCollection("UserEntity");
		collection.update("{_id:#}", userEntity.getPlaneId()).with(userEntity);

	}
}

package com.avic.sever.game.model;

import org.jongo.Jongo;
import org.jongo.MongoCollection;


public class AccountManager {
	static class inner{
		public static AccountManager inner=new AccountManager();
	}
	
	public static AccountManager getInstance(){
		return inner.inner;
	}
	
	public AccountEntity selectAccount(Jongo jongo,String account,String password){
		MongoCollection collection = jongo.getCollection("AccountEntity");
		try {
			AccountEntity as = collection.findOne("{_id:#,password:#}",account,password).as(AccountEntity.class);
			return  as;
		} catch (com.mongodb.DuplicateKeyException e) {
			return null;
		}
	}
	
	public AccountEntity regeditAccount(Jongo jongo ,String account,String password){
		AccountEntity accountEntity=new AccountEntity();
		accountEntity.setAccountId(account);
		accountEntity.setPassword(password);
		MongoCollection collection = jongo.getCollection("AccountEntity");
		try {
			collection.insert(accountEntity);
			return accountEntity;
		} catch (com.mongodb.DuplicateKeyException e) {
			return null;

		}
	}
	
	public boolean checkRegedit(Jongo jongo ,String account){
		MongoCollection collection = jongo.getCollection("AccountEntity");
		AccountEntity findOne = collection.findOne("{_id:#}",account).as(AccountEntity.class);
		return findOne!=null;
	}
}

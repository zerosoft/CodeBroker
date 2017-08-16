package com.huahang.gameengine.persistence.account;

import org.jongo.Jongo;
import org.jongo.MongoCollection;

import com.codebroker.api.AppContext;
import com.codebroker.database.JongoDBService;
import com.huahang.entity.AccountEntity;

public class AccountManager {
	static class inner{
		public static AccountManager inner=new AccountManager();
	}
	
	public AccountManager getInstance(){
		return inner.inner;
	}
	
	public AccountEntity selectAccount(String account,String password){
		JongoDBService manager = AppContext.getManager(JongoDBService.class);
		Jongo jongo = manager.getJongo();
		
		MongoCollection collection = jongo.getCollection("Account");
		try {
			AccountEntity as = collection.findOne("{_id:#,accountPassWord:#}",account,password).as(AccountEntity.class);
			return  as;
		} catch (com.mongodb.DuplicateKeyException e) {
		}
		return null;
	}
	
	public AccountEntity regeditAccount(String account,String password){
		JongoDBService manager = AppContext.getManager(JongoDBService.class);
		Jongo jongo = manager.getJongo();
		
		AccountEntity accountEntity=new AccountEntity();
		accountEntity.setAccountName(account);
		accountEntity.setAccountPassWord(password);
		MongoCollection collection = jongo.getCollection("Account");
		try {
			collection.insert(accountEntity);
			return accountEntity;
		} catch (com.mongodb.DuplicateKeyException e) {
		}
		return null;
	}
	
	public boolean checkRegedit(String account){
		JongoDBService manager = AppContext.getManager(JongoDBService.class);
		Jongo jongo = manager.getJongo();
		MongoCollection collection = jongo.getCollection("Account");
		AccountEntity findOne = collection.findOne("{_id:#}",account).as(AccountEntity.class);
		return findOne!=null;
	}
}

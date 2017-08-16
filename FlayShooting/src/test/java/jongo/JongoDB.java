package jongo;

import org.bson.types.ObjectId;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

import com.huahang.entity.AccountEntity;
import com.mongodb.DB;
import com.mongodb.MongoClient;

public class JongoDB {

	public static void main(String[] args) {
		DB db = new MongoClient("192.168.0.199",32770).getDB("fly");
		Jongo jongo = new Jongo(db);
		AccountEntity accountEntity=new AccountEntity();
		accountEntity.setAccountName("test2");
		accountEntity.setAccountPassWord("1234");
		MongoCollection collection = jongo.getCollection("Account");
		try {
			collection.insert(accountEntity);
		} catch (com.mongodb.DuplicateKeyException e) {
			System.out.println("1245");
		}
		AccountEntity as = collection.findOne("{_id:#,accountPassWord:#}","test2","12345").as(AccountEntity.class);
		System.out.println(as);
	}

}

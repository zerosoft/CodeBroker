package com.codebroker.pool;

import akka.actor.typed.ActorRef;
import com.codebroker.core.actortype.message.IUser;
import com.codebroker.core.entities.GameUser;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;

public class GameUserPool {

	static GenericObjectPool<GameUser> userGenericObjectPool=new GenericObjectPool(new GameUserBasePooledObjectFactory());

	public static GameUser getGameUser(String uid, ActorRef<IUser> self) {
		try {
			GameUser gameUser = userGenericObjectPool.borrowObject(500);
			gameUser.setUid(uid);
			gameUser.setActorRef(self);
			return gameUser;
		} catch (Exception e) {
			return new GameUser(null,null);
		}
	}

	public static void returnGameUser(GameUser gameUser){
		userGenericObjectPool.returnObject(gameUser);
	}

}

class GameUserBasePooledObjectFactory extends BasePooledObjectFactory<GameUser> {
	@Override
	public GameUser create(){
		return new GameUser("",null);
	}

	@Override
	public PooledObject wrap(GameUser obj) {
		DefaultPooledObject defaultPooledObject=new DefaultPooledObject(obj);
		return defaultPooledObject;
	}

	@Override
	public void passivateObject(PooledObject<GameUser> p){
		GameUser object = p.getObject();
		object.clean();
	}

}

package com.codebroker.demo.service.account.request;

import com.codebroker.component.service.RedisComponent;
import com.codebroker.core.ContextResolver;
import com.codebroker.core.data.CObject;
import com.codebroker.core.data.IObject;
import com.codebroker.demo.service.account.model.Account;
import com.codebroker.extensions.service.IServiceClientRequestHandler;
import com.codebroker.redis.collections.MapStructure;
import com.codebroker.redis.collections.builder.buider.RedisStrutureBuilder;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class LoginAccountHandler implements IServiceClientRequestHandler<IObject> {


	@Override
	public Object handleBackMessage(IObject message1) {
		IObject message=(IObject)message1;
		Optional<RedisComponent> component = ContextResolver.getComponent(RedisComponent.class);
		if (component.isPresent()){
			String name = message.getUtfString("name");
			String password = message.getUtfString("password");

			RedisComponent redisComponent = component.get();
			redis.clients.jedis.Jedis jedis = redisComponent.getJedis();
			try {
				MapStructure<Account> account = RedisStrutureBuilder.ofMap(jedis, Account.class).withNameSpace("Account").build();
				Map<String, Account> acccount = account.get("Acccount");
				if (acccount.containsKey(name)){
					Account account1 = acccount.get(name);
					if (account1.getPassoword().equals(password)){
						CObject cObject = CObject.newInstance();
						cObject.putUtfString("uid",account1.getUid());
						return cObject;
					}else{
						CObject cObject = CObject.newInstance();
						cObject.putUtfString("status","pass word error");
						return cObject;
					}
				}else{
					UUID uuid = UUID.randomUUID();
					Account account1=new Account();
					account1.setUid(uuid.toString());
					account1.setAccount(name);
					account1.setPassoword(password);
					acccount.put(name,account1);

					CObject cObject = CObject.newInstance();
					cObject.putUtfString("uid",account1.getUid());
					return cObject;
				}

			}catch (Exception e){
				CObject cObject = CObject.newInstance();
				cObject.putUtfString("status",e.getMessage());
				return cObject;
			}finally {
				if (Objects.nonNull(jedis)){
					jedis.close();
				}
			}

		}else {
			CObject cObject = CObject.newInstance();
			cObject.putUtfString("status","no db");
			return cObject;
		}
	}
}

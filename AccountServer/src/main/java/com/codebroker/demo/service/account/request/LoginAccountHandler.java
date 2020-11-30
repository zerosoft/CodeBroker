package com.codebroker.demo.service.account.request;

import com.codebroker.component.service.RedisComponent;
import com.codebroker.core.ContextResolver;
import com.codebroker.core.data.CObject;
import com.codebroker.core.data.IObject;
import com.codebroker.demo.service.account.model.Account;
import com.codebroker.extensions.service.IServiceClientRequestHandler;
import com.codebroker.redis.collections.MapStructure;
import com.codebroker.redis.collections.builder.buider.RedisStrutureBuilder;
import com.google.gson.JsonObject;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class LoginAccountHandler implements IServiceClientRequestHandler<JsonObject> {


	@Override
	public Object handleBackMessage(JsonObject jsonObject) {
		Optional<RedisComponent> component = ContextResolver.getComponent(RedisComponent.class);
		JsonObject result=new JsonObject();
		if (component.isPresent()){
			String name = jsonObject.get("name").getAsString();
			String password = jsonObject.get("password").getAsString();

			RedisComponent redisComponent = component.get();
			redis.clients.jedis.Jedis jedis = redisComponent.getJedis();

			try {
				MapStructure<Account> account = RedisStrutureBuilder.ofMap(jedis, Account.class).withNameSpace("Account").build();
				Map<String, Account> acccount = account.get("Acccount");
				if (acccount.containsKey(name)){
					Account account1 = acccount.get(name);
					if (account1.getPassoword().equals(password)){
						result.addProperty("uid",account1.getUid());
						return result;
					}else{
						result.addProperty("status","pass word error");
						return result;
					}
				}else{
					UUID uuid = UUID.randomUUID();
					Account account1=new Account();
					account1.setUid(uuid.toString());
					account1.setAccount(name);
					account1.setPassoword(password);
					acccount.put(name,account1);

					result.addProperty("uid",account1.getUid());
					return result;
				}

			}catch (Exception e){
				result.addProperty("status",e.getMessage());
				return result;
			}finally {
				if (Objects.nonNull(jedis)){
					jedis.close();
				}
			}

		}else {
			result.addProperty("status","no db");
			return result;
		}
	}
}

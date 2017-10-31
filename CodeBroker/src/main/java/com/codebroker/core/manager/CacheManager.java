package com.codebroker.core.manager;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.serialization.Serialization;
import com.codebroker.core.ContextResolver;
import com.codebroker.core.ServerEngine;
import com.codebroker.core.service.BaseCoreService;
import com.codebroker.core.service.RedisService;
import com.codebroker.redis.collections.MapStructure;
import com.codebroker.redis.collections.builder.RedisUtils;
import com.codebroker.redis.collections.builder.buider.RedisStrutureBuilder;
import com.codebroker.util.PropertiesWrapper;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CacheManager extends BaseCoreService {

    public static String nameSpace = "RedisCache";
    public static String areaManagerKey = "AreaManager";
    public static String ActorPathKey = "ActorPath";
    public static String AreaActorKey = "AreaActor";
    boolean redis = false;

    public static String getAreaId(int localId) {
        return "SERVER_" + ServerEngine.serverId + ":AREA_" + localId;
    }

    @Override
    public void init(Object obj) {
        super.init(obj);
        PropertiesWrapper propertiesWrapper = (PropertiesWrapper) obj;
        if (propertiesWrapper.getBooleanProperty("redis", false)) {
            redis = true;
        }
        Jedis jedis = getJedis();
        jedis.del(RedisUtils.createKeyWithNameSpace(ActorPathKey, nameSpace));

        MapStructure<String> mapStructure = RedisStrutureBuilder.ofMap(jedis, String.class).withNameSpace(nameSpace).build();
        Map<String, String> stringStringMap = mapStructure.get(areaManagerKey);
        for (Map.Entry<String, String> entry : stringStringMap.entrySet()
                ) {
            jedis.del(RedisUtils.createKeyWithNameSpace(AreaActorKey + ":" + entry.getKey(), nameSpace));
        }
        jedis.del(RedisUtils.createKeyWithNameSpace(areaManagerKey, nameSpace));
        jedis.close();
        super.setActive();
    }

    public ActorRef getLocalPath(String IDENTIFY) {
        String identifier = getActorRefPath().get(IDENTIFY);
        ActorSystem actorSystem = ContextResolver.getActorSystem();
        ActorRef actorRef = actorSystem.provider().resolveActorRef(identifier);
        return actorRef;
    }

    public List<ActorRef> getAreaLocalPaths() {
        ActorSystem actorSystem = ContextResolver.getActorSystem();
        Map<String, String> identifier = geAreaActorRefPath();

        List<ActorRef> result = new ArrayList<>();
        for (Map.Entry<String, String> entry : identifier.entrySet()) {
            ActorRef actorRef = actorSystem.provider().resolveActorRef(entry.getValue());
            result.add(actorRef);
        }
        return result;
    }

    public void setAreaUserRefPath(String areaId, String IDENTIFY, ActorRef actorRef) {
        String identifier = Serialization.serializedActorPath(actorRef);
        setAreaUserActorRefPath(areaId, IDENTIFY, identifier);
    }

    public void setLocalPath(String IDENTIFY, ActorRef actorRef) {
        String identifier = Serialization.serializedActorPath(actorRef);
        setActorRefPath(IDENTIFY, identifier);
    }

    public void setLocalAreaPath(String identify, String identifier) {
        setRedisActorPath(identify, identifier, areaManagerKey);
    }

    public void removeAreaActorRefPath(String key) {
        removeActorPath(key, areaManagerKey);
    }

    public void removeActorRefPath(String key) {
        removeActorPath(key, ActorPathKey);
    }

    public Map<String, String> getActorRefPath() {
        if (redis) {
            Map<String, String> result = getStringStringMap(ActorPathKey);
            return result;
        } else {
            return new HashMap<String, String>();
        }
    }

    public Map<String, String> getAreaUserActorRefPath(String areaId) {
        if (redis) {
            Map<String, String> result = getStringStringMap(AreaActorKey + ":" + areaId);
            return result;
        } else {
            return new HashMap<String, String>();
        }
    }

    public Map<String, String> geAreaActorRefPath() {
        if (redis) {
            return getStringStringMap(areaManagerKey);
        } else {
            return new HashMap<String, String>();
        }
    }

    private Map<String, String> getStringStringMap(String areaManagerKey) {
        Jedis jedis = getJedis();

        MapStructure<String> mapStructure = RedisStrutureBuilder.ofMap(jedis, String.class).withNameSpace(nameSpace).build();
        Map<String, String> map = mapStructure.get(areaManagerKey);

        Map<String, String> result = new HashMap<>();
        result.putAll(map);
        if (jedis != null) {
            jedis.close();
        }

        return result;
    }

    private void setActorRefPath(String identify, String identifier) {
        setRedisActorPath(identify, identifier, ActorPathKey);
    }

    private void setAreaUserActorRefPath(String prifix, String identify, String identifier) {
        setRedisActorPath(identify, identifier, AreaActorKey + ":" + prifix);
    }

    private void setRedisActorPath(String identify, String identifier, String areaManagerKey) {
        Jedis jedis = getJedis();

        MapStructure<String> mapStructure = RedisStrutureBuilder.ofMap(jedis, String.class).withNameSpace(nameSpace).build();
        Map<String, String> map = mapStructure.get(areaManagerKey);

        map.put(identify, identifier);
        if (jedis != null) {
            jedis.close();
        }
    }

    private void removeActorPath(String key, String actorPathKey) {
        Jedis jedis = getJedis();

        MapStructure<String> mapStructure = RedisStrutureBuilder.ofMap(jedis, String.class).withNameSpace(nameSpace).build();
        Map<String, String> map = mapStructure.get(actorPathKey);
        map.remove(key);
        if (jedis != null) {
            jedis.close();
        }
    }

    private Jedis getJedis() {
        RedisService component = ContextResolver.getComponent(RedisService.class);
        return component.getJedis();
    }

    public boolean containsAreaKey(int localId) {
        return geAreaActorRefPath().containsKey(getAreaId(localId));
    }

    public ActorRef getAreaLocalPaths(int loaclAreaId) {
        String areaId = getAreaId(loaclAreaId);
        Jedis jedis = getJedis();

        MapStructure<String> mapStructure = RedisStrutureBuilder.ofMap(jedis, String.class).withNameSpace(nameSpace).build();
        Map<String, String> map = mapStructure.get(areaManagerKey);
        String s = map.get(areaId);

        if (jedis != null) {
            jedis.close();
        }

        if (s != null) {
            ActorSystem actorSystem = ContextResolver.getActorSystem();
            ActorRef actorRef = actorSystem.provider().resolveActorRef(s);
            return actorRef;
        } else {
            return null;
        }


    }
}

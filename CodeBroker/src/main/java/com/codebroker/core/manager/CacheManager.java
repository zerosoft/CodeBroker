package com.codebroker.core.manager;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.serialization.Serialization;
import com.codebroker.cache.AreaInfoCache;
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
    public static String AREA_MANAGER_INDEX = "AreaManager";
    public static String AREA_INFO_INDEX = "AreaInfo";
    public static String ACTOR_GLOBAL_PATH_INDEX = "ActorPath";
//    public static String AREA_USER_ACTOR_INDEX = "AreaUserActor";
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
        //清空之前的数据
        Jedis jedis = getJedis();
        jedis.del(RedisUtils.createKeyWithNameSpace(ACTOR_GLOBAL_PATH_INDEX, nameSpace));

        MapStructure<String> mapStructure = RedisStrutureBuilder.ofMap(jedis, String.class).withNameSpace(nameSpace).build();
        jedis.del(RedisUtils.createKeyWithNameSpace(AREA_MANAGER_INDEX, nameSpace));
        jedis.close();
        super.setActive();
    }

    public AreaInfoCache getAreaInfoCache(String areaId){
        Jedis jedis = getJedis();
        try {
            final MapStructure<AreaInfoCache> build = RedisStrutureBuilder.ofMap(jedis, AreaInfoCache.class).withNameSpace(nameSpace).build();
            Map<String, AreaInfoCache> stringAreaInfoCacheMap = build.get(AREA_INFO_INDEX);
            return stringAreaInfoCacheMap.get(areaId);
        } finally {
            jedis.close();
        }
    }

    public void removeAreaInfoCache(String areaId){
        Jedis jedis = getJedis();
        try {
            final MapStructure<AreaInfoCache> build = RedisStrutureBuilder.ofMap(jedis, AreaInfoCache.class).withNameSpace(nameSpace).build();
            Map<String, AreaInfoCache> stringAreaInfoCacheMap = build.get(AREA_INFO_INDEX);
            stringAreaInfoCacheMap.remove(areaId);
        } finally {
            jedis.close();
        }
    }

    public void putAreaInfoCache(String areaId, AreaInfoCache info){
        Jedis jedis = getJedis();
        try {
            final MapStructure<AreaInfoCache> build = RedisStrutureBuilder.ofMap(jedis, AreaInfoCache.class).withNameSpace(nameSpace).build();
            Map<String, AreaInfoCache> stringAreaInfoCacheMap = build.get(AREA_INFO_INDEX);
            stringAreaInfoCacheMap.put(areaId,info);
        } finally {
            jedis.close();
        }
    }

    private Jedis getJedis() {
        RedisService component = ContextResolver.getComponent(RedisService.class);
        return component.getJedis();
    }

    /**
     * 获得全局唯一的Actort
     * @param IDENTIFY
     * @return
     */
    public ActorRef getActorGlobalPath(String IDENTIFY) {
        String identifier = getActorGlobalPath().get(IDENTIFY);
        ActorSystem actorSystem = ContextResolver.getActorSystem();
        ActorRef actorRef = actorSystem.provider().resolveActorRef(identifier);
        return actorRef;
    }

    /**
     * 获得区域列表Actor
     * @return
     */
    public List<ActorRef> getAreaLocalPaths() {
        ActorSystem actorSystem = ContextResolver.getActorSystem();
        Map<String, String> identifier = geAreaManagerActorPath();

        List<ActorRef> result = new ArrayList<>();
        for (Map.Entry<String, String> entry : identifier.entrySet()) {
            ActorRef actorRef = actorSystem.provider().resolveActorRef(entry.getValue());
            result.add(actorRef);
        }
        return result;
    }

    /**
     * 获得区域列表Actor
     * @return
     */
    public List<String> getAreaLocalIds() {
        Map<String, String> identifier = geAreaManagerActorPath();
        List<String> result = new ArrayList<>();
        result.addAll(identifier.keySet());
        return result;
    }


    /**
     * 放置唯一的
     * @param IDENTIFY
     * @param actorRef
     */
    public void putActorGlobalPath(String IDENTIFY, ActorRef actorRef) {
        String identifier = Serialization.serializedActorPath(actorRef);
        putActorRefPath(IDENTIFY, identifier);
    }

    public void putAreaManagerPath(String identify, String identifier) {
        putRedisActorPath(identify, identifier, AREA_MANAGER_INDEX);
    }


    public void removeAreaManagerPath(String key) {
        removeActorPath(key, AREA_MANAGER_INDEX);
    }

    public void removeGlobalActorRefPath(String key) {
        removeActorPath(key, ACTOR_GLOBAL_PATH_INDEX);
    }

    public Map<String, String> getActorGlobalPath() {
        if (redis) {
            Map<String, String> result = getStringStringMap(ACTOR_GLOBAL_PATH_INDEX);
            return result;
        } else {
            return new HashMap<String, String>();
        }
    }



    public Map<String, String> geAreaManagerActorPath() {
        if (redis) {
            return getStringStringMap(AREA_MANAGER_INDEX);
        } else {
            return new HashMap<String, String>();
        }
    }

    public boolean containsAreaKey(int localId) {
        return geAreaManagerActorPath().containsKey(getAreaId(localId));
    }

    public ActorRef getAreaLocalPaths(int loaclAreaId) {
        String areaId = getAreaId(loaclAreaId);
        Jedis jedis = getJedis();

        MapStructure<String> mapStructure = RedisStrutureBuilder.ofMap(jedis, String.class).withNameSpace(nameSpace).build();
        Map<String, String> map = mapStructure.get(AREA_MANAGER_INDEX);
        String stringPath = map.get(areaId);

        if (jedis != null) {
            jedis.close();
        }

        if (stringPath != null&&!stringPath.trim().equals("")) {
            ActorSystem actorSystem = ContextResolver.getActorSystem();
            ActorRef actorRef = actorSystem.provider().resolveActorRef(stringPath);
            return actorRef;
        } else {
            return null;
        }
    }



    private Map<String, String> getStringStringMap(String index) {
        Jedis jedis = getJedis();

        MapStructure<String> mapStructure = RedisStrutureBuilder.ofMap(jedis, String.class).withNameSpace(nameSpace).build();
        Map<String, String> map = mapStructure.get(index);

        Map<String, String> result = new HashMap<>();
        result.putAll(map);
        if (jedis != null) {
            jedis.close();
        }

        return result;
    }

    private void putActorRefPath(String identify, String identifier) {
        putRedisActorPath(identify, identifier, ACTOR_GLOBAL_PATH_INDEX);
    }

    private void putRedisActorPath(String identify, String identifier, String areaManagerKey) {
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


}

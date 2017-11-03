package com.codebroker.cache;

import akka.actor.ActorRef;
import akka.serialization.Serialization;
import com.codebroker.core.ContextResolver;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 区域的缓存信息
 */
public class AreaInfoCache{
    /**
     * 区域唯一id
     */
    private String areaId;
    /**
     * 区域Actor地址
     */
    private String AreaRef;
    /**
     * 区域A内玩家id和Actor地址
     */
    private Map<String,String> userPath=new HashMap<String, String>();
    /**
     * 区域内格子id和Actor地址
     */
    private Map<String,String> gridPath=new HashMap<String, String>();

    public Map<String, String> getUserPath() {
        return userPath;
    }

    public void setUserPath(Map<String, String> userPath) {
        this.userPath = userPath;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public ActorRef getAreaRef() {
        return ContextResolver.getActorSystem().provider().resolveActorRef(AreaRef);
    }

    public void setAreaRef(ActorRef areaRef) {
        AreaRef = Serialization.serializedActorPath(areaRef);
    }

    public Map<String, String> getGridPath() {
        return gridPath;
    }

    public void setGridPath(Map<String, String> gridPath) {
        this.gridPath = gridPath;
    }
}

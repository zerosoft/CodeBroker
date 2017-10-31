package com.codebroker.cache;

import akka.actor.ActorRef;

import java.io.Serializable;

/**
 * 区域的缓存信息
 */
public class AreaInfoCache implements Serializable {
    private String areaId;

    private ActorRef AreaRef;


}

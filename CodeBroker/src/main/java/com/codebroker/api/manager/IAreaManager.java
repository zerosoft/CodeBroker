package com.codebroker.api.manager;

import akka.actor.ActorRef;
import com.codebroker.api.IArea;

import java.util.List;

/**
 * 区域管理器
 *
 * @author xl
 */
public interface IAreaManager {

    void createArea(int loaclAreaId);

    IArea getAreaById(int loaclAreaId);

    List<ActorRef> getAllArea();

    List<String> getAllAreaIds();

    void removeArea(int loaclAreaId);
}

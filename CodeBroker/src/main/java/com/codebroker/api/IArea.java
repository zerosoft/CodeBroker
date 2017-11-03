package com.codebroker.api;

import com.codebroker.api.event.Event;

import java.util.Collection;
import java.util.List;

/**
 * 区域
 *
 * @author xl
 */
public interface IArea {
    /**
     * 获取区域ID
     *
     * @return
     * @throws Exception
     */
     String getId();

    /**
     * 进入区域
     *
     * @param user
     * @return
     * @throws Exception
     */
     void enterArea(IUser user);

    /**
     * 离开区域
     *
     * @param userID
     */
     void leaveArea(String userID);

    /**
     * 创建一个格子
     *
     * @param gridId
     * @return
     * @throws Exception
     */
     void createGrid(String gridId);

    /**
     * 删除一个格子
     *
     * @param gridId
     */
     void removeGridById(String gridId);

    /**
     * 根据id获得一个格子
     *
     * @param gridId
     * @return
     * @throws Exception
     */
     IGrid getGridById(String gridId);

    /**
     * 获得区域内所有格子
     *
     * @return
     * @throws Exception
     */
     Collection<IGrid> getAllGrid();

    /**
     * 获得当前区域里的所有玩家
     *
     * @return
     * @throws Exception
     */
     List<IUser> getPlayers();

    /**
     * 销毁区域
     */
     void destroy();

    /**
     * 广播当前区域所有用户
     *
     */
     void broadCastAllUser(Event object);

    /**
     * 对指定组的用户进行广播
     *
     * @param users
     */
     void broadCastUsers(Event object, Collection<IUser> users);
}

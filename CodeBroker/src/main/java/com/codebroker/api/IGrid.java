package com.codebroker.api;

import com.codebroker.api.event.Event;
import com.codebroker.api.event.IEventDispatcher;

import java.util.Collection;
import java.util.List;

/**
 * 区域中的格子
 *
 * @author ZERO
 */
public interface IGrid extends IEventDispatcher {
    /**
     * 获取格子ID
     *
     * @return
     * @throws Exception
     */
    public String getId() throws Exception;

    /**
     * 加入格子
     *
     * @param user
     * @return
     * @throws Exception
     */
    public boolean enterGrid(IUser user) throws Exception;

    /**
     * 离开格子
     *
     * @param userID
     */
    public void leaveGrid(String userID);

    /**
     * 获得当前格子里的所有玩家
     *
     * @return
     * @throws Exception
     */
    public List<IUser> getPlayers() throws Exception;

    /**
     * 对指定组的用户进行广播
     *
     * @param jsonString
     * @param users
     */
    public void broadCastUsers(Event object, Collection<IUser> users);

    /**
     * 广播当前格子中的所有用户
     *
     * @param jsonString
     */
    public void broadCastAllUser(Event object);

    /**
     * 销毁格子
     */
    public void destroy();
}

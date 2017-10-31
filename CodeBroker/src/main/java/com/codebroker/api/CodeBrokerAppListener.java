package com.codebroker.api;

import com.codebroker.api.internal.IService;
import com.codebroker.exception.AllReadyRegeditException;
import com.codebroker.exception.NoAuthException;

import java.util.Collection;

/**
 * 游戏应用登入的入口.
 *
 * @author ZERO
 */
public interface CodeBrokerAppListener extends IService {

    /**
     * 获得当前机器所有用户数量
     *
     * @return
     */
    int getUserCount();

    /**
     * 获得当前机器所有用户
     *
     * @return
     */
    Collection<IUser> getUserList();

    /**
     * 用户连接到系统
     *
     * @param name
     * @param parms
     * @return 重连接key
     */
    String handleLogin(String name, String parms) throws NoAuthException;

    /**
     * 用户注册到系统
     *
     * @param name
     * @param parms
     * @return 重连接key
     */
    boolean handleRegedit(String name, String parms) throws AllReadyRegeditException;

    /**
     * 用户主动下线
     *
     * @param user
     * @return
     */
    boolean handleLogout(IUser user);

    /**
     * 用户网络从新连接
     *
     * @param user
     * @return
     */
    boolean userReconnection(IUser user);

    /**
     * 处理用户的网络协议
     *
     * @param user
     * @param requestId
     * @param params
     */
    void handleClientRequest(IUser user, int requestId, Object params);
}

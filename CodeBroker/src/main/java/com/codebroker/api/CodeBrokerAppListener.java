package com.codebroker.api;

import com.codebroker.api.internal.IService;
import com.codebroker.exception.AllReadyRegeditException;
import com.codebroker.exception.NoAuthException;

import java.util.Collection;

/**
 * 游戏应用登入的入口.
 *
 * @author LongJu
 */
public interface CodeBrokerAppListener extends IService {

     /**
     * 用户连接到系统的验证
     *
     * @param name
     * @param parameter
     * @return 重连接key
     */
    String sessionLoginVerification(String name, String parameter) throws NoAuthException;

    /**
     * 用户登入到系统
     * @param user
     */
    void userLogin(IGameUser user);


    /**
     * 用户主动下线
     *
     * @param user
     * @return
     */
    boolean handleLogout(IGameUser user);

    /**
     * 用户网络从新连接
     *
     * @param user
     * @return
     */
    boolean userReconnection(IGameUser user);

    /**
     * 处理用户的网络协议
     *
     * @param user
     * @param requestId
     * @param params
     */
    void handleClientRequest(IGameUser user, int requestId, Object params) throws Exception;

}

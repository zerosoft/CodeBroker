package com.codebroker.api;

/**
 * 网络会话.
 *
 * @author ZERO
 */
public interface IoSession {
    /**
     * 获得Session ID
     *
     * @return
     */
    long getSessionId();

    /**
     * 数据写入.
     *
     * @param msg the msg
     */
    void write(Object msg);

    /**
     * 连接是否正常.
     */
    boolean isConnection();

    /**
     * 关闭连接
     *
     * @param close 用戶关闭
     */
    void close(boolean close);

}

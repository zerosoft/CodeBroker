package com.codebroker.api;

/**
 * 网络会话.
 *
 * @author LongJu
 */
public interface IoSession {

    /**
     * 数据写入.
     *
     * @param msg the msg
     */
    void write(Object msg);

    /**
     * 数据是否写出
     * @param msg
     * @param flush
     */
    void write(Object msg,boolean flush);

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

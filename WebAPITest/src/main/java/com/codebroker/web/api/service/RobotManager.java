package com.codebroker.web.api.service;

import com.codebroker.web.api.service.netty.TCPClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * description
 *
 * @author Dragon
 * @Date 2019/10/25
 */
@Component
public class RobotManager {
   //网络连接
   public TCPClient client;
   public Thread clientThread;
   public AtomicBoolean connect=new AtomicBoolean(false);
   //连接数量
   public AtomicInteger onlineNum=new AtomicInteger(0);


   public int nowUserServer;
   //机器人连接前缀
   public String namePifx;
   //当前机器人数量
   public AtomicInteger robotNum=new AtomicInteger(0);

}
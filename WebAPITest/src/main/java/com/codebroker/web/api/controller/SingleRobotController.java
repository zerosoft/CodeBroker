package com.codebroker.web.api.controller;

import com.codebroker.web.api.service.RobotManager;
import com.codebroker.web.api.service.netty.SingleRegisty;
import com.codebroker.web.api.service.netty.TCPClient;
import io.netty.channel.Channel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/sRobot")
@Api(value = "/sRobot")
public class SingleRobotController {
    @Autowired
    RobotManager robotManager;

    @ApiOperation(value = "启动网络服务", notes = "启动测试用的机器人网络。")
    @RequestMapping(value = "/start", method = {RequestMethod.GET}, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String start(String ip, int port) {
        if (!robotManager.connect.get()){
            robotManager.client = new TCPClient(ip,port);
            robotManager.client.setRegisty(new SingleRegisty());
            robotManager.clientThread = new Thread(() -> {
                robotManager.client.connect();
            }, "TcpClientThread");
            robotManager.clientThread.start();
            robotManager.connect.set(true);
            return "首次启动";
        }else {
            return "已经启动了";
        }
    }

    @ApiOperation(value="关闭机器人网络", notes="关闭机器人网络连接"  )
    @RequestMapping(value="/stopNet",method = {RequestMethod.GET}, produces="text/html;charset=UTF-8")
    @ResponseBody
    public String stopNet(){
        try {
            if (robotManager.connect.get()){
                robotManager.client.stop();
                robotManager.clientThread.join();
                robotManager.connect.set(false);
                return "连接关闭";
            }else {
                return "没有连接";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "抱歉异常了";
        }
    }

    @ApiOperation(value="机器人登入服务器", notes="启动机器人连接到测试服务器")
    @RequestMapping(value="/robotLogin",method = {RequestMethod.GET}, produces="text/html;charset=UTF-8")
    @ResponseBody
    public String robotLogin(String account,String passwod){
        robotManager.client.getChannel().writeAndFlush(null);
        return "增加机器人";
    }
}

package com.codebroker.demo.service.alliance.handler;

import com.codebroker.demo.service.alliance.message.GetAllianceName;
import com.codebroker.extensions.service.IServiceClientRequestHandler;

import java.util.Random;

public class DoSomeLogicMessage implements IServiceClientRequestHandler<GetAllianceName> {
	@Override
	public Object handleBackMessage(GetAllianceName o) {
		getClientRequestLogger().info("DoSomeLogicMessage init {}",o);
		Random random=new Random();
		try {
			//模拟测试业务消耗时间
			Thread.sleep(random.nextInt(100));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return "Done";
	}
}

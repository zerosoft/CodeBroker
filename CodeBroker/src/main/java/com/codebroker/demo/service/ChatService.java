package com.codebroker.demo.service;

import com.codebroker.api.internal.IService;
import com.codebroker.core.data.IObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatService implements IService {

    private Logger logger= LoggerFactory.getLogger(ChatService.class);

    @Override
    public void init(Object obj) {
        logger.info("init chat "+obj);
    }

    @Override
    public void destroy(Object obj) {

    }

    @Override
    public void handleMessage(IObject obj) {
        System.out.println("Get message "+obj.getDump());
    }

    @Override
    public String getName() {
        return "ChatService";
    }


}

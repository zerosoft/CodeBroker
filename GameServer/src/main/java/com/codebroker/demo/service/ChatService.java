package com.codebroker.demo.service;

import com.codebroker.api.IGameUser;
import com.codebroker.api.event.Event;
import com.codebroker.api.internal.IService;
import com.codebroker.core.data.CObject;
import com.codebroker.core.data.IObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatService extends AbstractIService {

    private Logger logger= LoggerFactory.getLogger(ChatService.class);

    @Override
    public void init(Object obj) {
        logger.info("init chat "+obj);
    }

    @Override
    public void destroy(Object obj) {
        logger.info("destroy chat "+obj);
    }

    @Override
    public String getName() {
        return "ChatService";
    }


}

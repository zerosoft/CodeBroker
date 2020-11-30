package com.codebroker.demo.service.item;

import com.codebroker.demo.service.item.handler.UserInit;
import com.codebroker.demo.service.item.handler.UserLogin;
import com.codebroker.demo.service.item.handler.UserLogout;
import com.codebroker.extensions.service.AbstractIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemService  extends AbstractIService<Integer> {

    private Logger logger= LoggerFactory.getLogger(ItemService.class);

    @Override
    public void init(Object obj) {
        logger.info("init Item Service "+obj);
        addRequestHandler(1,new UserInit());
        addRequestHandler(2,new UserLogin());
        addRequestHandler(3,new UserLogout());
    }

    @Override
    public void destroy(Object obj) {
        logger.info("destroy Item Service "+obj);
    }


    @Override
    public String getName() {
        return ItemService.class.getName();
    }

}

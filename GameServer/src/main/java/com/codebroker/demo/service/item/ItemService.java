package com.codebroker.demo.service.item;

import com.codebroker.api.internal.IService;
import com.codebroker.core.data.IObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemService implements IService {

    private Logger logger= LoggerFactory.getLogger(ItemService.class);

    @Override
    public void init(Object obj) {
        logger.info("init Item Service "+obj);
    }

    @Override
    public void destroy(Object obj) {
        logger.info("destroy Item Service "+obj);
    }

    @Override
    public void handleMessage(IObject obj) {
        System.out.println("O +"+obj);
    }

    @Override
    public String getName() {
        return ItemService.class.getName();
    }

}

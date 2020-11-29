package com.codebroker.demo.service.item;

import com.codebroker.api.internal.IService;
import com.codebroker.core.data.IObject;
import com.codebroker.demo.service.AbstractIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemService  extends AbstractIService {

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
    public String getName() {
        return ItemService.class.getName();
    }

}

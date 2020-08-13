package com.codebroker.demo.service;

import com.codebroker.api.internal.IService;
import com.codebroker.core.data.IObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AllianceService implements IService {

    private Logger logger= LoggerFactory.getLogger(AllianceService.class);

    @Override
    public void init(Object obj) {
        logger.info("init Alliance "+obj);
    }

    @Override
    public void destroy(Object obj) {

    }

    @Override
    public void handleMessage(IObject obj) {
            System.out.println("O +"+obj);
    }

    @Override
    public String getName() {
        return "Alliance";
    }


}

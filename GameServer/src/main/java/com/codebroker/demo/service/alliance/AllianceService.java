package com.codebroker.demo.service.alliance;

import com.codebroker.demo.service.alliance.handler.*;
import com.codebroker.extensions.service.AbstractIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class AllianceService extends AbstractIService<Integer> {

    private Logger logger= LoggerFactory.getLogger(AllianceService.class);

    @Override
    public void init(Object obj) {
        logger.info("init Alliance "+obj);
        addRequestHandler(1,new UserInit());
        addRequestHandler(2,new UserLogin());
        addRequestHandler(3,new UserLogout());
        addRequestHandler(4,new GetMessage());
        addRequestHandler(5,new DoSomeLogicMessage());
    }

    @Override
    public void destroy(Object obj) {
        logger.info("destroy Alliance "+obj);
    }


    @Override
    public String getName() {
        return AllianceService.class.getName();
    }


}

package com.codebroker.demo.service.alliance;

import com.codebroker.api.AppContext;
import com.codebroker.api.IGameUser;
import com.codebroker.api.event.Event;
import com.codebroker.api.internal.IService;
import com.codebroker.core.data.CObject;
import com.codebroker.core.data.IObject;
import com.codebroker.demo.service.AbstractIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class AllianceService extends AbstractIService {

    private Logger logger= LoggerFactory.getLogger(AllianceService.class);

    @Override
    public void init(Object obj) {
        logger.info("init Alliance "+obj);
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

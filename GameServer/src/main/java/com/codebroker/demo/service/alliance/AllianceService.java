package com.codebroker.demo.service.alliance;

import com.codebroker.extensions.service.AbstractIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class AllianceService extends AbstractIService<Integer> {

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

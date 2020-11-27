package com.codebroker.demo.service.alliance;

import com.codebroker.api.AppContext;
import com.codebroker.api.IGameUser;
import com.codebroker.api.event.Event;
import com.codebroker.api.internal.IService;
import com.codebroker.core.data.CObject;
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
        logger.info("destroy Alliance "+obj);
    }

    @Override
    public void handleMessage(IObject obj) {
        System.out.println("O============================ +"+obj);
        IGameUser iGameUser = (IGameUser) obj.getClass("IGameUser");
        String userId = iGameUser.getUserId();
        System.out.println("O============================ +"+userId);

        CObject cObject = CObject.newInstance();
        cObject.putUtfString("A","BBBBBBB");
//        Event event=new Event();
//        event.setTopic("login");
//        event.setMessage(cObject);
//        iGameUser.dispatchEvent(event);

        iGameUser.sendMessageToGameUser(iGameUser.getUserId(),cObject);
    }

    @Override
    public String getName() {
        return AllianceService.class.getName();
    }


}

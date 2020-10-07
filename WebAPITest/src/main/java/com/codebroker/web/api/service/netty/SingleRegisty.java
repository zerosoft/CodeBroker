package com.codebroker.web.api.service.netty;

import com.codebroker.web.api.service.api.IRegisty;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SingleRegisty implements IRegisty {
    Logger logger= LoggerFactory.getLogger(SingleRegisty.class.getSimpleName());

    @Override
    public void joining(ChannelHandlerContext ioSession) {
        logger.info("session join {}",ioSession);
    }

    @Override
    public void exception(ChannelHandlerContext ioSession, Throwable cause) {
        logger.info("session exception {}",cause);
    }

    @Override
    public void loseConnection(ChannelHandlerContext ioSession) {
        logger.info("session lose connection {}  ",ioSession);
    }

    @Override
    public void receiveMessage(ChannelHandlerContext ioSession, Object msg) {
        logger.info("session receive message {}",msg);
    }
}

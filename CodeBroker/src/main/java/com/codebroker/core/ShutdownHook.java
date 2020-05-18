package com.codebroker.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShutdownHook extends Thread {

    private Logger logger= LoggerFactory.getLogger("ShutdownHook");
    private ServerEngine serverEngine;

    public ShutdownHook(ServerEngine serverEngine) {
        this.serverEngine=serverEngine;
    }

    @Override
    public void run() {
        logger.info("Server shut down");
        serverEngine.stopEngine();
    }
}

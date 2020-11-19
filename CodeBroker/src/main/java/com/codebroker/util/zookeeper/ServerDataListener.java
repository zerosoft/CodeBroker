package com.codebroker.util.zookeeper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务器节点变化监控
 */
public class ServerDataListener implements DataListener{
    ZookeeperClusterServiceRegister clusterServiceRegister;
    private Logger logger= LoggerFactory.getLogger(ServerDataListener.class);

    public ServerDataListener(ZookeeperClusterServiceRegister clusterServiceRegister) {
        this.clusterServiceRegister = clusterServiceRegister;
    }

    @Override
    public void dataChanged(String path, Object value, EventType eventType) {
        if (path == null || (value == null && eventType != EventType.NodeDeleted)) {
            return;
        }

        if (!path.startsWith("/CodeBroker/Server/")){
            logger.info("Path not at /CodeBroker/Server/,now path {}",path);
            return;
        }
        if (path.split("/").length >= 5) {
            clusterServiceRegister.changeServerData(path);
        }else {
            logger.info("value path is error value {}",value);
        }

    }
}

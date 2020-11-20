package com.codebroker.util.zookeeper.listener;

import com.codebroker.util.zookeeper.DataListener;
import com.codebroker.util.zookeeper.ZookeeperClusterServiceRegister;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.curator.framework.recipes.cache.CuratorCacheListener.Type.NODE_CREATED;

public class ServiceDataListener implements DataListener {

    private Logger logger= LoggerFactory.getLogger(ServerDataListener.class);

    ZookeeperClusterServiceRegister clusterServiceRegister;

    public ServiceDataListener(ZookeeperClusterServiceRegister clusterServiceRegister) {
        this.clusterServiceRegister = clusterServiceRegister;
    }

    @Override
    public void dataChanged(String path, Object value, CuratorCacheListener.Type eventType) {
        if (path == null || (value == null && eventType !=NODE_CREATED)) {
            return;
        }

        if (!path.startsWith("/CodeBroker/Service/")){
            logger.info("Path not at /CodeBroker/Service/,now path {}",path);
            return;
        }
        logger.info("value {}",value);
        String[] split = path.split("/");
        if (split.length >= 5) {
            clusterServiceRegister.changeServiceData(split[3],path);
        }else {
            logger.info("value path is error value {}",value);
        }


    }
}

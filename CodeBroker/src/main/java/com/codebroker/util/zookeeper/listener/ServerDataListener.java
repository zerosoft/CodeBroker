package com.codebroker.util.zookeeper.listener;

import com.codebroker.util.zookeeper.DataListener;
import com.codebroker.util.zookeeper.ZookeeperClusterServiceRegister;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.curator.framework.recipes.cache.CuratorCacheListener.Type.NODE_CREATED;

/**
 * 服务器节点变化监控
 */
public class ServerDataListener implements DataListener {
    ZookeeperClusterServiceRegister clusterServiceRegister;
    private Logger logger= LoggerFactory.getLogger(ServerDataListener.class);

    public ServerDataListener(ZookeeperClusterServiceRegister clusterServiceRegister) {
        this.clusterServiceRegister = clusterServiceRegister;
    }

    @Override
    public void dataChanged(String path, Object value, CuratorCacheListener.Type eventType) {
        if (path == null || (value == null && eventType !=NODE_CREATED)) {
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

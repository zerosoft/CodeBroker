package com.codebroker.util.zookeeper;

import java.util.List;

public class ServiceDataListener implements DataListener{

    @Override
    public void dataChanged(String path, Object value, EventType eventType) {
        if (eventType.equals(EventType.NodeDataChanged)){

        }else if (eventType.equals(EventType.NodeDataChanged)){

        }else if (eventType.equals(EventType.NodeDeleted)){

        }
    }
}

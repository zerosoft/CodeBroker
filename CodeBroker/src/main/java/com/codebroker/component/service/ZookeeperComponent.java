package com.codebroker.component.service;

import com.codebroker.component.BaseCoreService;
import com.codebroker.setting.SystemEnvironment;
import com.codebroker.util.PropertiesWrapper;
import com.codebroker.util.zookeeper.IClusterServiceRegister;
import com.codebroker.util.zookeeper.StateListener;
import com.codebroker.util.zookeeper.ZookeeperURL;
import com.codebroker.util.zookeeper.ZookeeperClusterServiceRegister;
import com.codebroker.util.zookeeper.curator.CuratorZookeeperClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZookeeperComponent extends BaseCoreService {
	private static Logger logger = LoggerFactory.getLogger(ZookeeperComponent.class);

	CuratorZookeeperClient curatorZookeeperClient;
	ZookeeperClusterServiceRegister zookeeperClusterServiceRegister;

	@Override
	public void init(Object obj) {
		super.init(obj);
		PropertiesWrapper propertiesWrapper = (PropertiesWrapper) obj;
		String zookeeperHostname = propertiesWrapper.getProperty(SystemEnvironment.ZOOKEEPER_HOST, "127.0.0.1");
		int zookeeperPort = propertiesWrapper.getIntProperty(SystemEnvironment.ZOOKEEPER_PORT, 2181);
		logger.info("zookeeper ip {} port {}",zookeeperHostname,zookeeperPort);
		ZookeeperURL zookeeperUrl =new ZookeeperURL("",zookeeperHostname,zookeeperPort);
		curatorZookeeperClient=new CuratorZookeeperClient(zookeeperUrl);
		zookeeperClusterServiceRegister=new ZookeeperClusterServiceRegister(curatorZookeeperClient);

		curatorZookeeperClient.addStateListener(state -> {
			if (state == StateListener.RECONNECTED) {
				try {
					//重新连上干点啥？
					logger.info("CuratorZookeeperClient RECONNECTED");
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
		});
		setActive();
		name=getClass().getName();
	}

	@Override
	public void destroy(Object obj) {
		curatorZookeeperClient.doClose();
	}

	public IClusterServiceRegister getIClusterServiceRegister() {
		return zookeeperClusterServiceRegister;
	}
}

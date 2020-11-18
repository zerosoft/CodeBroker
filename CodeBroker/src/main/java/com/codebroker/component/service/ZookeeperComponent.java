package com.codebroker.component.service;

import com.codebroker.component.BaseCoreService;
import com.codebroker.setting.SystemEnvironment;
import com.codebroker.util.PropertiesWrapper;
import com.codebroker.util.zookeeper.IClusterServiceRegister;
import com.codebroker.util.zookeeper.URL;
import com.codebroker.util.zookeeper.ZookeeperClusterServiceRegister;
import com.codebroker.util.zookeeper.curator.CuratorZookeeperClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZookeeperComponent extends BaseCoreService {
	CuratorZookeeperClient curatorZookeeperClient;
	ZookeeperClusterServiceRegister zookeeperClusterServiceRegister;
	private static Logger logger = LoggerFactory.getLogger(ZookeeperComponent.class);
	@Override
	public void init(Object obj) {
		super.init(obj);
		PropertiesWrapper propertiesWrapper = (PropertiesWrapper) obj;
		String zookeeperHostname = propertiesWrapper.getProperty(SystemEnvironment.ZOOKEEPER_HOST, "127.0.0.1");
		int zookeeperPort = propertiesWrapper.getIntProperty(SystemEnvironment.ZOOKEEPER_PORT, 2181);
		logger.info("zookeeper ip {} port {}",zookeeperHostname,zookeeperPort);
		URL url=new URL("",zookeeperHostname,zookeeperPort);
		curatorZookeeperClient=new CuratorZookeeperClient(url);
		zookeeperClusterServiceRegister=new ZookeeperClusterServiceRegister(curatorZookeeperClient);
		setActive();
	}

	@Override
	public void destroy(Object obj) {


		curatorZookeeperClient.doClose();
	}

	@Override
	public String getName() {
		return ZookeeperComponent.class.getName();
	}

	public IClusterServiceRegister getIClusterServiceRegister() {
		return zookeeperClusterServiceRegister;
	}
}

package com.codebroker.util.zookeeper.listener;

import com.codebroker.util.zookeeper.ChildListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ServerWatch implements ChildListener {

	private Logger logger= LoggerFactory.getLogger(ServerWatch.class);

	@Override
	public void childChanged(String path, List<String> children) {
		logger.info("================path=============={}",path);
	}
}

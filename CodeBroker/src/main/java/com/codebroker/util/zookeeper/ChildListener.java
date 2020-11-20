package com.codebroker.util.zookeeper;


import java.util.List;

/**
 * 节点监听，正常应该用不到了
 */
public interface ChildListener {

	void childChanged(String path, List<String> children);

}

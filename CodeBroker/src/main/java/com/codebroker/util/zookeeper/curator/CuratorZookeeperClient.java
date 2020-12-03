package com.codebroker.util.zookeeper.curator;



import com.codebroker.util.zookeeper.*;
import com.codebroker.util.zookeeper.support.AbstractZookeeperClient;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.Locker;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.WatchedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;


public class CuratorZookeeperClient extends AbstractZookeeperClient<CuratorZookeeperClient.CuratorWatcherImpl, CuratorZookeeperClient.CuratorWatcherImpl> {

	protected static final Logger logger = LoggerFactory.getLogger(CuratorZookeeperClient.class);

	static final Charset CHARSET = Charset.forName("UTF-8");
	private final CuratorFramework client;
	private Map<String, CuratorCache> treeCacheMap = new ConcurrentHashMap<>();

	public CuratorZookeeperClient(ZookeeperURL zookeeperUrl) {
		super(zookeeperUrl);
		try {
			int timeout = 5000;
			CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder()
					.connectString(zookeeperUrl.getZookeeperAddress())
					.runSafeService(Executors.newSingleThreadExecutor())
					.retryPolicy(new RetryNTimes(1, 1000))
					.connectionTimeoutMs(timeout);
			client = builder.build();
			client.getConnectionStateListenable()
					.addListener((client, state) -> {
				if (state == ConnectionState.LOST) {
					stateChanged(StateListener.DISCONNECTED);
				} else if (state == ConnectionState.CONNECTED) {
					stateChanged(StateListener.CONNECTED);
				} else if (state == ConnectionState.RECONNECTED) {
					stateChanged(StateListener.RECONNECTED);
				}
			});
			client.start();
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	@Override
	public void createPersistent(String path) {
		try {
			client.create().forPath(path);
		} catch (NodeExistsException e) {
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	@Override
	public void createEphemeral(String path) {
		try {
			client.create().withMode(CreateMode.EPHEMERAL).forPath(path);
		} catch (NodeExistsException e) {
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	@Override
	protected void createPersistent(String path, String data) {
		byte[] dataBytes = data.getBytes(CHARSET);
		try {
			client.create().forPath(path, dataBytes);
		} catch (NodeExistsException e) {
			try {
				client.setData().forPath(path, dataBytes);
			} catch (Exception e1) {
				throw new IllegalStateException(e.getMessage(), e1);
			}
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	@Override
	protected void createEphemeral(String path, String data) {
		byte[] dataBytes = data.getBytes(CHARSET);
		try {
			client.create().withMode(CreateMode.EPHEMERAL).forPath(path, dataBytes);
		} catch (NodeExistsException e) {
			try {
				client.setData().forPath(path, dataBytes);
			} catch (Exception e1) {
				throw new IllegalStateException(e.getMessage(), e1);
			}
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	@Override
	public void delete(String path) {
		try {
			client.delete().forPath(path);
		} catch (NoNodeException e) {
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	@Override
	public List<String> getChildren(String path) {
		try {
			return client.getChildren().forPath(path);
		} catch (NoNodeException e) {
			logger.error("NoNodeException",e);
			return Lists.newArrayList();
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	@Override
	public boolean checkExists(String path) {
		try {
			if (client.checkExists().forPath(path) != null) {
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}

	@Override
	public boolean isConnected() {
		return client.getZookeeperClient().isConnected();
	}

	@Override
	public String doGetContent(String path) {
		try {
			byte[] dataBytes = client.getData().forPath(path);
			return (dataBytes == null || dataBytes.length == 0) ? null : new String(dataBytes, CHARSET);
		} catch (NoNodeException e) {
			// ignore NoNode Exception.
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public void doClose() {
		client.close();
	}

	@Override
	public CuratorZookeeperClient.CuratorWatcherImpl createTargetChildListener(String path, ChildListener listener) {
		return new CuratorZookeeperClient.CuratorWatcherImpl(client, listener);
	}

	@Override
	public List<String> addTargetChildListener(String path, CuratorWatcherImpl listener) {
		try {
			return client.getChildren().usingWatcher(listener).forPath(path);
		} catch (NoNodeException e) {
			logger.error("NoNodeException",e);
			return Lists.newArrayList();
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	@Override
	protected CuratorZookeeperClient.CuratorWatcherImpl createTargetDataListener(String path, DataListener listener) {
		return new CuratorWatcherImpl(client, listener);
	}

	@Override
	protected void addTargetDataListener(String path, CuratorZookeeperClient.CuratorWatcherImpl treeCacheListener) {
		this.addTargetDataListener(path, treeCacheListener, null);
	}

	@Override
	protected void addTargetDataListener(String path, CuratorZookeeperClient.CuratorWatcherImpl treeCacheListener, Executor executor) {
		try {
			CuratorCache curatorCache=CuratorCache.build(client,path);
			treeCacheMap.putIfAbsent(path, curatorCache);
			curatorCache.start();
			if (executor == null) {
				curatorCache.listenable().addListener(treeCacheListener);
			} else {
				curatorCache.listenable().addListener(treeCacheListener, executor);
			}
		} catch (Exception e) {
			throw new IllegalStateException("Add treeCache listener for path:" + path, e);
		}
	}

	@Override
	protected void removeTargetDataListener(String path, CuratorZookeeperClient.CuratorWatcherImpl treeCacheListener) {
		CuratorCache curatorCache = treeCacheMap.get(path);
		if (curatorCache != null) {
			curatorCache.listenable().removeListener(treeCacheListener);
		}
		treeCacheListener.dataListener = null;
	}

	@Override
	public void removeTargetChildListener(String path, CuratorWatcherImpl listener) {
		listener.unwatch();
	}

	public static class CuratorWatcherImpl implements CuratorWatcher, CuratorCacheListener {

		private CuratorFramework client;
		private volatile ChildListener childListener;
		private volatile DataListener dataListener;


		public CuratorWatcherImpl(CuratorFramework client, ChildListener listener) {
			this.client = client;
			this.childListener = listener;
		}

		public CuratorWatcherImpl(CuratorFramework client, DataListener dataListener) {
			this.dataListener = dataListener;
		}

		protected CuratorWatcherImpl() {
		}

		public void unwatch() {
			this.childListener = null;
		}

		@Override
		public void process(WatchedEvent event) throws Exception {
			if (childListener != null) {
				String path = event.getPath() == null ? "" : event.getPath();
				childListener.childChanged(path,
						StringUtils.isNotEmpty(path)
								? client.getChildren().usingWatcher(this).forPath(path)
								: Collections.emptyList());
			}
		}

		@Override
		public void event(Type type, ChildData oldData, ChildData data) {
			if (dataListener != null) {
				if (logger.isDebugEnabled()) {
					logger.debug("listen the zookeeper changed. The changed data:" +type);
				}
				String content = null;
				String path = null;
				switch (type) {
					case NODE_CREATED:
						path = data.getPath();
						content = data.getData() == null ? "" : new String(data.getData(), CHARSET);
						break;
					case NODE_CHANGED:
						path = data.getPath();
						content = data.getData() == null ? "" : new String(data.getData(), CHARSET);
						break;
					case NODE_DELETED:
						path = oldData.getPath();
						break;
				}
				dataListener.dataChanged(path, content, type);
			}
		}
	}

	public CuratorFramework getClient() {
		return client;
	}
}

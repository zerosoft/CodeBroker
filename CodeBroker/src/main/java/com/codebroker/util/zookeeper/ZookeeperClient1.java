package com.codebroker.util.zookeeper;

import com.codebroker.util.zookeeper.curator.CuratorZookeeperClient;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.CountDownLatch;


public class ZookeeperClient1 {
	static  Charset CHARSET = Charset.forName("UTF-8");
	private  ZooKeeper client;

	static 	Watcher watcher=new Watcher() {
		@Override
		public void process(WatchedEvent event) {
			if (event.getState() == Event.KeeperState.SyncConnected) {
				System.err.println("eventType:"+event.getType());
				if(event.getType()==Event.EventType.None){
				}else if(event.getType()==Event.EventType.NodeCreated){
					String path = event.getPath();
					System.out.println("listen:节点创建"+path);
				}else if(event.getType()==Event.EventType.NodeChildrenChanged){
					String path = event.getPath();
					System.out.println("listen:子节点修改"+path);
				}
			}
		}
	};

	public void registerClusterService(String serviceName,String ip,String port){
		createContainer("/CodeBorker/Service/"+serviceName,ip);
//		createContainer("/CodeBorker/Service",ip);
		createData("/CodeBorker/Service/"+serviceName+"/node","http://"+ip+":"+port);
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		String connStr = "127.0.0.1:2181";
//		CountDownLatch countDown = new CountDownLatch(1);
//
//
//
//		ZookeeperClient1 zookeeperClient=new ZookeeperClient1();
//
//
//		zookeeperClient.client = new ZooKeeper(connStr, 5000,watcher );
//
//
//
//		zookeeperClient.registerClusterService("test1","21.24.63.63","24");
//		zookeeperClient.registerClusterService("test2","21.24.63.63","24");
//		zookeeperClient.registerClusterService("test2","21.24.63.63","23");


//		countDown.await();
//
//		//注册监听,每次都要重新注册，否则监听不到
//		Stat exists = zookeeper.exists("/top/jinyong", watcher);
//		exists = zookeeper.exists("/top",false);
//		if (exists==null){
//			zookeeper.create("/top","112".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.CONTAINER);
//		}
//
//		zookeeper.create("/top/ccc12","Hekk".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);
//		exists = zookeeper.exists("/top/jinyong1",false);
//		String result;
//		List<String> children = zookeeper.getChildren("/top", false);
//		for (String child : children) {
//			System.out.println(child);
//		}
//	if (exists==null){
//		// 创建节点
//		result = zookeeper.create("/top/jinyong1", "一生一世".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
//		System.out.println(result);
//	}else {
//		// 获取节点
//		byte[] bs = zookeeper.getData("/top/jinyong1", true, null);
//		result = new String(bs);
//		result+="I love you ";
//		// 修改节点
//		zookeeper.setData("/top/jinyong1", result.getBytes(), -1);
//	}


//		Thread.sleep(10);
//
//		// 获取节点
//		byte[] bs = zookeeper.getData("/top/jinyong", true, null);
//		result = new String(bs);
//		System.out.println("创建节点后的数据是:" + result);
//
//		// 修改节点
//		zookeeper.setData("/top/jinyong", "I love you 2".getBytes(), -1);
//

//		try {
//			List<String> children = zookeeperClient.client.getChildren("/CodeBorker/Service/test2", false);
//			children.forEach(s -> System.out.println(s));
//		} catch (KeeperException e) {
//			e.printStackTrace();
//		}
		URL url=new URL("ptl","127.0.0.1",2181);
		CuratorZookeeperClient curatorZookeeperClient=new CuratorZookeeperClient(url);

		ChildListener childListener=new ChildListener() {
			@Override
			public void childChanged(String path, List<String> children) {
				System.out.println("============"+path);
				for (String child : children) {
					System.out.println(child);
				}
			}
		};
		DataListener dataListener = new DataListener() {
			@Override
			public void dataChanged(String path, Object value, EventType eventType) {
				System.out.println("============"+path);
				if (eventType.equals(EventType.NodeDataChanged)){
					List<String> children = curatorZookeeperClient.getChildren(path);
					for (String child : children) {
						System.out.println(child);
					}
				}
			}
		};

		curatorZookeeperClient.addChildListener("/",childListener);
		curatorZookeeperClient.addDataListener("/CodeBroker",dataListener);

//		curatorZookeeperClient.addTargetChildListener("/CodeBroker/Test124",new CuratorZookeeperClient.CuratorWatcherImpl(curatorZookeeperClient,childListener));
		boolean b = curatorZookeeperClient.checkExists("/CodeBroker");
		if (b){
			curatorZookeeperClient.createPersistent("/CodeBroker/Test125");
		}else{
			curatorZookeeperClient.createPersistent("/CodeBroker");
			curatorZookeeperClient.createPersistent("/CodeBroker/Test124");
		}
		boolean b1 = curatorZookeeperClient.checkExists("/CodeBroker/Test124");
		if (b1){
			String name = ChildListener.class.getName();
			boolean b2 = curatorZookeeperClient.checkExists("/CodeBroker/Test124/" + name);
			if (b2){
				curatorZookeeperClient.create("/CodeBroker/Test124/"+name+"/11",name,true);
				curatorZookeeperClient.create("/CodeBroker/Test124/"+name+"/13",name,true);
			}else {
				curatorZookeeperClient.create("/CodeBroker/Test124/"+name,name,false);
				curatorZookeeperClient.create("/CodeBroker/Test124/"+name+"/12",name,true);
			}


		}


		Thread.sleep(102009);

//		byte[] bs = zookeeper.getData("/top/jinyong", true, null);
//		result = new String(bs);
//		System.out.println("修改节点后的数据是:" + result);

		// 删除节点
//		zookeeper.delete("/top/jinyong", -1);
//		System.out.println("节点删除成功");
	}

	public static Watcher getWatcher() {
		return watcher;
	}

	protected void createContainer(String path, String data) {
		byte[] dataBytes = data.getBytes(CHARSET);

		String[] split = path.split("\\/");
		String checkPath="";
		for (String subPath : split) {
			if (subPath.equals("")){
				continue;
			}
			checkPath+="/"+subPath;
			try {
				Stat exists = client.exists(checkPath, ZookeeperClient1.getWatcher());
				if (exists == null){
					client.create(checkPath,dataBytes,ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.CONTAINER);
				}
			} catch (KeeperException |InterruptedException e) {
				e.printStackTrace();
			}
		}


	}

	protected void createData(String path, String data) {
		byte[] dataBytes = data.getBytes(CHARSET);
		try {
			Stat exists = client.exists(path, ZookeeperClient1.getWatcher());
			if (exists==null){
				client.create(path,dataBytes,ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
			}
		} catch (KeeperException |InterruptedException e) {
			e.printStackTrace();
		}
	}
}

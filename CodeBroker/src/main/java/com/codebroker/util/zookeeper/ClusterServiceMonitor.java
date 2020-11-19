//package com.codebroker.util.zookeeper;
//
//import org.apache.zookeeper.WatchedEvent;
//import org.apache.zookeeper.Watcher;
//
//public class ClusterServiceMonitor implements Watcher {
//	@Override
//	public void process(WatchedEvent event) {
//		if (event.getState() == Event.KeeperState.SyncConnected) {
//			System.err.println("eventType:"+event.getType());
//			if(event.getType()==Event.EventType.None){
//			}else if(event.getType()==Event.EventType.NodeCreated){
//				String path = event.getPath();
//				System.out.println("listen:节点创建"+path);
//			}else if(event.getType()==Event.EventType.NodeChildrenChanged){
//				String path = event.getPath();
//				System.out.println("listen:子节点修改"+path);
//			}
//		}
//	}
//
////	@Override
////	public void processResult(int rc, String path, Object ctx, Stat stat) {
////		boolean exists;
////		switch (rc) {
////			case KeeperException.Code.OK:
////				exists = true;
////				break;
////			case KeeperException.Code.NONODE:
////				exists = false;
////				break;
////			case KeeperException.Code.INVALIDCALLBACK:
////			case KeeperException.Code.NOAUTH:
//////				dead = true;
//////				listener.closing(rc);
////				return;
////			default:
////				// Retry errors
//////				zk.exists(znode, true, this, null);
////				return;
////		}
////
////		byte b[] = null;
////		if (exists) {
////			try {
////				b = zk.getData(znode, false, null);
////			} catch (KeeperException e) {
////				// We don't need to worry about recovering now. The watch
////				// callbacks will kick off any exception handling
////				e.printStackTrace();
////			} catch (InterruptedException e) {
////				return;
////			}
////		}
////		if ((b == null && b != prevData)
////				|| (b != null && !Arrays.equals(prevData, b))) {
////			listener.exists(b);
////			prevData = b;
////		}
////	}
//}

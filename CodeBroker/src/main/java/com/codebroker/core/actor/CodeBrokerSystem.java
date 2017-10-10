package com.codebroker.core.actor;

import java.io.Serializable;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codebroker.core.cluster.ClusterDistributedPub;
import com.codebroker.core.cluster.ClusterDistributedSub;
import com.codebroker.core.cluster.ClusterListener;
import com.codebroker.core.model.CodeDeadLetter;
import com.codebroker.core.monitor.MonitorManager;
import com.codebroker.protocol.ThriftSerializerFactory;
import com.codebroker.util.LogUtil;
import com.message.thrift.actor.Operation;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.DeadLetter;
import akka.actor.Props;
import akka.japi.Creator;
import akka.japi.pf.ReceiveBuilder;

/**
 *
 * @author ZERO
 */
public class CodeBrokerSystem extends AbstractActor {
	
	public static ActorRef monitorManager;

	private static Logger logger = LoggerFactory.getLogger("CodeBrokerSystem");
	ThriftSerializerFactory thriftSerializerFactory=new ThriftSerializerFactory();
	public static final String IDENTIFY = CodeBrokerSystem.class.getSimpleName();

	static class selfCreator implements Creator<CodeBrokerSystem> {

		private static final long serialVersionUID = -4506944735716145059L;

		private final ActorSystem actorSystem;

		public selfCreator(ActorSystem actorSystem) {
			super();
			this.actorSystem = actorSystem;
		}

		@Override
		public CodeBrokerSystem create() throws Exception {
			return new CodeBrokerSystem(actorSystem);
		}

	}

	public static Props props(ActorSystem actorSystem) {
		Props create = Props.create(new selfCreator(actorSystem));
		create.withDispatcher("session-default-dispatcher");
		return create;
	}

	public CodeBrokerSystem(ActorSystem actorSystem) {
		super();
		this.actorSystem = actorSystem;
	}

	private final ActorSystem actorSystem;

	ActorRef lastSender = getContext().system().deadLetters();

	private void processInitAvalon() {
		/**
		 * 集群监听
		 */
		ActorRef clusterListener = 
		actorSystem.actorOf(Props.create(ClusterListener.class), ClusterListener.IDENTIFY);
		this.getContext().watch(clusterListener);

		/**
		 * 错误地址信息
		 */
		Props avalonDeadLetterProps = Props.create(CodeDeadLetter.class);
		ActorRef avalonDeadLetterRef = actorSystem.actorOf(avalonDeadLetterProps);
		actorSystem.eventStream().subscribe(avalonDeadLetterRef, DeadLetter.class);
		/**
		 * 初始化最高级世界
		 */
		ActorRef world = actorSystem.actorOf(Props.create(WorldActor.class), WorldActor.IDENTIFY);
		this.getContext().watch(world);
		//初始化WORLD
		try {
			byte[] tbaseMessage = thriftSerializerFactory.getTbaseMessage(Operation.WORLD_INITIALIZE);
			world.tell(tbaseMessage, getSelf());
		} catch (TException e) {
			e.printStackTrace();
		}
	
		logger.info("World Path=" + world.path().toString());
		/**
		 * ELK日志记录
		 */
		ActorRef elkLogger = actorSystem.actorOf(Props.create(ELKLogActor.class), ELKLogActor.IDENTIFY);
		this.getContext().watch(elkLogger);
		LogUtil.elkLog = elkLogger;
		logger.info("ELKActor Path=" + elkLogger.path().toString());
		/**
		 * 分布式发布Actor
		 */
		Props pub = Props.create(ClusterDistributedPub.class);
		ActorRef clusterDistributedPub = 
				actorSystem.actorOf(pub,ClusterDistributedPub.IDENTIFY);
		this.getContext().watch(clusterDistributedPub);
		/**
		 * 分布式订阅 Actor
		 */
		Props sub = Props.create(ClusterDistributedSub.class, "CODE_BORKER_TOPIC");
		ActorRef clusterDistributedSub = 
				actorSystem.actorOf(sub, ClusterDistributedSub.IDENTIFY);
		this.getContext().watch(clusterDistributedSub);
		/**
		 * 数据相关监听Actor
		 */
		Props create = Props.create(MonitorManager.class);
		monitorManager = actorSystem.actorOf(create,MonitorManager.IDENTIFY);
		this.getContext().watch(monitorManager);
	}

	@Override
	public Receive createReceive() {
		return ReceiveBuilder.create()
		.match(InitAkkaSystem.class, msg -> {
			processInitAvalon();
		}).match(RestAkkaSystem.class, msg->{
			
		}).match(CloseAkkaSystem.class, msg->{
			
		}).build();
	}
	/**
	 * 初始化Akka系统
	 * @author zero
	 *
	 */
	public static class InitAkkaSystem implements Serializable{
		private static final long serialVersionUID = 6462859024035662121L;
	}
	/**
	 * 关闭Akka系统
	 * @author zero
	 *
	 */
	public static class CloseAkkaSystem implements Serializable{
		private static final long serialVersionUID = 806701713038586180L;
	}
	/**
	 * 重启Akka系统
	 * @author zero
	 *
	 */
	public static class RestAkkaSystem implements Serializable{
		private static final long serialVersionUID = 806701713038586180L;
	}
}
